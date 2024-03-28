package ch.unil.doplab.studybuddy;

import ch.unil.doplab.studybuddy.ui.StudentBean;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StudyBuddyService {
    private static final String BASE_URL = "http://localhost:8080/StudyBuddyService-1.0-SNAPSHOT/api";
    private WebTarget studentTarget;

    @PostConstruct
    public void init() {
        System.err.println("StuddyBuddyService created: " + this.hashCode());
        Client client = ClientBuilder.newClient();
        studentTarget = client.target(BASE_URL).path("students");
    }

    public StudentBean getStudent(String id) {
        return studentTarget
                .path(id.toString())
                .request()
                .get(StudentBean.class);
    }

    public boolean setStudent(StudentBean student) {
        var response = studentTarget
                .path(student.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(student, MediaType.APPLICATION_JSON));
        return response.getStatus() == 200;
    }

    public List<StudentBean> getAllStudents() {
        return studentTarget
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<StudentBean>>() {});
    }

    public StudentBean addStudent(StudentBean student) {
        student.setUUID(null);  // To make sure the id is not set and avoid bug related to ill-formed UUID on server side
        var response = studentTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(student, MediaType.APPLICATION_JSON));
        StudentBean newlyCreatedStudent = null;
        if (response.getStatus() == 200 && response.hasEntity()) {
            newlyCreatedStudent = response.readEntity(StudentBean.class);
            student.setId(newlyCreatedStudent.getId());
        }
        return newlyCreatedStudent;
    }

    public boolean deleteStudent(String id) {
        var response = studentTarget
                .path(id)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.getStatus() == 200;
    }
}
