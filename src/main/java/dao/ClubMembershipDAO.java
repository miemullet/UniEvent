package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * [NEW FILE]
 * Data Access Object for handling club membership operations.
 */
public class ClubMembershipDAO {

    /**
     * Adds a student to a club in the club_membership table.
     * @param studentId The ID of the student joining.
     * @param clubId The ID of the club being joined.
     * @throws SQLException if a database error occurs.
     */
    public void joinClub(String studentId, int clubId) throws SQLException {
        String sql = "INSERT INTO club_membership (student_no, club_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setInt(2, clubId);
            stmt.executeUpdate();
        }
    }

    /**
     * Checks if a student is already a member of a specific club.
     * @param studentId The ID of the student.
     * @param clubId The ID of the club.
     * @return true if the student is a member, false otherwise.
     * @throws SQLException if a database error occurs.
     */
    public boolean isMember(String studentId, int clubId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM club_membership WHERE student_no = ? AND club_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setInt(2, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Retrieves a set of all club IDs that a specific student has joined.
     * @param studentId The ID of the student.
     * @return A Set of integer club IDs.
     * @throws SQLException if a database error occurs.
     */
    public Set<Integer> getJoinedClubIds(String studentId) throws SQLException {
        Set<Integer> joinedClubIds = new HashSet<>();
        String sql = "SELECT club_id FROM club_membership WHERE student_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    joinedClubIds.add(rs.getInt("club_id"));
                }
            }
        }
        return joinedClubIds;
    }
}
