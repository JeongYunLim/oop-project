package domain;

public class User {
    private String name;
    private String studentId;
    private Temperature temperature;

    public User(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
        this.temperature = new Temperature();
    }

    public String getName() {
        return name;
    }

    public String getStudentId() {
        return studentId;
    }

    public Temperature getTemperature() {
        return temperature;
    }
}