 package ui;

import domain.Item;
import manager.ItemManager;
import manager.NavigationManager;
import transaction.Rental;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);

    private MainPanel mainPanel;
    private LoginPanel loginPanel;
    private SignupPanel signupPanel;
    private MyPagePanel myPagePanel;
    private ItemListPanel itemListPanel;
    private ItemRegisterPanel itemRegisterPanel;
    private ItemDetailPanel itemDetailPanel;
    private AdminPanel adminPanel;
    private InquiryPanel inquiryPanel;

    public MainFrame() {
        setTitle("CampusShare - 광운대학교 유휴자원 공유 플랫폼");
        setSize(1000, 700);
        setMinimumSize(new Dimension(900, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BACKGROUND_COLOR);

        mainContainer.setBackground(BACKGROUND_COLOR);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        NavigationManager.getInstance().setMainFrame(this);

        mainPanel = new MainPanel();
        loginPanel = new LoginPanel();
        signupPanel = new SignupPanel();
        myPagePanel = new MyPagePanel();
        itemListPanel = new ItemListPanel(ItemManager.getInstance());
        itemRegisterPanel = new ItemRegisterPanel(ItemManager.getInstance());
        itemDetailPanel = new ItemDetailPanel();
        adminPanel = new AdminPanel();
        inquiryPanel = new InquiryPanel();

        itemListPanel.setDetailPanel(itemDetailPanel);
        myPagePanel.setDetailPanel(itemDetailPanel);

        mainContainer.add(mainPanel, "MAIN");
        mainContainer.add(loginPanel, "LOGIN");
        mainContainer.add(signupPanel, "SIGNUP");
        mainContainer.add(myPagePanel, "MYPAGE");
        mainContainer.add(itemListPanel, "ITEM_LIST");
        mainContainer.add(itemRegisterPanel, "ITEM_REGISTER");
        mainContainer.add(itemDetailPanel, "ITEM_DETAIL");
        mainContainer.add(adminPanel, "ADMIN");
        mainContainer.add(inquiryPanel, "INQUIRY");

        add(mainContainer, BorderLayout.CENTER);

        showCard("LOGIN");
        setVisible(true);
    }

    public void showCard(String name) {
        if ("LOGIN".equals(name)) {
            loginPanel.clearFields();
        }

        if ("MYPAGE".equals(name)) {
            myPagePanel.refresh();
        }

        if ("MAIN".equals(name)) {
            mainPanel.refresh();
        }

        if ("ITEM_LIST".equals(name)) {
            itemListPanel.prepareForDisplay();
        }

        cardLayout.show(mainContainer, name);
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    public void showTransactionPanel(Rental rental) {
        TransactionPanel transactionPanel = new TransactionPanel(rental);

        mainContainer.add(transactionPanel, "TRANSACTION");
        showCard("TRANSACTION");
    }

    public void showInquiryPanel(Item item) {
        inquiryPanel.loadItem(item);
        showCard("INQUIRY");
    }
}