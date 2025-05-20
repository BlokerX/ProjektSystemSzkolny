package GuiApp;

import Core.*;
import Core.Class;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentManagementPanel extends JPanel {
    private SchoolManager manager;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextArea studentDetailsArea;
    private JComboBox<String> classFilterComboBox;

    public StudentManagementPanel(SchoolManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel title
        JLabel titleLabel = new JLabel("Zarządzanie uczniami", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Split panel - left: student list, right: details/actions
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        add(splitPane, BorderLayout.CENTER);

        // Left side - student list
        JPanel studentListPanel = createStudentListPanel();
        splitPane.setLeftComponent(studentListPanel);

        // Right side - details and actions panel
        JPanel detailsPanel = createDetailsPanel();
        splitPane.setRightComponent(detailsPanel);

        // Refresh data
        refreshStudentData();
    }

    private JPanel createStudentListPanel() {
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
        classFilterComboBox.addActionListener(e -> refreshStudentData());
        filterPanel.add(classFilterComboBox);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Create table model
        String[] columnNames = {"Imię i nazwisko", "PESEL", "Klasa"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable cells
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && studentTable.getSelectedRow() != -1) {
                String pesel = (String) studentTable.getValueAt(studentTable.getSelectedRow(), 1);
                String className = (String) studentTable.getValueAt(studentTable.getSelectedRow(), 2);
                displayStudentDetails(pesel, className);
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel under the table
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshButton = new JButton("Odśwież");
        refreshButton.addActionListener(e -> refreshStudentData());
        buttonPanel.add(refreshButton);

        JButton addButton = new JButton("Dodaj ucznia");
        addButton.addActionListener(e -> showAddStudentDialog());
        buttonPanel.add(addButton);

        JButton removeButton = new JButton("Usuń ucznia");
        removeButton.addActionListener(e -> removeSelectedStudent());
        buttonPanel.add(removeButton);

        JButton transferButton = new JButton("Przenieś ucznia");
        transferButton.addActionListener(e -> showTransferStudentDialog());
        buttonPanel.add(transferButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel detailsLabel = new JLabel("Szczegóły ucznia");
        detailsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(detailsLabel, BorderLayout.NORTH);

        studentDetailsArea = new JTextArea();
        studentDetailsArea.setEditable(false);
        studentDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        studentDetailsArea.setLineWrap(true);
        studentDetailsArea.setWrapStyleWord(true);
        JScrollPane detailsScrollPane = new JScrollPane(studentDetailsArea);
        panel.add(detailsScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshStudentData() {
        // Clear the table
        tableModel.setRowCount(0);

        String selectedClassName = (String) classFilterComboBox.getSelectedItem();

        // Get students based on filter
        for (Class cls : manager.getAllClasses()) {
            if ("Wszystkie klasy".equals(selectedClassName) || cls.getName().equals(selectedClassName)) {
                for (Student student : cls.getStudents()) {
                    tableModel.addRow(new Object[]{
                            student.getName() + " " + student.getSurname(),
                            student.getPeselNumber(),
                            cls.getName()
                    });
                }
            }
        }

        // Clear details if no student is selected
        if (studentTable.getSelectedRow() == -1) {
            studentDetailsArea.setText("");
        }
    }

    private void displayStudentDetails(String peselNumber, String className) {
        Class targetClass = manager.getClassByName(className);
        if (targetClass != null) {
            Student student = targetClass.getStudentByPeselNumber(peselNumber);
            if (student != null) {
                StringBuilder details = new StringBuilder();
                details.append("Imię i nazwisko: ").append(student.getName()).append(" ").append(student.getSurname())
                        .append("\n\nPESEL: ").append(student.getPeselNumber())
                        .append("\n\nWiek: ").append(student.getAge())
                        .append("\n\nKlasa: ").append(className);

                details.append("\n\nPrzedmioty i oceny:");

                if (student.getSubjects().isEmpty()) {
                    details.append("\nBrak przypisanych przedmiotów");
                } else {
                    for (Subject subject : student.getSubjects()) {
                        details.append("\n\n- ").append(subject.getName())
                                .append(" (nauczyciel: ").append(subject.getLeadingTeacher().getName())
                                .append(" ").append(subject.getLeadingTeacher().getSurname()).append(")");

                        if (subject.getGrades().isEmpty()) {
                            details.append("\n  Brak ocen");
                        } else {
                            for (Grade grade : subject.getGrades()) {
                                details.append("\n  Ocena: ").append(grade.getGrade())
                                        .append(" (waga: ").append(grade.getWeight()).append(")")
                                        .append(" - ").append(grade.getDescription());
                            }
                            details.append("\n  Średnia: ").append(String.format("%.2f", subject.getAverageGrade()));
                        }
                    }
                }

                // Obliczenie średniej ogólnej
                double averageGrade = manager.getStudentAverageGrade(peselNumber, className);
                if (averageGrade >= 0) {
                    details.append("\n\nŚrednia ogólna: ").append(String.format("%.2f", averageGrade));
                }

                studentDetailsArea.setText(details.toString());
            } else {
                studentDetailsArea.setText("Nie znaleziono ucznia.");
            }
        } else {
            studentDetailsArea.setText("Nie znaleziono klasy.");
        }
    }

    private void showAddStudentDialog() {
        // Check if there are available classes
        List<Class> classes = manager.getAllClasses();
        if (classes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Brak dostępnych klas. Dodaj klasę przed dodaniem ucznia.",
                    "Brak klas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));

        panel.add(new JLabel("Imię:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Nazwisko:"));
        JTextField surnameField = new JTextField();
        panel.add(surnameField);

        panel.add(new JLabel("Wiek:"));
        JTextField ageField = new JTextField();
        panel.add(ageField);

        panel.add(new JLabel("PESEL:"));
        JTextField peselField = new JTextField();
        panel.add(peselField);

        panel.add(new JLabel("Klasa:"));
        DefaultComboBoxModel<String> classModel = new DefaultComboBoxModel<>();
        for (Class cls : classes) {
            classModel.addElement(cls.getName());
        }
        JComboBox<String> classComboBox = new JComboBox<>(classModel);
        panel.add(classComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Dodaj nowego ucznia",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String ageText = ageField.getText().trim();
            String pesel = peselField.getText().trim();
            String className = (String) classComboBox.getSelectedItem();

            try {
                int age = Integer.parseInt(ageText);

                if (!name.isEmpty() && !surname.isEmpty() && !pesel.isEmpty() && className != null) {
                    manager.enrollStudent(name, surname, age, pesel, className);
                    refreshStudentData();
                    updateClassFilterComboBox();
                    JOptionPane.showMessageDialog(this,
                            "Uczeń " + name + " " + surname + " został dodany do klasy " + className + ".");
                } else {
                    JOptionPane.showMessageDialog(this, "Wszystkie pola muszą być wypełnione.",
                            "Błąd danych", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Wiek musi być liczbą.",
                        "Błąd danych", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow != -1) {
            String peselNumber = (String) studentTable.getValueAt(selectedRow, 1);
            String studentName = (String) studentTable.getValueAt(selectedRow, 0);
            String className = (String) studentTable.getValueAt(selectedRow, 2);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Czy na pewno chcesz usunąć ucznia " + studentName + " z klasy " + className + "?",
                    "Potwierdzenie usunięcia", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                manager.removeStudent(peselNumber, className);
                refreshStudentData();
                studentDetailsArea.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Wybierz ucznia do usunięcia.",
                    "Brak wyboru", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showTransferStudentDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow != -1) {
            String peselNumber = (String) studentTable.getValueAt(selectedRow, 1);
            String studentName = (String) studentTable.getValueAt(selectedRow, 0);
            String currentClassName = (String) studentTable.getValueAt(selectedRow, 2);

            // Get available classes to transfer to
            List<Class> classes = manager.getAllClasses();
            if (classes.size() <= 1) {
                JOptionPane.showMessageDialog(this,
                        "Brak innych klas do przeniesienia ucznia.",
                        "Brak klas", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JLabel("Wybierz klasę docelową dla ucznia " + studentName + ":"), BorderLayout.NORTH);

            DefaultComboBoxModel<String> classModel = new DefaultComboBoxModel<>();
            for (Class cls : classes) {
                if (!cls.getName().equals(currentClassName)) {
                    classModel.addElement(cls.getName());
                }
            }
            JComboBox<String> classComboBox = new JComboBox<>(classModel);
            panel.add(classComboBox, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(this, panel, "Przenieś ucznia",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION && classComboBox.getSelectedItem() != null) {
                String targetClassName = (String) classComboBox.getSelectedItem();
                manager.transferStudent(peselNumber, currentClassName, targetClassName);
                refreshStudentData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Wybierz ucznia do przeniesienia.",
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