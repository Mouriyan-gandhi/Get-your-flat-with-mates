# RoomMate.AI — Smart Student Rental Platform

A desktop-based smart rental application exclusively for college students that combines AI-powered search, roommate matching, and real-time communication.

## 🎯 Features

- **College-only Access**: Verified through college email domains
- **AI-Powered Search**: Natural language rental search using Gemini API
- **Roommate Matching**: Tinder-like compatibility system
- **Real-time Chat**: Firebase-powered messaging
- **Rent Splitting**: Automatic bill division calculator
- **Review System**: Rate landlords and roommates
- **Community Feed**: Social features for students

## 🏗️ Architecture

```
Java Swing Frontend (Desktop UI)
           ↓
Spring Boot Backend (REST APIs)
           ↓
MySQL Database + Firebase Chat
           ↓
Gemini AI + Google Maps APIs
```

## 🚀 Quick Start

### Prerequisites
- Java 17+
- MySQL 8.0+
- Maven 3.6+
- Firebase project setup
- Gemini API key

### Backend Setup
```bash
cd backend
mvn spring-boot:run
```

### Frontend Setup
```bash
cd frontend
mvn clean compile exec:java
```

## 📁 Project Structure

```
├── backend/                 # Spring Boot API server
│   ├── src/main/java/
│   │   └── com/roommateai/
│   │       ├── RoommateAiApplication.java
│   │       ├── config/      # Security, Firebase config
│   │       ├── controller/  # REST endpoints
│   │       ├── service/     # Business logic
│   │       ├── model/       # JPA entities
│   │       ├── repository/  # Data access
│   │       └── dto/         # Data transfer objects
│   └── pom.xml
├── frontend/               # Java Swing desktop app
│   ├── src/main/java/
│   │   └── com/roommateai/ui/
│   │       ├── MainFrame.java
│   │       ├── panels/      # UI components
│   │       ├── services/    # API clients
│   │       └── utils/       # Utilities
│   └── pom.xml
├── database/               # SQL scripts
└── docs/                   # Documentation
```

## 🔧 Configuration

1. **Database**: Update `application.properties` with MySQL credentials
2. **Firebase**: Add `firebase-adminsdk.json` to backend resources
3. **Gemini API**: Set `GEMINI_API_KEY` environment variable
4. **Google Maps**: Add Maps API key for location features

## 📊 Database Schema

- `users` - Student profiles and preferences
- `rentals` - Property listings
- `matches` - Roommate compatibility scores
- `reviews` - Rating system
- `rent_splits` - Bill division records
- `posts` - Community feed

## 🎨 UI Design

- Modern FlatLaf themes (Dark/Light)
- Tabbed navigation interface
- Responsive grid layouts
- Student-friendly color scheme

## 🔐 Security

- JWT-based authentication
- College email domain validation
- Spring Security integration
- Firebase security rules

## 📱 Development Phases

1. ✅ Backend setup and authentication
2. ✅ Database schema and rental management
3. ✅ AI search integration
4. ✅ Roommate matching system
5. ✅ Real-time chat
6. ✅ Rent splitting and reviews
7. ✅ Community features
8. ✅ UI polish and testing

## 🤝 Contributing

This is a student project focused on learning full-stack development with modern technologies.

## 📄 License

MIT License - See LICENSE file for details
