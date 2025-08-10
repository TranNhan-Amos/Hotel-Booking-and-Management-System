# Hotel Booking and Management System

A comprehensive hotel booking and management system built with modern Java technologies, providing a complete solution for hotel operations, customer bookings, and administrative management.

## 🚀 Features

- **Multi-role Authentication System** (Admin, Partner, Customer)
- **Hotel Room Management** with image galleries
- **Real-time Booking System** with status tracking
- **Payment Integration** with multiple status handling
- **Customer Profile Management** with email update support
- **Booking History** with persistent customer ID tracking
- **Admin Dashboard** for comprehensive management
- **Partner Portal** for hotel partners
- **Responsive Design** for all devices
- **Advanced Search & Filtering** for rooms
- **Notification System** for booking updates

## 🛠️ Technology Stack

### Backend Technologies

#### Core Framework & Language
- **Java 21** - Programming language
- **Spring Boot 3.5.0** - Main application framework
- **Maven** - Build tool and dependency management

#### Spring Framework Stack
- **Spring Boot Web** - Web application development
- **Spring Data JPA** - Database abstraction layer
- **Spring Security** - Authentication and authorization
- **Spring Boot DevTools** - Development utilities

#### Database & ORM
- **MySQL 8** - Primary database
- **Hibernate** - ORM framework with MySQL8Dialect
- **MySQL Connector/J** - Database driver

#### Validation & Serialization
- **Hibernate Validator 8.0.1** - Bean validation
- **Jakarta Validation API 3.0.2** - Validation specifications
- **Jackson JSR310** - JSON serialization for Java 8 date/time

#### Development Tools
- **Lombok** - Code generation (reduces boilerplate)
- **Apache Tomcat** - Embedded web server

### Frontend Technologies

#### Template Engine & Markup
- **Thymeleaf** - Server-side template engine
- **HTML5** - Semantic markup
- **Thymeleaf Spring Security Extensions** - Security integration

#### Styling & UI
- **CSS3** - Custom styling with modern features
- **Google Fonts (Poppins)** - Typography
- **Font Awesome 6.4.0** - Comprehensive icon library
- **Lucide Icons** - Additional modern icon set
- **Responsive Grid/Flexbox** - Modern layout systems

#### JavaScript
- **Vanilla JavaScript** - Client-side scripting
- **Custom Utility Functions** - Image handling and error management
- **DOM Manipulation** - Dynamic content updates

## 🏗️ Architecture & Design Patterns

### Design Patterns
- **MVC (Model-View-Controller)** - Application architecture
- **Repository Pattern** - Data access abstraction
- **Service Layer Pattern** - Business logic separation
- **DTO Pattern** - Data transfer objects
- **Custom User Details Service** - Authentication management

### Security Features
- **Spring Security Configuration** - Role-based access control
- **Custom Authentication** - Multi-role user management
- **Session Management** - Secure user state handling
- **CSRF Protection** - Cross-site request forgery prevention

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── sd19303no1/hotel_booking_and_management_system/
│   │       ├── Controller/          # MVC Controllers
│   │       │   ├── PageController/  # Page routing controllers
│   │       │   └── ApiController/   # REST API controllers
│   │       ├── Service/             # Business logic layer
│   │       ├── Repository/          # Data access layer
│   │       ├── Entity/              # JPA entities
│   │       ├── Config/              # Configuration classes
│   │       └── Security/            # Security configurations
│   └── resources/
│       ├── templates/               # Thymeleaf templates
│       │   ├── Admin/              # Admin dashboard pages
│       │   ├── Client/             # Customer pages
│       │   ├── Auth/               # Authentication pages
│       │   ├── Page/               # Main application pages
│       │   └── Layout/             # Reusable layout components
│       ├── static/                 # Static resources
│       │   ├── Css/                # Stylesheets
│       │   ├── js/                 # JavaScript files
│       │   └── img/                # Images and assets
│       └── application.properties   # Application configuration
└── test/                           # Unit and integration tests
```

## 🚀 Getting Started

### Prerequisites
- **Java 21** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- **Git**

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/Hotel-Booking-and-Management-System.git
   cd Hotel-Booking-and-Management-System
   ```

2. **Configure Database**
   - Update `src/main/resources/application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hotel_booking_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Open your browser and navigate to: `http://localhost:8080`

## 🔧 Configuration

### Database Configuration
The application uses MySQL 8 with Hibernate for ORM. Key configurations:
- **Auto DDL**: `update` (creates/updates tables automatically)
- **SQL Logging**: Enabled for development
- **Connection Pool**: HikariCP (default with Spring Boot)

### Security Configuration
- **Multi-role Authentication**: Admin, Partner, Customer
- **Session-based Authentication**: Secure session management
- **Password Encoding**: BCrypt encryption
- **CSRF Protection**: Enabled for form submissions

### Thymeleaf Configuration
- **Template Caching**: Disabled for development
- **Spring Security Integration**: Enabled
- **Fragment Support**: For reusable components

## 🎯 Key Features Implementation

### Booking System
- **Status Tracking**: PENDING, CONFIRMED, CANCELLED, COMPLETED, REFUNDED
- **Payment Integration**: Multiple payment status handling
- **Room Availability**: Real-time availability checking
- **Booking History**: Persistent tracking with customer ID

### User Management
- **Profile Updates**: Email change support with booking history preservation
- **Authentication Context**: Custom user details service
- **Role-based Access**: Different interfaces for different user types

### Admin Dashboard
- **Booking Management**: Complete booking lifecycle management
- **Room Management**: Add, edit, delete rooms with image galleries
- **Customer Management**: User account administration
- **Reports**: Booking and revenue analytics

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Development Team** - *Initial work and ongoing development*

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- Thymeleaf team for the powerful template engine
- Font Awesome and Lucide for beautiful icons
- All contributors who have helped improve this project

---

**Note**: This is an educational project developed as part of a graduation thesis (DATN - Đồ Án Tốt Nghiệp).
