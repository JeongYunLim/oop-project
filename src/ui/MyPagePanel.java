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
    private ArrayList<Item> myItemList = new ArrayList<>();
    private javax.swing.Timer countdownTimer;
    private ItemDetailPanel detailPanel;

    public void setDetailPanel(ItemDetailPanel detailPanel) {
        this.detailPanel = detailPanel;
    }

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

    private JPanel buildMyItemsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"물품명", "카테고리", "가격(원/시간)", "상태", "대여자"};
        myItemsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(myItemsModel);

        JButton reportBtn = new JButton("신고하기");
        reportBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "신고할 물품을 선택하세요.");
                return;
            }
            Item item = myItemList.get(row);
            User user = UserManager.getInstance().getLoggedInUser();
            Rental target = null;
            for (Rental r : RentalManager.getInstance().getRentalsByOwner(user.getId())) {
                if (r.getItem().getItemId() == item.getItemId()
                        && r.getStatus() == RentalStatus.RENTING) {
                    target = r;
                    break;
                }
            }
            if (target == null) {
                JOptionPane.showMessageDialog(panel, "현재 대여 중인 물품이 아닙니다.");
                return;
            }
            Rental finalTarget = target;
            JFrame reportFrame = new JFrame("문제 신고");
            reportFrame.setSize(500, 450);
            reportFrame.setLocationRelativeTo(null);
            reportFrame.add(new ReportPanel(finalTarget, () -> {
                refresh();
                reportFrame.dispose();
            }));
            reportFrame.setVisible(true);
        });

        JButton returnConfirmBtn = new JButton("반납 확인");
        returnConfirmBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "반납 확인할 물품을 선택하세요.");
                return;
            }
            Item item = myItemList.get(row);
            User user = UserManager.getInstance().getLoggedInUser();
            Rental target = null;
            for (Rental r : RentalManager.getInstance().getRentalsByOwner(user.getId())) {
                if (r.getItem().getItemId() == item.getItemId()
                        && r.getStatus() == RentalStatus.RENTING) {
                    target = r;
                    break;
                }
            }
            if (target == null) {
                JOptionPane.showMessageDialog(panel, "현재 대여 중인 물품이 아닙니다.");
                return;
            }
            target.completeReturn();
            refresh();
            JOptionPane.showMessageDialog(panel, "반납이 완료되었습니다. 매너온도가 상승했습니다.");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(returnConfirmBtn);
        btnPanel.add(reportBtn);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildBorrowingTab() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"물품명", "소유자", "대여 시작 시각", "종료 시각", "남은 시간", "거래 상태"};
        borrowingModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(borrowingModel);

        countdownTimer = new javax.swing.Timer(1000, e -> {
            if (!MyPagePanel.this.isShowing()) {
                countdownTimer.stop();
                return;
            }
            for (int i = 0; i < borrowingRentalList.size(); i++) {
                Rental r = borrowingRentalList.get(i);
                if (r.getStatus() == RentalStatus.RENTING) {
                    java.time.LocalDateTime end = r.getItem().getTimeSlot().getEndTime();
                    java.time.Duration remaining = java.time.Duration.between(java.time.LocalDateTime.now(), end);
                    String timeStr;
                    if (remaining.isNegative()) {
                        timeStr = "반납 기한 초과";
                    } else {
                        long h = remaining.toHours();
                        long m = remaining.toMinutesPart();
                        long s = remaining.toSecondsPart();
                        timeStr = String.format("%d시간 %02d분 %02d초", h, m, s);
                    }
                    borrowingModel.setValueAt(timeStr, i, 4);
                }
            }
        });
        countdownTimer.start();

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
            borrowingModel.setValueAt(rental.getItem().getTimeSlot().getEndTime().format(fmt), row, 3);
            borrowingModel.setValueAt("대여 중", row, 5);
            JOptionPane.showMessageDialog(panel, "대여가 시작되었습니다.");
        });

        JButton detailBtn = new JButton("상세 정보");
        detailBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "항목을 선택하세요.");
                return;
            }
            if (detailPanel != null) {
                detailPanel.loadItem(borrowingRentalList.get(row).getItem());
                NavigationManager.getInstance().showPanel("ITEM_DETAIL");
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(startBtn);
        btnPanel.add(detailBtn);

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
            requestedModel.setValueAt("대여 승인됨", row, 2);
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
        JTable sentTable = new JTable(sentModel);
        JButton sentDetailBtn = new JButton("문의 내용");
        sentDetailBtn.addActionListener(e -> {
            int row = sentTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(panel, "문의를 선택하세요."); return; }
            String msg   = (String) sentModel.getValueAt(row, 2);
            String reply = (String) sentModel.getValueAt(row, 3);
            showInquiryDialog("보낸 문의", msg, reply);
        });
        JPanel sentBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sentBtnPanel.add(sentDetailBtn);
        JPanel sentPanel = new JPanel(new BorderLayout());
        sentPanel.add(new JScrollPane(sentTable), BorderLayout.CENTER);
        sentPanel.add(sentBtnPanel, BorderLayout.SOUTH);

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

        JButton rcvDetailBtn = new JButton("문의 내용");
        rcvDetailBtn.addActionListener(e -> {
            int row = rcvTable.getSelectedRow();
            if (row < 0 || row >= rcvInquiries.size()) { JOptionPane.showMessageDialog(panel, "문의를 선택하세요."); return; }
            Inquiry inq = rcvInquiries.get(row);
            showInquiryDialog("받은 문의", inq.getMessage(),
                    inq.hasReply() ? inq.getReply() : "미답변");
        });

        JPanel rcvBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rcvBtnPanel.add(rcvDetailBtn);
        rcvBtnPanel.add(replyBtn);

        JPanel rcvPanel = new JPanel(new BorderLayout());
        rcvPanel.add(new JScrollPane(rcvTable), BorderLayout.CENTER);
        rcvPanel.add(rcvBtnPanel, BorderLayout.SOUTH);

        inquiryTabs.addTab("보낸 문의", sentPanel);
        inquiryTabs.addTab("받은 문의", rcvPanel);

        panel.add(inquiryTabs, BorderLayout.CENTER);
        return panel;
    }

    private void showInquiryDialog(String title, String message, String reply) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JTextArea msgArea = new JTextArea(message);
        msgArea.setEditable(false);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        JScrollPane msgScroll = new JScrollPane(msgArea);
        msgScroll.setBorder(BorderFactory.createTitledBorder("문의 내용"));

        JTextArea replyArea = new JTextArea(reply);
        replyArea.setEditable(false);
        replyArea.setLineWrap(true);
        replyArea.setWrapStyleWord(true);
        JScrollPane replyScroll = new JScrollPane(replyArea);
        replyScroll.setBorder(BorderFactory.createTitledBorder("답변"));

        JPanel center = new JPanel(new GridLayout(2, 1, 5, 5));
        center.add(msgScroll);
        center.add(replyScroll);

        dialog.add(center, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    public void refresh() {
        User user = UserManager.getInstance().getLoggedInUser();
        if (user == null) {
            nameLabel.setText("로그인이 필요합니다.");
            tempLabel.setText("");
            myItemsModel.setRowCount(0);
            myItemList.clear();
            borrowingModel.setRowCount(0);
            borrowingRentalList.clear();
            borrowingRentalList.clear();
            requestedModel.setRowCount(0);
            requestedRentalList.clear();
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
        myItemList = new ArrayList<>();
        for (Item item : ItemManager.getInstance().getItemsByOwner(user.getId())) {
            myItemList.add(item);
            String borrowerName = "-";
            for (Rental r : RentalManager.getInstance().getRentalsByOwner(user.getId())) {
                if (r.getItem().getItemId() == item.getItemId()
                        && r.getStatus() == RentalStatus.RENTING) {
                    borrowerName = r.getBorrower().getName();
                    break;
                }
            }
            myItemsModel.addRow(new Object[]{
                item.getName(), item.getCategory(), item.getPricePerHour(), item.getStateName(), borrowerName
            });
        }

        // 내가 빌린 물품 (borrower 기준, 진행 중인 상태)
        if (countdownTimer != null) countdownTimer.restart();
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
                String endTime = r.getStatus() == RentalStatus.RENTING
                        ? r.getItem().getTimeSlot().getEndTime().format(fmt) : "-";
                borrowingModel.addRow(new Object[]{
                    r.getItem().getName(),
                    r.getOwner().getName(),
                    startedAt,
                    endTime,
                    "-",
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
