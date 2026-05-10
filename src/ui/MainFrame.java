package ui;


import domain.Item;
import manager.ItemManager;
import manager.NavigationManager;
import transaction.Rental;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

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
        setTitle("CampusShare");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        NavigationManager.getInstance().setMainFrame(this);

        mainPanel        = new MainPanel();
        loginPanel       = new LoginPanel();
        signupPanel      = new SignupPanel();
        myPagePanel      = new MyPagePanel();
        itemListPanel    = new ItemListPanel(ItemManager.getInstance());
        itemRegisterPanel = new ItemRegisterPanel(ItemManager.getInstance());
        itemDetailPanel  = new ItemDetailPanel();
        adminPanel       = new AdminPanel();
        inquiryPanel     = new InquiryPanel();

        itemListPanel.setDetailPanel(itemDetailPanel);
        myPagePanel.setDetailPanel(itemDetailPanel);

        mainContainer.add(mainPanel,         "MAIN");
        mainContainer.add(loginPanel,        "LOGIN");
        mainContainer.add(signupPanel,       "SIGNUP");
        mainContainer.add(myPagePanel,       "MYPAGE");
        mainContainer.add(itemListPanel,     "ITEM_LIST");
        mainContainer.add(itemRegisterPanel, "ITEM_REGISTER");
        mainContainer.add(itemDetailPanel,   "ITEM_DETAIL");
        mainContainer.add(adminPanel,        "ADMIN");
        mainContainer.add(inquiryPanel,      "INQUIRY");

        add(mainContainer);
        showCard("MAIN");
    }

    public void showCard(String name) {
        if ("MYPAGE".equals(name))     myPagePanel.refresh();
        if ("MAIN".equals(name))       mainPanel.refresh();
        if ("ITEM_LIST".equals(name))  itemListPanel.loadAllItems();

        cardLayout.show(mainContainer, name);
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    public void showTransactionPanel(Rental rental) {
        TransactionPanel tp = new TransactionPanel(rental);
        mainContainer.add(tp, "TRANSACTION");
        showCard("TRANSACTION");
    }

    public void showInquiryPanel(Item item) {
        inquiryPanel.loadItem(item);
        showCard("INQUIRY");
    }
}
