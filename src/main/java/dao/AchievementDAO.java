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

/**
 * Data Access Object for handling all achievement-related database operations.
 * This includes adding, retrieving, and updating achievements, as well as
 * generating their corresponding certificates.
 *
 * @version 2.1
 * @author [Your Name/Team]
 */
public class AchievementDAO {

    // The relative path for storing certificates, used for database records.
    private static final String CERTIFICATE_DB_FOLDER = "uploads/certificates";

    /**
     * Maps a row from a ResultSet to an Achievement object.
     * @param rs The ResultSet to map.
     * @return An Achievement object.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Retrieves a specific achievement for a student by its title.
     * Used to check for unique awards like "Top Merit Award".
     * @param studentNo The student's ID.
     * @param title The exact title of the achievement.
     * @return The Achievement object if found, otherwise null.
     * @throws SQLException If a database error occurs.
     */
    public Achievement getAchievementByTitle(String studentNo, String title) throws SQLException {
        String sql = "SELECT * FROM achievement WHERE student_no = ? AND title = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentNo);
            stmt.setString(2, title);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAchievement(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Retrieves all achievements for a specific student, ordered by most recent.
     * @param studentNo The student's ID.
     * @return A list of Achievement objects.
     * @throws SQLException If a database error occurs.
     */
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

    /**
     * [NEW METHOD] Finds all achievements across all students that are missing a certificate path.
     * This method is used by the GenerateExistingCertsServlet to fix records retroactively.
     * @return A list of achievements that need a certificate generated.
     * @throws SQLException if a database access error occurs.
     */
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

    /**
     * Finds all achievements for a specific student that are missing a certificate path.
     * @param studentNo The student's ID.
     * @return A list of achievements that need a certificate generated.
     * @throws SQLException If a database error occurs.
     */
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

    /**
     * Adds a new achievement record to the database and immediately generates the certificate.
     * This is the primary method for creating achievements and their associated files.
     * @param achievement The Achievement object to add.
     * @param absoluteSavePath The absolute file system path (from ServletContext) where the certificate will be saved.
     * @throws SQLException If a database error occurs.
     */
    public void addAchievement(Achievement achievement, String absoluteSavePath) throws SQLException {
        String sql = "INSERT INTO achievement (student_no, activity_id, title, description, cert_path, date_awarded) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = DBConnection.getConnection();
            // We still set cert_path to null initially and update it after generation.
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, achievement.getStudent_no());
            if (achievement.getActivity_id() > 0) {
                 stmt.setInt(2, achievement.getActivity_id());
            } else {
                 stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setString(3, achievement.getTitle());
            stmt.setString(4, achievement.getDescription());
            stmt.setNull(5, java.sql.Types.VARCHAR); // Certificate path starts as NULL
            stmt.setTimestamp(6, achievement.getDate_awarded());
            
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating achievement failed, no rows affected.");
            }
            
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int achievementId = generatedKeys.getInt(1);
                achievement.setAchievement_id(achievementId);

                // --- Certificate Generation Step ---
                try {
                    StudentDAO studentDAO = new StudentDAO();
                    Student student = studentDAO.getStudentById(achievement.getStudent_no());

                    if (student != null) {
                        String certFileName = "achievement_" + student.getStudent_no() + "_" + achievementId + ".pdf";
                        String dbCertPath = CERTIFICATE_DB_FOLDER + "/" + certFileName;
                        
                        // Ensure the save directory exists
                        File saveDir = new File(absoluteSavePath);
                        if (!saveDir.exists()) {
                            if (!saveDir.mkdirs()) {
                                 System.err.println("FATAL: Could not create certificate directory at " + absoluteSavePath);
                                 return; // Stop if we can't create the directory
                            }
                        }
                        
                        String fullFilePath = absoluteSavePath + File.separator + certFileName;
                        
                        // Generate the certificate PDF
                        CertificateGenerator.createAchievementCertificate(student, achievement, fullFilePath);
                        
                        // Update the database record with the new path
                        updateCertificatePath(achievementId, dbCertPath);
                        System.out.println("Successfully generated and linked certificate: " + fullFilePath);
                    }
                } catch (Exception e) {
                    System.err.println("Error generating achievement certificate PDF for achievement ID " + achievementId + ": " + e.getMessage());
                    e.printStackTrace();
                    // The achievement is in the DB, but without a cert. It can be regenerated later.
                }
            }
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException logOrIgnore) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException logOrIgnore) {}
            if (conn != null) try { conn.close(); } catch (SQLException logOrIgnore) {}
        }
    }

    /**
     * Updates the certificate path for a given achievement.
     * @param achievementId The ID of the achievement to update.
     * @param certPath The new relative path to the certificate file.
     * @throws SQLException If a database error occurs.
     */
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
     * Deletes all achievements that match a specific title. Used to ensure unique system-wide awards.
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
