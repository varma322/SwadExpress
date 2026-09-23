# Business Requirements Document (BRD)
## Project Title: Food Delivery Microservices System

**Document Version:** 1.0.0  
**Status:** Approved for Implementation  
**Architecture Paradigm:** Distributed Microservices Architecture  
**Target Backend Stack:** Java Spring Boot 4.x / Spring Cloud / RESTful APIs / JPA  
**Target Frontend Stack:** Modern Responsive Web Application (HTML5 / Vanilla CSS / JavaScript ES2024)

---

## 1. Executive Summary & Introduction

The **Food Delivery Microservices System** is an enterprise-grade, distributed digital platform designed to connect consumers with local restaurants and independent delivery couriers. The platform delivers a seamless digital dining experience featuring instantaneous restaurant discovery, interactive visual menus, secure cart and checkout transactions, real-time live GPS/status order tracking, multi-criteria customer feedback, and multi-channel customer care.

By decomposing legacy monolithic architectures into decoupled, independently deployable microservices, the system guarantees high elasticity, fault isolation, continuous deployment capabilities, and horizontal scalability during peak dining hours.

---

## 2. Business & Strategic Objectives

| ID | Strategic Objective | Business Impact & Success Metric |
| :--- | :--- | :--- |
| **OBJ-01** | **Intuitive Customer Discovery** | Frictionless browsing, multi-criteria search (cuisine, location, rating), sub-100ms query retrieval. |
| **OBJ-02** | **Merchant Empowerment** | Streamlined menu management, real-time item availability toggles, automated order dispatch. |
| **OBJ-03** | **Transparent Delivery Tracking** | Real-time state progression (Placed $\rightarrow$ Preparing $\rightarrow$ In Transit $\rightarrow$ Delivered) and driver details. |
| **OBJ-04** | **Financial Integrity & Trust** | PCI-DSS compliant checkout, simulated and card payment rails, itemized invoices with delivery fees and taxes. |
| **OBJ-05** | **Customer Retention & Loyalty** | Instant 1-click favorites, saved multi-address management, and automated post-delivery review loops. |
| **OBJ-06** | **Responsive Incident Resolution** | Dedicated support ticket creation, order-linked issue reporting, and prompt dispute acknowledgment. |

---

## 3. System Scope & Stakeholders (Actors)

### 3.1 System Actors
1. **Customer (End-User)**: Discovers restaurants, configures food baskets, executes payments, monitors fulfillment, manages account settings, and rates orders.
2. **Restaurant Partner / Kitchen Staff**: Receives order events, acknowledges prep milestones, and updates item stock levels.
3. **Delivery Partner / Driver**: Accepts delivery assignments, updates transit telemetry, and marks final drop-off.
4. **Support Representative**: Resolves escalations, handles refund requests, and responds to customer tickets.
5. **System Automated Services**: Microservices communicating via REST and asynchronous event buses.

---

## 4. Comprehensive Functional Requirements (10 Core Use Cases)

### Use Case 1: User Registration & Authentication
- **Service Owner**: `User & Registration Service`
- **Actors**: Prospective Customer, Registration Service
- **Preconditions**: The user supplies a unique email address, valid mobile number, and secure password.
- **Postconditions**: An account entity is provisioned in the user datastore, a profile is initialized, and an authentication session token is issued.
- **Main Workflow**:
  1. User navigates to the Registration / Sign-Up view.
  2. User inputs full name, email, phone number, and password.
  3. Registration Service validates field constraints and confirms non-duplication of email/phone.
  4. Password is cryptographic salted and hashed (BCrypt).
  5. User profile is persisted; welcoming notification/SMS event is dispatched.
  6. Secure JWT session token is returned to the client.
- **Alternative Flows**:
  - *4a. Duplicate Email/Phone*: Service rejects registration with HTTP 409 Conflict and descriptive message.
  - *4b. Validation Failure*: Service returns HTTP 400 Bad Request with field-specific violations.

---

### Use Case 2: Restaurant Search & Discovery
- **Service Owner**: `Restaurant Search Service`
- **Actors**: Customer (Authenticated or Guest), Search Service
- **Preconditions**: Restaurant directory is indexed with operational status, coordinates, and cuisine tags.
- **Postconditions**: Customer receives a filtered, ranked collection of restaurant summaries.
- **Main Workflow**:
  1. Customer accesses the discovery portal and enters search parameters (keyword, location, cuisine like Italian, Japanese, Mexican, Indian, American).
  2. Client applies optional sort criteria (Top Rated, Fastest Delivery, Price Range).
  3. Restaurant Search Service queries the restaurant repository applying filters and geo-bounding.
  4. Service returns rich restaurant cards with banner images, ratings, ETA minutes, and minimum order values.
  5. Customer reviews results and selects a restaurant.

