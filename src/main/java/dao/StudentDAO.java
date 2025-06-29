package dao;

import model.Student;
import model.CourseStatistic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StudentDAO {

    /**
     * Maps a row from a ResultSet to a Student object.
     * Includes logic to populate the calculated merit score when available.
     * @param rs The ResultSet to map.
     * @return A Student object.
     * @throws SQLException If a database access error occurs.
     */
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudent_no(rs.getString("student_no"));
        student.setStudent_name(rs.getString("student_name"));
        student.setStudent_email(rs.getString("student_email"));
        student.setStudent_phonenum(rs.getString("student_phonenum"));
        student.setStudent_password(rs.getString("student_password"));
        student.setStudent_course(rs.getString("student_course"));
        student.setStudent_faculty(rs.getString("student_faculty"));
        student.setStudent_status(rs.getString("student_status"));
        student.setStudent_image_path(rs.getString("student_image_path"));

        // Safely try to get the calculated merit score if the column exists in the result set
        try {
            if (rs.findColumn("total_merit") > 0) {
                student.setStudent_merit(rs.getInt("total_merit"));
            }
        } catch (SQLException e) {
            // This is expected if the query doesn't calculate total_merit, so we can ignore it.
        }

        return student;
    }

    /**
     * Finds the student who earned the most merit points within a specific date range (a semester).
     * @param semesterStart The start timestamp of the semester.
     * @param semesterEnd The end timestamp of the semester.
     * @return The Student object with the highest merit for that period, or null.
     * @throws SQLException if a database error occurs.
     */
    public Student getTopMeritStudentForSemester(Timestamp semesterStart, Timestamp semesterEnd) throws SQLException {
        Student topStudent = null;
        String sql = "SELECT s.*, SUM(mh.merit_points) as semester_merit " +
                     "FROM student s " +
                     "JOIN merit_history mh ON s.student_no = mh.student_no " +
                     "WHERE mh.merit_date BETWEEN ? AND ? " +
                     "GROUP BY s.student_no, s.student_name, s.student_email, s.student_phonenum, s.student_password, s.student_course, s.student_faculty, s.student_status, s.student_image_path " +
                     "ORDER BY semester_merit DESC " +
                     "LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, semesterStart);
            stmt.setTimestamp(2, semesterEnd);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    topStudent = mapResultSetToStudent(rs);
                }
            }
        }
        return topStudent;
    }
    
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student WHERE student_status = 'ACTIVE' ORDER BY student_name ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }
        return students;
    }
    
    public List<CourseStatistic> getCourseStatisticsForActivity(int activityId) throws SQLException {
        List<CourseStatistic> stats = new ArrayList<>();
        String sql = "SELECT s.student_course, COUNT(s.student_no) as student_count " +
                     "FROM student s " +
                     "JOIN registration r ON s.student_no = r.student_no " +
                     "WHERE r.activity_id = ? AND s.student_course IS NOT NULL AND s.student_course != '' " +
                     "GROUP BY s.student_course " +
                     "ORDER BY student_count DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CourseStatistic stat = new CourseStatistic();
                    stat.setCourseName(rs.getString("student_course"));
                    stat.setStudentCount(rs.getInt("student_count"));
                    stats.add(stat);
                }
            }
        }
        return stats;
    }

    public Student login(String studentNo, String password) throws SQLException {
        String sql = "SELECT * FROM student WHERE student_no = ? AND student_password = ? AND student_status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentNo);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        }
        return null;
    }
    
     public Student getStudentById(String studentId) throws SQLException {
        String sql = "SELECT * FROM student WHERE student_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        }
        return null;
    }

    public void registerStudent(Student student) throws SQLException {
        String sql = "INSERT INTO student (student_no, student_name, student_email, student_phonenum, student_password, student_course, student_faculty, student_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudent_no());
            stmt.setString(2, student.getStudent_name());
            stmt.setString(3, student.getStudent_email());
            stmt.setString(4, student.getStudent_phonenum());
            stmt.setString(5, student.getStudent_password());
            stmt.setString(6, student.getStudent_course());
            stmt.setString(7, student.getStudent_faculty());
            stmt.setString(8, student.getStudent_status());
            stmt.executeUpdate();
        }
    }
    
    public void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE student SET student_name = ?, student_email = ?, student_phonenum = ?, student_password = ?, student_course = ?, student_faculty = ?, student_status = ? WHERE student_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getStudent_name());
            stmt.setString(2, student.getStudent_email());
            stmt.setString(3, student.getStudent_phonenum());
            stmt.setString(4, student.getStudent_password());
            stmt.setString(5, student.getStudent_course());
            stmt.setString(6, student.getStudent_faculty());
            stmt.setString(7, student.getStudent_status());
            stmt.setString(8, student.getStudent_no());
            stmt.executeUpdate();
        }
    }
    
    public void updateStudentImagePath(String studentId, String imagePath) throws SQLException {
        String sql = "UPDATE student SET student_image_path = ? WHERE student_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, imagePath);
            stmt.setString(2, studentId);
            stmt.executeUpdate();
        }
    }

    public List<Student> getNewMembersForClub(int clubId, int limit) throws SQLException {
        List<Student> members = new ArrayList<>();
        String sql = "SELECT s.* FROM student s " +
                     "JOIN club_membership cm ON s.student_no = cm.student_no " +
                     "WHERE cm.club_id = ? " +
                     "ORDER BY cm.join_date DESC LIMIT ?";
                     
         try (Connection conn = DBConnection.getConnection(); 
              PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()){
                    members.add(mapResultSetToStudent(rs));
                }
            }
        }
        return members;
    }

    /**
     * [FIXED] Gets a list of top-ranked students based on their total valid merit score.
     * The GROUP BY clause is now more explicit to prevent ambiguity.
     * @param limit The maximum number of students to retrieve.
     * @return A list of Student objects.
     * @throws SQLException if a database error occurs.
     */
    public List<Student> getTopStudents(int limit) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, IFNULL(SUM(mh.merit_points), 0) as total_merit " +
                     "FROM student s " +
                     "LEFT JOIN merit_history mh ON s.student_no = mh.student_no AND mh.merit_expiry_date > NOW() " +
                     "WHERE s.student_status = 'ACTIVE' " +
                     "GROUP BY s.student_no, s.student_name, s.student_email, s.student_phonenum, s.student_password, s.student_course, s.student_faculty, s.student_status, s.student_image_path " +
                     "ORDER BY total_merit DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        }
        return students;
    }

    /**
     * Gets the single student with the highest merit score.
     * @return The Student object with the highest merit, or null if no students are found.
     * @throws SQLException if a database error occurs.
     */
    public Student getTopMeritStudent() throws SQLException {
        Student topStudent = null;
        // This query also needs to be updated for consistency
        String sql = "SELECT s.*, IFNULL(SUM(mh.merit_points), 0) as total_merit " +
                     "FROM student s " +
                     "LEFT JOIN merit_history mh ON s.student_no = mh.student_no AND (mh.merit_expiry_date > NOW() OR mh.merit_expiry_date IS NULL) " +
                     "WHERE s.student_status = 'ACTIVE' " +
                     "GROUP BY s.student_no, s.student_name, s.student_email, s.student_phonenum, s.student_password, s.student_course, s.student_faculty, s.student_status, s.student_image_path " +
                     "ORDER BY total_merit DESC " +
                     "LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                topStudent = mapResultSetToStudent(rs);
            }
        }
        return topStudent;
    }

    public List<Student> getClubMembers(int clubId) throws SQLException {
        List<Student> members = new ArrayList<>();
        String sql = "SELECT DISTINCT s.* FROM student s " +
                     "JOIN club_membership cm ON s.student_no = cm.student_no " +
                     "WHERE cm.club_id = ? ORDER BY s.student_name ASC";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()){
                    members.add(mapResultSetToStudent(rs));
                }
            }
        }
        return members;
    }

    public int getTotalStudentsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM student WHERE student_status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}