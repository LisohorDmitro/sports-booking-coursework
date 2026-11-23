package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public List<Booking> findAll() throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT id, user_id, time_slot_id, state, f1, f2, f3, integral_index FROM booking ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Booking findById(long id) throws SQLException {
        String sql = "SELECT id, user_id, time_slot_id, state, f1, f2, f3, integral_index FROM booking WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public Booking insert(Booking b) throws SQLException {
        String sql = "INSERT INTO booking(user_id, time_slot_id, state, f1, f2, f3, integral_index) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, b.getUserId());
            ps.setLong(2, b.getTimeSlotId());
            ps.setString(3, b.getState().name());
            ps.setDouble(4, b.getF1());
            ps.setDouble(5, b.getF2());
            ps.setDouble(6, b.getF3());
            ps.setDouble(7, b.getIntegralIndex());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) b.setId(keys.getLong(1));
            }
        }
        return b;
    }

    public void update(Booking b) throws SQLException {
        String sql = "UPDATE booking SET user_id=?, time_slot_id=?, state=?, f1=?, f2=?, f3=?, integral_index=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, b.getUserId());
            ps.setLong(2, b.getTimeSlotId());
            ps.setString(3, b.getState().name());
            ps.setDouble(4, b.getF1());
            ps.setDouble(5, b.getF2());
            ps.setDouble(6, b.getF3());
            ps.setDouble(7, b.getIntegralIndex());
            ps.setLong(8, b.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM booking WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private Booking map(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getLong("time_slot_id"),
                BookingState.valueOf(rs.getString("state")),
                rs.getDouble("f1"),
                rs.getDouble("f2"),
                rs.getDouble("f3"),
                rs.getDouble("integral_index")
        );
    }
}
