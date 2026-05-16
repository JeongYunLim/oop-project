package ui;

import domain.Admin;
import domain.User;
import manager.NavigationManager;
import manager.UserManager;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color MAIN_COLOR = new Color(52, 120, 246);
    private final Color MAIN_DARK_COLOR = new Color(35, 90, 200);
    private final Color SUB_COLOR = new Color(235, 242, 255);
    private final Color TEXT_COLOR = new Color(40, 40, 40);
    private final Color GRAY_TEXT = new Color(110, 110, 110);

    private JLabel userInfoLabel = new JLabel("로그인하세요", SwingConstants.CENTER);

    private JButton loginBtn;
    private JButton signupBtn;
    private JButton itemListBtn;
    private JButton itemRegisterBtn;
    private JButton mypageBtn;
    private JButton adminBtn;
    private JButton logoutBtn;

    public MainPanel() {
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(20, 25));
        card.setBackground(CARD_COLOR);
        card.setPreferredSize(new Dimension(620, 500));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(CARD_COLOR);

        JLabel titleLabel = new JLabel("CampusShare");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 34));
        titleLabel.setForeground(MAIN_COLOR);

        JLabel subTitleLabel = new JLabel("광운대학교 캠퍼스 유휴자원 공유 플랫폼");
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        subTitleLabel.setForeground(GRAY_TEXT);

        userInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userInfoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        userInfoLabel.setForeground(TEXT_COLOR);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subTitleLabel);
        titlePanel.add(Box.createVerticalStrut(18));
        titlePanel.add(userInfoLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 14));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        loginBtn = createMainButton("로그인");
        signupBtn = createSubButton("회원가입");
        itemListBtn = createMainButton("물품 목록 보기");
        itemRegisterBtn = createSubButton("물품 등록");
        mypageBtn = createSubButton("마이페이지");
        adminBtn = createMainButton("관리자 페이지");
        logoutBtn = createTextButton("로그아웃");

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
            NavigationManager.getInstance().showPanel("LOGIN");
            JOptionPane.showMessageDialog(this, "로그아웃되었습니다.");
        });

        buttonPanel.add(loginBtn);
        buttonPanel.add(signupBtn);
        buttonPanel.add(itemListBtn);
        buttonPanel.add(itemRegisterBtn);
        buttonPanel.add(mypageBtn);
        buttonPanel.add(adminBtn);
        buttonPanel.add(logoutBtn);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(buttonPanel, BorderLayout.CENTER);

        add(card);
        refresh();
    }

    public void refresh() {
        User user = UserManager.getInstance().getLoggedInUser();

        if (user == null) {
            userInfoLabel.setText("로그인 후 서비스를 이용할 수 있습니다.");

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
                    user.getName(),
                    user.getTemperature().getValue()
            ));

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
                    user.getName(),
                    user.getTemperature().getValue()
            ));

            loginBtn.setVisible(false);
            signupBtn.setVisible(false);
            itemListBtn.setVisible(true);
            itemRegisterBtn.setVisible(true);
            mypageBtn.setVisible(true);
            adminBtn.setVisible(false);
            logoutBtn.setVisible(true);
        }

        revalidate();
        repaint();
    }

    private JButton createMainButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(MAIN_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(220, 48));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(MAIN_DARK_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(MAIN_COLOR);
            }
        });

        return button;
    }

    private JButton createSubButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        button.setForeground(MAIN_COLOR);
        button.setBackground(SUB_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(220, 48));

        return button;
    }

    private JButton createTextButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        button.setForeground(new Color(120, 120, 120));
        button.setBackground(new Color(245, 245, 245));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(220, 48));

        return button;
    }
}