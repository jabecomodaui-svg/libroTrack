// Member.java
// Represents a student member. Extends User (inheritance).
// This is a new separate file — it does not change the existing members.java GUI.

public class Member extends User {
    private String studentId;
    private String fullName;
    private String course;
    private String yearLevel;
    private String section;
    private String status;   // "Active" or "Inactive"

    public Member() {
        super();
        this.studentId = "";
        this.fullName = "";
        this.course = "";
        this.yearLevel = "";
        this.section = "";
        this.status = "Active";
    }

    public Member(int id, String username, String passwordHash,
                  String studentId, String fullName, String course,
                  String yearLevel, String section, String status) {
        super(id, username, passwordHash);
        this.studentId = studentId;
        this.fullName = fullName;
        this.course = course;
        this.yearLevel = yearLevel;
        this.section = section;
        this.status = status;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "Member[id=" + getId() + ", studentId=" + studentId
                + ", fullName=" + fullName + ", status=" + status + "]";
    }
}
