package GuiApp;

import Core.*;
import Core.Class;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClassManagementPanel extends JPanel {
    private SchoolManager manager;
    private JTable classTable;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;
    private JTextArea classDetailsArea;

    public ClassManagementPanel(SchoolManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tytuł panelu
        JLabel titleLabel = new JLabel("Zarządzanie klasami", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Panel podziału - lewa strona: lista klas, prawa strona: szczegóły/akcje
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        add(splitPane, BorderLayout.CENTER);

        // Lewa strona - lista klas
        JPanel classListPanel = createClassListPanel();
        splitPane.setLeftComponent(classListPanel);

        // Prawa strona - panel szczegółów i akcji
        detailsPanel = createDetailsPanel();
        splitPane.setRightComponent(detailsPanel);

        // Odświeżenie danych
        refreshClassData();
    }

    private JPanel createClassListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Utworzenie modelu tabeli
        String[] columnNames = {"Nazwa klasy", "Wychowawca"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Komórki niemodyfikowalne
            }
        };
        classTable = new JTable(tableModel);
        classTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && classTable.getSelectedRow() != -1) {
                displayClassDetails((String) classTable.getValueAt(classTable.getSelectedRow(), 0));
            }
        });

        JScrollPane scrollPane = new JScrollPane(classTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel przycisków pod tabelą
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Odśwież");
        refreshButton.addActionListener(e -> refreshClassData());
        buttonPanel.add(refreshButton);

        JButton addButton = new JButton("Dodaj klasę");
        addButton.addActionListener(e -> showAddClassDialog());
        buttonPanel.add(addButton);

        JButton removeButton = new JButton("Usuń klasę");
        removeButton.addActionListener(e -> removeSelectedClass());
        buttonPanel.add(removeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel detailsLabel = new JLabel("Szczegóły klasy");
        detailsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(detailsLabel, BorderLayout.NORTH);

        classDetailsArea = new JTextArea();
        classDetailsArea.setEditable(false);
        classDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        classDetailsArea.setLineWrap(true);
        classDetailsArea.setWrapStyleWord(true);
        JScrollPane detailsScrollPane = new JScrollPane(classDetailsArea);
        panel.add(detailsScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshClassData() {
        // Wyczyszczenie tabeli
        tableModel.setRowCount(0);

        // Pobranie wszystkich klas i dodanie ich do tabeli
        List<Class> classes = manager.getAllClasses();
        for (Class cls : classes) {
            Teacher teacher = cls.getSupervisingTeacher();
            String teacherName = teacher.getName() + " " + teacher.getSurname();
            tableModel.addRow(new Object[]{cls.getName(), teacherName});
        }

        // Wyczyszczenie szczegółów, jeśli żadna klasa nie jest wybrana
        if (classTable.getSelectedRow() == -1) {
            classDetailsArea.setText("");
        }
    }

    private void displayClassDetails(String className) {
        Class selectedClass = manager.getClassByName(className);
        if (selectedClass != null) {
            StringBuilder details = new StringBuilder();
            details.append("Nazwa klasy: ").append(className).append("\n\n");

            Teacher teacher = selectedClass.getSupervisingTeacher();
            details.append("Wychowawca: ").append(teacher.getName()).append(" ").append(teacher.getSurname())
                    .append(" (PESEL: ").append(teacher.getPeselNumber()).append(")\n\n");

            details.append("Lista uczniów (").append(selectedClass.getStudents().size()).append("):\n");
            for (Student student : selectedClass.getStudents()) {
                details.append("- ").append(student.getName()).append(" ").append(student.getSurname())
                        .append(" (PESEL: ").append(student.getPeselNumber()).append(")\n");
            }

            classDetailsArea.setText(details.toString());
        } else {
            classDetailsArea.setText("Nie znaleziono szczegółów klasy.");
        }
    }

    private void showAddClassDialog() {
        // Sprawdzenie czy są dostępni nauczyciele
        List<Teacher> teachers = manager.getAllTeachers();
        if (teachers.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Brak dostępnych nauczycieli. Dodaj nauczyciela przed utworzeniem klasy.",
                    "Brak nauczycieli", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Panel do wprowadzania danych
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Nazwa klasy:"));
        JTextField classNameField = new JTextField();
        panel.add(classNameField);

        panel.add(new JLabel("Wychowawca:"));
        DefaultComboBoxModel<String> teacherModel = new DefaultComboBoxModel<>();
        for (Teacher teacher : teachers) {
            teacherModel.addElement(teacher.getName() + " " + teacher.getSurname() +
                    " (PESEL: " + teacher.getPeselNumber() + ")");
        }
        JComboBox<String> teacherComboBox = new JComboBox<>(teacherModel);
        panel.add(teacherComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Dodaj nową klasę",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String className = classNameField.getText().trim();
            int selectedTeacherIndex = teacherComboBox.getSelectedIndex();

            if (!className.isEmpty() && selectedTeacherIndex != -1) {
                manager.createClass(className, teachers.get(selectedTeacherIndex));
                refreshClassData();
                JOptionPane.showMessageDialog(this, "Klasa " + className + " została utworzona pomyślnie.");
            } else {
                JOptionPane.showMessageDialog(this, "Wprowadź poprawne dane klasy.",
                        "Błąd danych", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeSelectedClass() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow != -1) {
            String className = (String) classTable.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Czy na pewno chcesz usunąć klasę " + className + "?",
                    "Potwierdzenie usunięcia", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                manager.removeClassByName(className);
                refreshClassData();
                classDetailsArea.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Wybierz klasę do usunięcia.",
                    "Brak wyboru", JOptionPane.WARNING_MESSAGE);
        }
    }
}
