/**
 * SwadExpress - India's Premier Online Food Delivery & Live Tracking
 * Connects directly to Java Spring Boot 4 REST Microservices
 */

// Base API URL configuration
const API_BASE = window.location.port === '8085' ? '' : 'http://localhost:8085';

// Global Application State
const AppState = {
  currentUser: {
    id: 1,
    name: 'Aarav Sharma',
    email: 'aarav.sharma@example.in',
    phone: '+91 98765 43210',
    avatarUrl: 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=200&q=80'
  },
  restaurants: [],
  filteredRestaurants: [],
  selectedCuisine: 'All',
  searchQuery: '',
  vegOnly: false,
  sortBy: 'featured',
  activeRestaurant: null,
  activeMenu: [],
  activeMenuCategory: 'ALL',
  cart: {
    restaurantId: null,
    restaurantName: null,
    deliveryFee: 35.00,
    items: []
  },
  addresses: [],
  selectedAddressId: null,
  currentCity: 'Bengaluru',
  currentLocality: 'Indiranagar',
  locationLat: 12.9784,
  locationLng: 77.6408,
  activeOrders: [],
  currentTracking: null,
  trackingPollTimer: null,
  userTickets: []
};

// ==========================================
// Toast Notification Utility
// ==========================================
function showToast(message, type = 'info') {
  let container = document.getElementById('toastContainer');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast-item toast-${type}`;
  
  let icon = 'ℹ️';
  if (type === 'success') icon = '✅';
  else if (type === 'error') icon = '⚠️';
  else if (type === 'warning') icon = '🔔';

  toast.innerHTML = `
    <span class="toast-icon">${icon}</span>
    <span class="toast-message">${message}</span>
  `;

  container.appendChild(toast);

  requestAnimationFrame(() => {
    toast.classList.add('show');
  });

  setTimeout(() => {
    toast.classList.remove('show');
    setTimeout(() => {
      if (toast.parentNode === container) {
        container.removeChild(toast);
      }
    }, 300);
  }, 4000);
}

// ==========================================
// Initialization
// ==========================================
document.addEventListener('DOMContentLoaded', async () => {
  initTheme();
  setupEventListeners();
  checkBackendHealth();
  updateAuthUI();
  await loadUserData();
  await loadRestaurants();
  await loadUserOrders();
  await checkActiveLiveOrder();
});

// Theme Management (Default: White / Light Theme)
function initTheme() {
  const savedTheme = localStorage.getItem('swad_theme') || 'light';
  applyTheme(savedTheme);

  const themeToggleBtn = document.getElementById('themeToggleBtn');
  if (themeToggleBtn) {
    themeToggleBtn.addEventListener('click', () => {
      const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
      const newTheme = currentTheme === 'light' ? 'dark' : 'light';
      applyTheme(newTheme);
      showToast(`Switched to ${newTheme === 'light' ? 'White / Light' : 'Dark'} theme`, 'info');
    });
  }
}

function applyTheme(theme) {
  document.documentElement.setAttribute('data-theme', theme);
  localStorage.setItem('swad_theme', theme);
  const themeIcon = document.getElementById('themeToggleIcon');
  if (themeIcon) {
    themeIcon.textContent = theme === 'light' ? '🌙' : '☀️';
  }
  const themeToggleBtn = document.getElementById('themeToggleBtn');
  if (themeToggleBtn) {
    themeToggleBtn.title = theme === 'light' ? 'Switch to Dark Theme' : 'Switch to White Theme';
  }
}

// Check Spring Boot connectivity
async function checkBackendHealth() {
  const statusText = document.getElementById('backendStatusText');
  try {
    const res = await fetch(`${API_BASE}/api/v1/restaurants?userId=${AppState.currentUser.id}`);
    if (res.ok) {
      statusText.textContent = 'Spring Boot 4 Microservices API Gateway: Connected (Port 8085)';
    }
  } catch (err) {
    statusText.textContent = 'Spring Boot API: Connecting... (Port :8085)';
  }
}

// ==========================================
// Event Listeners Setup
// ==========================================
function setupEventListeners() {
  // Brand Logo
  document.getElementById('brandLogo').addEventListener('click', (e) => {
    e.preventDefault();
    resetFilters();
  });

  // Search input with debounce
  const searchInput = document.getElementById('restaurantSearchInput');
  const clearSearchBtn = document.getElementById('clearSearchBtn');
  let debounceTimeout;

  searchInput.addEventListener('input', (e) => {
    clearTimeout(debounceTimeout);
    const val = e.target.value.trim();
    clearSearchBtn.style.display = val ? 'block' : 'none';
    debounceTimeout = setTimeout(() => {
      AppState.searchQuery = val;
      loadRestaurants();
    }, 250);
  });

  clearSearchBtn.addEventListener('click', () => {
    searchInput.value = '';
    clearSearchBtn.style.display = 'none';
    AppState.searchQuery = '';
    loadRestaurants();
  });

  // Cuisine filter pills
  const pills = document.querySelectorAll('.cuisine-pill');
  pills.forEach(pill => {
    pill.addEventListener('click', () => {
      pills.forEach(p => p.classList.remove('active'));
      pill.classList.add('active');
      AppState.selectedCuisine = pill.dataset.cuisine;
      loadRestaurants();
    });
  });

  // Veg Only Toggle
  document.getElementById('vegOnlyToggle').addEventListener('change', (e) => {
    AppState.vegOnly = e.target.checked;
    filterAndRenderRestaurants();
  });

  // Sort dropdown
  document.getElementById('sortBySelect').addEventListener('change', (e) => {
    AppState.sortBy = e.target.value;
    filterAndRenderRestaurants();
  });

  // Top Nav Buttons
  document.getElementById('navExploreBtn').addEventListener('click', () => {
    resetFilters();
    setActiveNav('navExploreBtn');
  });

  document.getElementById('navFavoritesBtn').addEventListener('click', () => {
    showFavoritesView();
    setActiveNav('navFavoritesBtn');
  });

  document.getElementById('navOrdersBtn').addEventListener('click', () => {
    openOrdersHistoryModal();
  });

  document.getElementById('navSupportBtn').addEventListener('click', () => {
    openSupportModal();
  });

  document.getElementById('navProfileBtn').addEventListener('click', () => {
    openAccountModal();
  });

  document.getElementById('navAddressPill').addEventListener('click', () => {
    openLocationPickerModal();
  });

  const closeLocBtn = document.getElementById('closeLocationModalBtn');
  if (closeLocBtn) {
    closeLocBtn.addEventListener('click', () => {
      document.getElementById('locationPickerModal').style.display = 'none';
    });
  }

  const confirmLocBtn = document.getElementById('confirmLocationBtn');
  if (confirmLocBtn) {
    confirmLocBtn.addEventListener('click', confirmLocationSelection);
  }

  const manageAddrBtn = document.getElementById('openManageAddressesBtn');
  if (manageAddrBtn) {
    manageAddrBtn.addEventListener('click', () => {
      document.getElementById('locationPickerModal').style.display = 'none';
      openAccountModal('addresses');
    });
  }

  document.querySelectorAll('.city-chip').forEach(btn => {
    btn.addEventListener('click', () => {
      selectCity(btn.dataset.city);
    });
  });

  // Cart Trigger Button
  document.getElementById('cartTriggerBtn').addEventListener('click', () => {
    openCartDrawer();
  });
  document.getElementById('closeCartDrawerBtn').addEventListener('click', () => {
    closeCartDrawer();
  });
  document.getElementById('startShoppingBtn').addEventListener('click', () => {
    closeCartDrawer();
    resetFilters();
  });

  // Menu Modal Close
  document.getElementById('closeMenuModalBtn').addEventListener('click', () => {
    document.getElementById('menuModal').style.display = 'none';
  });

  // Menu Category Tabs
  document.querySelectorAll('.cat-tab').forEach(tab => {
    tab.addEventListener('click', () => {
      document.querySelectorAll('.cat-tab').forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      AppState.activeMenuCategory = tab.dataset.category;
      renderMenuItems();
    });
  });

  // Checkout Actions
  document.getElementById('placeOrderBtn').addEventListener('click', handlePlaceOrder);
  document.getElementById('openAddAddressModalBtn').addEventListener('click', () => {
    openAccountModal('addresses');
  });

  // Tracking Modal
  document.getElementById('closeTrackingModalBtn').addEventListener('click', () => {
    document.getElementById('trackingModal').style.display = 'none';
    if (AppState.trackingPollTimer) clearInterval(AppState.trackingPollTimer);
  });
  document.getElementById('advanceStepBtn').addEventListener('click', handleAdvanceStep);
  document.getElementById('openLiveTrackerBtn').addEventListener('click', () => {
    if (AppState.activeOrders.length > 0) {
      openTrackingModal(AppState.activeOrders[0].id);
    }
  });

  // Review Modal
  document.getElementById('closeReviewModalBtn').addEventListener('click', () => {
    document.getElementById('reviewModal').style.display = 'none';
  });
  document.getElementById('reviewForm').addEventListener('submit', handleReviewSubmit);
  setupStarRating();

  // Orders History Modal
  document.getElementById('closeOrdersModalBtn').addEventListener('click', () => {
    document.getElementById('ordersHistoryModal').style.display = 'none';
  });

  // Support Modal
  document.getElementById('closeSupportModalBtn').addEventListener('click', () => {
    document.getElementById('supportModal').style.display = 'none';
  });
  document.getElementById('supportTicketForm').addEventListener('submit', handleTicketSubmit);

  // Account Modal
  document.getElementById('closeAccountModalBtn').addEventListener('click', () => {
    document.getElementById('accountModal').style.display = 'none';
  });
  document.getElementById('profileTabBtn').addEventListener('click', () => switchAccountTab('profile'));
  document.getElementById('addressesTabBtn').addEventListener('click', () => switchAccountTab('addresses'));
  document.getElementById('profileForm').addEventListener('submit', handleProfileSubmit);
  document.getElementById('addNewAddressBtn').addEventListener('click', () => {
    document.getElementById('newAddressForm').style.display = 'block';
  });
  document.getElementById('cancelAddressBtn').addEventListener('click', () => {
    document.getElementById('newAddressForm').style.display = 'none';
  });
  document.getElementById('newAddressForm').addEventListener('submit', handleNewAddressSubmit);

  // User Registration & Auth (UC-1)
  const navRegisterBtn = document.getElementById('navRegisterBtn');
  if (navRegisterBtn) {
    navRegisterBtn.addEventListener('click', () => openRegisterModal('register'));
  }
  const navLoginBtn = document.getElementById('navLoginBtn');
  if (navLoginBtn) {
    navLoginBtn.addEventListener('click', () => openRegisterModal('login'));
  }
  const navLogoutBtn = document.getElementById('navLogoutBtn');
  if (navLogoutBtn) {
    navLogoutBtn.addEventListener('click', handleLogout);
  }
  const modalLogoutBtn = document.getElementById('modalLogoutBtn');
  if (modalLogoutBtn) {
    modalLogoutBtn.addEventListener('click', handleLogout);
  }
  const closeRegisterModalBtn = document.getElementById('closeRegisterModalBtn');
  if (closeRegisterModalBtn) {
    closeRegisterModalBtn.addEventListener('click', closeRegisterModal);
  }
  const authTabRegisterBtn = document.getElementById('authTabRegisterBtn');
  if (authTabRegisterBtn) {
    authTabRegisterBtn.addEventListener('click', () => switchAuthTab('register'));
  }
  const authTabLoginBtn = document.getElementById('authTabLoginBtn');
  if (authTabLoginBtn) {
    authTabLoginBtn.addEventListener('click', () => switchAuthTab('login'));
  }
  const switchToLoginLink = document.getElementById('switchToLoginLink');
  if (switchToLoginLink) {
    switchToLoginLink.addEventListener('click', (e) => {
      e.preventDefault();
      switchAuthTab('login');
    });
  }
  const switchToRegisterLink = document.getElementById('switchToRegisterLink');
  if (switchToRegisterLink) {
    switchToRegisterLink.addEventListener('click', (e) => {
      e.preventDefault();
      switchAuthTab('register');
    });
  }
  const userRegistrationForm = document.getElementById('userRegistrationForm');
  if (userRegistrationForm) {
    userRegistrationForm.addEventListener('submit', handleUserRegistration);
  }
  const userLoginForm = document.getElementById('userLoginForm');
  if (userLoginForm) {
    userLoginForm.addEventListener('submit', handleUserLogin);
  }
  const startOrderingRegisteredBtn = document.getElementById('startOrderingRegisteredBtn');
  if (startOrderingRegisteredBtn) {
    startOrderingRegisteredBtn.addEventListener('click', handleContinueAsRegisteredUser);
  }
}

function setActiveNav(activeId) {
  ['navExploreBtn', 'navFavoritesBtn'].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.classList.toggle('active', id === activeId);
  });
}

function resetFilters() {
  AppState.selectedCuisine = 'All';
  AppState.searchQuery = '';
  AppState.vegOnly = false;
  AppState.sortBy = 'featured';
  document.getElementById('restaurantSearchInput').value = '';
  document.getElementById('clearSearchBtn').style.display = 'none';
  document.getElementById('vegOnlyToggle').checked = false;
  document.getElementById('sortBySelect').value = 'featured';
  document.querySelectorAll('.cuisine-pill').forEach(p => {
    p.classList.toggle('active', p.dataset.cuisine === 'All');
  });
  document.getElementById('sectionHeading').textContent = 'Top Rated Restaurants Near You';
  setActiveNav('navExploreBtn');
  loadRestaurants();
}

// ==========================================
// API Operations: Users & Addresses (UC-1, UC-7, UC-9)
// ==========================================
async function loadUserData() {
  if (!AppState.currentUser) {
    updateAuthUI();
    return;
  }
  try {
    const userRes = await fetch(`${API_BASE}/api/v1/users/${AppState.currentUser.id}`);
    if (userRes.ok) {
      AppState.currentUser = await userRes.json();
      document.getElementById('userNameDisplay').textContent = AppState.currentUser.name;
      document.getElementById('userAvatarImg').src = AppState.currentUser.avatarUrl;
      document.getElementById('profileNameInput').value = AppState.currentUser.name;
      document.getElementById('profileEmailInput').value = AppState.currentUser.email;
      document.getElementById('profilePhoneInput').value = AppState.currentUser.phone;
      document.getElementById('profileAvatarUrl').value = AppState.currentUser.avatarUrl;
      document.getElementById('profileAvatarPreview').src = AppState.currentUser.avatarUrl;
    }

    // Load Addresses
    const addrRes = await fetch(`${API_BASE}/api/v1/users/${AppState.currentUser.id}/addresses`);
    if (addrRes.ok) {
      AppState.addresses = await addrRes.json();
      renderAddresses();
    }
  } catch (err) {
    console.error('Failed to load user info:', err);
  }
}

function renderAddresses() {
  const navDisplay = document.getElementById('navAddressDisplay');
  const defaultAddr = AppState.addresses.find(a => a.default) || AppState.addresses[0];
  if (defaultAddr) {
    AppState.selectedAddressId = defaultAddr.id;
    navDisplay.textContent = `${defaultAddr.label}: ${defaultAddr.street}, ${defaultAddr.city}`;
    if (defaultAddr.city) {
      AppState.currentCity = defaultAddr.city;
      AppState.currentLocality = defaultAddr.suite || defaultAddr.street || 'Indiranagar';
      updateHeroDeliveryBadge(defaultAddr.city);
    }
  }

  // Render in Cart Drawer checkout
  const cartOptionsList = document.getElementById('addressOptionsList');
  cartOptionsList.innerHTML = AppState.addresses.map(a => `
    <label class="address-radio-label">
      <input type="radio" name="selectedDeliveryAddress" value="${a.id}" ${a.id === AppState.selectedAddressId ? 'checked' : ''} onchange="selectAddress(${a.id})">
      <div>
        <strong>${a.label}</strong>: ${a.street} ${a.suite ? '(' + a.suite + ')' : ''}, ${a.city} ${a.zipCode}
      </div>
    </label>
  `).join('');

  // Render in Account Modal
  const managerList = document.getElementById('addressesManagerList');
  if (managerList) {
    managerList.innerHTML = AppState.addresses.map(a => `
      <div class="address-card-row">
        <div class="address-info">
          <span class="address-tag">${a.label} ${a.default ? '★ DEFAULT' : ''}</span>
          <span class="address-text">${a.street} ${a.suite || ''}, ${a.city}, ${a.state} ${a.zipCode}</span>
        </div>
        <div class="address-actions">
          ${!a.default ? `<button class="secondary-btn" onclick="setDefaultAddress(${a.id})">Set Default</button>` : ''}
          <button class="secondary-btn" onclick="deleteAddress(${a.id})">Delete</button>
        </div>
      </div>
    `).join('');
  }
}

window.selectAddress = function(id) {
  AppState.selectedAddressId = id;
  const chosen = AppState.addresses.find(a => a.id === id);
  if (chosen) {
    document.getElementById('navAddressDisplay').textContent = `${chosen.label}: ${chosen.street}, ${chosen.city}`;
    if (chosen.city) {
      AppState.currentCity = chosen.city;
      AppState.currentLocality = chosen.suite || chosen.street || 'Indiranagar';
      updateHeroDeliveryBadge(chosen.city);
    }
  }
};

window.setDefaultAddress = async function(addressId) {
  try {
    const res = await fetch(`${API_BASE}/api/v1/users/${AppState.currentUser.id}/addresses/${addressId}/default`, {
      method: 'PUT'
    });
    if (res.ok) {
      showToast('Default delivery location updated!', 'success');
      await loadUserData();
    }
  } catch (err) {
    showToast('Failed to update default address', 'error');
  }
};

window.deleteAddress = async function(addressId) {
  try {
    const res = await fetch(`${API_BASE}/api/v1/users/${AppState.currentUser.id}/addresses/${addressId}`, {
      method: 'DELETE'
    });
    if (res.ok) {
      showToast('Address removed', 'info');
      await loadUserData();
    }
  } catch (err) {
    showToast('Failed to remove address', 'error');
  }
};

async function handleProfileSubmit(e) {
  e.preventDefault();
  const name = document.getElementById('profileNameInput').value.trim();
  const email = document.getElementById('profileEmailInput').value.trim();
  const phone = document.getElementById('profilePhoneInput').value.trim();
  const password = document.getElementById('profilePasswordInput').value;
  const avatarUrl = document.getElementById('profileAvatarUrl').value.trim();
  const errorAlert = document.getElementById('profileErrorAlert');
  const confirmCard = document.getElementById('profileUpdateConfirmationCard');
  const submitBtn = document.getElementById('saveProfileBtn');

  if (errorAlert) errorAlert.style.display = 'none';
  if (confirmCard) confirmCard.style.display = 'none';

  submitBtn.disabled = true;
  submitBtn.innerHTML = '<span>⏳</span> Saving changes & dispatching alerts...';

  try {
    const payload = { name, email, phone, avatarUrl };
    if (password && password.trim().length > 0) {
      payload.password = password.trim();
    }

    const res = await fetch(`${API_BASE}/api/v1/users/${AppState.currentUser.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    const data = await res.json();

    if (!res.ok) {
      if (errorAlert) {
        errorAlert.textContent = data.message || data.error || 'Failed to update account information.';
        errorAlert.style.display = 'block';
      }
      submitBtn.disabled = false;
      submitBtn.innerHTML = '<span>💾</span> Save Changes & Dispatch Confirmation (UC-7)';
      return;
    }

    // Step 5: Service confirms updates and notifies user via email or SMS
    AppState.currentUser = data;
    document.getElementById('userNameDisplay').textContent = data.name;
    document.getElementById('userAvatarImg').src = data.avatarUrl;

    if (confirmCard) {
      document.getElementById('profileConfirmationSummary').textContent = data.confirmationMessage ||
        `Account details for ${data.name} updated. Confirmation dispatched to ${data.phone} and ${data.email}.`;
      document.getElementById('profileSmsBadge').textContent = `Status: ${data.smsStatus || 'DISPATCHED'} to ${data.phone}`;
      document.getElementById('profileEmailBadge').textContent = `Status: ${data.emailStatus || 'DISPATCHED'} to ${data.email}`;
      confirmCard.style.display = 'block';
    }

    showToast('Account details updated & notification dispatched via SMS/Email!', 'success');
    submitBtn.disabled = false;
    submitBtn.innerHTML = '<span>💾</span> Save Changes & Dispatch Confirmation (UC-7)';
  } catch (err) {
    console.error('Account update error:', err);
    if (errorAlert) {
      errorAlert.textContent = 'Network or server error while updating account.';
      errorAlert.style.display = 'block';
    }
    submitBtn.disabled = false;
    submitBtn.innerHTML = '<span>💾</span> Save Changes & Dispatch Confirmation (UC-7)';
  }
}

