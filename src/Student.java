import java.util.List;

public class Student extends Person {
    private List<Subject> subjects;

    public Student(String name, String surname, int age, String peselNumber, List<Subject> subjects) {
        super(name, surname, age, peselNumber);
        this.subjects = subjects;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }
    public void removeSubject(Subject subject) {
        subjects.remove(subject);
    }

    @Override
    public String toString() {
        return "Student{" +
                "subjects=" + subjects +
                '}';
    }
}
