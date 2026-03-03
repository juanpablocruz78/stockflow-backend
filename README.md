# 📦 StockFlow – Inventory & Order Management API

## 🚀 Overview

StockFlow is a backend application built with Spring Boot for managing products and customer orders with real business rules and stock validation.

This project demonstrates clean architecture, domain-driven design principles, JWT security, and professional testing practices.

---

## 🏗 Architecture

Layered architecture:

Controller → Service → Domain → Repository

Modules:

- common (exceptions, shared logic)
- inventory (orders, products, customers)
- security (JWT authentication)

---

## 📊 Domain Model

### CustomerOrder
- States: CREATED, CONFIRMED, CANCELLED, SHIPPED
- Rules:
    - Orders can only be modified in CREATED state
    - Cannot confirm order without items
    - Confirming reduces product stock
    - Cancelling restores stock

### Product
- Stock cannot be negative
- Business validation included

---

## 🔐 Security

- JWT-based authentication
- Role-based access
- Swagger public
- Tests run without security

---

## 📚 API Documentation

Swagger UI:

http://localhost:8080/swagger-ui/index.html

---

## 🧪 Testing

Run tests:

mvn test

Includes:
- Unit tests (Service layer)
- Integration tests (Controller layer)
- H2 in-memory DB for test profile

---

## ▶️ Running the Project

### 1️⃣ Clone

git clone https://github.com/yourusername/stockflow.git

### 2️⃣ Configure PostgreSQL

Update application.properties with your DB credentials.

### 3️⃣ Run

mvn spring-boot:run

---

## 🌍 Future Improvements

- Pagination improvements
- Reports
- Metrics
- Docker support
- CI/CD pipeline