package controller;

import dao.AchievementDAO;
import dao.ActivityDAO;
import dao.ClubDAO;
import dao.MeritDAO; 
import dao.StudentDAO;
import model.Achievement;
import model.Activity;
import model.Club;
import model.MeritEntry; 
import model.Student;

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

/**
 * Controller for the student dashboard.
 * Gathers all necessary information for display, including upcoming events,
 * achievements, merit points, and club memberships.
 *
 * @version 2.1
 * @author [Your Name/Team]
 */
@WebServlet("/student/dashboard")
public class DashboardControllerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"Student".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=sessionExpired");
            return;
        }

        String studentId = (String) session.getAttribute("username");
        
        try {
            ActivityDAO activityDAO = new ActivityDAO();
            AchievementDAO achievementDAO = new AchievementDAO();
            StudentDAO studentDAO = new StudentDAO();
            ClubDAO clubDAO = new ClubDAO();
            MeritDAO meritDAO = new MeritDAO();

            // **FIX**: Changed to fetch ALL approved upcoming events, regardless of registration.
            // This allows students to see events they might want to join.
            List<Activity> activeEvents = activityDAO.getApprovedUpcomingEvents();
            
            List<Achievement> achievements = achievementDAO.getAchievementsByStudent(studentId);
            List<Student> topStudents = studentDAO.getTopStudents(3); 
            List<Club> joinedClubs = clubDAO.getJoinedClubs(studentId);
            int totalMerit = meritDAO.getCurrentValidMerit(studentId);
            List<Activity> topRatedEvents = activityDAO.getTopRatedActivities(3);

            // Set all attributes for the JSP
            request.setAttribute("studentName", session.getAttribute("studentName"));
            request.setAttribute("studentId", studentId);
            request.setAttribute("activeEvents", activeEvents);
            request.setAttribute("achievements", achievements);
            request.setAttribute("topStudents", topStudents);
            request.setAttribute("totalMerit", totalMerit);
            request.setAttribute("joinedClubs", joinedClubs);
            request.setAttribute("topRatedEvents", topRatedEvents);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/studentDashboard.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error in DashboardControllerServlet doGet: " + e.getMessage());
            throw new ServletException("Database error in Student Dashboard", e);
        }
    }
}
