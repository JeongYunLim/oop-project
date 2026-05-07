package ui;

import domain.Item;
import domain.Location;
import domain.TimeSlot;
import manager.ItemManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 물품 등록 화면 — 폼 입력 후 ItemManager에 Item 추가
 * ItemListPanel과 동일하게 ItemManager를 생성자 주입으로 받음
 */
public class ItemRegisterPanel extends JPanel {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String[] CATEGORIES =
            {"전자기기", "도서", "생활용품", "의류", "스포츠", "기타"};

    private final ItemManager itemManager;

    // ── 폼 필드 ───────────────────────────────────────
    private JTextField    nameField;
    private JComboBox<String> categoryCombo;
    private JTextArea     descriptionArea;
    private JTextField    priceField;
    private JTextField    buildingField;
    private JTextField    detailField;
    private JTextField    startTimeField;
    private JTextField    endTimeField;


    // ════════════════════════════════════════════════
    //  생성자
    // ════════════════════════════════════════════════

    public ItemRegisterPanel(ItemManager itemManager) {
        this.itemManager = itemManager;
        initComponents();
    }


    // ════════════════════════════════════════════════
    //  UI 초기화
    // ════════════════════════════════════════════════

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
        startTimeField  = new JTextField("yyyy-MM-dd HH:mm", 16);
        endTimeField    = new JTextField("yyyy-MM-dd HH:mm", 16);

        addRow(form, gbc, 0, "물품명 *",                      nameField);
        addRow(form, gbc, 1, "카테고리 *",                     categoryCombo);
        addRow(form, gbc, 2, "설명",                           new JScrollPane(descriptionArea));
        addRow(form, gbc, 3, "시간당 가격(원) *",              priceField);
        addRow(form, gbc, 4, "건물명 *",                       buildingField);
        addRow(form, gbc, 5, "세부 위치",                      detailField);
        addRow(form, gbc, 6, "대여 시작 (yyyy-MM-dd HH:mm) *", startTimeField);
        addRow(form, gbc, 7, "대여 종료 (yyyy-MM-dd HH:mm) *", endTimeField);

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
        JButton cancelButton   = new JButton("취소");

        registerButton.addActionListener(e -> onRegister());
        cancelButton.addActionListener(e -> onCancel());

        panel.add(registerButton);
        panel.add(cancelButton);
        return panel;
    }


    // ════════════════════════════════════════════════
    //  이벤트 핸들러
    // ════════════════════════════════════════════════

    private void onRegister() {
        // 1. 물품명
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("물품명을 입력하세요.");
            return;
        }

        // 2. 가격
        int price;
        try {
            price = Integer.parseInt(priceField.getText().trim());
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("시간당 가격은 1 이상의 숫자로 입력하세요.");
            return;
        }

        // 3. 건물명
        String building = buildingField.getText().trim();
        if (building.isEmpty()) {
            showError("건물명을 입력하세요.");
            return;
        }

        // 4. 시각 파싱
        LocalDateTime startTime, endTime;
        try {
            startTime = LocalDateTime.parse(startTimeField.getText().trim(), FMT);
            endTime   = LocalDateTime.parse(endTimeField.getText().trim(), FMT);
        } catch (DateTimeParseException e) {
            showError("시각 형식이 올바르지 않습니다.\n예: 2025-05-01 09:00");
            return;
        }

        // 5. TimeSlot 생성 (종료 < 시작이면 예외)
        TimeSlot timeSlot;
        try {
            timeSlot = new TimeSlot(startTime, endTime);
        } catch (IllegalArgumentException e) {
            showError("종료 시각은 시작 시각보다 늦어야 합니다.");
            return;
        }

        // 6. Item 생성 및 등록
        String category    = (String) categoryCombo.getSelectedItem();
        String detail      = detailField.getText().trim();
        String description = descriptionArea.getText().trim();
        Location location  = new Location(building, detail);

        // TODO: 1번 팀원 로그인 연동 후 실제 userId로 교체
        String ownerId = "guest";

        Item item = new Item(name, category, price, location, timeSlot, ownerId);
        item.setDescription(description);
        itemManager.addItem(item);

        JOptionPane.showMessageDialog(this, "물품이 등록되었습니다.");
        clearForm();

        // TODO: NavigationManager 연동 후 화면 전환 처리
        // NavigationManager.getInstance().showPanel("list");
    }

    private void onCancel() {
        // TODO: NavigationManager 연동 후 화면 전환 처리
        // NavigationManager.getInstance().showPanel("list");
    }


    // ════════════════════════════════════════════════
    //  유틸
    // ════════════════════════════════════════════════

    private void clearForm() {
        nameField.setText("");
        categoryCombo.setSelectedIndex(0);
        descriptionArea.setText("");
        priceField.setText("");
        buildingField.setText("");
        detailField.setText("");
        startTimeField.setText("yyyy-MM-dd HH:mm");
        endTimeField.setText("yyyy-MM-dd HH:mm");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "입력 오류", JOptionPane.WARNING_MESSAGE);
    }
}
