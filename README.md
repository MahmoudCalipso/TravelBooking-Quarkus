# 🌍 Travel Platform Backend

**Enterprise-grade travel booking platform backend built with Quarkus**

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.30.8-blue.svg)](https://quarkus.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## ✨ Features

### Core Functionality
- ✅ **Multi-Role User Management** - SUPER_ADMIN, TRAVELER, SUPPLIER_SUBSCRIBER, ASSOCIATION_MANAGER
- ✅ **Accommodation Marketplace** - Hotels, hostels, apartments, villas with approval workflow
- ✅ **Travel Reels** - Short-form video content (Instagram Reels/TikTok style)
- ✅ **Booking System** - Complete reservation with payment processing
- ✅ **Review & Rating** - Verified reviews with helpful votes
- ✅ **Events & Programs** - Organized travel experiences
- ✅ **Real-time Chat** - Group and direct messaging
- ✅ **Notifications** - Email, SMS, push notifications
- ✅ **Admin Dashboard** - Content moderation and analytics

### Advanced Features
- ✅ **OAuth 2.0** - Google, Microsoft, Apple login
- ✅ **Firebase Integration** - Cloud storage for images/videos
- ✅ **Payment Processing** - Stripe integration
- ✅ **Geolocation Search** - Location-based discovery
- ✅ **Analytics Dashboard** - Engagement and revenue tracking

---

## 🚀 Quick Start

### Using Docker Compose (Recommended)

```bash
# 1. Clone and navigate
git clone <repository-url>
cd backend

# 2. Setup environment
cp .env.example .env
# Edit .env with your configuration

# 3. Generate JWT keys
./generate-jwt-keys.sh

# 4. Start services
docker-compose up -d

# Access:
# - API: http://localhost:8080
# - Swagger: http://localhost:8080/swagger-ui
# - PgAdmin: http://localhost:5050
```

### Local Development

```bash
# 1. Install PostgreSQL 15+ and create database
createdb travelplatform

# 2. Generate JWT keys
./generate-jwt-keys.sh

# 3. Run in dev mode
./mvnw quarkus:dev

# Dev UI: http://localhost:8080/q/dev
```

---

## 📚 API Documentation

**Swagger UI**: http://localhost:8080/swagger-ui

### Sample Endpoints

```http
# Authentication
POST   /api/v1/auth/register
POST   /api/v1/auth/login
POST   /api/v1/auth/oauth/google

# Accommodations
GET    /api/v1/accommodations
POST   /api/v1/accommodations
GET    /api/v1/accommodations/{id}

# Travel Reels
GET    /api/v1/reels/feed
POST   /api/v1/reels
POST   /api/v1/reels/{id}/like

# Bookings
POST   /api/v1/bookings
GET    /api/v1/bookings
PUT    /api/v1/bookings/{id}/cancel

# Admin
PUT    /api/v1/admin/accommodations/{id}/approve
GET    /api/v1/admin/reports
```

---

## 🛠️ Tech Stack

- **Java 21** - Modern Java with latest features
- **Quarkus 3.30.8** - Supersonic Subatomic Java
- **PostgreSQL 15** - Relational database
- **Hibernate ORM** - Object-relational mapping
- **Flyway** - Database migrations
- **JWT** - Stateless authentication
- **OAuth 2.0** - Social login
- **Firebase** - Cloud storage
- **Stripe** - Payment processing
- **MapStruct** - DTO mapping
- **WebSockets** - Real-time chat, availability, notifications
- **Docker** - Containerization

## 🌐 Globalization & Local Payments

Local payment routing is currently a placeholder switch that selects a provider name per region. To productionize:
- Integrate each regional gateway SDK/webhooks (e.g., SEPA/iDEAL/Sofort for EU, Alipay/WeChat for APAC, Mercado Pago/OXXO for LATAM, PayTabs/Mada for MEA).
- Map gateway statuses back to your `PaymentStatus`, and handle retries, idempotency keys, and signature verification like the Stripe webhook does.
- Present and settle in the user’s preferred currency using live FX rates (configure `currency.rates.usd` or plug in a rates API).
- Provide clear customer flows for redirects/3DS where required and capture/void/refund paths per provider.

### Point 4: Redirect/3DS flow details
- Surface 3DS/redirect steps in the UI: show a loading state, open the bank challenge, and display a “returning to merchant” state while polling the webhook for final status.
- Track `payment_intent` (or gateway equivalent) status server-side; use webhooks as the source of truth and update bookings idempotently.
- Handle fallbacks: expire unfinished intents after a timeout, allow users to retry with a new intent, and surface a “try another payment method” option on failure.
- Capture/void/refund should be consistent with Stripe’s flow: create intent → authorize → capture on confirmation; void if cancelled pre-capture; issue refunds with audit logging.

## ⚡ Real-time Tokens & Caching (Scale)
- Push/WebSocket device tokens are persisted in `device_tokens` and rehydrated into an in-memory cache for fast fan-out; tokens are added/removed on register/unregister to survive restarts.
- Social proof counters and loyalty balances now use short-lived caches (45s for social proof, 5m for loyalty) layered over the database to reduce read load while keeping DB writes authoritative.
- Availability updates broadcast in real time over `/ws/availability/{accommodationId}` so active viewers see “just booked”/“now available” changes without polling.

---

## 🔐 Security

- BCrypt password hashing (cost factor 12)
- JWT token authentication (24-hour expiration)
- Role-Based Access Control (RBAC)
- OAuth 2.0 social login
- Input validation with Hibernate Validator
- SQL injection prevention (JPA)

---

## 📊 Database

### Core Tables
- users, user_profiles, user_preferences
- accommodations, accommodation_images
- travel_reels, reel_engagement, reel_comments
- bookings, booking_payments
- reviews, events, chat_groups
- notifications

### Migrations
Managed by Flyway in `src/main/resources/db/migration/`

---

## 🧪 Testing

```bash
# Unit tests
./mvnw test

# Integration tests
./mvnw verify

# Coverage report
./mvnw verify jacoco:report
```

---

## 🚢 Deployment

### Docker
```bash
docker build -t travel-platform .
docker run -p 8080:8080 travel-platform
```

### Production Build
```bash
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

---

## 📄 License

MIT License - see LICENSE file

---

## 📧 Support

- Documentation: See `/docs` folder
- Issues: GitHub Issues
- Email: support@travelplatform.com

---

**Built with ❤️ using Quarkus**
