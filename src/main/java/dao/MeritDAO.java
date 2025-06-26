package dao;

import model.MeritEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MeritDAO {

    /**
     * Maps a row from a ResultSet to a MeritEntry object.
     * @param rs The ResultSet to map.
     * @return A MeritEntry object.
     * @throws SQLException If a database access error occurs.
     */
    private MeritEntry mapResultSetToMeritEntry(ResultSet rs) throws SQLException {
        MeritEntry meritEntry = new MeritEntry();
        meritEntry.setMerit_entry_id(rs.getInt("merit_entry_id"));
        meritEntry.setStudent_no(rs.getString("student_no"));
        meritEntry.setActivity_id(rs.getInt("activity_id"));
        meritEntry.setMerit_points(rs.getInt("merit_points"));
        meritEntry.setRemarks(rs.getString("remarks"));
        meritEntry.setMerit_date(rs.getTimestamp("merit_date"));
        
        // Joined column (check if column exists)
        try {
            if (rs.findColumn("activity_name") > 0) {
                meritEntry.setActivity_name(rs.getString("activity_name"));
            }
        } catch (SQLException e) { /* Ignore if column not found */ }

        return meritEntry;
    }

    /**
     * [UPDATED] Adds a new merit entry to the database and sets an expiry date
     * based on the current semester (March-August or Sept-Feb).
     * @param meritEntry The MeritEntry object to add.
     * @throws SQLException If a database access error occurs.
     */
    public void addMeritEntry(MeritEntry meritEntry) throws SQLException {
        String sql = "INSERT INTO merit_history (student_no, activity_id, merit_points, remarks, merit_date, merit_expiry_date) VALUES (?, ?, ?, ?, NOW(), ?)";
        
        // Calculate expiry date based on semester
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH); // Calendar.MARCH is 2, Calendar.AUGUST is 7

        // March to August Semester
        if (currentMonth >= Calendar.MARCH && currentMonth <= Calendar.AUGUST) {
            cal.set(Calendar.MONTH, Calendar.AUGUST);
            cal.set(Calendar.DAY_OF_MONTH, 31);
        } 
        // September to February Semester
        else {
            // If it's Sep-Dec, expiry is next year's Feb
            if (currentMonth >= Calendar.SEPTEMBER) {
                cal.add(Calendar.YEAR, 1);
            }
            cal.set(Calendar.MONTH, Calendar.FEBRUARY);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH)); // Handles leap years
        }

        // Set time to the very end of the expiry day
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        
        Timestamp expiryDate = new Timestamp(cal.getTimeInMillis());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, meritEntry.getStudent_no());
            if (meritEntry.getActivity_id() > 0) {
                stmt.setInt(2, meritEntry.getActivity_id());
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setInt(3, meritEntry.getMerit_points());
            stmt.setString(4, meritEntry.getRemarks());
            stmt.setTimestamp(5, expiryDate);
            
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all merit entries for a specific student, including associated activity name.
     * This method now returns all historical entries, regardless of expiry.
     * @param studentNo The student's ID.
     * @return A list of MeritEntry objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<MeritEntry> getMeritHistoryByStudent(String studentNo) throws SQLException {
        List<MeritEntry> meritHistory = new ArrayList<>();
        String sql = "SELECT mh.*, a.activity_name FROM merit_history mh " +
                     "LEFT JOIN activity a ON mh.activity_id = a.activity_id " +
                     "WHERE mh.student_no = ? ORDER BY mh.merit_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentNo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    meritHistory.add(mapResultSetToMeritEntry(rs));
                }
            }
        }
        return meritHistory;
    }

    /**
     * [NEW] Calculates the total valid merit for a student.
     * It only sums points from entries that have not expired.
     * @param studentId The ID of the student.
     * @return The total valid merit score.
     * @throws SQLException if a database error occurs.
     */
    public int getCurrentValidMerit(String studentId) throws SQLException {
        int totalMerit = 0;
        String sql = "SELECT SUM(merit_points) AS total_valid_merit FROM merit_history WHERE student_no = ? AND merit_expiry_date > NOW()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalMerit = rs.getInt("total_valid_merit");
                }
            }
        }
        return totalMerit;
    }
}
