package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.util.List;
import java.io.File;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StudentCertificatesPanel extends JPanel {

    private final Student student;
    private final JsonDatabaseManager db;
    private final CertificateManager certManager;
    private final DefaultListModel<Certificate> model = new DefaultListModel<>();
    private final JList<Certificate> list;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public StudentCertificatesPanel(Student student, JsonDatabaseManager db) {
        this.student = student;
        this.db = db;
        this.certManager = new CertificateManager(db);

        setLayout(new BorderLayout(10,10));
        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton refreshBtn = new JButton("Refresh");
        JButton viewBtn = new JButton("View JSON");
        JButton downloadBtn = new JButton("Save JSON");

        refreshBtn.addActionListener(this::refreshList);
        viewBtn.addActionListener(this::viewSelected);
        downloadBtn.addActionListener(this::downloadSelected);

        btnPanel.add(refreshBtn);
        btnPanel.add(viewBtn);
        btnPanel.add(downloadBtn);
        add(btnPanel, BorderLayout.SOUTH);

        refreshList(null);
    }

    private void refreshList(ActionEvent e) {
        model.clear();
        List<Certificate> certs = certManager.getCertificatesForStudent(student.getUserId());
        for (Certificate c : certs) model.addElement(c);
    }

    private void viewSelected(ActionEvent e) {
        Certificate c = list.getSelectedValue();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Select a certificate first.");
            return;
        }
        String json = gson.toJson(c);
        JTextArea area = new JTextArea(json);
        area.setEditable(false);
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(600,400));
        JOptionPane.showMessageDialog(this, sp, "Certificate JSON", JOptionPane.INFORMATION_MESSAGE);
    }

    private void downloadSelected(ActionEvent e) {
        Certificate c = list.getSelectedValue();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Select a certificate first.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(c.getCertificateId() + ".json"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                gson.toJson(c, fw);
                JOptionPane.showMessageDialog(this, "Saved.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }
}