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

    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color MAIN_COLOR = new Color(52, 120, 246);
    private final Color MAIN_DARK_COLOR = new Color(35, 90, 200);
    private final Color SUB_COLOR = new Color(235, 242, 255);
    private final Color TEXT_COLOR = new Color(40, 40, 40);
    private final Color GRAY_TEXT = new Color(110, 110, 110);

    private static final String[] CATEGORIES =
            {"전자기기", "도서", "생활용품", "의류", "스포츠", "기타"};

    private final ItemManager itemManager;

    private JTextField nameField;
    private JComboBox<String> categoryCombo;
    private JTextArea descriptionArea;
    private JTextField priceField;
    private JTextField buildingField;
    private JTextField detailField;
    private JComboBox<String> startHourCombo;
    private JComboBox<String> endHourCombo;

    public ItemRegisterPanel(ItemManager itemManager) {
        this.itemManager = itemManager;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(20, 22));
        card.setBackground(CARD_COLOR);
        card.setPreferredSize(new Dimension(620, 570));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(32, 42, 32, 42)
        ));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(CARD_COLOR);

        JLabel titleLabel = new JLabel("물품 등록");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        titleLabel.setForeground(MAIN_COLOR);

        JLabel subTitleLabel = new JLabel("사용하지 않는 물품을 등록하고 필요한 학생들과 공유해보세요.");
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        subTitleLabel.setForeground(GRAY_TEXT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subTitleLabel);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(buildFormPanel(), BorderLayout.CENTER);
        card.add(buildButtonPanel(), BorderLayout.SOUTH);

        add(card);
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        nameField = createTextField(22);
        categoryCombo = createComboBox(CATEGORIES);

        descriptionArea = new JTextArea(3, 22);
        descriptionArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        descriptionArea.setBackground(new Color(250, 250, 250));

        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(280, 75));
        descriptionScroll.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 225), 1));

        priceField = createTextField(22);
        buildingField = createTextField(22);
        detailField = createTextField(22);

        String[] hours = IntStream.range(0, 24)
                .mapToObj(h -> String.format("%02d:00", h))
                .toArray(String[]::new);

        startHourCombo = createComboBox(hours);
        endHourCombo = createComboBox(hours);
        startHourCombo.setSelectedIndex(9);
        endHourCombo.setSelectedIndex(11);

        addRow(form, gbc, 0, "물품명 *", nameField);
        addRow(form, gbc, 1, "카테고리 *", categoryCombo);
        addRow(form, gbc, 2, "설명", descriptionScroll);
        addRow(form, gbc, 3, "시간당 가격(원) *", priceField);
        addRow(form, gbc, 4, "건물명 *", buildingField);
        addRow(form, gbc, 5, "세부 위치", detailField);
        addRow(form, gbc, 6, "대여 시작 시간 *", startHourCombo);
        addRow(form, gbc, 7, "대여 종료 시간 *", endHourCombo);

        return form;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row,
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
        form.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(field, gbc);
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setBackground(CARD_COLOR);

        JButton registerButton = createMainButton("등록");
        JButton cancelButton = createSubButton("뒤로가기");

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

            if (price <= 0) {
                throw new NumberFormatException();
            }
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

        LocalDateTime startTime = LocalDateTime.of(
                today,
                LocalTime.parse((String) startHourCombo.getSelectedItem())
        );

        LocalDateTime endTime = LocalDateTime.of(
                today,
                LocalTime.parse((String) endHourCombo.getSelectedItem())
        );

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

        String category = (String) categoryCombo.getSelectedItem();
        String detail = detailField.getText().trim();
        String description = descriptionArea.getText().trim();
        Location location = new Location(building, detail);

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

    private JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        field.setForeground(TEXT_COLOR);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setPreferredSize(new Dimension(280, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(280, 36));
        return comboBox;
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