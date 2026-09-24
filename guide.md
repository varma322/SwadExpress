# 🍛 SwadExpress — Complete Project Execution & Developer Guide

An enterprise-grade, distributed food delivery microservices platform implemented with **Java Spring Boot 4.1.1**, **Spring Data JPA**, **H2 In-Memory Database**, and a responsive frontend with interactive Leaflet GPS live tracking.

Built in accordance with the Business Requirements Document (`BRD.md`) and System Architecture Blueprint (`ARCHITECTURE.md`), fully implementing **all 10 Capstone Functional Use Cases (UC-1 to UC-10)** and **Non-Functional Requirements (NFRs)**.

---

## 📌 Table of Contents
1. [Prerequisites](#1-prerequisites)
2. [Project Architecture & File Layout](#2-project-architecture--file-layout)
3. [Running the Application (3 Methods)](#3-running-the-application)
   - [Method 1: Maven Wrapper (Recommended for Local Dev)](#method-1-maven-wrapper-recommended)
   - [Method 2: Standalone Executable JAR](#method-2-standalone-executable-jar)
   - [Method 3: Docker Compose](#method-3-docker-compose)
4. [Accessing Running Services & Endpoints](#4-accessing-running-services--endpoints)
5. [Pre-Seeded Demo Credentials & Database](#5-pre-seeded-demo-credentials--database)
6. [10 Core Use Cases Walkthrough](#6-10-core-use-cases-walkthrough)
7. [Running Automated Integration & Unit Tests](#7-running-automated-integration--unit-tests)
8. [Frontend Development & Resource Sync](#8-frontend-development--resource-sync)
9. [Troubleshooting & FAQ](#9-troubleshooting--faq)

---

## 1. Prerequisites

Ensure your development environment meets the following specifications:

| Requirement | Supported Versions | Verification Command | Notes |
| :--- | :--- | :--- | :--- |
| **Java JDK** | **JDK 21** or newer (Java 21 to 26) | `java -version` | Required to run Spring Boot 4.x |
| **Maven** | Included in project (`mvnw.cmd` / `./mvnw`) | `.\mvnw.cmd -v` | No separate installation needed |
| **Web Browser** | Chrome, Edge, Firefox, Brave, Safari | — | Any modern HTML5/ES2024 browser |
| **Docker (Optional)** | Docker Desktop 20+ | `docker --version` | Only required if running via container |

---

## 2. Project Architecture & File Layout

```text
demo/
├── backend/                                # Spring Boot 4 REST Backend
│   ├── src/main/java/com/fooddelivery/    # Java Microservices Domain Packages
│   │   ├── common/                         # Docs, Global Exception Handling, Actuator
│   │   │   ├── docs/OpenApiController.java # OpenAPI 3.0 specification endpoint
│   │   │   └── exception/                  # Standardized JSON error response handler
│   │   ├── data/DataSeeder.java            # Startup seed data (User, Addresses, Menus)
│   │   ├── order/                          # Order & payment settlement (UC-4)
│   │   ├── restaurant/                     # Search, menus, favorites (UC-2, UC-3, UC-8)
│   │   ├── review/                         # Customer reviews & ratings (UC-6)
│   │   ├── support/                        # Customer support tickets (UC-10)
│   │   ├── tracking/                       # Telemetry & GPS milestone simulator (UC-5)
│   │   └── user/                           # Users, authentication, addresses (UC-1, UC-7, UC-9)
│   ├── src/main/resources/
│   │   ├── application.properties          # Server port (8085), H2, Actuator config
│   │   └── static/                         # Production frontend assets served by Spring
│   │       ├── app.js                      # Frontend application logic & API client
│   │       ├── index.html                  # Single-page food delivery application
│   │       ├── style.css                   # Glassmorphism & saffron design system
│   │       └── swagger.html                # Embedded Swagger UI client
│   ├── mvnw.cmd / mvnw                     # Maven Wrapper for Windows / Linux
│   └── pom.xml                             # Dependencies (Spring Boot 4, H2, Validation)
├── frontend/                               # Source frontend workspace (HTML, CSS, JS)
│   ├── index.html                          # Main UI interface
│   ├── style.css                           # Design tokens, modal, drawer, responsive styles
│   ├── app.js                              # REST API client with intelligent port detection
│   └── swagger.html                        # Swagger UI bundle
├── docker-compose.yml                      # Containerized deployment definition
├── BRD.md                                  # Business Requirements Document (10 Use Cases)
├── ARCHITECTURE.md                         # Architecture Blueprint & Data Models
├── README.md                               # Quick-start summary
└── guide.md                                # This complete operational guide
```

---

## 3. Running the Application

### Method 1: Maven Wrapper (Recommended)

Open a terminal (PowerShell, Command Prompt, or Bash) in the project directory:

```powershell
# 1. Enter the backend directory
cd backend

# 2. Run using the Maven wrapper
.\mvnw.cmd spring-boot:run     # On Windows (PowerShell / CMD)
# or
./mvnw spring-boot:run        # On macOS / Linux
```

The Spring Boot backend will build, initialize the in-memory H2 database, run `DataSeeder`, and start Tomcat on port **8085** in ~4 seconds:
```text
Started FoodDeliveryBackendApplication in 3.85 seconds (process running for 4.134)
Tomcat started on port 8085 (http) with context path '/'
```

---

### Method 2: Standalone Executable JAR

To package and run an optimized production JAR:

```powershell
# 1. Navigate to backend directory
cd backend

# 2. Clean and package the JAR (skipping test execution for rapid build)
.\mvnw.cmd clean package -DskipTests

# 3. Launch the JAR
java -jar target\food-delivery-backend-0.0.1-SNAPSHOT.jar
```

To run in the background on Windows PowerShell:
```powershell
Start-Process java -ArgumentList "-jar target\food-delivery-backend-0.0.1-SNAPSHOT.jar" -WindowStyle Hidden
```

---

### Method 3: Docker Compose

If Docker Desktop is running:

```powershell
# Run from the root directory
docker-compose up --build
```

---

## 4. Accessing Running Services & Endpoints

Once the application has started, the following services are live:

| Service / Tool | URL | Description |
| :--- | :--- | :--- |
| **SwadExpress Web Application** | [http://localhost:8085/](http://localhost:8085/) | Full interactive customer interface |
| **Swagger / OpenAPI 3.0 Documentation** | [http://localhost:8085/swagger.html](http://localhost:8085/swagger.html) | Interactive UI to test all 10 microservices |
| **OpenAPI 3.0 JSON Schema** | [http://localhost:8085/v3/api-docs](http://localhost:8085/v3/api-docs) | Raw OpenAPI 3.0 specification |
| **H2 Database Web Console** | [http://localhost:8085/h2-console](http://localhost:8085/h2-console) | Web SQL GUI for live data queries |
| **Actuator Health Telemetry** | [http://localhost:8085/actuator/health](http://localhost:8085/actuator/health) | System health & operational status |
| **Actuator Metrics Endpoint** | [http://localhost:8085/actuator/metrics](http://localhost:8085/actuator/metrics) | JVM and HTTP request telemetry |

### Connecting to H2 Database Console
When opening [http://localhost:8085/h2-console](http://localhost:8085/h2-console), ensure these connection parameters are entered:
- **Driver Class**: `org.h2.Driver`
- **JDBC URL**: `jdbc:h2:mem:fooddeliverydb`
- **User Name**: `sa`
- **Password**: *(leave blank)*
- Click **Connect** to view and query tables: `USERS`, `ADDRESSES`, `RESTAURANTS`, `MENU_ITEMS`, `ORDERS`, `ORDER_ITEMS`, `ORDER_TRACKING`, `PAYMENTS`, `REVIEWS`, and `SUPPORT_TICKETS`.

---

## 5. Pre-Seeded Demo Credentials & Database

On every launch, `DataSeeder.java` populates the database with realistic demo data:

### Default Customer Account:
- **Email**: `aarav.sharma@example.in`
- **Password**: `password123`
- **Name**: `Aarav Sharma`
- **Phone**: `+91 98765 43210`
- **Pre-saved Addresses**:
  - `Home (Default)`: Flat 402, Shanti Niketan Apts, 12th Main HAL 2nd Stage, Indiranagar, Bengaluru 560038
  - `Office`: 5th Floor, Salarpuria Cyber Park, Electronic City Phase 1, Hosur Rd, Bengaluru 560100

*(You can also click **+ Register** in the navbar to create additional accounts.)*

### Seeded Bengaluru Restaurants & Menus:
1. **Meghana Foods & Royal Biryani** (Indiranagar) — Special Chicken Dum Biryani, Paneer Biryani, Chicken 65.
2. **Empire Restaurant** (Church Street) — Butter Chicken, Tandoori Roti, Empire Special Kebab.
3. **Vidyarthi Bhavan** (Gandhi Bazaar) — Benne Masala Dosa, Filter Coffee, Idli Vada.
4. **Nagarjuna Restaurant** (Residency Road) — Traditional Andhra Meals, Pepper Chicken.
5. **Truffles** (Koramangala) — All American Cheese Burger, Peri Peri Fries.
6. **Corner House Ice Cream** (Indiranagar) — Death By Chocolate (DBC), Hot Fudge Sundae.

---

## 6. 10 Core Use Cases Walkthrough

| Use Case | Title | How to Test in UI | CLI / REST Example |
| :---: | :--- | :--- | :--- |
| **UC-1** | **User Registration & Login** | Click user avatar in navbar $\rightarrow$ Sign Out $\rightarrow$ Click **Sign In** or **Register** $\rightarrow$ Submit form. | `POST /api/v1/users/login` |
| **UC-2** | **Restaurant Search & Filter** | Type in the search box ("Biryani", "Dosa") or click cuisine tags ("All", "Biryani", "South Indian", "North Indian"). | `GET /api/v1/restaurants?query=biryani` |
| **UC-3** | **Interactive View Menu** | Click **View Menu & Order** on any restaurant card $\rightarrow$ Switch category tabs (Starters, Mains, Desserts). | `GET /api/v1/restaurants/1/menu` |
| **UC-4** | **Place Order & Payment** | Click **+ ADD** on dishes $\rightarrow$ Click cart button $\rightarrow$ Select address $\rightarrow$ Pick payment method $\rightarrow$ Click **Confirm Order & Pay**. | `POST /api/v1/orders` |
| **UC-5** | **Real-Time GPS Order Tracking** | After placing an order, live GPS tracking modal opens. Click **Advance Order Milestone** to simulate lifecycle. | `GET /api/v1/tracking/{orderId}` |
| **UC-6** | **Review & Rate Order** | Once an order milestone reaches **Delivered**, click **Rate & Review** $\rightarrow$ Pick stars $\rightarrow$ Submit. | `POST /api/v1/reviews` |
| **UC-7** | **Manage Profile & Alerts** | Click user avatar $\rightarrow$ **Profile & Credentials** $\rightarrow$ Change phone/email/password $\rightarrow$ Click **Save Changes**. | `PUT /api/v1/users/1` |
| **UC-8** | **Favorite Restaurants** | Click the heart icon on any restaurant card $\rightarrow$ Click **Favorites** in the navigation bar to filter. | `POST /api/v1/favorites/toggle` |
| **UC-9** | **Delivery Address Book** | In cart drawer or profile modal, click **+ Add New Address** $\rightarrow$ Fill details $\rightarrow$ Save $\rightarrow$ Pre-selects in cart drawer. | `POST /api/v1/users/1/addresses` |
| **UC-10**| **Customer Support Tickets** | Click **Support** in top bar $\rightarrow$ Select order and category $\rightarrow$ Enter message $\rightarrow$ View created ticket badge. | `POST /api/v1/support/tickets` |

---

## 7. Running Automated Integration & Unit Tests

The repository includes a comprehensive JUnit 5 integration test suite covering all 10 use cases in isolated Spring contexts.

Run tests using the Maven wrapper:
```powershell
cd backend
.\mvnw.cmd test
```

### Test Results Output:
```text
[INFO] Running com.fooddelivery.FoodDeliveryBackendApplicationTests
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 5.720 s
[INFO] Results:
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 8. Frontend Development & Resource Sync

The project contains two frontend directories:
1. `backend/src/main/resources/static/`: Production files packaged inside the Spring Boot JAR and served on `http://localhost:8085/`.
2. `frontend/`: Source files for direct editing and local hot-reloading using tools like VS Code Live Server (`http://localhost:5500`).

### Smart Port Detection in JavaScript
[`app.js`](file:///c:/Users/ARUN/varma_scripts/experiments/demo/frontend/app.js#L7) automatically switches API base:
- When accessed via Spring Boot (`http://localhost:8085/`): Uses same-origin relative URLs (`''`).
- When accessed via a local dev server (port 5500, 3000, etc.): Automatically redirects all REST calls to `http://localhost:8085`.

### Synchronizing Frontend Changes
Whenever you edit files in `frontend/`, sync them to `backend/src/main/resources/static/` and repackage:
```powershell
# From project root:
Copy-Item frontend\* backend\src\main\resources\static\ -Force

# Rebuild backend JAR with updated static assets:
cd backend
.\mvnw.cmd package -DskipTests
```

---

## 9. Troubleshooting & FAQ

### 1. Port 8085 is Already in Use
If port 8085 is occupied by an earlier process:
```powershell
# Check which process is holding port 8085:
Get-NetTCPConnection -LocalPort 8085 | Select-Object OwningProcess

# Terminate that process:
Stop-Process -Id <PID> -Force
```
*Alternatively, change `server.port=8085` to another port in [backend/src/main/resources/application.properties](file:///c:/Users/ARUN/varma_scripts/experiments/demo/backend/src/main/resources/application.properties#L2).*

### 2. H2 Console Says "Database Not Found"
Make sure the **JDBC URL** is set to:
```text
jdbc:h2:mem:fooddeliverydb
```
*(Do not use the default `jdbc:h2:~/test` path).*

### 3. Browser Shows Cached Version of CSS or JS
If your browser does not display the latest styling or script changes, perform a hard cache refresh:
- **Windows / Linux**: `Ctrl + F5` or `Ctrl + Shift + R`
- **macOS**: `Cmd + Shift + R`
- Or open with a query parameter: `http://localhost:8085/?v=fresh`

### 4. Running Tests Fails Due to Java Agent Warning
On newer JDKs (Java 21 to 26), ByteBuddy and Mockito may display dynamic agent loading warnings. These do not affect functionality and all tests pass with exit code `0`.
