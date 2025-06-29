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
import java.sql.SQLException;
import java.util.List;

/**
 * A servlet for administrative use to retroactively generate certificates
 * for any existing achievement records in the database that are missing one.
 * This is a utility to fix records where certificate generation might have
 * failed previously.
 *
 * @version 2.0
 * @author [Your Name/Team]
 */
@WebServlet("/staff/GenerateExistingCertsServlet")
public class GenerateExistingCertsServlet extends HttpServlet {

    // The relative path within the web application where certificates are stored.
    private static final String CERTIFICATE_SAVE_FOLDER = "uploads" + File.separator + "certificates";
    private static final String CERTIFICATE_DB_FOLDER = "uploads/certificates";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);

        // Security check: Only authenticated staff/admins can run this process.
        if (session == null || !"Admin/Staff".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=auth");
            return;
        }

        int certificatesGenerated = 0;
        try {
            AchievementDAO achievementDAO = new AchievementDAO();
            StudentDAO studentDAO = new StudentDAO();
            
            // This DAO method finds all achievements across all students where cert_path is NULL or empty.
            List<Achievement> achievementsToProcess = achievementDAO.getAchievementsWithoutCertificates();

            if (achievementsToProcess.isEmpty()) {
                // Redirect with a message if there's nothing to do.
                response.sendRedirect(request.getContextPath() + "/staff/dashboard?cert_gen=none");
                return;
            }
            
            // **CRITICAL FIX**: Use getServletContext().getRealPath() to get the absolute path on the server.
            // This avoids hardcoding a path like "C:/Users/..." and makes the application portable.
            String absoluteSavePath = getServletContext().getRealPath(File.separator) + CERTIFICATE_SAVE_FOLDER;
            
            File certificateDir = new File(absoluteSavePath);
            if (!certificateDir.exists()) {
                if (!certificateDir.mkdirs()) {
                    System.err.println("FATAL: Could not create certificate directory at " + absoluteSavePath);
                    throw new IOException("Could not create save directory for certificates.");
                }
            }

            // Loop through each achievement that needs a certificate.
            for (Achievement achievement : achievementsToProcess) {
                Student student = studentDAO.getStudentById(achievement.getStudent_no());
                if (student != null) {
                    try {
                        String certFileName = "achievement_" + student.getStudent_no() + "_" + achievement.getAchievement_id() + ".pdf";
                        String dbCertPath = CERTIFICATE_DB_FOLDER + "/" + certFileName;
                        String fullFilePath = absoluteSavePath + File.separator + certFileName;

                        // Generate the PDF certificate file.
                        CertificateGenerator.createAchievementCertificate(student, achievement, fullFilePath);
                        
                        // Update the database record with the correct relative path.
                        achievementDAO.updateCertificatePath(achievement.getAchievement_id(), dbCertPath);
                        certificatesGenerated++;
                        System.out.println("Successfully generated certificate: " + fullFilePath);

                    } catch (Exception e) {
                        // Log errors for individual certificate failures but continue the batch process.
                        System.err.println("Failed to generate certificate for achievement ID " + achievement.getAchievement_id() + ". Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            
            // Redirect with a success message showing how many certificates were created.
            response.sendRedirect(request.getContextPath() + "/staff/dashboard?cert_gen=success&count=" + certificatesGenerated);

        } catch (Exception e) {
            e.printStackTrace();
            // Redirect with a generic error if the process fails.
            response.sendRedirect(request.getContextPath() + "/staff/dashboard?cert_gen=error");
        }
    }
}
