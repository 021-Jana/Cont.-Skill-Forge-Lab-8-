
package lab7;

import java.util.Date;
import java.util.List;

public class QuizServices {

    
    public int calculateScore(Quiz quiz, List<Integer> studentAnswers) {
        int score = 0;
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            if (quiz.getQuestions().get(i).getCorrectIdx() == studentAnswers.get(i)) {
                score++;
            }
        }
        return score;
    }

    public void recordAttempt(Student student, Quiz quiz, List<Integer> answers) {
        int score = calculateScore(quiz, answers);
        QuizAttempts attempt = new QuizAttempts(quiz.getQuizId(), score, new Date());
        student.addQuizAttempt(attempt, 85);
    }

    public boolean hasPassed(Student student, Quiz quiz) {
        QuizAttempts lastAttempt = student.getLastQuizAttempt(quiz.getQuizId());
        if (lastAttempt == null) return false;
            double percentage = (lastAttempt.getScore() * 100.0) / quiz.getQuestions().size();
            return percentage >= 50;
    }

}
