# 🚀 MentorBooster

MentorBooster is a full-stack mentorship platform that connects mentees with industry professionals. It handles everything from mentor discovery to real-time scheduling, payments, AI mentoring, and more — powered by Spring Boot, Stripe, Zoom, and OpenAI.

Frontend repo: <https://github.com/ManojKrays/Mentor-Boosters>

---

## 🌿 Branches

- **`main` is the production branch and always has the latest code.** Work on `main` and build the production JAR from `main`.
- `backup/main-before-reset` keeps the old `main` history (before `main` became the production branch). Do not use it for builds.
- The GitHub Actions workflows were removed. Deploys are done by hand (see below).

---

## 📦 Production

| Part | How it runs |
|------|-------------|
| Server | Private AWS EC2 instance (no public IP). Login only through AWS Session Manager (SSM). |
| Entry | Load balancer with HTTPS (idle timeout 300 s, needed for the AI chat stream) forwards to Nginx on port 80. |
| Nginx | Serves the React build from `/var/www/html` and sends `/api/` to the app on `127.0.0.1:8080`. The AI stream path `/api/ai-mentor/stream` has no buffering. Upload limit 10 MB. |
| App | Java 17, run as the systemd service `mentorbooster` (restarts on crash and on reboot). JAR in `/opt/mentorbooster`, the file `mentorbooster-latest.jar` is a link to the current JAR. |
| Settings | `/opt/mentorbooster/config/application-prod.properties` (owner `mentorbooster`, mode 600). Never in git. |
| Database | Supabase PostgreSQL (session pooler, `sslmode=require`). Tables are created by Hibernate (`ddl-auto=update`). |
| File storage | AWS S3, by the instance role (no access keys). Folders `mentee-images/`, `mentor-images/`, `mentor-resumes/`. |
| Secrets | Stripe API key and webhook secret come from AWS Secrets Manager (`spring.config.import=aws-secretsmanager:mentorbooster/stripe/secret`). |
| Logs | `sudo journalctl -u mentorbooster -f` (app) and `/var/log/nginx/` (Nginx). |

### Deploy a new version (by hand)

1. On `main`, build a clean JAR (without git-ignored secret files): `git archive` the code into a new folder, then `mvn clean package -DskipTests` there.
2. Upload the JAR to the deploy S3 bucket.
3. On the server (SSM session), download it into `/opt/mentorbooster`, point `mentorbooster-latest.jar` to it, and run `sudo systemctl restart mentorbooster`.
4. Check the log, and keep only one JAR on the server.

### Outside services the server must reach

| Service | Port |
|---|---|
| Gmail SMTP (`smtp.gmail.com`) | 587 / 465 |
| OpenAI (`api.openai.com`) | 443 |
| Zoom (`zoom.us`, `api.zoom.us`) | 443 |
| Stripe (`api.stripe.com`) | 443 |
| Supabase pooler | 5432 |
| AWS S3 and Secrets Manager | 443 |

The Stripe webhook is `POST /api/stripe/webhook` on the production domain.

---

## 🧠 Features

- 🧑‍🏫 **Mentor Onboarding** with availability slots
- 📆 **Session Booking** with conflict checks
- 🔒 **Role-based Auth** (User, Mentor, Admin)
- 💳 **Stripe Integration** for secure payments
- 📧 **Email Notifications** (OTP, booking, cancel, reschedule)
- 🧠 **AI Mentor Chat** using OpenAI (SSE enabled)
- 📹 **Zoom Integration** (meetings)
- 🌐 **REST APIs** for all user journeys
- ⚙️ **Admin Dashboards** with real-time data

---

## 🛠 Tech Stack

| Layer          | Tech Used                      |
|----------------|-------------------------------|
| Backend        | Java 17, Spring Boot 3.4, Maven |
| Auth           | Spring Security, JWT          |
| Database       | PostgreSQL (Supabase in production, local for development) |
| File Storage   | AWS S3                         |
| Meetings       | Zoom API                       |
| Payments       | Stripe                         |
| AI Chat        | OpenAI API + SSE              |
| Hosting        | AWS EC2 (private) + load balancer + Nginx |
| Frontend       | Separate repo (see top)       |

---

## 🚀 Setup Instructions (local PC)

### 1️⃣ Clone the Project

```bash
git clone https://github.com/Vaigundarajakrays/MentorboosterRefined.git
cd MentorboosterRefined
```

### 2️⃣ Create `application.properties`

Inside `src/main/resources/`, create a file named `application.properties` (it is git-ignored) and add the following:

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

In production, `stripe.apiKey` and `stripe.webhook.secret` are **not** in the settings file. They come from AWS Secrets Manager (see the Production section).

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
aws.s3.region=your-region
aws.s3.bucket=your-bucket-name
```

There are **no S3 access keys**. The app uses the default AWS credentials:
- On the server: the instance role.
- On your PC: run `aws login` in the same terminal before you start the app.

Uploaded files are saved as `<folder>/<uuid>-<file name>` at the top level of the bucket, and the app stores a direct link. For the links to open in the browser, the folders `mentee-images/`, `mentor-images/` and `mentor-resumes/` must allow public read.

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
**Last Updated:** 2026-10-26
