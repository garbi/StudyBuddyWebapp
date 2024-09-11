package ch.unil.doplab.studybuddy.ui;

import ch.unil.doplab.studybuddy.StudyBuddyService;
import ch.unil.doplab.studybuddy.domain.Level;
import ch.unil.doplab.studybuddy.domain.Student;
import ch.unil.doplab.studybuddy.domain.Rating;
import ch.unil.doplab.studybuddy.domain.Teacher;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.primefaces.component.tree.Tree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: consider making it a @ApplicationScoped bean
@SessionScoped
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
    private List<Rating> ratings;

    public UserData() {
        this.students = new ArrayList<Student>();
        this.languages = Arrays.stream(Locale.getISOLanguages())
                .map(Locale::new)
                .map(Locale::getDisplayLanguage)
                .collect(Collectors.toCollection(TreeSet::new));
        this.levels = Arrays.asList(Level.values());
        this.amounts = new TreeSet<>(Arrays.asList(50, 100, 150, 200, 250, 300));
        System.out.println("UserData created: " + this.hashCode());
        ratings = Stream.of(Rating.values()).collect(Collectors.toList());
//        ratings = Arrays.stream(Rating.values())
//                .sorted((r1, r2) -> Integer.compare(r1.getValue(), r2.getValue())) // Sorting by numeric value
//                .map(Rating::toString) // Mapping to the name of the enum (can be .toString() for stars)
//                .collect(Collectors.toList());
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
}
