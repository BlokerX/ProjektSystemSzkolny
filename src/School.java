import java.util.ArrayList;
import java.util.List;

public class School {
    private String name;
    private List<Class> classes;
    private List<Teacher> teachers;

    private School(String name) {
        this.name = name;
        this.classes = new ArrayList<Class>();
        this.teachers = new ArrayList<Teacher>();
    }

    public static School createSchool(String name) {
        School school = new School(name);
        return school;
    }

    public void addClass(Class _class) {
        this.classes.add(_class);
    }

    public void addTeacher(Teacher _teacher) {
        this.teachers.add(_teacher);
    }

    public void removeClass(Class _class){
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
