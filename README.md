# 📸 PhotoShare

A modern, feature-rich photo sharing social media platform built with Spring Boot and contemporary web technologies. PhotoShare enables users to upload, share, and interact with photos in a secure and engaging community environment.

---

## ✨ Features

### 🔐 Authentication & Security

- **JWT-based Authentication** - Secure token-based user authentication
- **Role-Based Access Control (RBAC)** - Fine-grained permission management
- **JWT Blacklist Service** - Token invalidation and logout management
- **Login Attempt Tracking** - Enhanced security with login attempt monitoring
- **OTP Support** - One-Time Password verification for sensitive operations
- **Password Security** - Encrypted password storage and validation

### 📷 Photo Management

- **Photo Upload & Sharing** - Users can upload and share their photos
- **Photo Tagging** - Tag photos for better organization and discovery
- **Photo Statistics** - Track photo views, likes, and engagement metrics
- **Photo First View Tracking** - Monitor first-time viewers
- **Daily View Statistics** - Analyze photo performance over time
- **View Guard** - Control and track photo viewing permissions

### 💬 Social Interaction

- **Comments System** - Users can comment on photos with nested discussions
- **Likes & Reactions** - Like and interact with photos
- **Follow System** - Follow other users to see their content
- **User Profiles** - Comprehensive user profile pages with statistics
- **Notifications** - Real-time notifications for user interactions

### 🏷️ Content Management

- **Tag System** - Create and manage photo tags
- **Moderation Logging** - Track content moderation activities
- **User Statistics** - Detailed user engagement and activity metrics
- **Comment Statistics** - Monitor comment activity and engagement

### 📧 Communication

- **Email Service** - Email notifications using SMTP (Brevo/SendGrid compatible)
- **User Notifications** - In-app notification system

### ⚡ Performance

- **Redis Caching** - High-performance data caching with Redis
- **Scheduled Tasks** - Background job processing with Spring Scheduling
- **JPA Auditing** - Automatic timestamp tracking for entities

---

## 🛠️ Tech Stack

### Backend

- **Java 17** - Modern Java with latest features
- **Spring Boot 3.5.7** - Latest Spring Boot version with virtual threads support
- **Spring Data JPA** - Object-relational mapping and database access
- **Spring Security** - Comprehensive security framework
- **Spring Validation** - Input validation and constraint checking
- **PostgreSQL 15** - Reliable relational database
- **Redis 7** - In-memory data store for caching and sessions
- **JWT (JSON Web Tokens)** - Stateless authentication

### Frontend Support

- **Thymeleaf** - Server-side template engine

### Build & Deployment

- **Maven** - Project build automation
- **Docker** - Containerization for consistent deployment
- **Docker Compose** - Multi-container orchestration

### Additional Libraries

- Lombok - Boilerplate code reduction
- Validation API - Data validation
- PostgreSQL JDBC Driver
- Redis Spring Data

---

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.8.9+**
- **Docker & Docker Compose** (for containerized deployment)
- **PostgreSQL 15** (for database)
- **Redis 7** (for caching)

---

## 🚀 Getting Started

### Local Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/dzungvd98/PhotoShare.git
   cd PhotoShare
   ```

2. **Configure environment variables**

   Create a `.env` file in the project root:

   ```env
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/photoshare?currentSchema=public
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=your_password
   SPRING_REDIS_HOST=localhost
   SPRING_REDIS_PORT=6379
   SPRING_REDIS_PASSWORD=
   MAIL_HOST=smtp-relay.brevo.com
   MAIL_PORT=587
   MAIL_USERNAME=your-email@example.com
   MAIL_PASSWORD=your-smtp-key
   ```

3. **Build the project**

   ```bash
   mvn clean package
   ```

4. **Run the application**

   ```bash
   java -jar target/photoshare-0.0.1-SNAPSHOT.jar
   ```

   Or using Maven:

   ```bash
   mvn spring-boot:run
   ```

### Docker Deployment

1. **Build and run with Docker Compose**

   ```bash
   docker-compose up -d
   ```

   This will start:

   - PostgreSQL database (port 5432)
   - Redis cache (port 6379)
   - PhotoShare application (port 8080)

2. **Access the application**
   ```
   http://localhost:8080
   ```

---

## 📁 Project Structure

```
PhotoShare/
├── src/
│   ├── main/
│   │   ├── java/com/dev/photoshare/
│   │   │   ├── config/              # Configuration classes (Security, Redis, Web)
│   │   │   ├── controller/          # REST API endpoints
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── exception/           # Custom exceptions
│   │   │   ├── repository/          # Data access layer
│   │   │   ├── security/            # Security-related classes
│   │   │   ├── service/             # Business logic layer
│   │   │   └── utils/               # Utility classes and enums
│   │   └── resources/
│   │       └── application.yaml     # Configuration file
│   └── test/
│       └── java/                    # Test classes
├── docker-compose.yml               # Docker Compose configuration
├── Dockerfile                       # Docker image definition
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

