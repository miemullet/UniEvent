package controller;

import dao.AchievementDAO;
import model.Achievement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

@WebServlet("/AwardAchievementServlet")
public class AwardAchievementServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || !"Admin/Staff".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=auth");
            return;
        }

        try {
            String studentId = request.getParameter("student_id");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            int activityId = Integer.parseInt(request.getParameter("activity_id"));

            Achievement achievement = new Achievement();
            achievement.setStudent_no(studentId);
            achievement.setTitle(title);
            achievement.setDescription(description);
            achievement.setActivity_id(activityId);
            achievement.setDate_awarded(new Timestamp(System.currentTimeMillis()));

            AchievementDAO achievementDAO = new AchievementDAO();
            achievementDAO.addAchievement(achievement);

            response.sendRedirect(request.getContextPath() + "/staff/dashboard?award=success");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/staff/dashboard?award=error");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error awarding achievement.", e);
        }
    }
}
