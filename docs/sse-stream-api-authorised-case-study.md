# 🔐 SSE Authentication Flow for `/api/ai-mentor/stream`

## 🧠 Problem

We use **Server-Sent Events (SSE)** for streaming AI responses.

However, the frontend uses **EventSource**, which:
- ❌ **Does not support setting custom headers**
- ❌ Cannot send the usual `Authorization: Bearer <JWT>` header

This makes it impossible to authenticate the `/stream` API using traditional JWT header-based auth.

---

## ✅ Our Solution: Temporary Handshake Token

To securely allow access to the `/stream` endpoint, we implemented a **temporary token handshake mechanism**:

### 🔁 Flow:

1. ✅ Frontend **first authenticates normally** with email/password and gets a JWT
2. ✅ Frontend then calls `/api/ai-mentor/sse-token` with JWT in header
    - Example:
      ```
      GET /api/ai-mentor/sse-token
      Authorization: Bearer <JWT>
      ```
3. ✅ Backend verifies JWT and issues a **short-lived temp token** (UUID)
4. ✅ Frontend then uses:
   ```js
   new EventSource(`/api/ai-mentor/stream?message=hi&token=<TEMP_TOKEN>`)
5. ✅ Backend verifies the `token` param before starting the AI stream

---

## 🛡️ Temp Token Details

- 🔑 Generated per authenticated user
- 🕒 Valid for **20 minutes**
- 🗂️ Stored in memory (or Redis in production)
- ♻️ Can be reused within the TTL
- 🛑 If token is missing/invalid/expired → streaming is denied

---

## ⚙️ Backend Code Summary

- `/sse-token` is a **JWT-protected** endpoint
- `/stream` is a **public** endpoint that internally validates the temp token manually
- Token validation is handled by `TempTokenService`

---

## 🔍 Why this is secure

- Tokens are only issued to logged-in users
- Tokens expire quickly
- Tokens are never stored in the frontend after use
- We avoid using JWTs in URLs (which is dangerous) — instead, we use internal short UUIDs

---

## 💡 Alternatives We Considered

| Option | Why We Didn’t Use It |
|-------|----------------------|
| **Cookies (HttpOnly)** | Requires CORS + `credentials: include`, which is complex in EventSource |
| **WebSockets** | Overkill for this simple uni-directional stream |
| **API Gateway** | Adds infra complexity, not needed right now |

---

## 🛠️ Future Improvements

- [ ] Store temp tokens in Redis for multi-instance scaling
- [ ] Add rate-limiting per token/user
- [ ] Support token revocation on logout

---

## 📎 Related Files

- `AiMentorController.java`
- `TempTokenService.java`
- `JwtService.java`
- `SecurityConfig.java`

---

**Author:** Team MentorBoosters  
**Last Updated:** 2025-07-09
