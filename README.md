# ⚡ QuickBite Express - Food Delivery Microservices Platform

An enterprise-grade, distributed food delivery microservices platform implemented with **Java Spring Boot 4.x**, **Spring Data JPA**, **H2 In-Memory Database**, and a high-aesthetic **Modern Web Application Frontend**.

Built in accordance with the Business Requirements Document ([`BRD.md`](file:///c:/Users/ARUN/varma_scripts/experiments/demo/BRD.md)) and System Architecture Document ([`ARCHITECTURE.md`](file:///c:/Users/ARUN/varma_scripts/experiments/demo/ARCHITECTURE.md)).

---

## 🌟 10 Core Use Cases Implemented

| Use Case | Title | Microservice Context | Description |
| :---: | :--- | :--- | :--- |
| **UC-1** | **User Registration & Login** | `UserService` | User sign-up with duplicate detection, BCrypt hashing, and session authentication. |
| **UC-2** | **Restaurant Search & Filter** | `RestaurantService` | Multi-criteria search by keyword, cuisine (Italian, Japanese, Burgers, etc.), and sort options. |
| **UC-3** | **Interactive View Menu** | `RestaurantService` | Category-based dish exploration (Starters, Mains, Desserts, Drinks) with dietary badges. |
| **UC-4** | **Place Order & Payment** | `OrderService` + `PaymentService` | Cart checkout, subtotal/tax/delivery calculation, and automated payment transaction settlement. |
| **UC-5** | **Real-Time Order Tracking** | `TrackingService` | 4-stage lifecycle progression (`PLACED` $\rightarrow$ `PREPARING` $\rightarrow$ `IN TRANSIT` $\rightarrow$ `DELIVERED`), driver contact, and GPS route simulator. |
| **UC-6** | **Review & Rate Order** | `ReviewService` | 5-star customer feedback on delivered orders with automatic recalculation of restaurant rating. |
| **UC-7** | **Manage Account** | `UserService` | Profile editing, mobile phone updates, and avatar image customization. |
| **UC-8** | **Save Favorite Restaurants** | `FavoriteService` | 1-click toggle of favorite restaurants with persistent state and dedicated favorites filter. |
| **UC-9** | **Delivery Address Book** | `AddressService` | Multi-address management (Home, Work, Other) with default address selector. |
| **UC-10** | **Customer Support Request** | `SupportService` | Order-linked ticket creation (`TCK-XXXXX`), status workflow (`OPEN`, `INVESTIGATING`, `RESOLVED`). |

---

## 🏗️ Architecture & Technology Stack

- **Backend Runtime**: Java 21 / 26
- **Framework**: Spring Boot `4.1.1` (WebMVC, Data JPA, Validation)
- **Database**: H2 In-Memory Database (`jdbc:h2:mem:fooddeliverydb`) with web console at `/h2-console`
- **Frontend**: Vanilla ES2024, Modern CSS Design System (Glassmorphism, Dark Obsidian aesthetic, Plus Jakarta Sans)
- **Containerization**: Multi-stage `Dockerfile` and `docker-compose.yml`

---

## 🚀 How to Run

### Prerequisites
- **Java**: JDK 21 or newer (Java 21 to Java 26 supported)
- **Maven**: Included via Maven Wrapper (`mvnw.cmd` on Windows, `./mvnw` on Linux/macOS)
- **Browser**: Chrome, Edge, Firefox, or Safari

### Option 1: Native Spring Boot (Recommended for Local Dev)
Run using the included Maven Wrapper from the `backend/` directory:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

*Or package and run the executable JAR:*
```powershell
cd backend
.\mvnw.cmd package -DskipTests
java -jar target\food-delivery-backend-0.0.1-SNAPSHOT.jar
```

Once started:
- **Web Application**: [http://localhost:8085/](http://localhost:8085/)
- **Swagger / OpenAPI 3.0 Documentation**: [http://localhost:8085/swagger.html](http://localhost:8085/swagger.html)
- **OpenAPI JSON Specification**: [http://localhost:8085/v3/api-docs](http://localhost:8085/v3/api-docs)
- **H2 In-Memory Database Console**: [http://localhost:8085/h2-console](http://localhost:8085/h2-console)
  - **JDBC URL**: `jdbc:h2:mem:fooddeliverydb`
  - **User**: `sa`
  - **Password**: *(leave blank)*
- **Actuator Health & Metrics**: [http://localhost:8085/actuator/health](http://localhost:8085/actuator/health)

### 🔑 Pre-Seeded Demo Account
The database is pre-seeded on startup with Indian culinary restaurants (Meghana Foods, Empire Restaurant, Vidyarthi Bhavan, Nagarjuna, etc.) and a demo user:
- **Email**: `aarav.sharma@example.in`
- **Password**: `password123`
*(You can also register a new account anytime via the UI.)*

### Option 2: Docker Compose
```powershell
docker-compose up --build
```

### Option 3: Run Automated Unit & Integration Tests
```powershell
cd backend
.\mvnw.cmd test
```

All 10 use cases are covered by automated unit and integration tests in [`FoodDeliveryBackendApplicationTests.java`](file:///c:/Users/ARUN/varma_scripts/experiments/demo/backend/src/test/java/com/fooddelivery/FoodDeliveryBackendApplicationTests.java).

---

## 📋 REST API Endpoints Overview

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/users/register` | Register new user account |
| `POST` | `/api/v1/users/login` | Authenticate customer |
| `GET` | `/api/v1/users/{id}` | Get customer profile |
| `PUT` | `/api/v1/users/{id}` | Update profile details |
| `GET` | `/api/v1/users/{userId}/addresses` | List saved addresses |
| `POST` | `/api/v1/users/{userId}/addresses` | Add new delivery address |
| `GET` | `/api/v1/restaurants` | Search & filter restaurants |
| `GET` | `/api/v1/restaurants/{id}/menu` | Retrieve categorized restaurant menu |
| `POST` | `/api/v1/favorites/toggle` | Toggle favorite restaurant |
| `POST` | `/api/v1/orders` | Place order and settle payment |
| `GET` | `/api/v1/orders/user/{userId}` | Get user order history |
| `GET` | `/api/v1/tracking/{orderId}` | Fetch real-time delivery telemetry |
| `POST` | `/api/v1/tracking/{orderId}/advance-step`| Advance delivery milestone (simulation) |
| `POST` | `/api/v1/reviews` | Submit order rating and review |
| `POST` | `/api/v1/support/tickets` | Submit customer support ticket |
| `GET` | `/api/v1/support/tickets/user/{userId}` | List user support tickets |
