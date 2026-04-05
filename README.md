# 🚀 DevTrack — Full Stack System Monitoring App

DevTrack is a full-stack system monitoring application that tracks CPU, RAM, and Disk usage from a Linux server and displays it in a real-time Android app.

---

## 📱 Demo Features

- 📊 Live CPU, RAM, Disk usage
- 📈 Real-time CPU graph
- 🔄 Auto-refresh every 5 seconds
- 🌙 Dark mode support
- ⚠️ High usage alerts
- 🌐 Deployed backend (Render)

---

## 🧠 Architecture

```
Android App (Compose)
        ↓
ViewModel (State)
        ↓
Retrofit (API)
        ↓
Node.js Backend (Render)
        ↓
Bash Scripts (Linux)
```

---

## 🛠 Tech Stack

### Android

- Kotlin
- Jetpack Compose
- Retrofit
- ViewModel + Coroutines
- MPAndroidChart

### Backend

- Node.js
- Express.js
- Shell scripts

### DevOps

- Docker (local)
- Render (deployment)

---

## 📂 Project Structure

```
DevTrack/
├── backend/
├── android-app/
```

---

## 🚀 Setup

### Backend

```
cd backend
npm install
node server.js
```

### Android

- Open `android-app` in Android Studio
- Run on emulator

---

## 🌐 Live API

```
https://your-render-url.onrender.com/cpu
```

---

## 💼 Resume Highlight

Built a full-stack Android monitoring app with a deployed Node.js backend and real-time system metrics visualization.
