package GuiApp;

import Core.*;
import Core.Class;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsPanel extends JPanel {
    private SchoolManager manager;
    private JTabbedPane tabbedPane;

    // Komponenty dla poszczególnych zakładek
    private JPanel generalStatsPanel;
    private JPanel classStatsPanel;
    private JPanel subjectStatsPanel;
    private JPanel studentRankingPanel;

    public StatisticsPanel(SchoolManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Nagłówek panelu
        JLabel titleLabel = new JLabel("Statystyki szkoły", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Inicjalizacja zakładek
        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        // Utworzenie i dodanie paneli do zakładek
        generalStatsPanel = createGeneralStatsPanel();
        classStatsPanel = createClassStatsPanel();
        subjectStatsPanel = createSubjectStatsPanel();
        studentRankingPanel = createStudentRankingPanel();

        tabbedPane.addTab("Ogólne statystyki", generalStatsPanel);
        tabbedPane.addTab("Statystyki klas", classStatsPanel);
        tabbedPane.addTab("Statystyki przedmiotów", subjectStatsPanel);
        tabbedPane.addTab("Ranking uczniów", studentRankingPanel);

        // Przycisk odświeżania
        JButton refreshButton = new JButton("Odśwież dane");
        refreshButton.addActionListener(e -> refreshAllData());
        add(refreshButton, BorderLayout.SOUTH);

        // Pierwsze ładowanie danych
        refreshAllData();
    }

    private JPanel createGeneralStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel z podsumowaniem liczbowym
        JPanel summaryPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Podsumowanie",
                TitledBorder.LEFT, TitledBorder.TOP));

        panel.add(summaryPanel, BorderLayout.NORTH);

        // Tabela z liczbą uczniów w klasach
        JPanel classDistributionPanel = new JPanel(new BorderLayout());
        classDistributionPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Rozkład uczniów w klasach",
                TitledBorder.LEFT, TitledBorder.TOP));

        String[] columnNames = {"Klasa", "Liczba uczniów", "Wychowawca"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable classTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(classTable);
        classDistributionPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(classDistributionPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createClassStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel wyboru klasy
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel("Wybierz klasę:"));
        JComboBox<String> classComboBox = new JComboBox<>();
        selectionPanel.add(classComboBox);

        panel.add(selectionPanel, BorderLayout.NORTH);

        // Panel z danymi statystycznymi klasy
        JPanel statsPanel = new JPanel(new BorderLayout());

        // Panel z podsumowaniem klasy
        JPanel classSummaryPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        classSummaryPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Informacje o klasie",
                TitledBorder.LEFT, TitledBorder.TOP));

        statsPanel.add(classSummaryPanel, BorderLayout.NORTH);

        // Tabela z przedmiotami i średnimi
        JPanel subjectStatsPanel = new JPanel(new BorderLayout());
        subjectStatsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Statystyki przedmiotów",
                TitledBorder.LEFT, TitledBorder.TOP));

        String[] columnNames = {"Przedmiot", "Średnia ocen", "Nauczyciel"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable subjectsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(subjectsTable);
        subjectStatsPanel.add(scrollPane, BorderLayout.CENTER);

        statsPanel.add(subjectStatsPanel, BorderLayout.CENTER);

        panel.add(statsPanel, BorderLayout.CENTER);

        // Dodanie akcji dla combobox
        classComboBox.addActionListener(e -> {
            String selectedClass = (String) classComboBox.getSelectedItem();
            if (selectedClass != null) {
                updateClassStatistics(selectedClass, classSummaryPanel, tableModel);
            }
        });

        return panel;
    }

    private JPanel createSubjectStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel wyboru przedmiotu i klasy
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel("Klasa:"));
        JComboBox<String> classComboBox = new JComboBox<>();
        selectionPanel.add(classComboBox);

        selectionPanel.add(new JLabel("Przedmiot:"));
        JComboBox<String> subjectComboBox = new JComboBox<>();
        selectionPanel.add(subjectComboBox);

        JButton showButton = new JButton("Pokaż statystyki");
        selectionPanel.add(showButton);

        panel.add(selectionPanel, BorderLayout.NORTH);

        // Panel z danymi statystycznymi przedmiotu
        JPanel statsPanel = new JPanel(new BorderLayout());

        // Panel z podsumowaniem przedmiotu
        JPanel subjectSummaryPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        subjectSummaryPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Informacje o przedmiocie",
                TitledBorder.LEFT, TitledBorder.TOP));

        statsPanel.add(subjectSummaryPanel, BorderLayout.NORTH);

        // Tabela z ocenami uczniów
        JPanel gradesPanel = new JPanel(new BorderLayout());
        gradesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Oceny uczniów",
                TitledBorder.LEFT, TitledBorder.TOP));

        String[] columnNames = {"Uczeń", "Średnia ocen", "Liczba ocen"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable gradesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        gradesPanel.add(scrollPane, BorderLayout.CENTER);

        statsPanel.add(gradesPanel, BorderLayout.CENTER);

        panel.add(statsPanel, BorderLayout.CENTER);

        // Dodanie akcji dla comboboxów
        classComboBox.addActionListener(e -> {
            String selectedClass = (String) classComboBox.getSelectedItem();
            if (selectedClass != null) {
                updateSubjectComboBox(selectedClass, subjectComboBox);
            }
        });

        showButton.addActionListener(e -> {
            String selectedClass = (String) classComboBox.getSelectedItem();
            String selectedSubject = (String) subjectComboBox.getSelectedItem();
            if (selectedClass != null && selectedSubject != null) {
                updateSubjectStatistics(selectedClass, selectedSubject, subjectSummaryPanel, tableModel);
            }
        });

        return panel;
    }

    private JPanel createStudentRankingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel wyboru filtru
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filtruj według:"));

        String[] filterOptions = {"Wszystkie klasy", "Konkretna klasa"};
        JComboBox<String> filterComboBox = new JComboBox<>(filterOptions);
        filterPanel.add(filterComboBox);

        JComboBox<String> classComboBox = new JComboBox<>();
        classComboBox.setVisible(false);
        filterPanel.add(classComboBox);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Tabela z rankingiem uczniów
        String[] columnNames = {"Pozycja", "Imię i nazwisko", "Klasa", "Średnia ocen"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable rankingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(rankingTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Dodanie akcji dla comboboxów
        filterComboBox.addActionListener(e -> {
            String selectedFilter = (String) filterComboBox.getSelectedItem();
            if ("Konkretna klasa".equals(selectedFilter)) {
                classComboBox.setVisible(true);
            } else {
                classComboBox.setVisible(false);
            }
            updateStudentRanking(tableModel,
                    "Konkretna klasa".equals(selectedFilter) ? (String) classComboBox.getSelectedItem() : null);
        });

        classComboBox.addActionListener(e -> {
            String selectedClass = (String) classComboBox.getSelectedItem();
            if (selectedClass != null && "Konkretna klasa".equals(filterComboBox.getSelectedItem())) {
                updateStudentRanking(tableModel, selectedClass);
            }
        });

        return panel;
    }

    private void refreshAllData() {
        updateGeneralStats();
        updateClassComboBoxes();

        // Uaktualnienie danych w aktywnej zakładce
        int selectedIndex = tabbedPane.getSelectedIndex();
        switch (selectedIndex) {
            case 0: // Ogólne statystyki
                updateGeneralStats();
                break;
            case 1: // Statystyki klas
                JPanel filterPanel = (JPanel) classStatsPanel.getComponent(0);
                JComboBox<String> classCombo = (JComboBox<String>) filterPanel.getComponent(1);
                if (classCombo.getSelectedItem() != null) {
                    JPanel summaryPanel = (JPanel) ((JPanel) classStatsPanel.getComponent(1)).getComponent(0);
                    DefaultTableModel model = (DefaultTableModel)
                            ((JTable) ((JScrollPane) ((JPanel) ((JPanel) classStatsPanel.getComponent(1))
                                    .getComponent(1)).getComponent(0)).getViewport().getView()).getModel();
                    updateClassStatistics((String) classCombo.getSelectedItem(), summaryPanel, model);
                }
                break;
            case 2: // Statystyki przedmiotów
                // Odświeżanie statystyk przedmiotu - tutaj podobnie jak wyżej, ale bardziej skomplikowany dostęp
                break;
            case 3: // Ranking uczniów
                JComboBox<String> filterCombo = (JComboBox<String>)
                        ((JPanel) studentRankingPanel.getComponent(0)).getComponent(1);
                JComboBox<String> classFilterCombo = (JComboBox<String>)
                        ((JPanel) studentRankingPanel.getComponent(0)).getComponent(2);

                DefaultTableModel rankingModel = (DefaultTableModel)
                        ((JTable) ((JScrollPane) studentRankingPanel.getComponent(1)).getViewport().getView()).getModel();

                String filter = (String) filterCombo.getSelectedItem();
                updateStudentRanking(rankingModel,
                        "Konkretna klasa".equals(filter) ? (String) classFilterCombo.getSelectedItem() : null);
                break;
        }
    }

    private void updateGeneralStats() {
        // Pobieranie wszystkich danych
        List<Class> classes = manager.getAllClasses();
        List<Teacher> teachers = manager.getAllTeachers();

        // Zliczanie wszystkich uczniów
        int totalStudents = 0;
        for (Class cls : classes) {
            totalStudents += cls.getStudents().size();
        }

        // Panel podsumowania
        JPanel summaryPanel = (JPanel) generalStatsPanel.getComponent(0);
        summaryPanel.removeAll();

        summaryPanel.add(new JLabel("Nazwa szkoły:"));
        summaryPanel.add(new JLabel(manager.getSchoolName()));

        summaryPanel.add(new JLabel("Liczba klas:"));
        summaryPanel.add(new JLabel(String.valueOf(classes.size())));

        summaryPanel.add(new JLabel("Liczba nauczycieli:"));
        summaryPanel.add(new JLabel(String.valueOf(teachers.size())));

        summaryPanel.add(new JLabel("Liczba uczniów:"));
        summaryPanel.add(new JLabel(String.valueOf(totalStudents)));

        // Średnia liczba uczniów na klasę
        double avgStudentsPerClass = classes.isEmpty() ? 0 : (double) totalStudents / classes.size();
        summaryPanel.add(new JLabel("Średnia liczba uczniów na klasę:"));
        summaryPanel.add(new JLabel(String.format("%.2f", avgStudentsPerClass)));

        // Aktualizacja tabeli klas
        DefaultTableModel tableModel = (DefaultTableModel)
                ((JTable) ((JScrollPane) ((JPanel) generalStatsPanel.getComponent(1)).getComponent(0)).getViewport().getView()).getModel();

        tableModel.setRowCount(0);
        for (Class cls : classes) {
            Teacher teacher = cls.getSupervisingTeacher();
            tableModel.addRow(new Object[] {
                    cls.getName(),
                    cls.getStudents().size(),
                    teacher.getName() + " " + teacher.getSurname()
            });
        }

        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    private void updateClassComboBoxes() {
        List<Class> classes = manager.getAllClasses();
        List<String> classNames = classes.stream()
                .map(Class::getName)
                .collect(Collectors.toList());

        // Aktualizacja combo dla zakładki statystyk klas
        JComboBox<String> classStatsCombo = (JComboBox<String>)
                ((JPanel) classStatsPanel.getComponent(0)).getComponent(1);

        String selectedClass = (String) classStatsCombo.getSelectedItem();
        classStatsCombo.removeAllItems();
        for (String className : classNames) {
            classStatsCombo.addItem(className);
        }
        // Przywracanie poprzedniego wyboru jeśli istnieje
        if (selectedClass != null && classNames.contains(selectedClass)) {
            classStatsCombo.setSelectedItem(selectedClass);
        } else if (!classNames.isEmpty()) {
            classStatsCombo.setSelectedIndex(0);
        }

        // Aktualizacja combo dla zakładki statystyk przedmiotów
        JComboBox<String> subjectStatsClassCombo = (JComboBox<String>)
                ((JPanel) subjectStatsPanel.getComponent(0)).getComponent(1);

        selectedClass = (String) subjectStatsClassCombo.getSelectedItem();
        subjectStatsClassCombo.removeAllItems();
        for (String className : classNames) {
            subjectStatsClassCombo.addItem(className);
        }
        // Przywracanie poprzedniego wyboru
        if (selectedClass != null && classNames.contains(selectedClass)) {
            subjectStatsClassCombo.setSelectedItem(selectedClass);
        } else if (!classNames.isEmpty()) {
            subjectStatsClassCombo.setSelectedIndex(0);
            // Aktualizacja listy przedmiotów dla pierwszej klasy
            updateSubjectComboBox(classNames.get(0),
                    (JComboBox<String>) ((JPanel) subjectStatsPanel.getComponent(0)).getComponent(3));
        }

        // Aktualizacja combo dla zakładki rankingu uczniów
        JComboBox<String> rankingClassCombo = (JComboBox<String>)
                ((JPanel) studentRankingPanel.getComponent(0)).getComponent(2);

        selectedClass = (String) rankingClassCombo.getSelectedItem();
        rankingClassCombo.removeAllItems();
        for (String className : classNames) {
            rankingClassCombo.addItem(className);
        }
        // Przywracanie poprzedniego wyboru
        if (selectedClass != null && classNames.contains(selectedClass)) {
            rankingClassCombo.setSelectedItem(selectedClass);
        } else if (!classNames.isEmpty()) {
            rankingClassCombo.setSelectedIndex(0);
        }
    }

    private void updateClassStatistics(String className, JPanel summaryPanel, DefaultTableModel tableModel) {
        Class selectedClass = manager.getClassByName(className);
        if (selectedClass == null) return;

        // Czyszczenie panelu podsumowania
        summaryPanel.removeAll();

        // Dodawanie informacji o klasie
        summaryPanel.add(new JLabel("Nazwa klasy:"));
        summaryPanel.add(new JLabel(className));

        Teacher teacher = selectedClass.getSupervisingTeacher();
        summaryPanel.add(new JLabel("Wychowawca:"));
        summaryPanel.add(new JLabel(teacher.getName() + " " + teacher.getSurname()));

        int studentCount = selectedClass.getStudents().size();
        summaryPanel.add(new JLabel("Liczba uczniów:"));
        summaryPanel.add(new JLabel(String.valueOf(studentCount)));

        // Zebranie przedmiotów unikalnych dla tej klasy
        Set<String> subjectNames = new HashSet<>();
        for (Student student : selectedClass.getStudents()) {
            for (Subject subject : student.getSubjects()) {
                subjectNames.add(subject.getName());
            }
        }

        summaryPanel.add(new JLabel("Liczba przedmiotów:"));
        summaryPanel.add(new JLabel(String.valueOf(subjectNames.size())));

        // Obliczenie średniej ocen dla klasy
        double classAvg = calculateClassAverage(selectedClass);
        summaryPanel.add(new JLabel("Średnia ocen klasy:"));
        summaryPanel.add(new JLabel(classAvg > 0 ? String.format("%.2f", classAvg) : "Brak ocen"));

        // Aktualizacja tabeli przedmiotów
        tableModel.setRowCount(0);

        for (String subjectName : subjectNames) {
            double subjectAvg = manager.getClassAverageGrade(className, subjectName);

            // Znalezienie nauczyciela prowadzącego przedmiot
            String teacherName = "Nieznany";
            for (Student student : selectedClass.getStudents()) {
                for (Subject subject : student.getSubjects()) {
                    if (subject.getName().equals(subjectName)) {
                        Teacher subjectTeacher = subject.getLeadingTeacher();
                        teacherName = subjectTeacher.getName() + " " + subjectTeacher.getSurname();
                        break;
                    }
                }
                if (!teacherName.equals("Nieznany")) break;
            }

            tableModel.addRow(new Object[] {
                    subjectName,
                    subjectAvg > 0 ? String.format("%.2f", subjectAvg) : "Brak ocen",
                    teacherName
            });
        }

        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    private void updateSubjectComboBox(String className, JComboBox<String> subjectComboBox) {
        Class selectedClass = manager.getClassByName(className);
        if (selectedClass == null) return;

        // Zebranie unikalnych nazw przedmiotów
        Set<String> subjectNames = new HashSet<>();
        for (Student student : selectedClass.getStudents()) {
            for (Subject subject : student.getSubjects()) {
                subjectNames.add(subject.getName());
            }
        }

        // Aktualizacja comboboxa
        String selectedSubject = (String) subjectComboBox.getSelectedItem();
        subjectComboBox.removeAllItems();

        for (String name : subjectNames) {
            subjectComboBox.addItem(name);
        }

        // Przywracanie poprzedniego wyboru
        if (selectedSubject != null && subjectNames.contains(selectedSubject)) {
            subjectComboBox.setSelectedItem(selectedSubject);
        } else if (!subjectNames.isEmpty()) {
            subjectComboBox.setSelectedIndex(0);
        }
    }

    private void updateSubjectStatistics(String className, String subjectName,
                                         JPanel summaryPanel, DefaultTableModel tableModel) {
        Class selectedClass = manager.getClassByName(className);
        if (selectedClass == null) return;

        // Czyszczenie panelu podsumowania
        summaryPanel.removeAll();

        // Dodawanie podstawowych informacji
        summaryPanel.add(new JLabel("Klasa:"));
        summaryPanel.add(new JLabel(className));

        summaryPanel.add(new JLabel("Przedmiot:"));
        summaryPanel.add(new JLabel(subjectName));

        // Znalezienie nauczyciela prowadzącego przedmiot
        String teacherName = "Nieznany";
        Teacher subjectTeacher = null;
        int totalGrades = 0;

        for (Student student : selectedClass.getStudents()) {
            for (Subject subject : student.getSubjects()) {
                if (subject.getName().equals(subjectName)) {
                    if (subjectTeacher == null) {
                        subjectTeacher = subject.getLeadingTeacher();
                        teacherName = subjectTeacher.getName() + " " + subjectTeacher.getSurname();
                    }
                    totalGrades += subject.getGrades().size();
                }
            }
        }

        summaryPanel.add(new JLabel("Prowadzący:"));
        summaryPanel.add(new JLabel(teacherName));

        double subjectAvg = manager.getClassAverageGrade(className, subjectName);
        summaryPanel.add(new JLabel("Średnia ocen:"));
        summaryPanel.add(new JLabel(subjectAvg > 0 ? String.format("%.2f", subjectAvg) : "Brak ocen"));

        summaryPanel.add(new JLabel("Łączna liczba ocen:"));
        summaryPanel.add(new JLabel(String.valueOf(totalGrades)));

        // Aktualizacja tabeli ocen uczniów
        tableModel.setRowCount(0);

        for (Student student : selectedClass.getStudents()) {
            // Szukanie przedmiotu dla tego ucznia
            for (Subject subject : student.getSubjects()) {
                if (subject.getName().equals(subjectName)) {
                    int gradesCount = subject.getGrades().size();
                    double average = subject.getAverageGrade();

                    tableModel.addRow(new Object[] {
                            student.getName() + " " + student.getSurname(),
                            gradesCount > 0 ? String.format("%.2f", average) : "Brak ocen",
                            gradesCount
                    });
                    break;
                }
            }
        }

        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    private void updateStudentRanking(DefaultTableModel tableModel, String className) {
        tableModel.setRowCount(0);

        // Tworzenie listy uczniów z ich średnimi
        List<Map.Entry<Student, Double>> studentRankings = new ArrayList<>();

        // Pobieranie wszystkich klas lub konkretnej klasy
        List<Class> classesToProcess;
        if (className == null) {
            classesToProcess = manager.getAllClasses();
        } else {
            Class selectedClass = manager.getClassByName(className);
            if (selectedClass == null) return;
            classesToProcess = Arrays.asList(selectedClass);
        }

        // Zbieranie danych o uczniach
        for (Class cls : classesToProcess) {
            for (Student student : cls.getStudents()) {
                double avg = calculateStudentAverage(student);
                if (avg > 0) { // Dodaj tylko uczniów z ocenami
                    studentRankings.add(new AbstractMap.SimpleEntry<>(student, avg));
                }
            }
        }

        // Sortowanie według średniej (malejąco)
        studentRankings.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        // Wypełnianie tabeli danymi
        int position = 1;
        for (Map.Entry<Student, Double> entry : studentRankings) {
            Student student = entry.getKey();
            double avg = entry.getValue();

            // Znalezienie klasy ucznia
            String studentClassName = "Nieznana";
            for (Class cls : classesToProcess) {
                if (cls.getStudents().contains(student)) {
                    studentClassName = cls.getName();
                    break;
                }
            }

            tableModel.addRow(new Object[] {
                    position++,
                    student.getName() + " " + student.getSurname(),
                    studentClassName,
                    String.format("%.2f", avg)
            });
        }
    }

    private double calculateClassAverage(Class cls) {
        double totalWeightedGrade = 0;
        int totalWeight = 0;

        for (Student student : cls.getStudents()) {
            for (Subject subject : student.getSubjects()) {
                for (Grade grade : subject.getGrades()) {
                    totalWeightedGrade += grade.getGradeWithWeight();
                    totalWeight += grade.getWeight();
                }
            }
        }

        return totalWeight > 0 ? totalWeightedGrade / totalWeight : 0;
    }

    private double calculateStudentAverage(Student student) {
        double totalWeightedGrade = 0;
        int totalWeight = 0;

        for (Subject subject : student.getSubjects()) {
            for (Grade grade : subject.getGrades()) {
                totalWeightedGrade += grade.getGradeWithWeight();
                totalWeight += grade.getWeight();
            }
        }

        return totalWeight > 0 ? totalWeightedGrade / totalWeight : 0;
    }
}