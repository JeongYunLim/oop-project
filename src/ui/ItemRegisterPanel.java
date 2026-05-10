package ui;

import domain.Item;
import domain.Location;
import domain.TimeSlot;
import domain.User;
import manager.ItemManager;
import manager.NavigationManager;
import manager.UserManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.IntStream;

public class ItemRegisterPanel extends JPanel {

    private static final String[] CATEGORIES =
            {"전자기기", "도서", "생활용품", "의류", "스포츠", "기타"};

    private final ItemManager itemManager;

    private JTextField    nameField;
    private JComboBox<String> categoryCombo;
    private JTextArea     descriptionArea;
    private JTextField    priceField;
    private JTextField    buildingField;
    private JTextField    detailField;
    private JComboBox<String> startHourCombo;
    private JComboBox<String> endHourCombo;

    public ItemRegisterPanel(ItemManager itemManager) {
        this.itemManager = itemManager;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildFormPanel(),   BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        nameField       = new JTextField(20);
        categoryCombo   = new JComboBox<>(CATEGORIES);
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        priceField      = new JTextField(10);
        buildingField   = new JTextField(10);
        detailField     = new JTextField(10);
        String[] hours = IntStream.range(0, 24)
                .mapToObj(h -> String.format("%02d:00", h))
                .toArray(String[]::new);
        startHourCombo = new JComboBox<>(hours);
        endHourCombo   = new JComboBox<>(hours);
        startHourCombo.setSelectedIndex(9);
        endHourCombo.setSelectedIndex(11);

        addRow(form, gbc, 0, "물품명 *",        nameField);
        addRow(form, gbc, 1, "카테고리 *",      categoryCombo);
        addRow(form, gbc, 2, "설명",             new JScrollPane(descriptionArea));
        addRow(form, gbc, 3, "시간당 가격(원) *", priceField);
        addRow(form, gbc, 4, "건물명 *",         buildingField);
        addRow(form, gbc, 5, "세부 위치",         detailField);
        addRow(form, gbc, 6, "대여 시작 시간 *",  startHourCombo);
        addRow(form, gbc, 7, "대여 종료 시간 *",  endHourCombo);

        return form;
    }

    private void addRow(JPanel form, GridBagConstraints gbc,
                        int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        form.add(new JLabel(label + ":"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(field, gbc);
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton registerButton = new JButton("등록");
        JButton cancelButton   = new JButton("뒤로가기");

        registerButton.addActionListener(e -> onRegister());
        cancelButton.addActionListener(e -> onCancel());

        panel.add(registerButton);
        panel.add(cancelButton);
        return panel;
    }

    private void onRegister() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("물품명을 입력하세요.");
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceField.getText().trim());
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("시간당 가격은 1 이상의 숫자로 입력하세요.");
            return;
        }

        String building = buildingField.getText().trim();
        if (building.isEmpty()) {
            showError("건물명을 입력하세요.");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDateTime startTime = LocalDateTime.of(today,
                LocalTime.parse((String) startHourCombo.getSelectedItem()));
        LocalDateTime endTime   = LocalDateTime.of(today,
                LocalTime.parse((String) endHourCombo.getSelectedItem()));

        TimeSlot timeSlot;
        try {
            timeSlot = new TimeSlot(startTime, endTime);
        } catch (IllegalArgumentException e) {
            showError("종료 시각은 시작 시각보다 늦어야 합니다.");
            return;
        }

        User loggedIn = UserManager.getInstance().getLoggedInUser();
        if (loggedIn == null) {
            showError("로그인이 필요합니다.");
            return;
        }

        String category    = (String) categoryCombo.getSelectedItem();
        String detail      = detailField.getText().trim();
        String description = descriptionArea.getText().trim();
        Location location  = new Location(building, detail);

        Item item = new Item(name, category, price, location, timeSlot, loggedIn);
        item.setDescription(description);
        itemManager.addItem(item);

        JOptionPane.showMessageDialog(this, "물품이 등록되었습니다.");
        clearForm();
        NavigationManager.getInstance().showPanel("ITEM_LIST");
    }

    private void onCancel() {
        NavigationManager.getInstance().showPanel("ITEM_LIST");
    }

    private void clearForm() {
        nameField.setText("");
        categoryCombo.setSelectedIndex(0);
        descriptionArea.setText("");
        priceField.setText("");
        buildingField.setText("");
        detailField.setText("");
        startHourCombo.setSelectedIndex(9);
        endHourCombo.setSelectedIndex(11);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "입력 오류", JOptionPane.WARNING_MESSAGE);
    }
}
