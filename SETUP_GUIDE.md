# 🏠 RoomMate.AI - Complete Setup Guide

## 🎯 Project Overview

RoomMate.AI is a comprehensive desktop application for college students to find rentals, roommates, and manage their housing needs. It features AI-powered search, real-time chat, and modern UI design.

## 🏗️ Architecture

```
┌────────────────────────────────────────────────────┐
│                JAVA SWING FRONTEND (Desktop UI)     │
│-----------------------------------------------------│
│ - Modern FlatLaf UI with dark/light themes         │
│ - AI-powered rental search interface               │
│ - Tinder-like roommate matching                    │
│ - Real-time chat with Firebase                     │
│ - Community feed and social features               │
│ - Profile management and settings                  │
└────────────────────────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────────┐
│           SPRING BOOT BACKEND (API + Logic)         │
│-----------------------------------------------------│
│ - REST APIs with JWT authentication                │
│ - College email domain validation                  │
│ - Gemini AI integration for natural language       │
│ - Firebase Admin SDK for chat                      │
│ - MySQL database with JPA/Hibernate                │
│ - Comprehensive business logic                      │
└────────────────────────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────────┐
│                MYSQL DATABASE (Data Layer)          │
│-----------------------------------------------------│
│ - Users, Rentals, Matches, Reviews                 │
│ - Rent Splits, Posts, Comments, Chat Rooms         │
│ - Optimized indexes and relationships               │
└────────────────────────────────────────────────────┘
```

## 🚀 Quick Start

### Prerequisites

- **Java 17+** (OpenJDK or Oracle JDK)
- **Maven 3.6+**
- **MySQL 8.0+**
- **Git**

### 1. Clone and Setup

```bash
# Clone the repository
git clone <repository-url>
cd Get-your-flat-with-mates

# Create MySQL database
mysql -u root -p
CREATE DATABASE roommate_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit

# Import database schema
mysql -u root -p roommate_ai < database/schema.sql
```

### 2. Backend Setup

```bash
cd backend

# Update application.properties with your database credentials
# Edit: src/main/resources/application.properties
# Set: spring.datasource.username=your_username
# Set: spring.datasource.password=your_password

# Set Gemini API key
export GEMINI_API_KEY="AIzaSyBK1jxgv3Os4rLjM71qu872oiy5A_BE6Mo"

# Build and run backend
mvn clean install
mvn spring-boot:run
```

**Backend will start at:** `http://localhost:8080/api`

### 3. Frontend Setup

```bash
cd frontend

# Build and run desktop application
mvn clean compile exec:java
```

**Desktop application will launch automatically**

## 🔧 Configuration

### Backend Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/roommate_ai?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password

# JWT Configuration
jwt.secret=your-secret-key-here
jwt.expiration=86400000

# Gemini API Configuration
gemini.api-key=${GEMINI_API_KEY}
gemini.api-url=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent

# College Email Domains
app.allowed-college-domains=.edu,.ac.in,.edu.in,srmuniv.ac.in,vit.ac.in,iit.ac.in,nit.ac.in
```

### Firebase Setup (Optional - for Chat)

1. Create Firebase project at https://console.firebase.google.com
2. Download `firebase-adminsdk.json` service account key
3. Place it in `backend/src/main/resources/`
4. Update `firebase.project-id` in application.properties

## 📊 Database Schema

The application uses the following main tables:

- **users** - Student profiles and preferences
- **rentals** - Property listings with location data
- **matches** - Roommate compatibility scores
- **reviews** - Rating system for landlords/roommates
- **rent_splits** - Bill division calculations
- **posts** - Community feed content
- **chat_rooms** - Firebase chat metadata

## 🎨 UI Features

### Modern Design
- **FlatLaf** themes (Dark/Light mode)
- **Responsive** grid layouts
- **Student-friendly** color scheme
- **Intuitive** navigation with tabs

### Key Panels
1. **Dashboard** - Overview and quick actions
2. **Rental Search** - AI-powered property search
3. **Roommate Matching** - Tinder-like interface
4. **Chat** - Real-time messaging
5. **Community** - Social feed and posts
6. **Profile** - User settings and preferences

## 🤖 AI Integration

### Gemini API Features
- **Natural Language Search**: "Find a room near SRM under ₹10k with AC"
- **Smart Filtering**: Extracts location, price, amenities automatically
- **Context Awareness**: Uses user's college for better results

### Example Queries
```
"PG with WiFi and meals under ₹8000"
"Shared room near VIT with gym access"
"Single room in Chennai with parking"
"Hostel near college with 24/7 security"
```

## 🔐 Security Features

- **College Email Validation**: Only `.edu`, `.ac.in` domains allowed
- **JWT Authentication**: Secure token-based auth
- **Role-based Access**: Student, Admin, Landlord roles
- **Input Validation**: Comprehensive data validation
- **CORS Configuration**: Secure cross-origin requests

## 📱 API Endpoints

### Authentication
- `POST /api/auth/signup` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/validate` - Token validation

### Rentals
- `GET /api/rentals` - List all rentals
- `POST /api/rentals` - Create rental
- `GET /api/rentals/{id}` - Get rental details
- `PUT /api/rentals/{id}` - Update rental
- `DELETE /api/rentals/{id}` - Delete rental

### AI Search
- `POST /api/ai/search` - AI-powered search
- `GET /api/ai/search?q=query` - Quick AI search
- `GET /api/ai/suggestions` - Search suggestions

## 🛠️ Development

### Backend Development
```bash
cd backend
mvn spring-boot:run
# Hot reload enabled with spring-boot-devtools
```

### Frontend Development
```bash
cd frontend
mvn compile exec:java
# Recompile and restart for changes
```

### Database Changes
```bash
# Update schema.sql for structural changes
# Use JPA ddl-auto=update for development
```

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
mvn test
```

## 📦 Deployment

### Backend Deployment
```bash
cd backend
mvn clean package
java -jar target/roommate-ai-backend-1.0.0.jar
```

### Frontend Deployment
```bash
cd frontend
mvn clean package
java -jar target/roommate-ai-1.0.0.jar
```

## 🔍 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check MySQL service is running
   - Verify credentials in application.properties
   - Ensure database exists

2. **Gemini API Errors**
   - Verify GEMINI_API_KEY environment variable
   - Check API quota and billing
   - Test API key with curl

3. **Frontend Won't Start**
   - Ensure Java 17+ is installed
   - Check Maven dependencies
   - Verify backend is running

4. **Authentication Issues**
   - Check JWT secret configuration
   - Verify college email domains
   - Clear browser cache/cookies

### Logs
- Backend logs: Console output or `logs/` directory
- Frontend logs: Console output
- Database logs: MySQL error log

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [FlatLaf Documentation](https://www.formdev.com/flatlaf/)
- [Gemini API Documentation](https://ai.google.dev/docs)
- [Firebase Documentation](https://firebase.google.com/docs)
- [MySQL Documentation](https://dev.mysql.com/doc/)

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🎓 Educational Purpose

This project is designed for learning full-stack development with modern technologies:
- Java Spring Boot for backend
- Java Swing with FlatLaf for desktop UI
- AI integration with Gemini API
- Real-time features with Firebase
- Database design and optimization

---

**Built with ❤️ for college students by college students**
