package controller;

import dao.ClubDAO;
import dao.StaffDAO;
import dao.StudentDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

@WebServlet("/ImageUploadServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 5,      // 5MB
                 maxRequestSize = 1024 * 1024 * 10)   // 10MB
public class ImageUploadServlet extends HttpServlet {

    private static final String ABSOLUTE_SAVE_PATH = "C:\\Users\\Acer\\OneDrive\\Documents\\NetBeansProjects\\UniEvent1\\src\\main\\webapp\\uploads\\images";
    private static final String DATABASE_SAVE_DIR = "uploads/images";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=sessionExpired");
            return;
        }

        String uploadType = request.getParameter("uploadType");
        String role = (String) session.getAttribute("role");
        String redirectURL = request.getContextPath();
        String popupStatus = "error";

        try {
            Part filePart = request.getPart("imageFile");
            if (filePart == null || filePart.getSize() == 0) {
                throw new ServletException("No file was uploaded.");
            }

            File fileSaveDir = new File(ABSOLUTE_SAVE_PATH);
            if (!fileSaveDir.exists()) {
                fileSaveDir.mkdirs();
            }
            
            String fileName = extractFileName(filePart);
            String dbPath = DATABASE_SAVE_DIR + "/" + fileName;

            Path destinationPath = Paths.get(ABSOLUTE_SAVE_PATH, fileName);
            try (InputStream input = filePart.getInputStream()) {
                Files.copy(input, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }

            if ("profilePicture".equals(uploadType)) {
                if ("Student".equals(role) || "Club Organizer".equals(role)) {
                    String studentId = (String) session.getAttribute("username");
                    StudentDAO studentDAO = new StudentDAO();
                    studentDAO.updateStudentImagePath(studentId, dbPath);
                    session.setAttribute("studentImagePath", dbPath);
                    redirectURL += "/student/account";
                    popupStatus = "success";
                } else if ("Admin/Staff".equals(role)) {
                    String staffId = (String) session.getAttribute("username");
                    StaffDAO staffDAO = new StaffDAO();
                    staffDAO.updateStaffImagePath(staffId, dbPath);
                    session.setAttribute("staffImagePath", dbPath);
                    redirectURL += "/staff/account";
                    popupStatus = "success";
                }
            } else if ("clubIcon".equals(uploadType) && "Club Organizer".equals(role)) {
                int clubId = (int) session.getAttribute("clubId");
                ClubDAO clubDAO = new ClubDAO();
                clubDAO.updateClubLogoPath(clubId, dbPath);
                redirectURL += "/club/about";
                popupStatus = "success";
            } else {
                 throw new ServletException("Invalid upload type or role combination.");
            }

        } catch (SQLException | ServletException | IOException e) {
            e.printStackTrace();
            System.err.println("ERROR during image upload: " + e.getMessage());
            // Determine redirect URL based on role if an error occurs
            if ("Student".equals(role) || "Club Organizer".equals(role)) {
                redirectURL += "/student/account";
            } else if ("Admin/Staff".equals(role)) {
                redirectURL += "/staff/account";
            } else {
                redirectURL += "/login.jsp"; // Fallback
            }
        } finally {
            response.sendRedirect(redirectURL + "?upload=" + popupStatus);
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                String rawFileName = s.substring(s.indexOf("=") + 2, s.length() - 1);
                return rawFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
            }
        }
        return "";
    }
}
