import Core.Class;
import Core.School;
import Core.SchoolManager;
import Core.Teacher;
import GuiApp.MainFrame;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("============================");
        System.out.println("Wybierz tryb uruchomienia: ");
        System.out.println("1. Tryb okienkowy");
        System.out.println("2. Tryb konsolowy");
        System.out.println("============================");
        System.out.print("Numer trybu: ");
        Scanner scanner = new Scanner(System.in);
        String x = scanner.nextLine();
        switch (x)
        {
            case "1":
                System.out.println("1.Tryb okienkowy");
                //region Swing app
                SwingUtilities.invokeLater(() -> {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                });

                //endregion
                break;

            case "2":
                System.out.println("2.Tryb konsolowy");//region Console

                // Inicjalizacja podstawowych danych szkoły
                School school;
                SchoolManager manager;

                // Próba wczytania danych przy starcie
                try {
                    manager = SchoolManager.loadFromFile("school_data.ser");

                    school = manager.getSchool();

                    System.out.println("Dane wczytane pomyślnie!");

                } catch (IOException | ClassNotFoundException e) {

                    // Jeśli plik nie istnieje, utwórz nową szkołę z danymi testowymi

                    school = School.createSchool("Liceum im. Marii Skłodowskiej-Curie");

                    manager = new SchoolManager(school);

                    initializeSchoolData(manager);

                    System.out.println("Utworzono nową szkołę z danymi testowymi");
                }



                // Wstępne załadowanie przykładowych danych
                initializeSchoolData(manager);

                boolean running = true;
                while (running) {
                    displayMenu();
                    System.out.print("Wybierz opcję: ");

                    try {
                        int choice = Integer.parseInt(scanner.nextLine());

                        switch (choice) {
                            case 1:
                                displaySchoolInfo(manager);
                                break;
                            case 2:
                                manageClasses(scanner, manager);
                                break;
                            case 3:
                                manageTeachers(scanner, manager);
                                break;
                            case 4:
                                manageStudents(scanner, manager);
                                break;
                            case 5:
                                manageSubjects(scanner, manager);
                                break;
                            case 6:
                                manageGrades(scanner, manager);
                                break;
                            case 7:
                                displayStatistics(scanner, manager);
                                break;
                            case 8:
                                saveData(manager);
                                break;
                            case 0:
                                running = false;
                                System.out.println("Zamykanie systemu...");
                                break;
                            default:
                                System.out.println("Nieprawidłowa opcja. Spróbuj ponownie.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Błąd: Wprowadź liczbę.");
                    }

                    // Pauza przed powrotem do menu
                    if (running) {
                        System.out.println("\nNaciśnij ENTER, aby kontynuować...");
                        scanner.nextLine();
                    }
                }

                scanner.close();
                //endregion
                break;
        }
    }

    private static void saveData(SchoolManager manager) {
        try {
            manager.saveToFile("school_data.ser");
            System.out.println("Dane zapisane pomyślnie!");
        } catch (IOException e) {
            System.out.println("Błąd zapisu danych: " + e.getMessage());
        }
    }

    private static void showStatisticsPanel(MainFrame mainFrame) {
        // Zakładając, że statisticsPanel jest już zainicjalizowany w konstruktorze
        if (mainFrame.statisticsPanel != null) {
            JFrame statisticsFrame = new JFrame("Statystyki szkolne");
            statisticsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            statisticsFrame.setSize(800, 600);

            // Dodaj panel statystyk do nowego okna
            statisticsFrame.add(mainFrame.statisticsPanel);

            // Wyśrodkuj okno na ekranie
            statisticsFrame.setLocationRelativeTo(null);

            // Wyświetl okno
            statisticsFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Panel statystyk nie został zainicjalizowany.", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }


    private static void displayMenu() {
        System.out.println("\n========== SYSTEM ZARZĄDZANIA SZKOŁĄ ==========");
        System.out.println("1. Wyświetl informacje o szkole");
        System.out.println("2. Zarządzaj klasami");
        System.out.println("3. Zarządzaj nauczycielami");
        System.out.println("4. Zarządzaj uczniami");
        System.out.println("5. Zarządzaj przedmiotami");
        System.out.println("6. Zarządzaj ocenami");
        System.out.println("7. Wyświetl statystyki");
        System.out.println("8. Zapisz zmiany");
        System.out.println("0. Wyjście");
        System.out.println("==============================================");
    }

    // Podmenu do zarządzania klasami
    private static void manageClasses(Scanner scanner, SchoolManager manager) {
        boolean running = true;
        while (running) {
            System.out.println("\n----- ZARZĄDZANIE KLASAMI -----");
            System.out.println("1. Wyświetl wszystkie klasy");
            System.out.println("2. Wyświetl szczegóły klasy");
            System.out.println("3. Dodaj nową klasę");
            System.out.println("4. Usuń klasę");
            System.out.println("0. Powrót do menu głównego");

            System.out.print("Wybierz opcję: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        manager.displayAllClasses();
                        break;
                    case 2:
                        System.out.print("Podaj nazwę klasy: ");
                        String className = scanner.nextLine();
                        manager.displayClassDetails(className);
                        break;
                    case 3:
                        addClass(scanner, manager);
                        break;
                    case 4:
                        System.out.print("Podaj nazwę klasy do usunięcia: ");
                        String classToRemove = scanner.nextLine();
                        manager.removeClassByName(classToRemove);
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Nieprawidłowa opcja. Spróbuj ponownie.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Błąd: Wprowadź liczbę.");
            }

            if (running) {
                System.out.println("\nNaciśnij ENTER, aby kontynuować...");
                scanner.nextLine();
            }
        }
    }

    // Pomocnicza metoda do dodawania nowej klasy
    private static void addClass(Scanner scanner, SchoolManager manager) {
        System.out.print("Podaj nazwę nowej klasy: ");
        String name = scanner.nextLine();

        // Wyświetl dostępnych nauczycieli
        List<Teacher> teachers = manager.getAllTeachers();
        if (teachers.isEmpty()) {
            System.out.println("Brak dostępnych nauczycieli. Najpierw dodaj nauczyciela.");
            return;
        }

        System.out.println("Dostępni nauczyciele:");
        for (int i = 0; i < teachers.size(); i++) {
            Teacher teacher = teachers.get(i);
            System.out.println((i + 1) + ". " + teacher.getName() + " " + teacher.getSurname() +
                    " (PESEL: " + teacher.getPeselNumber() + ")");
        }

        System.out.print("Wybierz numer nauczyciela jako wychowawcę: ");
        try {
            int teacherIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (teacherIndex >= 0 && teacherIndex < teachers.size()) {
                manager.createClass(name, teachers.get(teacherIndex));
                System.out.println("Klasa " + name + " została utworzona pomyślnie.");
            } else {
                System.out.println("Nieprawidłowy numer nauczyciela.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Błąd: Wprowadź liczbę.");
        }
    }

    // Podmenu do zarządzania nauczycielami
    private static void manageTeachers(Scanner scanner, SchoolManager manager) {
        boolean running = true;
        while (running) {
            System.out.println("\n----- ZARZĄDZANIE NAUCZYCIELAMI -----");
            System.out.println("1. Wyświetl wszystkich nauczycieli");
            System.out.println("2. Dodaj nauczyciela");
            System.out.println("3. Usuń nauczyciela");
            System.out.println("0. Powrót do menu głównego");

            System.out.print("Wybierz opcję: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        displayAllTeachers(manager);
                        break;
                    case 2:
                        addTeacher(scanner, manager);
                        break;
                    case 3:
                        System.out.print("Podaj PESEL nauczyciela do usunięcia: ");
                        String peselToRemove = scanner.nextLine();
                        manager.fireTeacher(peselToRemove);
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Nieprawidłowa opcja. Spróbuj ponownie.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Błąd: Wprowadź liczbę.");
            }

            if (running) {
                System.out.println("\nNaciśnij ENTER, aby kontynuować...");
                scanner.nextLine();
            }
        }
    }

    // Wyświetla wszystkich nauczycieli
    private static void displayAllTeachers(SchoolManager manager) {
        List<Teacher> teachers = manager.getAllTeachers();
        if (teachers.isEmpty()) {
            System.out.println("Brak nauczycieli w szkole.");
            return;
        }

        System.out.println("Lista nauczycieli:");
        for (Teacher teacher : teachers) {
            System.out.println("- " + teacher.getName() + " " + teacher.getSurname() +
                    " (PESEL: " + teacher.getPeselNumber() + ")");
        }
    }

    // Dodaje nowego nauczyciela
    private static void addTeacher(Scanner scanner, SchoolManager manager) {
        System.out.print("Podaj imię nauczyciela: ");
        String name = scanner.nextLine();

        System.out.print("Podaj nazwisko nauczyciela: ");
        String surname = scanner.nextLine();

        int age = 0;
        boolean validAge = false;
        while (!validAge) {
            System.out.print("Podaj wiek nauczyciela: ");
            try {
                age = Integer.parseInt(scanner.nextLine());
                validAge = true;
            } catch (NumberFormatException e) {
                System.out.println("Błąd: Wiek musi być liczbą.");
            }
        }

        System.out.print("Podaj PESEL nauczyciela: ");
        String pesel = scanner.nextLine();

        double salary = 0;
        boolean validSalary = false;
        while (!validSalary) {
            System.out.print("Podaj wynagrodzenie nauczyciela: ");
            try {
                salary = Double.parseDouble(scanner.nextLine());
                validSalary = true;
            } catch (NumberFormatException e) {
                System.out.println("Błąd: Wynagrodzenie musi być liczbą.");
            }
        }

        manager.hireTeacher(name, surname, age, pesel, salary);
        System.out.println("Nauczyciel " + name + " " + surname + " został dodany pomyślnie.");
    }

    // Podmenu do zarządzania uczniami
    private static void manageStudents(Scanner scanner, SchoolManager manager) {
        boolean running = true;
        while (running) {
            System.out.println("\n----- ZARZĄDZANIE UCZNIAMI -----");
            System.out.println("1. Wyświetl szczegóły ucznia");
            System.out.println("2. Dodaj ucznia do klasy");
            System.out.println("3. Usuń ucznia z klasy");
            System.out.println("4. Przenieś ucznia do innej klasy");
            System.out.println("0. Powrót do menu głównego");

            System.out.print("Wybierz opcję: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        System.out.print("Podaj PESEL ucznia: ");
                        String studentPesel = scanner.nextLine();
                        System.out.print("Podaj klasę ucznia: ");
                        String studentClass = scanner.nextLine();
                        manager.displayStudentDetails(studentPesel, studentClass);
                        break;
                    case 2:
                        addStudent(scanner, manager);
                        break;
                    case 3:
                        System.out.print("Podaj PESEL ucznia do usunięcia: ");
                        String peselToRemove = scanner.nextLine();
                        System.out.print("Podaj klasę ucznia: ");
                        String classToRemoveFrom = scanner.nextLine();
                        manager.removeStudent(peselToRemove, classToRemoveFrom);
                        break;
                    case 4:
                        transferStudent(scanner, manager);
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Nieprawidłowa opcja. Spróbuj ponownie.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Błąd: Wprowadź liczbę.");
            }

            if (running) {
                System.out.println("\nNaciśnij ENTER, aby kontynuować...");
                scanner.nextLine();
            }
        }
    }

    // Dodaje nowego ucznia
    private static void addStudent(Scanner scanner, SchoolManager manager) {
        System.out.print("Podaj imię ucznia: ");
        String name = scanner.nextLine();

        System.out.print("Podaj nazwisko ucznia: ");
        String surname = scanner.nextLine();

        int age = 0;
        boolean validAge = false;
        while (!validAge) {
            System.out.print("Podaj wiek ucznia: ");
            try {
                age = Integer.parseInt(scanner.nextLine());
                validAge = true;
            } catch (NumberFormatException e) {
                System.out.println("Błąd: Wiek musi być liczbą.");
            }
        }

        System.out.print("Podaj PESEL ucznia: ");
        String pesel = scanner.nextLine();

        System.out.print("Podaj klasę, do której zostanie przypisany uczeń: ");
        String className = scanner.nextLine();

        manager.enrollStudent(name, surname, age, pesel, className);
        System.out.println("Uczeń " + name + " " + surname + " został dodany do klasy " + className + ".");
    }

    // Przenosi ucznia z jednej klasy do drugiej
    private static void transferStudent(Scanner scanner, SchoolManager manager) {
        System.out.print("Podaj PESEL ucznia do przeniesienia: ");
        String pesel = scanner.nextLine();

        System.out.print("Podaj nazwę obecnej klasy ucznia: ");
        String currentClass = scanner.nextLine();

        System.out.print("Podaj nazwę klasy docelowej: ");
        String targetClass = scanner.nextLine();

        manager.transferStudent(pesel, currentClass, targetClass);
    }

    // Podmenu do zarządzania przedmiotami
    private static void manageSubjects(Scanner scanner, SchoolManager manager) {
        boolean running = true;
        while (running) {
            System.out.println("\n----- ZARZĄDZANIE PRZEDMIOTAMI -----");
            System.out.println("1. Dodaj nowy przedmiot do klasy");
            System.out.println("0. Powrót do menu głównego");

            System.out.print("Wybierz opcję: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        addSubject(scanner, manager);
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Nieprawidłowa opcja. Spróbuj ponownie.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Błąd: Wprowadź liczbę.");
            }

            if (running) {
                System.out.println("\nNaciśnij ENTER, aby kontynuować...");
                scanner.nextLine();
            }
        }
    }

    // Dodaje nowy przedmiot do klasy
    private static void addSubject(Scanner scanner, SchoolManager manager) {
        System.out.print("Podaj nazwę przedmiotu: ");
        String subjectName = scanner.nextLine();

        System.out.print("Podaj klasę, do której zostanie dodany przedmiot: ");
        String className = scanner.nextLine();

        List<Teacher> teachers = manager.getAllTeachers();
        if (teachers.isEmpty()) {
            System.out.println("Brak dostępnych nauczycieli. Najpierw dodaj nauczyciela.");
            return;
        }

        System.out.println("Dostępni nauczyciele:");
        for (int i = 0; i < teachers.size(); i++) {
            Teacher teacher = teachers.get(i);
            System.out.println((i + 1) + ". " + teacher.getName() + " " + teacher.getSurname() +
                    " (PESEL: " + teacher.getPeselNumber() + ")");
        }

        System.out.print("Wybierz numer nauczyciela prowadzącego: ");
        try {
            int teacherIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (teacherIndex >= 0 && teacherIndex < teachers.size()) {
                manager.createSubject(subjectName, className, teachers.get(teacherIndex).getPeselNumber());
            } else {
                System.out.println("Nieprawidłowy numer nauczyciela.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Błąd: Wprowadź liczbę.");
        }
    }

    // Podmenu do zarządzania ocenami
    private static void manageGrades(Scanner scanner, SchoolManager manager) {
        boolean running = true;
        while (running) {
            System.out.println("\n----- ZARZĄDZANIE OCENAMI -----");
            System.out.println("1. Dodaj ocenę dla ucznia");
            System.out.println("0. Powrót do menu głównego");

            System.out.print("Wybierz opcję: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        addGrade(scanner, manager);
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Nieprawidłowa opcja. Spróbuj ponownie.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Błąd: Wprowadź liczbę.");
            }

            if (running) {
                System.out.println("\nNaciśnij ENTER, aby kontynuować...");
                scanner.nextLine();
            }
        }
    }

    // Dodaje ocenę dla ucznia
    private static void addGrade(Scanner scanner, SchoolManager manager) {
        System.out.print("Podaj PESEL ucznia: ");
        String studentPesel = scanner.nextLine();

        System.out.print("Podaj klasę ucznia: ");
        String className = scanner.nextLine();

        System.out.print("Podaj nazwę przedmiotu: ");
        String subjectName = scanner.nextLine();

        int grade = 0;
        boolean validGrade = false;
        while (!validGrade) {
            System.out.print("Podaj ocenę (1-6): ");
            try {
                grade = Integer.parseInt(scanner.nextLine());
                if (grade >= 1 && grade <= 6) {
                    validGrade = true;
                } else {
                    System.out.println("Błąd: Ocena musi być z zakresu 1-6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Błąd: Ocena musi być liczbą.");
            }
        }

        int weight = 0;
        boolean validWeight = false;
        while (!validWeight) {
            System.out.print("Podaj wagę oceny: ");
            try {
                weight = Integer.parseInt(scanner.nextLine());
                if (weight > 0) {
                    validWeight = true;
                } else {
                    System.out.println("Błąd: Waga musi być większa od 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Błąd: Waga musi być liczbą.");
            }
        }

        System.out.print("Podaj opis oceny: ");
        String description = scanner.nextLine();

        System.out.print("Podaj PESEL nauczyciela wystawiającego ocenę: ");
        String teacherPesel = scanner.nextLine();

        manager.addGradeToStudent(studentPesel, className, subjectName, grade, weight, description, teacherPesel);
    }

    // Wyświetla statystyki
    private static void displayStatistics(Scanner scanner, SchoolManager manager) {
        boolean running = true;
        while (running) {
            System.out.println("\n----- STATYSTYKI -----");
            System.out.println("1. Średnia ocen ucznia");
            System.out.println("2. Średnia ocen z przedmiotu w klasie");
            System.out.println("0. Powrót do menu głównego");

            System.out.print("Wybierz opcję: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        System.out.print("Podaj PESEL ucznia: ");
                        String studentPesel = scanner.nextLine();
                        System.out.print("Podaj klasę ucznia: ");
                        String studentClass = scanner.nextLine();
                        double studentAvg = manager.getStudentAverageGrade(studentPesel, studentClass);
                        if (studentAvg >= 0) {
                            System.out.println("Średnia ocen ucznia: " + String.format("%.2f", studentAvg));
                        }
                        break;
                    case 2:
                        System.out.print("Podaj nazwę klasy: ");
                        String className = scanner.nextLine();
                        System.out.print("Podaj nazwę przedmiotu: ");
                        String subjectName = scanner.nextLine();
                        double classAvg = manager.getClassAverageGrade(className, subjectName);
                        if (classAvg >= 0) {
                            System.out.println("Średnia ocen z przedmiotu " + subjectName +
                                    " w klasie " + className + ": " + String.format("%.2f", classAvg));
                        }
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        System.out.println("Nieprawidłowa opcja. Spróbuj ponownie.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Błąd: Wprowadź liczbę.");
            }

            if (running) {
                System.out.println("\nNaciśnij ENTER, aby kontynuować...");
                scanner.nextLine();
            }
        }
    }

    // Wyświetla informacje o szkole
    private static void displaySchoolInfo(SchoolManager manager) {
        System.out.println("\n----- INFORMACJE O SZKOLE -----");
        System.out.println("Nazwa szkoły: " + manager.getSchoolName());
        System.out.println("Liczba klas: " + manager.getAllClasses().size());
        System.out.println("Liczba nauczycieli: " + manager.getAllTeachers().size());

        int totalStudents = 0;
        for (Class cls : manager.getAllClasses()) {
            totalStudents += cls.getStudents().size();
        }
        System.out.println("Liczba uczniów: " + totalStudents);
    }

    // Inicjalizuje początkowe dane szkoły
    private static void initializeSchoolData(SchoolManager manager) {
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

    public static void test()
    {
        // Utworzenie szkoły
        School school = School.createSchool("Liceum im. Marii Skłodowskiej-Curie");
        SchoolManager manager = new SchoolManager(school);

        System.out.println("=== INICJALIZACJA SZKOŁY ===");

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

        // Dodanie ocen dla uczniów z klasy 1A
        manager.addGradeToStudent("07241012345", "1A", "Matematyka", 5, 2, "Sprawdzian - Równania", "65072512345");
        manager.addGradeToStudent("07241012345", "1A", "Matematyka", 4, 1, "Kartkówka - Funkcje", "65072512345");
        manager.addGradeToStudent("07241012345", "1A", "Fizyka", 3, 3, "Sprawdzian - Mechanika", "70112334567");

        manager.addGradeToStudent("07052234567", "1A", "Matematyka", 4, 2, "Sprawdzian - Równania", "65072512345");
        manager.addGradeToStudent("07052234567", "1A", "Fizyka", 5, 3, "Sprawdzian - Mechanika", "70112334567");
        manager.addGradeToStudent("07052234567", "1A", "Język Polski", 4, 2, "Wypracowanie", "80052265432");

        manager.addGradeToStudent("06121087654", "1A", "Matematyka", 3, 2, "Sprawdzian - Równania", "65072512345");
        manager.addGradeToStudent("06121087654", "1A", "Język Polski", 5, 2, "Wypracowanie", "80052265432");

        // Dodanie ocen dla uczniów z klasy 2B
        manager.addGradeToStudent("06082143215", "2B", "Matematyka", 5, 2, "Sprawdzian - Logarytmy", "65072512345");
        manager.addGradeToStudent("06082143215", "2B", "Biologia", 4, 3, "Sprawdzian - Genetyka", "83021987654");

        manager.addGradeToStudent("05112265432", "2B", "Matematyka", 3, 2, "Sprawdzian - Logarytmy", "65072512345");
        manager.addGradeToStudent("05112265432", "2B", "Język Polski", 5, 3, "Rozprawka", "80052265432");
        manager.addGradeToStudent("05112265432", "2B", "Biologia", 4, 3, "Sprawdzian - Genetyka", "83021987654");

        manager.addGradeToStudent("06062198765", "2B", "Matematyka", 4, 2, "Sprawdzian - Logarytmy", "65072512345");
        manager.addGradeToStudent("06062198765", "2B", "Język Polski", 3, 3, "Rozprawka", "80052265432");

        System.out.println("\n=== PREZENTACJA FUNKCJONALNOŚCI SYSTEMU ===");

        // Wyświetlenie listy wszystkich klas
        System.out.println("\n1. Lista wszystkich klas:");
        manager.displayAllClasses();

        // Wyświetlenie szczegółów klasy 1A
        System.out.println("\n2. Szczegóły klasy 1A:");
        manager.displayClassDetails("1A");

        // Wyświetlenie szczegółów ucznia
        System.out.println("\n3. Szczegóły ucznia Ewy Zielińskiej:");
        manager.displayStudentDetails("07052234567", "1A");

        // Przeniesienie ucznia między klasami
        System.out.println("\n4. Przeniesienie ucznia między klasami:");
        manager.transferStudent("06121087654", "1A", "2B");

        // Wyświetlenie zaktualizowanych szczegółów klas
        System.out.println("\n5. Zaktualizowane szczegóły klasy 1A (po przeniesieniu):");
        manager.displayClassDetails("1A");

        System.out.println("\n6. Zaktualizowane szczegóły klasy 2B (po przeniesieniu):");
        manager.displayClassDetails("2B");

        // Obliczenie średnich ocen
        System.out.println("\n7. Średnie ocen:");
        double avgMathClass1A = manager.getClassAverageGrade("1A", "Matematyka");
        System.out.println("Średnia z matematyki dla klasy 1A: " + String.format("%.2f", avgMathClass1A));

        double avgStudentKarolina = manager.getStudentAverageGrade("06082143215", "2B");
        System.out.println("Średnia Karoliny Lewandowskiej: " + String.format("%.2f", avgStudentKarolina));

        // Dodanie nowej oceny i sprawdzenie zmiany średniej
        System.out.println("\n8. Dodanie nowej oceny i sprawdzenie zmiany średniej:");
        manager.addGradeToStudent("06082143215", "2B", "Matematyka", 3, 1, "Kartkówka - Ciągi", "65072512345");

        double newAvgStudentKarolina = manager.getStudentAverageGrade("06082143215", "2B");
        System.out.println("Nowa średnia Karoliny Lewandowskiej: " + String.format("%.2f", newAvgStudentKarolina));

        // Usunięcie ucznia
        System.out.println("\n9. Usunięcie ucznia:");
        manager.removeStudent("06082143215", "2B");
        manager.displayClassDetails("2B");
    }

}