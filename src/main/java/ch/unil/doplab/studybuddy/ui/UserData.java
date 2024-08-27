package ch.unil.doplab.studybuddy.ui;

import ch.unil.doplab.studybuddy.StudyBuddyService;
import ch.unil.doplab.studybuddy.domain.Level;
import ch.unil.doplab.studybuddy.domain.Student;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.primefaces.component.tree.Tree;

import java.util.*;
import java.util.stream.Collectors;

// TODO: consider making it a @ApplicationScoped bean
@SessionScoped
@Named
public class UserData implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    StudyBuddyService theService;

    private List<Student> students;
    private Set<String> languages;
    private List<Level> levels;
    private Set<Integer> amounts;



    public UserData() {
        this.students = new ArrayList<Student>();
        this.languages = Arrays.stream(Locale.getISOLanguages())
                .map(Locale::new)
                .map(Locale::getDisplayLanguage)
                .collect(Collectors.toCollection(TreeSet::new));
        this.levels = Arrays.asList(Level.values());
        this.amounts = new TreeSet<>(Arrays.asList(50, 100, 150, 200, 250, 300));
        System.out.println("UserData created: " + this.hashCode());
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
//        students.clear();  // For local testing only
//        populateStudents(); // For local testing only
        students = theService.getAllStudents();
        System.out.println("Students loaded: " + students);
    }

    // For local testing only
//    private void populateStudents() {
//        students.add(new Student(UUID.fromString("b8d0c81d-e1c6-4708-bd02-d218a23e4805"), "paul", "Smith", "paul.smith@gmail.com", "paul"));
//        students.add(new Student(UUID.fromString("0ab2ec68-c574-4d81-bed0-a93c31fab1c0"), "Jane", "Doe", "jane.doe@icloud.com", "jane"));
//        students.add(new Student(UUID.fromString("5d53a98b-53a8-4580-adc1-28067b37582a"), "Jean", "Dupont", "jean.dupont@facebook.com", "jean"));
//    }
}
