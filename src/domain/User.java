 package domain;

public class User {
    private String id;
    private String password;
    private String name;
    private Temperature temperature;

    // 회원가입용 생성자
    public User(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.temperature = new Temperature();
    }

    // 기존 테스트 코드 호환용 생성자
    // 예: new User("김소유", "20240001")
    public User(String name, String studentId) {
        this.id = studentId;
        this.password = "";
        this.name = name;
        this.temperature = new Temperature();
    }

    // getter 메서드
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