async function handleNewAddressSubmit(e) {
  e.preventDefault();
  const label = document.getElementById('newAddressLabel').value.trim();
  const street = document.getElementById('newAddressStreet').value.trim();
  const suite = document.getElementById('newAddressSuite').value.trim();
  const city = document.getElementById('newAddressCity').value.trim();
  const state = document.getElementById('newAddressState').value.trim();
  const zipCode = document.getElementById('newAddressZip').value.trim();

  try {
    const res = await fetch(`${API_BASE}/api/v1/users/${AppState.currentUser.id}/addresses`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ label, street, suite, city, state, zipCode, default: false })
    });
    if (res.ok) {
      showToast('New location saved to address book!', 'success');
      document.getElementById('newAddressForm').reset();
      document.getElementById('newAddressForm').style.display = 'none';
      await loadUserData();
    }
  } catch (err) {
    showToast('Failed to add address', 'error');
  }
}

// ==========================================
// API Operations: Restaurants & Menus (UC-2, UC-3, UC-8)
// ==========================================
async function loadRestaurants() {
  try {
    let url = `${API_BASE}/api/v1/restaurants?userId=${AppState.currentUser.id}`;
    if (AppState.searchQuery) url += `&query=${encodeURIComponent(AppState.searchQuery)}`;
    if (AppState.selectedCuisine && AppState.selectedCuisine !== 'All') {
      url += `&cuisine=${encodeURIComponent(AppState.selectedCuisine)}`;
    }

    const res = await fetch(url);
    if (res.ok) {
      AppState.restaurants = await res.json();
      filterAndRenderRestaurants();
      updateFavCountBadge();
    }
  } catch (err) {
    console.error('Failed to load restaurants:', err);
  }
}

