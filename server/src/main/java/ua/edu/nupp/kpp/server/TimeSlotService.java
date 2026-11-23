package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class TimeSlotService {
    private static final Logger LOG = Logger.getLogger(TimeSlotService.class.getName());
    private final TimeSlotDAO dao = new TimeSlotDAO();

    public List<TimeSlot> getAll() throws SQLException {
        return dao.findAll();
    }

    public TimeSlot getById(long id) throws SQLException {
        TimeSlot t = dao.findById(id);
        if (t == null) throw new IllegalArgumentException("Слот з id=" + id + " не знайдено");
        return t;
    }

    public TimeSlot create(TimeSlot t) throws SQLException {
        validate(t);
        TimeSlot created = dao.insert(t);
        LOG.info("Створено time_slot id=" + created.getId());
        return created;
    }

    public TimeSlot update(TimeSlot t) throws SQLException {
        if (t.getId() == null) throw new IllegalArgumentException("id обов’язковий для оновлення");
        validate(t);
        dao.update(t);
        LOG.info("Оновлено time_slot id=" + t.getId());
        return t;
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено time_slot id=" + id);
    }

    public static void validate(TimeSlot t) {
        if (t.getFacilityId() <= 0) {
            throw new IllegalArgumentException("facilityId має бути додатним");
        }
        if (t.getSlotDate() == null || t.getSlotDate().isBlank()) {
            throw new IllegalArgumentException("Дата слота обов’язкова (YYYY-MM-DD)");
        }
        if (t.getStartTime() == null || t.getStartTime().isBlank()) {
            throw new IllegalArgumentException("Час початку обов’язковий");
        }
        if (t.getEndTime() == null || t.getEndTime().isBlank()) {
            throw new IllegalArgumentException("Час кінця обов’язковий");
        }
        if (t.getPrice() < 0) {
            throw new IllegalArgumentException("Ціна не може бути від’ємною");
        }
    }
}
