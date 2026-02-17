# 🪧 Virtual Notice Board - JAVA

### Developed by: Team Code Monkeys  
**College:** Vivekananda College of Engineering & Technology (VCET)

The **Virtual Notice Board (VNB)** backend is the core of the system that powers authentication, authorization, and all notice management operations.  
It serves RESTful APIs for the Angular frontend and securely stores all data in a MySQL database.

---

## 🚀 Features

- 🔐 **JWT Authentication** and **Role-Based Access Control (RBAC)**
- 🧩 **RESTful APIs** for creating, reading, updating, and deleting notices
- 🧑‍🏫 **User Roles:** Admin, Teacher, and Student
- 💾 **MySQL Database Integration** for persistent storage
- 🧱 **Error Handling & Validation** for secure data flow
- ⚙️ **Spring Security** for protecting all endpoints

---

## 🛠️ Tech Stack

| Technology | Purpose |
|-------------|----------|
| **Java 17+** | Core backend programming language |
| **Spring Boot 3+** | Framework for building production-ready REST APIs |
| **Spring Security + JWT** | Authentication and authorization management |
| **Spring Data JPA** | ORM layer for database interaction |
| **MySQL** | Relational database for data storage |
| **Postman / Swagger** | API testing and documentation tools |

---

## ⚙️ Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Shrinidhi-2006/Virtual-Notice-Board-JAVA.git
   cd Virtual-Notice-Board-JAVA

2. **Create MySQL Database**
   ```bash
   CREATE DATABASE virtual_notice_board;
3. **Configure Database Connection**
   ```bash
   spring.datasource.url=jdbc:mysql://localhost:3306/virtual_notice_board
   spring.datasource.username=root
   spring.datasource.password=root
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.hibernate.ddl-auto=update

4. **Run the Application**
   ```bash
   mvn spring-boot:run
5. **The backend server will start on:**
   ```bash
   http://localhost:8080
