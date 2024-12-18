package ch.unil.doplab.studybuddy.ui;

import ch.unil.doplab.studybuddy.StudyBuddyService;
import ch.unil.doplab.studybuddy.domain.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Named
public class UserData implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    StudyBuddyService theService;

    private List<Student> students;
    private List<Teacher> teachers;
    private Set<String> languages;
    private List<Level> levels;
    private Set<Integer> amounts;
    private Set<Integer> hourlyFees;
    private List<Rating> ratings;

    public UserData() {
        this.students = new ArrayList<Student>();
        this.languages = Arrays.stream(Locale.getISOLanguages())
                .map(Locale::new)
                .map(Locale::getDisplayLanguage)
                .collect(Collectors.toCollection(TreeSet::new));
        this.levels = Arrays.asList(Level.values());
        this.amounts = new TreeSet<>(Arrays.asList(50, 100, 150, 200, 250, 300));
        this.hourlyFees = new TreeSet<>(Arrays.asList(25, 50, 75, 100));
        System.out.println("UserData created: " + this.hashCode());
        ratings = Stream.of(Rating.values()).collect(Collectors.toList());
    }

    public String getStudentRole() {
        return Utils.studentRole;
    }

    public String getTeacherRole() {
        return Utils.teacherRole;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public Set<Integer> getAmounts() {
        return amounts;
    }

    public void setAmounts(Set<Integer> amounts) {
        this.amounts = amounts;
    }

    public Set<Integer> getHourlyFees() {
        return hourlyFees;
    }

    public void setHourlyFees(Set<Integer> hourlyFees) {
        this.hourlyFees = hourlyFees;
    }

    public void reset() {
        theService.resetService();
        loadStudents();
        loadTeachers();
    }

    public List<String> getTopics() {
        return theService.getTopics();
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Level> getLevels() {
        return levels;
    }

    public void loadStudents() {
        students = theService.getAllStudents();
        System.out.println("Students loaded: " + students);
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void loadTeachers() {
        teachers = theService.getAllTeachers();
        System.out.println("Teachers loaded: " + students);
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public Date convertToDate(LocalDateTime timeslot) {
        return Date.from(timeslot.atZone(ZoneId.systemDefault()).toInstant());
    }
}
