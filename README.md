# 🚀 MentorBooster

MentorBooster is a full-stack mentorship platform that connects mentees with industry professionals. It handles everything from mentor discovery to real-time scheduling, payments, AI mentoring, and more — powered by Spring Boot, Stripe, Google Meet/Zoom, and OpenAI.

---

## 📦 Deployment Status

| Environment | Status Badge | Logs |
|-------------|--------------|------|
| 🧪 Dev       | ![Dev Deploy](https://github.com/Vaigundarajakrays/MentorboosterRefined/actions/workflows/deploy-dev.yml/badge.svg) | [View Logs »](https://github.com/Vaigundarajakrays/MentorboosterRefined/actions/workflows/deploy-dev.yml) |
| 🚀 Prod      | ![Prod Deploy](https://github.com/Vaigundarajakrays/MentorboosterRefined/actions/workflows/deploy-prod.yml/badge.svg) | [View Logs »](https://github.com/Vaigundarajakrays/MentorboosterRefined/actions/workflows/deploy-prod.yml) |

> ✅ Green = Successful Deploy  
> ❌ Red = Failed Deploy  
> 🔁 Blue = Running Now

---

## 🧠 Features

- 🧑‍🏫 **Mentor Onboarding** with availability slots
- 📆 **Session Booking** with conflict checks
- 🔒 **Role-based Auth** (User, Mentor, Admin)
- 💳 **Stripe Integration** for secure payments
- 📧 **Email Notifications** (booking, cancel, reschedule)
- 🧠 **AI Mentor Chat** using OpenAI (SSE enabled)
- 📹 **Google Meet & Zoom Integration**
- 🌐 **REST APIs** for all user journeys
- ⚙️ **Admin Dashboards** with real-time data

---

## 🛠 Tech Stack

| Layer          | Tech Used                      |
|----------------|-------------------------------|
| Backend        | Java 17, Spring Boot, Maven   |
| Auth           | Spring Security, JWT          |
| Database       | PostgreSQL (Neon, local)      |
| File Storage   | AWS S3                         |
| Scheduling     | Google Calendar API, Zoom API |
| Payments       | Stripe                         |
| AI Chat        | OpenAI API + SSE              |
| DevOps         | GitHub Actions → EC2          |
| Frontend       | (Not included in this repo)   |

---

## 🚀 Setup Instructions

### 1️⃣ Clone the Project

```bash
git clone https://github.com/Vaigundarajakrays/MentorboosterRefined.git
cd MentorboosterRefined
````

### 2️⃣ Create `application.properties`

Inside `src/main/resources/`, create a file named `application.properties` and add the following:

#### 🗄️ Database Configuration

```properties
spring.datasource.url=jdbc:postgresql://your-db-url:5432/dbname
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

#### 📧 SMTP (Email) Configuration

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.connectiontimeout=7000
spring.mail.properties.mail.smtp.timeout=7000
spring.mail.properties.mail.smtp.writetimeout=7000
spring.mail.default-encoding=UTF-8
mail.from=your-email@gmail.com
```

#### 💸 Stripe Configuration

```properties
stripe.apiKey=your-stripe-secret-key
stripe.successUrl=https://yourdomain.com/success
stripe.cancelUrl=https://yourdomain.com/cancel
stripe.webhook.secret=whsec_*****
```

#### 🔐 JWT Security

```properties
security.jwt.secret.key=your-super-secret-key
```

#### 🧠 OpenAI Configuration

```properties
openai.api.key=your-openai-api-key
```

#### 📹 Zoom Configuration

```properties
zoom.account.id=your-zoom-account-id
zoom.client.id=your-client-id
zoom.client.secret=your-client-secret
```

#### ☁️ AWS S3 Configuration

```properties
aws.s3.access-key=your-access-key
aws.s3.secret-key=your-secret-key
aws.s3.region=your-region
aws.s3.bucket=your-bucket-name
```

#### 📁 File Upload Size (up to 10 MB)

```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

#### 💨 GZIP Compression

```properties
server.compression.enabled=true
server.compression.min-response-size=5120
server.compression.mime-types=application/json,text/html,text/xml,text/plain
```

#### 🔍 Actuator Base Path

```properties
management.endpoints.web.base-path=/api/actuator
```

---

**Author:** Team MentorBoosters  
**Last Updated:** 2025-07-19

