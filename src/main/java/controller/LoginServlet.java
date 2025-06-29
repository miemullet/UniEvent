package controller;

import dao.ClubDAO;
import dao.StaffDAO;
import dao.StudentDAO;
import dao.AchievementDAO;
import dao.RegistrationDAO;
import dao.ActivityDAO;
import model.Club;
import model.Staff;
import model.Student;
import model.Achievement;
import model.Registration;
import model.Activity;
import util.CertificateGenerator;

import javax.servlet.ServletContext;
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
import java.util.List;

/**
 * Handles user authentication for all roles.
 * Upon successful student login, it triggers background processes to:
 * 1. Check and grant the "Top Merit Award".
 * 2. Generate any missing participation or achievement certificates.
 *
 * @version 2.0
 * @author [Your Name/Team]
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    
    // The relative path for storing certificates within the web application structure.
    private static final String CERTIFICATE_SAVE_FOLDER = "uploads" + File.separator + "certificates";
    private static final String AWARD_TITLE = "Top Merit Award";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        String redirectPath = request.getContextPath() + "/login.jsp?error=wrongCredentials";

        try {
            if ("Student".equals(role) || "Club Organizer".equals(role)) {
                StudentDAO studentDAO = new StudentDAO();
                Student student = studentDAO.login(username, password);
                
                if (student != null) {
                    HttpSession session = request.getSession();
                    session.setAttribute("username", student.getStudent_no());
                    session.setAttribute("studentName", student.getStudent_name());
                    session.setAttribute("studentImagePath", student.getStudent_image_path());

                    // --- AUTOMATIC PROCESSES ON LOGIN ---
                    try {
                        // **CRITICAL FIX**: Pass ServletContext to get the real server path for file saving.
                        String absoluteCertPath = getServletContext().getRealPath(File.separator) + CERTIFICATE_SAVE_FOLDER;
                        
                        checkAndGrantTopMeritAward(absoluteCertPath);
                        generateMissingAchievementCertificates(student, absoluteCertPath);
                        generateMissingParticipationCertificates(student, absoluteCertPath);

                    } catch (Exception e) {
                        System.err.println("CRITICAL ERROR during background processes for student " + student.getStudent_no() + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                    // --- END OF BLOCK ---

                    if ("Club Organizer".equals(role)) {
                        ClubDAO clubDAO = new ClubDAO();
                        Club club = clubDAO.getClubByPresident(student.getStudent_no());
                        if(club != null){
                            session.setAttribute("role", role);
                            session.setAttribute("clubId", club.getClub_id());
                            session.setAttribute("clubName", club.getClub_name());
                            redirectPath = request.getContextPath() + "/club/dashboard";
                        } else {
                            redirectPath = request.getContextPath() + "/login.jsp?error=notOrganizer";
                        }
                    } else {
                        session.setAttribute("role", "Student");
                        redirectPath = request.getContextPath() + "/student/dashboard";
                    }
                } else {
                    // Check if the login failed because the account is pending or rejected
                    StudentDAO tempStudentDAO = new StudentDAO();
                    Student tempStudent = tempStudentDAO.getStudentById(username);
                    if (tempStudent != null) {
                        if ("PENDING".equals(tempStudent.getStudent_status())) {
                             redirectPath = request.getContextPath() + "/login.jsp?error=pendingApproval";
                        } else if ("REJECTED".equals(tempStudent.getStudent_status())) {
                             redirectPath = request.getContextPath() + "/login.jsp?error=rejectedAccount";
                        }
                    }
                }
            } else if ("Admin/Staff".equals(role)) {
                StaffDAO staffDAO = new StaffDAO();
                Staff staff = staffDAO.login(username, password);
                if (staff != null) {
                    HttpSession session = request.getSession();
                    session.setAttribute("username", staff.getHep_staffid());
                    session.setAttribute("staffName", staff.getHep_staffname());
                    session.setAttribute("role", role);
                    session.setAttribute("staffImagePath", staff.getHep_staff_image_path());
                    session.setAttribute("isAdmin", staff.isHep_staffadminstatus());
                    redirectPath = request.getContextPath() + "/staff/dashboard";
                }
            }
            response.sendRedirect(redirectPath);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error in LoginServlet: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=databaseError");
        }
    }

    /**
     * Checks and grants the 'Top Merit Award'. This logic now ensures that if a new student
     * becomes the top, the award is transferred, and a certificate is generated. It also ensures
     * that if the current holder is missing a certificate, it gets generated.
     * * @param absoluteSavePath The absolute file system path to save the certificate.
     * @throws SQLException If a database error occurs.
     */
    private void checkAndGrantTopMeritAward(String absoluteSavePath) throws SQLException {
        StudentDAO studentDAO = new StudentDAO();
        AchievementDAO achievementDAO = new AchievementDAO();
        
        Student topStudent = studentDAO.getTopMeritStudent();

        if (topStudent != null) {
            // Check if the current top student already holds the award
            Achievement existingAward = achievementDAO.getAchievementByTitle(topStudent.getStudent_no(), AWARD_TITLE);

            if (existingAward == null) {
                // A new student is now the top merit holder.
                // 1. Remove the award from any previous recipient to ensure only one student holds it.
                achievementDAO.deleteAchievementsByTitle(AWARD_TITLE);

                // 2. Grant the new award to the current top student.
                String description = "Awarded for achieving the highest current merit score in the university.";
                Achievement newAchievement = new Achievement();
                newAchievement.setStudent_no(topStudent.getStudent_no());
                newAchievement.setTitle(AWARD_TITLE);
                newAchievement.setDescription(description);
                newAchievement.setActivity_id(0); // System-generated award
                newAchievement.setDate_awarded(new Timestamp(System.currentTimeMillis()));

                // **RELIABILITY FIX**: The addAchievement method now handles certificate generation internally.
                achievementDAO.addAchievement(newAchievement, absoluteSavePath);
                System.out.println("Granted '" + AWARD_TITLE + "' to new top student: " + topStudent.getStudent_no());

            } else if (existingAward.getCert_path() == null || existingAward.getCert_path().isEmpty()) {
                // The current top student has the award record but is missing the certificate.
                System.out.println("Attempting to regenerate missing certificate for Top Merit Award holder: " + topStudent.getStudent_no());
                try {
                    String certFileName = "achievement_" + topStudent.getStudent_no() + "_" + existingAward.getAchievement_id() + ".pdf";
                    String dbCertPath = CERTIFICATE_SAVE_FOLDER.replace(File.separator, "/") + "/" + certFileName;
                    String fullFilePath = absoluteSavePath + File.separator + certFileName;
                    
                    // Create directories if they don't exist
                    File saveDir = new File(absoluteSavePath);
                    if (!saveDir.exists()) saveDir.mkdirs();

                    CertificateGenerator.createAchievementCertificate(topStudent, existingAward, fullFilePath);
                    achievementDAO.updateCertificatePath(existingAward.getAchievement_id(), dbCertPath);
                    System.out.println("Successfully regenerated missing certificate for " + AWARD_TITLE);
                } catch (Exception e) {
                    System.err.println("Failed to regenerate certificate for achievement ID " + existingAward.getAchievement_id() + ". Error: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Generates certificates for achievements that are recorded in the database but have no certificate file path.
     * * @param student The student who logged in.
     * @param absoluteSavePath The absolute file system path to save the certificates.
     * @throws SQLException If a database error occurs.
     */
    private void generateMissingAchievementCertificates(Student student, String absoluteSavePath) throws SQLException {
        AchievementDAO achievementDAO = new AchievementDAO();
        List<Achievement> achievementsToProcess = achievementDAO.getAchievementsWithoutCertificatesForStudent(student.getStudent_no());
        
        if (achievementsToProcess.isEmpty()) return;

        File certificateDir = new File(absoluteSavePath);
        if (!certificateDir.exists()) certificateDir.mkdirs();

        System.out.println("Found " + achievementsToProcess.size() + " missing achievement certificates for student " + student.getStudent_no());

        for (Achievement achievement : achievementsToProcess) {
            // Skip the Top Merit Award as it's handled by its own logic
            if (AWARD_TITLE.equals(achievement.getTitle())) {
                continue;
            }
            try {
                String certFileName = "achievement_" + student.getStudent_no() + "_" + achievement.getAchievement_id() + ".pdf";
                String dbCertPath = CERTIFICATE_SAVE_FOLDER.replace(File.separator, "/") + "/" + certFileName;
                String fullFilePath = absoluteSavePath + File.separator + certFileName;
                
                CertificateGenerator.createAchievementCertificate(student, achievement, fullFilePath);
                achievementDAO.updateCertificatePath(achievement.getAchievement_id(), dbCertPath);
                System.out.println("Generated missing certificate for achievement ID: " + achievement.getAchievement_id());
            } catch (Exception e) {
                System.err.println("Failed to generate certificate for achievement ID " + achievement.getAchievement_id() + ". Error: " + e.getMessage());
            }
        }
    }

    /**
     * Generates certificates for event participations that are recorded in the database but have no certificate file path.
     * * @param student The student who logged in.
     * @param absoluteSavePath The absolute file system path to save the certificates.
     * @throws SQLException If a database error occurs.
     */
    private void generateMissingParticipationCertificates(Student student, String absoluteSavePath) throws SQLException {
        RegistrationDAO registrationDAO = new RegistrationDAO();
        ActivityDAO activityDAO = new ActivityDAO();
        List<Registration> registrationsToProcess = registrationDAO.getRegistrationsMissingCertificates(student.getStudent_no());
        
        if (registrationsToProcess.isEmpty()) return;
        
        File certificateDir = new File(absoluteSavePath);
        if (!certificateDir.exists()) certificateDir.mkdirs();

        System.out.println("Found " + registrationsToProcess.size() + " missing participation certificates for student " + student.getStudent_no());

        for (Registration registration : registrationsToProcess) {
            try {
                Activity activity = activityDAO.getActivityDetails(registration.getActivity_id());
                if (activity != null) {
                    String certFileName = "participation_" + student.getStudent_no() + "_" + activity.getActivity_id() + ".pdf";
                    String dbCertPath = CERTIFICATE_SAVE_FOLDER.replace(File.separator, "/") + "/" + certFileName;
                    String fullFilePath = absoluteSavePath + File.separator + certFileName;

                    CertificateGenerator.createParticipationCertificate(student, activity, fullFilePath);
                    registrationDAO.updateCertificatePath(registration.getRegistration_id(), dbCertPath);
                    System.out.println("Generated missing certificate for registration ID: " + registration.getRegistration_id());
                }
            } catch (Exception e) {
                System.err.println("Failed to generate certificate for registration ID " + registration.getRegistration_id() + ". Error: " + e.getMessage());
            }
        }
    }
}
