package model;

public class Student {
    private String student_no;
    private String student_name;
    private String student_email;
    private String student_phonenum;
    private String student_password;
    private String student_course;
    private String student_faculty;
    private int student_merit; // Used to hold calculated merit for display purposes
    private String student_status;
    private String student_image_path;

    // Getters and Setters
    public String getStudent_no() { 
        return student_no; 
    }
    public void setStudent_no(String student_no) { 
        this.student_no = student_no; 
    }

    public String getStudent_name() { 
        return student_name; 
    }
    public void setStudent_name(String student_name) { 
        this.student_name = student_name; 
    }

    public String getStudent_email() { 
        return student_email; 
    }
    public void setStudent_email(String student_email) { 
        this.student_email = student_email; 
    }

    public String getStudent_phonenum() { 
        return student_phonenum; 
    }
    public void setStudent_phonenum(String student_phonenum) { 
        this.student_phonenum = student_phonenum; 
    }

    public String getStudent_password() { 
        return student_password; 
    }
    public void setStudent_password(String student_password) { 
        this.student_password = student_password; 
    }

    public String getStudent_course() { 
        return student_course; 
    }
    public void setStudent_course(String student_course) { 
        this.student_course = student_course; 
    }

    public String getStudent_faculty() { 
        return student_faculty; 
    }
    public void setStudent_faculty(String student_faculty) { 
        this.student_faculty = student_faculty; 
    }

    public int getStudent_merit() { 
        return student_merit; 
    }
    public void setStudent_merit(int student_merit) { 
        this.student_merit = student_merit; 
    }

    public String getStudent_status() { 
        return student_status; 
    }
    public void setStudent_status(String student_status) { 
        this.student_status = student_status; 
    }

    public String getStudent_image_path() { 
        return student_image_path; 
    }
    public void setStudent_image_path(String student_image_path) { 
        this.student_image_path = student_image_path; 
    }
}
