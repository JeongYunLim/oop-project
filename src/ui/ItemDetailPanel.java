package ui;

import domain.Item;

import javax.swing.*;
import java.awt.*;

/**
 * 물품 상세 정보 화면 — ItemListPanel에서 행 선택 시 loadItem()으로 내용 갱신
 */
public class ItemDetailPanel extends JPanel {

    private JLabel  idLabel;
    private JLabel  nameLabel;
    private JLabel  categoryLabel;
    private JLabel  priceLabel;
    private JLabel  stateLabel;
    private JLabel  ownerLabel;
    private JLabel  locationLabel;
    private JTextArea descriptionArea;


    public ItemDetailPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("물품 상세 정보"));

        JPanel grid = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(4, 8, 4, 8);
        gbc.anchor  = GridBagConstraints.WEST;

        idLabel          = new JLabel("-");
        nameLabel        = new JLabel("-");
        categoryLabel    = new JLabel("-");
        priceLabel       = new JLabel("-");
        stateLabel       = new JLabel("-");
        ownerLabel       = new JLabel("-");
        locationLabel    = new JLabel("-");
        descriptionArea  = new JTextArea(3, 20);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);

        addRow(grid, gbc, 0, "ID",          idLabel);
        addRow(grid, gbc, 1, "물품명",       nameLabel);
        addRow(grid, gbc, 2, "카테고리",     categoryLabel);
        addRow(grid, gbc, 3, "가격(원/시간)", priceLabel);
        addRow(grid, gbc, 4, "상태",         stateLabel);
        addRow(grid, gbc, 5, "등록자",       ownerLabel);
        addRow(grid, gbc, 6, "위치",         locationLabel);
        addRow(grid, gbc, 7, "설명",         new JScrollPane(descriptionArea));

        add(grid, BorderLayout.CENTER);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row,
                        String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label + ":"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    /** ItemListPanel에서 행 선택 시 호출 — 화면에 선택된 물품 정보를 표시 */
    public void loadItem(Item item) {
        if (item == null) {
            clearFields();
            return;
        }
        idLabel.setText(String.valueOf(item.getItemId()));
        nameLabel.setText(item.getName());
        categoryLabel.setText(item.getCategory());
        priceLabel.setText(item.getPricePerHour() + " 원");
        stateLabel.setText(item.getState());
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
}