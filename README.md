# Курсова робота — Інформаційна система бронювання спортивних об’єктів

**Дисципліна:** Кросплатформене програмування  
**Варіант №14** | Група 204-ТН | Лисогор Дмитро Сергійович  
**Навчальний рік:** 2026–2027

---

## 1. Технологічний стек

| Компонент | Версія / призначення |
|-----------|----------------------|
| Java | 21 |
| Maven | керування залежностями та збірка |
| JavaFX | 21 — графічний клієнт |
| Servlet API | 4.0.1 (javax) + Apache Tomcat **9.x** |
| MySQL + Connector/J | 9.x |
| Jackson | 2.18 — HTTP/JSON |
| JUnit 5 | модульні тести (≥10) |

---

## 2. Архітектура

```
JavaFX-клієнт → HttpSportsClient → HTTP/JSON
      → Servlet → Service → DAO → JDBC → MySQL
```

- **Клієнт** ніколи не підключається до MySQL напряму.
- **Servlet** — HTTP-контролер (без SQL і без предметної логіки).
- **Service** — валідація, життєвий цикл, розрахунок інтегрального показника.
- **DAO** — лише PreparedStatement + JDBC.

### Основні сутності
1. **Facility** (спортивний об’єкт)  
2. **AppUser** (користувач)  
3. **TimeSlot** (часовий слот)  
4. **Booking** (бронювання) — об’єкт життєвого циклу

### Життєвий цикл Booking
```
CREATED → CONFIRMED → ACTIVE → COMPLETED
```
Недопустимі переходи відхиляються в `BookingService.isAllowedTransition(...)`.

### Інтегральний показник
\[
I = 0{,}40\cdot F_1 + 0{,}35\cdot F_2 + 0{,}25\cdot F_3
\]
- **F1** — кількість учасників (нормується: participants / 50 × 100, max 100)  
- **F2** — тривалість використання в хвилинах (нормується: minutes / 180 × 100, max 100)  
- **F3** — потреба в додатковому обладнанні (вже в діапазоні 0–100)

Усі фактори зберігаються вже нормованими (0–100). Розрахунок виконується один раз у сервісному шарі (`RiskCalculator`).

---

## 3. Структура проєкту

```
sports-booking-coursework/
├─ database.sql                 # повне відтворення БД + демо-дані
├─ README.md
├─ .gitignore
├─ server/                      # WAR-модуль
│  ├─ pom.xml
│  └─ src/main/java/.../server/
│     ├─ model (Facility, AppUser, TimeSlot, Booking, BookingState)
│     ├─ DAO × 4
│     ├─ Service × 4 + RiskCalculator
│     ├─ Servlet × 4 + JsonUtil + DBUtil
│     └─ webapp/WEB-INF/web.xml
│  └─ src/test/.../CourseworkTest.java   # 18 тестів
└─ client/
   ├─ pom.xml
   ├─ sample-data/time_slots.csv
   └─ src/main/java/.../client/
      ├─ model (дзеркальні класи)
      ├─ HttpSportsClient
      ├─ ImportTimeSlotsTask
      └─ MainApp (JavaFX)
```

---

## 4. Запуск

### 4.1. База даних
```bash
mysql -u root -p < database.sql
```
Або виконати файл у phpMyAdmin / MySQL Workbench.  
Має з’явитися БД `sports_booking` з таблицями `facility`, `app_user`, `time_slot`, `booking`.

### 4.2. Параметри підключення (необов’язково)
За замовчуванням: `root` / порожній пароль, `localhost:3306`.  
Можна перевизначити:
```bash
export DB_URL="jdbc:mysql://localhost:3306/sports_booking?..."
export DB_USER="root"
export DB_PASSWORD="your_password"
```

### 4.3. Сервер
```bash
cd server
mvn clean package          # також запускає тести
# скопіювати target/sports-booking-server.war → $CATALINA_HOME/webapps/
# запустити Tomcat 9.x (startup.sh / startup.bat)
```
Перевірка в браузері:
- http://localhost:8080/sports-booking-server/facilities  
- http://localhost:8080/sports-booking-server/bookings

### 4.4. Клієнт
**Важливо:** запускати з каталогу `client` (відносний шлях до CSV):
```bash
cd client
mvn javafx:run
```

---

## 5. CSV-імпорт

Файл: `client/sample-data/time_slots.csv`

```
facility_id;slot_date;start_time;end_time;price
1;2026-11-02;10:00;12:00;450.0
...
```

- Виконується у фоновому `JavaFX Task`
- ProgressBar + текстовий статус
- Помилка одного рядка не зупиняє весь імпорт
- Є кнопка **Скасувати** (перевіряється `isCancelled()`)

---

## 6. Тести

```bash
cd server
mvn test
```

18 тестів покривають:
- нормування F1 та розрахунок I
- відхилення фактора > 100
- усі дозволені та один заборонений перехід станів
- валідацію Facility / AppUser / TimeSlot

Тести **не потребують** запущеного MySQL.

---

## 7. Подання на GitHub (за методичкою)

```bash
git init
git add .
git commit -m "Final coursework submission"
git remote add origin https://github.com/USERNAME/REPOSITORY.git
git push -u origin main

git tag -a coursework-final -m "Final coursework submission"
git push origin coursework-final
```

У пояснювальній записці вказати:
- посилання на репозиторій
- пряме посилання на commit з тегом `coursework-final`

---

## 8. Контрольний список перед захистом

- [ ] `database.sql` відтворює БД з нуля
- [ ] `mvn clean test` — усі тести зелені
- [ ] CRUD працює для 4 сутностей через HTTP/JSON
- [ ] Переходи станів бронювання контролюються
- [ ] Інтегральний показник рахується коректно
- [ ] CSV-імпорт з ProgressBar і можливістю скасування
- [ ] Немає абсолютних шляхів у коді
- [ ] Логування основних операцій (див. консоль / логи Tomcat)
- [ ] Пояснювальна записка + посилання на GitHub commit/tag

---

## 9. Приклад розрахунку I (для записки)

Учасників = 25 → F1 = 25/50×100 = **50**  
Тривалість = 108 хв → F2 = 108/180×100 = **60**  
Обладнання = **40**

\[
I = 0{,}40\cdot50 + 0{,}35\cdot60 + 0{,}25\cdot40 = 20 + 21 + 10 = 51{,}0
\]
