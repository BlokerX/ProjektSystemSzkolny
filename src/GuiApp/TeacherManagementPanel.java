package GuiApp;

import Core.*;
import Core.Class;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TeacherManagementPanel extends JPanel {
    private SchoolManager manager;
    private JTable teacherTable;
    private DefaultTableModel tableModel;
    private JTextArea teacherDetailsArea;

    public TeacherManagementPanel(SchoolManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel title
        JLabel titleLabel = new JLabel("Zarządzanie nauczycielami", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Split panel - left: teacher list, right: details/actions
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        add(splitPane, BorderLayout.CENTER);

        // Left side - teacher list
        JPanel teacherListPanel = createTeacherListPanel();
        splitPane.setLeftComponent(teacherListPanel);

        // Right side - details and actions panel
        JPanel detailsPanel = createDetailsPanel();
        splitPane.setRightComponent(detailsPanel);

        // Refresh data
        refreshTeacherData();
    }

    private JPanel createTeacherListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Create table model
        String[] columnNames = {"Imię i nazwisko", "PESEL"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable cells
            }
        };
        teacherTable = new JTable(tableModel);
        teacherTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teacherTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && teacherTable.getSelectedRow() != -1) {
                displayTeacherDetails((String) teacherTable.getValueAt(teacherTable.getSelectedRow(), 1));
            }
        });

        JScrollPane scrollPane = new JScrollPane(teacherTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel under the table
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshButton = new JButton("Odśwież");
        refreshButton.addActionListener(e -> refreshTeacherData());
        buttonPanel.add(refreshButton);

        JButton addButton = new JButton("Dodaj nauczyciela");
        addButton.addActionListener(e -> showAddTeacherDialog());
        buttonPanel.add(addButton);

        JButton removeButton = new JButton("Usuń nauczyciela");
        removeButton.addActionListener(e -> removeSelectedTeacher());
        buttonPanel.add(removeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel detailsLabel = new JLabel("Szczegóły nauczyciela");
        detailsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(detailsLabel, BorderLayout.NORTH);

        teacherDetailsArea = new JTextArea();
        teacherDetailsArea.setEditable(false);
        teacherDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        teacherDetailsArea.setLineWrap(true);
        teacherDetailsArea.setWrapStyleWord(true);
        JScrollPane detailsScrollPane = new JScrollPane(teacherDetailsArea);
        panel.add(detailsScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshTeacherData() {
        // Clear the table
        tableModel.setRowCount(0);

        // Get all teachers and add them to the table
        List<Teacher> teachers = manager.getAllTeachers();
        for (Teacher teacher : teachers) {
            tableModel.addRow(new Object[]{
                    teacher.getName() + " " + teacher.getSurname(),
                    teacher.getPeselNumber()
            });
        }

        // Clear details if no teacher is selected
        if (teacherTable.getSelectedRow() == -1) {
            teacherDetailsArea.setText("");
        }
    }

    private void displayTeacherDetails(String peselNumber) {
        Teacher teacher = manager.getTeacherByPesel(peselNumber);
        if (teacher != null) {
            StringBuilder details = new StringBuilder();
            details.append("Imię i nazwisko: ").append(teacher.getName()).append(" ").append(teacher.getSurname())
                    .append("\n\nPESEL: ").append(teacher.getPeselNumber())
                    .append("\n\nWiek: ").append(teacher.getAge());

            // Find classes where this teacher is supervising
            boolean isSupervisingTeacher = false;
            details.append("\n\nKlasy wychowawcze:");
            for (Class cls : manager.getAllClasses()) {
                if (cls.getSupervisingTeacher().getPeselNumber().equals(peselNumber)) {
                    details.append("\n- ").append(cls.getName());
                    isSupervisingTeacher = true;
                }
            }
            if (!isSupervisingTeacher) {
                details.append(" brak");
            }

            teacherDetailsArea.setText(details.toString());
        } else {
            teacherDetailsArea.setText("Nie znaleziono szczegółów nauczyciela.");
        }
    }

    private void showAddTeacherDialog() {
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

        panel.add(new JLabel("Wynagrodzenie:"));
        JTextField salaryField = new JTextField();
        panel.add(salaryField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Dodaj nowego nauczyciela",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String ageText = ageField.getText().trim();
            String pesel = peselField.getText().trim();
            String salaryText = salaryField.getText().trim();

            try {
                int age = Integer.parseInt(ageText);
                double salary = Double.parseDouble(salaryText);

                if (!name.isEmpty() && !surname.isEmpty() && !pesel.isEmpty()) {
                    manager.hireTeacher(name, surname, age, pesel, salary);
                    refreshTeacherData();
                    JOptionPane.showMessageDialog(this,
                            "Nauczyciel " + name + " " + surname + " został dodany pomyślnie.");
                } else {
                    JOptionPane.showMessageDialog(this, "Wszystkie pola muszą być wypełnione.",
                            "Błąd danych", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Wiek i wynagrodzenie muszą być liczbami.",
                        "Błąd danych", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeSelectedTeacher() {
        int selectedRow = teacherTable.getSelectedRow();
        if (selectedRow != -1) {
            String peselNumber = (String) teacherTable.getValueAt(selectedRow, 1);
            String teacherName = (String) teacherTable.getValueAt(selectedRow, 0);

            // Check if teacher is a supervising teacher for any class
            boolean isSupervisingTeacher = false;
            for (Class cls : manager.getAllClasses()) {
                if (cls.getSupervisingTeacher().getPeselNumber().equals(peselNumber)) {
                    isSupervisingTeacher = true;
                    break;
                }
            }

            if (isSupervisingTeacher) {
                JOptionPane.showMessageDialog(this,
                        "Nie można usunąć nauczyciela, który jest wychowawcą klasy.\n" +
                                "Najpierw przypisz innego wychowawcę do jego klasy.",
                        "Operacja niemożliwa", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Czy na pewno chcesz usunąć nauczyciela " + teacherName + "?",
                    "Potwierdzenie usunięcia", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                manager.fireTeacher(peselNumber);
                refreshTeacherData();
                teacherDetailsArea.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Wybierz nauczyciela do usunięcia.",
                    "Brak wyboru", JOptionPane.WARNING_MESSAGE);
        }
    }
}
