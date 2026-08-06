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
- Jakarta Servlet (HttpSession)

## Features

### Authentication
- User registration with OTP verification
- Session-based authentication
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
- View home feed
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
