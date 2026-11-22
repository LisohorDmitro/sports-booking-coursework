package ua.edu.nupp.kpp.client;

import javafx.concurrent.Task;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

/**
 * Фоновий імпорт часових слотів з CSV (Етап IV).
 * Формат: facility_id;slot_date;start_time;end_time;price
 * (перший рядок — заголовок, роздільник — крапка з комою, UTF-8).
 *
 * Кожен коректний рядок надсилається на сервер через HttpSportsClient.
 * Некоректні рядки пропускаються з WARNING у Logger; мережеві/серверні
 * помилки також фіксуються, імпорт продовжується.
 */
public class ImportTimeSlotsTask extends Task<Integer> {

    private static final Logger LOG = Logger.getLogger(ImportTimeSlotsTask.class.getName());
    private static final String EXPECTED_HEADER =
            "facility_id;slot_date;start_time;end_time;price";

    private final Path csvPath;
    private final HttpSportsClient api;

    public ImportTimeSlotsTask(Path csvPath, HttpSportsClient api) {
        this.csvPath = csvPath;
        this.api = api;
    }

    @Override
    protected Integer call() throws Exception {
        if (!Files.exists(csvPath)) {
            String msg = "CSV-файл не знайдено: " + csvPath.toAbsolutePath();
            updateMessage(msg);
            LOG.severe(msg);
            throw new IllegalArgumentException(msg);
        }

        List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            updateMessage("Файл порожній");
            return 0;
        }

        // Перевірка заголовка (вимога Етапу IV)
        String header = lines.get(0).trim().toLowerCase().replace(" ", "");
        String expected = EXPECTED_HEADER.toLowerCase();
        if (!header.equals(expected)) {
            String msg = "Некоректний заголовок CSV. Очікується: " + EXPECTED_HEADER
                    + " | Отримано: " + lines.get(0).trim();
            updateMessage(msg);
            LOG.severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (lines.size() <= 1) {
            updateMessage("Файл містить лише заголовок");
            return 0;
        }

        int total = lines.size() - 1;
        int imported = 0;
        int errors = 0;

        for (int i = 1; i < lines.size(); i++) {
            if (isCancelled()) {
                updateMessage("Скасовано користувачем. Імпортовано: " + imported);
                LOG.info("CSV import cancelled after " + imported + " rows");
                break;
            }

            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                updateProgress(i, total);
                continue;
            }

            try {
                String[] parts = line.split(";", -1);
                if (parts.length < 5) {
                    errors++;
                    LOG.warning("Рядок " + (i + 1) + ": недостатньо полів (" + parts.length + ")");
                } else {
                    long facilityId = Long.parseLong(parts[0].trim());
                    String slotDate = parts[1].trim();
                    String startTime = parts[2].trim();
                    String endTime = parts[3].trim();
                    double price = Double.parseDouble(parts[4].trim());

                    if (slotDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                        throw new IllegalArgumentException("порожні обов'язкові поля");
                    }

                    TimeSlot slot = new TimeSlot(null, facilityId, slotDate, startTime, endTime, price);
                    api.addTimeSlot(slot);
                    imported++;
                    LOG.info("Імпортовано рядок " + (i + 1) + ": facility=" + facilityId
                            + ", " + slotDate + " " + startTime + "-" + endTime);
                }
            } catch (Exception e) {
                errors++;
                LOG.warning("Рядок " + (i + 1) + ": " + e.getMessage());
            }

            updateProgress(i, total);
            updateMessage(String.format("Оброблено %d/%d · успішно %d · помилок %d",
                    i, total, imported, errors));

            // Невелика пауза, щоб ProgressBar був помітним на коротких файлах
            Thread.sleep(80);
        }

        if (!isCancelled()) {
            updateMessage(String.format("Готово. Успішно: %d, помилок: %d", imported, errors));
        }
        LOG.info("CSV import finished: ok=" + imported + ", errors=" + errors);
        return imported;
    }
}
