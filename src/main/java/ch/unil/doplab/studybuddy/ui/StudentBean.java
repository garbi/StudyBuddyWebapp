package ch.unil.doplab.studybuddy.ui;

import ch.unil.doplab.studybuddy.StudyBuddyService;
import ch.unil.doplab.studybuddy.domain.Student;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@SessionScoped
@Named
public class StudentBean extends Student implements Serializable {

    // Field theStudent always contains the current student as know on the server
    // so that we can compare it with the current student in the form
    private Student theStudent;
    private boolean changed;

    @Inject
    StudyBuddyService theService;

    public StudentBean() {
        this(null, null, null, null, null);
    }

    public StudentBean(UUID id, String firstName, String lastName, String email, String username) {
        super(id, firstName, lastName, email, username);
        theStudent = new Student(id, firstName, lastName, email, username);
        changed = false;
    }

    public boolean isButtonDisabled() {
        return !changed;
    }

    public void checkIfChanged() {
        boolean firstNameChanged = !theStudent.getFirstName().equals(this.getFirstName());
        boolean lastNameChanged = !theStudent.getLastName().equals(this.getLastName());
        boolean emailChanged = !theStudent.getEmail().equals(this.getEmail());
        boolean usernameChanged = !theStudent.getUsername().equals(this.getUsername());
        changed = firstNameChanged || lastNameChanged || emailChanged || usernameChanged;
    }

    public void reset() {
        this.replaceWith(theStudent);
        changed = false;
    }

    public void deleteLanguage(String language) {
        this.removeLanguage(language);
        updateStudent();
    }


    public void loadStudent() {
        var id = this.getUUID();
        if (id != null) {
            theStudent = theService.getStudent(id.toString());
            replaceWith(theStudent);
        }
    }

    public void updateStudent() {
        if (this.getUUID() != null) {
            theService.setStudent(this);
            theStudent = theService.getStudent(this.getUUID().toString());
            changed = false;
        }
    }

    public void addStudent() {
        var student = theService.addStudent(theStudent);
    }

    public void deleteStudent() {
        var id = this.getUUID();
        if (id != null) {
            theService.deleteStudent(id.toString());
        }
    }

    public void setId(String id) {
        try {
            setUUID(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            setUUID(null);
        }
    }

    public String getId() {
        var id = getUUID();
        if (id == null) {
            return null;
        }
        return id.toString();
    }
}
