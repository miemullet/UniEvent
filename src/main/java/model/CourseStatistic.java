package model;

/**
 * A simple model class to hold the statistics for student courses in an event.
 */
public class CourseStatistic {
    private String courseName;
    private int studentCount;

    // Getters and Setters
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }
}
