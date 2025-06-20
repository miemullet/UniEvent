package controller;

import dao.ActivityDAO;
import dao.MeritDAO;
import dao.RegistrationDAO;
import dao.StudentDAO; // Import StudentDAO
import model.Activity;
import model.MeritEntry;
import model.Student; // Import Student model
import util.CertificateGenerator; // Import CertificateGenerator

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File; // Import File class
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet to handle student event registrations.
 * UPDATED: Now automatically generates a participation certificate and awards merit points instantly.
 */
@WebServlet("/EventRegistrationServlet")
public class EventRegistrationServlet extends HttpServlet {

    // IMPORTANT: This must match the location used by your CertificateServlet
    private static final String CERTIFICATE_SAVE_DIRECTORY = "C:\\Users\\ayopc\\Documents\\NetBeansProjects\\UniEvent2\\src\\main\\webapp\\uploads\\certificates";
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Ensure student is logged in
        if (session == null || session.getAttribute("username") == null || !"Student".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=sessionExpired");
            return;
        }

        String studentId = (String) session.getAttribute("username");
        String action = request.getParameter("action");
        String popupStatus = "error"; // Default popup status

        try {
            if ("register".equals(action)) {
                int activityId = Integer.parseInt(request.getParameter("activity_id"));
                
                RegistrationDAO registrationDAO = new RegistrationDAO();
                ActivityDAO activityDAO = new ActivityDAO();
                MeritDAO meritDAO = new MeritDAO();
                StudentDAO studentDAO = new StudentDAO();

                if (registrationDAO.isStudentRegisteredForActivity(studentId, activityId)) {
                    popupStatus = "already_registered";
                } else {
                    // --- START TRANSACTIONAL LOGIC ---
                    // Step 1: Register student for the activity
                    registrationDAO.registerStudentForActivity(studentId, activityId);
                    
                    // Step 2: Get the activity and student details
                    Activity activity = activityDAO.getActivityDetails(activityId);
                    Student student = studentDAO.getStudentById(studentId);
                    
                    // Step 3: Generate Participation Certificate if details are available
                    if (student != null && activity != null) {
                        try {
                            String certFileName = "participation_" + studentId + "_" + activityId + ".pdf";
                            String dbCertPath = "uploads/certificates/" + certFileName;
                            
                            // Ensure the save directory exists
                            File certificateDir = new File(CERTIFICATE_SAVE_DIRECTORY);
                            if (!certificateDir.exists()) {
                                certificateDir.mkdirs();
                            }
                            
                            String fullFilePath = CERTIFICATE_SAVE_DIRECTORY + File.separator + certFileName;

                            // Generate the PDF certificate
                            CertificateGenerator.createParticipationCertificate(student, activity, fullFilePath);

                            // Update the registration record with the certificate path
                            registrationDAO.updateCertificatePathByStudentAndActivity(studentId, activityId, dbCertPath);

                        } catch (Exception e) {
                            // Log the certificate generation error, but don't fail the whole registration
                            System.err.println("ERROR generating participation certificate for student " + studentId + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    // Step 4: If activity exists and has merit points, award them
                    if (activity != null && activity.getActivity_merit() > 0) {
                        MeritEntry meritEntry = new MeritEntry();
                        meritEntry.setStudent_no(studentId);
                        meritEntry.setActivity_id(activityId);
                        meritEntry.setMerit_points(activity.getActivity_merit());
                        meritEntry.setRemarks("Merit awarded for registering in event: " + activity.getActivity_name());
                        
                        meritDAO.addMeritEntry(meritEntry);
                    }
                    // --- END TRANSACTIONAL LOGIC ---
                    
                    popupStatus = "success";
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action for registration.");
                return;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.err.println("Invalid Activity ID provided: " + e.getMessage());
            popupStatus = "error";
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error during event registration or merit update: " + e.getMessage());
            popupStatus = "error";
        } finally {
            response.sendRedirect(request.getContextPath() + "/student/events?registration=" + popupStatus);
        }
    }
}
