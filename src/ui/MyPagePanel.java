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

    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color MAIN_COLOR = new Color(52, 120, 246);
    private final Color MAIN_DARK_COLOR = new Color(35, 90, 200);
    private final Color SUB_COLOR = new Color(235, 242, 255);
    private final Color TEXT_COLOR = new Color(40, 40, 40);
    private final Color GRAY_TEXT = new Color(110, 110, 110);

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
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel headerCard = createHeaderCard();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        tabs.setBackground(CARD_COLOR);
        tabs.addTab("내가 등록한 물품", buildMyItemsTab());
        tabs.addTab("내가 빌린 물품", buildBorrowingTab());
        tabs.addTab("대여 요청 받은 목록", buildRequestedTab());
        tabs.addTab("문의 관리", buildInquiryTab());

        JPanel centerCard = new JPanel(new BorderLayout());
        centerCard.setBackground(CARD_COLOR);
        centerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        centerCard.add(tabs, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton backBtn = createSubButton("메인으로");
        JButton logoutBtn = createTextButton("로그아웃");

        backBtn.addActionListener(e -> NavigationManager.getInstance().showPanel("MAIN"));

        logoutBtn.addActionListener(e -> {
            UserManager.getInstance().logout();
            NavigationManager.getInstance().showPanel("LOGIN");
        });

        buttonPanel.add(backBtn);
        buttonPanel.add(logoutBtn);

        add(headerCard, BorderLayout.NORTH);
        add(centerCard, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(22, 28, 22, 28)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(CARD_COLOR);

        JLabel titleLabel = new JLabel("마이페이지");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        titleLabel.setForeground(MAIN_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subTitleLabel = new JLabel("내 물품, 대여 거래, 문의 내역을 한 번에 관리합니다.");
        subTitleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        subTitleLabel.setForeground(GRAY_TEXT);
        subTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_COLOR);

        tempLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        tempLabel.setForeground(new Color(255, 130, 60));

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subTitleLabel);

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(CARD_COLOR);
        userPanel.add(nameLabel);
        userPanel.add(Box.createVerticalStrut(5));
        userPanel.add(tempLabel);

        card.add(textPanel, BorderLayout.WEST);
        card.add(userPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel buildMyItemsTab() {
        JPanel panel = createTabPanel();

        String[] cols = {"물품명", "카테고리", "가격(원/시간)", "상태", "대여자"};
        myItemsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = createStyledTable(myItemsModel);

        JButton returnConfirmBtn = createMainButton("반납 확인");
        JButton reportBtn = createDangerButton("신고하기");

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

        JPanel btnPanel = createButtonPanel();
        btnPanel.add(returnConfirmBtn);
        btnPanel.add(reportBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildBorrowingTab() {
        JPanel panel = createTabPanel();

        String[] cols = {"물품명", "소유자", "대여 시작 시각", "종료 시각", "남은 시간", "거래 상태"};
        borrowingModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = createStyledTable(borrowingModel);

        countdownTimer = new javax.swing.Timer(1000, e -> {
            if (!MyPagePanel.this.isShowing()) {
                countdownTimer.stop();
                return;
            }

            for (int i = 0; i < borrowingRentalList.size(); i++) {
                Rental r = borrowingRentalList.get(i);

                if (r.getStatus() == RentalStatus.RENTING) {
                    java.time.LocalDateTime end = r.getItem().getTimeSlot().getEndTime();
                    java.time.Duration remaining =
                            java.time.Duration.between(java.time.LocalDateTime.now(), end);

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

        JButton startBtn = createMainButton("대여 시작");
        JButton cancelBtn = createTextButton("요청 취소");
        JButton detailBtn = createSubButton("상세 정보");

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

        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "취소할 대여 요청을 선택하세요.");
                return;
            }

            Rental rental = borrowingRentalList.get(row);

            if (rental.getStatus() != RentalStatus.REQUESTED) {
                JOptionPane.showMessageDialog(panel, "승인 전 대여 요청만 취소할 수 있습니다.");
                return;
            }

            User user = UserManager.getInstance().getLoggedInUser();

            int result = JOptionPane.showConfirmDialog(
                    panel,
                    "대여 요청을 취소하시겠습니까?",
                    "요청 취소 확인",
                    JOptionPane.YES_NO_OPTION
            );

            if (result != JOptionPane.YES_OPTION) {
                return;
            }

            if (rental.cancelRequest(user)) {
                refresh();
                JOptionPane.showMessageDialog(panel, "대여 요청이 취소되었습니다.");
            } else {
                JOptionPane.showMessageDialog(panel, "대여 요청을 취소할 수 없습니다.");
            }
        });

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

        JPanel btnPanel = createButtonPanel();
        btnPanel.add(startBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(detailBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildRequestedTab() {
        JPanel panel = createTabPanel();

        String[] cols = {"물품명", "대여 요청자", "거래 상태"};
        requestedModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = createStyledTable(requestedModel);
        JButton approveBtn = createMainButton("승인하기");

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

        JPanel btnPanel = createButtonPanel();
        btnPanel.add(approveBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildInquiryTab() {
        JPanel panel = createTabPanel();

        JTabbedPane inquiryTabs = new JTabbedPane();
        inquiryTabs.setFont(new Font("맑은 고딕", Font.BOLD, 13));

        String[] sentCols = {"물품ID", "받는사람", "문의내용", "답변"};
        sentModel = new DefaultTableModel(sentCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable sentTable = createStyledTable(sentModel);
        JButton sentDetailBtn = createSubButton("문의 내용");

        sentDetailBtn.addActionListener(e -> {
            int row = sentTable.getSelectedRow();

            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "문의를 선택하세요.");
                return;
            }

            String msg = (String) sentModel.getValueAt(row, 2);
            String reply = (String) sentModel.getValueAt(row, 3);

            showInquiryDialog("보낸 문의", msg, reply);
        });

        JPanel sentBtnPanel = createButtonPanel();
        sentBtnPanel.add(sentDetailBtn);

        JPanel sentPanel = createTabPanel();
        sentPanel.add(new JScrollPane(sentTable), BorderLayout.CENTER);
        sentPanel.add(sentBtnPanel, BorderLayout.SOUTH);

        String[] rcvCols = {"물품ID", "보낸사람", "문의내용", "답변 여부"};
        rcvModel = new DefaultTableModel(rcvCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable rcvTable = createStyledTable(rcvModel);

        JButton rcvDetailBtn = createSubButton("문의 내용");
        JButton replyBtn = createMainButton("답변하기");

        replyBtn.addActionListener(e -> {
            int row = rcvTable.getSelectedRow();

            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "문의를 선택하세요.");
                return;
            }

            if (row >= rcvInquiries.size()) {
                return;
            }

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

        rcvDetailBtn.addActionListener(e -> {
            int row = rcvTable.getSelectedRow();

            if (row < 0 || row >= rcvInquiries.size()) {
                JOptionPane.showMessageDialog(panel, "문의를 선택하세요.");
                return;
            }

            Inquiry inq = rcvInquiries.get(row);

            showInquiryDialog(
                    "받은 문의",
                    inq.getMessage(),
                    inq.hasReply() ? inq.getReply() : "미답변"
            );
        });

        JPanel rcvBtnPanel = createButtonPanel();
        rcvBtnPanel.add(rcvDetailBtn);
        rcvBtnPanel.add(replyBtn);

        JPanel rcvPanel = createTabPanel();
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
        dialog.setSize(430, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JTextArea msgArea = createDialogTextArea(message);
        JScrollPane msgScroll = new JScrollPane(msgArea);
        msgScroll.setBorder(BorderFactory.createTitledBorder("문의 내용"));

        JTextArea replyArea = createDialogTextArea(reply);
        JScrollPane replyScroll = new JScrollPane(replyArea);
        replyScroll.setBorder(BorderFactory.createTitledBorder("답변"));

        JPanel center = new JPanel(new GridLayout(2, 1, 8, 8));
        center.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        center.add(msgScroll);
        center.add(replyScroll);

        dialog.add(center, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JTextArea createDialogTextArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        area.setBackground(new Color(250, 250, 250));
        return area;
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

            requestedModel.setRowCount(0);
            requestedRentalList.clear();

            sentModel.setRowCount(0);
            rcvModel.setRowCount(0);
            rcvInquiries.clear();

            return;
        }

        nameLabel.setText("이름: " + user.getName());
        tempLabel.setText(String.format("매너온도: %.1f °C", user.getTemperature().getValue()));

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
                    item.getName(),
                    item.getCategory(),
                    item.getPricePerHour(),
                    item.getStateName(),
                    borrowerName
            });
        }

        if (countdownTimer != null) {
            countdownTimer.restart();
        }

        borrowingModel.setRowCount(0);
        borrowingRentalList = new ArrayList<>();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd HH:mm");

        for (Rental r : RentalManager.getInstance().getRentalsByBorrower(user.getId())) {
            if (r.getStatus() == RentalStatus.REQUESTED
                    || r.getStatus() == RentalStatus.APPROVED
                    || r.getStatus() == RentalStatus.RENTING) {

                borrowingRentalList.add(r);

                String startedAt = r.getStartedAt() != null
                        ? r.getStartedAt().format(fmt)
                        : "-";

                String endTime = r.getStatus() == RentalStatus.RENTING
                        ? r.getItem().getTimeSlot().getEndTime().format(fmt)
                        : "-";

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

        requestedModel.setRowCount(0);
        requestedRentalList = new ArrayList<>();

        for (Rental r : RentalManager.getInstance().getRentalsByOwner(user.getId())) {
            if (r.getStatus() == RentalStatus.REQUESTED
                    || r.getStatus() == RentalStatus.APPROVED) {

                requestedRentalList.add(r);

                requestedModel.addRow(new Object[]{
                        r.getItem().getName(),
                        r.getBorrower().getName(),
                        r.getStatusText()
                });
            }
        }

        sentModel.setRowCount(0);

        for (Inquiry i : InquiryManager.getInstance().getInquiriesByUser(user.getId())) {
            sentModel.addRow(new Object[]{
                    i.getItemId(),
                    i.getToUserId(),
                    i.getMessage(),
                    i.hasReply() ? i.getReply() : "미답변"
            });
        }

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
        table.getTableHeader().setFont(new Font("맑은 고딕", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 244, 250));
        table.getTableHeader().setForeground(TEXT_COLOR);
        table.setGridColor(new Color(230, 230, 230));
        return table;
    }

    private JButton createMainButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(MAIN_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 36));

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
        button.setPreferredSize(new Dimension(120, 36));
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(235, 87, 87));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 36));
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
        button.setPreferredSize(new Dimension(120, 36));
        return button;
    }

    @Override
    public void updateUI() {
        super.updateUI();

        if (nameLabel != null && myItemsModel != null) {
            refresh();
        }
    }
}