 package ui;

import domain.Inquiry;
import domain.Item;
import domain.User;
import manager.InquiryManager;
import manager.NavigationManager;
import manager.UserManager;

import javax.swing.*;
import java.awt.*;

public class InquiryPanel extends JPanel {

    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color MAIN_COLOR = new Color(52, 120, 246);
    private final Color MAIN_DARK_COLOR = new Color(35, 90, 200);
    private final Color SUB_COLOR = new Color(235, 242, 255);
    private final Color TEXT_COLOR = new Color(40, 40, 40);
    private final Color GRAY_TEXT = new Color(110, 110, 110);

    private Item currentItem;
    private JLabel itemLabel = new JLabel("-");
    private JTextArea messageArea = new JTextArea(6, 28);

    public InquiryPanel() {
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel card = new JPanel(new BorderLayout(20, 22));
        card.setBackground(CARD_COLOR);
        card.setPreferredSize(new Dimension(560, 430));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(35, 42, 35, 42)
        ));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(CARD_COLOR);

        JLabel titleLabel = new JLabel("문의하기");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        titleLabel.setForeground(MAIN_COLOR);

        JLabel subTitleLabel = new JLabel("물품 소유자에게 궁금한 내용을 남겨보세요.");
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        subTitleLabel.setForeground(GRAY_TEXT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subTitleLabel);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);

        JPanel itemPanel = new JPanel(new BorderLayout(8, 0));
        itemPanel.setBackground(new Color(250, 250, 250));
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        itemPanel.setMaximumSize(new Dimension(480, 46));

        JLabel itemTitleLabel = new JLabel("문의 물품");
        itemTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        itemTitleLabel.setForeground(GRAY_TEXT);

        itemLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        itemLabel.setForeground(TEXT_COLOR);

        itemPanel.add(itemTitleLabel, BorderLayout.WEST);
        itemPanel.add(itemLabel, BorderLayout.CENTER);

        JLabel messageLabel = new JLabel("문의 내용");
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        messageLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        messageLabel.setForeground(TEXT_COLOR);

        messageArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        messageArea.setForeground(TEXT_COLOR);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBackground(new Color(250, 250, 250));
        messageArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 225), 1));
        scrollPane.setMaximumSize(new Dimension(480, 150));

        contentPanel.add(itemPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(scrollPane);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(CARD_COLOR);

        JButton sendBtn = createMainButton("전송");
        JButton cancelBtn = createSubButton("뒤로가기");

        sendBtn.addActionListener(e -> {
            User sender = UserManager.getInstance().getLoggedInUser();

            if (sender == null) {
                JOptionPane.showMessageDialog(this, "로그인이 필요합니다.");
                return;
            }

            if (currentItem == null) {
                JOptionPane.showMessageDialog(this, "문의할 물품이 선택되지 않았습니다.");
                return;
            }

            String message = messageArea.getText().trim();

            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "문의 내용을 입력하세요.");
                return;
            }

            String toUserId = currentItem.getOwnerId();
            int itemId = currentItem.getItemId();

            Inquiry inquiry = new Inquiry(sender.getId(), toUserId, itemId, message);
            InquiryManager.getInstance().addInquiry(inquiry);

            JOptionPane.showMessageDialog(this, "문의가 전송되었습니다.");

            messageArea.setText("");
            NavigationManager.getInstance().showPanel("ITEM_LIST");
        });

        cancelBtn.addActionListener(e ->
                NavigationManager.getInstance().showPanel("ITEM_DETAIL")
        );

        buttonPanel.add(sendBtn);
        buttonPanel.add(cancelBtn);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        add(card);
    }

    public void loadItem(Item item) {
        this.currentItem = item;
        itemLabel.setText(item != null ? item.getName() : "-");
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
}