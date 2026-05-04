package app.manager;

import app.domain.User;
import java.util.ArrayList;

public class UserManager {
    private ArrayList<User> users = new ArrayList<>();
    private User loggedInUser = null;

    // 싱글톤 패턴 적용[cite: 7]
    private static UserManager instance = new UserManager();
    
    private UserManager() {
        // 테스트용 계정 미리 등록
        users.add(new User("test", "1234", "홍길동"));
    }

    public static UserManager getInstance() {
        return instance;
    }

    public boolean signup(String id, String password, String name) {
        for (User u : users) {
            if (u.getId().equals(id)) return false; // 중복 체크
        }
        users.add(new User(id, password, name));
        return true;
    }

    public boolean login(String id, String password) {
        for (User u : users) {
            if (u.getId().equals(id) && u.getPassword().equals(password)) {
                loggedInUser = u;
                return true;
            }
        }
        return false;
    }

    public void logout() { loggedInUser = null; }
    public User getLoggedInUser() { return loggedInUser; }
}