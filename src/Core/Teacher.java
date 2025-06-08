package Core;

import java.io.Serializable;

public class Teacher extends Person implements Serializable {
    private double salary;

    public Teacher(String name, String surname, int age, String peselNumber, double salary) {
        super(name, surname, age, peselNumber);
        this.salary = salary;
    }
}