---

### Use Case 3: Interactive Menu Exploration
- **Service Owner**: `Menu Service`
- **Actors**: Customer, Menu Service
- **Preconditions**: Selected restaurant is operational.
- **Postconditions**: Structured catalog of categories and food items is rendered with real-time pricing and dietary badges.
- **Main Workflow**:
  1. Customer selects a restaurant from search results.
  2. Menu Service fetches hierarchical menu categories (Starters, Mains, Desserts, Beverages) and items.
  3. Items display titles, descriptions, calorie/allergen info, price, vegetarian/vegan/spicy tags, and imagery.
  4. Customer filters items (e.g., Vegetarian Only, Chef's Specials).
  5. Customer selects item quantities, adds customization notes, and stages items in the active cart.

---

### Use Case 4: Cart Checkout & Order Placement
- **Service Owner**: `Order Service`, `Payment Service`
- **Actors**: Customer, Order Service, Payment Service
- **Preconditions**: Cart contains at least one active item meeting restaurant minimum order value.
- **Postconditions**: An immutable `ORDER_PLACED` record is generated, payment transaction is settled, and notification is dispatched.
- **Main Workflow**:
  1. Customer inspects cart items, quantities, subtotal, delivery charges, and taxes.
  2. Customer selects a verified delivery address and enters delivery instructions.
  3. Customer selects payment method (Credit/Debit Card, UPI, Digital Wallet, Cash on Delivery).
  4. Payment Service processes payment verification and generates an authorized transaction ID.
  5. Order Service transitions order status to `CONFIRMED`, allocates unique Order ID, and dispatches confirmation to customer and kitchen.

---

### Use Case 5: Real-Time Order Tracking
- **Service Owner**: `Tracking Service`
- **Actors**: Customer, Courier, Kitchen Dispatcher, Tracking Service
- **Preconditions**: Order has been placed and possesses an active fulfillment lifecycle.
- **Postconditions**: Customer views real-time status progression, estimated arrival countdown, and assigned courier details.
- **Main Workflow**:
  1. Customer navigates to "Active Orders" or follows post-checkout redirect.
  2. Tracking Service provides the current state in the state machine:
     - `PLACED`: Order received by platform.
     - `PREPARING`: Restaurant kitchen is cooking.
     - `OUT_FOR_DELIVERY`: Courier picked up package and is en route.
     - `DELIVERED`: Order safely dropped off.
  3. Service supplies courier details: Driver Name, Contact Number, Vehicle Type, and ETA countdown.
  4. Real-time updates push automatically to client view without manual page reloads.

---

### Use Case 6: Review & Rating Submission
- **Service Owner**: `Review Service`
- **Actors**: Customer, Review Service
- **Preconditions**: Order status is marked `DELIVERED` and has not been previously reviewed by this user.
- **Postconditions**: Feedback record is stored; restaurant aggregate score and total review count are dynamically recalculated.
- **Main Workflow**:
  1. Customer navigates to order history and selects a delivered order.
  2. Customer rates restaurant and food quality (1 to 5 stars) and enters review commentary.
  3. Review Service validates rating bounds and prevents duplicate submissions.
  4. Service persists review and triggers recalculation of restaurant's weighted average rating.
  5. UI displays updated customer rating and confirms submission.

---

### Use Case 7: Account & Profile Management
- **Service Owner**: `Account Management Service`
- **Actors**: Customer, Account Service
- **Preconditions**: User is authenticated with a valid session token.
- **Postconditions**: User profile attributes (Name, Email, Phone, Password) are updated in system storage.
- **Main Workflow**:
  1. Customer navigates to Profile / Account Settings.
  2. Service loads current profile data.
  3. Customer modifies fields (e.g., changes display name or updates phone number).
  4. Service validates fields, updates database, and returns updated entity with confirmation prompt.

---

### Use Case 8: Save & Manage Favorite Restaurants
- **Service Owner**: `Favorites Service`
- **Actors**: Customer, Favorites Service
- **Preconditions**: User is authenticated.
- **Postconditions**: Restaurant association is added to or removed from the user's favorites collection.
- **Main Workflow**:
  1. Customer views restaurant card or menu page and clicks "Favorite" heart icon.
  2. Favorites Service toggles association for `(userId, restaurantId)`.
  3. Customer accesses "Saved Favorites" tab in navigation to instantly view curated restaurants.
  4. Customer can 1-click navigate back to favorite menus.

---

### Use Case 9: Delivery Address Management
- **Service Owner**: `Address Management Service`
- **Actors**: Customer, Address Management Service
- **Preconditions**: User is authenticated.
- **Postconditions**: Delivery addresses (Home, Work, Other) are added, updated, or marked default.
- **Main Workflow**:
  1. Customer visits Address Book in Account Settings or during checkout.
  2. Customer submits street address, apartment/suite, city, state, postal code, and label tag.
  3. Service validates postal code format and stores record linked to user profile.
  4. Customer can switch active/default delivery address for 1-click checkout.

---

### Use Case 10: Customer Support & Dispute Resolution
- **Service Owner**: `Customer Support Service`
- **Actors**: Customer, Support Agent, Support Service
- **Preconditions**: User is logged in or provides active Order ID.
- **Postconditions**: Support ticket is registered with priority code, acknowledged via confirmation ID, and queued for resolution.
- **Main Workflow**:
  1. Customer accesses Support portal.
  2. Customer selects category (Late Delivery, Missing Item, Food Quality, Billing/Refund, General Inquiry).
  3. Customer inputs description and optional order reference.
  4. Support Service issues a tracked Ticket Reference ID (e.g., `TCK-10042`) and logs ticket in database.
  5. Customer views ticket status (`OPEN`, `INVESTIGATING`, `RESOLVED`).

---

## 5. Non-Functional Requirements (NFRs)

### 5.1 Performance & Latency
- **NFR-P1**: API Gateway response latency must not exceed **200ms** at the 95th percentile under standard operating loads.
- **NFR-P2**: Restaurant search queries must return within **100ms** through indexed queries and catalog caching.
- **NFR-P3**: Database connections managed via connection pool (HikariCP) sized for high concurrent throughput.

### 5.2 Scalability & Elasticity
- **NFR-S1**: All microservice backend components must remain completely stateless to facilitate seamless horizontal replication.
- **NFR-S2**: Persistence layers must support read-replicas for catalog browsing and write-primary for order transactions.

### 5.3 Reliability, Availability & Fault Tolerance
- **NFR-R1**: 99.9% system availability target.
- **NFR-R2**: Isolation of service failures (e.g., if Review or Support Service experiences degradation, core checkout and order tracking remain unaffected).
- **NFR-R3**: Database transactions in Order & Payment services wrapped in ACID-compliant transactional boundaries (`@Transactional`).

### 5.4 Security & Regulatory Compliance
- **NFR-SEC1**: Secure credential storage using BCrypt one-way password hashing with work factor $\ge 10$.
- **NFR-SEC2**: API endpoints protected against common vulnerabilities (SQL injection via parameterized JPA queries, XSS sanitization, CORS policy controls).
- **NFR-SEC3**: Sensitive payment tokens decoupled from raw credit card numbers to maintain PCI-DSS alignment.

### 5.5 Observability & Maintainability
- **NFR-O1**: Centralized REST exception handling (`@RestControllerAdvice`) delivering consistent RFC 7807 problem details.
- **NFR-O2**: Interactive Swagger / OpenAPI documentation auto-generated and accessible at `/swagger-ui.html`.
- **NFR-O3**: Actuator health check endpoints exposed at `/actuator/health`.

---

## 6. Microservices Traceability Matrix

| Use Case ID | Feature / Action | Microservice Name | Primary REST Endpoint |
| :--- | :--- | :--- | :--- |
| **UC-01** | User Registration & Auth | `User & Registration Service` | `POST /api/v1/users/register`, `POST /api/v1/users/login` |
| **UC-02** | Restaurant Search | `Restaurant Search Service` | `GET /api/v1/restaurants/search` |
| **UC-03** | Menu Inspection | `Menu Service` | `GET /api/v1/restaurants/{id}/menu` |
| **UC-04** | Order Placement & Pay | `Order Service` / `Payment Service`| `POST /api/v1/orders`, `POST /api/v1/payments/process` |
| **UC-05** | Real-Time Order Tracking | `Tracking Service` | `GET /api/v1/tracking/{orderId}` |
| **UC-06** | Ratings & Reviews | `Review Service` | `POST /api/v1/reviews`, `GET /api/v1/reviews/restaurant/{id}` |
| **UC-07** | Account Management | `Account Service` | `GET /api/v1/users/profile`, `PUT /api/v1/users/profile` |
| **UC-08** | Favorite Restaurants | `Favorites Service` | `GET /api/v1/favorites`, `POST /api/v1/favorites/toggle` |
| **UC-09** | Delivery Address Book | `Address Service` | `GET /api/v1/addresses`, `POST /api/v1/addresses` |
| **UC-10** | Customer Care Tickets | `Support Service` | `POST /api/v1/support/tickets`, `GET /api/v1/support/tickets` |

---

## 7. Conclusion & Next Steps

This Business Requirements Document establishes the definitive operational baseline and functional scope for the Food Delivery Microservices platform. By combining Spring Boot's modular architecture with a modern, high-aesthetic web interface, the system satisfies all enterprise delivery requirements with robustness, performance, and maintainability.
