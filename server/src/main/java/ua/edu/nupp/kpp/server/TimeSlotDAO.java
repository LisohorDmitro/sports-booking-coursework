package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAO {

    public List<TimeSlot> findAll() throws SQLException {
        List<TimeSlot> list = new ArrayList<>();
        String sql = "SELECT id, facility_id, slot_date, start_time, end_time, price FROM time_slot ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public TimeSlot findById(long id) throws SQLException {
        String sql = "SELECT id, facility_id, slot_date, start_time, end_time, price FROM time_slot WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public TimeSlot insert(TimeSlot t) throws SQLException {
        String sql = "INSERT INTO time_slot(facility_id, slot_date, start_time, end_time, price) VALUES (?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, t.getFacilityId());
            ps.setString(2, t.getSlotDate());
            ps.setString(3, t.getStartTime());
            ps.setString(4, t.getEndTime());
            ps.setDouble(5, t.getPrice());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) t.setId(keys.getLong(1));
            }
        }
        return t;
    }

    public void update(TimeSlot t) throws SQLException {
        String sql = "UPDATE time_slot SET facility_id=?, slot_date=?, start_time=?, end_time=?, price=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, t.getFacilityId());
            ps.setString(2, t.getSlotDate());
            ps.setString(3, t.getStartTime());
            ps.setString(4, t.getEndTime());
            ps.setDouble(5, t.getPrice());
            ps.setLong(6, t.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM time_slot WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private TimeSlot map(ResultSet rs) throws SQLException {
        return new TimeSlot(rs.getLong("id"), rs.getLong("facility_id"),
                rs.getString("slot_date"), rs.getString("start_time"),
                rs.getString("end_time"), rs.getDouble("price"));
    }
}
