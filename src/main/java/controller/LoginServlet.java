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
import java.util.Calendar;
import java.util.List;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    
    private static final String CERTIFICATE_SAVE_DIRECTORY = "C:\\Users\\Acer\\OneDrive\\Documents\\NetBeansProjects\\UniEvent1\\src\\main\\webapp\\uploads\\certificates";

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
                        checkAndGrantTopMeritAward(); // Check for and grant the yearly top merit award
                        generateMissingAchievementCertificates(student);
                        generateMissingParticipationCertificates(student);
                    } catch (Exception e) {
                        System.err.println("Error during background generation processes for student " + student.getStudent_no() + ": " + e.getMessage());
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
                    Student tempStudent = studentDAO.getStudentById(username);
                    if (tempStudent != null && "PENDING".equals(tempStudent.getStudent_status())) {
                         redirectPath = request.getContextPath() + "/login.jsp?error=pendingApproval";
                    } else if (tempStudent != null && "REJECTED".equals(tempStudent.getStudent_status())) {
                         redirectPath = request.getContextPath() + "/login.jsp?error=rejectedAccount";
                    }
                }
            } else if ("Admin/Staff".equals(role)) {
                // ... (Staff login logic remains the same)
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
     * [NEW] Checks if the yearly Top Merit Award needs to be granted and does so if required.
     * This runs only once per year, triggered by the first student login of a new year.
     */
    private void checkAndGrantTopMeritAward() throws SQLException {
        ServletContext application = getServletContext();
        Integer lastAwardYear = (Integer) application.getAttribute("lastTopMeritAwardYear");
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        // We grant the award for the *previous* year, so we check if the current year is greater.
        if (lastAwardYear == null || currentYear > lastAwardYear) {
            System.out.println("Running automatic check for Top Merit Award...");
            int yearToAward = currentYear - 1;

            StudentDAO studentDAO = new StudentDAO();
            AchievementDAO achievementDAO = new AchievementDAO();
            
            Student topStudent = studentDAO.getTopMeritStudent();
            if (topStudent != null) {
                String title = "Top Merit Award " + yearToAward;
                // Check if this award was already given for that year to prevent duplicates
                if (!achievementDAO.hasAchievementByTitle(title)) {
                    String description = "Awarded for achieving the highest merit score in the university for the year " + yearToAward + ".";
                    Achievement newAchievement = new Achievement();
                    newAchievement.setStudent_no(topStudent.getStudent_no());
                    newAchievement.setTitle(title);
                    newAchievement.setDescription(description);
                    newAchievement.setActivity_id(0);
                    newAchievement.setDate_awarded(new Timestamp(System.currentTimeMillis()));

                    achievementDAO.addAchievement(newAchievement);
                    System.out.println("Successfully granted '" + title + "' to student " + topStudent.getStudent_no());
                } else {
                    System.out.println("Top Merit Award for " + yearToAward + " has already been granted.");
                }
            }
            // Update the application-wide flag to the current year
            application.setAttribute("lastTopMeritAwardYear", currentYear);
        }
    }
    
    private void generateMissingAchievementCertificates(Student student) throws SQLException {
        AchievementDAO achievementDAO = new AchievementDAO();
        List<Achievement> achievementsToProcess = achievementDAO.getAchievementsWithoutCertificatesForStudent(student.getStudent_no());
        if (achievementsToProcess.isEmpty()) return;
        File certificateDir = new File(CERTIFICATE_SAVE_DIRECTORY);
        if (!certificateDir.exists()) certificateDir.mkdirs();
        for (Achievement achievement : achievementsToProcess) {
            try {
                String certFileName = "achievement_" + student.getStudent_no() + "_" + achievement.getAchievement_id() + ".pdf";
                String dbCertPath = "uploads/certificates/" + certFileName;
                String fullFilePath = CERTIFICATE_SAVE_DIRECTORY + File.separator + certFileName;
                CertificateGenerator.createAchievementCertificate(student, achievement, fullFilePath);
                achievementDAO.updateCertificatePath(achievement.getAchievement_id(), dbCertPath);
            } catch (Exception e) {
                System.err.println("Failed to generate certificate for achievement ID " + achievement.getAchievement_id() + ". Error: " + e.getMessage());
            }
        }
    }

    private void generateMissingParticipationCertificates(Student student) throws SQLException {
        RegistrationDAO registrationDAO = new RegistrationDAO();
        ActivityDAO activityDAO = new ActivityDAO();
        List<Registration> registrationsToProcess = registrationDAO.getRegistrationsMissingCertificates(student.getStudent_no());
        if (registrationsToProcess.isEmpty()) return;
        File certificateDir = new File(CERTIFICATE_SAVE_DIRECTORY);
        if (!certificateDir.exists()) certificateDir.mkdirs();
        for (Registration registration : registrationsToProcess) {
            try {
                Activity activity = activityDAO.getActivityDetails(registration.getActivity_id());
                if (activity != null) {
                    String certFileName = "participation_" + student.getStudent_no() + "_" + activity.getActivity_id() + ".pdf";
                    String dbCertPath = "uploads/certificates/" + certFileName;
                    String fullFilePath = CERTIFICATE_SAVE_DIRECTORY + File.separator + certFileName;
                    CertificateGenerator.createParticipationCertificate(student, activity, fullFilePath);
                    registrationDAO.updateCertificatePath(registration.getRegistration_id(), dbCertPath);
                }
            } catch (Exception e) {
                System.err.println("Failed to generate certificate for registration ID " + registration.getRegistration_id() + ". Error: " + e.getMessage());
            }
        }
    }
}
