package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.UUID;

public class QuizEditorFrame extends JFrame {

    private Lesson lesson;
    private Course course;
    private JsonDatabaseManager db;

    private Quiz currentQuiz;

    private DefaultListModel<String> questionListModel;
    private JList<String> questionJList;

    private JTextField questionField;
    private JTextField choiceAField;
    private JTextField choiceBField;
    private JTextField choiceCField;
    private JTextField choiceDField;
    private JComboBox<String> correctCombo;

    public QuizEditorFrame(Lesson lesson, Course course, JsonDatabaseManager db) {
        this.lesson = lesson;
        this.course = course;
        this.db = db;

        loadQuiz();

        setTitle("Quiz Editor – " + lesson.getTitle());
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        refreshQuestionList();
        setVisible(true);
    }

    private void initUI() {

        // ================= LEFT LIST PANEL =================
        questionListModel = new DefaultListModel<>();
        questionJList = new JList<>(questionListModel);
        questionJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionJList.setFont(new Font("Arial", Font.PLAIN, 14));
        questionJList.setFixedCellHeight(28);

        JScrollPane listScroll = new JScrollPane(questionJList);
        listScroll.setPreferredSize(new Dimension(250, 0));
        listScroll.setBorder(BorderFactory.createTitledBorder("Questions"));

        questionJList.addListSelectionListener(e -> loadSelectedQuestion());

        listScroll.setPreferredSize(new Dimension(0, 180));
        add(listScroll, BorderLayout.NORTH);

        JPanel editor = new JPanel(new GridLayout(7, 2, 10, 10));
        editor.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        questionField = new JTextField();
        choiceAField = new JTextField();
        choiceBField = new JTextField();
        choiceCField = new JTextField();
        choiceDField = new JTextField();

        questionField.setFont(new Font("Arial", Font.PLAIN, 14));
        choiceAField.setFont(new Font("Arial", Font.PLAIN, 14));
        choiceBField.setFont(new Font("Arial", Font.PLAIN, 14));
        choiceCField.setFont(new Font("Arial", Font.PLAIN, 14));
        choiceDField.setFont(new Font("Arial", Font.PLAIN, 14));

        correctCombo = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        correctCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        editor.add(new JLabel("Question:"));
        editor.add(questionField);

        editor.add(new JLabel("Choice A:"));
        editor.add(choiceAField);

        editor.add(new JLabel("Choice B:"));
        editor.add(choiceBField);

        editor.add(new JLabel("Choice C:"));
        editor.add(choiceCField);

        editor.add(new JLabel("Choice D:"));
        editor.add(choiceDField);

        editor.add(new JLabel("Correct Answer:"));
        editor.add(correctCombo);

        listScroll.setPreferredSize(new Dimension(0, 200));
        add(editor, BorderLayout.CENTER);


        // ================= BUTTON PANEL =================

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addBtn = new JButton("Add Question");
        JButton updateBtn = new JButton("Update Question");
        JButton deleteBtn = new JButton("Delete Question");

        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        updateBtn.setFont(new Font("Arial", Font.BOLD, 12));
        deleteBtn.setFont(new Font("Arial", Font.BOLD, 12));

        addBtn.setPreferredSize(new Dimension(150, 40));
        updateBtn.setPreferredSize(new Dimension(150, 40));
        deleteBtn.setPreferredSize(new Dimension(150, 40));

        addBtn.setBackground(new Color(60, 179, 113));
        addBtn.setForeground(Color.WHITE);

        updateBtn.setBackground(new Color(65, 105, 225));
        updateBtn.setForeground(Color.WHITE);

        deleteBtn.setBackground(new Color(220, 80, 60));
        deleteBtn.setForeground(Color.WHITE);

        addBtn.addActionListener(e -> onAddQuestion());
        updateBtn.addActionListener(e -> onUpdateQuestion());
        deleteBtn.addActionListener(e -> onDeleteQuestion());

        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);

