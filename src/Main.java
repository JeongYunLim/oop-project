import domain.Item;
import domain.User;
import transaction.Rental;
import ui.TransactionPanel;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        User owner = new User("김소유", "20240001");
        User borrower = new User("이대여", "20240002");

        Item item = new Item("맥북", "노트북", "새빛관", 3000);

        Rental rental = new Rental(owner, borrower, item);
        rental.request();

        JFrame frame = new JFrame("CampusShare 거래 테스트");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 550);
        frame.setLocationRelativeTo(null);
        frame.add(new TransactionPanel(rental));
        frame.setVisible(true);
    }
}