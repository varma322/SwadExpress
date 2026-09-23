package com.fooddelivery.data;

import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderItem;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.repository.OrderItemRepository;
import com.fooddelivery.order.repository.OrderRepository;
import com.fooddelivery.payment.entity.PaymentTransaction;
import com.fooddelivery.payment.repository.PaymentRepository;
import com.fooddelivery.restaurant.entity.Favorite;
import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.FavoriteRepository;
import com.fooddelivery.restaurant.repository.MenuItemRepository;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import com.fooddelivery.review.entity.Review;
import com.fooddelivery.review.repository.ReviewRepository;
import com.fooddelivery.support.entity.SupportTicket;
import com.fooddelivery.support.repository.SupportTicketRepository;
import com.fooddelivery.tracking.entity.OrderTracking;
import com.fooddelivery.tracking.repository.OrderTrackingRepository;
import com.fooddelivery.user.entity.Address;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.repository.AddressRepository;
import com.fooddelivery.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final FavoriteRepository favoriteRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final OrderTrackingRepository trackingRepository;
    private final ReviewRepository reviewRepository;
    private final SupportTicketRepository supportTicketRepository;

    public DataSeeder(UserRepository userRepository,
                      AddressRepository addressRepository,
                      RestaurantRepository restaurantRepository,
                      MenuItemRepository menuItemRepository,
                      FavoriteRepository favoriteRepository,
                      OrderRepository orderRepository,
                      OrderItemRepository orderItemRepository,
                      PaymentRepository paymentRepository,
                      OrderTrackingRepository trackingRepository,
                      ReviewRepository reviewRepository,
                      SupportTicketRepository supportTicketRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.favoriteRepository = favoriteRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.trackingRepository = trackingRepository;
        this.reviewRepository = reviewRepository;
        this.supportTicketRepository = supportTicketRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        // 1. Seed Demo Indian User
        User user = new User(
                "Aarav Sharma",
                "aarav.sharma@example.in",
                "+91 98765 43210",
                "password123",
                "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=200&q=80"
        );
        user = userRepository.save(user);

        // 2. Seed Bengaluru Delivery Addresses
        Address homeAddr = new Address(
                user.getId(),
                "Home",
                "Flat 402, Shanti Niketan Apts, 12th Main",
                "HAL 2nd Stage, Indiranagar",
                "Bengaluru",
                "Karnataka",
                "560038",
                true
        );
        Address workAddr = new Address(
                user.getId(),
                "Office",
                "5th Floor, Salarpuria Cyber Park",
                "Electronic City Phase 1, Hosur Rd",
                "Bengaluru",
                "Karnataka",
                "560100",
                false
        );
        addressRepository.saveAll(List.of(homeAddr, workAddr));

        // 3. Seed Authentic Indian Restaurants (Prices in INR ₹)
        Restaurant r1 = new Restaurant(
                "Punjab Grill & Tandoori Dhaba",
                "North Indian",
                4.88,
                320,
                25,
                BigDecimal.valueOf(35.00),
                BigDecimal.valueOf(200.00),
                "https://images.unsplash.com/photo-1585937421612-70a008356fbe?auto=format&fit=crop&w=800&q=80",
                "100 Feet Rd, Indiranagar, Bengaluru",
                true,
                true
        );

        Restaurant r2 = new Restaurant(
                "Meghana Foods & Royal Biryani",
                "Biryani & Mughlai",
                4.94,
                580,
                30,
                BigDecimal.valueOf(40.00),
                BigDecimal.valueOf(250.00),
                "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=800&q=80",
                "Koramangala 5th Block, Bengaluru",
                true,
                true
        );

        Restaurant r3 = new Restaurant(
                "Sri Udupi Krishna Grand (Pure Veg)",
                "South Indian",
                4.91,
                410,
                18,
                BigDecimal.valueOf(25.00),
                BigDecimal.valueOf(120.00),
                "https://images.unsplash.com/photo-1630383249896-424e482df921?auto=format&fit=crop&w=800&q=80",
                "CMH Road, Indiranagar, Bengaluru",
                true,
                true
        );

        Restaurant r4 = new Restaurant(
                "Mumbai Tadka & Street Bites",
                "Street Food & Chaat",
                4.76,
                245,
                15,
                BigDecimal.valueOf(20.00),
                BigDecimal.valueOf(100.00),
                "https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=800&q=80",
                "27th Main Rd, HSR Layout, Bengaluru",
                true,
                false
        );

        Restaurant r5 = new Restaurant(
                "Coastal Karavalli & Malabar Express",
                "Coastal & Seafood",
                4.85,
                190,
                35,
                BigDecimal.valueOf(45.00),
                BigDecimal.valueOf(300.00),
                "https://images.unsplash.com/photo-1552611052-33e04de081de?auto=format&fit=crop&w=800&q=80",
                "Residency Road, Ashok Nagar, Bengaluru",
                true,
                false
        );

        Restaurant r6 = new Restaurant(
                "Bengal Sweet & Kolkata Kathi Rolls",
                "Bengali & Sweets",
                4.79,
                160,
                22,
                BigDecimal.valueOf(30.00),
                BigDecimal.valueOf(150.00),
                "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?auto=format&fit=crop&w=800&q=80",
                "BTM Layout 2nd Stage, Bengaluru",
                true,
                false
        );

        restaurantRepository.saveAll(List.of(r1, r2, r3, r4, r5, r6));

        // 4. Seed Menu Items in INR (₹)
        // Punjab Grill
        MenuItem m1 = new MenuItem(r1.getId(), "Amritsari Paneer Tikka", "Starters", "Cottage cheese steeped in mustard oil, ajwain, and Kashmiri red chili marinade, char-grilled in clay tandoor.", BigDecimal.valueOf(240.00), "https://images.unsplash.com/photo-1596797038530-2c107229654b?auto=format&fit=crop&w=600&q=80", true, false, true);
        MenuItem m2 = new MenuItem(r1.getId(), "Butter Chicken Murgh Makhani", "Mains", "Tandoor smoked chicken morsels simmered in velvety fenugreek and buttered tomato gravy.", BigDecimal.valueOf(380.00), "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?auto=format&fit=crop&w=600&q=80", false, false, true);
        MenuItem m3 = new MenuItem(r1.getId(), "Dal Makhani Bukhara", "Mains", "Black lentils slow-cooked overnight with white butter, vine-ripened tomatoes, and fresh cream.", BigDecimal.valueOf(220.00), "https://images.unsplash.com/photo-1546833999-b9f581a1996d?auto=format&fit=crop&w=600&q=80", true, false, true);
        MenuItem m4 = new MenuItem(r1.getId(), "Garlic Butter Naan Basket (2 pcs)", "Breads", "Traditional clay-oven leavened flatbread brushed with roasted garlic ghee and cilantro.", BigDecimal.valueOf(90.00), "https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=600&q=80", true, false, true);
        MenuItem m5 = new MenuItem(r1.getId(), "Gulab Jamun with Rabdi", "Desserts", "Warm khoya dumplings steeped in saffron cardamom syrup, served with chilled malai rabdi.", BigDecimal.valueOf(130.00), "https://images.unsplash.com/photo-1624300629298-e9de39c13be5?auto=format&fit=crop&w=600&q=80", true, false, true);

        // Meghana Foods Biryani
        MenuItem m6 = new MenuItem(r2.getId(), "Special Hyderabadi Chicken Dum Biryani", "Biryani", "Long-grain aromatic basmati rice layered with marinated chicken, saffron milk, and fried onions. Served with mirchi ka salan and raita.", BigDecimal.valueOf(340.00), "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=600&q=80", false, true, true);
        MenuItem m7 = new MenuItem(r2.getId(), "Paneer Dum Biryani (Clay Handi)", "Biryani", "Dum cooked spiced basmati rice with marinated paneer cubes, mint, coriander, and caramelized shallots.", BigDecimal.valueOf(280.00), "https://images.unsplash.com/photo-1633945274405-b6c8069047b0?auto=format&fit=crop&w=600&q=80", true, false, true);
        MenuItem m8 = new MenuItem(r2.getId(), "Andhra Guntur Chicken 65", "Starters", "Crisp fried boneless chicken tossed with fiery Guntur red chilies, curry leaves, and ginger-garlic.", BigDecimal.valueOf(260.00), "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?auto=format&fit=crop&w=600&q=80", false, true, true);
        MenuItem m9 = new MenuItem(r2.getId(), "Double Ka Meetha", "Desserts", "Royal Hyderabadi bread pudding soaked in saffron cardamom reduced milk, topped with toasted cashews.", BigDecimal.valueOf(110.00), "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?auto=format&fit=crop&w=600&q=80", true, false, true);

        // Sri Udupi Krishna Grand (Pure Veg)
        MenuItem m10 = new MenuItem(r3.getId(), "Crisp Ghee Roast Masala Dosa", "Tiffins", "Golden crisp fermented crepe roasted in pure desi ghee, stuffed with tempered spiced potato bhaji, served with 3 chutneys & sambar.", BigDecimal.valueOf(110.00), "https://images.unsplash.com/photo-1630383249896-424e482df921?auto=format&fit=crop&w=600&q=80", true, false, true);
        MenuItem m11 = new MenuItem(r3.getId(), "Steamed Idli & Medu Vada Combo", "Tiffins", "Two fluffy steamed rice cakes and one crunchy lentil doughnut, served with piping hot Udupi drumstick sambar.", BigDecimal.valueOf(85.00), "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&w=600&q=80", true, false, true);
        MenuItem m12 = new MenuItem(r3.getId(), "Bisi Bele Bath with Boondi", "Mains", "Authentic Karnataka spiced rice and lentil porridge cooked with vegetables, tamarind, and desi ghee, topped with crispy boondi.", BigDecimal.valueOf(95.00), "https://images.unsplash.com/photo-1546833999-b9f581a1996d?auto=format&fit=crop&w=600&q=80", true, false, true);
        MenuItem m13 = new MenuItem(r3.getId(), "Degree Filter Coffee (Brass Dabara)", "Beverages", "Freshly brewed Chicory-blended South Indian filter kaapi frothed with whole boiled milk.", BigDecimal.valueOf(45.00), "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=600&q=80", true, false, true);

        // Mumbai Tadka Street Bites
        MenuItem m14 = new MenuItem(r4.getId(), "Amul Butter Pav Bhaji Platter", "Street Food", "Spiced mashed vegetable curry slow-simmered with pav bhaji masala, served with 2 butter-toasted ladi pav, onion rings, and lemon.", BigDecimal.valueOf(130.00), "https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=600&q=80", true, true, true);
        MenuItem m15 = new MenuItem(r4.getId(), "Mumbai Vada Pav Duo with Fried Mirchi", "Street Food", "Two golden spiced potato batata vadas tucked in fresh pav with dry garlic coconut chutney and salted green chilies.", BigDecimal.valueOf(60.00), "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?auto=format&fit=crop&w=600&q=80", true, true, true);
        MenuItem m16 = new MenuItem(r4.getId(), "Dahi Puri & Sev Papdi Chaat", "Chaat", "Crispy puffed puris filled with boiled potatoes and sprouted moong, smothered in sweet yogurt, tamarind chutney, and fine sev.", BigDecimal.valueOf(95.00), "https://images.unsplash.com/photo-1584278860047-22db9ff82bed?auto=format&fit=crop&w=600&q=80", true, false, true);
        MenuItem m17 = new MenuItem(r4.getId(), "Cutting Masala Chai Flask (4 Servings)", "Beverages", "Strong Assam CTC tea boiled with crushed cardamom, ginger, cloves, and full-cream milk.", BigDecimal.valueOf(80.00), "https://images.unsplash.com/photo-1576092768241-dec231879fc3?auto=format&fit=crop&w=600&q=80", true, false, true);

        // Coastal Karavalli
        MenuItem m18 = new MenuItem(r5.getId(), "Malabar Flaky Parotta with Chicken Sukka", "Mains", "Layered crisp Kerala flatbread served with dry roasted coastal coconut chicken sukka.", BigDecimal.valueOf(310.00), "https://images.unsplash.com/photo-1552611052-33e04de081de?auto=format&fit=crop&w=600&q=80", false, true, true);
        MenuItem m19 = new MenuItem(r5.getId(), "Alleppey Raw Mango Fish Curry", "Mains", "Fresh seer fish steak gently cooked in mild coconut milk broth with raw green mango slices and tempered curry leaves.", BigDecimal.valueOf(390.00), "https://images.unsplash.com/photo-1617196034796-73dfa7b1fd56?auto=format&fit=crop&w=600&q=80", false, false, true);
        MenuItem m20 = new MenuItem(r5.getId(), "Ghee Rice with Kerala Veg Kurma", "Mains", "Fragrant Jeerakasala rice tempered with whole spices, served with mixed vegetable and cashew coconut gravy.", BigDecimal.valueOf(210.00), "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=600&q=80", true, false, true);

        // Bengal Sweet & Kathi Rolls
        MenuItem m21 = new MenuItem(r6.getId(), "Kolkata Double Egg Chicken Kathi Roll", "Rolls", "Crisp flaky lachha paratha layered with beaten eggs, spiced chicken skewers, sliced onions, and green chili lime seasoning.", BigDecimal.valueOf(150.00), "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?auto=format&fit=crop&w=600&q=80", false, true, true);
        MenuItem m22 = new MenuItem(r6.getId(), "Paneer Tikka Kathi Roll", "Rolls", "Marinated tandoori paneer wrapped in flaky griddled paratha with mint chutney and crunchy capsicum.", BigDecimal.valueOf(130.00), "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?auto=format&fit=crop&w=600&q=80", true, false, true);
        MenuItem m23 = new MenuItem(r6.getId(), "Kolkata Spongy Rasgulla (4 pcs)", "Sweets", "Traditional cottage cheese spheres poached in fragrant light sugar syrup, served warm.", BigDecimal.valueOf(90.00), "https://images.unsplash.com/photo-1505394033641-40c6ad1178d7?auto=format&fit=crop&w=600&q=80", true, false, true);

        menuItemRepository.saveAll(List.of(
                m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12,
                m13, m14, m15, m16, m17, m18, m19, m20, m21, m22, m23
        ));

        // 5. Seed Favorites for demo user
        favoriteRepository.save(new Favorite(user.getId(), r1.getId()));
        favoriteRepository.save(new Favorite(user.getId(), r3.getId()));

        // 6. Seed a Delivered Past Order (with 5-Star Review in INR)
        Order pastOrder = new Order(
                user.getId(),
                r1.getId(),
                r1.getName(),
                BigDecimal.valueOf(470.00),
                r1.getDeliveryFee(),
                BigDecimal.valueOf(23.50), // 5% GST on Restaurant Food
                BigDecimal.valueOf(528.50),
                "Flat 402, Shanti Niketan Apts, 12th Main, Indiranagar, Bengaluru - 560038",
                "Leave with security guard if door is locked.",
                "UPI",
                "SUCCESS",
                "TXN-UPI99210"
        );
        pastOrder.setStatus(OrderStatus.DELIVERED);
        pastOrder.setCreatedAt(LocalDateTime.now().minusDays(1));
        pastOrder = orderRepository.save(pastOrder);

        orderItemRepository.save(new OrderItem(pastOrder.getId(), m2.getId(), m2.getName(), m2.getPrice(), 1, m2.getPrice()));
        orderItemRepository.save(new OrderItem(pastOrder.getId(), m4.getId(), m4.getName(), m4.getPrice(), 1, m4.getPrice()));

        paymentRepository.save(new PaymentTransaction(pastOrder.getId(), user.getId(), pastOrder.getTotalAmount(), "UPI", "TXN-UPI99210", "SUCCESS"));

        OrderTracking pastTracking = new OrderTracking(
                pastOrder.getId(),
                OrderStatus.DELIVERED,
                0,
                "Ramesh Kumar",
                "+91 98450 12345",
                "Hero Splendor Plus (#KA-03-EX-9921)",
                4.95
        );
        pastTracking.setCurrentStep(4);
        pastTracking.setStatusMessage("Delivered successfully! Handed over to Aarav at Indiranagar.");
        trackingRepository.save(pastTracking);

        Review review = new Review(
                pastOrder.getId(),
                user.getId(),
                user.getName(),
                r1.getId(),
                5,
                "The Butter Chicken and Garlic Naan were out of the world! Truly authentic Punjabi taste right here in Bengaluru. Delivery was under 22 minutes!"
        );
        reviewRepository.save(review);

        // 7. Seed an Active In-Progress Order for real-time tracking (UC-5)
        Order activeOrder = new Order(
                user.getId(),
                r2.getId(),
                r2.getName(),
                BigDecimal.valueOf(600.00),
                r2.getDeliveryFee(),
                BigDecimal.valueOf(30.00),
                BigDecimal.valueOf(670.00),
                "Flat 402, Shanti Niketan Apts, 12th Main, Indiranagar, Bengaluru - 560038",
                "Call on mobile upon reaching building gate.",
                "UPI",
                "SUCCESS",
                "TXN-UPI84920"
        );
        activeOrder.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        activeOrder.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        activeOrder = orderRepository.save(activeOrder);

        orderItemRepository.save(new OrderItem(activeOrder.getId(), m6.getId(), m6.getName(), m6.getPrice(), 1, m6.getPrice()));
        orderItemRepository.save(new OrderItem(activeOrder.getId(), m8.getId(), m8.getName(), m8.getPrice(), 1, m8.getPrice()));

        paymentRepository.save(new PaymentTransaction(activeOrder.getId(), user.getId(), activeOrder.getTotalAmount(), "UPI", "TXN-UPI84920", "SUCCESS"));

        OrderTracking activeTracking = new OrderTracking(
                activeOrder.getId(),
                OrderStatus.OUT_FOR_DELIVERY,
                8,
                "Suresh Gowda",
                "+91 97410 88291",
                "TVS Apache RTR 160 (#KA-01-MJ-4412)",
                4.92
        );
        activeTracking.setCurrentStep(3);
        activeTracking.setStatusMessage("Delivery partner Suresh picked up your biryani! En route via 100 Feet Road, Indiranagar.");
        activeTracking.setDriverLatitude(12.9716);
        activeTracking.setDriverLongitude(77.5946);
        trackingRepository.save(activeTracking);

        // 8. Seed Indian Support Tickets (UC-10)
        SupportTicket t1 = new SupportTicket(
                "TCK-55219",
                user.getId(),
                pastOrder.getId(),
                "FOOD_QUALITY",
                "The Dal Makhani had extra butter garnish, absolutely loved it! Please convey compliments to the chef."
        );
        t1.setStatus("RESOLVED");
        t1.setResolutionNotes("Chef acknowledged with thanks. Added ₹50 SwadExpress loyalty points to your account.");
        supportTicketRepository.save(t1);

        SupportTicket t2 = new SupportTicket(
                "TCK-88102",
                user.getId(),
                activeOrder.getId(),
                "LATE_DELIVERY",
                "Can the delivery partner carry extra mint raita with the Hyderabadi Biryani?"
        );
        t2.setStatus("OPEN");
        t2.setResolutionNotes("Support contacted restaurant kitchen. Additional raita pouch packed.");
        supportTicketRepository.save(t2);
    }
}
