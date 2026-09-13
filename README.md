# Chit Fund Management — Backend

A full-featured backend for managing chit funds (kuries), built with Spring Boot 3.5 and PostgreSQL.

---

## Tech Stack

| Layer        | Technology                                      |
|--------------|-------------------------------------------------|
| Language     | Java 21                                         |
| Framework    | Spring Boot 3.5.15                              |
| Database     | PostgreSQL 16                                   |
| ORM          | Spring Data JPA (Hibernate)                     |
| Auth         | Spring Security + JWT (jjwt 0.11.5)             |
| Validation   | Jakarta Bean Validation                         |
| Utilities    | Lombok                                          |
| Container    | Docker (multi-stage build)                      |
| Orchestration| Docker Compose + Kubernetes (Helm charts)       |
| CI/CD        | GitHub Actions → GHCR → Render                  |

---

## Project Structure

```
chitfund-backend/
├── src/main/java/com/rahul/chitfund_backend/
│   ├── ChitfundBackendApplication.java        # Entry point
│   ├── controller/                            # REST API layer (11 controllers)
│   │   ├── AuthController.java                # POST /api/auth/login
│   │   ├── ChitGroupController.java           # CRUD /api/chits
│   │   ├── MemberController.java              # CRUD /api/members
│   │   ├── AuctionController.java             # /api/auctions
│   │   ├── MeetingController.java             # /api/meetings
│   │   ├── PaymentController.java             # /api/payments
│   │   ├── SettlementController.java          # /api/settlements
│   │   ├── OwnerMonthController.java          # /api/owner-month
│   │   ├── OwnerPaymentController.java        # /api/owner-payments
│   │   ├── DashboardController.java           # /api/dashboard
│   │   └── HealthController.java              # /api/health
│   ├── service/                               # Business logic (9 services)
│   ├── entity/                                # JPA entities (11 entities)
│   ├── repository/                            # Data access (8 repositories)
│   ├── dto/                                   # Request/Response DTOs (7 DTOs)
│   ├── security/                              # JWT auth + Security config
│   │   ├── SecurityConfig.java
│   │   ├── JwtUtil.java
│   │   └── JwtAuthFilter.java
│   └── exception/                             # Global error handling
│       ├── GlobalExceptionHandler.java
│       ├── CustomException.java
│       ├── ResourceNotFoundException.java
│       └── DuplicatePaymentException.java
├── src/main/resources/
│   ├── application.properties                 # Production config (env vars)
│   ├── application-local.properties           # Local dev config (Supabase)
│   ├── migration/                             # SQL migrations
│   ├── templates/                             # Server-side templates
│   └── static/                                # Static resources
├── Dockerfile                                 # Multi-stage Docker build
├── docker-compose.yml                         # App + Postgres + Frontend
├── pom.xml                                    # Maven config
├── k8s/                                       # Kubernetes manifests
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── ingress.yaml
│   └── postgres.yaml
├── helm-chart/                                # Helm chart for K8s
└── .github/workflows/                         # CI/CD pipeline
```

---

## API Endpoints

### Auth (Public)
| Method | Endpoint                | Description              |
|--------|-------------------------|--------------------------|
| POST   | `/api/auth/login`       | Login, returns JWT token |
| GET    | `/api/health`           | Health check             |

### Chit Groups (Auth required)
| Method | Endpoint                  | Description              |
|--------|---------------------------|--------------------------|
| POST   | `/api/chits`              | Create a chit group      |
| GET    | `/api/chits`              | List all chit groups     |
| GET    | `/api/chits/{id}`         | Get chit group by ID     |

### Members
| Method | Endpoint                          | Description              |
|--------|-----------------------------------|--------------------------|
| POST   | `/api/members`                    | Add member to group      |
| GET    | `/api/members/chit/{id}`          | List members in group    |
| PUT    | `/api/members/{id}`               | Update member            |
| DELETE | `/api/members/{id}`               | Delete member            |

### Auctions
| Method | Endpoint                                          | Description                  |
|--------|---------------------------------------------------|------------------------------|
| POST   | `/api/auctions/record`                            | Record auction result        |
| GET    | `/api/auctions/group/{id}`                        | List auctions for group      |
| GET    | `/api/auctions/group/{id}/chit-group-balance`     | Get group balance            |
| GET    | `/api/auctions/group/{id}/last-month-payout`      | Get last month payout info   |
| GET    | `/api/auctions/chitgroups/{id}/completed-months`  | List completed months        |

