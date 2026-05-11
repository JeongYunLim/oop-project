package ui;

import manager.NavigationManager;
import manager.UserManager;
import javax.swing.*;
import java.awt.*;

public class SignupPanel extends JPanel {

    private boolean phoneVerified = false;
    private String generatedCode = null;

    public SignupPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel title = new JLabel("회원가입", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 8행 2열: 아이디/비밀번호/이름/전화번호/발송버튼+상태/인증번호/인증버튼/가입+뒤로
        JPanel form = new JPanel(new GridLayout(8, 2, 8, 8));

        JTextField idField    = new JTextField();
        JPasswordField pwField = new JPasswordField();
        JTextField nameField  = new JTextField();
        JTextField phoneField = new JTextField();
        JButton sendCodeBtn   = new JButton("인증번호 발송");
        JLabel verifiedLabel  = new JLabel("  (미인증)");
        JTextField codeField  = new JTextField();
        JButton verifyBtn     = new JButton("인증 확인");
        JButton signupBtn     = new JButton("가입 완료");
        JButton backBtn       = new JButton("뒤로가기");

        form.add(new JLabel("아이디:"));    form.add(idField);
        form.add(new JLabel("비밀번호:"));  form.add(pwField);
        form.add(new JLabel("이름:"));      form.add(nameField);
        form.add(new JLabel("전화번호:"));  form.add(phoneField);
        form.add(sendCodeBtn);              form.add(verifiedLabel);
        form.add(new JLabel("인증번호:"));  form.add(codeField);
        form.add(verifyBtn);                form.add(new JLabel());
        form.add(signupBtn);                form.add(backBtn);

        // 인증번호 발송: 형식 검사 후 6자리 코드 생성 → JOptionPane으로 표시
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
            verifiedLabel.setText("  (미인증)");
            // 실제 SMS 대신 화면에 표시 (테스트 목적)
            JOptionPane.showMessageDialog(this,
                "인증번호: " + generatedCode + "\n(실제 서비스에서는 문자로 발송됩니다)");
        });

        // 인증 확인: 입력값과 생성된 코드 비교
        verifyBtn.addActionListener(e -> {
            if (generatedCode == null) {
                JOptionPane.showMessageDialog(this, "먼저 인증번호를 발송하세요.");
                return;
            }
            if (generatedCode.equals(codeField.getText().trim())) {
                phoneVerified = true;
                verifiedLabel.setText("  ✓ 인증 완료");
                JOptionPane.showMessageDialog(this, "인증이 완료되었습니다.");
            } else {
                JOptionPane.showMessageDialog(this, "인증번호가 일치하지 않습니다. 다시 확인해주세요.");
            }
        });

        // 가입 완료: 인증 여부 확인 후 UserManager에 등록
        signupBtn.addActionListener(e -> {
            if (!phoneVerified) {
                JOptionPane.showMessageDialog(this, "전화번호 인증을 완료해주세요.");
                return;
            }
            String id    = idField.getText().trim();
            String pw    = new String(pwField.getPassword());
            String name  = nameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (id.isEmpty() || pw.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.");
                return;
            }

            if (UserManager.getInstance().signup(id, pw, name, phone)) {
                JOptionPane.showMessageDialog(this, "가입 성공! 로그인해주세요.");
                // 입력 초기화
                idField.setText(""); pwField.setText(""); nameField.setText("");
                phoneField.setText(""); codeField.setText("");
                phoneVerified = false; generatedCode = null;
                verifiedLabel.setText("  (미인증)");
                NavigationManager.getInstance().showPanel("LOGIN");
            } else {
                JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.");
            }
        });

        backBtn.addActionListener(e ->
            NavigationManager.getInstance().showPanel("LOGIN"));

        add(title, BorderLayout.NORTH);
        add(form,  BorderLayout.CENTER);
    }
}
