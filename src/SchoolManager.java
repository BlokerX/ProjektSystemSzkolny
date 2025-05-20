import java.util.ArrayList;
import java.util.List;

public class SchoolManager {
    private School school;

    public SchoolManager(School school) {
        this.school = school;
    }

    // Metody związane ze szkołą
    public String getSchoolName() {
        return school.getName();
    }

    public void changeSchoolName(String newName) {
        school.setName(newName);
    }

    // Metody związane z klasami
    public void createClass(String name, Teacher supervisingTeacher) {
        Class newClass = new Class(name, new ArrayList<>(), supervisingTeacher);
        school.addClass(newClass);
    }

    public void removeClassByName(String className) {
        List<Class> classes = school.getClasses();
        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i).getName().equals(className)) {
                school.removeClass(classes.get(i));
                return;
            }
        }
        System.out.println("Klasa o nazwie " + className + " nie istnieje.");
    }

    public Class getClassByName(String className) {
        for (Class cls : school.getClasses()) {
            if (cls.getName().equals(className)) {
                return cls;
            }
        }
        return null;
    }

    public List<Class> getAllClasses() {
        return school.getClasses();
    }

    // Metody związane z nauczycielami
    public void hireTeacher(String name, String surname, int age, String peselNumber, double salary) {
        Teacher newTeacher = new Teacher(name, surname, age, peselNumber, salary);
        school.addTeacher(newTeacher);
    }

    public void fireTeacher(String peselNumber) {
        Teacher teacher = school.getTeacherByPesel(peselNumber);
        if (teacher != null) {
            school.removeTeacher(teacher);
        } else {
            System.out.println("Nauczyciel o numerze PESEL " + peselNumber + " nie istnieje.");
        }
    }

    public List<Teacher> getAllTeachers() {
        return school.getTeachers();
    }

    // Metody związane z uczniami
    public void enrollStudent(String name, String surname, int age, String peselNumber, String className) {
        Class targetClass = getClassByName(className);
        if (targetClass != null) {
            Student newStudent = new Student(name, surname, age, peselNumber, new ArrayList<>());
            targetClass.addStudent(newStudent);
        } else {
            System.out.println("Klasa o nazwie " + className + " nie istnieje.");
        }
    }

    public void removeStudent(String peselNumber, String className) {
        Class targetClass = getClassByName(className);
        if (targetClass != null) {
            Student student = targetClass.getStudentByPeselNumber(peselNumber);
            if (student != null) {
                targetClass.removeStudent(student);
            } else {
                System.out.println("Uczeń o numerze PESEL " + peselNumber + " nie istnieje w klasie " + className);
            }
        } else {
            System.out.println("Klasa o nazwie " + className + " nie istnieje.");
        }
    }

    public void transferStudent(String peselNumber, String fromClassName, String toClassName) {
        Class fromClass = getClassByName(fromClassName);
        Class toClass = getClassByName(toClassName);

        if (fromClass == null) {
            System.out.println("Klasa źródłowa o nazwie " + fromClassName + " nie istnieje.");
            return;
        }

        if (toClass == null) {
            System.out.println("Klasa docelowa o nazwie " + toClassName + " nie istnieje.");
            return;
        }

        Student student = fromClass.getStudentByPeselNumber(peselNumber);
        if (student != null) {
            fromClass.removeStudent(student);
            toClass.addStudent(student);
            System.out.println("Przeniesiono ucznia " + student.getName() + " " + student.getSurname() +
                    " z klasy " + fromClassName + " do klasy " + toClassName);
        } else {
            System.out.println("Uczeń o numerze PESEL " + peselNumber + " nie istnieje w klasie " + fromClassName);
        }
    }

    // Metody związane z przedmiotami i ocenami
    public void createSubject(String subjectName, String className, String teacherPesel) {
        Teacher teacher = school.getTeacherByPesel(teacherPesel);
        if (teacher == null) {
            System.out.println("Nauczyciel o numerze PESEL " + teacherPesel + " nie istnieje.");
            return;
        }

        Class targetClass = getClassByName(className);
        if (targetClass == null) {
            System.out.println("Klasa o nazwie " + className + " nie istnieje.");
            return;
        }

        Subject newSubject = new Subject(subjectName, new ArrayList<>(), teacher);
        targetClass.addSubjectToAllStudents(newSubject);
        System.out.println("Dodano przedmiot " + subjectName + " do klasy " + className);
    }

    public void addGradeToStudent(String studentPesel, String className, String subjectName,
                                  int grade, int weight, String description, String teacherPesel) {
        Class targetClass = getClassByName(className);
        if (targetClass == null) {
            System.out.println("Klasa o nazwie " + className + " nie istnieje.");
            return;
        }

        Student student = targetClass.getStudentByPeselNumber(studentPesel);
        if (student == null) {
            System.out.println("Uczeń o numerze PESEL " + studentPesel + " nie istnieje w klasie " + className);
            return;
        }

        Teacher teacher = school.getTeacherByPesel(teacherPesel);
        if (teacher == null) {
            System.out.println("Nauczyciel o numerze PESEL " + teacherPesel + " nie istnieje.");
            return;
        }

        for (Subject subject : student.getSubjects()) {
            if (subject.getName().equals(subjectName)) {
                Grade newGrade = new Grade(grade, weight, description, teacher);
                subject.addGrade(newGrade);
                System.out.println("Dodano ocenę " + grade + " dla ucznia " + student.getName() +
                        " " + student.getSurname() + " z przedmiotu " + subjectName);
                return;
            }
        }
        System.out.println("Przedmiot o nazwie " + subjectName + " nie istnieje dla tego ucznia.");
    }

    public double getStudentAverageGrade(String studentPesel, String className) {
        Class targetClass = getClassByName(className);
        if (targetClass == null) {
            System.out.println("Klasa o nazwie " + className + " nie istnieje.");
            return -1;
        }

        Student student = targetClass.getStudentByPeselNumber(studentPesel);
        if (student == null) {
            System.out.println("Uczeń o numerze PESEL " + studentPesel + " nie istnieje w klasie " + className);
            return -1;
        }

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

    public double getClassAverageGrade(String className, String subjectName) {
        Class targetClass = getClassByName(className);
        if (targetClass == null) {
            System.out.println("Klasa o nazwie " + className + " nie istnieje.");
            return -1;
        }

        return targetClass.getAverageGrade(subjectName);
    }

    // Metody pomocnicze do wyświetlania informacji
    public void displayAllClasses() {
        List<Class> classes = school.getClasses();
        System.out.println("Klasy w szkole " + school.getName() + ":");
        for (Class cls : classes) {
            System.out.println("- " + cls.getName() + " (wychowawca: " +
                    cls.getSupervisingTeacher().getName() + " " +
                    cls.getSupervisingTeacher().getSurname() + ")");
        }
    }

    public void displayClassDetails(String className) {
        Class targetClass = getClassByName(className);
        if (targetClass == null) {
            System.out.println("Klasa o nazwie " + className + " nie istnieje.");
            return;
        }

        System.out.println("Informacje o klasie " + className + ":");
        System.out.println("Wychowawca: " + targetClass.getSupervisingTeacher().getName() +
                " " + targetClass.getSupervisingTeacher().getSurname());
        System.out.println("Liczba uczniów: " + targetClass.getStudents().size());

        System.out.println("Lista uczniów:");
        for (Student student : targetClass.getStudents()) {
            System.out.println("- " + student.getName() + " " + student.getSurname() +
                    " (PESEL: " + student.getPeselNumber() + ")");
        }
    }

    public void displayStudentDetails(String studentPesel, String className) {
        Class targetClass = getClassByName(className);
        if (targetClass == null) {
            System.out.println("Klasa o nazwie " + className + " nie istnieje.");
            return;
        }

        Student student = targetClass.getStudentByPeselNumber(studentPesel);
        if (student == null) {
            System.out.println("Uczeń o numerze PESEL " + studentPesel + " nie istnieje w klasie " + className);
            return;
        }

        System.out.println("Informacje o uczniu " + student.getName() + " " + student.getSurname() + ":");
        System.out.println("PESEL: " + student.getPeselNumber());
        System.out.println("Wiek: " + student.getAge());
        System.out.println("Klasa: " + className);

        System.out.println("Przedmioty i oceny:");
        for (Subject subject : student.getSubjects()) {
            System.out.println("- " + subject.getName() + " (nauczyciel: " +
                    subject.getLeadingTeacher().getName() + " " +
                    subject.getLeadingTeacher().getSurname() + ")");
            if (subject.getGrades().isEmpty()) {
                System.out.println("  Brak ocen");
            } else {
                for (Grade grade : subject.getGrades()) {
                    System.out.println("  Ocena: " + grade.getGrade() +
                            " (waga: " + grade.getWeight() + ")" +
                            " - " + grade.getDescription());
                }
                System.out.println("  Średnia: " + String.format("%.2f", subject.getAverageGrade()));
            }
        }

        double averageGrade = getStudentAverageGrade(studentPesel, className);
        System.out.println("Średnia ogólna: " + String.format("%.2f", averageGrade));
    }
}