package ch.unil.doplab.studybuddy;

import ch.unil.doplab.studybuddy.domain.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class StudyBuddyService {
    private static final String BASE_URL = "http://localhost:8080/StudyBuddyService-1.0-SNAPSHOT/api";
    private WebTarget studentTarget;
    private WebTarget teacherTarget;
    private WebTarget serviceTarget;

    @PostConstruct
    public void init() {
        System.out.println("StuddyBuddyService created: " + this.hashCode());
        Client client = ClientBuilder.newClient();
        studentTarget = client.target(BASE_URL).path("students");
        teacherTarget = client.target(BASE_URL).path("teachers");
        serviceTarget = client.target(BASE_URL).path("service");
    }

    public void resetService() {
        String response = serviceTarget
                .path("reset")
                .request()
                .get(String.class);
    }

    public List<String> getTopics() {
        var topics = serviceTarget
                .path("topics")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<String>>() {
                });
        return topics;
    }

    /*
     * Student operations
     */

    public Student getStudent(String id) {
        var student = studentTarget
                .path(id.toString())
                .request()
                .get(Student.class);
        return student;
    }

    public boolean setStudent(Student student) throws Exception {
        var response = studentTarget
                .path(student.getUUID().toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(student, MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getStatus() == 200;
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            throw Utils.buildException(description);
        }
    }

    public List<Student> getAllStudents() {
        var students = studentTarget
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Student>>() {
                });
        return students;
    }

    public Student addStudent(Student student) {
        student.setUUID(null);  // To make sure the id is not set and avoid bug related to ill-formed UUID on server side
        var response = studentTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(student, MediaType.APPLICATION_JSON));
        Student newlyCreatedStudent = null;
        if (response.getStatus() == 200 && response.hasEntity()) {
            newlyCreatedStudent = response.readEntity(Student.class);
            student.setUUID(newlyCreatedStudent.getUUID());
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

    /*
     * Teacher operations
     */

    public List<Teacher> getAllTeachers() {
        var teachers = teacherTarget
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Teacher>>() {
                });
        return teachers;
    }

    public Teacher getTeacher(String id) {
        var teacher = teacherTarget
                .path(id.toString())
                .request()
                .get(Teacher.class);
        return teacher;
    }


    public boolean setTeacher(Teacher teacher) throws Exception {
        var response = teacherTarget
                .path(teacher.getUUID().toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(teacher, MediaType.APPLICATION_JSON));

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getStatus() == 200;
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            throw Utils.buildException(description);
        }
    }


    public Teacher addTeacher(Teacher teacher) {
        teacher.setUUID(null);  // To make sure the id is not set and avoid bug related to ill-formed UUID on server side
        var response = teacherTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(teacher, MediaType.APPLICATION_JSON));
        Teacher newlyCreatedTeacher = null;
        if (response.getStatus() == 200 && response.hasEntity()) {
            newlyCreatedTeacher = response.readEntity(Teacher.class);
            teacher.setUUID(newlyCreatedTeacher.getUUID());
        }
        return newlyCreatedTeacher;
    }

    public boolean deleteTeacher(String id) {
        var response = teacherTarget
                .path(id)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.getStatus() == 200;
    }

    public SortedSet<LocalDateTime> getTimeslotsOf(UUID teacher) {
        var timeslots = teacherTarget
                .path("timeslotsOf")
                .path(teacher.toString())
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<SortedSet<LocalDateTime>>() {
                });
        return timeslots;
    }

    /*
     * Miscellanea
     */

    // The key is the UUID of the teacher and the value is the set of affinities between the student and the teacher
    public Map<UUID, Set<Affinity>> findAffinitiesWith(UUID studentId) {
        var affinities = serviceTarget
                .path("affinitiesWith")
                .path(studentId.toString())
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<Map<UUID, Set<Affinity>>>() {
                });
        return affinities;
    }

    public Lesson bookLesson(Lesson lesson) throws Exception {
        var response = serviceTarget
                .path("bookLesson")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(lesson, MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.readEntity(Lesson.class);
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            throw Utils.buildException(description);
        }
    }

    public void cancelLesson(Lesson lesson) throws Exception {
        var response = serviceTarget
                .path("cancelLesson")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(lesson, MediaType.APPLICATION_JSON));
        var statusFamily = response.getStatusInfo().getFamily();
        System.out.println("-----------------> Canceling lesson response: " + statusFamily);
        if (statusFamily != Response.Status.Family.SUCCESSFUL) {
            System.out.println("-----------------> Canceling lesson status: " + response.getStatus());
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            throw Utils.buildException(description);
        }
    }

    public void rateLesson(Lesson lesson, Rating rating) throws Exception {
        var response = serviceTarget
                .path("rateLesson")
                .path(rating.name())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(lesson, MediaType.APPLICATION_JSON));
        var statusFamily = response.getStatusInfo().getFamily();
        System.out.println("-----------------> Rating lesson response: " + statusFamily);
        if (statusFamily != Response.Status.Family.SUCCESSFUL) {
            System.out.println("-----------------> Rating lesson status: " + response.getStatus());
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            throw Utils.buildException(description);
        }
    }
}
