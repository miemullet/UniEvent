package controller;

import dao.AchievementDAO;
import dao.StudentDAO;
import model.Achievement;
import model.Student;
import util.CertificateGenerator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;

@WebServlet("/staff/GenerateExistingCertsServlet")
public class GenerateExistingCertsServlet extends HttpServlet {

    private static final String CERTIFICATE_SAVE_DIRECTORY = "C:/Users/ayopc/Documents/NetBeansProjects/UniEvent/web/uploads/certificates";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);

        if (session == null || !"Admin/Staff".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=auth");
            return;
        }

        int certificatesGenerated = 0;
        try {
            AchievementDAO achievementDAO = new AchievementDAO();
            StudentDAO studentDAO = new StudentDAO();
            
            // This method name is a placeholder, you'll need to create it in AchievementDAO
            List<Achievement> achievementsToProcess = achievementDAO.getAchievementsWithoutCertificates();

            if (achievementsToProcess.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/staff/dashboard?cert_gen=none");
                return;
            }
            
            File certificateDir = new File(CERTIFICATE_SAVE_DIRECTORY);
            if (!certificateDir.exists()) {
                certificateDir.mkdirs();
            }

            for (Achievement achievement : achievementsToProcess) {
                Student student = studentDAO.getStudentById(achievement.getStudent_no());
                if (student != null) {
                    String certFileName = "achievement_" + student.getStudent_no() + "_" + achievement.getAchievement_id() + ".pdf";
                    String dbCertPath = "uploads/certificates/" + certFileName;
                    String fullFilePath = CERTIFICATE_SAVE_DIRECTORY + File.separator + certFileName;

                    CertificateGenerator.createAchievementCertificate(student, achievement, fullFilePath);
                    achievementDAO.updateCertificatePath(achievement.getAchievement_id(), dbCertPath);
                    certificatesGenerated++;
                }
            }
            
            response.sendRedirect(request.getContextPath() + "/staff/dashboard?cert_gen=success&count=" + certificatesGenerated);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/staff/dashboard?cert_gen=error");
        }
    }
}
