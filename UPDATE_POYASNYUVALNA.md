# Що оновити в пояснювальній записці перед поданням

## 1. Блок «ВІДОМОСТІ ПРО ПРОГРАМНУ РЕАЛІЗАЦІЮ»

Замінити плейсхолдери на реальні значення **після** створення tag:

```
GitHub-репозиторій програмної реалізації:
https://github.com/<ВАШ_USERNAME>/sports-booking-coursework

Фінальна версія для оцінювання (commit з тегом coursework-final):
https://github.com/<ВАШ_USERNAME>/sports-booking-coursework/commit/<SHA>
```

Або коротше:
https://github.com/<ВАШ_USERNAME>/sports-booking-coursework/releases/tag/coursework-final

## 2. Розділ про тести (розділ 5)

Оновити кількість тестів:

> Клас CourseworkTest містить **18 тестів**. Вони охоплюють:
> - нормування F1 (0, 25, 50, >50 → 100)
> - нормування F2 (90 хв → 50, 180 хв → 100)
> - розрахунок I = 51,0 та I = 31,0
> - відхилення фактора > 100 і від’ємного
> - три дозволені переходи життєвого циклу
> - два заборонені (CREATED→COMPLETED, COMPLETED→…)
> - валідацію Facility, AppUser, TimeSlot

Таблицю тестів можна залишити / розширити.

## 3. Розділ про CSV (розділ 4.1)

Додати/підкреслити:

- перевірка заголовка `facility_id;slot_date;start_time;end_time;price`
- UTF-8
- відносний шлях `Path.of("sample-data", "time_slots.csv")`
- помилки рядків не зупиняють імпорт
- мережеві помилки фіксуються в Logger
- ProgressBar прив’язаний до `progressProperty()`, статус — до `messageProperty()`
- кнопка «Скасувати» викликає `task.cancel()` і перевіряє `isCancelled()`

## 4. Висновки

Залишити як є — вони вже відповідають реальному стану проєкту.

## 5. Додатки

Переконайтеся, що скріншоти JavaFX актуальні (вкладки Об’єкти / Користувачі / Слоти+CSV / Бронювання).

---

Після оновлення ПЗ:
1. Збережіть файл.
2. Зробіть фінальний commit.
3. `git tag -a coursework-final -m "Final Stage IV submission"`
4. `git push origin main --tags`
5. Вставте реальні посилання в ПЗ і зробіть ще один commit (або amend, якщо ще не пушили).
