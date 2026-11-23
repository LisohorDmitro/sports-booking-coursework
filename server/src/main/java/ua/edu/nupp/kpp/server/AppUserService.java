package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class AppUserService {
    private static final Logger LOG = Logger.getLogger(AppUserService.class.getName());
    private final AppUserDAO dao = new AppUserDAO();

    public List<AppUser> getAll() throws SQLException {
        return dao.findAll();
    }

    public AppUser getById(long id) throws SQLException {
        AppUser u = dao.findById(id);
        if (u == null) throw new IllegalArgumentException("Користувача з id=" + id + " не знайдено");
        return u;
    }

    public AppUser create(AppUser u) throws SQLException {
        validate(u);
        AppUser created = dao.insert(u);
        LOG.info("Створено користувача id=" + created.getId());
        return created;
    }

    public AppUser update(AppUser u) throws SQLException {
        if (u.getId() == null) throw new IllegalArgumentException("id обов’язковий для оновлення");
        validate(u);
        dao.update(u);
        LOG.info("Оновлено користувача id=" + u.getId());
        return u;
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено користувача id=" + id);
    }

    public static void validate(AppUser u) {
        if (u.getFullName() == null || u.getFullName().isBlank()) {
            throw new IllegalArgumentException("ПІБ обов’язкове");
        }
        if (u.getEmail() == null || u.getEmail().isBlank() || !u.getEmail().contains("@")) {
            throw new IllegalArgumentException("Коректний email обов’язковий");
        }
        if (u.getPhone() == null || u.getPhone().isBlank()) {
            throw new IllegalArgumentException("Телефон обов’язковий");
        }
    }
}
