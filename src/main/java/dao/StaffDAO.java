package dao;

import model.Staff;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffDAO {

    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setHep_staffid(rs.getString("hep_staffid"));
        staff.setHep_staffname(rs.getString("hep_staffname"));
        staff.setHep_staffemail(rs.getString("hep_staffemail"));
        staff.setHep_staffphonenum(rs.getString("hep_staffphonenum"));
        staff.setHep_staffpassword(rs.getString("hep_staffpassword"));
        staff.setHep_staffadminstatus(rs.getBoolean("hep_staffadminstatus"));
        staff.setStaff_role(rs.getString("hep_staffrole"));
        staff.setHep_staff_image_path(rs.getString("hep_staff_image_path")); // Mapped new field
        return staff;
    }
    
    public void updateStaffImagePath(String staffId, String imagePath) throws SQLException {
        String sql = "UPDATE hep_staff SET hep_staff_image_path = ? WHERE hep_staffid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, imagePath);
            stmt.setString(2, staffId);
            stmt.executeUpdate();
        }
    }

    public Staff login(String staffId, String password) throws SQLException {
        String sql = "SELECT * FROM hep_staff WHERE hep_staffid = ? AND hep_staffpassword = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staffId);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        }
        return null;
    }

    public Staff getStaffById(String staffId) throws SQLException {
        String sql = "SELECT * FROM hep_staff WHERE hep_staffid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staffId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        }
        return null;
    }

    public void addStaff(Staff staff) throws SQLException {
        // hep_staff_image_path is not included as it's null on registration
        String sql = "INSERT INTO hep_staff (hep_staffid, hep_staffname, hep_staffemail, hep_staffphonenum, hep_staffpassword, hep_staffadminstatus, hep_staffrole) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staff.getHep_staffid());
            stmt.setString(2, staff.getHep_staffname());
            stmt.setString(3, staff.getHep_staffemail());
            stmt.setString(4, staff.getHep_staffphonenum());
            stmt.setString(5, staff.getHep_staffpassword());
            stmt.setBoolean(6, staff.isHep_staffadminstatus());
            stmt.setString(7, staff.getStaff_role());
            stmt.executeUpdate();
        }
    }

    public void updateStaff(Staff staff) throws SQLException {
        // image path is updated by its own method
        String sql = "UPDATE hep_staff SET hep_staffname = ?, hep_staffemail = ?, hep_staffphonenum = ?, hep_staffpassword = ? WHERE hep_staffid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, staff.getHep_staffname());
            stmt.setString(2, staff.getHep_staffemail());
            stmt.setString(3, staff.getHep_staffphonenum());
            stmt.setString(4, staff.getHep_staffpassword());
            stmt.setString(5, staff.getHep_staffid());
            stmt.executeUpdate();
        }
    }
}
