package ui;

import transaction.Rental;

import javax.swing.*;
import java.awt.*;

public class TransactionPanel extends JPanel {

    private Rental rental;

    private JLabel itemNameLabel;
    private JLabel itemCategoryLabel;
    private JLabel itemLocationLabel;
    private JLabel itemPriceLabel;
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
        infoPanel.setLayout(new GridLayout(10, 1, 10, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        itemNameLabel = new JLabel();
        itemCategoryLabel = new JLabel();
        itemLocationLabel = new JLabel();
        itemPriceLabel = new JLabel();
        ownerLabel = new JLabel();
        borrowerLabel = new JLabel();
        itemStateLabel = new JLabel();
        rentalStatusLabel = new JLabel();
        ownerTempLabel = new JLabel();
        borrowerTempLabel = new JLabel();

        JLabel[] labels = {
                itemNameLabel,
                itemCategoryLabel,
                itemLocationLabel,
                itemPriceLabel,
                ownerLabel,
                borrowerLabel,
                itemStateLabel,
                rentalStatusLabel,
                ownerTempLabel,
                borrowerTempLabel
        };

        for (JLabel label : labels) {
            label.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        }

        itemNameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));

        infoPanel.add(itemNameLabel);
        infoPanel.add(itemCategoryLabel);
        infoPanel.add(itemLocationLabel);
        infoPanel.add(itemPriceLabel);
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
        itemCategoryLabel.setText("카테고리: " + rental.getItem().getCategory());

        if (rental.getItem().getLocation() != null) {
            itemLocationLabel.setText("위치: " + rental.getItem().getLocation().toString());
        } else {
            itemLocationLabel.setText("위치: -");
        }

        itemPriceLabel.setText("가격: " + rental.getItem().getPricePerHour() + "원 / 시간");
        ownerLabel.setText("소유자: " + rental.getOwner().getName());
        borrowerLabel.setText("대여자: " + rental.getBorrower().getName());
        itemStateLabel.setText("물품 상태: " + convertItemState(rental.getItem().getState()));
        rentalStatusLabel.setText("거래 상태: " + rental.getStatusText());

        ownerTempLabel.setText(
                String.format("소유자 매너온도: %.1f℃", rental.getOwner().getTemperature().getDegree())
        );

        borrowerTempLabel.setText(
                String.format("대여자 매너온도: %.1f℃", rental.getBorrower().getTemperature().getDegree())
        );
    }

    private String convertItemState(String state) {
        if ("available".equals(state)) {
            return "대여 가능";
        } else if ("reserved".equals(state)) {
            return "예약됨";
        } else if ("rented".equals(state)) {
            return "대여 중";
        }
        return state;
    }
}