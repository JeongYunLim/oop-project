package ui;

import domain.Admin;
import domain.User;
import manager.NavigationManager;
import manager.UserManager;
import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    private JLabel userInfoLabel = new JLabel("로그인하세요");

    private JButton loginBtn;
    private JButton signupBtn;
    private JButton itemListBtn;
    private JButton itemRegisterBtn;
    private JButton mypageBtn;
    private JButton adminBtn;
    private JButton logoutBtn;

    public MainPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        userInfoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        topPanel.add(userInfoLabel);

        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("CampusShare", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titlePanel.add(titleLabel);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(titlePanel, BorderLayout.NORTH);
        northPanel.add(topPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));

        loginBtn        = new JButton("로그인");
        signupBtn       = new JButton("회원가입");
        itemListBtn     = new JButton("물품 목록 보기");
        itemRegisterBtn = new JButton("물품 등록");
        mypageBtn       = new JButton("마이페이지");
        adminBtn        = new JButton("관리자 페이지");
        logoutBtn       = new JButton("로그아웃");

        loginBtn.addActionListener(e ->
            NavigationManager.getInstance().showPanel("LOGIN"));
        signupBtn.addActionListener(e ->
            NavigationManager.getInstance().showPanel("SIGNUP"));
        itemListBtn.addActionListener(e ->
            NavigationManager.getInstance().showPanel("ITEM_LIST"));
        itemRegisterBtn.addActionListener(e -> {
            if (UserManager.getInstance().getLoggedInUser() == null) {
                JOptionPane.showMessageDialog(this, "로그인이 필요합니다.");
            } else {
                NavigationManager.getInstance().showPanel("ITEM_REGISTER");
            }
        });
        mypageBtn.addActionListener(e -> {
            if (UserManager.getInstance().getLoggedInUser() == null) {
                JOptionPane.showMessageDialog(this, "로그인이 필요합니다.");
            } else {
                NavigationManager.getInstance().showPanel("MYPAGE");
            }
        });
        adminBtn.addActionListener(e -> {
            User user = UserManager.getInstance().getLoggedInUser();
            if (!Admin.isAdmin(user)) {
                JOptionPane.showMessageDialog(this, "관리자만 접근 가능합니다.");
                return;
            }
            NavigationManager.getInstance().showPanel("ADMIN");
        });
        logoutBtn.addActionListener(e -> {
            UserManager.getInstance().logout();
            refresh();
            JOptionPane.showMessageDialog(this, "로그아웃되었습니다.");
        });

        buttonPanel.add(loginBtn);
        buttonPanel.add(signupBtn);
        buttonPanel.add(itemListBtn);
        buttonPanel.add(itemRegisterBtn);
        buttonPanel.add(mypageBtn);
        buttonPanel.add(adminBtn);
        buttonPanel.add(logoutBtn);
        buttonPanel.add(new JLabel());

        add(northPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    public void refresh() {
        User user = UserManager.getInstance().getLoggedInUser();
        if (user == null) {
            userInfoLabel.setText("로그인하세요");
            loginBtn.setVisible(true);
            signupBtn.setVisible(true);
            itemListBtn.setVisible(false);
            itemRegisterBtn.setVisible(false);
            mypageBtn.setVisible(false);
            adminBtn.setVisible(false);
            logoutBtn.setVisible(false);
        } else if (Admin.isAdmin(user)) {
            userInfoLabel.setText(String.format(
                "%s님 안녕하세요! 매너온도: %.1f°C",
                user.getName(), user.getTemperature().getValue()));
            loginBtn.setVisible(false);
            signupBtn.setVisible(false);
            itemListBtn.setVisible(false);
            itemRegisterBtn.setVisible(false);
            mypageBtn.setVisible(false);
            adminBtn.setVisible(true);
            logoutBtn.setVisible(true);
        } else {
            userInfoLabel.setText(String.format(
                "%s님 안녕하세요! 매너온도: %.1f°C",
                user.getName(), user.getTemperature().getValue()));
            loginBtn.setVisible(false);
            signupBtn.setVisible(false);
            itemListBtn.setVisible(true);
            itemRegisterBtn.setVisible(true);
            mypageBtn.setVisible(true);
            adminBtn.setVisible(false);
            logoutBtn.setVisible(true);
        }
    }
}
