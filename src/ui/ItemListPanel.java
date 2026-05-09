package ui;

import manager.ItemManager;
import manager.NavigationManager;
import domain.Item;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * 물품 목록 화면 — 검색/필터/정렬 후 JTable로 표시
 * ItemManager를 생성자로 주입받아 사용
 */
public class ItemListPanel extends JPanel {

    private final ItemManager itemManager;

    // 상세 패널 참조 — 항목 선택 시 loadItem() 호출
    private ItemDetailPanel detailPanel;

    // ── 상단 컨트롤 ──────────────────────────────────
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> buildingCombo;
    private JCheckBox availableCheckBox;
    private JButton sortByPriceButton;
    private JButton sortByLatestButton;

    // ── 중앙 목록 ─────────────────────────────────────
    private JTable itemTable;
    private DefaultTableModel tableModel;

    // 현재 테이블에 표시된 Item 목록 (행 인덱스 → Item 매핑용)
    private ArrayList<Item> currentItems = new ArrayList<>();


    // ════════════════════════════════════════════════
    //  생성자
    // ════════════════════════════════════════════════

    public ItemListPanel(ItemManager itemManager) {
        this.itemManager = itemManager;
        initComponents();
        loadAllItems(); // 초기 전체 목록 표시
    }


    // ════════════════════════════════════════════════
    //  외부 패널 연결
    // ════════════════════════════════════════════════

    /** 상세 패널 주입 — 항목 선택 이벤트 연결 시 호출 */
    public void setDetailPanel(ItemDetailPanel detailPanel) {
        this.detailPanel = detailPanel;
    }


    // ════════════════════════════════════════════════
    //  UI 초기화
    // ════════════════════════════════════════════════

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    /** 상단: 1행(검색+카테고리) + 2행(건물+대여가능+정렬) */
    private JPanel buildTopPanel() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        // 1행: 검색 + 카테고리
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchField = new JTextField(15);
        searchButton = new JButton("검색");
        String[] categories = {"전체", "전자기기", "도서", "생활용품", "의류", "스포츠"};
        categoryCombo = new JComboBox<>(categories);
        searchRow.add(new JLabel("검색:"));
        searchRow.add(searchField);
        searchRow.add(searchButton);
        searchRow.add(new JLabel("카테고리:"));
        searchRow.add(categoryCombo);

        // 2행: 건물+대여가능(왼쪽) / 정렬버튼(오른쪽)
        JPanel filterRow = new JPanel(new BorderLayout());
        JPanel filterBuilding = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        String[] buildings = {"전체", "새빛관", "비마관", "한울관", "누리관", "옥의관", "기념관", "참빛관", "연구관"};
        buildingCombo = new JComboBox<>(buildings);
        availableCheckBox = new JCheckBox("대여가능만");
        filterBuilding.add(new JLabel("건물:"));
        filterBuilding.add(buildingCombo);
        filterBuilding.add(availableCheckBox);

        JPanel filterAvailable = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        sortByPriceButton  = new JButton("가격순");
        sortByLatestButton = new JButton("최신순");
        filterAvailable.add(sortByPriceButton);
        filterAvailable.add(sortByLatestButton);

        filterRow.add(filterBuilding,  BorderLayout.WEST);
        filterRow.add(filterAvailable, BorderLayout.EAST);

        top.add(searchRow);
        top.add(filterRow);

        // 이벤트 연결
        searchButton.addActionListener(e -> onSearch());
        searchField.addActionListener(e -> onSearch());
        categoryCombo.addActionListener(e -> applyFilters());
        buildingCombo.addActionListener(e -> applyFilters());
        availableCheckBox.addActionListener(e -> applyFilters());
        sortByPriceButton.addActionListener(e -> displayItems(itemManager.sortByPrice()));
        sortByLatestButton.addActionListener(e -> displayItems(itemManager.sortByLatest()));

        return top;
    }

    /** 중앙: 이름/카테고리/가격 3열 JTable */
    private JScrollPane buildTablePanel() {
        String[] columns = {"이름", "카테고리", "가격(원/시간)"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 직접 편집 방지
            }
        };

        itemTable = new JTable(tableModel);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.getTableHeader().setReorderingAllowed(false);

        // 행 선택 시 상세 패널로 전달
        itemTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onItemSelected();
            }
        });

        return new JScrollPane(itemTable);
    }


    // ════════════════════════════════════════════════
    //  이벤트 핸들러
    // ════════════════════════════════════════════════

    /** 검색 버튼 / Enter 키 처리 */
    private void onSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllItems();
        } else {
            displayItems(itemManager.searchByName(keyword));
        }
    }

    /** 카테고리 + 건물 + 대여가능 조합 필터 */
    private void applyFilters() {
        String category = (String) categoryCombo.getSelectedItem();
        String building  = (String) buildingCombo.getSelectedItem();

        ArrayList<Item> result = itemManager.getItems();
        if (!"전체".equals(category)) result = itemManager.filterByCategory(category);
        if (!"전체".equals(building))  result.retainAll(itemManager.filterByBuilding(building));
        if (availableCheckBox.isSelected()) result.removeIf(item -> !item.isAvailable());

        displayItems(result);
    }


    private void onItemSelected() {
        int row = itemTable.getSelectedRow();
        if (row < 0 || row >= currentItems.size()) return;

        Item selected = currentItems.get(row);

        if (detailPanel != null) {
            detailPanel.loadItem(selected);
        }

        NavigationManager.getInstance().showPanel("ITEM_DETAIL");
    }


    // ════════════════════════════════════════════════
    //  목록 갱신
    // ════════════════════════════════════════════════

    /** 전체 목록으로 초기화 (외부에서도 호출 가능) */
    public void loadAllItems() {
        displayItems(itemManager.getItems());
    }

    /** 주어진 목록으로 테이블 갱신 — 모든 표시는 이 메서드를 통해 */
    private void displayItems(ArrayList<Item> items) {
        currentItems = items;
        tableModel.setRowCount(0); // 기존 행 전체 제거
        for (Item item : items) {
            tableModel.addRow(new Object[]{
                item.getName(),
                item.getCategory(),
                item.getPricePerHour()
            });
        }
    }
}
