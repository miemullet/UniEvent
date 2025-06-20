package dao;

import model.Registration;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {
    
    private Registration mapResultSetToRegistration(ResultSet rs) throws SQLException {
        Registration reg = new Registration();
        reg.setRegistration_id(rs.getInt("registration_id"));
        reg.setStudent_no(rs.getString("student_no"));
        reg.setActivity_id(rs.getInt("activity_id"));
        reg.setRegistration_date(rs.getTimestamp("registration_date"));
        reg.setRegistration_status(rs.getString("registration_status"));
        try {
            if (rs.findColumn("cert_path") > 0) {
                 reg.setCert_path(rs.getString("cert_path"));
            }
        } catch (SQLException e) { /* Ignore */ }
        return reg;
    }

    /**
     * [UPDATED] Retrieves all of a student's registrations for APPROVED or COMPLETED events 
     * that are missing a participation certificate.
     * @param studentNo The ID of the student.
     * @return A list of registration records.
     * @throws SQLException if a database error occurs.
     */
    public List<Registration> getRegistrationsMissingCertificates(String studentNo) throws SQLException {
        List<Registration> registrations = new ArrayList<>();
        // UPDATED SQL: Now checks for 'APPROVED' OR 'COMPLETED' status
        String sql = "SELECT r.* FROM registration r " +
                     "JOIN activity a ON r.activity_id = a.activity_id " +
                     "WHERE r.student_no = ? AND (a.activity_status = 'COMPLETED' OR a.activity_status = 'APPROVED') " +
                     "AND (r.cert_path IS NULL OR r.cert_path = '')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentNo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
        }
        return registrations;
    }
    
    // ... rest of the existing methods ...
    
    public void registerStudentForActivity(String studentNo, int activityId) throws SQLException {
        String sql = "INSERT INTO registration (student_no, activity_id, registration_date) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentNo);
            stmt.setInt(2, activityId);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
        }
    }

    public boolean isStudentRegisteredForActivity(String studentNo, int activityId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM registration WHERE student_no = ? AND activity_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentNo);
            stmt.setInt(2, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void updateCertificatePath(int registrationId, String certPath) throws SQLException {
        String sql = "UPDATE registration SET cert_path = ? WHERE registration_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, certPath);
            stmt.setInt(2, registrationId);
            stmt.executeUpdate();
        }
    }

    public void updateCertificatePathByStudentAndActivity(String studentNo, int activityId, String certPath) throws SQLException {
        String sql = "UPDATE registration SET cert_path = ? WHERE student_no = ? AND activity_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, certPath);
            stmt.setString(2, studentNo);
            stmt.setInt(3, activityId);
            stmt.executeUpdate();
        }
    }
    
    public int getRegistrationId(String studentNo, int activityId) throws SQLException {
        String sql = "SELECT registration_id FROM registration WHERE student_no = ? AND activity_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentNo);
            stmt.setInt(2, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("registration_id");
                }
            }
        }
        return 0;
    }
    
    public List<Integer> getRegistrationIdsForActivity(int activityId) throws SQLException {
        List<Integer> registrationIds = new ArrayList<>();
        String sql = "SELECT registration_id FROM registration WHERE activity_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrationIds.add(rs.getInt("registration_id"));
                }
            }
        }
        return registrationIds;
    }
}
