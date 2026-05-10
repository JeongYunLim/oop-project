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

    private DefaultTableModel userTableModel;
    private ArrayList<User> userList = new ArrayList<>();
    private DefaultTableModel reportTableModel;
    private ArrayList<Rental> reportList = new ArrayList<>();

    public AdminPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("관리자 페이지", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 22));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("신고 관리",  buildReportTab());
        tabs.addTab("사용자 관리", buildUserTab());
        tabs.addTab("거래 관리",  buildRentalTab());

        JButton backBtn = new JButton("메인으로");
        backBtn.addActionListener(e ->
            NavigationManager.getInstance().showPanel("MAIN"));

        add(title,   BorderLayout.NORTH);
        add(tabs,    BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);
    }

    private JPanel buildReportTab() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"물품명", "소유자", "대여자", "신고 유형", "거래 상태", "처리 여부"};
        reportTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(reportTableModel);
        loadReportTable();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row < 0 || row >= reportList.size()) return;
            Rental rental = reportList.get(row);

            JDialog dialog = new JDialog();
            dialog.setTitle("신고 상세");
            dialog.setSize(400, 280);
            dialog.setLocationRelativeTo(panel);
            dialog.setLayout(new BorderLayout(10, 10));

            JPanel info = new JPanel(new GridLayout(0, 1, 4, 4));
            info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            info.add(new JLabel("신고 유형: " + rental.getPendingPolicy().getPenaltyName()));
            info.add(new JLabel("처리 여부: " + (rental.isResolved() ? "처리됨" : "미처리")));

            String detail = rental.getReportDetail();
            JTextArea detailArea = new JTextArea(detail.isEmpty() ? "내용 없음" : detail);
            detailArea.setEditable(false);
            detailArea.setLineWrap(true);
            detailArea.setWrapStyleWord(true);
            JScrollPane scroll = new JScrollPane(detailArea);
            scroll.setBorder(BorderFactory.createTitledBorder("신고 내용"));

            JButton resolveBtn = new JButton("처리 완료");
            resolveBtn.setEnabled(!rental.isResolved());
            resolveBtn.addActionListener(ev -> {
                rental.resolve();
                reportTableModel.setValueAt("처리됨", row, 5);
                JOptionPane.showMessageDialog(dialog, "처리가 완료되었습니다.");
                dialog.dispose();
            });

            JPanel btnPanel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPanel2.add(resolveBtn);

            dialog.add(info,   BorderLayout.NORTH);
            dialog.add(scroll, BorderLayout.CENTER);
            dialog.add(btnPanel2, BorderLayout.SOUTH);
            dialog.setVisible(true);
            table.clearSelection();
        });

        JButton refreshBtn = new JButton("새로고침");
        refreshBtn.addActionListener(e -> loadReportTable());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(refreshBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
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
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"아이디", "이름", "매너온도", "정지 여부"};
        userTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(userTableModel);
        loadUserTable();

        JButton banBtn   = new JButton("정지");
        JButton unbanBtn = new JButton("정지 해제");
        JButton refreshBtn = new JButton("새로고침");

        banBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(panel, "유저를 선택하세요."); return; }
            User user = userList.get(row);
            if (Admin.isAdmin(user)) { JOptionPane.showMessageDialog(panel, "관리자는 정지 불가합니다."); return; }
            if (user.isBanned())     { JOptionPane.showMessageDialog(panel, "이미 정지된 계정입니다."); return; }
            user.setBanned(true);
            userTableModel.setValueAt("정지", row, 3);
        });

        unbanBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(panel, "유저를 선택하세요."); return; }
            User user = userList.get(row);
            if (!user.isBanned()) { JOptionPane.showMessageDialog(panel, "정지된 계정이 아닙니다."); return; }
            user.setBanned(false);
            userTableModel.setValueAt("정상", row, 3);
        });

        refreshBtn.addActionListener(e -> loadUserTable());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
            userTableModel.addRow(new Object[]{
                u.getId(),
                u.getName(),
                String.format("%.1f", u.getTemperature().getValue()),
                u.isBanned() ? "정지" : "정상"
            });
        }
    }

    private JPanel buildRentalTab() {
        JPanel panel = new JPanel(new BorderLayout());
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
        panel.add(new JScrollPane(new JTable(model)));
        return panel;
    }
}
