package app.domain;

public class User {
    private String id;
    private String password;
    private String name;
    private Temperature temperature;

    public User(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.temperature = new Temperature(); // 회원가입 시 생성[cite: 6]
    }

    public String getId() { return id; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public Temperature getTemperature() { return temperature; }
}