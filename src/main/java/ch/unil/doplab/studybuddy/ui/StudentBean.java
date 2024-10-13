package ch.unil.doplab.studybuddy.ui;

import ch.unil.doplab.studybuddy.StudyBuddyService;
import ch.unil.doplab.studybuddy.domain.*;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SessionScoped
@Named
public class StudentBean extends Student implements Serializable {
    private static final long serialVersionUID = 1L;

    // Field theStudent always contains the current student as know on the server
    // so that we can compare it with the current student in the form
    private Student theStudent;
    private String currentPassword;
    private String newPassword;
    private boolean changed;
    private String selectedLanguage;
    private String selectedTopic;
    private Level selectedLevel;
    private int selectedAmount;
    Map<UUID, Set<Affinity>> allAffinities;
    private List<Affinity> selectedAffinities;
    private boolean showSelectedAffinities;
    private String selectedAffinityTopic;
    private Level selectedAffinityLevel;
    private Affinity selectedAffinity;
    private SortedSet<LocalDateTime> selectedTimeslots;
    private boolean showSelectedTimeslots;
    private String dialogMessage;

    @Inject
    StudyBuddyService theService;

    public StudentBean() {
        this(null, null, null, null, null, null);
    }

    public StudentBean(UUID id, String firstName, String lastName, String email, String username, String password) {
        super(id, firstName, lastName, email, username, password);
        init();
        theStudent = new Student(id, firstName, lastName, email, username, password);
    }

    public void init() {
        theStudent = null;
        currentPassword = null;
        newPassword = null;
        changed = false;
        selectedLanguage = null;
        selectedTopic = null;
        selectedLevel = null;
        selectedAmount = 0;
        allAffinities = null;
        selectedAffinities = null;
        showSelectedAffinities = false;
        selectedAffinityTopic = null;
        selectedAffinityLevel = null;
        selectedAffinity = null;
        selectedTimeslots = null;
        showSelectedTimeslots = false;
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
        if (Utils.checkPassword(currentPassword, theStudent.getPassword())) {
            this.setPassword(Utils.hashPassword(newPassword));
            updateStudent();
            dialogMessage = "Your password was successfully changed.";
            PrimeFaces.current().executeScript("PF('passwordChangeDialog').show();");
            resetPasswordChange();
        } else {
            dialogMessage = "Your password could not be changed because the current password is incorrect.";
            PrimeFaces.current().executeScript("PF('passwordChangeDialog').show();");
        }
    }

    public void resetPasswordChange() {
        currentPassword = null;
        newPassword = null;
    }

