package GuiApp;

import Core.*;
import Core.Class;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SchoolInfoPanel extends JPanel {
    private SchoolManager manager;
    private JLabel schoolNameLabel;
    private JLabel classCountLabel;
    private JLabel teacherCountLabel;
    private JLabel studentCountLabel;

    public SchoolInfoPanel(SchoolManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Tytuł panelu
        JLabel titleLabel = new JLabel("Informacje o szkole", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Panel z informacjami
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(5, 1, 10, 10));
        infoPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Etykiety z informacjami
        schoolNameLabel = new JLabel("Nazwa szkoły: ");
        schoolNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        classCountLabel = new JLabel("Liczba klas: ");
        classCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        teacherCountLabel = new JLabel("Liczba nauczycieli: ");
        teacherCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        studentCountLabel = new JLabel("Liczba uczniów: ");
        studentCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Dodanie etykiet do panelu
        infoPanel.add(schoolNameLabel);
        infoPanel.add(classCountLabel);
        infoPanel.add(teacherCountLabel);
        infoPanel.add(studentCountLabel);
        infoPanel.add(new JLabel()); // Puste miejsce dla wyrównania

        // Dodanie panelu informacyjnego do głównego panelu
        add(infoPanel, BorderLayout.CENTER);

        // Przycisk odświeżania
        JButton refreshButton = new JButton("Odśwież dane");
        refreshButton.addActionListener(e -> updateInfo());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Inicjalne wypełnienie informacji
        updateInfo();
    }

    // Metoda aktualizująca informacje
    public void updateInfo() {
        schoolNameLabel.setText("Nazwa szkoły: " + manager.getSchoolName());
        classCountLabel.setText("Liczba klas: " + manager.getAllClasses().size());
        teacherCountLabel.setText("Liczba nauczycieli: " + manager.getAllTeachers().size());

        int totalStudents = 0;
        for (Class cls : manager.getAllClasses()) {
            totalStudents += cls.getStudents().size();
        }
        studentCountLabel.setText("Liczba uczniów: " + totalStudents);
    }
}
