 package ui;

import domain.Item;
import domain.User;
import manager.NavigationManager;
import manager.UserManager;
import transaction.Rental;

import javax.swing.*;
import java.awt.*;

public class ItemDetailPanel extends JPanel {

    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color MAIN_COLOR = new Color(52, 120, 246);
    private final Color MAIN_DARK_COLOR = new Color(35, 90, 200);
    private final Color SUB_COLOR = new Color(235, 242, 255);
    private final Color TEXT_COLOR = new Color(40, 40, 40);
    private final Color GRAY_TEXT = new Color(110, 110, 110);

    private Item currentItem;

    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel categoryLabel;
    private JLabel priceLabel;
    private JLabel stateLabel;
    private JLabel ownerLabel;
    private JLabel locationLabel;
    private JTextArea descriptionArea;

    public ItemDetailPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel card = new JPanel(new BorderLayout(20, 22));
        card.setBackground(CARD_COLOR);
        card.setPreferredSize(new Dimension(620, 520));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(35, 45, 35, 45)
        ));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(CARD_COLOR);

        JLabel titleLabel = new JLabel("물품 상세 정보");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        titleLabel.setForeground(MAIN_COLOR);

        JLabel subTitleLabel = new JLabel("물품 정보를 확인하고 대여 요청 또는 문의를 진행할 수 있습니다.");
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        subTitleLabel.setForeground(GRAY_TEXT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subTitleLabel);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(CARD_COLOR);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 235, 235), 1),
                BorderFactory.createEmptyBorder(22, 28, 22, 28)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.anchor = GridBagConstraints.WEST;

        idLabel = createValueLabel();
        nameLabel = createValueLabel();
        categoryLabel = createValueLabel();
        priceLabel = createValueLabel();
        stateLabel = createValueLabel();
        ownerLabel = createValueLabel();
        locationLabel = createValueLabel();

        descriptionArea = new JTextArea(3, 22);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        descriptionArea.setForeground(TEXT_COLOR);
        descriptionArea.setBackground(new Color(250, 250, 250));
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(300, 75));
        descriptionScroll.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 225), 1));

        addRow(infoPanel, gbc, 0, "ID", idLabel);
        addRow(infoPanel, gbc, 1, "물품명", nameLabel);
        addRow(infoPanel, gbc, 2, "카테고리", categoryLabel);
        addRow(infoPanel, gbc, 3, "가격(원/시간)", priceLabel);
        addRow(infoPanel, gbc, 4, "상태", stateLabel);
        addRow(infoPanel, gbc, 5, "등록자", ownerLabel);
        addRow(infoPanel, gbc, 6, "위치", locationLabel);
        addRow(infoPanel, gbc, 7, "설명", descriptionScroll);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(CARD_COLOR);

        JButton rentalBtn = createMainButton("대여 요청");
        JButton inquiryBtn = createSubButton("문의하기");
        JButton backBtn = createTextButton("뒤로가기");

        rentalBtn.addActionListener(e -> onRentalRequest());
        inquiryBtn.addActionListener(e -> onInquiry());
        backBtn.addActionListener(e ->
                NavigationManager.getInstance().showPanel("ITEM_LIST")
        );

        buttonPanel.add(rentalBtn);
        buttonPanel.add(inquiryBtn);
        buttonPanel.add(backBtn);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        add(card);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row,
                        String label, JComponent field) {
        JLabel labelComponent = new JLabel(label + ":");
        labelComponent.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        labelComponent.setForeground(TEXT_COLOR);
        labelComponent.setHorizontalAlignment(SwingConstants.RIGHT);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("-");
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    public void loadItem(Item item) {
        this.currentItem = item;

        if (item == null) {
            clearFields();
            return;
        }

        idLabel.setText(String.valueOf(item.getItemId()));
        nameLabel.setText(item.getName());
        categoryLabel.setText(item.getCategory());
        priceLabel.setText(item.getPricePerHour() + " 원");
        stateLabel.setText(item.getStateName());
        ownerLabel.setText(item.getOwnerId());
        locationLabel.setText(item.getLocation() != null ? item.getLocation().toString() : "-");
        descriptionArea.setText(item.getDescription());
    }

    private void clearFields() {
        idLabel.setText("-");
        nameLabel.setText("-");
        categoryLabel.setText("-");
        priceLabel.setText("-");
        stateLabel.setText("-");
        ownerLabel.setText("-");
        locationLabel.setText("-");
        descriptionArea.setText("");
    }

    private void onRentalRequest() {
        User loggedIn = UserManager.getInstance().getLoggedInUser();

        if (loggedIn == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.");
            return;
        }

        if (currentItem == null) {
            JOptionPane.showMessageDialog(this, "선택된 물품이 없습니다.");
            return;
        }

        if (loggedIn.getId().equals(currentItem.getOwnerId())) {
            JOptionPane.showMessageDialog(this, "본인 물품은 대여할 수 없습니다.");
            return;
        }

        if (!currentItem.isAvailable()) {
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

        Window window = SwingUtilities.getWindowAncestor(this);

        if (window instanceof MainFrame) {
            ((MainFrame) window).showTransactionPanel(rental);
        }
    }

    private void onInquiry() {
        if (currentItem == null) {
            JOptionPane.showMessageDialog(this, "선택된 물품이 없습니다.");
            return;
        }

        Window window = SwingUtilities.getWindowAncestor(this);

        if (window instanceof MainFrame) {
            ((MainFrame) window).showInquiryPanel(currentItem);
        }
    }

    private JButton createMainButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(MAIN_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 40));

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
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setForeground(MAIN_COLOR);
        button.setBackground(SUB_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 40));
        return button;
    }

    private JButton createTextButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setForeground(new Color(120, 120, 120));
        button.setBackground(new Color(235, 235, 235));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 40));
        return button;
    }
}