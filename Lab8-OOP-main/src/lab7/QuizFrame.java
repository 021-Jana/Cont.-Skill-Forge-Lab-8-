package lab7;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class QuizFrame extends JFrame {

    private Quiz quiz;
    private Student student;
    private QuizServices quizService;
    private Lesson lesson;
    private Course course;
    private List<ButtonGroup> answerGroups;
    private JButton submitBtn;

    public QuizFrame(Quiz quiz, Student student, QuizServices quizService, Lesson lesson, Course course) {
        this.quiz = quiz;
        this.student = student;
        this.quizService = quizService;
        this.answerGroups = new ArrayList<>();
        this.lesson = lesson;
        this.course = course;

        setTitle("Quiz - Lesson: " + quiz.getLessonId());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setupUI();
        setVisible(true);
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            Question q = quiz.getQuestions().get(i);

            JPanel questionPanel = new JPanel();
            questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
            questionPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 10, 10, 10),
                    BorderFactory.createTitledBorder("Question " + (i + 1))
            ));
            questionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel questionLabel = new JLabel("<html><b>" + q.getQuestionText() + "</b></html>");
            questionPanel.add(questionLabel);

            questionPanel.add(Box.createVerticalStrut(8));

            ButtonGroup group = new ButtonGroup();

            for (int j = 0; j < q.getChoices().size(); j++) {
                JRadioButton optionButton = new JRadioButton(q.getChoices().get(j));
                optionButton.setActionCommand(String.valueOf(j));
                optionButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                group.add(optionButton);
                questionPanel.add(optionButton);
            }

            answerGroups.add(group);
            mainPanel.add(questionPanel);
            mainPanel.add(Box.createVerticalStrut(12));
        }

        submitBtn = new JButton("Submit Quiz");
        submitBtn.setBackground(new Color(60, 179, 113));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        submitBtn.addActionListener(e -> submitQuiz());

        mainPanel.add(submitBtn);
        
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.setMaximumSize(new Dimension(700, Integer.MAX_VALUE));
        centerWrapper.add(mainPanel);
        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);

    }

    private void submitQuiz() {
        if (!answerGroups.get(0).getElements().nextElement().isEnabled()) {
            JOptionPane.showMessageDialog(this, "You already submitted this quiz.", "Submission Blocked",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Integer> studentAnswers = new ArrayList<>();

        for (ButtonGroup group : answerGroups) {
            String selected = null;

            for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
                AbstractButton b = buttons.nextElement();
                if (b.isSelected()) {
                    selected = b.getActionCommand();
                    break;
                }
            }

            if (selected == null) {
                JOptionPane.showMessageDialog(this,
                        "Please answer all questions!",
                        "Incomplete Quiz",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            studentAnswers.add(Integer.parseInt(selected));
        }

        quizService.recordAttempt(student, quiz, studentAnswers);
        boolean passed = quizService.hasPassed(student, quiz);
        int score = quizService.calculateScore(quiz, studentAnswers);

        QuizAttempts attempt = new QuizAttempts(quiz.getQuizId(), score, new java.util.Date());
        student.addQuizAttempt(attempt, 85);

        // mark lesson completed
        if (passed) {
            course.markLessonCompleted(student.getUserId(), lesson.getLessonId());
        }

        JsonDatabaseManager db = new JsonDatabaseManager("data/courses.json", "data/users.json");
        db.putUser(student);
        db.putCourse(course);
        JOptionPane.showMessageDialog(this,
                "Quiz submitted! Score: " + score + " - You " + (passed ? "passed ✓" : "failed ✗"),
                "Quiz Result",
                JOptionPane.INFORMATION_MESSAGE);

        submitBtn.setEnabled(false);
        submitBtn.setText("Submitted");
        showCorrectAnswers(studentAnswers);

        CertificateManager cm = new CertificateManager(db);
        Certificate cert = cm.generateCertificateIfEligible(student.getUserId(), course.getCourseId());
        if (cert != null) {
            JOptionPane.showMessageDialog(this,
                    "Congratulations! You earned a certificate for completing " + course.getTitle(),
                    "Certificate Awarded",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showCorrectAnswers(List<Integer> studentAnswers) {

        for (int i = 0; i < quiz.getQuestions().size(); i++) {

            Question q = quiz.getQuestions().get(i);
            int correctIndex = q.getCorrectIdx();
            int selectedIndex = studentAnswers.get(i);

            ButtonGroup group = answerGroups.get(i);
            Enumeration<AbstractButton> buttons = group.getElements();

            int index = 0;
            while (buttons.hasMoreElements()) {

                JRadioButton btn = (JRadioButton) buttons.nextElement();

                btn.setOpaque(true);
                btn.setFocusable(false);

                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);

                if (index == correctIndex) {
                    btn.setBackground(new Color(144, 238, 144));  // Light green
                    btn.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 0), 2));
                    btn.setFont(btn.getFont().deriveFont(Font.BOLD));
                }

                if (index == selectedIndex && selectedIndex != correctIndex) {
                    btn.setBackground(new Color(255, 182, 193)); // Light red
                    btn.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                    btn.setForeground(Color.BLACK);
                }

                btn.setEnabled(false);

                index++;
            }
        }
    }
}
