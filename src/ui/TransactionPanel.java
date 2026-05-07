package ui;

import transaction.Rental;

import javax.swing.*;
import java.awt.*;

public class TransactionPanel extends JPanel {

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

        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 248));

        JLabel titleLabel = new JLabel("거래 관리 페이지", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(7, 1, 10, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        itemNameLabel = new JLabel();
        ownerLabel = new JLabel();
        borrowerLabel = new JLabel();
        itemStateLabel = new JLabel();
        rentalStatusLabel = new JLabel();
        ownerTempLabel = new JLabel();
        borrowerTempLabel = new JLabel();

        itemNameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        ownerLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        borrowerLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        itemStateLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        rentalStatusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        ownerTempLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        borrowerTempLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));

        infoPanel.add(itemNameLabel);
        infoPanel.add(ownerLabel);
        infoPanel.add(borrowerLabel);
        infoPanel.add(itemStateLabel);
        infoPanel.add(rentalStatusLabel);
        infoPanel.add(ownerTempLabel);
        infoPanel.add(borrowerTempLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(245, 246, 248));

        JButton approveButton = new JButton("예약 승인");
        JButton startButton = new JButton("대여 시작");
        JButton returnButton = new JButton("반납 확인");
        JButton reportButton = new JButton("문제 신고");

        approveButton.addActionListener(e -> {
            rental.approve();
            updateLabels();
            JOptionPane.showMessageDialog(this, "예약이 승인되었습니다.");
        });

        startButton.addActionListener(e -> {
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
            reportFrame.setSize(500, 400);
            reportFrame.setLocationRelativeTo(null);

            reportFrame.add(new ReportPanel(rental, () -> {
                updateLabels();
                reportFrame.dispose();
            }));

            reportFrame.setVisible(true);
        });

        buttonPanel.add(approveButton);
        buttonPanel.add(startButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(reportButton);

        add(titleLabel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateLabels();
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
}