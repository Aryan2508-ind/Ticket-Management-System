# 🎫 Ticket Management System - Java & MySQL

A robust and scalable Ticket Management System built using Java (JDK 21) and MySQL. This project demonstrates professional software development practices, including clean architecture and optimized database interactions.

## 🌟 Key Highlights
* DAO (Data Access Object) Pattern: Separated database logic from business logic for better maintainability and testing.
* Singleton Design Pattern: Implemented a thread-safe Singleton for database connections to optimize resource usage.
* CRUD Functionality: Full support for Creating, Reading, Updating, and Deleting ticket records.
* Maven Integration: Managed dependencies and build lifecycle using pom.xml.

## 🛠️ Tech Stack
* Language: Java (JDK 21)
* Database: MySQL
* Build Tool: Maven
* Architecture: Modular Layered Architecture (DAO, Service, Model, Utils)

## 📁 Project Structure
* **com.ticketsystem.dao**: Contains TicketDAO for all database queries.
* **com.ticketsystem.models**: Contains the Ticket entity class.
* **com.ticketsystem.service**: Business logic layer.
* **com.ticketsystem.utils**: Database connection utility using Singlschema.sql* **schema.sql**: Database schema for quick table setup.

---
*Developed with ❤️ by Aryan*
