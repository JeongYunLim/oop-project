 package ui;

import domain.Admin;
import domain.User;
import manager.NavigationManager;
import manager.RentalManager;
import manager.ReportManager;
import manager.UserManager;
import transaction.Rental;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class AdminPanel extends JPanel {

    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color MAIN_COLOR = new Color(52, 120, 246);
    private final Color MAIN_DARK_COLOR = new Color(35, 90, 200);
    private final Color SUB_COLOR = new Color(235, 242, 255);
    private final Color TEXT_COLOR = new Color(40, 40, 40);
    private final Color GRAY_TEXT = new Color(110, 110, 110);
    private final Color DANGER_COLOR = new Color(235, 87, 87);

    private DefaultTableModel userTableModel;
    private ArrayList<User> userList = new ArrayList<>();

    private DefaultTableModel reportTableModel;
    private ArrayList<Rental> reportList = new ArrayList<>();

    public AdminPanel() {
        setLayout(new BorderLayout(0, 18));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel headerCard = createHeaderCard();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        tabs.setBackground(CARD_COLOR);
        tabs.addTab("신고 관리", buildReportTab());
        tabs.addTab("사용자 관리", buildUserTab());
        tabs.addTab("거래 관리", buildRentalTab());

        JPanel centerCard = new JPanel(new BorderLayout());
        centerCard.setBackground(CARD_COLOR);
        centerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        centerCard.add(tabs, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setBackground(BACKGROUND_COLOR);

        JButton backBtn = createTextButton("메인으로");
        backBtn.addActionListener(e ->
                NavigationManager.getInstance().showPanel("MAIN")
        );
        bottomPanel.add(backBtn);

        add(headerCard, BorderLayout.NORTH);
        add(centerCard, BorderLayout.CENTER);
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

        JLabel titleLabel = new JLabel("관리자 페이지");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        titleLabel.setForeground(MAIN_COLOR);

        JLabel subTitleLabel = new JLabel("신고, 사용자, 거래 내역을 관리합니다.");
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        subTitleLabel.setForeground(GRAY_TEXT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(subTitleLabel);

        return card;
    }

    private JPanel buildReportTab() {
        JPanel panel = createTabPanel();

        String[] cols = {"물품명", "소유자", "대여자", "신고 유형", "거래 상태", "처리 여부"};
        reportTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = createStyledTable(reportTableModel);
        loadReportTable();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;

            int row = table.getSelectedRow();

            if (row < 0 || row >= reportList.size()) return;

            Rental rental = reportList.get(row);
            showReportDetailDialog(panel, rental, row);

            table.clearSelection();
        });

        JButton refreshBtn = createSubButton("새로고침");
        refreshBtn.addActionListener(e -> loadReportTable());

        JPanel btnPanel = createButtonPanel();
        btnPanel.add(refreshBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showReportDetailDialog(JPanel parent, Rental rental, int row) {
        JDialog dialog = new JDialog();
        dialog.setTitle("신고 상세");
        dialog.setSize(440, 340);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBackground(CARD_COLOR);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel info = new JPanel(new GridLayout(0, 1, 5, 5));
        info.setBackground(CARD_COLOR);

        info.add(createDialogInfoLabel("신고 유형: " + rental.getPendingPolicy().getPenaltyName()));
        info.add(createDialogInfoLabel("처리 여부: " + (rental.isResolved() ? "처리됨" : "미처리")));

        String borrowerPhone = rental.getBorrower().getPhoneNumber();
        info.add(createDialogInfoLabel("대여자 전화번호: " + (borrowerPhone.isEmpty() ? "(없음)" : borrowerPhone)));

        String detail = rental.getReportDetail();

        JTextArea detailArea = new JTextArea(detail.isEmpty() ? "내용 없음" : detail);
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        detailArea.setBackground(new Color(250, 250, 250));
        detailArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(detailArea);
        scroll.setBorder(BorderFactory.createTitledBorder("신고 내용"));

        JButton resolveBtn = createMainButton("처리 완료");
        resolveBtn.setEnabled(!rental.isResolved());

        resolveBtn.addActionListener(ev -> {
            rental.resolve();
            reportTableModel.setValueAt("처리됨", row, 5);
            JOptionPane.showMessageDialog(dialog, "처리가 완료되었습니다.");
            dialog.dispose();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(CARD_COLOR);
        btnPanel.add(resolveBtn);

        wrapper.add(info, BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);
        wrapper.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(wrapper, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JLabel createDialogInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private void loadReportTable() {
        reportList = ReportManager.getInstance().getReports();
        reportTableModel.setRowCount(0);

        for (Rental r : reportList) {
            reportTableModel.addRow(new Object[]{
                    r.getItem().getName(),
                    r.getOwner().getName(),
                    r.getBorrower().getName(),
                    r.getPendingPolicy().getPenaltyName(),
                    r.getStatusText(),
                    r.isResolved() ? "처리됨" : "미처리"
            });
        }
    }

    private JPanel buildUserTab() {
        JPanel panel = createTabPanel();

        String[] cols = {"아이디", "이름", "전화번호", "인증 여부", "매너온도", "정지 여부"};
        userTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = createStyledTable(userTableModel);
        loadUserTable();

        JButton banBtn = createDangerButton("정지");
        JButton unbanBtn = createSubButton("정지 해제");
        JButton refreshBtn = createSubButton("새로고침");

        banBtn.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "유저를 선택하세요.");
                return;
            }

            User user = userList.get(row);

            if (Admin.isAdmin(user)) {
                JOptionPane.showMessageDialog(panel, "관리자는 정지 불가합니다.");
                return;
            }

            if (user.isBanned()) {
                JOptionPane.showMessageDialog(panel, "이미 정지된 계정입니다.");
                return;
            }

            user.setBanned(true);
            userTableModel.setValueAt("정지", row, 5);
        });

        unbanBtn.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "유저를 선택하세요.");
                return;
            }

            User user = userList.get(row);

            if (!user.isBanned()) {
                JOptionPane.showMessageDialog(panel, "정지된 계정이 아닙니다.");
                return;
            }

            user.setBanned(false);
            userTableModel.setValueAt("정상", row, 5);
        });

        refreshBtn.addActionListener(e -> loadUserTable());

        JPanel btnPanel = createButtonPanel();
        btnPanel.add(banBtn);
        btnPanel.add(unbanBtn);
        btnPanel.add(refreshBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadUserTable() {
        userList = UserManager.getInstance().getAllUsers();
        userTableModel.setRowCount(0);

        for (User u : userList) {
            String phone = u.getPhoneNumber();

            userTableModel.addRow(new Object[]{
                    u.getId(),
                    u.getName(),
                    phone.isEmpty() ? "(없음)" : phone,
                    u.isPhoneVerified() ? "인증" : "미인증",
                    String.format("%.1f", u.getTemperature().getValue()),
                    u.isBanned() ? "정지" : "정상"
            });
        }
    }

    private JPanel buildRentalTab() {
        JPanel panel = createTabPanel();

        String[] cols = {"물품명", "소유자", "대여자", "거래 상태"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Rental r : RentalManager.getInstance().getAllRentals()) {
            model.addRow(new Object[]{
                    r.getItem().getName(),
                    r.getOwner().getName(),
                    r.getBorrower().getName(),
                    r.getStatusText()
            });
        }

        JTable table = createStyledTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTabPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setBackground(CARD_COLOR);
        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(220, 235, 255));
        table.setSelectionForeground(TEXT_COLOR);
        table.setGridColor(new Color(230, 230, 230));

        table.getTableHeader().setFont(new Font("맑은 고딕", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 244, 250));
        table.getTableHeader().setForeground(TEXT_COLOR);
        table.getTableHeader().setReorderingAllowed(false);

        return table;
    }

    private JButton createMainButton(String text) {
        JButton button = createBaseButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(MAIN_COLOR);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(MAIN_DARK_COLOR);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(MAIN_COLOR);
                }
            }
        });

        return button;
    }

    private JButton createSubButton(String text) {
        JButton button = createBaseButton(text);
        button.setForeground(MAIN_COLOR);
        button.setBackground(SUB_COLOR);
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = createBaseButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(DANGER_COLOR);
        return button;
    }

    private JButton createTextButton(String text) {
        JButton button = createBaseButton(text);
        button.setForeground(new Color(120, 120, 120));
        button.setBackground(new Color(235, 235, 235));
        button.setPreferredSize(new Dimension(120, 38));
        return button;
    }

    private JButton createBaseButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 36));
        return button;
    }
}