import java.util.Date;

public class Grade {
    private int grade;
    private int weight;
    private String description;
    private Teacher author;

    public Grade(int grade, int weight, String description, Teacher author) {
        this.grade = grade;
        this.weight = weight;
        this.description = description;
        this.author = author;
    }

    public int getGrade() {
        return grade;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Teacher getAuthor() {
        return author;
    }

    public double getGradeWithWeight(){
        return grade*weight;
    }
}
