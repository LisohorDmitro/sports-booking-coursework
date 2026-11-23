package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class FacilityService {
    private static final Logger LOG = Logger.getLogger(FacilityService.class.getName());
    private final FacilityDAO dao = new FacilityDAO();

    public List<Facility> getAll() throws SQLException {
        return dao.findAll();
    }

    public Facility getById(long id) throws SQLException {
        Facility f = dao.findById(id);
        if (f == null) throw new IllegalArgumentException("Об’єкт з id=" + id + " не знайдено");
        return f;
    }

    public Facility create(Facility f) throws SQLException {
        validate(f);
        Facility created = dao.insert(f);
        LOG.info("Створено facility id=" + created.getId());
        return created;
    }

    public Facility update(Facility f) throws SQLException {
        if (f.getId() == null) throw new IllegalArgumentException("id обов’язковий для оновлення");
        validate(f);
        dao.update(f);
        LOG.info("Оновлено facility id=" + f.getId());
        return f;
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено facility id=" + id);
    }

    public static void validate(Facility f) {
        if (f.getCode() == null || f.getCode().isBlank()) {
            throw new IllegalArgumentException("Код об’єкта обов’язковий");
        }
        if (f.getName() == null || f.getName().isBlank()) {
            throw new IllegalArgumentException("Назва об’єкта обов’язкова");
        }
        if (f.getAddress() == null || f.getAddress().isBlank()) {
            throw new IllegalArgumentException("Адреса обов’язкова");
        }
    }
}