function filterAndRenderRestaurants() {
  let list = [...AppState.restaurants];

  // Pure Veg Filter
  if (AppState.vegOnly) {
    list = list.filter(r => r.name.toLowerCase().includes('pure veg') || r.cuisine.toLowerCase().includes('south indian'));
  }

  // Sorting
  if (AppState.sortBy === 'rating') {
    list.sort((a, b) => b.rating - a.rating);
  } else if (AppState.sortBy === 'deliveryTime') {
    list.sort((a, b) => a.deliveryTimeMinutes - b.deliveryTimeMinutes);
  } else if (AppState.sortBy === 'deliveryFee') {
    list.sort((a, b) => a.deliveryFee - b.deliveryFee);
  }

  AppState.filteredRestaurants = list;
  renderRestaurantGrid(list);
}

function renderRestaurantGrid(list) {
  const grid = document.getElementById('restaurantGrid');
  const countEl = document.getElementById('resultsCount');

  if (list.length === 0) {
    grid.innerHTML = `
      <div style="grid-column: 1/-1; text-align: center; padding: 60px 20px;">
        <span style="font-size: 3rem;">🔍</span>
        <h3 style="margin-top: 12px; font-size: 1.25rem;">No kitchens found</h3>
        <p style="color: var(--text-dim); margin-top: 6px;">Try searching for biryani, butter chicken, dosa, or pav bhaji.</p>
      </div>
    `;
    countEl.textContent = 'Showing 0 kitchens';
    return;
  }

  countEl.textContent = `Showing ${list.length} kitchen${list.length > 1 ? 's' : ''}`;

  grid.innerHTML = list.map(r => `
    <article class="restaurant-card" onclick="openMenuModal(${r.id})" id="restaurantCard-${r.id}">
      <div class="card-media">
        <img src="${r.imageUrl}" alt="${r.name}" class="card-img" loading="lazy">
        <div class="card-badges">
          ${r.featured ? '<span class="card-badge-pill featured">★ TOP PICK</span>' : ''}
          <span class="card-badge-pill">${r.cuisine}</span>
        </div>
        <button class="fav-btn ${r.favorite ? 'active' : ''}" id="favBtn-${r.id}" onclick="event.stopPropagation(); toggleFavorite(${r.id})" title="${r.favorite ? 'Remove from favorites' : 'Add to favorites'}">
          ${r.favorite ? '❤️' : '🤍'}
        </button>
      </div>

      <div class="card-body">
        <div class="card-title-row">
          <h3 class="card-name">${r.name}</h3>
          <div class="card-rating">
            <span>★</span>
            <span>${r.rating.toFixed(1)}</span>
          </div>
        </div>

        <div class="card-cuisine">${r.cuisine} • ${r.reviewCount} reviews</div>
        <div class="card-address">📍 ${r.address}</div>

        <div class="card-meta-row">
          <div class="card-meta-item">
            <span>⏱️</span>
            <strong>${r.deliveryTimeMinutes} mins</strong>
          </div>
          <div class="card-meta-item">
            <span>🛵</span>
            <span>₹${r.deliveryFee.toFixed(0)} delivery</span>
          </div>
          <div class="card-meta-item" style="margin-left: auto;">
            <span>Min ₹${r.minOrderAmount.toFixed(0)}</span>
          </div>
        </div>
      </div>
    </article>
  `).join('');
}

