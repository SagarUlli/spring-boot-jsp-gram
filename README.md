# JSPGram - Social Media Web Application (Backend)

## Description

JSPGram is a Spring Boot REST API for a social media platform where users can register, verify accounts using OTP, create and manage posts, follow other users, interact through likes and comments, manage profiles, and upgrade to Prime membership using Razorpay.

## Tech Stack

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA (Hibernate)
- MySQL
- Maven
- Lombok
- Bean Validation
- Cloudinary
- Razorpay
- Spring Security + JWT (jjwt)
- Redis (OTP storage, feed cache)

## Features

### Authentication
- User registration with OTP verification
- Stateless JWT authentication (Spring Security)
- OTPs stored in Redis with expiry, attempt limit and resend cooldown
- Login and logout
- User profile retrieval

### User Management
- View and update profile
- Upload profile images using Cloudinary
- Follow and unfollow users
- View followers and following
- User suggestions

### Post Management
- Create posts with images
- View home feed (cached per user in Redis)
- Update posts
- Delete posts
- View individual posts

### Social Features
- Like and unlike posts
- Add comments
- View comments

### Premium Membership
- Razorpay order creation
- Payment verification
- Prime membership activation

### API Features
- RESTful API design
- DTO-based request and response models
- Generic API response wrapper
- Centralized exception handling
- Input validation using Bean Validation

## Database

- MySQL
- Spring Data JPA
- Hibernate ORM

## Third-Party Integrations

- Cloudinary for image storage
- Razorpay for payment processing

## Project Architecture

- Controller Layer
- Service Layer
- Repository Layer
- DTO Layer
- Mapper Layer
- Exception Handling


## Authentication

`POST /api/users/login` returns `data.token`. Send it on every other request:

```
Authorization: Bearer <token>
```

Logout is client-side (discard the token). Public endpoints: `register`, `login`, `verify-otp`, `resend-otp`, `logout`.

Accounts created before the JWT migration have an AES-encrypted password; it is converted to BCrypt automatically the first time the user logs in.

## Redis

- `otp:<userId>` - OTP (5 min TTL), plus `otp:attempts:<userId>` and `otp:cooldown:<userId>`
- `feed:<userId>` - cached home feed (60 s TTL, evicted when the user posts, likes, comments, follows, etc.)

The feed cache is optional: if Redis is down the feed is read from the database. OTP verification needs Redis.

## Environment variables

| Variable | Required | Notes |
|---|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | yes | database |
| `JWT_SECRET` | yes | at least 32 characters, keep private |
| `REDIS_URL` | no | default `redis://localhost:6379`; use `rediss://...` for TLS providers |
| `JWT_EXPIRATION_MINUTES` | no | default 1440 (24 h) |
| `CORS_ALLOWED_ORIGINS` | no | comma-separated frontend origins |
| `OTP_EMAIL_ENABLED` | no | `false` (default) logs the OTP to the server console for local dev; `true` emails it via Gmail SMTP |
| `GMAIL_USERNAME`, `GMAIL_APP_PASSWORD` | if emailing | SMTP |
| `CLOUDINARY_URL`, `RAZORPAY_KEY`, `RAZORPAY_SECRET` | yes | unchanged |

Run Redis locally: `docker run -p 6379:6379 redis:7`
