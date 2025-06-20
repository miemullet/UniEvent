package model;

import java.sql.Timestamp;

/**
 * Represents a single registration record from the database.
 * This class is used to hold data retrieved from the 'registration' table.
 */
public class Registration {
    private int registration_id;
    private String student_no;
    private int activity_id;
    private Timestamp registration_date;
    private String registration_status;
    private String cert_path;

    // Getters and Setters
    public int getRegistration_id() { return registration_id; }
    public void setRegistration_id(int registration_id) { this.registration_id = registration_id; }

    public String getStudent_no() { return student_no; }
    public void setStudent_no(String student_no) { this.student_no = student_no; }

    public int getActivity_id() { return activity_id; }
    public void setActivity_id(int activity_id) { this.activity_id = activity_id; }

    public Timestamp getRegistration_date() { return registration_date; }
    public void setRegistration_date(Timestamp registration_date) { this.registration_date = registration_date; }

    public String getRegistration_status() { return registration_status; }
    public void setRegistration_status(String registration_status) { this.registration_status = registration_status; }

    public String getCert_path() { return cert_path; }
    public void setCert_path(String cert_path) { this.cert_path = cert_path; }
}
