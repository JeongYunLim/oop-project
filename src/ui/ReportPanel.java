package ui;

import strategy.DamagePenalty;
import strategy.LatePenalty;
import strategy.NoReturnPenalty;
import strategy.PenaltyPolicy;
import transaction.Rental;

import javax.swing.*;
import java.awt.*;

public class ReportPanel extends JPanel {

    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color MAIN_COLOR = new Color(52, 120, 246);
    private final Color MAIN_DARK_COLOR = new Color(35, 90, 200);
    private final Color TEXT_COLOR = new Color(40, 40, 40);
    private final Color GRAY_TEXT = new Color(110, 110, 110);
    private final Color DANGER_COLOR = new Color(235, 87, 87);

    public ReportPanel(Rental rental, Runnable onComplete) {
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 18));
        card.setBackground(CARD_COLOR);
        card.setPreferredSize(new Dimension(420, 360));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225), 1),
                BorderFactory.createEmptyBorder(28, 32, 28, 32)
        ));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(CARD_COLOR);

        JLabel titleLabel = new JLabel("문제 신고");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        titleLabel.setForeground(DANGER_COLOR);

        JLabel subTitleLabel = new JLabel("문제 유형을 선택하고 필요한 내용을 작성해주세요.");
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        subTitleLabel.setForeground(GRAY_TEXT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subTitleLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(CARD_COLOR);

        JLabel infoLabel = new JLabel("신고 유형");
        infoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        infoLabel.setForeground(TEXT_COLOR);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JRadioButton lateButton = createRadioButton("연체 - 매너온도 0.5 감소");
        JRadioButton damageButton = createRadioButton("파손 - 매너온도 1.5 감소");
        JRadioButton noReturnButton = createRadioButton("미반납 - 매너온도 3.0 감소");

        ButtonGroup group = new ButtonGroup();
        group.add(lateButton);
        group.add(damageButton);
        group.add(noReturnButton);

        JTextArea reportDetailArea = new JTextArea(4, 20);
        reportDetailArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        reportDetailArea.setLineWrap(true);
        reportDetailArea.setWrapStyleWord(true);
        reportDetailArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        reportDetailArea.setBackground(new Color(250, 250, 250));

        JScrollPane detailScroll = new JScrollPane(reportDetailArea);
        detailScroll.setBorder(BorderFactory.createTitledBorder("신고 내용 (선택)"));
        detailScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailScroll.setMaximumSize(new Dimension(360, 95));

        centerPanel.add(infoLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(lateButton);
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(damageButton);
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(noReturnButton);
        centerPanel.add(Box.createVerticalStrut(14));
        centerPanel.add(detailScroll);

        JButton applyButton = createMainButton("신고 완료");

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

            rental.reportProblem(policy, reportDetailArea.getText().trim());

            JOptionPane.showMessageDialog(this, "신고가 완료되었습니다.");

            if (onComplete != null) {
                onComplete.run();
            }
        });

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(applyButton, BorderLayout.SOUTH);

        add(card);
    }

    private JRadioButton createRadioButton(String text) {
        JRadioButton button = new JRadioButton(text);
        button.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        button.setForeground(TEXT_COLOR);
        button.setBackground(CARD_COLOR);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createMainButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(MAIN_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 42));

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
}