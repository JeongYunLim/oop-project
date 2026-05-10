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
        String[] cols = {"물품명", "소유자", "대여자", "거래 상태", "처리 여부"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        ArrayList<Rental> reports = ReportManager.getInstance().getReports();
        for (Rental r : reports) {
            model.addRow(new Object[]{
                r.getItem().getName(),
                r.getOwner().getName(),
                r.getBorrower().getName(),
                r.getStatusText(),
                r.isResolved() ? "처리됨" : "미처리"
            });
        }

        JTable table = new JTable(model);

        JButton resolveBtn = new JButton("처리 완료");
        resolveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "신고를 선택해주세요.");
                return;
            }
            Rental rental = reports.get(row);
            if (rental.isResolved()) {
                JOptionPane.showMessageDialog(panel, "이미 처리된 신고입니다.");
                return;
            }
            rental.resolve();
            model.setValueAt("처리됨", row, 4);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(resolveBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
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
