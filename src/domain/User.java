package domain;

public class User {
    private String id;
    private String password;
    private String name;
    private Temperature temperature;

    public User(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.temperature = new Temperature();
    }

    // 기존 테스트 코드와의 호환용 생성자
    public User(String name, String studentId) {
        this.id = studentId;
        this.password = "";
        this.name = name;
        this.temperature = new Temperature();
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public Temperature getTemperature() {
        return temperature;
    }
}