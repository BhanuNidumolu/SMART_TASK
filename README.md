# 🚀 Smart Task Planner

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)
![Build Status](https://img.shields.io/badge/Build-Passing-success.svg)
![AI Powered](https://img.shields.io/badge/AI%20Integration-Spring%20AI-orange.svg)

---

***NOTE***: ***Used AI for FrontEnd***

## 🧠 Overview

**Smart Task Planner** is an **AI-powered web application** that transforms natural-language goals into structured, scheduled, and actionable project plans.  
It intelligently breaks down your goals, analyzes dependencies, and generates an optimized timeline with **critical path analysis**.  

Built with **Java 17**, **Spring Boot 3**, and **Spring AI**, it combines the power of AI with modern backend engineering to simplify project planning and execution.

---

## ✨ Key Features

### 🤖 AI-Powered Plan Generation  
Type a goal (e.g., *"Launch a new marketing campaign"*) — the AI assistant generates a full breakdown of tasks and subtasks.

### 🔗 Dependency-Aware Scheduling  
The system automatically detects task dependencies and schedules them in the correct logical order.

### ⭐ Critical Path Analysis  
Highlights the most time-sensitive tasks that determine the project’s total duration.

### ⚡ Asynchronous Processing  
AI plan generation runs in background threads for a **non-blocking user experience**.

### 📡 Real-Time Notifications  
Users receive **WebSocket updates** when the plan is ready — no refresh required.

### 🔒 Secure Authentication  
Implemented with **Spring Security**, ensuring each user’s plans are fully isolated and secure.

### ✅ Interactive UI  
A sleek interface built with **Thymeleaf + Bootstrap 5**, offering real-time updates and one-click task status changes.

---

## 🏗️ Architecture Overview

The system is modular and decoupled for scalability and maintainability.

### 🧩 High-Level Architecture

<img width="907" height="758" alt="Screenshot 2025-10-15 095952" src="https://github.com/user-attachments/assets/0a87a6dd-f7f0-4d9b-923e-c60343c43a8a" />

Copy code

**Diagram Placeholder:**  
📊 Add a visual diagram (PNG/SVG) showing data flow between:

<img width="1249" height="571" alt="Screenshot 2025-10-15 100843" src="https://github.com/user-attachments/assets/c502106f-05af-401a-861d-2cd23b3787d5" />


## 🛠️ Technology Stack

### **Backend**
- **Java 17**
- **Spring Boot 3**
- **Spring AI** (for AI model integration)
- **Spring Security**
- **Spring Data JPA**
- **Spring Web / WebSocket**
- **Project Reactor**

### **Database**
-  **MySQL**
- Supports **PostgreSQL**,**H2 (Development)**, etc.

### **Frontend**
- **Thymeleaf**
- **HTML5**, **CSS3**, **Bootstrap 5**
- **SockJS + STOMP.js** (for WebSockets)

### **Build Tool**
- **Apache Maven**

---

## ⚙️ Getting Started

### **Prerequisites**
- JDK 17 or higher  
- Apache Maven  
- AI Provider API Key (OpenAI, Google Gemini, etc.)

---

### 🧩 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/smart-task-planner.git
   cd smart-task-planner
Configure your AI Provider
Open src/main/resources/application.properties and add:

properties
```
Copy code
#
# Spring AI Configuration
#
spring.ai.openai.api-key=YOUR_OPENAI_API_KEY
spring.ai.openai.chat.options.model=gpt-4
```

# Default system prompt
default.system.prompt=You are an expert project planner...
Database Configuration (Optional)
For Mysqk:

properties
```
Copy code
spring.datasource.url=jdbc:Mysql://localhost:5432/smart_task_planner
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
Run the application
```
```
Copy code
mvn spring-boot:run
```
Open your browser:
👉 http://localhost:8080

🔄 Core Workflow: AI Plan Generation
Sequence Overview
Mysql

<img width="1655" height="467" alt="image" src="https://github.com/user-attachments/assets/ac5785ba-dc1f-4676-8097-3eb8602003cb" />

Step-by-Step Flow
User Prompt: Authenticated user submits a goal.

Async Processing: TaskController forwards to AsyncSchedulingService.

AI Interaction: ChatService sends a structured request to the AI model.

Data Parsing: TaskService stores the generated tasks and computes dependencies.

Critical Path: Tasks are analyzed for timing and priority.

Real-Time Update: NotificationService triggers a WebSocket event.

Visualization: UI updates automatically with the timeline and task list.

Diagram Placeholder:
<img width="1036" height="496" alt="image" src="https://github.com/user-attachments/assets/23f64ea1-68f2-4b11-8cfa-112909106a93" />


📂 Project Structure
bash
```
Copy code
src/main/java/springai/smart_task/
├── Configurations/   # Security, WebSocket, and AI configs
├── Controllers/      # Web and REST endpoints
├── DTO/              # Data Transfer Objects for API + AI
├── Entities/         # JPA Entities (User, Task)
├── Repositories/     # Database Repositories
└── Service/          # Core business logic (TaskService, ChatService, etc.)

src/main/resources/
├── static/css/       # Stylesheets
└── templates/        # Thymeleaf HTML templates
```
***💡 Future Enhancements***

✅ User-defined task priority and weights

✅ Integration with Google Calendar

✅ Gantt chart and timeline visualization

✅ Collaborative multi-user projects

✅ Export to PDF/Excel formats

✅ Integration with Slack / Email notifications
 ***Login Page***

<img width="868" height="643" alt="Screenshot 2025-10-15 101652" src="https://github.com/user-attachments/assets/c5fc31e4-5f8a-4b4f-9479-d9b73935238e" />

***Scheduling page***
<img width="1720" height="769" alt="Screenshot 2025-10-15 102128" src="https://github.com/user-attachments/assets/3d9322e0-a0e2-4a80-95d3-0cf3edf4f1b2" />

***TAsk BREAKDOWN PAGE***
<img width="1726" height="890" alt="Screenshot 2025-10-15 102244" src="https://github.com/user-attachments/assets/01b7cd59-8377-4a21-a700-64ac757f8fd9" />

***👨‍💻 Author***

N. Bhanu Prasad

📍 Vijaywada, India

🔗 GitHub: BhanuNidumolu

💼 Computer Science Engineering Student | Java & Spring Developer | AI Enthusiast

⭐ Support
If you like this project, please consider giving it a ⭐ on GitHub!
Your support helps improve and maintain open-source software.
