package ui;

import manager.NavigationManager;
import manager.UserManager;

import javax.swing.*;
import java.awt.*;

public class SignupPanel extends JPanel {

    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color MAIN_COLOR = new Color(52, 120, 246);
    private final Color MAIN_DARK_COLOR = new Color(35, 90, 200);
    private final Color SUB_COLOR = new Color(235, 242, 255);
    private final Color TEXT_COLOR = new Color(40, 40, 40);
    private final Color GRAY_TEXT = new Color(110, 110, 110);

    private boolean phoneVerified = false;
    private String generatedCode = null;

    public SignupPanel() {
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(35, 42, 35, 42)
        ));

        JLabel titleLabel = new JLabel("회원가입");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setForeground(MAIN_COLOR);

        JLabel subTitleLabel = new JLabel("CampusShare 이용을 위해 정보를 입력해주세요.");
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        subTitleLabel.setForeground(GRAY_TEXT);

        JTextField idField = createTextField();
        JPasswordField pwField = createPasswordField();
        JTextField nameField = createTextField();
        JTextField phoneField = createTextField();
        JTextField codeField = createTextField();

        JButton sendCodeBtn = createSubButton("인증번호 발송");
        JLabel verifiedLabel = new JLabel("(미인증)", SwingConstants.CENTER);
        verifiedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        verifiedLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        verifiedLabel.setForeground(GRAY_TEXT);

        JButton verifyBtn = createSubButton("인증 확인");
        JButton signupBtn = createMainButton("가입 완료");
        JButton backBtn = createTextButton("로그인으로 돌아가기");

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(subTitleLabel);
        card.add(Box.createVerticalStrut(24));

        card.add(createLabel("아이디"));
        card.add(Box.createVerticalStrut(5));
        card.add(idField);
        card.add(Box.createVerticalStrut(12));

        card.add(createLabel("비밀번호"));
        card.add(Box.createVerticalStrut(5));
        card.add(pwField);
        card.add(Box.createVerticalStrut(12));

        card.add(createLabel("이름"));
        card.add(Box.createVerticalStrut(5));
        card.add(nameField);
        card.add(Box.createVerticalStrut(12));

        card.add(createLabel("전화번호"));
        card.add(Box.createVerticalStrut(5));
        card.add(phoneField);
        card.add(Box.createVerticalStrut(8));

        JPanel phoneAuthPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        phoneAuthPanel.setBackground(CARD_COLOR);
        phoneAuthPanel.setMaximumSize(new Dimension(320, 38));
        phoneAuthPanel.add(sendCodeBtn);
        phoneAuthPanel.add(verifiedLabel);

        card.add(phoneAuthPanel);
        card.add(Box.createVerticalStrut(12));

        card.add(createLabel("인증번호"));
        card.add(Box.createVerticalStrut(5));
        card.add(codeField);
        card.add(Box.createVerticalStrut(8));
        card.add(verifyBtn);
        card.add(Box.createVerticalStrut(20));
        card.add(signupBtn);
        card.add(Box.createVerticalStrut(14));
        card.add(backBtn);

        add(card);

        sendCodeBtn.addActionListener(e -> {
            String phone = phoneField.getText().trim();

            if (!phone.matches("^010-?\\d{4}-?\\d{4}$")) {
                JOptionPane.showMessageDialog(this,
                        "전화번호 형식이 올바르지 않습니다.\n예: 01012345678 또는 010-1234-5678");
                return;
            }

            int code = (int) (Math.random() * 900000) + 100000;
            generatedCode = String.valueOf(code);
            phoneVerified = false;

            verifiedLabel.setText("(미인증)");
            verifiedLabel.setForeground(GRAY_TEXT);

            JOptionPane.showMessageDialog(this,
                    "인증번호: " + generatedCode + "\n(실제 서비스에서는 문자로 발송됩니다)");
        });

        verifyBtn.addActionListener(e -> {
            if (generatedCode == null) {
                JOptionPane.showMessageDialog(this, "먼저 인증번호를 발송하세요.");
                return;
            }

            if (generatedCode.equals(codeField.getText().trim())) {
                phoneVerified = true;
                verifiedLabel.setText("✓ 인증 완료");
                verifiedLabel.setForeground(new Color(39, 174, 96));
                JOptionPane.showMessageDialog(this, "인증이 완료되었습니다.");
            } else {
                JOptionPane.showMessageDialog(this, "인증번호가 일치하지 않습니다. 다시 확인해주세요.");
            }
        });

        signupBtn.addActionListener(e -> {
            if (!phoneVerified) {
                JOptionPane.showMessageDialog(this, "전화번호 인증을 완료해주세요.");
                return;
            }

            String id = idField.getText().trim();
            String pw = new String(pwField.getPassword());
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (id.isEmpty() || pw.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.");
                return;
            }

            if (UserManager.getInstance().signup(id, pw, name, phone)) {
                JOptionPane.showMessageDialog(this, "가입 성공! 로그인해주세요.");

                idField.setText("");
                pwField.setText("");
                nameField.setText("");
                phoneField.setText("");
                codeField.setText("");

                phoneVerified = false;
                generatedCode = null;
                verifiedLabel.setText("(미인증)");
                verifiedLabel.setForeground(GRAY_TEXT);

                NavigationManager.getInstance().showPanel("LOGIN");
            } else {
                JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.");
            }
        });

        backBtn.addActionListener(e ->
                NavigationManager.getInstance().showPanel("LOGIN")
        );
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(320, 20));
        label.setPreferredSize(new Dimension(320, 20));
        label.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(320, 38));
        field.setPreferredSize(new Dimension(320, 38));
        field.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225), 1),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(320, 38));
        field.setPreferredSize(new Dimension(320, 38));
        field.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225), 1),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        return field;
    }

    private JButton createMainButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(320, 42));
        button.setPreferredSize(new Dimension(320, 42));
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
        button.setMaximumSize(new Dimension(320, 38));
        button.setPreferredSize(new Dimension(320, 38));
        button.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        button.setForeground(MAIN_COLOR);
        button.setBackground(SUB_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createTextButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        button.setForeground(new Color(120, 120, 120));
        button.setBackground(CARD_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}