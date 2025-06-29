package dao;

import model.Activity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityDAO {

    private Activity mapResultSetToActivity(ResultSet rs) throws SQLException {
        Activity activity = new Activity();
        activity.setActivity_id(rs.getInt("activity_id"));
        activity.setActivity_name(rs.getString("activity_name"));
        activity.setActivity_desc(rs.getString("activity_desc"));
        activity.setActivity_location(rs.getString("activity_location"));
        activity.setActivity_startdate(rs.getTimestamp("activity_startdate"));
        activity.setActivity_enddate(rs.getTimestamp("activity_enddate"));
        activity.setActivity_status(rs.getString("activity_status"));
        activity.setParticipant_limit(rs.getInt("participant_limit"));
        activity.setOrganizerid(rs.getString("organizerid"));
        activity.setClub_id(rs.getInt("club_id"));
        activity.setHepstaffid(rs.getString("hepstaffid"));
        activity.setCategory_id(rs.getInt("category_id"));
        activity.setImage_path(rs.getString("image_path"));
        activity.setActivity_objectives(rs.getString("activity_objectives"));
        activity.setTarget_audience(rs.getString("target_audience"));
        activity.setCommittee_list(rs.getString("committee_list"));
        activity.setProgram_flow_path(rs.getString("program_flow_path"));
        activity.setBudget_path(rs.getString("budget_path"));
        activity.setTotal_budget(rs.getDouble("total_budget"));
        activity.setPromotion_strategy(rs.getString("promotion_strategy"));
        activity.setReport_path(rs.getString("report_path"));
        activity.setActivity_fee(rs.getDouble("activity_fee"));
        activity.setActivity_merit(rs.getInt("activity_merit"));

        try {
            if (rs.findColumn("club_name") > 0) {
                activity.setClub_name(rs.getString("club_name"));
            }
        } catch (SQLException e) { /* Ignore */ }
        try {
            if (rs.findColumn("category_name") > 0) {
                activity.setCategory_name(rs.getString("category_name"));
            }
        } catch (SQLException e) { /* Ignore */ }
        try {
            if (rs.findColumn("avg_rating") > 0) {
                activity.setAverageRating(rs.getDouble("avg_rating"));
            }
        } catch (SQLException e) { /* Ignore */ }
        try {
            if (rs.findColumn("registration_cert_path") > 0) {
                activity.setRegistration_cert_path(rs.getString("registration_cert_path"));
            }
        } catch (SQLException e) { /* Ignore */ }
        return activity;
    }
    
    public List<Activity> getTopRatedActivities(int limit) throws SQLException {
        List<Activity> topActivities = new ArrayList<>();
        String sql = "SELECT a.activity_id, a.activity_name, c.club_name, AVG(f.feedback_rating) as avg_rating " +
                     "FROM activity a " +
                     "JOIN feedback f ON a.activity_id = f.activity_id " +
                     "JOIN club c ON a.club_id = c.club_id " +
                     "WHERE a.activity_status = 'COMPLETED' " +
                     "GROUP BY a.activity_id, a.activity_name, c.club_name " +
                     "ORDER BY avg_rating DESC " +
                     "LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Activity activity = new Activity();
                    activity.setActivity_id(rs.getInt("activity_id"));
                    activity.setActivity_name(rs.getString("activity_name"));
                    activity.setClub_name(rs.getString("club_name"));
                    activity.setAverageRating(rs.getDouble("avg_rating"));
                    topActivities.add(activity);
                }
            }
        }
        return topActivities;
    }

    public void addActivity(Activity activity) throws SQLException {
        String sql = "INSERT INTO activity (activity_name, activity_desc, activity_location, activity_startdate, activity_enddate, " +
                     "activity_status, participant_limit, organizerid, club_id, category_id, image_path, " +
                     "activity_objectives, target_audience, committee_list, program_flow_path, budget_path, " +
                     "total_budget, promotion_strategy, activity_fee, activity_merit) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, activity.getActivity_name());
            stmt.setString(2, activity.getActivity_desc());
            stmt.setString(3, activity.getActivity_location());
            stmt.setTimestamp(4, activity.getActivity_startdate());
            stmt.setTimestamp(5, activity.getActivity_enddate());
            stmt.setString(6, activity.getActivity_status());
            stmt.setInt(7, activity.getParticipant_limit());
            stmt.setString(8, activity.getOrganizerid());
            stmt.setInt(9, activity.getClub_id());
            stmt.setInt(10, activity.getCategory_id());
            stmt.setString(11, activity.getImage_path());
            stmt.setString(12, activity.getActivity_objectives());
            stmt.setString(13, activity.getTarget_audience());
            stmt.setString(14, activity.getCommittee_list());
            stmt.setString(15, activity.getProgram_flow_path());
            stmt.setString(16, activity.getBudget_path());
            stmt.setDouble(17, activity.getTotal_budget());
            stmt.setString(18, activity.getPromotion_strategy());
            stmt.setDouble(19, activity.getActivity_fee());
            stmt.setInt(20, activity.getActivity_merit());
            stmt.executeUpdate();
        }
    }

    public void updateActivityStatus(int activityId, String status, String hepStaffId) throws SQLException {
        String sql = "UPDATE activity SET activity_status = ?, hepstaffid = ? WHERE activity_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, hepStaffId);
            stmt.setInt(3, activityId);
            stmt.executeUpdate();
        }
    }
    
    public void deleteActivity(int activityId) throws SQLException {
        String sql = "DELETE FROM activity WHERE activity_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, activityId);
            stmt.executeUpdate();
        }
    }

    public List<Activity> getActivitiesByClub(int clubId) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT a.*, c.club_name, cat.category_name FROM activity a " +
                     "JOIN club c ON a.club_id = c.club_id " +
                     "JOIN category cat ON a.category_id = cat.category_id " +
                     "WHERE a.club_id = ? ORDER BY a.activity_startdate DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(mapResultSetToActivity(rs));
                }
            }
        }
        return activities;
    }
    
    public List<Activity> getPastEventsByClub(int clubId) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT a.*, c.club_name, cat.category_name FROM activity a " +
                     "JOIN club c ON a.club_id = c.club_id " +
                     "JOIN category cat ON a.category_id = cat.category_id " +
                     "WHERE a.club_id = ? AND a.activity_enddate < NOW() ORDER BY a.activity_startdate DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(mapResultSetToActivity(rs));
                }
            }
        }
        return activities;
    }

    public List<Activity> getApprovedUpcomingEvents() throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT a.*, c.club_name, cat.category_name FROM activity a " +
                     "JOIN club c ON a.club_id = c.club_id " +
                     "JOIN category cat ON a.category_id = cat.category_id " +
                     "WHERE a.activity_startdate > NOW() AND a.activity_status = 'APPROVED' ORDER BY a.activity_startdate ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        }
        return activities;
    }

    public Activity getActivityDetails(int activityId) throws SQLException {
        Activity activity = null;
        String sql = "SELECT a.*, c.club_name, cat.category_name FROM activity a " +
                     "JOIN club c ON a.club_id = c.club_id " +
                     "JOIN category cat ON a.category_id = cat.category_id " +
                     "WHERE a.activity_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    activity = mapResultSetToActivity(rs);
                }
            }
        }
        return activity;
    }

    public List<Activity> getAllActivitiesWithClubAndCategoryNames() throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT a.*, c.club_name, cat.category_name FROM activity a " +
                     "JOIN club c ON a.club_id = c.club_id " +
                     "JOIN category cat ON a.category_id = cat.category_id " +
                     "ORDER BY a.activity_startdate DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        }
        return activities;
    }

    public List<Activity> getPendingActivities() throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT a.*, c.club_name, cat.category_name FROM activity a " +
                     "JOIN club c ON a.club_id = c.club_id " +
                     "JOIN category cat ON a.category_id = cat.category_id " +
                     "WHERE a.activity_status = 'PENDING' ORDER BY a.activity_startdate ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        }
        return activities;
    }

    public int getTotalActivitiesCount() throws SQLException {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM activity";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        }
        return count;
    }

    /**
     * [UPDATED] Retrieves all registered events for a student that are currently
     * in-progress OR are scheduled for the future.
     * @param studentId The student's ID.
     * @return A list of Activity objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Activity> getRegisteredInProgressAndUpcomingEventsByStudent(String studentId) throws SQLException {
        List<Activity> events = new ArrayList<>();
        // **FIX**: The WHERE clause now checks for events where the END date is in the future.
        // This correctly includes both events that have started but not ended (in-progress)
        // and events that have not yet started (upcoming).
        String sql = "SELECT a.*, c.club_name, cat.category_name FROM activity a " +
                     "JOIN registration r ON a.activity_id = r.activity_id " +
                     "JOIN club c ON a.club_id = c.club_id " +
                     "JOIN category cat ON a.category_id = cat.category_id " +
                     "WHERE r.student_no = ? AND a.activity_status = 'APPROVED' " +
                     "AND a.activity_enddate >= NOW() ORDER BY a.activity_startdate ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapResultSetToActivity(rs));
                }
            }
        }
        return events;
    }

    public int getPendingActivitiesCount() throws SQLException {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM activity WHERE activity_status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        }
        return count;
    }

    public int getApprovedActivitiesCount() throws SQLException {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM activity WHERE activity_status = 'APPROVED'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        }
        return count;
    }

    public List<Activity> getUpcomingEventsByClub(int clubId) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT a.*, c.club_name, cat.category_name FROM activity a " +
                     "JOIN club c ON a.club_id = c.club_id " +
                     "JOIN category cat ON a.category_id = cat.category_id " +
                     "WHERE a.club_id = ? AND a.activity_startdate > NOW() ORDER BY a.activity_startdate ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(mapResultSetToActivity(rs));
                }
            }
        }
        return activities;
    }

    public List<Activity> getRegisteredActivitiesByStudent(String studentId) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT a.*, c.club_name, cat.category_name, r.cert_path AS registration_cert_path FROM activity a " +
                     "JOIN registration r ON a.activity_id = r.activity_id " +
                     "JOIN club c ON a.club_id = c.club_id " +
                     "JOIN category cat ON a.category_id = cat.category_id " +
                     "WHERE r.student_no = ? ORDER BY a.activity_startdate DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(mapResultSetToActivity(rs));
                }
            }
        }
        return activities;
    }
}
