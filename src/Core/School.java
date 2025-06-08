package Core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class School implements Serializable {
    private String name;
    private List<Core.Class> classes;
    private List<Teacher> teachers;

    private School(String name) {
        this.name = name;
        this.classes = new ArrayList<Core.Class>();
        this.teachers = new ArrayList<Teacher>();
    }

    public static School createSchool(String name) {
        School school = new School(name);
        return school;
    }

    public void addClass(Core.Class _class) {
        this.classes.add(_class);
    }

    public void addTeacher(Teacher _teacher) {
        this.teachers.add(_teacher);
    }

    public void removeClass(Core.Class _class){
        this.classes.remove(_class);
    }

    public void removeTeacher(Teacher _teacher){
        this.teachers.remove(_teacher);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<Class> getClasses(){
        return classes;
    }

    public List<Teacher> getTeachers(){
        return teachers;
    }

    public Teacher getTeacherByPesel(String pesel){
        for (Teacher teacher : teachers) {
            if(teacher.getPeselNumber().equals(pesel)){
                return teacher;
            }
        }
        return null;
    }
}