window.toggleFavorite = async function(restaurantId) {
  try {
    const res = await fetch(`${API_BASE}/api/v1/favorites/toggle`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: AppState.currentUser.id, restaurantId })
    });
    if (res.ok) {
      const data = await res.json();
      showToast(data.message, data.favorite ? 'success' : 'info');
      // Update local state
      const target = AppState.restaurants.find(r => r.id === restaurantId);
      if (target) target.favorite = data.favorite;
      filterAndRenderRestaurants();
      updateFavCountBadge();
    }
  } catch (err) {
    showToast('Failed to update favorites', 'error');
  }
};

function updateFavCountBadge() {
  const count = AppState.restaurants.filter(r => r.favorite).length;
  document.getElementById('favCountBadge').textContent = count;
}

function showFavoritesView() {
  const favorites = AppState.restaurants.filter(r => r.favorite);
  document.getElementById('sectionHeading').textContent = 'Your Saved Favorite Kitchens (UC-8)';
  renderRestaurantGrid(favorites);
}

// ==========================================
// Interactive Menu Modal (UC-3)
// ==========================================
window.openMenuModal = async function(restaurantId) {
  const restaurant = AppState.restaurants.find(r => r.id === restaurantId);
  if (!restaurant) return;

  AppState.activeRestaurant = restaurant;
  document.getElementById('menuRestaurantName').textContent = restaurant.name;
  document.getElementById('menuCuisineBadge').textContent = restaurant.cuisine;
  document.getElementById('menuRatingSpan').textContent = `⭐ ${restaurant.rating.toFixed(2)} (${restaurant.reviewCount} reviews)`;
  document.getElementById('menuEtaSpan').textContent = `⏱️ ${restaurant.deliveryTimeMinutes} mins`;
  document.getElementById('menuMinOrderSpan').textContent = `Min Order: ₹${restaurant.minOrderAmount.toFixed(0)}`;
  document.getElementById('menuAddressSpan').textContent = `📍 ${restaurant.address}`;
  document.getElementById('menuHeroBanner').style.backgroundImage = `url('${restaurant.imageUrl}')`;

  // Fetch Menu Items
  try {
    const res = await fetch(`${API_BASE}/api/v1/restaurants/${restaurantId}/menu`);
    if (res.ok) {
      AppState.activeMenu = await res.json();
      AppState.activeMenuCategory = 'ALL';
      document.querySelectorAll('.cat-tab').forEach(t => t.classList.toggle('active', t.dataset.category === 'ALL'));
      renderMenuItems();
      document.getElementById('menuModal').style.display = 'flex';
    }
  } catch (err) {
    showToast('Failed to fetch restaurant menu', 'error');
  }
};

function renderMenuItems() {
  const container = document.getElementById('menuItemsContainer');
  let items = [...AppState.activeMenu];

  if (AppState.activeMenuCategory !== 'ALL') {
    items = items.filter(i => i.category.toLowerCase().includes(AppState.activeMenuCategory.toLowerCase()));
  }
  if (AppState.vegOnly) {
    items = items.filter(i => i.vegetarian);
  }

  if (items.length === 0) {
    container.innerHTML = `<p style="grid-column: 1/-1; text-align: center; color: var(--text-dim); padding: 40px;">No items found in this category.</p>`;
    return;
  }

  container.innerHTML = items.map(item => `
    <div class="menu-item-card" id="menuItem-${item.id}">
      <div class="item-info">
        <div class="item-title-row">
          <span style="font-size: 0.9rem;">${item.vegetarian ? '🟢' : '🔴'}</span>
          <h4 class="item-name">${item.name}</h4>
          ${item.spicy ? '<span class="item-badge spicy">🌶️ SPICY</span>' : ''}
        </div>
        <p class="item-desc">${item.description}</p>
        <span class="item-price">₹${item.price.toFixed(0)}</span>
      </div>

      <div class="item-media">
        <img src="${item.imageUrl}" alt="${item.name}" class="item-img" loading="lazy">
        <button class="add-to-cart-btn" onclick="addToCart(${item.id})">+ ADD</button>
      </div>
    </div>
  `).join('');
}

// ==========================================
// Cart & Checkout (UC-4)
// ==========================================
window.addToCart = function(menuItemId) {
  const item = AppState.activeMenu.find(i => i.id === menuItemId);
  if (!item || !AppState.activeRestaurant) return;

  // Check if cart contains items from another restaurant
  if (AppState.cart.restaurantId && AppState.cart.restaurantId !== AppState.activeRestaurant.id) {
    if (!confirm(`Your basket contains dishes from ${AppState.cart.restaurantName}. Clear basket to order from ${AppState.activeRestaurant.name}?`)) {
      return;
    }
    AppState.cart.items = [];
  }

  AppState.cart.restaurantId = AppState.activeRestaurant.id;
  AppState.cart.restaurantName = AppState.activeRestaurant.name;
  AppState.cart.deliveryFee = AppState.activeRestaurant.deliveryFee;

  const existing = AppState.cart.items.find(i => i.id === item.id);
  if (existing) {
    existing.quantity += 1;
  } else {
    AppState.cart.items.push({
      id: item.id,
      name: item.name,
      price: item.price,
      quantity: 1,
      imageUrl: item.imageUrl
    });
  }

  updateCartUI();
  showToast(`Added ${item.name} to basket!`, 'success');
};

