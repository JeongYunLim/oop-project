package ui;

import manager.UserManager;
import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    public LoginPanel(MainFrame frame) {
        setBackground(Color.WHITE); 
        setOpaque(true); 
        
        setLayout(new GridLayout(4, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));

        JTextField idField = new JTextField();
        JPasswordField pwField = new JPasswordField();
        JButton loginBtn = new JButton("로그인");
        JButton toSignupBtn = new JButton("회원가입 이동");

        add(new JLabel("아이디:")); add(idField);
        add(new JLabel("비밀번호:")); add(pwField);
        add(loginBtn); add(toSignupBtn);

        // 로그인 
        loginBtn.addActionListener(e -> {
            String id = idField.getText();
            String pw = new String(pwField.getPassword());

            if (UserManager.getInstance().login(id, pw)) {
                frame.showCard("MYPAGE");

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame, "로그인 성공!");
                });
            } else {
                JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 틀렸습니다.");
            }
        });

        // 회원가입 화면으로
        toSignupBtn.addActionListener(e -> frame.showCard("SIGNUP"));
    }
}