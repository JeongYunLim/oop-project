package domain;

public class Admin {

    public static boolean isAdmin(User user) {
        return user != null && "admin".equals(user.getId());
    }
}
