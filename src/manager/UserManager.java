package manager;

import domain.User;
import java.util.ArrayList;

public class UserManager {
    private ArrayList<User> users = new ArrayList<>();
    private User loggedInUser = null;

    // 싱글톤 패턴 적용
    private static UserManager instance = new UserManager();
    
    private UserManager() {
        // 테스트용 계정 미리 등록
        users.add(new User("test", "1234", "홍길동"));
    }

    public static UserManager getInstance() {
        return instance;
    }
    
    // 회원가입, 로그인, 로그아웃 기능 구현


    // 회원가입: 아이디 중복 체크 후 새 사용자 추가
    public boolean signup(String id, String password, String name) {
        for (User u : users) {
            if (u.getId().equals(id)) return false; // 중복 체크
        }
        users.add(new User(id, password, name));
        return true;
    }

    // 로그인: 아이디와 비밀번호 일치 여부 확인 후 로그인 처리
    public boolean login(String id, String password) {
        for (User u : users) {
            if (u.getId().equals(id) && u.getPassword().equals(password)) {
                loggedInUser = u;
                return true;
            }
        }
        return false;
    }
    // 로그아웃: 로그인된 사용자 정보 초기화
    public void logout() { loggedInUser = null; }
    
    // 로그인된 사용자 정보 반환 
    public User getLoggedInUser() { return loggedInUser; }
}