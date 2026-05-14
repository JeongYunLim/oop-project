 package ui;

import manager.ItemManager;
import manager.NavigationManager;
import manager.UserManager;
import domain.Item;
import domain.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ItemListPanel extends JPanel {

    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color MAIN_COLOR = new Color(52, 120, 246);
    private final Color MAIN_DARK_COLOR = new Color(35, 90, 200);
    private final Color SUB_COLOR = new Color(235, 242, 255);
    private final Color TEXT_COLOR = new Color(40, 40, 40);
    private final Color GRAY_TEXT = new Color(110, 110, 110);

    private final ItemManager itemManager;
    private ItemDetailPanel detailPanel;

    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> buildingCombo;
    private JCheckBox availableCheckBox;
    private JButton sortByPriceButton;
    private JButton sortByLatestButton;

    private JTable itemTable;
    private DefaultTableModel tableModel;
    private ArrayList<Item> currentItems = new ArrayList<>();

    private boolean isRestoringSearchState = false;
    private String lastDisplayedUserKey = null;

    private Map<String, SearchState> searchHistoryByUser = new HashMap<>();

    private static final DateTimeFormatter SLOT_DATE_FMT =
            DateTimeFormatter.ofPattern("MM/dd HH:mm");
    private static final DateTimeFormatter SLOT_TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm");

    private static class SearchState {
        String keyword = "";
        String category = "전체";
        String building = "전체";
        boolean availableOnly = false;
        String sortType = "NONE";
    }

    public ItemListPanel(ItemManager itemManager) {
        this.itemManager = itemManager;
        initComponents();
        prepareForDisplay();
    }

    public void setDetailPanel(ItemDetailPanel detailPanel) {
        this.detailPanel = detailPanel;
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 18));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel headerCard = createHeaderCard();

        JPanel listCard = new JPanel(new BorderLayout(12, 12));
        listCard.setBackground(CARD_COLOR);
        listCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        listCard.add(buildTopPanel(), BorderLayout.NORTH);
        listCard.add(buildTablePanel(), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setBackground(BACKGROUND_COLOR);

        JButton backBtn = createTextButton("메인으로");
        backBtn.addActionListener(e -> NavigationManager.getInstance().showPanel("MAIN"));
        bottomPanel.add(backBtn);

        add(headerCard, BorderLayout.NORTH);
        add(listCard, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(24, 30, 24, 30)
        ));

        JLabel titleLabel = new JLabel("물품 목록");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        titleLabel.setForeground(MAIN_COLOR);

        JLabel subTitleLabel = new JLabel("필요한 물품을 검색하고 대여 가능한 시간을 확인하세요.");
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        subTitleLabel.setForeground(GRAY_TEXT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(subTitleLabel);

        return card;
    }

    private JPanel buildTopPanel() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(CARD_COLOR);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchRow.setBackground(CARD_COLOR);

        searchField = createTextField(15);
        searchButton = createMainButton("검색");

        String[] categories = {"전체", "전자기기", "도서", "생활용품", "의류", "스포츠", "기타"};
        categoryCombo = createComboBox(categories);

        searchRow.add(createSmallLabel("검색"));
        searchRow.add(searchField);
        searchRow.add(searchButton);
        searchRow.add(Box.createHorizontalStrut(10));
        searchRow.add(createSmallLabel("카테고리"));
        searchRow.add(categoryCombo);

        JPanel filterRow = new JPanel(new BorderLayout());
        filterRow.setBackground(CARD_COLOR);

        JPanel filterBuilding = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterBuilding.setBackground(CARD_COLOR);

        String[] buildings = {"전체", "새빛관", "비마관", "한울관", "누리관", "옥의관", "기념관", "참빛관", "연구관"};
        buildingCombo = createComboBox(buildings);

        availableCheckBox = new JCheckBox("대여가능만");
        availableCheckBox.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        availableCheckBox.setForeground(TEXT_COLOR);
        availableCheckBox.setBackground(CARD_COLOR);
        availableCheckBox.setFocusPainted(false);

        filterBuilding.add(createSmallLabel("건물"));
        filterBuilding.add(buildingCombo);
        filterBuilding.add(availableCheckBox);

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        sortPanel.setBackground(CARD_COLOR);

        sortByPriceButton = createSubButton("가격순");
        sortByLatestButton = createSubButton("최신순");

        sortPanel.add(sortByPriceButton);
        sortPanel.add(sortByLatestButton);

        filterRow.add(filterBuilding, BorderLayout.WEST);
        filterRow.add(sortPanel, BorderLayout.EAST);

        top.add(searchRow);
        top.add(Box.createVerticalStrut(6));
        top.add(filterRow);

        searchButton.addActionListener(e -> {
            saveCurrentSearchState("NONE");
            applySearchAndFilters();
        });

        searchField.addActionListener(e -> {
            saveCurrentSearchState("NONE");
            applySearchAndFilters();
        });

        categoryCombo.addActionListener(e -> {
            if (!isRestoringSearchState) {
                saveCurrentSearchState("NONE");
                applySearchAndFilters();
            }
        });

        buildingCombo.addActionListener(e -> {
            if (!isRestoringSearchState) {
                saveCurrentSearchState("NONE");
                applySearchAndFilters();
            }
        });

        availableCheckBox.addActionListener(e -> {
            if (!isRestoringSearchState) {
                saveCurrentSearchState("NONE");
                applySearchAndFilters();
            }
        });

        sortByPriceButton.addActionListener(e -> {
            saveCurrentSearchState("PRICE");
            applySearchAndFilters();
        });

        sortByLatestButton.addActionListener(e -> {
            saveCurrentSearchState("LATEST");
            applySearchAndFilters();
        });

        return top;
    }

    private JScrollPane buildTablePanel() {
        String[] columns = {"이름", "카테고리", "가격(원/시간)", "대여 가능 시간"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        itemTable = new JTable(tableModel);
        itemTable.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        itemTable.setRowHeight(32);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.setSelectionBackground(new Color(220, 235, 255));
        itemTable.setSelectionForeground(TEXT_COLOR);
        itemTable.setGridColor(new Color(230, 230, 230));

        itemTable.getTableHeader().setReorderingAllowed(false);
        itemTable.getTableHeader().setFont(new Font("맑은 고딕", Font.BOLD, 13));
        itemTable.getTableHeader().setBackground(new Color(240, 244, 250));
        itemTable.getTableHeader().setForeground(TEXT_COLOR);

        itemTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onItemSelected();
            }
        });

        JScrollPane scrollPane = new JScrollPane(itemTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225), 1));
        return scrollPane;
    }

    public void prepareForDisplay() {
        String currentUserKey = getCurrentUserKey();

        if (lastDisplayedUserKey != null && !lastDisplayedUserKey.equals(currentUserKey)) {
            saveCurrentSearchStateForUser(lastDisplayedUserKey);
        }

        restoreSearchStateForUser(currentUserKey);
        lastDisplayedUserKey = currentUserKey;

        applySearchAndFilters();
    }

    public void loadAllItems() {
        prepareForDisplay();
    }

    private String getCurrentUserKey() {
        User user = UserManager.getInstance().getLoggedInUser();

        if (user == null) {
            return "GUEST";
        }

        return user.getId();
    }

    private void saveCurrentSearchState(String sortType) {
        if (isRestoringSearchState) {
            return;
        }

        String userKey = getCurrentUserKey();
        SearchState state = getSearchState(userKey);

        state.keyword = searchField.getText().trim();
        state.category = (String) categoryCombo.getSelectedItem();
        state.building = (String) buildingCombo.getSelectedItem();
        state.availableOnly = availableCheckBox.isSelected();
        state.sortType = sortType;
    }

    private void saveCurrentSearchStateForUser(String userKey) {
        if (searchField == null || categoryCombo == null || buildingCombo == null || availableCheckBox == null) {
            return;
        }

        SearchState state = getSearchState(userKey);

        state.keyword = searchField.getText().trim();
        state.category = (String) categoryCombo.getSelectedItem();
        state.building = (String) buildingCombo.getSelectedItem();
        state.availableOnly = availableCheckBox.isSelected();
    }

    private SearchState getSearchState(String userKey) {
        SearchState state = searchHistoryByUser.get(userKey);

        if (state == null) {
            state = new SearchState();
            searchHistoryByUser.put(userKey, state);
        }

        return state;
    }

    private void restoreSearchStateForUser(String userKey) {
        SearchState state = getSearchState(userKey);

        isRestoringSearchState = true;

        searchField.setText(state.keyword);
        categoryCombo.setSelectedItem(state.category);
        buildingCombo.setSelectedItem(state.building);
        availableCheckBox.setSelected(state.availableOnly);

        isRestoringSearchState = false;
    }

    private void applySearchAndFilters() {
        String userKey = getCurrentUserKey();
        SearchState state = getSearchState(userKey);

        ArrayList<Item> result = new ArrayList<>(itemManager.getItems());

        String keyword = searchField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String building = (String) buildingCombo.getSelectedItem();
        boolean availableOnly = availableCheckBox.isSelected();

        state.keyword = keyword;
        state.category = category;
        state.building = building;
        state.availableOnly = availableOnly;

        if (!keyword.isEmpty()) {
            result.removeIf(item -> !item.getName().contains(keyword));
        }

        if (!"전체".equals(category)) {
            result.removeIf(item -> !item.getCategory().equalsIgnoreCase(category));
        }

        if (!"전체".equals(building)) {
            result.removeIf(item ->
                    item.getLocation() == null
                            || item.getLocation().getBuilding() == null
                            || !item.getLocation().getBuilding().equalsIgnoreCase(building)
            );
        }

        if (availableOnly) {
            result.removeIf(item -> !item.isAvailable());
        }

        if ("PRICE".equals(state.sortType)) {
            result.sort(Comparator.comparingInt(Item::getPricePerHour));
        } else if ("LATEST".equals(state.sortType)) {
            result.sort(Comparator.comparing(Item::getRegisteredAt).reversed());
        }

        displayItems(result);
    }

    private void onItemSelected() {
        int row = itemTable.getSelectedRow();

        if (row < 0 || row >= currentItems.size()) {
            return;
        }

        Item selected = currentItems.get(row);

        if (detailPanel != null) {
            detailPanel.loadItem(selected);
        }

        NavigationManager.getInstance().showPanel("ITEM_DETAIL");
    }

    private void displayItems(ArrayList<Item> items) {
        currentItems = items;
        tableModel.setRowCount(0);

        for (Item item : items) {
            String timeRange = "-";

            if (item.getTimeSlot() != null) {
                timeRange = item.getTimeSlot().getStartTime().format(SLOT_DATE_FMT)
                        + " ~ "
                        + item.getTimeSlot().getEndTime().format(SLOT_TIME_FMT);
            }

            tableModel.addRow(new Object[]{
                    item.getName(),
                    item.getCategory(),
                    item.getPricePerHour(),
                    timeRange
            });
        }
    }

    private JLabel createSmallLabel(String text) {
        JLabel label = new JLabel(text + ":");
        label.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        field.setForeground(TEXT_COLOR);
        field.setPreferredSize(new Dimension(170, 34));
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
        comboBox.setPreferredSize(new Dimension(130, 34));
        return comboBox;
    }

    private JButton createMainButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(MAIN_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(90, 34));

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
        button.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        button.setForeground(MAIN_COLOR);
        button.setBackground(SUB_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(90, 34));
        return button;
    }

    private JButton createTextButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        button.setForeground(new Color(120, 120, 120));
        button.setBackground(new Color(235, 235, 235));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 36));
        return button;
    }
}