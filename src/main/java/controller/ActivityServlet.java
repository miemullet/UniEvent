package controller;

import dao.ActivityDAO;
import model.Activity;

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

@WebServlet("/club/activities")
public class ActivityServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"Club Organizer".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=sessionExpired");
            return;
        }

        try {
            int clubId = (int) session.getAttribute("clubId");
            ActivityDAO activityDAO = new ActivityDAO();
            List<Activity> activities = activityDAO.getActivitiesByClub(clubId);
            request.setAttribute("activities", activities);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("/clubActivity.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error in ActivityServlet (Club Organizer)", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        // Security check for role
        if (session == null || !"Club Organizer".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=auth");
            return;
        }

        String action = request.getParameter("action");
        String redirectStatus = "error";

        if ("deleteActivity".equals(action)) {
            try {
                int activityId = Integer.parseInt(request.getParameter("activity_id"));
                ActivityDAO activityDAO = new ActivityDAO();
                
                // For extra security, you could fetch the activity and verify the clubId
                // matches the one in the session before deleting.
                
                activityDAO.deleteActivity(activityId);
                redirectStatus = "success";

            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
                System.err.println("Error deleting activity: " + e.getMessage());
                redirectStatus = "error";
            }
            response.sendRedirect(request.getContextPath() + "/club/activities?delete=" + redirectStatus);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action specified.");
        }
    }
}
