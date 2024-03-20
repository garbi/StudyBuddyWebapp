package ch.unil.doplab.studdybuddy.ui;

import ch.unil.doplab.studdybuddy.StuddyBuddyService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@RequestScoped
@Named
public class StudentBean {

    @Inject
    StuddyBuddyService theService;

    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;

    public StudentBean() {
        System.out.println("StudentBean created: " + this.hashCode());
    }

    public StudentBean(String id, String firstName, String lastName, String email, String username) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
    }


//    public List
    public void loadStudent() {
        var student = theService.getStudent(this.id);
        this.id = student.getId();
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.username = student.getUsername();
        this.email = student.getEmail();
        System.out.println("Student loaded: " + student.hashCode() + " " + student.id);
    }

    public void updateStudent() {
        if (this.id != null && !this.id.isEmpty()) {
            if (this.firstName != null && this.firstName.isEmpty()) {
                this.firstName = null;
            }
            if (this.lastName != null && this.lastName.isEmpty()) {
                this.lastName = null;
            }
            if (this.username != null && this.username.isEmpty()) {
                this.username = null;
            }
            if (this.email != null && this.email.isEmpty()) {
                this.email = null;
            }
            theService.setStudent(this);
        }
    }

    public void addStudent() {
        var student = theService.addStudent(this);
        System.out.println("Student added: " + student.hashCode() + " " + student.id);
    }

    public void deleteStudent() {
        if (this.id != null && !this.id.isEmpty()) {
            theService.deleteStudent(this.id);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
