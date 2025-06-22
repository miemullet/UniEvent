package controller;

import dao.ClubMembershipDAO;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * [NEW FILE]
 * Servlet to handle requests for a student to join a club.
 */
@WebServlet("/JoinClubServlet")
public class JoinClubServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"Student".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=auth");
            return;
        }

        String studentId = (String) session.getAttribute("username");
        String redirectStatus = "error";
        String clubIdParam = request.getParameter("club_id");
        
        if (studentId == null || clubIdParam == null) {
            response.sendRedirect(request.getContextPath() + "/student/clubs?status=error");
            return;
        }

        try {
            int clubId = Integer.parseInt(clubIdParam);
            ClubMembershipDAO membershipDAO = new ClubMembershipDAO();
            
            if (!membershipDAO.isMember(studentId, clubId)) {
                membershipDAO.joinClub(studentId, clubId);
                redirectStatus = "join_success";
            } else {
                redirectStatus = "already_member";
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            redirectStatus = "error";
        } catch (SQLException e) {
            e.printStackTrace();
            // MySQL error code for duplicate entry
            if (e.getErrorCode() == 1062) { 
                redirectStatus = "already_member";
            } else {
                redirectStatus = "error";
            }
        } finally {
            response.sendRedirect(request.getContextPath() + "/student/clubs?status=" + redirectStatus);
        }
    }
}
