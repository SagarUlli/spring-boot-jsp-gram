# JSPGram - Backend

JSPGram Backend is a Spring Boot REST API for a social media platform that enables secure user authentication, post management, social interactions, image uploads, and premium membership integration.

## Tech Stack

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- MySQL
- Bean Validation
- Lombok
- Maven
- Cloudinary
- Razorpay

## Features

- User registration with OTP verification and session-based authentication.
- Secure login and logout with centralized exception handling.
- Profile management with Cloudinary image upload support.
- Create, update, delete, and retrieve posts with image uploads.
- Personalized home feed based on followed users.
- Follow and unfollow users with followers and following management.
- Like, unlike, add, and delete comments on posts.
- Razorpay integration for Prime membership activation.
- RESTful APIs using DTOs, mappers, and a generic API response wrapper.
- Layered architecture following Controller-Service-Repository design.

## Project Structure

```text
src
├── controller
├── service
├── repository
├── model
├── dto
├── mapper
├── exception
├── helper
└── config
```
