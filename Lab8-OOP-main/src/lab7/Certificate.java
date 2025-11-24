package lab7;

public class Certificate {
    private String certificateId;
    private String studentId;
    private String courseId;
    private String courseTitle;
    private String issueDate;

    public Certificate() {}

    public Certificate(String certificateId, String studentId, String courseId, String courseTitle, String issueDate) {
        this.certificateId = certificateId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.issueDate = issueDate;
    }

    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }

    @Override
    public String toString() {
        return courseTitle + " — Issued: " + issueDate;
    }
}