function updateCartUI() {
  const countBadge = document.getElementById('cartCountBadge');
  const totalDisplay = document.getElementById('cartTotalDisplay');
  const itemsList = document.getElementById('cartItemsList');
  const emptyView = document.getElementById('emptyCartView');
  const populatedView = document.getElementById('populatedCartView');
  const drawerFooter = document.getElementById('cartDrawerFooter');

  const totalQty = AppState.cart.items.reduce((sum, i) => sum + i.quantity, 0);
  const subtotal = AppState.cart.items.reduce((sum, i) => sum + (i.price * i.quantity), 0);
  const deliveryFee = AppState.cart.items.length > 0 ? AppState.cart.deliveryFee : 0;
  const tax = subtotal * 0.05; // 5% GST on Restaurant Dining
  const total = subtotal + deliveryFee + tax;

  if (countBadge) countBadge.textContent = totalQty;
  if (totalDisplay) totalDisplay.textContent = `₹${total.toFixed(0)}`;

  if (AppState.cart.items.length === 0) {
    emptyView.style.display = 'flex';
    populatedView.style.display = 'none';
    drawerFooter.style.display = 'none';
    return;
  }

  emptyView.style.display = 'none';
  populatedView.style.display = 'block';
  drawerFooter.style.display = 'block';

  document.getElementById('drawerRestaurantName').textContent = `Ordering from ${AppState.cart.restaurantName}`;

  itemsList.innerHTML = AppState.cart.items.map(item => `
    <div class="cart-item-row">
      <span class="cart-item-name">${item.name}</span>
      <div class="cart-item-stepper">
        <button class="step-btn" onclick="updateItemQuantity(${item.id}, -1)">−</button>
        <span class="step-qty">${item.quantity}</span>
        <button class="step-btn" onclick="updateItemQuantity(${item.id}, 1)">+</button>
      </div>
      <span class="cart-item-price">₹${(item.price * item.quantity).toFixed(0)}</span>
    </div>
  `).join('');

  document.getElementById('summarySubtotal').textContent = `₹${subtotal.toFixed(2)}`;
  document.getElementById('summaryDeliveryFee').textContent = `₹${deliveryFee.toFixed(2)}`;
  document.getElementById('summaryTax').textContent = `₹${tax.toFixed(2)}`;
  document.getElementById('summaryTotal').textContent = `₹${total.toFixed(2)}`;
  document.getElementById('placeOrderTotalSpan').textContent = `₹${total.toFixed(2)}`;
}

window.updateItemQuantity = function(itemId, delta) {
  const item = AppState.cart.items.find(i => i.id === itemId);
  if (!item) return;

  item.quantity += delta;
  if (item.quantity <= 0) {
    AppState.cart.items = AppState.cart.items.filter(i => i.id !== itemId);
    if (AppState.cart.items.length === 0) {
      AppState.cart.restaurantId = null;
      AppState.cart.restaurantName = null;
    }
  }
  updateCartUI();
};

function openCartDrawer() {
  document.getElementById('cartDrawer').style.display = 'flex';
}

function closeCartDrawer() {
  document.getElementById('cartDrawer').style.display = 'none';
}

async function handlePlaceOrder() {
  if (AppState.cart.items.length === 0) return;

  const btn = document.getElementById('placeOrderBtn');
  const btnText = document.getElementById('placeOrderText');
  btn.disabled = true;
  btnText.textContent = 'Verifying UPI / Payment...';

  const chosenAddr = AppState.addresses.find(a => a.id === AppState.selectedAddressId) || AppState.addresses[0];
  const deliveryAddress = chosenAddr 
    ? `${chosenAddr.street} ${chosenAddr.suite || ''}, ${chosenAddr.city}, ${chosenAddr.state} ${chosenAddr.zipCode}`
    : 'Flat 402, Shanti Niketan, Indiranagar, Bengaluru - 560038';

  const instructions = document.getElementById('deliveryInstructionsInput').value.trim();
  const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;

  const payload = {
    userId: AppState.currentUser.id,
    restaurantId: AppState.cart.restaurantId,
    items: AppState.cart.items.map(i => ({ menuItemId: i.id, quantity: i.quantity })),
    deliveryAddress,
    deliveryInstructions: instructions,
    paymentMethod
  };

  try {
    const res = await fetch(`${API_BASE}/api/v1/orders`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (res.ok) {
      const order = await res.json();
      showToast(`SwadExpress Order #ORD-${order.id} placed! Kitchen notified.`, 'success');

      // Clear basket
      AppState.cart.items = [];
      AppState.cart.restaurantId = null;
      AppState.cart.restaurantName = null;
      updateCartUI();
      closeCartDrawer();
      if (document.getElementById('menuModal')) {
        document.getElementById('menuModal').style.display = 'none';
      }

      await loadUserOrders();
      openTrackingModal(order.id);
    } else {
      const errData = await res.json();
      showToast(errData.message || 'Order placement failed', 'error');
    }
  } catch (err) {
    showToast('Failed to connect to backend Order Service', 'error');
  } finally {
    btn.disabled = false;
    btnText.textContent = 'Confirm Order & Pay';
  }
}

// ==========================================
// Live Order Tracking (UC-5)
// ==========================================
async function openTrackingModal(orderId) {
  document.getElementById('trackingModal').style.display = 'flex';
  await pollTrackingStatus(orderId);

  if (AppState.trackingPollTimer) clearInterval(AppState.trackingPollTimer);
  AppState.trackingPollTimer = setInterval(() => pollTrackingStatus(orderId), 3000);
}

async function pollTrackingStatus(orderId) {
  try {
    const res = await fetch(`${API_BASE}/api/v1/tracking/${orderId}`);
    if (res.ok) {
      const tracking = await res.json();
      AppState.currentTracking = tracking;
      renderTrackingView(tracking);
    }
  } catch (err) {
    console.error('Failed to poll tracking:', err);
  }
}

function renderTrackingView(t) {
  document.getElementById('trackOrderTitle').textContent = `Order #ORD-${t.orderId}`;
  document.getElementById('trackEtaDisplay').textContent = t.estimatedDeliveryMinutes > 0 ? `${t.estimatedDeliveryMinutes} mins` : 'Arrived!';
  document.getElementById('trackStatusHeadline').textContent = t.status.replace(/_/g, ' ');
  document.getElementById('trackStatusMessage').textContent = t.statusMessage;

  document.getElementById('trackDriverName').textContent = t.driverName;
  document.getElementById('trackVehicle').textContent = t.vehicleType;
  document.getElementById('trackDriverRating').textContent = `⭐ ${t.driverRating.toFixed(2)} Rating (Vaccinated)`;
  document.getElementById('callDriverBtn').href = `tel:${t.driverPhone}`;

  // Update Stepper
  const step = t.currentStep;
  for (let i = 1; i <= 4; i++) {
    const el = document.getElementById(`step${i}`);
    el.classList.remove('active', 'completed');
    if (i < step) el.classList.add('completed');
    if (i === step) el.classList.add('active');
  }
  for (let i = 1; i <= 3; i++) {
    const line = document.getElementById(`line${i}`);
    line.classList.toggle('active', i < step);
  }

  // Update Map Marker position
  const marker = document.getElementById('driverPulseMarker');
  if (step === 1) marker.style.left = '15%';
  else if (step === 2) marker.style.left = '35%';
  else if (step === 3) marker.style.left = '65%';
  else if (step === 4) marker.style.left = '90%';

  // Show Rate & Review button if Delivered
  const reviewBtn = document.getElementById('reviewDeliveredOrderBtn');
  const advanceBtn = document.getElementById('advanceStepBtn');
  if (step === 4) {
    reviewBtn.style.display = 'block';
    advanceBtn.style.display = 'none';
    reviewBtn.onclick = () => {
      document.getElementById('trackingModal').style.display = 'none';
      openReviewModal(t.orderId);
    };
  } else {
    reviewBtn.style.display = 'none';
    advanceBtn.style.display = 'block';
  }
}

async function handleAdvanceStep() {
  if (!AppState.currentTracking) return;
  const orderId = AppState.currentTracking.orderId;

  try {
    const res = await fetch(`${API_BASE}/api/v1/tracking/${orderId}/advance-step`, {
      method: 'POST'
    });
    if (res.ok) {
      const updated = await res.json();
      AppState.currentTracking = updated;
      renderTrackingView(updated);
      showToast(`Milestone updated: ${updated.status}`, 'info');
      await loadUserOrders();
      await checkActiveLiveOrder();
    }
  } catch (err) {
    showToast('Failed to advance tracking step', 'error');
  }
}

// ==========================================
// Rate & Review (UC-6)
// ==========================================
let currentRatingScore = 5;

function setupStarRating() {
  const stars = document.querySelectorAll('#starRatingWidget .star-btn');
  const scoreText = document.getElementById('ratingScoreText');
  const labels = {
    1: '1.0 - Thoda Aur Behtar Ho Sakta Tha',
    2: '2.0 - Average Swad',
    3: '3.0 - Theek Thaak (Satisfactory)',
    4: '4.0 - Bahut Badhiya Swad Aur Delivery!',
    5: '5.0 - Ekdum Lajawab! (Exceptional Experience)'
  };

  stars.forEach(star => {
    star.addEventListener('click', () => {
      currentRatingScore = parseInt(star.dataset.value);
      stars.forEach(s => {
        s.classList.toggle('active', parseInt(s.dataset.value) <= currentRatingScore);
      });
      scoreText.textContent = labels[currentRatingScore];
    });
  });
}

window.openReviewModal = function(orderId) {
  const order = AppState.activeOrders.find(o => o.id === orderId);
  document.getElementById('reviewOrderId').value = orderId;
  document.getElementById('reviewRestaurantName').textContent = order ? order.restaurantName : 'Completed Order';
  document.getElementById('reviewCommentsInput').value = '';
  document.getElementById('reviewModal').style.display = 'flex';
};

async function handleReviewSubmit(e) {
  e.preventDefault();
  const orderId = parseInt(document.getElementById('reviewOrderId').value);
  const comments = document.getElementById('reviewCommentsInput').value.trim();

  try {
    const res = await fetch(`${API_BASE}/api/v1/reviews`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        orderId,
        userId: AppState.currentUser.id,
        rating: currentRatingScore,
        comments
      })
    });

    if (res.ok) {
      showToast('Dhanyawad! Your review has been submitted to the kitchen.', 'success');
      document.getElementById('reviewModal').style.display = 'none';
      await loadRestaurants(); // Refresh updated ratings
    } else {
      const err = await res.json();
      showToast(err.message || 'Review submission failed', 'error');
    }
  } catch (err) {
    showToast('Failed to submit review', 'error');
  }
}

