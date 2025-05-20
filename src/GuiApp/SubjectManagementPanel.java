package GuiApp;

import Core.*;
import Core.Class;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SubjectManagementPanel extends JPanel {
    private SchoolManager manager;
    private JTable subjectTable;
    private DefaultTableModel tableModel;
    private JTextArea subjectDetailsArea;
    private JComboBox<String> classFilterComboBox;

    public SubjectManagementPanel(SchoolManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel title
        JLabel titleLabel = new JLabel("Zarządzanie przedmiotami", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Split panel - left: subject list, right: details/actions
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        add(splitPane, BorderLayout.CENTER);

        // Left side - subject list
        JPanel subjectListPanel = createSubjectListPanel();
        splitPane.setLeftComponent(subjectListPanel);

        // Right side - details and actions panel
        JPanel detailsPanel = createDetailsPanel();
        splitPane.setRightComponent(detailsPanel);

        // Refresh data
        refreshSubjectData();
    }

    private JPanel createSubjectListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Panel for class filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filtruj według klasy:"));

        classFilterComboBox = new JComboBox<>();
        classFilterComboBox.addItem("Wszystkie klasy");
        for (Class cls : manager.getAllClasses()) {
            classFilterComboBox.addItem(cls.getName());
        }
        classFilterComboBox.addActionListener(e -> refreshSubjectData());
        filterPanel.add(classFilterComboBox);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Create table model
        String[] columnNames = {"Nazwa przedmiotu", "Klasa", "Nauczyciel"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable cells
            }
        };
        subjectTable = new JTable(tableModel);
        subjectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subjectTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && subjectTable.getSelectedRow() != -1) {
                String subjectName = (String) subjectTable.getValueAt(subjectTable.getSelectedRow(), 0);
                String className = (String) subjectTable.getValueAt(subjectTable.getSelectedRow(), 1);
                displaySubjectDetails(subjectName, className);
            }
        });

        JScrollPane scrollPane = new JScrollPane(subjectTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel under the table
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshButton = new JButton("Odśwież");
        refreshButton.addActionListener(e -> refreshSubjectData());
        buttonPanel.add(refreshButton);

        JButton addButton = new JButton("Dodaj przedmiot");
        addButton.addActionListener(e -> showAddSubjectDialog());
        buttonPanel.add(addButton);

        JButton removeButton = new JButton("Usuń przedmiot");
        removeButton.addActionListener(e -> removeSelectedSubject());
        buttonPanel.add(removeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel detailsLabel = new JLabel("Szczegóły przedmiotu");
        detailsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(detailsLabel, BorderLayout.NORTH);

        subjectDetailsArea = new JTextArea();
        subjectDetailsArea.setEditable(false);
        subjectDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        subjectDetailsArea.setLineWrap(true);
        subjectDetailsArea.setWrapStyleWord(true);
        JScrollPane detailsScrollPane = new JScrollPane(subjectDetailsArea);
        panel.add(detailsScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshSubjectData() {
        // Clear the table
        tableModel.setRowCount(0);

        String selectedClassName = (String) classFilterComboBox.getSelectedItem();

        // Get classes based on filter
        for (Class cls : manager.getAllClasses()) {
            if ("Wszystkie klasy".equals(selectedClassName) || cls.getName().equals(selectedClassName)) {
                // Get students to extract unique subjects for the class
                List<Student> students = cls.getStudents();
                if (!students.isEmpty()) {
                    // Take first student to get subject list (assuming all students in a class have the same subjects)
                    Student firstStudent = students.get(0);
                    for (Subject subject : firstStudent.getSubjects()) {
                        tableModel.addRow(new Object[]{
                                subject.getName(),
                                cls.getName(),
                                subject.getLeadingTeacher().getName() + " " + subject.getLeadingTeacher().getSurname()
                        });
                    }
                }
            }
        }

        // Clear details if no subject is selected
        if (subjectTable.getSelectedRow() == -1) {
            subjectDetailsArea.setText("");
        }
    }

    private void displaySubjectDetails(String subjectName, String className) {
        Class targetClass = manager.getClassByName(className);
        if (targetClass != null) {
            List<Student> students = targetClass.getStudents();
            if (!students.isEmpty()) {
                // Find the subject in the first student (assuming all have the same subjects)
                Student firstStudent = students.get(0);
                for (Subject subject : firstStudent.getSubjects()) {
                    if (subject.getName().equals(subjectName)) {
                        Teacher teacher = subject.getLeadingTeacher();

                        StringBuilder details = new StringBuilder();
                        details.append("Nazwa przedmiotu: ").append(subject.getName())
                                .append("\n\nKlasa: ").append(className)
                                .append("\n\nNauczyciel prowadzący: ").append(teacher.getName())
                                .append(" ").append(teacher.getSurname());

                        // Calculate the average grade for this subject in this class
                        double classAverage = manager.getClassAverageGrade(className, subjectName);
                        if (classAverage >= 0) {
                            details.append("\n\nŚrednia klasy z przedmiotu: ")
                                    .append(String.format("%.2f", classAverage));
                        } else {
                            details.append("\n\nŚrednia klasy z przedmiotu: brak ocen");
                        }

                        // List students with their grades
                        details.append("\n\nLista uczniów i ich oceny:");

                        boolean hasGrades = false;
                        for (Student student : students) {
                            Subject studentSubject = null;
                            // Find the subject for this student
                            for (Subject s : student.getSubjects()) {
                                if (s.getName().equals(subjectName)) {
                                    studentSubject = s;
                                    break;
                                }
                            }

                            if (studentSubject != null) {
                                details.append("\n\n- ").append(student.getName())
                                        .append(" ").append(student.getSurname());

                                List<Grade> grades = studentSubject.getGrades();
                                if (grades.isEmpty()) {
                                    details.append("\n  Brak ocen");
                                } else {
                                    hasGrades = true;
                                    for (Grade grade : grades) {
                                        details.append("\n  Ocena: ").append(grade.getGrade())
                                                .append(" (waga: ").append(grade.getWeight()).append(")")
                                                .append(" - ").append(grade.getDescription());
                                    }
                                    details.append("\n  Średnia: ")
                                            .append(String.format("%.2f", studentSubject.getAverageGrade()));
                                }
                            }
                        }

                        if (!hasGrades) {
                            details.append("\nBrak ocen dla wszystkich uczniów z tego przedmiotu.");
                        }

                        subjectDetailsArea.setText(details.toString());
                        return;
                    }
                }
            }
            subjectDetailsArea.setText("Nie znaleziono szczegółów przedmiotu.");
        } else {
            subjectDetailsArea.setText("Nie znaleziono klasy.");
        }
    }

    private void showAddSubjectDialog() {
        // Check if there are available classes and teachers
        List<Class> classes = manager.getAllClasses();
        List<Teacher> teachers = manager.getAllTeachers();

        if (classes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Brak dostępnych klas. Dodaj klasę przed dodaniem przedmiotu.",
                    "Brak klas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (teachers.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Brak dostępnych nauczycieli. Dodaj nauczyciela przed dodaniem przedmiotu.",
                    "Brak nauczycieli", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        panel.add(new JLabel("Nazwa przedmiotu:"));
        JTextField subjectNameField = new JTextField();
        panel.add(subjectNameField);

        panel.add(new JLabel("Klasa:"));
        DefaultComboBoxModel<String> classModel = new DefaultComboBoxModel<>();
        for (Class cls : classes) {
            classModel.addElement(cls.getName());
        }
        JComboBox<String> classComboBox = new JComboBox<>(classModel);
        panel.add(classComboBox);

        panel.add(new JLabel("Nauczyciel:"));
        DefaultComboBoxModel<String> teacherModel = new DefaultComboBoxModel<>();
        for (Teacher teacher : teachers) {
            teacherModel.addElement(teacher.getName() + " " + teacher.getSurname() + " (" + teacher.getPeselNumber() + ")");
        }
        JComboBox<String> teacherComboBox = new JComboBox<>(teacherModel);
        panel.add(teacherComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Dodaj nowy przedmiot",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String subjectName = subjectNameField.getText().trim();
            String className = (String) classComboBox.getSelectedItem();
            String teacherInfo = (String) teacherComboBox.getSelectedItem();

            if (subjectName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nazwa przedmiotu nie może być pusta.",
                        "Błąd danych", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Extract teacher PESEL from selection
            String teacherPesel = teacherInfo.substring(teacherInfo.lastIndexOf("(") + 1, teacherInfo.lastIndexOf(")"));

            // Check if subject already exists in this class
            Class targetClass = manager.getClassByName(className);
            if (targetClass != null && !targetClass.getStudents().isEmpty()) {
                Student firstStudent = targetClass.getStudents().get(0);
                for (Subject subject : firstStudent.getSubjects()) {
                    if (subject.getName().equals(subjectName)) {
                        JOptionPane.showMessageDialog(this, "Przedmiot o nazwie " + subjectName +
                                        " już istnieje w klasie " + className + ".",
                                "Duplikat przedmiotu", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            manager.createSubject(subjectName, className, teacherPesel);
            refreshSubjectData();
            updateClassFilterComboBox();
            JOptionPane.showMessageDialog(this,
                    "Przedmiot " + subjectName + " został dodany do klasy " + className + ".");
        }
    }

    private void removeSelectedSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow != -1) {
            String subjectName = (String) subjectTable.getValueAt(selectedRow, 0);
            String className = (String) subjectTable.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Czy na pewno chcesz usunąć przedmiot " + subjectName + " z klasy " + className + "?\n" +
                            "Wszystkie oceny związane z tym przedmiotem również zostaną usunięte.",
                    "Potwierdzenie usunięcia", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Niestety, w SchoolManager nie ma bezpośredniej metody do usuwania przedmiotów
                // Zazwyczaj musielibyśmy zaimplementować tę metodę w SchoolManager
                // Poniżej informacja dla użytkownika o braku funkcjonalności
                JOptionPane.showMessageDialog(this,
                        "Funkcjonalność usuwania przedmiotu nie jest jeszcze zaimplementowana w systemie.",
                        "Funkcjonalność niedostępna", JOptionPane.INFORMATION_MESSAGE);

                // Odświeżamy dane dla spójności
                refreshSubjectData();
                subjectDetailsArea.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Wybierz przedmiot do usunięcia.",
                    "Brak wyboru", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateClassFilterComboBox() {
        String selectedItem = (String) classFilterComboBox.getSelectedItem();
        classFilterComboBox.removeAllItems();
        classFilterComboBox.addItem("Wszystkie klasy");

        for (Class cls : manager.getAllClasses()) {
            classFilterComboBox.addItem(cls.getName());
        }

        // Try to restore previous selection
        if (selectedItem != null) {
            for (int i = 0; i < classFilterComboBox.getItemCount(); i++) {
                if (selectedItem.equals(classFilterComboBox.getItemAt(i))) {
                    classFilterComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
}