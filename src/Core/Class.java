package Core;

import java.util.List;

public class Class {
    private String name;
    private List<Student> students;
    private Teacher supervisingTeacher;

    public Class(String name, List<Student> students, Teacher supervisingTeacher) {
        this.name = name;
        this.students = students;
        this.supervisingTeacher = supervisingTeacher;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<Student> getStudents() {
        return students;
    }
    public void addStudent(Student student) {
        students.add(student);
    }
    public void removeStudent(Student student) {
        students.remove(student);
    }

    public Student getStudentByPeselNumber(String peselNumber) {
        for (Student student : students) {
            if (student.getPeselNumber().equals(peselNumber)) {
                return student;
            }
        }
        return null;
    }

    public void addSubjectToAllStudents(Subject subject) {
        for (Student student : students) {
            student.addSubject(subject);
        }
    }

    public void removeSubjectFromAllStudents(Subject subject) {
        for (Student student : students) {
            student.removeSubject(subject);
        }
    }

    public double getAverageGrade(String subjectName) {
        double average = 0;
        int count = 0;
        for (Student student : students) {
            for(Subject subject : student.getSubjects())
            {
                if(subject.getName().equals(subjectName)){
                    for(Grade grade : subject.getGrades())
                    {
                        average += grade.getGradeWithWeight();
                        count++;
                    }
                }
            }
        }
        return count !=0  ? average / count : 0;
    }

    public Teacher getSupervisingTeacher() {
        return supervisingTeacher;
    }
    public void setSupervisingTeacher(Teacher supervisingTeacher) {
        this.supervisingTeacher = supervisingTeacher;
    }

}
