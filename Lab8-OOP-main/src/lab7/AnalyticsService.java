/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DELL
 */
public class AnalyticsService {

    private JsonDatabaseManager db;

    public AnalyticsService(JsonDatabaseManager db) {
        this.db = db;
    }

    
    // 1) Lesson Completion %
   
    public Map<String, Double> computeLessonCompletion(String courseId) {

        Map<String, Double> results = new HashMap<>();

        Course course = db.getCourse(courseId);
        if (course == null) return results;

        List<Lesson> lessons = course.getLessons();

        ArrayList<User> users = db.getUsers();

        // enrolled Students
        int registered = 0;
        for (User u : users) {
            if (u instanceof Student) {
                Student s = (Student) u;
                if (s.getEnrolledCourses().contains(courseId)) {
                    registered++;
                }
            }
        }

        if (registered == 0) return results;

        // students who completed each lesson
        for (Lesson lesson : lessons) {
            int completed = 0;

            for (User u : users) {
                if (u instanceof Student) {
                    Student s = (Student) u;

                    if (s.getEnrolledCourses().contains(courseId) &&
                        s.isLessonCompleted(courseId, lesson.getLessonId())) {

                        completed++;
                    }
                }
            }

            double percentage = (completed * 100.0) / registered;
            results.put(lesson.getLessonId(), percentage);
        }

        return results;
    }

    // --------------------------------------------------------------
    // 2) Quiz Average Score
    // --------------------------------------------------------------
    public Map<String, Double> computeQuizAverage(String courseId) {

        Map<String, Double> results = new HashMap<>();

        Course course = db.getCourse(courseId);
        if (course == null) return results;

        for (Lesson lesson : course.getLessons()) {

            if (lesson.getQuiz() != null) {

                for (Quiz quiz : lesson.getQuiz()) {

                    String quizId = quiz.getQuizId();

                    double total = 0;
                    int count = 0;

                    for (User u : db.getUsers()) {
                        if (u instanceof Student) {
                            Student s = (Student) u;

                            QuizAttempts attempt = s.getLastQuizAttempt(quizId);
                            if (attempt != null) {
                                total += attempt.getScore();
                                count++;
                            }
                        }
                    }

                    if (count > 0) {
                        results.put(quizId, total / count);
                    }
                }
            }
        }

        return results;
    }

    // --------------------------------------------------------------
    // 3) Student Progress %
    // --------------------------------------------------------------
    public Map<String, Integer> computeStudentProgress(String courseId) {

        Map<String, Integer> results = new HashMap<>();

        Course course = db.getCourse(courseId);
        if (course == null) return results;

        int totalLessons = course.getLessons().size();

        for (User u : db.getUsers()) {
            if (u instanceof Student) {

                Student s = (Student) u;

                if (s.getEnrolledCourses().contains(courseId)) {

                    int completedLessons = s.getCompletedLessons(courseId).size();

                    int progress = (int) ((completedLessons * 100.0) / totalLessons);

                    results.put(s.getUserId(), progress);
                }
            }
        }

        return results;
    }
    public void updateAnalytics(String courseId) {
    Map<String, Double> lessonCompletion = computeLessonCompletion(courseId);
    Map<String, Double> quizAverages = computeQuizAverage(courseId);

    db.updateCourseAnalytics(courseId, lessonCompletion, quizAverages);
}


}
