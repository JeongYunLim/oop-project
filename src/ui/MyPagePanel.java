package ui;

import domain.Inquiry;
import domain.Item;
import domain.User;
import manager.InquiryManager;
import manager.ItemManager;
import manager.NavigationManager;
import manager.RentalManager;
import manager.UserManager;
import transaction.Rental;
import transaction.RentalStatus;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MyPagePanel extends JPanel {

    private JLabel nameLabel = new JLabel("-");
    private JLabel tempLabel = new JLabel("-");
    private DefaultTableModel myItemsModel;
    private DefaultTableModel borrowingModel;
    private DefaultTableModel requestedModel;
    private DefaultTableModel sentModel;
    private DefaultTableModel rcvModel;
    private ArrayList<Inquiry> rcvInquiries = new ArrayList<>();
    private ArrayList<Rental> requestedRentalList = new ArrayList<>();
    private ArrayList<Rental> borrowingRentalList = new ArrayList<>();

    public MyPagePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.add(nameLabel);
        infoPanel.add(tempLabel);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("내가 등록한 물품",    buildMyItemsTab());
        tabs.addTab("내가 빌린 물품",      buildBorrowingTab());
        tabs.addTab("대여 요청 받은 목록", buildRequestedTab());
        tabs.addTab("문의 관리",           buildInquiryTab());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton backBtn   = new JButton("메인으로");
        JButton logoutBtn = new JButton("로그아웃");
        backBtn.addActionListener(e -> NavigationManager.getInstance().showPanel("MAIN"));
        logoutBtn.addActionListener(e -> {
            UserManager.getInstance().logout();
            NavigationManager.getInstance().showPanel("MAIN");
        });
        buttonPanel.add(backBtn);
        buttonPanel.add(logoutBtn);

        add(infoPanel,   BorderLayout.NORTH);
        add(tabs,        BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JScrollPane buildMyItemsTab() {
        String[] cols = {"물품명", "카테고리", "가격(원/시간)", "상태"};
        myItemsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        return new JScrollPane(new JTable(myItemsModel));
    }

    private JPanel buildBorrowingTab() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"물품명", "소유자", "대여 시작 시각", "거래 상태"};
        borrowingModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(borrowingModel);

        JButton startBtn = new JButton("대여 시작");
        startBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "대여 시작할 항목을 선택하세요.");
                return;
            }
            Rental rental = borrowingRentalList.get(row);
            if (rental.getStatus() != RentalStatus.APPROVED) {
                JOptionPane.showMessageDialog(panel, "소유자의 승인 후에 대여를 시작할 수 있습니다.");
                return;
            }
            rental.start();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd HH:mm");
            borrowingModel.setValueAt(rental.getStartedAt().format(fmt), row, 2);
            borrowingModel.setValueAt("대여 중", row, 3);
            JOptionPane.showMessageDialog(panel, "대여가 시작되었습니다.");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(startBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildRequestedTab() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"물품명", "대여 요청자", "거래 상태"};
        requestedModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(requestedModel);

        JButton approveBtn = new JButton("승인하기");
        approveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "승인할 요청을 선택하세요.");
                return;
            }
            Rental rental = requestedRentalList.get(row);
            if (rental.getStatus() != RentalStatus.REQUESTED) {
                JOptionPane.showMessageDialog(panel, "이미 처리된 요청입니다.");
                return;
            }
            rental.approve();
            requestedModel.setValueAt("예약 승인됨", row, 2);
            JOptionPane.showMessageDialog(panel, "대여 요청을 승인했습니다.");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(approveBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildInquiryTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTabbedPane inquiryTabs = new JTabbedPane();

        String[] sentCols = {"물품ID", "받는사람", "문의내용", "답변"};
        sentModel = new DefaultTableModel(sentCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JScrollPane sentPane = new JScrollPane(new JTable(sentModel));

        String[] rcvCols = {"물품ID", "보낸사람", "문의내용", "답변 여부"};
        rcvModel = new DefaultTableModel(rcvCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable rcvTable = new JTable(rcvModel);

        JButton replyBtn = new JButton("답변하기");
        replyBtn.addActionListener(e -> {
            int row = rcvTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "문의를 선택하세요.");
                return;
            }
            if (row >= rcvInquiries.size()) return;
            Inquiry inquiry = rcvInquiries.get(row);
            if (inquiry.hasReply()) {
                JOptionPane.showMessageDialog(panel, "이미 답변된 문의입니다.");
                return;
            }
            String replyText = JOptionPane.showInputDialog(panel, "답변 내용을 입력하세요:");
            if (replyText != null && !replyText.trim().isEmpty()) {
                inquiry.addReply(replyText.trim());
                rcvModel.setValueAt("답변 완료", row, 3);
                JOptionPane.showMessageDialog(panel, "답변이 등록되었습니다.");
            }
        });

        JPanel rcvBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rcvBtnPanel.add(replyBtn);

        JPanel rcvPanel = new JPanel(new BorderLayout());
        rcvPanel.add(new JScrollPane(rcvTable), BorderLayout.CENTER);
        rcvPanel.add(rcvBtnPanel, BorderLayout.SOUTH);

        inquiryTabs.addTab("보낸 문의", sentPane);
        inquiryTabs.addTab("받은 문의", rcvPanel);

        panel.add(inquiryTabs, BorderLayout.CENTER);
        return panel;
    }

    public void refresh() {
        User user = UserManager.getInstance().getLoggedInUser();
        if (user == null) {
            nameLabel.setText("로그인이 필요합니다.");
            tempLabel.setText("");
            myItemsModel.setRowCount(0);
            borrowingModel.setRowCount(0);
            borrowingRentalList.clear();
            requestedModel.setRowCount(0);
            requestedRentalList.clear();
            sentModel.setRowCount(0);
            rcvModel.setRowCount(0);
            rcvInquiries.clear();
            return;
        }

        nameLabel.setText("이름: " + user.getName());
        tempLabel.setText(String.format("매너온도: %.1f °C", user.getTemperature().getValue()));

        // 내가 등록한 물품
        myItemsModel.setRowCount(0);
        for (Item item : ItemManager.getInstance().getItemsByOwner(user.getId())) {
            myItemsModel.addRow(new Object[]{
                item.getName(), item.getCategory(), item.getPricePerHour(), item.getStateName()
            });
        }

        // 내가 빌린 물품 (borrower 기준, 진행 중인 상태)
        borrowingModel.setRowCount(0);
        borrowingRentalList = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd HH:mm");
        for (Rental r : RentalManager.getInstance().getRentalsByBorrower(user.getId())) {
            if (r.getStatus() == RentalStatus.REQUESTED
                    || r.getStatus() == RentalStatus.APPROVED
                    || r.getStatus() == RentalStatus.RENTING) {
                borrowingRentalList.add(r);
                String startedAt = r.getStartedAt() != null
                        ? r.getStartedAt().format(fmt) : "-";
                borrowingModel.addRow(new Object[]{
                    r.getItem().getName(),
                    r.getOwner().getName(),
                    startedAt,
                    r.getStatusText()
                });
            }
        }

        // 대여 요청 받은 목록 (owner 기준, 테이블 행 인덱스 = 리스트 인덱스)
        requestedModel.setRowCount(0);
        requestedRentalList = new ArrayList<>();
        for (Rental r : RentalManager.getInstance().getRentalsByOwner(user.getId())) {
            if (r.getStatus() == RentalStatus.REQUESTED || r.getStatus() == RentalStatus.APPROVED) {
                requestedRentalList.add(r);
                requestedModel.addRow(new Object[]{
                    r.getItem().getName(),
                    r.getBorrower().getName(),
                    r.getStatusText()
                });
            }
        }

        // 보낸 문의
        sentModel.setRowCount(0);
        for (Inquiry i : InquiryManager.getInstance().getInquiriesByUser(user.getId())) {
            sentModel.addRow(new Object[]{
                i.getItemId(),
                i.getToUserId(),
                i.getMessage(),
                i.hasReply() ? i.getReply() : "미답변"
            });
        }

        // 받은 문의
        rcvInquiries = InquiryManager.getInstance().getInquiriesByOwner(user.getId());
        rcvModel.setRowCount(0);
        for (Inquiry i : rcvInquiries) {
            rcvModel.addRow(new Object[]{
                i.getItemId(),
                i.getFromUserId(),
                i.getMessage(),
                i.hasReply() ? "답변 완료" : "미답변"
            });
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (nameLabel != null && myItemsModel != null) {
            refresh();
        }
    }
}