// ==========================================
// Order History & Active Orders
// ==========================================
async function loadUserOrders() {
  if (!AppState.currentUser) {
    const badge = document.getElementById('activeOrdersBadge');
    if (badge) badge.style.display = 'none';
    const callout = document.getElementById('liveOrderCallout');
    if (callout) callout.style.display = 'none';
    return;
  }
  try {
    const res = await fetch(`${API_BASE}/api/v1/orders/user/${AppState.currentUser.id}`);
    if (res.ok) {
      AppState.activeOrders = await res.json();
      const inProgress = AppState.activeOrders.filter(o => o.status !== 'DELIVERED' && o.status !== 'CANCELLED');
      const badge = document.getElementById('activeOrdersBadge');
      badge.textContent = inProgress.length;
      badge.style.display = inProgress.length > 0 ? 'inline-block' : 'none';
    }
  } catch (err) {
    console.error('Failed to load orders:', err);
  }
}

async function checkActiveLiveOrder() {
  const inProgress = AppState.activeOrders.find(o => o.status !== 'DELIVERED' && o.status !== 'CANCELLED');
  const callout = document.getElementById('liveOrderCallout');

  if (inProgress) {
    document.getElementById('calloutRestaurantName').textContent = inProgress.restaurantName;
    document.getElementById('calloutStatusText').textContent = `Status: ${inProgress.status.replace(/_/g, ' ')} • Tap to view delivery route`;
    callout.style.display = 'block';
  } else {
    callout.style.display = 'none';
  }
}

function openOrdersHistoryModal() {
  const container = document.getElementById('ordersListContainer');
  if (AppState.activeOrders.length === 0) {
    container.innerHTML = `<p style="text-align: center; color: var(--text-dim); padding: 40px;">No past orders found.</p>`;
  } else {
    container.innerHTML = AppState.activeOrders.map(o => `
      <div class="order-history-card">
        <div class="order-history-header">
          <div>
            <div class="order-history-rest">${o.restaurantName}</div>
            <span style="font-size: 0.75rem; color: var(--text-dim);">Order #ORD-${o.id} • ${new Date(o.createdAt).toLocaleDateString()}</span>
          </div>
          <span class="order-status-tag ${o.status}">${o.status.replace(/_/g, ' ')}</span>
        </div>

        <div class="order-history-items">
          ${o.items.map(i => `${i.quantity}x ${i.itemName}`).join(', ')}
        </div>

        <div class="order-history-footer">
          <strong>Total: ₹${o.totalAmount.toFixed(2)} (${o.paymentMethod})</strong>
          <div style="display: flex; gap: 8px;">
            <button class="secondary-btn" onclick="openTrackingModal(${o.id})">Track Order</button>
            ${o.status === 'DELIVERED' ? `<button class="primary-btn" onclick="openReviewModal(${o.id})">Rate & Review</button>` : ''}
          </div>
        </div>
      </div>
    `).join('');
  }
  document.getElementById('ordersHistoryModal').style.display = 'flex';
}

// ==========================================
// Customer Support Tickets (UC-10)
// ==========================================
async function openSupportModal() {
  document.getElementById('supportModal').style.display = 'flex';

  // Populate orders select
  const orderSelect = document.getElementById('ticketOrderSelect');
  orderSelect.innerHTML = `<option value="">-- General Inquiry / No Order --</option>` +
    AppState.activeOrders.map(o => `<option value="${o.id}">Order #ORD-${o.id} (${o.restaurantName})</option>`).join('');

  await loadSupportTickets();
}

async function loadSupportTickets() {
  try {
    const res = await fetch(`${API_BASE}/api/v1/support/tickets/user/${AppState.currentUser.id}`);
    if (res.ok) {
      AppState.userTickets = await res.json();
      const container = document.getElementById('ticketsListContainer');
      if (AppState.userTickets.length === 0) {
        container.innerHTML = `<p style="color: var(--text-dim); font-size: 0.8125rem;">No support requests filed yet.</p>`;
        return;
      }
      container.innerHTML = AppState.userTickets.map(t => `
        <div class="ticket-item">
          <div class="ticket-header">
            <span class="ticket-num">${t.ticketNumber} (${t.category.replace(/_/g, ' ')})</span>
            <span class="ticket-status-badge ${t.status}">${t.status}</span>
          </div>
          <p class="ticket-desc">${t.description}</p>
          <div class="ticket-notes">ℹ️ Resolution: ${t.resolutionNotes || 'Agent reviewing ticket...'}</div>
        </div>
      `).join('');
    }
  } catch (err) {
    console.error('Failed to load tickets:', err);
  }
}

async function handleTicketSubmit(e) {
  e.preventDefault();
  const orderIdVal = document.getElementById('ticketOrderSelect').value;
  const category = document.getElementById('ticketCategorySelect').value;
  const description = document.getElementById('ticketDescriptionInput').value.trim();

  try {
    const res = await fetch(`${API_BASE}/api/v1/support/tickets`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: AppState.currentUser.id,
        orderId: orderIdVal ? parseInt(orderIdVal) : null,
        category,
        description
      })
    });

    if (res.ok) {
      const ticket = await res.json();
      showToast(`Support Ticket ${ticket.ticketNumber} registered!`, 'success');
      document.getElementById('supportTicketForm').reset();
      await loadSupportTickets();
    }
  } catch (err) {
    showToast('Failed to create support ticket', 'error');
  }
}

// ==========================================
// Account Modal Management (UC-7, UC-9)
// ==========================================
async function openAccountModal(tab = 'profile') {
  // Step 2: Account Management Service retrieves the user’s account details
  await loadUserData();

  const errAlert = document.getElementById('profileErrorAlert');
  if (errAlert) errAlert.style.display = 'none';
  const confirmCard = document.getElementById('profileUpdateConfirmationCard');
  if (confirmCard) confirmCard.style.display = 'none';
  const pwdInput = document.getElementById('profilePasswordInput');
  if (pwdInput) pwdInput.value = '';

  document.getElementById('accountModal').style.display = 'flex';
  switchAccountTab(tab);
}

function switchAccountTab(tab) {
  const profileTabBtn = document.getElementById('profileTabBtn');
  const addressesTabBtn = document.getElementById('addressesTabBtn');
  const profileContent = document.getElementById('profileTabContent');
  const addressesContent = document.getElementById('addressesTabContent');

  if (tab === 'profile') {
    profileTabBtn.classList.add('active');
    addressesTabBtn.classList.remove('active');
    profileContent.style.display = 'block';
    addressesContent.style.display = 'none';
  } else {
    profileTabBtn.classList.remove('active');
    addressesTabBtn.classList.add('active');
    profileContent.style.display = 'none';
    addressesContent.style.display = 'block';
  }
}

