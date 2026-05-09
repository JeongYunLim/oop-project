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

    private Item currentItem;
    private JLabel itemLabel = new JLabel("-");
    private JTextArea messageArea = new JTextArea(5, 30);

    public InquiryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("문의 물품: "));
        topPanel.add(itemLabel);

        messageArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("문의 내용"));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton sendBtn   = new JButton("전송");
        JButton cancelBtn = new JButton("취소");

        sendBtn.addActionListener(e -> {
            User sender = UserManager.getInstance().getLoggedInUser();
            if (sender == null) {
                JOptionPane.showMessageDialog(this, "로그인이 필요합니다.");
                return;
            }
            String message = messageArea.getText().trim();
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "문의 내용을 입력하세요.");
                return;
            }
            String toUserId = currentItem != null ? currentItem.getOwnerId() : "";
            int itemId      = currentItem != null ? currentItem.getItemId() : -1;
            Inquiry inquiry = new Inquiry(sender.getId(), toUserId, itemId, message);
            InquiryManager.getInstance().addInquiry(inquiry);
            JOptionPane.showMessageDialog(this, "문의가 전송되었습니다.");
            messageArea.setText("");
            NavigationManager.getInstance().showPanel("ITEM_LIST");
        });

        cancelBtn.addActionListener(e ->
            NavigationManager.getInstance().showPanel("ITEM_DETAIL"));

        buttonPanel.add(sendBtn);
        buttonPanel.add(cancelBtn);

        add(topPanel,   BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadItem(Item item) {
        this.currentItem = item;
        itemLabel.setText(item != null ? item.getName() : "-");
    }
}
