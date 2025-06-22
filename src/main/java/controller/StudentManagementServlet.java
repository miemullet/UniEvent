package controller;

import dao.AchievementDAO;
import dao.ActivityDAO;
import dao.ClubDAO;
import dao.MeritDAO;
import dao.StudentDAO;
import dao.FeedbackDAO;
import dao.CategoryDAO;
import dao.ClubMembershipDAO; // Updated import
import model.Achievement;
import model.Activity;
import model.Club;
import model.MeritEntry;
import model.Student;
import model.Feedback;
import model.Category;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set; // Updated import
import java.util.LinkedHashSet;

@WebServlet("/student/*")
public class StudentManagementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) {
            action = "/dashboard";
        }
        HttpSession session = request.getSession(false);

        if (session == null || !"Student".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=sessionExpired");
            return;
        }

        String studentId = (String) session.getAttribute("username");
        
        try {
            switch (action) {
                case "/clubs":
                    showClubs(request, response, studentId);
                    break;
                case "/clubDetails":
                    showClubDetails(request, response);
                    break;
                case "/events":
                    showEvents(request, response, studentId);
                    break;
                case "/merit":
                    showMerit(request, response, studentId);
                    break;
                case "/achievements":
                    showAchievements(request, response, studentId);
                    break;
                case "/feedback":
                    showFeedback(request, response, studentId);
                    break;
                case "/account":
                    showAccount(request, response, studentId);
                    break;
                case "/activityHistory":
                    showActivityHistory(request, response, studentId);
                    break;
                case "/eventDetails":
                    showEventDetails(request, response, studentId);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/student/dashboard");
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error accessing student page.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);

        if (session == null || !"Student".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=auth");
            return;
        }
        String studentId = (String) session.getAttribute("username");

        try {
            if ("updateAccount".equals(action)) {
                updateStudentAccount(request, response, studentId);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action for student management.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/student/account?update=error");
        }
    }

    /**
     * [UPDATED] This method now uses the ClubMembershipDAO to determine which clubs
     * the student has joined and passes this information to the JSP.
     */
    private void showClubs(HttpServletRequest request, HttpServletResponse response, String studentId) throws SQLException, ServletException, IOException {
        ClubDAO clubDAO = new ClubDAO();
        ClubMembershipDAO membershipDAO = new ClubMembershipDAO(); // New DAO
        
        List<Club> clubs = clubDAO.getAllClubs();
        Set<Integer> joinedClubIds = membershipDAO.getJoinedClubIds(studentId); // Get joined clubs
        
        Set<String> uniqueCategories = new LinkedHashSet<>();
        if (clubs != null) {
            for (Club club : clubs) {
                if (club.getClub_category() != null && !club.getClub_category().trim().isEmpty()) {
                    uniqueCategories.add(club.getClub_category());
                }
            }
        }
        
        request.setAttribute("clubs", clubs);
        request.setAttribute("categories", uniqueCategories);
        request.setAttribute("joinedClubIds", joinedClubIds); // Pass the set of joined IDs to the JSP
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/studentClubs.jsp");
        dispatcher.forward(request, response);
    }

    private void showClubDetails(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        int clubId = Integer.parseInt(request.getParameter("club_id"));
        ClubDAO clubDAO = new ClubDAO();
        Club club = clubDAO.getClubById(clubId);
        request.setAttribute("club", club);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/clubDetails.jsp");
        dispatcher.forward(request, response);
    }

    private void showEvents(HttpServletRequest request, HttpServletResponse response, String studentId) throws SQLException, ServletException, IOException {
        ActivityDAO activityDAO = new ActivityDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Activity> availableActivities = activityDAO.getApprovedUpcomingEvents(); 
        List<Category> categories = categoryDAO.getAllCategories();
        
        for (Activity activity : availableActivities) {
            boolean isRegistered = new dao.RegistrationDAO().isStudentRegisteredForActivity(studentId, activity.getActivity_id());
            activity.setRegistered(isRegistered);
        }

        request.setAttribute("availableActivities", availableActivities);
        request.setAttribute("categories", categories);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/studentEvents.jsp");
        dispatcher.forward(request, response);
    }

    private void showMerit(HttpServletRequest request, HttpServletResponse response, String studentId) throws SQLException, ServletException, IOException {
        StudentDAO studentDAO = new StudentDAO();
        MeritDAO meritDAO = new MeritDAO();
        Student student = studentDAO.getStudentById(studentId);
        int totalMerit = (student != null) ? student.getStudent_merit() : 0;
        List<MeritEntry> meritHistory = meritDAO.getMeritHistoryByStudent(studentId);
        request.setAttribute("totalMerit", totalMerit);
        request.setAttribute("meritHistory", meritHistory);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/studentMerit.jsp");
        dispatcher.forward(request, response);
    }

    private void showAchievements(HttpServletRequest request, HttpServletResponse response, String studentId) throws SQLException, ServletException, IOException {
        AchievementDAO achievementDAO = new AchievementDAO();
        List<Achievement> achievements = achievementDAO.getAchievementsByStudent(studentId);
        request.setAttribute("achievements", achievements);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/studentAchievements.jsp");
        dispatcher.forward(request, response);
    }

    private void showFeedback(HttpServletRequest request, HttpServletResponse response, String studentId) throws SQLException, ServletException, IOException {
        FeedbackDAO feedbackDAO = new FeedbackDAO();
        ActivityDAO activityDAO = new ActivityDAO();
        
        List<Activity> registeredActivities = activityDAO.getRegisteredActivitiesByStudent(studentId); 
        
        registeredActivities.removeIf(activity -> !"APPROVED".equals(activity.getActivity_status()));

        List<Feedback> studentFeedbackList = feedbackDAO.getFeedbackByStudent(studentId);

        request.setAttribute("attendedActivities", registeredActivities);
        request.setAttribute("studentFeedbackList", studentFeedbackList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/studentFeedback.jsp");
        dispatcher.forward(request, response);
    }

    private void showAccount(HttpServletRequest request, HttpServletResponse response, String studentId) throws SQLException, ServletException, IOException {
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.getStudentById(studentId);
        request.setAttribute("student", student);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/studentAccount.jsp");
        dispatcher.forward(request, response);
    }

    private void updateStudentAccount(HttpServletRequest request, HttpServletResponse response, String studentId) throws SQLException, IOException {
        String studentName = request.getParameter("student_name");
        String studentEmail = request.getParameter("student_email");
        String studentPhone = request.getParameter("student_phone");
        String newPassword = request.getParameter("student_password");
        String studentCourse = request.getParameter("student_course");
        String studentFaculty = request.getParameter("student_faculty");

        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.getStudentById(studentId);
        String popupStatus = "error";

        if (student != null) {
            student.setStudent_name(studentName);
            student.setStudent_email(studentEmail);
            student.setStudent_phonenum(studentPhone);
            student.setStudent_course(studentCourse);
            student.setStudent_faculty(studentFaculty);

            if (newPassword != null && !newPassword.trim().isEmpty()) {
                student.setStudent_password(newPassword);
            }

            studentDAO.updateStudent(student);
            popupStatus = "success";
            request.getSession().setAttribute("studentName", studentName);
        }
        response.sendRedirect(request.getContextPath() + "/student/account?update=" + popupStatus);
    }

    private void showEventDetails(HttpServletRequest request, HttpServletResponse response, String studentId) throws SQLException, ServletException, IOException {
        int activityId = Integer.parseInt(request.getParameter("activity_id"));
        ActivityDAO activityDAO = new ActivityDAO();
        Activity activity = activityDAO.getActivityDetails(activityId);

        if (activity != null) {
            boolean isRegistered = new dao.RegistrationDAO().isStudentRegisteredForActivity(studentId, activityId);
            activity.setRegistered(isRegistered);
        }
        
        request.setAttribute("activity", activity);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/studentEventDetails.jsp");
        dispatcher.forward(request, response);
    }

    private void showActivityHistory(HttpServletRequest request, HttpServletResponse response, String studentId) throws SQLException, ServletException, IOException {
        ActivityDAO activityDAO = new ActivityDAO();
        List<Activity> registeredActivities = activityDAO.getRegisteredActivitiesByStudent(studentId);
        request.setAttribute("registeredActivities", registeredActivities);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/studentActivityHistory.jsp");
        dispatcher.forward(request, response);
    }
}