        add(buttons, BorderLayout.SOUTH);
    }

    private void loadQuiz() {
        ArrayList<Quiz> qlist = lesson.getQuiz();

        if (qlist == null || qlist.isEmpty()) {
            currentQuiz = null;
            return;
        }

        currentQuiz = qlist.get(0);

        if (currentQuiz.getQuestions() == null) {
            currentQuiz.setQuestions(new ArrayList<>());
        }
    }

    private void refreshQuestionList() {
        questionListModel.clear();

        if (currentQuiz == null) {
            return;
        }

        for (Question q : currentQuiz.getQuestions()) {
            questionListModel.addElement(q.getQuestionText());
        }
    }

    private void loadSelectedQuestion() {
        int idx = questionJList.getSelectedIndex();
        if (idx < 0 || currentQuiz == null) {
            return;
        }

        Question q = currentQuiz.getQuestions().get(idx);

        questionField.setText(q.getQuestionText());
        choiceAField.setText(q.getChoices().get(0));
        choiceBField.setText(q.getChoices().get(1));
        choiceCField.setText(q.getChoices().get(2));
        choiceDField.setText(q.getChoices().get(3));

        correctCombo.setSelectedIndex(q.getCorrectIdx());
    }

    private Question readQuestionFromFields() {
        String text = questionField.getText().trim();
        String A = choiceAField.getText().trim();
        String B = choiceBField.getText().trim();
        String C = choiceCField.getText().trim();
        String D = choiceDField.getText().trim();

        if (text.isEmpty() || A.isEmpty() || B.isEmpty() || C.isEmpty() || D.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields are required!",
                    "Missing Data",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        ArrayList<String> choices = new ArrayList<>();
        choices.add(A);
        choices.add(B);
        choices.add(C);
        choices.add(D);

        return new Question(text, choices, correctCombo.getSelectedIndex());
    }

    private void onAddQuestion() {
        Question q = readQuestionFromFields();
        if (q == null) {
            return;
        }

        ArrayList<Quiz> quizzes = lesson.getQuiz();
        if (quizzes == null) {
            quizzes = new ArrayList<>();
            lesson.setQuiz(quizzes);
        }

        if (quizzes.isEmpty()) {
            currentQuiz = new Quiz(
                    "Q" + UUID.randomUUID().toString().substring(0, 8),
                    lesson.getLessonId(),
                    new ArrayList<>()
            );
            quizzes.add(currentQuiz);
        } else {
            currentQuiz = quizzes.get(0);
        }

        currentQuiz.getQuestions().add(q);

        persist();
        refreshQuestionList();
        questionJList.setSelectedIndex(questionListModel.size() - 1);

        JOptionPane.showMessageDialog(this, "Question added.");
    }

    private void onUpdateQuestion() {
        int idx = questionJList.getSelectedIndex();
        if (idx < 0 || currentQuiz == null) {
            return;
        }

        Question q = readQuestionFromFields();
        if (q == null) {
            return;
        }

        currentQuiz.getQuestions().set(idx, q);
        persist();
        refreshQuestionList();

        JOptionPane.showMessageDialog(this, "Question updated.");
    }

    private void onDeleteQuestion() {
        int idx = questionJList.getSelectedIndex();
        if (idx < 0 || currentQuiz == null) {
            return;
        }

        currentQuiz.getQuestions().remove(idx);

        if (currentQuiz.getQuestions().isEmpty()) {
            ArrayList<Quiz> qlist = lesson.getQuiz();
            qlist.clear();
            currentQuiz = null;
        }

        persist();
        refreshQuestionList();
        clearEditorFields();
    }

    private void clearEditorFields() {
        questionField.setText("");
        choiceAField.setText("");
        choiceBField.setText("");
        choiceCField.setText("");
        choiceDField.setText("");
        correctCombo.setSelectedIndex(0);
    }

    private void persist() {
        db.putCourse(course);
    }
}
