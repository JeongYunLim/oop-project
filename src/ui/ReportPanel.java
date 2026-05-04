package ui;

import strategy.DamagePenalty;
import strategy.LatePenalty;
import strategy.NoReturnPenalty;
import strategy.PenaltyPolicy;
import transaction.Rental;

import javax.swing.*;
import java.awt.*;

public class ReportPanel extends JPanel {

    public ReportPanel(Rental rental, Runnable onComplete) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 248));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("문제 신고 및 패널티 적용", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(5, 1, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel infoLabel = new JLabel("신고할 문제 유형을 선택하세요.");
        infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));

        JRadioButton lateButton = new JRadioButton("연체 - 매너온도 0.5 감소");
        JRadioButton damageButton = new JRadioButton("파손 - 매너온도 1.5 감소");
        JRadioButton noReturnButton = new JRadioButton("미반납 - 매너온도 3.0 감소");

        lateButton.setBackground(Color.WHITE);
        damageButton.setBackground(Color.WHITE);
        noReturnButton.setBackground(Color.WHITE);

        ButtonGroup group = new ButtonGroup();
        group.add(lateButton);
        group.add(damageButton);
        group.add(noReturnButton);

        centerPanel.add(infoLabel);
        centerPanel.add(lateButton);
        centerPanel.add(damageButton);
        centerPanel.add(noReturnButton);

        JButton applyButton = new JButton("패널티 적용");

        applyButton.addActionListener(e -> {
            PenaltyPolicy policy = null;

            if (lateButton.isSelected()) {
                policy = new LatePenalty();
            } else if (damageButton.isSelected()) {
                policy = new DamagePenalty();
            } else if (noReturnButton.isSelected()) {
                policy = new NoReturnPenalty();
            }

            if (policy == null) {
                JOptionPane.showMessageDialog(this, "문제 유형을 선택해주세요.");
                return;
            }

            rental.reportProblem(policy);

            JOptionPane.showMessageDialog(
                    this,
                    policy.getPenaltyName() + "가 적용되었습니다.\n"
                            + "대여자 매너온도 -" + policy.getPenaltyAmount()
            );

            if (onComplete != null) {
                onComplete.run();
            }
        });

        add(titleLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(applyButton, BorderLayout.SOUTH);
    }
}