    public void onRatingChange(Lesson lesson) {
        if (lesson.getRatingUpdate() != null && lesson.getRatingUpdate() != lesson.getRating()) {
            rateLesson(lesson);
        }
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public int getSelectedAmount() {
        return selectedAmount;
    }

    public void setSelectedAmount(int selectedAmount) {
        this.selectedAmount = selectedAmount;
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

    public String getSelectedAffinityTopic() {
        return selectedAffinityTopic;
    }

    public Level getSelectedAffinityLevel() {
        return selectedAffinityLevel;
    }

    public List<Affinity> getSelectedAffinities() {
        return selectedAffinities;
    }

    public void checkIfChanged() {
        boolean firstNameChanged = !theStudent.getFirstName().equals(this.getFirstName());
        boolean lastNameChanged = !theStudent.getLastName().equals(this.getLastName());
        boolean emailChanged = !theStudent.getEmail().equals(this.getEmail());
        boolean usernameChanged = !theStudent.getUsername().equals(this.getUsername());
        boolean passwordChanged = !theStudent.getPassword().equals(this.getPassword());
        changed = firstNameChanged || lastNameChanged || emailChanged || usernameChanged || passwordChanged;
    }

    public void undoChanges() {
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

    public void addSelectedAmount() {
        if (selectedAmount > 0) {
            this.deposit(selectedAmount);
            theStudent.deposit(selectedAmount);
            theService.setStudent(theStudent);
        }
    }

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

    public void updateAffinities(String topic) {
        allAffinities = theService.findAffinitiesWith(this.getUUID());
        selectedAffinityTopic = topic;

        Topic interest = getInterests().stream().
                filter(t -> t.getTitle().equals(topic)).
                findFirst().orElse(null);

        if (interest != null) {
            this.selectedAffinityLevel = interest.getLevels().stream().findFirst().get();
            selectedAffinities = allAffinities.values().stream()
                    .flatMap(Set::stream)
                    .filter(affinity -> affinity.getTitle().equals(topic))
                    .sorted()
                    .collect(Collectors.toList());
        }
        showSelectedAffinities = selectedAffinities != null && !selectedAffinities.isEmpty();
        showSelectedTimeslots = false;
    }

    public boolean isShowSelectedAffinities() {
        return showSelectedAffinities;
    }

    public Affinity getSelectedAffinity() {
        return selectedAffinity;
    }

    public void loadTimeslotsOf(Affinity affinity) {
        selectedAffinity = affinity;
        selectedTimeslots = theService.getTimeslotsOf(selectedAffinity.getTeacherID());
        showSelectedTimeslots = !selectedTimeslots.isEmpty();
    }

    public boolean isShowSelectedTimeslots() {
        return showSelectedTimeslots;
    }

    public SortedSet<LocalDateTime> getSelectedTimeslots() {
        return selectedTimeslots;
    }

    private void rateLesson(Lesson lesson) {
        System.out.println("-----------------> Rating lesson on " + lesson.getTitle() + " by " + lesson.getTeacherName() + "at " + lesson.getTimeslot());
        try {
            theService.rateLesson(lesson, lesson.getRatingUpdate());
            lesson.updateRating();
            theStudent.rateLesson(lesson.getTimeslot(), lesson.getRating());
            lesson.updateRating();
            if (selectedAffinityTopic != null) {
                updateAffinities(selectedAffinityTopic);
            }
        } catch (Exception e) {
            dialogMessage = e.getMessage();
            PrimeFaces.current().executeScript("PF('ratingErrorDialog').show();");
        }
    }

    public void bookLesson(LocalDateTime timeslot) {
        System.out.println("-----------------> Booking lesson on " + selectedAffinity.getTitle() + " with " + selectedAffinity.getTeacherName() + " at " + timeslot);
        var lesson = new Lesson(timeslot, selectedAffinity);
        try {
            theService.bookLesson(lesson);
            updateAffinities(selectedAffinity.getTitle());
            loadStudent();
        } catch (Exception e) {
            dialogMessage = e.getMessage();
            PrimeFaces.current().executeScript("PF('bookingErrorDialog').show();");
        }
    }

    public void cancelLesson(Lesson lesson) {
        System.out.println("-----------------> "+ lesson.getStudentName() +" is canceling lesson on " + lesson.getTitle() + " with " + lesson.getTeacherName() + " at " + lesson.getTimeslot());
        try {
            theService.cancelLesson(lesson);
            updateAffinities(lesson.getTitle());
            loadStudent();
        } catch (Exception e) {
            dialogMessage = e.getMessage();
            PrimeFaces.current().executeScript("PF('cancellationErrorDialog').show();");
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
        if (student != null) {
            theStudent = student;
            this.replaceWith(theStudent);
            changed = false;
        }
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

    public void loadAddStudentPage() {
        if(this.getFirstName() == null) {
            this.setFirstName("Blaise");
        }
        if (this.getLastName() == null) {
            this.setLastName("Pascal");
        }
        if (this.getEmail() == null) {
            this.setEmail("blaise@random.com");
        }
        if (this.getUsername() == null) {
            this.setUsername("blaise");
        }
        theStudent.replaceWith(this);
    }
}
