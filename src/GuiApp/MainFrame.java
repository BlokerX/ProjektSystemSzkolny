package GuiApp;

import Core.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;

public class MainFrame extends JFrame {
    private School school;
    private SchoolManager manager;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Panele dla różnych widoków
    private SchoolInfoPanel schoolInfoPanel;
    private ClassManagementPanel classManagementPanel;
    private TeacherManagementPanel teacherManagementPanel;
    private StudentManagementPanel studentManagementPanel;
    private SubjectManagementPanel subjectManagementPanel;
    private GradeManagementPanel gradeManagementPanel;
    public StatisticsPanel statisticsPanel;

    private static final String DATA_FILE = "school_data.ser";


    public MainFrame() {
        setTitle("System Zarządzania Szkołą");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Próba wczytania danych przy starcie
        try {
            manager = SchoolManager.loadFromFile(DATA_FILE);
            school = manager.getSchool();
            JOptionPane.showMessageDialog(this, "Dane wczytane pomyślnie!");
        } catch (IOException | ClassNotFoundException e) {
            // Jeśli plik nie istnieje, utwórz nową szkołę z danymi testowymi
            school = School.createSchool("Liceum im. Marii Skłodowskiej-Curie");
            manager = new SchoolManager(school);
            initializeSchoolData(manager);
            JOptionPane.showMessageDialog(this, "Utworzono nową szkołę z danymi testowymi");
        }

        // Ustawienie głównego layoutu
        setLayout(new BorderLayout());

        // Panel tytułowy
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Panel menu
        JPanel menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.WEST);

        // Panel zawartości z CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(contentPanel, BorderLayout.CENTER);

        // Inicjalizacja paneli funkcyjnych
        initializePanels();

        // Pokazanie domyślnego panelu informacyjnego
        cardLayout.show(contentPanel, "SchoolInfo");

        // Dodanie opcji zapisu w menu
        addMenuButton(menuPanel, "Zapisz dane", e -> saveData());
    }

    private void saveData() {
        try {
            manager.saveToFile(DATA_FILE);
            JOptionPane.showMessageDialog(this, "Dane zapisane pomyślnie!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Błąd zapisu danych: " + e.getMessage());
        }
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(51, 102, 153));
        panel.setPreferredSize(new Dimension(800, 60));
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("SYSTEM ZARZĄDZANIA SZKOŁĄ", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 500));
        panel.setBackground(new Color(240, 240, 240));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Przyciski menu
        addMenuButton(panel, "Informacje o szkole", e -> cardLayout.show(contentPanel, "SchoolInfo"));
        addMenuButton(panel, "Zarządzaj klasami", e -> cardLayout.show(contentPanel, "ClassManagement"));
        addMenuButton(panel, "Zarządzaj nauczycielami", e -> cardLayout.show(contentPanel, "TeacherManagement"));
        addMenuButton(panel, "Zarządzaj uczniami", e -> cardLayout.show(contentPanel, "StudentManagement"));
        addMenuButton(panel, "Zarządzaj przedmiotami", e -> cardLayout.show(contentPanel, "SubjectManagement"));
        addMenuButton(panel, "Zarządzaj ocenami", e -> cardLayout.show(contentPanel, "GradeManagement"));
        addMenuButton(panel, "Statystyki", e -> cardLayout.show(contentPanel, "Statistics"));
        addMenuButton(panel, "Wyjście", e -> System.exit(0));

        // Dodanie elastycznego wypełniacza, aby przyciski były wyrównane od góry
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void addMenuButton(JPanel panel, String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.addActionListener(listener);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setFocusPainted(false);
        panel.add(button);
        panel.add(Box.createVerticalStrut(10));
    }

    private void initializePanels() {
        // Inicjalizacja wszystkich paneli funkcyjnych
        schoolInfoPanel = new SchoolInfoPanel(manager);
        contentPanel.add(schoolInfoPanel, "SchoolInfo");

        classManagementPanel = new ClassManagementPanel(manager);
        contentPanel.add(classManagementPanel, "ClassManagement");

        teacherManagementPanel = new TeacherManagementPanel(manager);
        contentPanel.add(teacherManagementPanel, "TeacherManagement");

        studentManagementPanel = new StudentManagementPanel(manager);
        contentPanel.add(studentManagementPanel, "StudentManagement");

        subjectManagementPanel = new SubjectManagementPanel(manager);
        contentPanel.add(subjectManagementPanel, "SubjectManagement");

        gradeManagementPanel = new GradeManagementPanel(manager);
        contentPanel.add(gradeManagementPanel, "GradeManagement");

        statisticsPanel = new StatisticsPanel(manager);
        contentPanel.add(statisticsPanel, "Statistics");
    }

    // Inicjalizacja przykładowych danych szkoły - wykorzystanie istniejącego kodu
    private void initializeSchoolData(SchoolManager manager) {
        // Dodanie nauczycieli
        manager.hireTeacher("Jan", "Kowalski", 45, "65072512345", 5500.0);
        manager.hireTeacher("Anna", "Nowak", 38, "83021987654", 5200.0);
        manager.hireTeacher("Piotr", "Wiśniewski", 52, "70112334567", 6000.0);
        manager.hireTeacher("Magdalena", "Kowalczyk", 41, "80052265432", 5800.0);

        // Pobranie nauczycieli do utworzenia klas
        List<Teacher> teachers = manager.getAllTeachers();

        // Utworzenie klas
        manager.createClass("1A", teachers.get(0)); // Jan Kowalski - wychowawca klasy 1A
        manager.createClass("2B", teachers.get(1)); // Anna Nowak - wychowawca klasy 2B

        // Dodanie uczniów do klasy 1A
        manager.enrollStudent("Adam", "Malinowski", 15, "07241012345", "1A");
        manager.enrollStudent("Ewa", "Zielińska", 15, "07052234567", "1A");
        manager.enrollStudent("Michał", "Dąbrowski", 16, "06121087654", "1A");

        // Dodanie uczniów do klasy 2B
        manager.enrollStudent("Karolina", "Lewandowska", 16, "06082143215", "2B");
        manager.enrollStudent("Tomasz", "Jankowski", 17, "05112265432", "2B");
        manager.enrollStudent("Natalia", "Szymańska", 16, "06062198765", "2B");

        // Utworzenie przedmiotów dla klasy 1A
        manager.createSubject("Matematyka", "1A", "65072512345"); // Jan Kowalski
        manager.createSubject("Fizyka", "1A", "70112334567"); // Piotr Wiśniewski
        manager.createSubject("Język Polski", "1A", "80052265432"); // Magdalena Kowalczyk

        // Utworzenie przedmiotów dla klasy 2B
        manager.createSubject("Matematyka", "2B", "65072512345"); // Jan Kowalski
        manager.createSubject("Biologia", "2B", "83021987654"); // Anna Nowak
        manager.createSubject("Język Polski", "2B", "80052265432"); // Magdalena Kowalczyk

        // Dodanie przykładowych ocen
        manager.addGradeToStudent("07241012345", "1A", "Matematyka", 5, 2, "Sprawdzian - Równania", "65072512345");
        manager.addGradeToStudent("07052234567", "1A", "Fizyka", 4, 3, "Sprawdzian - Mechanika", "70112334567");
        manager.addGradeToStudent("06082143215", "2B", "Matematyka", 5, 2, "Sprawdzian - Logarytmy", "65072512345");
    }

    public static void main(String[] args) {
        try {
            // Ustawienie wyglądu systemu
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}