

```md
# 🚀 MentorBooster

For dev environment

![Deploy to EC2](https://github.com/Vaigundarajakrays/MentorboosterRefined/actions/workflows/deploy.yml/badge.svg)

MentorBooster is a full-stack mentorship platform that connects mentees with industry professionals. It handles everything from mentor discovery to real-time scheduling, payments, AI mentoring, and more — powered by Spring Boot, Stripe, Google Meet/Zoom, and OpenAI.

---

## 🧠 Features

- 🧑‍🏫 **Mentor Onboarding** with availability slots
- 📆 **Session Booking** with conflict checks
- 🔒 **Role-based Auth** (User, Mentor, Admin)
- 💳 **Stripe Integration** for secure payments
- 📧 **Email Notifications** for confirmations, cancellations, and updates
- 🧠 **AI Mentor Chat** using OpenAI (with SSE support)
- 📹 **Google Meet & Zoom Integration**
- 🌐 **REST APIs** for all user journeys
- ⚙️ **Admin Dashboards** with real-time data

---

## 🔧 Tech Stack

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


```


## 🚀 How to Run Locally

### 🧰 Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL DB (or use [Neon DB](https://neon.tech))
- AWS S3 bucket (for file upload)
- OpenAI API key (for AI mentor)
- Stripe Keys, Google OAuth Keys

### 🔨 Build & Run

```bash
./mvnw clean package -DskipTests
java -jar target/mentorbooster-*.jar
````

Or run from IDE (run `MentorBooster.java`)

---

## 🛡️ API Auth Flow (Simplified)

* **Login →** `/auth/login` → returns JWT in `HttpOnly` cookie
* **JWT included automatically** in subsequent requests
* **Logout →** `/auth/logout` → clears cookie

---

## 💰 Payment Flow

1. User books mentor slot
2. Stripe checkout session created
3. Upon successful payment → session confirmed
4. Confirmation emails sent to mentor and mentee

---

## 📡 Deployment Setup (GitHub Actions → EC2)

✔️ On push to `main`, the pipeline:

1. Builds the project with Maven
2. SCPs the JAR to your EC2 instance
3. Renames it with timestamp
4. Updates `mentorbooster-latest.jar` symlink
5. Runs your `start.sh` to restart the app 💥

---

## 📦 Production Ready Tips

* [ ] Add monitoring: Prometheus + Grafana / Actuator + Datadog
* [ ] Dockerize for better portability
* [ ] Add CI test stage before deploy
* [ ] Rate limiting & abuse prevention
* [ ] Environment secrets vault (AWS Secrets Manager / GitHub Encrypted Secrets)

---

## 🙌 Author

Built with ❤️ by [Vaigundarajakrays](https://github.com/Vaigundarajakrays)
Feel free to ⭐ the repo and contribute!

---

## 📃 License

MIT License. Feel free to use and modify.

---

## 🪄 Badge Magic (if you missed it)

This line in README shows live CI/CD status:

```md
![Deploy to EC2](https://github.com/Vaigundarajakrays/MentorboosterRefined/actions/workflows/deploy.yml/badge.svg)
```

