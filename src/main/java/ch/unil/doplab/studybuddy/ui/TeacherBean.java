package ch.unil.doplab.studybuddy.ui;


import ch.unil.doplab.studybuddy.StudyBuddyService;
import ch.unil.doplab.studybuddy.domain.Teacher;
import ch.unil.doplab.studybuddy.domain.Utils;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.UUID;

@SessionScoped
@Named
public class TeacherBean extends Teacher implements Serializable {
    private static final long serialVersionUID = 1L;

    // Field theTeacher always contains the current teacher as know on the server
    // so that we can compare it with the current teacher in the form
    private Teacher theTeacher;
    private int selectedAmount;
    private int selectedHourlyFee;
    private String selectedLanguage;
    private boolean changed;
    private String errorMessage;

    @Inject
    StudyBuddyService theService;


    public TeacherBean() {
        this(null, null, null, null, null);
    }

    public TeacherBean(UUID id, String firstName, String lastName, String email, String username) {
        super(id, firstName, lastName, email, username);
        init();
        theTeacher = new Teacher(id, firstName, lastName, email, username);
    }

    public void init() {
        theTeacher = null;
        selectedAmount = 0;
        selectedLanguage = null;
        changed = false;
        errorMessage = null;
    }

    public void loadTeacher() {
        var id = this.getUUID();
        if (id != null) {
            theTeacher = theService.getTeacher(id.toString());
            replaceWith(theTeacher);
        }
    }

    public void addTeacher() {
        var teacher = theService.addTeacher(theTeacher);
        if (teacher != null) {
            theTeacher = teacher;
            this.replaceWith(theTeacher);
            changed = false;
        }
    }

    public void updateTeacher() {
        if (this.getUUID() != null) {
            theService.setTeacher(this);
            theTeacher = theService.getTeacher(this.getUUID().toString());
            changed = false;
        }
    }

    public void deleteTeacher() {
        var id = this.getUUID();
        if (id != null) {
            theService.deleteTeacher(id.toString());
        }
    }

    public String getErrorMessage() {
        return errorMessage;
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

    public void checkIfChanged() {
        boolean firstNameChanged = !theTeacher.getFirstName().equals(this.getFirstName());
        boolean lastNameChanged = !theTeacher.getLastName().equals(this.getLastName());
        boolean emailChanged = !theTeacher.getEmail().equals(this.getEmail());
        boolean usernameChanged = !theTeacher.getUsername().equals(this.getUsername());
        boolean biographyChanged = !theTeacher.getBiography().equals(this.getBiography());
        changed = firstNameChanged || lastNameChanged || emailChanged || usernameChanged || biographyChanged;
    }

    public void undoChanges() {
        this.replaceWith(theTeacher);
        changed = false;
    }

    public int getSelectedAmount() {
        return selectedAmount;
    }

    public void setSelectedAmount(int selectedAmount) {
        this.selectedAmount = selectedAmount;
    }

    public void addSelectedAmount() {
        if (selectedAmount > 0) {
            this.deposit(selectedAmount);
            theTeacher.deposit(selectedAmount);
            theService.setTeacher(theTeacher);
        }
    }

    public int getSelectedHourlyFee() {
        return selectedHourlyFee;
    }

    public void setSelectedHourlyFee(int selectedHourlyFee) {
        this.selectedHourlyFee = selectedHourlyFee;
    }

    public void saveHourlyFee() {
        if (selectedHourlyFee != this.getHourlyFee()) {
            this.setHourlyFee(selectedHourlyFee);
            theTeacher.setHourlyFee(selectedHourlyFee);
            theService.setTeacher(theTeacher);
        }
    }

    public void saveBiography() {
        if (!theTeacher.getBiography().equals(this.getBiography())) {
            theTeacher.setBiography(this.getBiography());
            theService.setTeacher(theTeacher);
        }
    }

    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }


    public void addSelectedLanguage() {
        if (selectedLanguage != null) {
            this.addLanguage(selectedLanguage);
            theTeacher.addLanguage(selectedLanguage);
            theService.setTeacher(theTeacher);
        }
    }

    public void deleteLanguage(String language) {
        this.removeLanguage(language);
        theTeacher.removeLanguage(language);
        theService.setTeacher(theTeacher);
    }

    public String getFormattedRating() {
        return Utils.formatTeacherRating(this.getRatingAverage());
    }

    public boolean isButtonDisabled() {
        return !changed;
    }

    public void loadAddTeacherPage() {
        if(this.getFirstName() == null) {
            this.setFirstName("Aristotle");
        }
        if (this.getLastName() == null) {
            this.setLastName("Stagiritis");
        }
        if (this.getEmail() == null) {
            this.setEmail("aristotle@wise.edu");
        }
        if (this.getUsername() == null) {
            this.setUsername("aristotle");
        }
        theTeacher.replaceWith(this);
    }

}
