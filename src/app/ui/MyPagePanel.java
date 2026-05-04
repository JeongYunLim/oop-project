package app.ui;

import app.domain.User;
import app.manager.UserManager;
import javax.swing.*;
import java.awt.*;

public class MyPagePanel extends JPanel {
    private JLabel nameLabel = new JLabel();
    private JLabel tempLabel = new JLabel();

    public MyPagePanel(MainFrame frame) {
        setLayout(new GridLayout(4, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));

        JButton logoutBtn = new JButton("로그아웃");
        add(new JLabel("--- 마이페이지 ---"));
        add(nameLabel);
        add(tempLabel);
        add(logoutBtn);

        logoutBtn.addActionListener(e -> {
            UserManager.getInstance().logout();
            frame.showCard("LOGIN");
        });
    }

    public void updateUI() {
        User user = UserManager.getInstance().getLoggedInUser();
        if (user != null) {
            nameLabel.setText("이름: " + user.getName());
            tempLabel.setText(String.format("매너온도: %.1f °C", user.getTemperature().getDegree()));
        }
    }
}