package entities;

import java.util.Objects;

public class Student {
    private String name;
    private String secondName;
    private String mat;

    public Student(String name, String secondName, String mat) {
        this.name = name;
        this.secondName = secondName;
        this.mat = mat;
    }

    public String getMat() {
        return mat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(mat, student.mat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mat);
    }
}
