package ui;

import domain.User;
import manager.NavigationManager;
import manager.RentalManager;
import manager.ReportManager;
import manager.UserManager;
import transaction.Rental;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminPanel extends JPanel {

    public AdminPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("관리자 페이지", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 22));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("신고 관리",  buildReportTab());
        tabs.addTab("사용자 관리", buildUserTab());
        tabs.addTab("거래 관리",  buildRentalTab());

        JButton backBtn = new JButton("뒤로가기");
        backBtn.addActionListener(e ->
            NavigationManager.getInstance().showPanel("MAIN"));

        add(title,   BorderLayout.NORTH);
        add(tabs,    BorderLayout.CENTER);
        add(backBtn, BorderLayout.SOUTH);
    }

    private JPanel buildReportTab() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"물품명", "소유자", "대여자", "거래 상태"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Rental r : ReportManager.getInstance().getReports()) {
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

    private JPanel buildUserTab() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"아이디", "이름", "매너온도"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (User u : UserManager.getInstance().getAllUsers()) {
            model.addRow(new Object[]{
                u.getId(),
                u.getName(),
                String.format("%.1f", u.getTemperature().getValue())
            });
        }
        panel.add(new JScrollPane(new JTable(model)));
        return panel;
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
