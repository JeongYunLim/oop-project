package transaction;

import domain.Item;
import domain.User;
import manager.RentalManager;
import manager.ReportManager;
import strategy.PenaltyPolicy;
import java.time.LocalDateTime;

public class Rental extends Transaction {

    private boolean isResolved = false;
    private LocalDateTime startedAt = null;
    private PenaltyPolicy pendingPolicy = null;
    private String reportDetail = "";

    public LocalDateTime getStartedAt() { return startedAt; }
    public boolean isResolved() { return isResolved; }
    public PenaltyPolicy getPendingPolicy() { return pendingPolicy; }
    public String getReportDetail() { return reportDetail; }

    public void resolve() {
        if (pendingPolicy != null) pendingPolicy.apply(borrower);
        isResolved = true;
    }

    public Rental(User owner, User borrower, Item item) {
        super(owner, borrower, item);
    }

    @Override
    public void request() {
        item.requestRental();
        status = RentalStatus.REQUESTED;
        RentalManager.getInstance().addRental(this);
    }

    @Override
    public void approve() {
        if (status != RentalStatus.REQUESTED) {
            System.out.println("대여 요청 상태에서만 승인 가능합니다.");
            return;
        }
        status = RentalStatus.APPROVED;
    }

    @Override
    public void start() {
        if (status != RentalStatus.APPROVED) {
            System.out.println("승인된 상태에서만 대여 시작이 가능합니다.");
            return;
        }
        item.startRental();
        status = RentalStatus.RENTING;
        startedAt = LocalDateTime.now();
    }

    @Override
    public void completeReturn() {
        item.returnItem();
        status = RentalStatus.COMPLETED;
        borrower.getTemperature().increase(0.3);
        owner.getTemperature().increase(0.2);
    }

    public void reportProblem(PenaltyPolicy policy, String detail) {
        item.returnItem();
        pendingPolicy = policy;
        reportDetail = detail != null ? detail : "";
        status = RentalStatus.REPORTED;
        ReportManager.getInstance().addReport(this);
    }
}
