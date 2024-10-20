package ch.unil.doplab.studybuddy.ui;

import ch.unil.doplab.studybuddy.StudyBuddyService;
import ch.unil.doplab.studybuddy.domain.Student;
import ch.unil.doplab.studybuddy.domain.Teacher;
import ch.unil.doplab.studybuddy.domain.User;
import ch.unil.doplab.studybuddy.domain.Utils;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.logging.Logger;

@SessionScoped
@Named
public class RegisterBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(RegisterBean.class.getName());

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String role;
    private String language;
    private int teacherHourlyFee;
    private String teacherBiography;

    @Inject
    StudyBuddyService theService;

    public RegisterBean() {
        reset();
    }

    public void reset() {
        firstName = null;
        lastName = null;
        email = null;
        username = null;
        password = null;
        role = null;
        language = null;
        teacherHourlyFee = 0;
        teacherBiography = "Write your biography here, emphasizing your experience as teacher...";
    }

    public String register() {
        LoginBean.invalidateSession();
        var hashedPassword = Utils.hashPassword(password);
        String errorMessage = null;

        switch (role) {
            case Utils.studentRole:
                Student student = new Student(firstName, lastName, email, username, hashedPassword);
                student.addLanguage(language);
                try {
                    student = theService.addStudent(student);
                    log.info("Student successfully registered: " + student);
                    return "Login?faces-redirect=true";
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    log.warning("Failed to register new student " + username + ": " + errorMessage);
                }
                break;
            case Utils.teacherRole:
                Teacher teacher = new Teacher(firstName, lastName, email, username, hashedPassword, teacherBiography, teacherHourlyFee);
                teacher.addLanguage(language);
                try {
                    teacher = theService.addTeacher(teacher);
                    log.info("Teacher successfully registered: " + teacher);
                    return "Login?faces-redirect=true";
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    log.warning("Failed to register new teacher " + username + ": " + errorMessage);
                }
                break;
            default:
                throw new IllegalStateException("Invalid role: " + role);
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Registration failed. " + errorMessage, null));

        return "register?faces-redirect=true";
    }

    public boolean isTeacherSelected() {
        return role != null && role.equals(Utils.teacherRole);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getTeacherHourlyFee() {
        return teacherHourlyFee;
    }

    public void setTeacherHourlyFee(int teacherHourlyFee) {
        this.teacherHourlyFee = teacherHourlyFee;
    }

    public String getTeacherBiography() {
        return teacherBiography;
    }

    public void setTeacherBiography(String teacherBiography) {
        this.teacherBiography = teacherBiography;
    }
}
