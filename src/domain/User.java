package domain;

public class User {
    private String id;
    private String password;
    private String name;
    private Temperature temperature;
    private boolean isBanned = false;
    private String phoneNumber = "";
    private boolean phoneVerified = false;

    public User(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.temperature = new Temperature();
    }

    public String getId() { return id; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public Temperature getTemperature() { return temperature; }
    public String getPhoneNumber() { return phoneNumber; }
   
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber != null ? phoneNumber : ""; }
    public void setPhoneVerified(boolean phoneVerified) { this.phoneVerified = phoneVerified; }
    public void setBanned(boolean banned) { this.isBanned = banned; }
    
    public boolean isBanned() { return isBanned; }
    public boolean isPhoneVerified() { return phoneVerified; }
    
}