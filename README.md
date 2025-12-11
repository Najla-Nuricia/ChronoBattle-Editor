# **ChronoBattle Editor**

A desktop Java application for creating, editing, and managing RPG-style battles between **Heroes** and **Enemies**, complete with strategies, skills, status effects, and automatic battle logging into a PostgreSQL database.

## 🚀 Features

* Create & edit **Heroes** (name, HP, ATK, level, strategy, skills, effects)
* Create & edit **Enemies** (normal & boss monsters)
* Battle simulation between selected Hero vs Enemy
* Live battle log display inside GUI
* Auto-save battle logs into database
* PostgreSQL integration
* Clean Swing-based GUI built in Java

## 🛠️ Tech Stack

* **Java 21**
* **Swing GUI**
* **PostgreSQL**
* **JDBC**
* **NetBeans** (recommended IDE)

## 📦 Project Structure

```
src/
 ├─ game/
 │   ├─ character/       # Player, Enemy, Boss, Monster
 │   ├─ skills/          # Skills
 │   ├─ strategy/        # Attack strategies
 │   ├─ effects/         # Status effects
 │   ├─ battle/          # Battle engine
 │   └─ config/          # DAO + DB helper
└─ gui/MainGUI.java
```

## ▶️ Running the Program

### **1. Using NetBeans (recommended)**

1. Open NetBeans
2. File → Open Project → pilih folder project
3. Klik **Run Project** (F6)

### **2. Running with .JAR**
#### **Running the App (Recommended for Users)**

1. Go to the `dist/` folder.
2. **Double‑click the generated `.jar` file** to run the application.

#### **Running the .JAR manually (optional)**

If double‑clicking doesn’t work, run it via terminal:

```
java -jar GUI_game.jar
```



