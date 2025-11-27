/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ChartFrame extends JFrame {

    private Map<String, Double> lessonCompletion;
    private Map<String, Double> quizAverages;

    public ChartFrame(String title, Map<String, Double> lessonCompletion, Map<String, Double> quizAverages) {
        super(title);
        this.lessonCompletion = lessonCompletion;
        this.quizAverages = quizAverages;

        setLayout(new GridLayout(2, 1)); 

        add(new ChartPanel(lessonCompletion, "Lesson Completion %", Color.BLUE));

        
        add(new ChartPanel(quizAverages, "Quiz Average %", Color.GREEN));
    }

  
    private static class ChartPanel extends JPanel {

        private Map<String, Double> data;
        private String title;
        private Color color;

        public ChartPanel(Map<String, Double> data, String title, Color color) {
            this.data = data;
            this.title = title;
            this.color = color;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (data == null || data.isEmpty()) return;

            int width = getWidth();
            int height = getHeight();
            int padding = 50;
            int barWidth = (width - 2 * padding) / data.size();

            
            g.setColor(Color.BLACK);
            g.drawString(title, padding, 20);

            int i = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                int barHeight = (int) ((entry.getValue() / 100.0) * (height - 2 * padding));
                int x = padding + i * barWidth;
                int y = height - padding - barHeight;

                
                g.setColor(color);
                g.fillRect(x, y, barWidth - 10, barHeight);

                
                g.setColor(Color.BLACK);
                g.drawString(entry.getKey(), x, height - padding + 15);

                
                g.drawString(String.format("%.1f", entry.getValue()), x, y - 5);

                i++;
            }
        }
    }
}

    

