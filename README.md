# AET Preparation App – Backend

A robust backend API for the AET Preparation App, built with Spring Boot and Java 17.

---

## Features

- User registration and authentication (JWT-based)
- Password reset via email (with code verification)
- Profile management (edit name, change email with confirmation)
- Exam/test modules and scoring
- PostgreSQL database integration
- Secure RESTful API with JSON responses
- Email notifications (HTML support)
- AWS S3 integration for file storage

---

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven
- PostgreSQL database

### Installation
1. Clone the repository:
   ```bash
   git clone [<your-repo-url>](https://github.com/rayyxd/AET-Preparation-App)
   cd aet_back
   ```
2. Configure your database and environment variables in `src/main/resources/application.properties`.
3. Build the project:
   ```bash
   ./mvnw clean package
   ```
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The backend will be available at `http://localhost:8080` by default.

---

## Main Dependencies

- Spring Boot (Web, Security, Data JPA, Validation, Mail)
- PostgreSQL
- AWS Java SDK (S3)
- JSON Web Token (jjwt)
- Jackson (JSON serialization)

See `pom.xml` for the full list.

---

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

---

## License

This project is licensed under the MIT License.
