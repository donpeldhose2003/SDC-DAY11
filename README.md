🎟️ Event Ticket Token System (Java + Vert.x + MongoDB)

📅 Task Day 11

A secure backend system to manage event registrations, user logins, and token bookings with email notifications.

🚀 Objective
Build a Java-based system using Vert.x, MongoDB, and SMTP that allows:

User registration with email-based password delivery

Event viewing and token booking with limited availability

Email confirmation of unique booking tokens

📋 Features
✅ User Registration
Users register using email and name

A random password is generated

Password is emailed to the user via SMTP

✅ User Authentication
Users log in with their email and password

Secure login using hashed password verification

✅ Event Viewing
Authenticated users can fetch a list of upcoming events

Each event includes:

Name, Date, Venue, Total Tokens, Remaining Tokens

✅ Token Booking
Users can book tokens for a specific event

System generates a unique token ID (e.g., alphanumeric or QR)

Available token count is decremented by 1

Token is sent to the user’s email via SMTP

✅ Email Notifications
Passwords sent on registration

Booking confirmation (with token) sent on successful booking

🧰 Tech Stack

Component	Technology
Backend	Java with Vert.x
Database	MongoDB
Email	JavaMail + SMTP
Build Tool	Maven / Gradle
Server Port	8888 (default)

📦 API Endpoints

Method	Endpoint	Description
POST	/api/register	Register user
POST	/api/login	Login with email & password
GET	/api/events	List upcoming events
POST	/api/book/:eventId	Book a token for selected event

📧 SMTP Setup

Make sure to configure EmailUtil.java:

String from = "your_email@gmail.com";
String password = "your_app_password"; // Use Gmail App Password

🗃️ MongoDB Collections
users


{
  "_id": ObjectId,
  "name": "John Doe",
  "email": "john@example.com",
  "password": "hashed_password"
}
events

{
  "_id": ObjectId,
  "title": "Tech Fest 2025",
  "venue": "Main Auditorium",
  "date": "2025-07-10",
  "totalTokens": 100,
  "availableTokens": 78
}
bookings


{
  "_id": ObjectId,
  "userEmail": "john@example.com",
  "eventId": ObjectId,
  "tokenCode": "EVT2025-ABC123",
  "timestamp": ISODate
}
✅ Prerequisites
Java 17+

MongoDB running locally or cloud

Internet access (for SMTP)

Maven / Gradle

Gmail with App Passwords enabled

🚦 How to Run

# Clone the project
git clone https://github.com/your-username/event-token-system.git

# Navigate into the project
cd event-token-system

# Build the project
mvn clean install

# Run the app
java -cp target/event-token-system.jar org.example.Main
Access the server at: http://localhost:8888

📬 Example SMTP Output

Subject: Your Event Booking Token

Hello John,

Your booking for Tech Fest 2025 is confirmed.
Here is your unique token: EVT2025-XYZ789

Please present this at the entrance.
🧪 Sample Testing (Postman)
You can test the APIs using Postman:

Register user ➝ /api/register

Login ➝ /api/login

List events ➝ /api/events

Book token ➝ /api/book/<eventId>

📁 Suggested Directory Structure

event-token-system/

├── src/

│   ├── main/java/org/example/

│   │   ├── Main.java

│   │   ├── utils/EmailUtil.java

│   │   ├── handlers/...

│   │   └── database/MongoClientProvider.java

├── pom.xml

└── README.md
