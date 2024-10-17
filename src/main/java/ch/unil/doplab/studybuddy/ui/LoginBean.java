package ch.unil.doplab.studybuddy.ui;

import ch.unil.doplab.studybuddy.StudyBuddyService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;

@SessionScoped
@Named
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String role;

    @Inject
    StudyBuddyService theService;

    @Inject
    StudentBean studentBean;

    @Inject
    TeacherBean teacherBean;

    public LoginBean() {
        reset();
    }

    public void reset() {
        username = null;
        password = null;
        role = null;
    }

    public String login() {
        var uuid = theService.authenticate(username, password, role);
        var session = getSession(true);
        if (uuid != null) {
            session.setAttribute("uuid", uuid);
            session.setAttribute("username", username);
            session.setAttribute("role", role);
            switch (role) {
                case "student":
                    studentBean.setUUID(uuid);
                    studentBean.loadStudent();
                    return "StudentLearning?faces-redirect=true"; // Redirect to the student's learning page
                case "teacher":
                    teacherBean.setUUID(uuid);
                    teacherBean.loadTeacher();
                    return "TeacherTeaching?faces-redirect=true"; // Redirect to the teacher's teaching page
            }
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Invalid login",
                        "Please check your username and password."));
        reset();
        return "Login";
    }

    public String logout() {
        invalidateSession();
        reset();
        return "Login?faces-redirect=true";
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public static HttpSession getSession(boolean create) {
        var facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {
            return null;
        }
        var externalContext = facesContext.getExternalContext();
        if (externalContext == null) {
            return null;
        }
        return (HttpSession) externalContext.getSession(create);
    }

    public static void invalidateSession() {
        var session = getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
