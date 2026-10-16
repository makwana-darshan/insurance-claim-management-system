# Insurance Claim Management System (ICMS)

A full-stack, role-based web application for filing, reviewing, inspecting and deciding insurance claims. Customers submit claims, claim officers assign surveyors, surveyors inspect, and admins make the final decision, with a complete audit trail for every status change.

**Live demo:** https://insurance-claim-management-system.netlify.app
**Backend API:** https://icms-l9cn.onrender.com

> The backend runs on a free hosting tier and sleeps after inactivity. The first request after a quiet period can take 30-50 seconds to respond.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Claim Lifecycle](#claim-lifecycle)
- [Roles and Permissions](#roles-and-permissions)
- [API Overview](#api-overview)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [Deployment](#deployment)
- [Security Notes](#security-notes)
- [Known Limitations](#known-limitations)
- [Author](#author)

---

## Features

**Authentication and accounts**
- Stateless JWT authentication with a custom Spring Security filter chain
- Customer self-registration with **email verification** (accounts stay locked until the link is clicked)
- Forgot password / reset password via emailed, expiring, single-use tokens
- Change password (with strength meter) and profile editing
- Deactivated accounts are rejected on every request, not only at login, so a deactivated user is logged out immediately

**Claims workflow**
- Customers create, edit (while draft), submit and cancel claims
- Claim officers review submitted claims and assign surveyors
- Surveyors see only their assigned claims and mark them inspected
- Admins approve or reject inspected claims, with remarks
- Every transition is written to an audit log and viewable as a timeline
- Status changes and audit writes happen in one transaction, so they cannot drift apart

**Admin tools**
- Dashboard with live summary counts
- Searchable, filterable claims table and a claim detail page with inline timeline
- Staff account provisioning (Claim Officer, Surveyor, and others) and activate/deactivate controls

**Frontend experience**
- Angular 22 standalone components using signals (zoneless)
- Role-aware navbar, route guards and automatic redirect to the right dashboard
- Toast notification system, with a global HTTP error interceptor
- Empty states for every list, loading states, and responsive layouts for mobile and tablet

**Backend engineering**
- Asynchronous email sending with `@Async`, so SMTP/API latency never blocks a request
- Scheduled keep-alive job (`@Scheduled`) to reduce cold starts on free hosting
- Global exception handling that returns clean, consistent JSON errors
- Bean Validation on request DTOs

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 4.0.2, Spring Security, Spring Data JPA (Hibernate), Bean Validation |
| Auth | JWT (JJWT 0.12.x), BCrypt password hashing |
| Database | PostgreSQL |
| Frontend | Angular 22 (standalone components, signals, functional guards and interceptors), TypeScript |
| Email | Brevo transactional email HTTP API |
| Build and containers | Maven, Docker (multi-stage build) |
| Hosting | Render (backend), Neon (PostgreSQL), Netlify (frontend) |

---

## Architecture

```
┌──────────────────┐   HTTPS + JWT    ┌─────────────────────┐        ┌──────────────┐
│  Angular 22 SPA  │ ───────────────► │  Spring Boot REST   │ ─────► │  PostgreSQL  │
│  (Netlify)       │ ◄─────────────── │  API (Render/Docker)│        │  (Neon)      │
└──────────────────┘     JSON         └──────────┬──────────┘        └──────────────┘
                                                 │ async (HTTPS)
                                                 ▼
                                        Brevo email API
```

- The frontend is a standalone SPA. It stores the JWT client-side and attaches it through an HTTP interceptor.
- The backend is stateless: no server-side sessions. A `JwtAuthenticationFilter` validates the token and re-checks the account's enabled/locked state on each request.
- Controllers return DTOs, never JPA entities. Business rules live in services.

---

## Claim Lifecycle

```
DRAFT ──► SUBMITTED ──► UNDER_REVIEW ──► INSPECTED ──► APPROVED
  │            │              │               │     └─► REJECTED
  └────────────┴──────────────┴───────────────┴──────► CANCELLED (by customer, before a final decision)
```

| Transition | Performed by |
|---|---|
| DRAFT → SUBMITTED | Customer |
| SUBMITTED → UNDER_REVIEW (surveyor assigned) | Claim Officer |
| UNDER_REVIEW → INSPECTED | Surveyor |
| INSPECTED → APPROVED / REJECTED | Admin |
| Any non-final state → CANCELLED | Customer (owner) |

The `ClaimStatus` enum also contains `SURVEYOR_ASSIGNED`, which the current workflow does not use.

---

## Roles and Permissions

| Role | Capabilities |
|---|---|
| `CUSTOMER` | Register, file/edit/submit/cancel own claims, view own claim timeline |
| `CLAIM_OFFICER` | View submitted claims, assign surveyors |
| `SURVEYOR` | View assigned claims, mark inspected |
| `SUPER_ADMIN` | Dashboard, all claims, approve/reject, claim detail, user management |
| `FINANCE_OFFICER`, `MEDICAL_REVIEWER` | Seeded roles that can be assigned to staff accounts; no workflow is built for them yet |

Role checks use `hasAuthority(...)` against unprefixed role names stored in the database.

---

## API Overview

All endpoints are under `/api`. Everything except `/api/auth/**` and `/api/health` requires `Authorization: Bearer <token>`.

**Auth (public)**

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a customer and send a verification email |
| POST | `/api/auth/verify-email` | Verify an email token |
| POST | `/api/auth/login` | Log in, returns JWT and user info |
| POST | `/api/auth/forgot-password` | Request a reset email (always returns a generic success message) |
| POST | `/api/auth/reset-password` | Set a new password with a reset token |
| GET | `/api/health` | Lightweight health check used by the keep-alive job |

**Account (any authenticated user)**

| Method | Endpoint | Description |
|---|---|---|
| GET / PUT | `/api/account/me` | View / update own profile |
| POST | `/api/account/change-password` | Change own password |
| GET | `/api/claims/{id}/timeline` | Claim plus audit timeline (owner, or any staff role) |

**Customer** (`CUSTOMER`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/customer/dashboard` | List own claims |
| GET / PUT | `/api/customer/claims/{id}` | View / edit a draft claim |
| POST | `/api/customer/claims` | Create a draft claim |
| POST | `/api/customer/claims/{id}/submit` | Submit a claim |
| POST | `/api/customer/claims/{id}/cancel` | Cancel a claim |

**Claim Officer** (`CLAIM_OFFICER`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/claim-officer/dashboard` | Submitted claims |
| GET | `/api/claim-officer/surveyors` | Available surveyors |
| POST | `/api/claim-officer/claims/{id}/assign` | Assign a surveyor |

**Surveyor** (`SURVEYOR`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/surveyor/dashboard` | Assigned claims |
| POST | `/api/surveyor/claims/{id}/inspect` | Mark inspected |

**Admin** (`SUPER_ADMIN`)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/dashboard` | Summary counts |
| GET | `/api/admin/claims` | All claims |
| POST | `/api/admin/claims/{id}/approve` · `/reject` | Final decision with remarks |
| GET / POST | `/api/admin/users` | List users / create a staff account |
| PATCH | `/api/admin/users/{id}/status` | Activate or deactivate a user |

---

## Project Structure

```
icms/
├── insurance-claim-management-system/     # Spring Boot backend
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/insurance/icms/
│       ├── admin/          # Admin controllers
│       ├── bootstrap/      # DataInitializer (roles, permissions, seed users)
│       ├── claim/          # Claim entity, DTOs, services, controllers, audit log
│       ├── common/         # Global exception handler, health check, keep-alive job
│       └── security/       # JWT, user details, auth/account controllers, email service
└── icms-frontend/                         # Angular frontend
    └── src/app/
        ├── core/           # Guards, interceptors, models, services, shell layout
        ├── features/       # auth, home, customer, claim-officer, surveyor, admin, account
        └── shared/         # Toast container, empty-state component
```

---

## Getting Started

### Prerequisites

- Java 17+
- Node.js 20+ and Angular CLI 22
- PostgreSQL 14+ (a local instance and a database named `icms_db`)

### 1. Clone

```bash
git clone https://github.com/makwana-darshan/insurance-claim-management-system.git
cd insurance-claim-management-system
```

### 2. Backend

Create `insurance-claim-management-system/src/main/resources/application-local.properties` (it is git-ignored, so your secrets stay out of version control):

```properties
spring.datasource.password=your_local_postgres_password
jwt.secret=replace-with-a-random-string-of-at-least-32-characters
BREVO_API_KEY=your_brevo_api_key
BREVO_SENDER_EMAIL=your_verified_sender@example.com
```

Run it with the `local` profile:

```bash
cd insurance-claim-management-system
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

On Windows use `mvnw spring-boot:run "-Dspring-boot.run.profiles=local"`. The API starts on `http://localhost:8080`. Hibernate creates the tables on first run, and `DataInitializer` seeds roles, permissions and a few demo accounts.

### 3. Frontend

```bash
cd icms-frontend
npm install
ng serve
```

Open `http://localhost:4200`. The dev build reads `src/environments/environment.ts`, which points at `http://localhost:8080/api`.

### Seeded demo accounts

`DataInitializer` creates one account per role on first run:

`admin@icms.com` · `officer@icms.com` · `surveyor@icms.com` · `customer@icms.com`

Their passwords are defined in `DataInitializer.java`. Change them before using the app for anything real.

---

## Environment Variables

| Variable | Required | Description |
|---|---|---|
| `DB_URL` | Production | JDBC URL, e.g. `jdbc:postgresql://host/dbname?sslmode=require`. Defaults to local Postgres |
| `DB_USERNAME` | Production | Database user (default `postgres`) |
| `DB_PASSWORD` | Yes | Database password |
| `JWT_SECRET` | Yes | HMAC signing key, at least 32 characters |
| `BREVO_API_KEY` | Yes | Brevo API key for transactional email |
| `BREVO_SENDER_EMAIL` | Yes | A sender address verified in Brevo |
| `FRONTEND_URL` | Production | Public frontend URL, used to build links in emails (defaults to `http://localhost:4200`) |
| `KEEP_ALIVE_BASE_URL` | Optional | Public backend URL for the keep-alive ping. Falls back to Render's `RENDER_EXTERNAL_URL` |

Never commit real secrets. Local values belong in `application-local.properties` (git-ignored), and production values in your host's environment settings.

---

## Deployment

- **Backend:** built from the included multi-stage `Dockerfile` and deployed as a Docker web service on Render. Set the environment variables above in the service settings.
- **Database:** managed PostgreSQL on Neon. Use a JDBC URL with `sslmode=require`.
- **Frontend:** `ng build` (production configuration) deployed to Netlify with base directory `icms-frontend`. `angular.json` swaps in `environment.prod.ts` through `fileReplacements`, so set `apiUrl` there to your backend's public URL.
- **CORS:** add your frontend's origin to the allowed origins in `SecurityConfig`.
- **Email:** sent through Brevo's HTTPS API. Many free hosts block outbound SMTP ports (25/465/587), which is why raw SMTP is not used.

---

## Security Notes

- Passwords are hashed with BCrypt
- JWTs are signed with a secret supplied through the environment, and account status is re-checked on each request
- The forgot-password endpoint responds identically whether or not the email exists, to prevent account enumeration
- Reset and verification tokens are random UUIDs, expire, and are single-use
- Admin accounts cannot be deactivated through the API, and staff creation is restricted to a whitelist of roles
- Customers can only read or modify their own claims (ownership checks in the service layer)
- CSRF protection is disabled because authentication uses a bearer token in a header rather than cookies

---

## Known Limitations

- **Free-tier cold starts:** the backend sleeps after about 15 minutes idle, and the keep-alive job cannot wake a sleeping instance
- **Database enum constraints:** Hibernate generates `CHECK` constraints for `@Enumerated(EnumType.STRING)` columns. If you add a new `ClaimStatus` value to an existing database, you must update the constraints on `claims` and `claim_audit_logs` by hand
- **Fresh databases:** accounts seeded by `DataInitializer` must be created with `emailVerified = true`, or they cannot log in
- **JWT lifetime:** tokens last 1 hour and there is no refresh-token flow yet
- **Unfinished roles:** `FINANCE_OFFICER` and `MEDICAL_REVIEWER` exist but have no dashboards or workflow
- **Pagination:** lists load all records; there is no server-side pagination yet
- **Tests:** automated test coverage is minimal

---

## Author

**Darshan Makwana** — Java Backend Developer

- GitHub: [github.com/makwana-darshan](https://github.com/makwana-darshan)
- LinkedIn: [linkedin.com/in/darshan-makwana-dev](https://linkedin.com/in/darshan-makwana-dev)