// ==========================================
// User Registration & Authentication (UC-1)
// ==========================================
function openRegisterModal(mode = 'register') {
  document.getElementById('registerModal').style.display = 'flex';
  document.getElementById('registerConfirmationView').style.display = 'none';
  switchAuthTab(mode);
}

function closeRegisterModal() {
  document.getElementById('registerModal').style.display = 'none';
}

function switchAuthTab(mode) {
  const regTab = document.getElementById('authTabRegisterBtn');
  const loginTab = document.getElementById('authTabLoginBtn');
  const regContainer = document.getElementById('registerFormContainer');
  const loginContainer = document.getElementById('loginFormContainer');
  const modalTitle = document.getElementById('registerModalTitle');
  const modalSub = document.getElementById('registerModalSubtitle');

  const regErr = document.getElementById('registerErrorAlert');
  if (regErr) regErr.style.display = 'none';
  const loginErr = document.getElementById('loginErrorAlert');
  if (loginErr) loginErr.style.display = 'none';

  if (mode === 'login') {
    if (regTab) regTab.classList.remove('active');
    if (loginTab) loginTab.classList.add('active');
    if (regContainer) regContainer.style.display = 'none';
    if (loginContainer) loginContainer.style.display = 'block';
    if (modalTitle) modalTitle.textContent = 'Sign In to SwadExpress';
    if (modalSub) modalSub.textContent = 'Access your saved addresses, live orders, and favorites';
  } else {
    if (regTab) regTab.classList.add('active');
    if (loginTab) loginTab.classList.remove('active');
    if (regContainer) regContainer.style.display = 'block';
    if (loginContainer) loginContainer.style.display = 'none';
    if (modalTitle) modalTitle.textContent = 'Create New Account (UC-1)';
    if (modalSub) modalSub.textContent = 'Join SwadExpress for authentic flavors & live tracking';
  }
}

async function handleUserLogin(e) {
  e.preventDefault();
  const emailInput = document.getElementById('loginEmail');
  const passwordInput = document.getElementById('loginPassword');
  const errorAlert = document.getElementById('loginErrorAlert');
  const submitBtn = document.getElementById('submitLoginBtn');

  if (errorAlert) errorAlert.style.display = 'none';
  submitBtn.disabled = true;
  submitBtn.innerHTML = '<span>⏳</span> Signing In...';

  try {
    const res = await fetch(`${API_BASE}/api/v1/users/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: emailInput.value.trim(),
        password: passwordInput.value
      })
    });

    const data = await res.json();

    if (!res.ok) {
      if (errorAlert) {
        errorAlert.textContent = data.message || data.error || 'Invalid email or password.';
        errorAlert.style.display = 'block';
      }
      submitBtn.disabled = false;
      submitBtn.innerHTML = '<span>🔐</span> Sign In to SwadExpress';
      return;
    }

    AppState.currentUser = data;
    closeRegisterModal();
    updateAuthUI();
    await loadUserData();
    await loadUserOrders();
    showToast(`Welcome back, ${data.name}!`, 'success');
  } catch (err) {
    console.error('Login error:', err);
    if (errorAlert) {
      errorAlert.textContent = 'Failed to connect to authentication service.';
      errorAlert.style.display = 'block';
    }
  } finally {
    submitBtn.disabled = false;
    submitBtn.innerHTML = '<span>🔐</span> Sign In to SwadExpress';
  }
}

async function handleUserRegistration(e) {
  e.preventDefault();
  const nameInput = document.getElementById('regFullName');
  const emailInput = document.getElementById('regEmail');
  const phoneInput = document.getElementById('regPhone');
  const passwordInput = document.getElementById('regPassword');
  const errorAlert = document.getElementById('registerErrorAlert');
  const submitBtn = document.getElementById('submitRegisterBtn');

  errorAlert.style.display = 'none';

  const regData = {
    name: nameInput.value.trim(),
    email: emailInput.value.trim(),
    phone: phoneInput.value.trim(),
    password: passwordInput.value
  };

  submitBtn.disabled = true;
  submitBtn.innerHTML = '<span>⏳</span> Validating & Creating Account...';

  try {
    const res = await fetch(`${API_BASE}/api/v1/users/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(regData)
    });

    const data = await res.json();

    if (!res.ok) {
      errorAlert.textContent = data.message || data.error || 'Registration failed. Please verify your information.';
      errorAlert.style.display = 'block';
      submitBtn.disabled = false;
      submitBtn.innerHTML = '<span>🚀</span> Create Account & Send Confirmation';
      return;
    }

    // Step 5: Display confirmation screen showing dispatched SMS and Email
    document.getElementById('registerFormContainer').style.display = 'none';
    const loginCont = document.getElementById('loginFormContainer');
    if (loginCont) loginCont.style.display = 'none';
    const authTabs = document.getElementById('authTabsWrapper');
    if (authTabs) authTabs.style.display = 'none';
    
    const confirmView = document.getElementById('registerConfirmationView');
    confirmView.style.display = 'block';

    document.getElementById('regSuccessSummary').textContent = data.confirmationMessage || 
      `Welcome to SwadExpress, ${data.name}! Your account has been created. Confirmation notifications have been dispatched.`;
    document.getElementById('regSmsRecipient').textContent = `Status: ${data.smsStatus || 'DISPATCHED'} to ${data.phone}`;
    document.getElementById('regEmailRecipient').textContent = `Status: ${data.emailStatus || 'DISPATCHED'} to ${data.email}`;
    document.getElementById('regNewUserName').textContent = data.name;

    window._lastRegisteredUser = data;
    showToast(`Account created for ${data.name}! Confirmation email & SMS dispatched.`, 'success');
  } catch (err) {
    errorAlert.textContent = 'Server connection error. Please try again.';
    errorAlert.style.display = 'block';
  } finally {
    submitBtn.disabled = false;
    submitBtn.innerHTML = '<span>🚀</span> Create Account & Send Confirmation';
  }
}

function handleContinueAsRegisteredUser() {
  if (window._lastRegisteredUser) {
    const user = window._lastRegisteredUser;
    AppState.currentUser = {
      id: user.id,
      name: user.name,
      email: user.email,
      phone: user.phone,
      avatarUrl: user.avatarUrl
    };
    closeRegisterModal();
    updateAuthUI();
    loadUserData();
    showToast(`Logged in as ${user.name}! Welcome to SwadExpress.`, 'success');
  } else {
    closeRegisterModal();
  }
}

function handleLogout() {
  AppState.currentUser = null;
  const accountModal = document.getElementById('accountModal');
  if (accountModal) accountModal.style.display = 'none';
  updateAuthUI();
  showToast('You have been logged out of SwadExpress. Register or Sign In anytime!', 'info');
}

function updateAuthUI() {
  const loginBtn = document.getElementById('navLoginBtn');
  const profileBtn = document.getElementById('navProfileBtn');

  if (AppState.currentUser) {
    if (loginBtn) loginBtn.style.display = 'none';
    if (profileBtn) {
      profileBtn.style.display = 'inline-flex';
      const parts = AppState.currentUser.name.split(' ');
      document.getElementById('userNameDisplay').textContent = parts[0] + (parts[1] ? ' ' + parts[1][0] + '.' : '');
      document.getElementById('userAvatarImg').src = AppState.currentUser.avatarUrl || 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=120&q=80';
    }
  } else {
    if (loginBtn) loginBtn.style.display = 'inline-flex';
    if (profileBtn) profileBtn.style.display = 'none';
  }
}

