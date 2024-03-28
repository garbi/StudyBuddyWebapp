package ch.unil.doplab.studybuddy.ui;

import ch.unil.doplab.studybuddy.StudyBuddyService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.List;

@SessionScoped
@Named
public class UserData implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    StudyBuddyService theService;

    private List<StudentBean> students;

    public UserData() {
        this.students = new ArrayList<StudentBean>();
        System.out.println("UserData created: " + this.hashCode());
    }

    public List<StudentBean> getStudents() {
        return students;
    }

    public void loadStudents() {
//        students.clear();  // For local testing only
//        populateStudents(); // For local testing only
        students = theService.getAllStudents();
        System.out.println("Students loaded: " + students);
    }

    // For local testing only
    private void populateStudents() {
        students.add(new StudentBean("b8d0c81d-e1c6-4708-bd02-d218a23e4805", "paul", "Smith", "paul.smith@gmail.com", "paul"));
        students.add(new StudentBean("0ab2ec68-c574-4d81-bed0-a93c31fab1c0", "Jane", "Doe", "jane.doe@icloud.com", "jane"));
        students.add(new StudentBean("5d53a98b-53a8-4580-adc1-28067b37582a", "Jean", "Dupont", "jean.dupont@facebook.com", "jean"));
    }
}
