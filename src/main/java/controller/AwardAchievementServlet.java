package controller;

import dao.AchievementDAO;
import model.Achievement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Handles the administrative action of manually awarding an achievement to a student.
 * This servlet processes the form from the staff dashboard, creates an achievement record,
 * and triggers the generation of a PDF certificate.
 *
 * @version 2.0
 * @author [Your Name/Team]
 */
@WebServlet("/AwardAchievementServlet")
public class AwardAchievementServlet extends HttpServlet {

    // The relative path within the web application where certificates are stored.
    private static final String CERTIFICATE_SAVE_FOLDER = "uploads" + File.separator + "certificates";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // Security check: Only authenticated staff/admins can perform this action.
        if (session == null || !"Admin/Staff".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=auth");
            return;
        }

        try {
            // Retrieve form data
            String studentId = request.getParameter("student_id");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            // Activity ID is optional for manually awarded achievements, default to 0 if not provided.
            int activityId = 0; 
            String activityIdParam = request.getParameter("activity_id");
            if (activityIdParam != null && !activityIdParam.isEmpty()) {
                activityId = Integer.parseInt(activityIdParam);
            }

            // Populate the Achievement model
            Achievement achievement = new Achievement();
            achievement.setStudent_no(studentId);
            achievement.setTitle(title);
            achievement.setDescription(description);
            achievement.setActivity_id(activityId);
            achievement.setDate_awarded(new Timestamp(System.currentTimeMillis()));

            // **CRITICAL FIX**: Use getServletContext().getRealPath() to determine the absolute server path.
            // This is essential for the certificate file to be saved correctly in any environment.
            String absoluteSavePath = getServletContext().getRealPath(File.separator) + CERTIFICATE_SAVE_FOLDER;

            // Call the DAO to add the achievement and generate the certificate
            AchievementDAO achievementDAO = new AchievementDAO();
            achievementDAO.addAchievement(achievement, absoluteSavePath);

            // Redirect with a success message
            response.sendRedirect(request.getContextPath() + "/staff/dashboard?award=success");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.err.println("Invalid Activity ID format in AwardAchievementServlet.");
            response.sendRedirect(request.getContextPath() + "/staff/dashboard?award=error&reason=invalid_id");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error in AwardAchievementServlet: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/staff/dashboard?award=error&reason=db_error");
        } catch (Exception e) {
            e.printStackTrace();
            // This will catch other potential errors, like issues with file I/O during certificate generation.
            throw new ServletException("A critical error occurred while awarding an achievement.", e);
        }
    }
}
