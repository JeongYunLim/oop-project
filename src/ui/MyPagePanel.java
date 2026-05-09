package ui;

import domain.Item;
import domain.User;
import manager.ItemManager;
import manager.NavigationManager;
import manager.UserManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class MyPagePanel extends JPanel {

    private JLabel nameLabel = new JLabel("-");
    private JLabel tempLabel = new JLabel("-");
    private DefaultTableModel tableModel;

    public MyPagePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.add(new JLabel("--- 마이페이지 ---"));
        infoPanel.add(nameLabel);
        infoPanel.add(tempLabel);

        String[] cols = {"물품명", "카테고리", "가격(원/시간)", "상태"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable itemTable = new JTable(tableModel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton backBtn   = new JButton("뒤로가기");
        JButton logoutBtn = new JButton("로그아웃");

        backBtn.addActionListener(e ->
            NavigationManager.getInstance().showPanel("MAIN"));
        logoutBtn.addActionListener(e -> {
            UserManager.getInstance().logout();
            NavigationManager.getInstance().showPanel("MAIN");
        });
        buttonPanel.add(backBtn);
        buttonPanel.add(logoutBtn);

        add(infoPanel, BorderLayout.NORTH);
        add(new JScrollPane(itemTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refresh() {
        User user = UserManager.getInstance().getLoggedInUser();
        if (user != null) {
            nameLabel.setText("이름: " + user.getName());
            tempLabel.setText(String.format("매너온도: %.1f °C", user.getTemperature().getValue()));
            tableModel.setRowCount(0);
            ArrayList<Item> items = ItemManager.getInstance().getItemsByOwner(user.getId());
            for (Item item : items) {
                tableModel.addRow(new Object[]{
                    item.getName(), item.getCategory(),
                    item.getPricePerHour(), item.getStateName()
                });
            }
        } else {
            nameLabel.setText("로그인이 필요합니다.");
            tempLabel.setText("");
            tableModel.setRowCount(0);
        }
    }

    @Override
public void updateUI() {
    super.updateUI();
    if (nameLabel != null && tableModel != null) {
        refresh();
    }
}
}
