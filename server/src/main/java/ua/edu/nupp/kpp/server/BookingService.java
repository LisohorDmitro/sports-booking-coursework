package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class BookingService {
    private static final Logger LOG = Logger.getLogger(BookingService.class.getName());
    private final BookingDAO dao = new BookingDAO();

    public List<Booking> getAll() throws SQLException {
        return dao.findAll();
    }

    public Booking getById(long id) throws SQLException {
        Booking b = dao.findById(id);
        if (b == null) throw new IllegalArgumentException("Бронювання з id=" + id + " не знайдено");
        return b;
    }

    public Booking create(long userId, long timeSlotId, double f1, double f2, double f3) throws SQLException {
        if (f1 < 0 || f1 > 100 || f2 < 0 || f2 > 100 || f3 < 0 || f3 > 100) {
            throw new IllegalArgumentException("Фактори F1–F3 мають бути в діапазоні 0–100");
        }
        double index = RiskCalculator.calculate(f1, f2, f3);
        Booking b = new Booking(null, userId, timeSlotId, BookingState.CREATED, f1, f2, f3, index);
        Booking created = dao.insert(b);
        LOG.info("Створено бронювання id=" + created.getId() + ", I=" + index);
        return created;
    }

    public Booking recalculate(long id, double f1, double f2, double f3) throws SQLException {
        Booking b = getById(id);
        if (f1 < 0 || f1 > 100 || f2 < 0 || f2 > 100 || f3 < 0 || f3 > 100) {
            throw new IllegalArgumentException("Фактори F1–F3 мають бути в діапазоні 0–100");
        }
        b.setF1(f1);
        b.setF2(f2);
        b.setF3(f3);
        b.setIntegralIndex(RiskCalculator.calculate(f1, f2, f3));
        dao.update(b);
        LOG.info("Перераховано бронювання id=" + id + ", I=" + b.getIntegralIndex());
        return b;
    }

    public Booking changeState(long id, BookingState newState) throws SQLException {
        Booking b = getById(id);
        if (!isAllowedTransition(b.getState(), newState)) {
            throw new IllegalArgumentException(
                    "Недопустимий перехід: " + b.getState() + " → " + newState);
        }
        b.setState(newState);
        dao.update(b);
        LOG.info("Змінено стан бронювання id=" + id + " на " + newState);
        return b;
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено бронювання id=" + id);
    }

    public static boolean isAllowedTransition(BookingState from, BookingState to) {
        if (from == null || to == null) return false;
        return switch (from) {
            case CREATED -> to == BookingState.CONFIRMED;
            case CONFIRMED -> to == BookingState.ACTIVE;
            case ACTIVE -> to == BookingState.COMPLETED;
            case COMPLETED -> false;
        };
    }
}
