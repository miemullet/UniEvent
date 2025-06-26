package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@WebServlet("/certificates/*")
public class CertificateServlet extends HttpServlet {
    
    // IMPORTANT: This must match the ABSOLUTE path where you save generated certs
    private static final String CERTIFICATE_DIRECTORY = "C:\\Users\\ayopc\\Documents\\NetBeansProjects\\UniEvent2\\src\\main\\webapp\\uploads\\certificates";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestedFile = request.getPathInfo();

        if (requestedFile == null || requestedFile.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // No file requested
            return;
        }

        File file = new File(CERTIFICATE_DIRECTORY, requestedFile);

        if (!file.exists() || file.isDirectory()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // File not found
            return;
        }

        // Set content type to PDF and write file to response
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        response.setContentLength((int) file.length());

        Files.copy(file.toPath(), response.getOutputStream());
    }
}