### Payments
| Method | Endpoint                                                         | Description                  |
|--------|------------------------------------------------------------------|------------------------------|
| POST   | `/api/payments/record`                                           | Record a member payment      |
| DELETE | `/api/payments/group/{gid}/month/{m}/member/{mid}`               | Unmark payment               |
| GET    | `/api/payments/group/{id}`                                       | All payments for group       |
| GET    | `/api/payments/group/{gid}/month/{m}/unpaid`                     | Unpaid members for month     |
| GET    | `/api/payments/group/{gid}/month/{m}/status`                     | Per-member payment status    |
| GET    | `/api/payments/group/{gid}/month/{m}/summary`                    | Monthly payment summary      |

### Meetings
| Method | Endpoint                        | Description          |
|--------|---------------------------------|----------------------|
| POST   | `/api/meetings`                 | Create meeting       |
| GET    | `/api/meetings/group/{id}`      | List meetings        |
| PUT    | `/api/meetings/{id}`            | Update meeting       |
| DELETE | `/api/meetings/{id}`            | Delete meeting       |

### Settlements
| Method | Endpoint                           | Description              |
|--------|------------------------------------|--------------------------|
| POST   | `/api/settlements/group/{id}/settle` | Settle group           |
| GET    | `/api/settlements/group/{id}`        | Get settlements        |

### Owner Month & Payments
| Method | Endpoint                                      | Description                  |
|--------|-----------------------------------------------|------------------------------|
| POST   | `/api/owner-month/trigger`                    | Trigger owner month          |
| GET    | `/api/owner-month/group/{id}`                 | List owner months            |
| POST   | `/api/owner-payments/group/{gid}/month/{m}`   | Record owner payment         |
| DELETE | `/api/owner-payments/group/{gid}/month/{m}`   | Unmark owner payment         |
| GET    | `/api/owner-payments/group/{id}`              | List owner payments          |

### Dashboard
| Method | Endpoint                          | Description          |
|--------|-----------------------------------|----------------------|
| GET    | `/api/dashboard/group/{id}`       | Group dashboard data |

---

## Domain Model

```
ChitGroup (1) ──── (N) Member
ChitGroup (1) ──── (N) Auction
ChitGroup (1) ──── (N) Payment
ChitGroup (1) ──── (N) Meeting
ChitGroup (1) ──── (N) Settlement
ChitGroup (1) ──── (N) OwnerMonth
ChitGroup (1) ──── (N) OwnerPayment
```

**Enums:**
- `ChitGroupStatus` — ACTIVE, COMPLETED, etc.
- `PaymentStatus` — PAID, UNPAID
- `PaymentMode` — CASH, ONLINE, etc.

---

## Getting Started

### Prerequisites
- Java 21
- Maven 3.9+
- Docker & Docker Compose (optional)
- PostgreSQL (or use Docker)

### Local Development

1. Clone the repo:
   ```bash
   git clone https://github.com/<your-username>/chitfund-backend.git
   cd chitfund-backend
   ```

2. Configure local properties (already gitignored):
   - Edit `src/main/resources/application-local.properties` with your DB credentials

3. Run with Maven:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

4. Or run the main class directly from your IDE with the `local` profile.

### Docker Compose

```bash
docker-compose up --build
```

This starts:
- **App** on `http://localhost:8080`
- **PostgreSQL** on `localhost:5432`
- **Frontend** on `http://localhost:3000`

### Kubernetes

```bash
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
```

---

## Environment Variables

| Variable          | Description                    |
|-------------------|--------------------------------|
| `DB_HOST`         | PostgreSQL host                |
| `DB_PORT`         | PostgreSQL port (default 5432) |
| `DB_NAME`         | Database name                  |
| `DB_USER`         | Database username              |
| `DB_PASSWORD`     | Database password              |
| `ADMIN_USERNAME`  | Admin login username           |
| `ADMIN_PASSWORD`  | Admin login password           |
| `JWT_SECRET`      | Secret key for JWT signing     |
| `PORT`            | Server port (default 8080)     |

---

## Authentication

- Single admin login via `/api/auth/login`
- Returns a JWT token (valid for 24 hours)
- All endpoints except `/api/auth/login` and `/api/health` require the `Authorization: Bearer <token>` header
- CORS enabled for `localhost:3000` (dev) and `chit-fund-frontend.vercel.app` (prod)

---

## CI/CD Pipeline

1. Push to `main` branch
2. GitHub Actions runs tests
3. Docker image built and pushed to GHCR
4. Auto-deployed to Render

---

## Current Status

- Core CRUD for chit groups, members, auctions, payments, meetings, settlements
- Owner month tracking and owner payment recording
- Dashboard summary endpoint
- JWT-based auth with single admin
- Dockerized with multi-stage build
- Kubernetes manifests ready
- GitHub Actions CI/CD to Render
- Frontend (React) lives in a separate repo: `chitfund-frontend`
