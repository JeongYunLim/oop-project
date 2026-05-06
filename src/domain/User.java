package domain;

public class User {
    private String id;
    private String password;
    private String name;
    private Temperature temperature;

    
    // 생성자: 회원가입 시 아이디, 비밀번호, 이름을 받아 초기화하고 온도 객체도 생성
    public User(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.temperature = new Temperature(); // 회원가입 시 생성
    }

    
    //getter 메서드: 아이디, 비밀번호, 이름, 온도 반환
    public String getId() { return id; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public Temperature getTemperature() { return temperature; }
}