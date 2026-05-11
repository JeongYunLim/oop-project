 package ui;

import domain.User;
import manager.NavigationManager;
import manager.UserManager;
import transaction.Rental;
import transaction.RentalStatus;

import javax.swing.*;
import java.awt.*;

public class TransactionPanel extends JPanel {

    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color MAIN_COLOR = new Color(52, 120, 246);
    private final Color MAIN_DARK_COLOR = new Color(35, 90, 200);
    private final Color SUB_COLOR = new Color(235, 242, 255);
    private final Color TEXT_COLOR = new Color(40, 40, 40);
    private final Color GRAY_TEXT = new Color(110, 110, 110);
    private final Color DANGER_COLOR = new Color(235, 87, 87);

    private Rental rental;

    private JLabel itemNameLabel;
    private JLabel ownerLabel;
    private JLabel borrowerLabel;
    private JLabel itemStateLabel;
    private JLabel rentalStatusLabel;
    private JLabel ownerTempLabel;
    private JLabel borrowerTempLabel;

    public TransactionPanel(Rental rental) {
        this.rental = rental;

        User loggedInUser = UserManager.getInstance().getLoggedInUser();
        boolean isOwner = loggedInUser != null
                && loggedInUser.getId().equals(rental.getOwner().getId());

        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel card = new JPanel(new BorderLayout(20, 24));
        card.setBackground(CARD_COLOR);
        card.setPreferredSize(new Dimension(620, 500));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(35, 45, 35, 45)
        ));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(CARD_COLOR);

        JLabel titleLabel = new JLabel("거래 관리");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        titleLabel.setForeground(MAIN_COLOR);

        JLabel subTitleLabel = new JLabel("대여 요청부터 반납 확인까지 거래 상태를 관리합니다.");
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        subTitleLabel.setForeground(GRAY_TEXT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subTitleLabel);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(7, 1, 8, 8));
        infoPanel.setBackground(CARD_COLOR);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 235, 235), 1),
                BorderFactory.createEmptyBorder(22, 28, 22, 28)
        ));

        itemNameLabel = createInfoLabel(true);
        ownerLabel = createInfoLabel(false);
        borrowerLabel = createInfoLabel(false);
        itemStateLabel = createInfoLabel(false);
        rentalStatusLabel = createInfoLabel(false);
        ownerTempLabel = createInfoLabel(false);
        borrowerTempLabel = createInfoLabel(false);

        infoPanel.add(itemNameLabel);
        infoPanel.add(ownerLabel);
        infoPanel.add(borrowerLabel);
        infoPanel.add(itemStateLabel);
        infoPanel.add(rentalStatusLabel);
        infoPanel.add(ownerTempLabel);
        infoPanel.add(borrowerTempLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(CARD_COLOR);

        JButton approveButton = createMainButton("예약 승인");
        JButton startButton = createMainButton("대여 시작");
        JButton returnButton = createSubButton("반납 확인");
        JButton reportButton = createDangerButton("문제 신고");
        JButton mainButton = createTextButton("메인으로");

        approveButton.addActionListener(e -> {
            rental.approve();
            updateLabels();
            JOptionPane.showMessageDialog(this, "예약이 승인되었습니다.");
        });

        startButton.addActionListener(e -> {
            if (rental.getStatus() != RentalStatus.APPROVED) {
                JOptionPane.showMessageDialog(this, "소유자의 승인 후에 대여를 시작할 수 있습니다.");
                return;
            }

            rental.start();
            updateLabels();
            JOptionPane.showMessageDialog(this, "대여가 시작되었습니다.");
        });

        returnButton.addActionListener(e -> {
            rental.completeReturn();
            updateLabels();
            JOptionPane.showMessageDialog(this, "반납이 완료되었습니다. 매너온도가 상승했습니다.");
        });

        reportButton.addActionListener(e -> {
            JFrame reportFrame = new JFrame("문제 신고");
            reportFrame.setSize(500, 450);
            reportFrame.setLocationRelativeTo(null);
            reportFrame.add(new ReportPanel(rental, () -> {
                updateLabels();
                reportFrame.dispose();
            }));
            reportFrame.setVisible(true);
        });

        mainButton.addActionListener(e ->
                NavigationManager.getInstance().showPanel("MAIN")
        );

        approveButton.setVisible(isOwner);
        startButton.setVisible(!isOwner);
        returnButton.setVisible(isOwner);
        reportButton.setVisible(isOwner);

        buttonPanel.add(approveButton);
        buttonPanel.add(startButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(reportButton);
        buttonPanel.add(mainButton);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        add(card);
        updateLabels();
    }

    private JLabel createInfoLabel(boolean title) {
        JLabel label = new JLabel();
        label.setFont(new Font("맑은 고딕", title ? Font.BOLD : Font.PLAIN, title ? 18 : 15));
        label.setForeground(title ? MAIN_COLOR : TEXT_COLOR);
        return label;
    }

    private void updateLabels() {
        itemNameLabel.setText("물품명: " + rental.getItem().getName());
        ownerLabel.setText("소유자: " + rental.getOwner().getName());
        borrowerLabel.setText("대여자: " + rental.getBorrower().getName());
        itemStateLabel.setText("물품 상태: " + rental.getItem().getStateName());
        rentalStatusLabel.setText("거래 상태: " + rental.getStatusText());

        ownerTempLabel.setText(
                String.format("소유자 매너온도: %.1f℃", rental.getOwner().getTemperature().getValue())
        );

        borrowerTempLabel.setText(
                String.format("대여자 매너온도: %.1f℃", rental.getBorrower().getTemperature().getValue())
        );
    }

    private JButton createMainButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(MAIN_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(115, 38));

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
        button.setPreferredSize(new Dimension(115, 38));
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(DANGER_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(115, 38));
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
        button.setPreferredSize(new Dimension(115, 38));
        return button;
    }
}