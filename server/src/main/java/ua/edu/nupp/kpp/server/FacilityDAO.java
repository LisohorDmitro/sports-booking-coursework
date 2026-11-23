package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacilityDAO {

    public List<Facility> findAll() throws SQLException {
        List<Facility> list = new ArrayList<>();
        String sql = "SELECT id, code, name, address FROM facility ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Facility findById(long id) throws SQLException {
        String sql = "SELECT id, code, name, address FROM facility WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public Facility insert(Facility f) throws SQLException {
        String sql = "INSERT INTO facility(code, name, address) VALUES (?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getCode());
            ps.setString(2, f.getName());
            ps.setString(3, f.getAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) f.setId(keys.getLong(1));
            }
        }
        return f;
    }

    public void update(Facility f) throws SQLException {
        String sql = "UPDATE facility SET code=?, name=?, address=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, f.getCode());
            ps.setString(2, f.getName());
            ps.setString(3, f.getAddress());
            ps.setLong(4, f.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM facility WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Facility map(ResultSet rs) throws SQLException {
        return new Facility(rs.getLong("id"), rs.getString("code"),
                rs.getString("name"), rs.getString("address"));
    }
}
