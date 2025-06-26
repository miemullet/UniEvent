package dao;

import model.Achievement;
import model.Student;
import util.CertificateGenerator;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AchievementDAO {

    private static final String CERTIFICATE_SAVE_DIRECTORY = "C:\\Users\\ariff\\Documents\\NetBeansProjects\\UniEvent\\src\\main\\webapp\\uploads\\certificates";

    private Achievement mapResultSetToAchievement(ResultSet rs) throws SQLException {
        Achievement achievement = new Achievement();
        achievement.setAchievement_id(rs.getInt("achievement_id"));
        achievement.setStudent_no(rs.getString("student_no"));
        achievement.setActivity_id(rs.getInt("activity_id"));
        achievement.setTitle(rs.getString("title"));
        achievement.setDescription(rs.getString("description"));
        achievement.setCert_path(rs.getString("cert_path"));
        achievement.setDate_awarded(rs.getTimestamp("date_awarded"));
        return achievement;
    }
    
    public boolean hasAchievement(String studentNo, String title) throws SQLException {
        String sql = "SELECT COUNT(*) FROM achievement WHERE student_no = ? AND title = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentNo);
            stmt.setString(2, title);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean hasAchievementByTitle(String title) throws SQLException {
        String sql = "SELECT COUNT(*) FROM achievement WHERE title = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<Achievement> getAchievementsByStudent(String studentNo) throws SQLException {
        List<Achievement> achievements = new ArrayList<>();
        String sql = "SELECT * FROM achievement WHERE student_no = ? ORDER BY date_awarded DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentNo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    achievements.add(mapResultSetToAchievement(rs));
                }
            }
        }
        return achievements;
    }

    public List<Achievement> getAchievementsWithoutCertificates() throws SQLException {
        List<Achievement> achievements = new ArrayList<>();
        String sql = "SELECT * FROM achievement WHERE cert_path IS NULL OR cert_path = ''";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                achievements.add(mapResultSetToAchievement(rs));
            }
        }
        return achievements;
    }
    
    public List<Achievement> getAchievementsWithoutCertificatesForStudent(String studentNo) throws SQLException {
        List<Achievement> achievements = new ArrayList<>();
        String sql = "SELECT * FROM achievement WHERE (cert_path IS NULL OR cert_path = '') AND student_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentNo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    achievements.add(mapResultSetToAchievement(rs));
                }
            }
        }
        return achievements;
    }

    public void addAchievement(Achievement achievement) throws SQLException {
        String sql = "INSERT INTO achievement (student_no, activity_id, title, description, cert_path, date_awarded) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, achievement.getStudent_no());
            if (achievement.getActivity_id() > 0) {
                 stmt.setInt(2, achievement.getActivity_id());
            } else {
                 stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setString(3, achievement.getTitle());
            stmt.setString(4, achievement.getDescription());
            stmt.setNull(5, java.sql.Types.VARCHAR);
            stmt.setTimestamp(6, achievement.getDate_awarded());
            stmt.executeUpdate();
            
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int achievementId = generatedKeys.getInt(1);
                achievement.setAchievement_id(achievementId);

                try {
                    StudentDAO studentDAO = new StudentDAO();
                    Student student = studentDAO.getStudentById(achievement.getStudent_no());

                    if (student != null) {
                        String certFileName = "achievement_" + student.getStudent_no() + "_" + achievementId + ".pdf";
                        String dbCertPath = "uploads/certificates/" + certFileName;
                        
                        File certificateDir = new File(CERTIFICATE_SAVE_DIRECTORY);
                        if (!certificateDir.exists()) {
                            certificateDir.mkdirs();
                        }
                        String fullFilePath = CERTIFICATE_SAVE_DIRECTORY + File.separator + certFileName;

                        CertificateGenerator.createAchievementCertificate(student, achievement, fullFilePath);
                        updateCertificatePath(achievementId, dbCertPath);
                    }
                } catch (Exception e) {
                    System.err.println("Error generating achievement certificate: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException logOrIgnore) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException logOrIgnore) {}
            if (conn != null) try { conn.close(); } catch (SQLException logOrIgnore) {}
        }
    }

    public void updateCertificatePath(int achievementId, String certPath) throws SQLException {
        String sql = "UPDATE achievement SET cert_path = ? WHERE achievement_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, certPath);
            stmt.setInt(2, achievementId);
            stmt.executeUpdate();
        }
    }

    /**
     * [NEW] Deletes all achievements that match a specific title.
     * This is used to remove a semester's Top Merit Award before re-assigning it.
     * @param title The exact title of the achievements to delete.
     * @throws SQLException if a database error occurs.
     */
    public void deleteAchievementsByTitle(String title) throws SQLException {
        String sql = "DELETE FROM achievement WHERE title = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.executeUpdate();
        }
    }
}