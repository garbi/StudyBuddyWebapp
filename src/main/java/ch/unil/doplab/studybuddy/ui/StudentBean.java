package ch.unil.doplab.studybuddy.ui;

import ch.unil.doplab.studybuddy.StudyBuddyService;
import ch.unil.doplab.studybuddy.domain.Level;
import ch.unil.doplab.studybuddy.domain.Student;
import ch.unil.doplab.studybuddy.domain.Topic;
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
    private String selectedLanguage;
    private String selectedTopic;
    private Level selectedLevel;

    @Inject
    StudyBuddyService theService;

    public StudentBean() {
        this(null, null, null, null, null);
    }

    public StudentBean(UUID id, String firstName, String lastName, String email, String username) {
        super(id, firstName, lastName, email, username);
        theStudent = new Student(id, firstName, lastName, email, username);
        selectedLanguage = null;
        changed = false;
    }

    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public String getSelectedTopic() {
        return selectedTopic;
    }

    public void setSelectedTopic(String selectedTopic) {
        this.selectedTopic = selectedTopic;
    }

    public Level getSelectedLevel() {
        return selectedLevel;
    }

    public void setSelectedLevel(Level selectedLevel) {
        this.selectedLevel = selectedLevel;
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
        theStudent.removeLanguage(language);
        theService.setStudent(theStudent);
    }

    public void addSelectedLanguage() {
        if (selectedLanguage != null) {
            this.addLanguage(selectedLanguage);
            theStudent.addLanguage(selectedLanguage);
            theService.setStudent(theStudent);
        }
    }

    // TODO: CONTINUE FROM HERE
    public void deleteInterest(String topic) {
        this.removeInterest(topic);
        theStudent.removeInterest(topic);
        theService.setStudent(theStudent);
    }

    public void addSelectedInterest() {
        if (selectedTopic != null && selectedLevel != null) {
            var topic = new Topic(selectedTopic, selectedLevel);
            this.addInterest(topic);
            theStudent.addInterest(topic);
            theService.setStudent(theStudent);
        }
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
