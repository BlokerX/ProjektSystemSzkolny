public class Teacher extends Person {
    private double salary;

    public Teacher(String name, String surname, int age, String peselNumber, double salary) {
        super(name, surname, age, peselNumber);
        this.salary = salary;
    }
}
