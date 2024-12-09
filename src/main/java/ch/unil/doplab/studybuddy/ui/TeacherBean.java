package ch.unil.doplab.studybuddy.ui;


import ch.unil.doplab.studybuddy.StudyBuddyService;
import ch.unil.doplab.studybuddy.domain.*;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@SessionScoped
@Named
public class TeacherBean extends Teacher implements Serializable {
    private static final long serialVersionUID = 1L;

    // Field theTeacher always contains the current teacher as know on the server
    // so that we can compare it with the current teacher in the form
    private Teacher theTeacher;
    private String currentPassword;
    private String newPassword;
    private int selectedAmount;
    private int selectedHourlyFee;
    private String selectedLanguage;
    private String selectedTopic;
    private String selectedDescription;
    private List<Level> selectedLevels;
    private String addCourseButtonName;
    private LocalDateTime selectedTimeslot;
    private boolean changed;
    private boolean addCoursePossible;
    private String dialogMessage;

    @Inject
    StudyBuddyService theService;

    @Named
    @Inject
    private LoginBean loginBean;

    public TeacherBean() {
        this(null, null, null, null, null, null);
    }

    public TeacherBean(UUID id, String firstName, String lastName, String email, String username, String password) {
        super(id, firstName, lastName, email, username, password);
        init();
        theTeacher = new Teacher(id, firstName, lastName, email, username, password);
    }

    public void init() {
        theTeacher = null;
        currentPassword = null;
        newPassword = null;
        selectedAmount = 0;
        selectedHourlyFee = defaultHourlyRate;
        selectedLanguage = null;
        selectedTopic = null;
        selectedDescription = null;
        selectedLevels = null;
        addCourseButtonName = "Add";
        selectedTimeslot = null;
        changed = false;
        addCoursePossible = false;
        dialogMessage = null;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void savePasswordChange() {
        if (Utils.checkPassword(currentPassword, theTeacher.getPassword())) {
            this.setPassword(Utils.hashPassword(newPassword));
            updateTeacher();
            dialogMessage = "Your password was successfully changed.";
            PrimeFaces.current().executeScript("PF('passwordChangeDialog').show();");
            resetPasswordChange();
        } else {
            dialogMessage = "Your password could not be changed because the current password you entered is incorrect.";
            PrimeFaces.current().executeScript("PF('passwordChangeDialog').show();");
        }
    }

    public void resetPasswordChange() {
        currentPassword = null;
        newPassword = null;
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
        try {
            if (this.getUUID() != null) {
                theService.setTeacher(this);
                theTeacher = theService.getTeacher(this.getUUID().toString());
                changed = false;
            }
        } catch (Exception e) {
            dialogMessage = e.getMessage();
            PrimeFaces.current().executeScript("PF('updateErrorDialog').show();");
        }
    }

    public void deleteTeacher() {
        var id = this.getUUID();
        if (id != null) {
            theService.deleteTeacher(id.toString());
        }
    }

    public String getDialogMessage() {
        return dialogMessage;
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

    public void addSelectedAmount() throws Exception{
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

    public void saveHourlyFee() throws Exception {
        if (selectedHourlyFee != this.getHourlyFee()) {
            this.setHourlyFee(selectedHourlyFee);
            theTeacher.setHourlyFee(selectedHourlyFee);
            theService.setTeacher(theTeacher);
        }
    }

    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public void addSelectedLanguage() throws Exception {
        if (selectedLanguage != null) {
            this.addLanguage(selectedLanguage);
            theTeacher.addLanguage(selectedLanguage);
            theService.setTeacher(theTeacher);
        }
    }

    public String getSelectedTopic() {
        return selectedTopic;
    }

    public void setSelectedTopic(String selectedTopic) {
        this.selectedTopic = selectedTopic;
        if (this.teaches(selectedTopic)) {
            var course = this.getCourses().get(selectedTopic);
            this.setSelectedDescription(course.getDescription());
            this.setSelectedLevels(List.copyOf(course.getLevels()));
            this.setAddCourseButtonName("Update");
        } else {
            this.setAddCourseButtonName("Add");
        }
    }

    boolean teaches(String topic) {
        return getCourses().containsKey(topic);
    }

    public String getSelectedDescription() {
        return selectedDescription;
    }

    public void setSelectedDescription(String selectedDescription) {
        this.selectedDescription = selectedDescription;
    }

    public List<Level> getSelectedLevels() {
        return selectedLevels;
    }

    public void setSelectedLevels(List<Level> selectedLevels) {
        this.selectedLevels = selectedLevels;
    }

    public String getAddCourseButtonName() {
        return addCourseButtonName;
    }

    public void setAddCourseButtonName(String addCourseButtonName) {
        this.addCourseButtonName = addCourseButtonName;
    }

    public void addSelectedCourse() throws Exception {
        if (addCoursePossible) {
            var topic = new Topic(selectedTopic, selectedDescription, EnumSet.copyOf(selectedLevels));
            this.addCourse(topic);
            theTeacher.addCourse(topic);
            theService.setTeacher(theTeacher);
            this.setAddCourseButtonName("Update");
        }
    }

    public void deleteCourse(String topic) throws Exception{
        this.removeCourse(topic);
        theTeacher.removeCourse(topic);
        theService.setTeacher(theTeacher);
        if (topic.equals(selectedTopic)) {
            this.setAddCourseButtonName("Add");
        }
    }

    public void checkIfAddCoursePossible() {
        addCoursePossible = selectedTopic != null && selectedDescription != null && !selectedDescription.isBlank() && selectedLevels != null && !selectedLevels.isEmpty();
    }

    public boolean isAddCourseButtonDisabled() {
        return !addCoursePossible;
    }

    public LocalDateTime getSelectedTimeslot() {
        return selectedTimeslot;
    }

    public void setSelectedTimeslot(LocalDateTime timeslot) {
        this.selectedTimeslot = timeslot;
    }

    public void addSelectedTimeslot() throws Exception{
        this.addTimeslot(selectedTimeslot);
        theTeacher.addTimeslot(selectedTimeslot);
        theService.setTeacher(theTeacher);
    }

    public void deleteTimeslot(LocalDateTime timeslot) throws Exception{
        this.removeTimeslot(timeslot);
        theTeacher.removeTimeslot(timeslot);
        theService.setTeacher(theTeacher);
    }

    public void deleteLanguage(String language) throws Exception {
        this.removeLanguage(language);
        theTeacher.removeLanguage(language);
        theService.setTeacher(theTeacher);
    }

    public LocalDateTime getEarliestPossibleTimeslot() {
        return LocalDateTime.now().plusDays(2).withHour(9).withMinute(0).withSecond(0).withNano(0);
    }

    public String getFormattedRating() {
        return Utils.formatTeacherRating(this.getRatingAverage());
    }

    public boolean isButtonDisabled() {
        return !changed;
    }

    public void loadAddTeacherPage() {
        if (this.getFirstName() == null) {
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

    public void cancelLesson(Lesson lesson) {
        try {
            theService.cancelLesson(lesson);
            loadTeacher();
        } catch (Exception e) {
            dialogMessage = e.getMessage();
            PrimeFaces.current().executeScript("PF('cancellationErrorDialog').show();");
        }
    }

    public String deleteAccount() {
        deleteTeacher();
        return loginBean.logout();
    }

}
