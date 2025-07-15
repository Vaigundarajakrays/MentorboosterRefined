# 🚨 Case Study: File Upload Fails Locally and in Production (Spring Boot + Nginx)

## 🧠 Problem

We were implementing a feature where users could upload images to AWS S3 using a Spring Boot backend. Everything seemed fine until we started testing with larger files.

Uploads were failing — but in different ways — both **locally** and in **production**.

---

## 🔍 Behavior Summary

| Environment | File <1MB | File >1MB | Error Observed                       |
| ----------- | --------- | --------- | ------------------------------------ |
| Local       | ✅ Works   | ❌ Fails   | 400 Bad Request (Spring Boot)        |
| Production  | ✅ Works   | ❌ Fails   | 413 Request Entity Too Large (NGINX) |

---

## 🧪 Root Causes

### 📌 Local Environment: Spring Boot Limits

By default, Spring Boot limits file uploads to **1MB**.

To fix this, we added the following to `application.properties`:

```properties
spring.servlet.multipart.max-file-size=3MB
spring.servlet.multipart.max-request-size=3MB
```

✅ After this, large file uploads started working fine **locally**.

---

### 📌 Production Environment: NGINX Body Size Limit

In production, we were using **NGINX** as a reverse proxy.

Even though Spring Boot allowed larger files now, NGINX was rejecting requests **before they reached** the backend if size > 1MB.

We got this error in Postman:

```html
<title>413 Request Entity Too Large</title>
```

💡 This means NGINX was blocking the request at the server level.

---

## 🛠️ Final Fix: Update Both Configs

### ✅ Spring Boot

In `application.properties`:

```properties
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB
```

### ✅ NGINX

In `/etc/nginx/nginx.conf`, inside the `http {}` block:

```nginx
client_max_body_size 20M;
```

Then restart NGINX:

```bash
sudo nginx -t
sudo systemctl restart nginx
```

---

## ✅ Result

* File uploads work for all sizes up to 20MB
* Both Spring Boot and NGINX allow large request bodies

---

## 🧵 Key Learnings

* Spring Boot AND NGINX have their own file size limits
* A frontend CORS error may hide the **real** server-side issue
* Postman is your friend when debugging uploads
* Error `413` means **NGINX** is rejecting your request before your app sees it

---

**Author:** Team MentorBoosters  
**Last Updated:** 2025-07-14