---

## 🔌 API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/logout` - Logout user

### Photos

- `GET /api/photos` - Get all photos
- `POST /api/photos` - Upload a new photo
- `GET /api/photos/{id}` - Get photo details
- `PUT /api/photos/{id}` - Update photo
- `DELETE /api/photos/{id}` - Delete photo
- `GET /api/photos/{id}/stats` - Get photo statistics

### Comments

- `GET /api/photos/{photoId}/comments` - Get comments on a photo
- `POST /api/photos/{photoId}/comments` - Add a comment
- `DELETE /api/comments/{id}` - Delete a comment

### Likes

- `POST /api/photos/{photoId}/like` - Like a photo
- `DELETE /api/photos/{photoId}/like` - Unlike a photo

### Follow

- `POST /api/users/{userId}/follow` - Follow a user
- `DELETE /api/users/{userId}/follow` - Unfollow a user
- `GET /api/users/{userId}/followers` - Get user followers

### User Profile

- `GET /api/users/{id}` - Get user profile
- `PUT /api/users/{id}` - Update user profile
- `GET /api/users/{id}/stats` - Get user statistics

---

## 🗄️ Database Schema

The application uses PostgreSQL with the following main entities:

- **Users** - User accounts and profiles
- **Photos** - Photo uploads and metadata
- **Comments** - Comments on photos
- **Likes** - Photo likes and reactions
- **Follows** - User follow relationships
- **Tags** - Photo tags and categories
- **Notifications** - User notifications
- **Refresh Tokens** - Token management
- **Moderation Logs** - Content moderation history

---

## ⚙️ Configuration

### Application Properties

Edit `src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: photoshare
  datasource:
    url: jdbc:postgresql://localhost:5432/photoshare
    username: postgres
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update # Options: none, validate, update, create-drop, create
  redis:
    host: localhost
    port: 6379
  mail:
    host: smtp-relay.brevo.com
    port: 587
```

### File Upload Configuration

- **Max File Size**: 10MB
- **Max Request Size**: 10MB
- **Upload Directory**: `./uploads/`

---

## 🔒 Security Features

- **JWT Authentication** - Token-based stateless authentication
- **Password Encryption** - Secure password hashing
- **Role-Based Authorization** - Control access based on user roles
- **CORS Support** - Cross-origin resource sharing configuration
- **Input Validation** - Server-side validation of all inputs
- **SQL Injection Prevention** - Using parameterized queries with JPA
- **Login Attempt Tracking** - Monitor and prevent brute force attacks

---

## 🧪 Testing

Run tests with Maven:

```bash
mvn test
```

Or run specific test classes:

```bash
mvn test -Dtest=YourTestClassName
```

---

## 📦 Dependencies

Core dependencies include:

- Spring Boot Starters (Data JPA, Security, Web, Validation)
- PostgreSQL JDBC Driver
- Spring Data Redis
- Thymeleaf Template Engine
- Lombok (for reducing boilerplate)
- Spring Mail (for email notifications)

See `pom.xml` for the complete list of dependencies.

---

## 🚧 Development

### Building from Source

```bash
# Clean and build
mvn clean package

# Build skipping tests
mvn clean package -DskipTests

# Run with debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx512m -Xms256m"
```

### Code Quality

The project uses:

- Spring Boot best practices
- RESTful API design principles
- Service layer architecture
- Repository pattern for data access

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is part of the PTIT curriculum. Please check the LICENSE file for more details.

---

## 👨‍💻 Author

**dzungvd98** - [GitHub Profile](https://github.com/dzungvd98)

---

## 📞 Support

For issues, questions, or suggestions, please open an issue in the [GitHub repository](https://github.com/dzungvd98/PhotoShare/issues).

---

## 🎯 Roadmap

- [ ] Mobile app (iOS/Android)
- [ ] Advanced photo filtering and effects
- [ ] Direct messaging between users
- [ ] Story/Story highlights feature
- [ ] Real-time notifications using WebSocket
- [ ] Photo discovery algorithm
- [ ] User recommendations
- [ ] Analytics dashboard for content creators

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Redis Documentation](https://redis.io/documentation)
- [JWT Introduction](https://jwt.io/introduction)

---

**Last Updated**: December 2025

**Status**: ✅ Active Development (dev branch)
