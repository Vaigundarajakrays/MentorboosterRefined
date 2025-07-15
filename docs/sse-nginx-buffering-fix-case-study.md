# Case Study: SSE Streaming Issue in Production Due to Nginx

## 🧠 Problem Statement

We implemented a streaming AI mentor feature using Server-Sent Events (SSE) in a Spring Boot backend and a React frontend. In development, everything worked perfectly — the bot would "type out" responses slowly, word by word.

However, in production, the AI responses would come all at once like a bulk message — no streaming behavior.

---

## 🔍 Initial Setup

* **Frontend**: React app hosted on same EC2 instance as backend
* **Backend**: Spring Boot app using `WebClient` to call OpenAI API and stream response via `Flux<String>`.
* **Deployment**: Nginx used as reverse proxy for both frontend and backend on a single EC2 instance.

---

## 🔧 Backend SSE Endpoint (Spring Boot)

```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> streamAi(@RequestParam String message) {
    // validation + moderation checks...
    return aiMentorService.streamResponse(message); // uses WebClient to call OpenAI with stream: true
}
```

---

## ❓ The Confusing Part

* **Local/dev**: Streaming worked — bot replied word by word (thanks to SSE)
* **Prod**: Full message came at once — no typing effect

That means the backend was likely sending data **correctly**, but something between backend and frontend was buffering or breaking the stream.

---

## 💡 Suspicion: Nginx

After lots of trial & error, we guessed that the culprit was **Nginx buffering** the response.

---

## 🔍 How We Confirmed

1. Inspected response via browser DevTools (Network tab)

    * Saw chunks like:

      ```
      data: Good
      ```

data: bye data: ! \`\`\` but they arrived all at once, not in real-time

2. Ran the same SSE endpoint directly (bypassing Nginx):

   ```bash
   curl http://localhost:8080/api/ai-mentor/stream?message=hi
   ```

   ✅ Streaming worked perfectly

That clearly pointed to **Nginx as the bottleneck**.

---

## 🔧 Solution: Disable Nginx Buffering for SSE Endpoint

We updated our Nginx config to treat the SSE `/stream` endpoint specially:

### ✅ Added this block BEFORE `/api/`

```nginx
location /api/ai-mentor/stream {
    proxy_pass http://127.0.0.1:8080;
    proxy_http_version 1.1;
    proxy_set_header Connection '';
    chunked_transfer_encoding on;
    proxy_buffering off;       # 💥 This line is KEY
    proxy_cache off;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
}
```

### 🧠 Why It Worked

* `proxy_buffering off`: Prevents Nginx from buffering entire response before sending it to the client.
* `chunked_transfer_encoding on`: Makes sure the data is streamed in chunks.

Also, we placed this location **before** the general `/api/` block, because Nginx follows **longest prefix match**.

---

## ✅ Result

* Now, the AI bot streams its response in production just like in dev.
* User sees the message "typing out" live — smooth and real-time.

---

## 🧵 Key Learnings

* Always suspect Nginx buffering when streaming doesn’t work in production
* SSE needs special Nginx treatment
* Order of location blocks matters
* `curl` is your best friend to debug network behavior

---

## 📂 Bonus Tip: Nginx Logging

You can inspect `/var/log/nginx/access.log` and `error.log` to check what's going on at proxy level.

---

This case was a great learning moment to understand:

* Streaming in web apps
* How Nginx proxies requests
* Nginx buffering and its effects

> "It’s not always your code. Sometimes, it’s your server config." 😅

**Author:** Team MentorBoosters  
**Last Updated:** 2025-07-14