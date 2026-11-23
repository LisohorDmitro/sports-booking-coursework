package ua.edu.nupp.kpp.server;

/**
 * Розрахунок інтегрального показника бронювання.
 *
 * I = 0,40·F1 + 0,35·F2 + 0,25·F3
 *
 * Правила нормування (описані також у пояснювальній записці):
 * - F1 (кількість учасників): participants / 50 × 100, обмеження 0–100
 * - F2 (тривалість, хв): minutes / 180 × 100, обмеження 0–100
 * - F3 (потреба в обладнанні): уже в діапазоні 0–100
 */
public final class RiskCalculator {

    private RiskCalculator() {}

    /** F1 — кількість учасників, нормована до 0–100 (max = 50 осіб). */
    public static double normalizeParticipants(int participants) {
        if (participants < 0) {
            throw new IllegalArgumentException("Кількість учасників не може бути від’ємною");
        }
        double f1 = Math.min(100.0, participants / 50.0 * 100.0);
        return Math.round(f1 * 10.0) / 10.0;
    }

    /** F2 — тривалість у хвилинах, нормована до 0–100 (max = 180 хв). */
    public static double normalizeDuration(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Тривалість не може бути від’ємною");
        }
        double f2 = Math.min(100.0, minutes / 180.0 * 100.0);
        return Math.round(f2 * 10.0) / 10.0;
    }

    /** F3 — потреба в додатковому обладнанні (вже 0–100). */
    public static double normalizeEquipmentNeed(double need) {
        if (need < 0 || need > 100) {
            throw new IllegalArgumentException("F3 має бути в діапазоні 0–100");
        }
        return need;
    }

    public static double calculate(double f1, double f2, double f3) {
        if (f1 < 0 || f1 > 100 || f2 < 0 || f2 > 100 || f3 < 0 || f3 > 100) {
            throw new IllegalArgumentException("Фактори F1–F3 мають бути в діапазоні 0–100");
        }
        double i = 0.40 * f1 + 0.35 * f2 + 0.25 * f3;
        return Math.round(i * 10.0) / 10.0;
    }
}
