package dao;

import model.Club;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClubDAO {
    
    private Club mapResultSetToClub(ResultSet rs) throws SQLException {
        Club club = new Club();
        club.setClub_id(rs.getInt("club_id"));
        club.setClub_name(rs.getString("club_name"));
        club.setClub_desc(rs.getString("club_desc"));
        club.setClub_presidentID(rs.getString("club_presidentID"));
        club.setLogo_path(rs.getString("logo_path"));
        try {
            if (rs.findColumn("club_category") > 0) {
                club.setClub_category(rs.getString("club_category"));
            }
        } catch (SQLException e) { /* Ignore */ }
        return club;
    }

    public void updateClubLogoPath(int clubId, String logoPath) throws SQLException {
        String sql = "UPDATE club SET logo_path = ? WHERE club_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, logoPath);
            stmt.setInt(2, clubId);
            stmt.executeUpdate();
        }
    }

    public List<Club> getAllClubs() throws SQLException {
        List<Club> clubs = new ArrayList<>();
        String sql = "SELECT * FROM club ORDER BY club_name ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clubs.add(mapResultSetToClub(rs));
            }
        }
        return clubs;
    }
    
    public List<Club> getJoinedClubs(String studentId) throws SQLException {
        List<Club> clubs = new ArrayList<>();
        String sql = "SELECT DISTINCT c.* FROM club c " +
                     "JOIN activity a ON c.club_id = a.club_id " +
                     "JOIN registration r ON a.activity_id = r.activity_id " +
                     "WHERE r.student_no = ? ORDER BY c.club_name ASC";
         try (Connection conn = DBConnection.getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clubs.add(mapResultSetToClub(rs));
                }
            }
        }
        return clubs;
    }
    
     public Club getClubByPresident(String studentId) throws SQLException {
        String sql = "SELECT * FROM club WHERE club_presidentID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToClub(rs);
                }
            }
        }
        return null;
    }

    public int getTotalClubsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM club";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public Club getClubById(int clubId) throws SQLException {
        String sql = "SELECT * FROM club WHERE club_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToClub(rs);
                }
            }
        }
        return null;
    }

    public void setClubPresident(String studentId, int clubId) throws SQLException {
        String sql = "UPDATE club SET club_presidentID = ? WHERE club_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setInt(2, clubId);
            stmt.executeUpdate();
        }
    }

    public void addClub(Club club) throws SQLException {
        String sql = "INSERT INTO club (club_name, club_desc, club_presidentID, logo_path, club_category) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, club.getClub_name());
            stmt.setString(2, club.getClub_desc());
            stmt.setString(3, club.getClub_presidentID());
            stmt.setString(4, club.getLogo_path());
            stmt.setString(5, club.getClub_category());
            stmt.executeUpdate();
        }
    }

    public void updateClub(Club club) throws SQLException {
        // logo path is handled by its own method
        String sql = "UPDATE club SET club_name = ?, club_desc = ?, club_presidentID = ?, club_category = ? WHERE club_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, club.getClub_name());
            stmt.setString(2, club.getClub_desc());
            stmt.setString(3, club.getClub_presidentID());
            stmt.setString(4, club.getClub_category());
            stmt.setInt(5, club.getClub_id());
            stmt.executeUpdate();
        }
    }
}
