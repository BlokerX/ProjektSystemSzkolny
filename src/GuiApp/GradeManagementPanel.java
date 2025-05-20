package GuiApp;

import Core.*;
import Core.Class;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GradeManagementPanel extends JPanel {
    private SchoolManager manager;
    private JTable gradesTable;
    private DefaultTableModel tableModel;
    private JTextArea gradeDetailsArea;
    private JComboBox<String> classComboBox;
    private JComboBox<String> studentComboBox;
    private JComboBox<String> subjectComboBox;

    public GradeManagementPanel(SchoolManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel tytuł
        JLabel titleLabel = new JLabel("Zarządzanie ocenami", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Panel podzielony - lewa: lista ocen, prawa: szczegóły/akcje
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        add(splitPane, BorderLayout.CENTER);

        // Lewa strona - lista ocen
        JPanel gradesListPanel = createGradesListPanel();
        splitPane.setLeftComponent(gradesListPanel);

        // Prawa strona - panel szczegółów i akcji
        JPanel detailsPanel = createDetailsPanel();
        splitPane.setRightComponent(detailsPanel);

        // Odświeżenie danych
        refreshClassComboBox();
    }

    private JPanel createGradesListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Panel filtrów
        JPanel filterPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        // Filtr klasy
        filterPanel.add(new JLabel("Klasa:"));
        classComboBox = new JComboBox<>();
        classComboBox.addActionListener(e -> {
            refreshStudentComboBox();
            refreshSubjectComboBox();
            refreshGradesData();
        });
        filterPanel.add(classComboBox);

        // Filtr ucznia
        filterPanel.add(new JLabel("Uczeń:"));
        studentComboBox = new JComboBox<>();
        studentComboBox.addActionListener(e -> refreshGradesData());
        filterPanel.add(studentComboBox);

        // Filtr przedmiotu
        filterPanel.add(new JLabel("Przedmiot:"));
        subjectComboBox = new JComboBox<>();
        subjectComboBox.addItem("Wszystkie przedmioty");
        subjectComboBox.addActionListener(e -> refreshGradesData());
        filterPanel.add(subjectComboBox);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Tabela ocen
        String[] columnNames = {"Uczeń", "Przedmiot", "Ocena", "Waga", "Opis"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Komórki nieedytowalne
            }
        };
        gradesTable = new JTable(tableModel);
        gradesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && gradesTable.getSelectedRow() != -1) {
                displayGradeDetails(gradesTable.getSelectedRow());
            }
        });

        JScrollPane scrollPane = new JScrollPane(gradesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel przycisków pod tabelą
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshButton = new JButton("Odśwież");
        refreshButton.addActionListener(e -> refreshGradesData());
        buttonPanel.add(refreshButton);

        JButton addButton = new JButton("Dodaj ocenę");
        addButton.addActionListener(e -> showAddGradeDialog());
        buttonPanel.add(addButton);

        JButton removeButton = new JButton("Usuń ocenę");
        removeButton.addActionListener(e -> removeSelectedGrade());
        buttonPanel.add(removeButton);

        JButton editButton = new JButton("Edytuj ocenę");
        editButton.addActionListener(e -> editSelectedGrade());
        buttonPanel.add(editButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel detailsLabel = new JLabel("Szczegóły oceny");
        detailsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(detailsLabel, BorderLayout.NORTH);

        gradeDetailsArea = new JTextArea();
        gradeDetailsArea.setEditable(false);
        gradeDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        gradeDetailsArea.setLineWrap(true);
        gradeDetailsArea.setWrapStyleWord(true);
        JScrollPane detailsScrollPane = new JScrollPane(gradeDetailsArea);
        panel.add(detailsScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshClassComboBox() {
        classComboBox.removeAllItems();
        classComboBox.addItem("Wszystkie klasy");

        for (Class cls : manager.getAllClasses()) {
            classComboBox.addItem(cls.getName());
        }

        refreshStudentComboBox();
        refreshSubjectComboBox();
        refreshGradesData();
    }

    private void refreshStudentComboBox() {
        String selectedClassName = (String) classComboBox.getSelectedItem();
        studentComboBox.removeAllItems();
        studentComboBox.addItem("Wszyscy uczniowie");

        if (selectedClassName != null && !"Wszystkie klasy".equals(selectedClassName)) {
            Class selectedClass = manager.getClassByName(selectedClassName);
            if (selectedClass != null) {
                for (Student student : selectedClass.getStudents()) {
                    studentComboBox.addItem(student.getName() + " " + student.getSurname() + " [" + student.getPeselNumber() + "]");
                }
            }
        } else {
            for (Class cls : manager.getAllClasses()) {
                for (Student student : cls.getStudents()) {
                    studentComboBox.addItem(student.getName() + " " + student.getSurname() + " [" + student.getPeselNumber() + "] - " + cls.getName());
                }
            }
        }
    }

    private void refreshSubjectComboBox() {
        String selectedClassName = (String) classComboBox.getSelectedItem();
        subjectComboBox.removeAllItems();
        subjectComboBox.addItem("Wszystkie przedmioty");

        if (selectedClassName != null && !"Wszystkie klasy".equals(selectedClassName)) {
            Class selectedClass = manager.getClassByName(selectedClassName);
            if (selectedClass != null && !selectedClass.getStudents().isEmpty()) {
                // Pobranie pierwszego ucznia, aby uzyskać listę przedmiotów
                Student firstStudent = selectedClass.getStudents().get(0);
                for (Subject subject : firstStudent.getSubjects()) {
                    subjectComboBox.addItem(subject.getName());
                }
            }
        } else {
            // Zbieranie unikatowych nazw przedmiotów ze wszystkich klas
            List<String> uniqueSubjects = new ArrayList<>();
            for (Class cls : manager.getAllClasses()) {
                if (!cls.getStudents().isEmpty()) {
                    Student firstStudent = cls.getStudents().get(0);
                    for (Subject subject : firstStudent.getSubjects()) {
                        if (!uniqueSubjects.contains(subject.getName())) {
                            uniqueSubjects.add(subject.getName());
                            subjectComboBox.addItem(subject.getName());
                        }
                    }
                }
            }
        }
    }

    private void refreshGradesData() {
        tableModel.setRowCount(0);
        String selectedClassName = (String) classComboBox.getSelectedItem();
        String selectedStudentItem = (String) studentComboBox.getSelectedItem();
        String selectedSubjectName = (String) subjectComboBox.getSelectedItem();

        if (selectedClassName == null || selectedStudentItem == null) {
            return;
        }

        List<Class> classesToCheck = new ArrayList<>();
        if ("Wszystkie klasy".equals(selectedClassName)) {
            classesToCheck.addAll(manager.getAllClasses());
        } else {
            Class cls = manager.getClassByName(selectedClassName);
            if (cls != null) {
                classesToCheck.add(cls);
            }
        }

        for (Class cls : classesToCheck) {
            for (Student student : cls.getStudents()) {
                boolean studentMatches;

                if ("Wszyscy uczniowie".equals(selectedStudentItem)) {
                    studentMatches = true;
                } else {
                    String studentInfo;
                    if ("Wszystkie klasy".equals(selectedClassName)) {
                        studentInfo = student.getName() + " " + student.getSurname() + " [" + student.getPeselNumber() + "] - " + cls.getName();
                    } else {
                        studentInfo = student.getName() + " " + student.getSurname() + " [" + student.getPeselNumber() + "]";
                    }
                    studentMatches = selectedStudentItem.equals(studentInfo);
                }

                if (studentMatches) {
                    for (Subject subject : student.getSubjects()) {
                        boolean subjectMatches = "Wszystkie przedmioty".equals(selectedSubjectName) ||
                                subject.getName().equals(selectedSubjectName);

                        if (subjectMatches) {
                            for (Grade grade : subject.getGrades()) {
                                tableModel.addRow(new Object[]{
                                        student.getName() + " " + student.getSurname(),
                                        subject.getName(),
                                        grade.getGrade(),
                                        grade.getWeight(),
                                        grade.getDescription()
                                });
                            }
                        }
                    }
                }
            }
        }

        if (gradesTable.getSelectedRow() == -1) {
            gradeDetailsArea.setText("");
        }
    }

    private void displayGradeDetails(int selectedRow) {
        if (selectedRow >= 0 && selectedRow < tableModel.getRowCount()) {
            String studentName = (String) tableModel.getValueAt(selectedRow, 0);
            String subjectName = (String) tableModel.getValueAt(selectedRow, 1);
            int grade = (int) tableModel.getValueAt(selectedRow, 2);
            int weight = (int) tableModel.getValueAt(selectedRow, 3);
            String description = (String) tableModel.getValueAt(selectedRow, 4);

            StringBuilder details = new StringBuilder();
            details.append("Uczeń: ").append(studentName)
                    .append("\n\nPrzedmiot: ").append(subjectName)
                    .append("\n\nOcena: ").append(grade)
                    .append("\n\nWaga: ").append(weight)
                    .append("\n\nOpis: ").append(description);

            // Znajdź studenta i nauczyciela
            Student student = null;
            Teacher teacher = null;
            for (Class cls : manager.getAllClasses()) {
                for (Student s : cls.getStudents()) {
                    String fullName = s.getName() + " " + s.getSurname();
                    if (studentName.equals(fullName)) {
                        student = s;
                        for (Subject subject : student.getSubjects()) {
                            if (subject.getName().equals(subjectName)) {
                                for (Grade g : subject.getGrades()) {
                                    if (g.getGrade() == grade && g.getWeight() == weight && g.getDescription().equals(description)) {
                                        teacher = g.getAuthor();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (teacher != null) {
                details.append("\n\nNauczyciel: ").append(teacher.getName()).append(" ").append(teacher.getSurname());
            }

            gradeDetailsArea.setText(details.toString());
        }
    }

    private void showAddGradeDialog() {
        String selectedClassName = (String) classComboBox.getSelectedItem();
        if (selectedClassName == null || "Wszystkie klasy".equals(selectedClassName)) {
            JOptionPane.showMessageDialog(this,
                    "Wybierz konkretną klasę, aby dodać ocenę.",
                    "Wybór klasy", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));

        panel.add(new JLabel("Uczeń:"));
        DefaultComboBoxModel<String> studentModel = new DefaultComboBoxModel<>();
        Class selectedClass = manager.getClassByName(selectedClassName);
        if (selectedClass != null) {
            for (Student student : selectedClass.getStudents()) {
                studentModel.addElement(student.getName() + " " + student.getSurname() + " [" + student.getPeselNumber() + "]");
            }
        }
        JComboBox<String> studentCombo = new JComboBox<>(studentModel);
        panel.add(studentCombo);

        panel.add(new JLabel("Przedmiot:"));
        DefaultComboBoxModel<String> subjectModel = new DefaultComboBoxModel<>();
        if (selectedClass != null && !selectedClass.getStudents().isEmpty()) {
            Student firstStudent = selectedClass.getStudents().get(0);
            for (Subject subject : firstStudent.getSubjects()) {
                subjectModel.addElement(subject.getName());
            }
        }
        JComboBox<String> subjectCombo = new JComboBox<>(subjectModel);
        panel.add(subjectCombo);

        panel.add(new JLabel("Ocena (1-6):"));
        JSpinner gradeSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 6, 1));
        panel.add(gradeSpinner);

        panel.add(new JLabel("Waga (1-5):"));
        JSpinner weightSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        panel.add(weightSpinner);

        panel.add(new JLabel("Opis:"));
        JTextField descriptionField = new JTextField();
        panel.add(descriptionField);

        panel.add(new JLabel("Nauczyciel:"));
        DefaultComboBoxModel<String> teacherModel = new DefaultComboBoxModel<>();
        for (Teacher teacher : manager.getAllTeachers()) {
            teacherModel.addElement(teacher.getName() + " " + teacher.getSurname() + " [" + teacher.getPeselNumber() + "]");
        }
        JComboBox<String> teacherCombo = new JComboBox<>(teacherModel);
        panel.add(teacherCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Dodaj nową ocenę",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (studentCombo.getSelectedItem() == null || subjectCombo.getSelectedItem() == null ||
                    teacherCombo.getSelectedItem() == null || descriptionField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Wszystkie pola muszą być wypełnione.",
                        "Błąd danych", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String studentText = (String) studentCombo.getSelectedItem();
            String studentPesel = studentText.substring(studentText.indexOf("[") + 1, studentText.indexOf("]"));

            String subjectName = (String) subjectCombo.getSelectedItem();

            int grade = (int) gradeSpinner.getValue();
            int weight = (int) weightSpinner.getValue();

            String description = descriptionField.getText().trim();

            String teacherText = (String) teacherCombo.getSelectedItem();
            String teacherPesel = teacherText.substring(teacherText.indexOf("[") + 1, teacherText.indexOf("]"));

            manager.addGradeToStudent(studentPesel, selectedClassName, subjectName, grade, weight, description, teacherPesel);
            refreshGradesData();
        }
    }

    private void removeSelectedGrade() {
        int selectedRow = gradesTable.getSelectedRow();
        if (selectedRow != -1) {
            String studentName = (String) tableModel.getValueAt(selectedRow, 0);
            String subjectName = (String) tableModel.getValueAt(selectedRow, 1);
            int grade = (int) tableModel.getValueAt(selectedRow, 2);
            int weight = (int) tableModel.getValueAt(selectedRow, 3);
            String description = (String) tableModel.getValueAt(selectedRow, 4);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Czy na pewno chcesz usunąć ocenę " + grade + " dla ucznia " + studentName + " z przedmiotu " + subjectName + "?",
                    "Potwierdzenie usunięcia", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Znajdź studenta i usuń ocenę
                for (Class cls : manager.getAllClasses()) {
                    for (Student student : cls.getStudents()) {
                        String fullName = student.getName() + " " + student.getSurname();
                        if (studentName.equals(fullName)) {
                            for (Subject subject : student.getSubjects()) {
                                if (subject.getName().equals(subjectName)) {
                                    for (Grade g : new ArrayList<>(subject.getGrades())) {
                                        if (g.getGrade() == grade && g.getWeight() == weight && g.getDescription().equals(description)) {
                                            subject.removeGrade(g);
                                            refreshGradesData();
                                            gradeDetailsArea.setText("");
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Wybierz ocenę do usunięcia.",
                    "Brak wyboru", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editSelectedGrade() {
        int selectedRow = gradesTable.getSelectedRow();
        if (selectedRow != -1) {
            String studentName = (String) tableModel.getValueAt(selectedRow, 0);
            String subjectName = (String) tableModel.getValueAt(selectedRow, 1);
            int oldGrade = (int) tableModel.getValueAt(selectedRow, 2);
            int oldWeight = (int) tableModel.getValueAt(selectedRow, 3);
            String oldDescription = (String) tableModel.getValueAt(selectedRow, 4);

            // Znajdź odpowiedniego studenta, przedmiot i ocenę
            Student targetStudent = null;
            Subject targetSubject = null;
            Grade targetGrade = null;
            String className = "";

            for (Class cls : manager.getAllClasses()) {
                for (Student student : cls.getStudents()) {
                    String fullName = student.getName() + " " + student.getSurname();
                    if (studentName.equals(fullName)) {
                        targetStudent = student;
                        className = cls.getName();

                        for (Subject subject : student.getSubjects()) {
                            if (subject.getName().equals(subjectName)) {
                                targetSubject = subject;
                                for (Grade g : subject.getGrades()) {
                                    if (g.getGrade() == oldGrade && g.getWeight() == oldWeight && g.getDescription().equals(oldDescription)) {
                                        targetGrade = g;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (targetGrade == null) {
                JOptionPane.showMessageDialog(this, "Nie można znaleźć wybranej oceny.",
                        "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Dialog edycji oceny
            JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

            panel.add(new JLabel("Ocena (1-6):"));
            JSpinner gradeSpinner = new JSpinner(new SpinnerNumberModel(targetGrade.getGrade(), 1, 6, 1));
            panel.add(gradeSpinner);

            panel.add(new JLabel("Waga (1-5):"));
            JSpinner weightSpinner = new JSpinner(new SpinnerNumberModel(targetGrade.getWeight(), 1, 5, 1));
            panel.add(weightSpinner);

            panel.add(new JLabel("Opis:"));
            JTextField descriptionField = new JTextField(targetGrade.getDescription());
            panel.add(descriptionField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edytuj ocenę",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                int newGrade = (int) gradeSpinner.getValue();
                int newWeight = (int) weightSpinner.getValue();
                String newDescription = descriptionField.getText().trim();

                if (newDescription.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Opis nie może być pusty.",
                            "Błąd danych", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Aktualizacja oceny
                targetGrade.setGrade(newGrade);
                targetGrade.setWeight(newWeight);
                targetGrade.setDescription(newDescription);

                refreshGradesData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Wybierz ocenę do edycji.",
                    "Brak wyboru", JOptionPane.WARNING_MESSAGE);
        }
    }
}