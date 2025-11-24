package lab7;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

public class CertificateManager {

    private final JsonDatabaseManager db;
    private final File usersFile;
    private final Gson gson;

    public CertificateManager(JsonDatabaseManager db) {
        this.db = db;
        this.usersFile = new File("data/users.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public boolean checkCourseCompleted(String studentId, String courseId) {
        Course course = db.getCourse(courseId);
        User user = db.getUserById(studentId);
        if (course == null || user == null || !(user instanceof Student)) {
            return false;
        }
        Student student = (Student) user;
        List<Lesson> lessons = course.getLessons();
        if (lessons == null) {
            return false;
        }

        for (Lesson lesson : lessons) {
            String lid = lesson.getLessonId();
            if (!course.getProgress().containsKey(studentId)
                    || !course.getProgress().get(studentId).contains(lid)) {
                return false;
            }

            if (lesson.getQuiz() != null && !lesson.getQuiz().isEmpty()) {
                Quiz q = lesson.getQuiz().get(0);
                QuizAttempts attempt = student.getLastQuizAttempt(q.getQuizId());
                if (attempt == null) {
                    return false;
                }
                double pct = (attempt.getScore() * 100.0) / q.getQuestions().size();
                if (pct < 50.0) {
                    return false;
                }
            }
        }
        return true;
    }

    public Certificate generateCertificateIfEligible(String studentId, String courseId) {
        if (!checkCourseCompleted(studentId, courseId)) {
            return null;
        }
        Course course = db.getCourse(courseId);
        User u = db.getUserById(studentId);
        if (course == null || u == null) {
            return null;
        }

        String certId = UUID.randomUUID().toString();
        String issueDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String courseTitle = course.getTitle() != null ? course.getTitle() : courseId;

        Certificate cert = new Certificate(certId, studentId, courseId, courseTitle, issueDate);
        saveCertificateToUserJson(cert);
        db.loadUsers();
        return cert;

    }

    private void saveCertificateToUserJson(Certificate cert) {
        try {
            Reader r = new FileReader(usersFile);
            JsonElement root = JsonParser.parseReader(r);
            r.close();

            if (!root.isJsonArray()) {
                return;
            }
            JsonArray usersArray = root.getAsJsonArray();
            boolean updated = false;

            for (JsonElement userElem : usersArray) {
                JsonObject userObj = userElem.getAsJsonObject();
                if (!userObj.has("userId")) {
                    continue;
                }

                if (cert.getStudentId().equals(userObj.get("userId").getAsString())) {
                    JsonArray certsArr;
                    if (userObj.has("certificates") && userObj.get("certificates").isJsonArray()) {
                        certsArr = userObj.get("certificates").getAsJsonArray();
                    } else {
                        certsArr = new JsonArray();
                        userObj.add("certificates", certsArr);
                    }
                    JsonObject certObj = JsonParser.parseString(gson.toJson(cert)).getAsJsonObject();
                    certsArr.add(certObj);
                    updated = true;
                    break;
                }
            }

            if (updated) {
                Writer w = new FileWriter(usersFile);
                gson.toJson(usersArray, w);
                w.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Certificate> getCertificatesForStudent(String studentId) {
        List<Certificate> out = new ArrayList<>();
        try {
            Reader r = new FileReader(usersFile);
            JsonElement root = JsonParser.parseReader(r);
            r.close();

            if (!root.isJsonArray()) {
                return out;
            }
            JsonArray arr = root.getAsJsonArray();

            for (JsonElement userElem : arr) {
                JsonObject userObj = userElem.getAsJsonObject();
                if (!userObj.has("userId")) {
                    continue;
                }

                if (studentId.equals(userObj.get("userId").getAsString())) {
                    if (userObj.has("certificates") && userObj.get("certificates").isJsonArray()) {
                        JsonArray certsArr = userObj.get("certificates").getAsJsonArray();
                        Type type = new TypeToken<List<Certificate>>() {
                        }.getType();
                        out = gson.fromJson(certsArr, type);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }
}
