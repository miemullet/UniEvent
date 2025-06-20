package util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import model.Activity;
import model.Achievement;
import model.Student;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

public class CertificateGenerator {

    public static void createParticipationCertificate(Student student, Activity activity, String filePath) throws DocumentException, FileNotFoundException {
        // Create a new document instance
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        // Open the document to add content
        document.open();

        // Add title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 30, Font.BOLD);
        Paragraph title = new Paragraph("Certificate of Participation", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        // Add "This certificate is proudly presented to"
        Font textFont = new Font(Font.FontFamily.HELVETICA, 16);
        Paragraph presentedTo = new Paragraph("This certificate is proudly presented to", textFont);
        presentedTo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(presentedTo);

        // Add student name
        Font studentFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.BLUE);
        Paragraph studentName = new Paragraph(student.getStudent_name(), studentFont);
        studentName.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(studentName);

        // Add "for participating in the event"
        Paragraph forParticipating = new Paragraph("for participating in the event", textFont);
        forParticipating.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(forParticipating);

        // Add activity name
        Font activityFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD);
        Paragraph activityName = new Paragraph(activity.getActivity_name(), activityFont);
        activityName.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(activityName);

        // Add event date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
        String eventDate = sdf.format(activity.getActivity_startdate());
        Paragraph heldOn = new Paragraph("held on " + eventDate, textFont);
        heldOn.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(heldOn);

        // Close the document
        document.close();
    }

    public static void createAchievementCertificate(Student student, Achievement achievement, String filePath) throws DocumentException, FileNotFoundException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 30, Font.BOLD);
        Paragraph title = new Paragraph("Certificate of Achievement", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        Font textFont = new Font(Font.FontFamily.HELVETICA, 16);
        Paragraph awardedTo = new Paragraph("This special recognition is awarded to", textFont);
        awardedTo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(awardedTo);

        Font studentFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.BLUE);
        Paragraph studentName = new Paragraph(student.getStudent_name(), studentFont);
        studentName.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(studentName);

        Paragraph forAchievement = new Paragraph("for outstanding achievement in", textFont);
        forAchievement.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(forAchievement);

        Font achievementFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD);
        Paragraph achievementTitle = new Paragraph(achievement.getTitle(), achievementFont);
        achievementTitle.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(achievementTitle);

        if (achievement.getDescription() != null && !achievement.getDescription().isEmpty()) {
            Font descFont = new Font(Font.FontFamily.HELVETICA, 14, Font.ITALIC);
            Paragraph achievementDesc = new Paragraph("\"" + achievement.getDescription() + "\"", descFont);
            achievementDesc.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(achievementDesc);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
        String awardDate = sdf.format(achievement.getDate_awarded());
        Paragraph awardedOn = new Paragraph("Awarded on: " + awardDate, textFont);
        awardedOn.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(awardedOn);

        document.close();
    }
}
