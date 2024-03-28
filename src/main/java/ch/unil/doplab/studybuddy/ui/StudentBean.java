package ch.unil.doplab.studybuddy.ui;

import ch.unil.doplab.studybuddy.StudyBuddyService;
import ch.unil.doplab.studybuddy.domain.Student;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.UUID;

@RequestScoped
@Named
public class StudentBean  extends Student {

    @Inject
    StudyBuddyService theService;

    public StudentBean() {
        this(null, null, null, null, null);
    }

    public StudentBean(String id, String firstName, String lastName, String email, String username) {
        super(id == null ? null : UUID.fromString(id), firstName, lastName, email, username);
    }

    public void loadStudent() {
        var id = this.getUUID();
        if (id != null) {
            var student = theService.getStudent(id.toString());
            replace(student);
        }
    }

    public void updateStudent() {
        if (this.getUUID() != null) {
            var changes = 0;
            if (this.getFirstName() != null && this.getFirstName().isEmpty()) {
                this.setFirstName(null);
            } else {
                changes++;
            }
            if (this.getLastName() != null && this.getLastName().isEmpty()) {
                this.setLastName(null);
            } else {
                changes++;
            }
            if (this.getUsername() != null && this.getUsername().isEmpty()) {
                this.setUsername(null);
            } else {
                changes++;
            }
            if (this.getEmail() != null && this.getEmail().isEmpty()) {
                this.setEmail(null);
            } else {
                changes++;
            }
            if (changes > 0) {
                theService.setStudent(this);
            }
        }
    }

    public void addStudent() {
        var student = theService.addStudent(this);
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
