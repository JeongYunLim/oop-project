package ui;

import manager.UserManager;
import javax.swing.*;
import java.awt.*;

public class SignupPanel extends JPanel {
    public SignupPanel(MainFrame frame) {
        setLayout(new GridLayout(5, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));

        JTextField idField = new JTextField();
        JPasswordField pwField = new JPasswordField();
        JTextField nameField = new JTextField();
        JButton signupBtn = new JButton("가입 완료");
        JButton backBtn = new JButton("뒤로가기");

        add(new JLabel("아이디:")); add(idField);
        add(new JLabel("비밀번호:")); add(pwField);
        add(new JLabel("이름:")); add(nameField);
        add(signupBtn); add(backBtn);

        signupBtn.addActionListener(e -> {
            if (UserManager.getInstance().signup(idField.getText(), new String(pwField.getPassword()), nameField.getText())) {
                JOptionPane.showMessageDialog(this, "가입 성공! 로그인해주세요.");
                frame.showCard("LOGIN");
            } else {
                JOptionPane.showMessageDialog(this, "아이디 중복!");
            }
        });

        backBtn.addActionListener(e -> frame.showCard("LOGIN"));
    }
}