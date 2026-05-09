package ui;

import domain.Item;
import domain.User;
import domain.Wishlist;
import manager.NavigationManager;
import manager.UserManager;
import transaction.Rental;
import javax.swing.*;
import java.awt.*;

public class ItemDetailPanel extends JPanel {

    private Item currentItem;

    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel categoryLabel;
    private JLabel priceLabel;
    private JLabel stateLabel;
    private JLabel ownerLabel;
    private JLabel locationLabel;
    private JTextArea descriptionArea;
    private JButton wishBtn;

    public ItemDetailPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("물품 상세 정보"));

        JPanel grid = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;

        idLabel         = new JLabel("-");
        nameLabel       = new JLabel("-");
        categoryLabel   = new JLabel("-");
        priceLabel      = new JLabel("-");
        stateLabel      = new JLabel("-");
        ownerLabel      = new JLabel("-");
        locationLabel   = new JLabel("-");
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);

        addRow(grid, gbc, 0, "ID",           idLabel);
        addRow(grid, gbc, 1, "물품명",        nameLabel);
        addRow(grid, gbc, 2, "카테고리",      categoryLabel);
        addRow(grid, gbc, 3, "가격(원/시간)", priceLabel);
        addRow(grid, gbc, 4, "상태",          stateLabel);
        addRow(grid, gbc, 5, "등록자",        ownerLabel);
        addRow(grid, gbc, 6, "위치",          locationLabel);
        addRow(grid, gbc, 7, "설명",          new JScrollPane(descriptionArea));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton rentalBtn  = new JButton("대여 요청");
        wishBtn            = new JButton("찜하기");
        JButton inquiryBtn = new JButton("문의하기");
        JButton backBtn    = new JButton("목록으로");

        rentalBtn.addActionListener(e -> onRentalRequest());
        wishBtn.addActionListener(e -> onToggleWish());
        inquiryBtn.addActionListener(e -> onInquiry());
        backBtn.addActionListener(e ->
            NavigationManager.getInstance().showPanel("ITEM_LIST"));

        buttonPanel.add(rentalBtn);
        buttonPanel.add(wishBtn);
        buttonPanel.add(inquiryBtn);
        buttonPanel.add(backBtn);

        add(grid, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row,
                        String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label + ":"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    public void loadItem(Item item) {
        this.currentItem = item;
        if (item == null) { clearFields(); return; }
        idLabel.setText(String.valueOf(item.getItemId()));
        nameLabel.setText(item.getName());
        categoryLabel.setText(item.getCategory());
        priceLabel.setText(item.getPricePerHour() + " 원");
        stateLabel.setText(item.getStateName());
        ownerLabel.setText(item.getOwnerId());
        locationLabel.setText(item.getLocation() != null ? item.getLocation().toString() : "-");
        descriptionArea.setText(item.getDescription());
        updateWishButton();
    }

    private void clearFields() {
        idLabel.setText("-"); nameLabel.setText("-"); categoryLabel.setText("-");
        priceLabel.setText("-"); stateLabel.setText("-"); ownerLabel.setText("-");
        locationLabel.setText("-"); descriptionArea.setText("");
        wishBtn.setText("찜하기");
    }

    private void onRentalRequest() {
        User loggedIn = UserManager.getInstance().getLoggedInUser();
        if (loggedIn == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.");
            return;
        }
        if (currentItem == null || !currentItem.isAvailable()) {
            JOptionPane.showMessageDialog(this, "대여 불가능한 물품입니다.");
            return;
        }
        User owner = UserManager.getInstance().getUserById(currentItem.getOwnerId());
        if (owner == null) {
            JOptionPane.showMessageDialog(this, "소유자 정보를 찾을 수 없습니다.");
            return;
        }
        Rental rental = new Rental(owner, loggedIn, currentItem);
        rental.request();
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof MainFrame) {
            ((MainFrame) w).showTransactionPanel(rental);
        }
    }

    private void onToggleWish() {
        User loggedIn = UserManager.getInstance().getLoggedInUser();
        if (loggedIn == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.");
            return;
        }
        if (currentItem == null) return;
        Wishlist.getInstance().toggle(loggedIn, currentItem);
        updateWishButton();
    }

    private void updateWishButton() {
        User loggedIn = UserManager.getInstance().getLoggedInUser();
        if (loggedIn != null && currentItem != null) {
            boolean wished = Wishlist.getInstance().isWished(loggedIn, currentItem);
            wishBtn.setText(wished ? "찜 해제" : "찜하기");
        } else {
            wishBtn.setText("찜하기");
        }
    }

    private void onInquiry() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof MainFrame) {
            ((MainFrame) w).showInquiryPanel(currentItem);
        }
    }
}
