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
- **Docker** - Containerization

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
