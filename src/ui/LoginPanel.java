package ui;

import manager.NavigationManager;
import manager.UserManager;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color MAIN_COLOR = new Color(52, 120, 246);
    private final Color MAIN_DARK_COLOR = new Color(35, 90, 200);
    private final Color TEXT_COLOR = new Color(40, 40, 40);

    private JTextField idField;
    private JPasswordField pwField;

    public LoginPanel() {
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(35, 40, 35, 40)
        ));

        JLabel titleLabel = new JLabel("CampusShare");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setForeground(MAIN_COLOR);

        JLabel subTitleLabel = new JLabel("광운대학교 유휴자원 공유 플랫폼");
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        subTitleLabel.setForeground(new Color(110, 110, 110));

        idField = createTextField();
        pwField = createPasswordField();

        JButton loginBtn = createMainButton("로그인");
        JButton toSignupBtn = createSubButton("회원가입");

        JLabel idLabel = createLabel("아이디");
        JLabel pwLabel = createLabel("비밀번호");

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(subTitleLabel);
        card.add(Box.createVerticalStrut(30));

        card.add(idLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(idField);
        card.add(Box.createVerticalStrut(15));

        card.add(pwLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(pwField);
        card.add(Box.createVerticalStrut(25));

        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(toSignupBtn);

        add(card);

        loginBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String pw = new String(pwField.getPassword());

            if (UserManager.getInstance().login(id, pw)) {
                clearFields();
                NavigationManager.getInstance().showPanel("MAIN");

                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "로그인 성공!")
                );
            } else {
                JOptionPane.showMessageDialog(this, UserManager.getInstance().getLastLoginError());
            }
        });

        toSignupBtn.addActionListener(e -> {
            clearFields();
            NavigationManager.getInstance().showPanel("SIGNUP");
        });
    }

    public void clearFields() {
        if (idField != null) {
            idField.setText("");
        }

        if (pwField != null) {
            pwField.setText("");
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(320, 22));
        label.setPreferredSize(new Dimension(320, 22));
        label.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(320, 42));
        field.setPreferredSize(new Dimension(320, 42));
        field.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(320, 42));
        field.setPreferredSize(new Dimension(320, 42));
        field.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JButton createMainButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(320, 44));
        button.setPreferredSize(new Dimension(320, 44));
        button.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(MAIN_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(320, 42));
        button.setPreferredSize(new Dimension(320, 42));
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setForeground(MAIN_COLOR);
        button.setBackground(new Color(235, 242, 255));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}