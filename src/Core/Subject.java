package Core;

import java.util.List;

public class Subject {
    private String name;
    private List<Grade> grades;
    private Teacher leadingTeacher;

    public Subject(String name, List<Grade> grades, Teacher leadingTeacher) {
        this.name = name;
        this.grades = grades;
        this.leadingTeacher = leadingTeacher;
    }

    public double getAverageGrade() {
        double average = 0;
        for (Grade grade : grades) {
            average += grade.getGrade();
        }
        return average / grades.size();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void addGrade(Grade grade) {
        grades.add(grade);
    }

    public void removeGrade(Grade grade) {
        grades.remove(grade);
    }

    public Teacher getLeadingTeacher() {
        return leadingTeacher;
    }
    public void setLeadingTeacher(Teacher leadingTeacher) {
        this.leadingTeacher = leadingTeacher;
    }
}
