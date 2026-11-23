package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppUserDAO {

    public List<AppUser> findAll() throws SQLException {
        List<AppUser> list = new ArrayList<>();
        String sql = "SELECT id, full_name, email, phone FROM app_user ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public AppUser findById(long id) throws SQLException {
        String sql = "SELECT id, full_name, email, phone FROM app_user WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public AppUser insert(AppUser u) throws SQLException {
        String sql = "INSERT INTO app_user(full_name, email, phone) VALUES (?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPhone());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getLong(1));
            }
        }
        return u;
    }

    public void update(AppUser u) throws SQLException {
        String sql = "UPDATE app_user SET full_name=?, email=?, phone=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPhone());
            ps.setLong(4, u.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM app_user WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private AppUser map(ResultSet rs) throws SQLException {
        return new AppUser(rs.getLong("id"), rs.getString("full_name"),
                rs.getString("email"), rs.getString("phone"));
    }
}
