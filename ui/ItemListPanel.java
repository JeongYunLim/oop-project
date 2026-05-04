package ui;

import manager.ItemManager;
import model.Item;

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

    /** 상단: 검색창, 카테고리 콤보박스, 정렬 버튼 */
    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));

        searchField = new JTextField(15);
        searchButton = new JButton("검색");

        // 카테고리 목록 — Item.category 에서 사용하는 값과 일치시킬 것
        String[] categories = {"전체", "전자기기", "도서", "생활용품", "의류", "스포츠"};
        categoryCombo = new JComboBox<>(categories);

        sortByPriceButton  = new JButton("가격순");
        sortByLatestButton = new JButton("최신순");

        top.add(new JLabel("검색:"));
        top.add(searchField);
        top.add(searchButton);
        top.add(new JLabel("카테고리:"));
        top.add(categoryCombo);
        top.add(sortByPriceButton);
        top.add(sortByLatestButton);

        // 이벤트 연결
        searchButton.addActionListener(e -> onSearch());
        searchField.addActionListener(e -> onSearch());   // Enter 키 검색
        categoryCombo.addActionListener(e -> onCategoryFilter());
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

    /** 카테고리 콤보박스 변경 처리 */
    private void onCategoryFilter() {
        String selected = (String) categoryCombo.getSelectedItem();
        if ("전체".equals(selected)) {
            loadAllItems();
        } else {
            displayItems(itemManager.filterByCategory(selected));
        }
    }

    /** 행 선택 시 상세 패널에 Item 전달 */
    private void onItemSelected() {
        int row = itemTable.getSelectedRow();
        if (row < 0 || row >= currentItems.size()) return;

        Item selected = currentItems.get(row);

        if (detailPanel != null) {
            detailPanel.loadItem(selected);
        }

        // TODO: NavigationManager 연동 후 화면 전환 처리
        // NavigationManager.getInstance().showPanel("detail");
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
