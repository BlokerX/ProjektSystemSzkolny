package Core;

import java.io.Serializable;

public abstract class Person implements Serializable {
    private String name;
    private String surname;
    private int age;
    private String peselNumber;

    public Person(String name, String surname, int age, String peselNumber) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.peselNumber = peselNumber;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPeselNumber() {
        return peselNumber;
    }

    public void setPeselNumber(String peselNumber) {
        this.peselNumber = peselNumber;
    }
}