// ==========================================
// Delivery Location Picker & Leaflet Map
// ==========================================
const INDIAN_CITIES = {
  'Bengaluru': {
    city: 'Bengaluru',
    locality: 'Indiranagar',
    lat: 12.9784,
    lng: 77.6408,
    state: 'Karnataka',
    sub: 'Serving Indiranagar, Koramangala, HSR, Whitefield & central zones'
  },
  'Mumbai': {
    city: 'Mumbai',
    locality: 'Bandra West',
    lat: 19.0596,
    lng: 72.8295,
    state: 'Maharashtra',
    sub: 'Serving Bandra, Andheri, Colaba, Juhu & BKC'
  },
  'Delhi NCR': {
    city: 'Delhi NCR',
    locality: 'Connaught Place',
    lat: 28.6315,
    lng: 77.2167,
    state: 'Delhi',
    sub: 'Serving CP, Cyber Hub Gurgaon, Noida & South Delhi'
  },
  'Hyderabad': {
    city: 'Hyderabad',
    locality: 'Banjara Hills',
    lat: 17.4156,
    lng: 78.4350,
    state: 'Telangana',
    sub: 'Serving Banjara Hills, Jubilee Hills, Hitec City & Gachibowli'
  },
  'Pune': {
    city: 'Pune',
    locality: 'Koregaon Park',
    lat: 18.5362,
    lng: 73.8940,
    state: 'Maharashtra',
    sub: 'Serving Koregaon Park, Kothrud, Viman Nagar & Kalyani Nagar'
  },
  'Chennai': {
    city: 'Chennai',
    locality: 'Anna Nagar',
    lat: 13.0850,
    lng: 80.2101,
    state: 'Tamil Nadu',
    sub: 'Serving Anna Nagar, T. Nagar, Adyar & Nungambakkam'
  }
};

let deliveryMapInstance = null;
let deliveryMarker = null;

function updateHeroDeliveryBadge(city) {
  const heroBadge = document.getElementById('heroDeliveryBadge');
  if (heroBadge) {
    const formattedCity = (city || 'BENGALURU').toUpperCase();
    heroBadge.textContent = `⚡ SUPERFAST 20-MIN DELIVERY ACROSS ${formattedCity}`;
    heroBadge.classList.remove('badge-pulse');
    void heroBadge.offsetWidth;
    heroBadge.classList.add('badge-pulse');
  }
}

function openLocationPickerModal() {
  const modal = document.getElementById('locationPickerModal');
  if (!modal) return;
  modal.style.display = 'flex';

  const quickContainer = document.getElementById('savedAddressesQuickContainer');
  const quickList = document.getElementById('quickAddressesList');
  if (quickContainer && quickList && AppState.addresses && AppState.addresses.length > 0) {
    quickContainer.style.display = 'block';
    quickList.innerHTML = AppState.addresses.map(a => `
      <div class="quick-addr-item" onclick="selectQuickAddress(${a.id})" style="display: flex; align-items: center; justify-content: space-between; padding: 8px 12px; background: var(--bg-card); border: 1px solid ${a.id === AppState.selectedAddressId ? 'var(--accent-saffron)' : 'var(--border-subtle)'}; border-radius: 8px; cursor: pointer; transition: all 0.2s;">
        <div style="display: flex; align-items: center; gap: 8px;">
          <span style="font-size: 1.1rem;">${a.label === 'Home' ? '🏠' : a.label === 'Office' ? '🏢' : '📍'}</span>
          <div>
            <div style="font-size: 0.825rem; font-weight: 700; color: var(--text-main);">${a.label}: ${a.street}</div>
            <div style="font-size: 0.75rem; color: var(--text-muted);">${a.city}, ${a.zipCode}</div>
          </div>
        </div>
        <span style="font-size: 0.75rem; font-weight: 700; color: var(--accent-saffron);">${a.id === AppState.selectedAddressId ? '✓ ACTIVE' : 'Select'}</span>
      </div>
    `).join('');
  } else if (quickContainer) {
    quickContainer.style.display = 'none';
  }

  const currentCityName = AppState.currentCity || 'Bengaluru';
  document.querySelectorAll('.city-chip').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.city === currentCityName);
  });

  setTimeout(() => {
    initDeliveryMap();
  }, 120);
}

function initDeliveryMap() {
  const container = document.getElementById('deliveryMapContainer');
  if (!container) return;

  if (typeof L === 'undefined') {
    console.warn('Leaflet script is still loading...');
    setTimeout(initDeliveryMap, 300);
    return;
  }

  const currentLat = AppState.locationLat || 12.9784;
  const currentLng = AppState.locationLng || 77.6408;

  if (!deliveryMapInstance) {
    deliveryMapInstance = L.map('deliveryMapContainer', {
      zoomControl: true,
      attributionControl: false
    }).setView([currentLat, currentLng], 14);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(deliveryMapInstance);

    const customIcon = L.divIcon({
      className: 'custom-map-pin',
      html: '<div class="pin-marker">📍</div>',
      iconSize: [32, 32],
      iconAnchor: [16, 30]
    });

    deliveryMarker = L.marker([currentLat, currentLng], {
      draggable: true,
      icon: customIcon
    }).addTo(deliveryMapInstance);

    deliveryMarker.on('dragend', function(e) {
      const pos = e.target.getLatLng();
      handleMapPinMoved(pos.lat, pos.lng);
    });

    deliveryMapInstance.on('click', function(e) {
      deliveryMarker.setLatLng(e.latlng);
      handleMapPinMoved(e.latlng.lat, e.latlng.lng);
    });
  } else {
    deliveryMapInstance.invalidateSize();
    deliveryMapInstance.setView([currentLat, currentLng], 14);
    deliveryMarker.setLatLng([currentLat, currentLng]);
  }
}

function selectCity(cityName) {
  const target = INDIAN_CITIES[cityName];
  if (!target) return;

  AppState.currentCity = target.city;
  AppState.currentLocality = target.locality;
  AppState.locationLat = target.lat;
  AppState.locationLng = target.lng;

  document.querySelectorAll('.city-chip').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.city === cityName);
  });

  const tag = document.getElementById('mapLocalityTag');
  if (tag) tag.textContent = `${target.locality}, ${target.city}`;
  const title = document.getElementById('selectedLocTitle');
  if (title) title.textContent = `${target.locality}, ${target.city}`;
  const sub = document.getElementById('selectedLocSubtitle');
  if (sub) sub.textContent = target.sub;

  if (deliveryMapInstance && deliveryMarker) {
    deliveryMapInstance.setView([target.lat, target.lng], 14);
    deliveryMarker.setLatLng([target.lat, target.lng]);
  }
}

function handleMapPinMoved(lat, lng) {
  AppState.locationLat = lat;
  AppState.locationLng = lng;

  let closestCity = 'Bengaluru';
  let minDistance = Infinity;
  for (const [key, info] of Object.entries(INDIAN_CITIES)) {
    const d = Math.hypot(lat - info.lat, lng - info.lng);
    if (d < minDistance) {
      minDistance = d;
      closestCity = key;
    }
  }

  const target = INDIAN_CITIES[closestCity];
  AppState.currentCity = target.city;
  AppState.currentLocality = target.locality;

  document.querySelectorAll('.city-chip').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.city === closestCity);
  });

  const tag = document.getElementById('mapLocalityTag');
  if (tag) tag.textContent = `${target.city} (${lat.toFixed(3)}, ${lng.toFixed(3)})`;
  const title = document.getElementById('selectedLocTitle');
  if (title) title.textContent = `${target.locality}, ${target.city}`;
  const sub = document.getElementById('selectedLocSubtitle');
  if (sub) sub.textContent = `Pinpoint GPS: [${lat.toFixed(4)}, ${lng.toFixed(4)}] • Ready for 20-min express delivery`;
}

function confirmLocationSelection() {
  const city = AppState.currentCity || 'Bengaluru';
  const locality = AppState.currentLocality || 'Indiranagar';

  const navDisplay = document.getElementById('navAddressDisplay');
  if (navDisplay) {
    navDisplay.textContent = `${locality}, ${city}`;
  }

  updateHeroDeliveryBadge(city);

  const modal = document.getElementById('locationPickerModal');
  if (modal) modal.style.display = 'none';

  showToast(`📍 Delivery location set to ${locality}, ${city}! 20-min express delivery active.`, 'success');
}

window.selectQuickAddress = function(id) {
  const addr = AppState.addresses.find(a => a.id === id);
  if (!addr) return;
  AppState.selectedAddressId = id;
  AppState.currentCity = addr.city || 'Bengaluru';
  AppState.currentLocality = addr.suite || addr.street || 'Indiranagar';

  const match = Object.values(INDIAN_CITIES).find(c => c.city.toLowerCase() === addr.city.toLowerCase());
  if (match) {
    AppState.locationLat = match.lat;
    AppState.locationLng = match.lng;
    selectCity(match.city);
  }

  confirmLocationSelection();
};

