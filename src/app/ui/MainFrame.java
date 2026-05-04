package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);
    private LoginPanel loginPanel;
    private SignupPanel signupPanel;
    private MyPagePanel myPagePanel;

    public MainFrame() {
        setTitle("Campus Share - User Management");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loginPanel = new LoginPanel(this);
        signupPanel = new SignupPanel(this);
        myPagePanel = new MyPagePanel(this);

        mainContainer.add(loginPanel, "LOGIN");
        mainContainer.add(signupPanel, "SIGNUP");
        mainContainer.add(myPagePanel, "MYPAGE");

        add(mainContainer);
        showCard("LOGIN");
    }

    public void showCard(String name) {
        if (name.equals("MYPAGE")) {
            myPagePanel.updateUI(); 
        }
    
        cardLayout.show(mainContainer, name);
    
        mainContainer.revalidate(); 
        mainContainer.repaint();   
        mainContainer.requestFocusInWindow();
    }
}