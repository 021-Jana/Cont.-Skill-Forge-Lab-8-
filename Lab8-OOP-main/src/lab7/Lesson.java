package lab7;

import java.util.ArrayList;

public class Lesson {
    private String lessonId;
    private String title;
    private String content;
    private ArrayList<String> resources;
    private ArrayList<Quiz> quiz;

    public Lesson() {
        resources = new ArrayList<>();
    }
    
    public Lesson(String lessonId, String title, String content) {
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;
        this.resources = new ArrayList<>();
    }
    
    
    public String getLessonId() { return lessonId; }
    public void setLessonId(String id) { this.lessonId = id; }
    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }
    public String getContent() { return content; }
    public void setContent(String c) { this.content = c; }
    public ArrayList<String> getResources() { return resources; }
    public void setResources(ArrayList<String> r) { this.resources = r; }

    public void setQuiz(ArrayList<Quiz> quiz){ this.quiz = quiz; }
    public ArrayList<Quiz> getQuiz(){
        if(quiz==null)
            quiz = new ArrayList<>();
        return quiz;
    }
    
    @Override
    public String toString() {
        return title;
    }
}