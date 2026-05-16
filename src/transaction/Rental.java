 package transaction;

import domain.Item;
import domain.User;
import manager.RentalManager;
import manager.ReportManager;
import state.AvailableState;
import state.ReservedState;
import state.RentedState;
import strategy.PenaltyPolicy;

import java.time.LocalDateTime;

public class Rental extends Transaction {

    private User owner;
    private User borrower;
    private Item item;

    private RentalStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime startedAt;
    private LocalDateTime returnedAt;

    private PenaltyPolicy pendingPolicy;
    private String reportDetail = "";
    private boolean resolved = false;

    private boolean registered = false;

    public Rental(User owner, User borrower, Item item) {
        super(owner, borrower, item);

        this.owner = owner;
        this.borrower = borrower;
        this.item = item;
        this.status = RentalStatus.REQUESTED;
        this.requestedAt = LocalDateTime.now();
    }

    @Override
    public void request() {
        if (item == null) {
            return;
        }

        if (!item.isAvailable()) {
            return;
        }

        status = RentalStatus.REQUESTED;
        requestedAt = LocalDateTime.now();

        item.setState(new ReservedState());

        if (!registered) {
            RentalManager.getInstance().addRental(this);
            registered = true;
        }
    }

    @Override
    public void approve() {
        if (status != RentalStatus.REQUESTED) {
            return;
        }

        status = RentalStatus.APPROVED;
        approvedAt = LocalDateTime.now();
    }

    @Override
    public void start() {
        if (status != RentalStatus.APPROVED) {
            return;
        }

        status = RentalStatus.RENTING;
        startedAt = LocalDateTime.now();

        item.setState(new RentedState());
    }

    @Override
    public void completeReturn() {
        if (status != RentalStatus.RENTING) {
            return;
        }

        status = RentalStatus.COMPLETED;
        returnedAt = LocalDateTime.now();

        item.setState(new AvailableState());

        if (borrower != null && borrower.getTemperature() != null) {
            borrower.getTemperature().increase(0.3);
        }

        if (owner != null && owner.getTemperature() != null) {
            owner.getTemperature().increase(0.3);
        }
    }

    public boolean cancelRequest(User user) {
        if (user == null) {
            return false;
        }

        if (borrower == null) {
            return false;
        }

        if (!user.getId().equals(borrower.getId())) {
            return false;
        }

        if (status != RentalStatus.REQUESTED) {
            return false;
        }

        status = RentalStatus.CANCELLED;

        if (item != null) {
            item.setState(new AvailableState());
        }

        return true;
    }

    public void reportProblem(PenaltyPolicy policy, String detail) {
        if (policy == null) {
            return;
        }

        this.pendingPolicy = policy;
        this.reportDetail = detail == null ? "" : detail;
        this.resolved = false;

        // 신고 완료 시 거래 상태를 문제 신고 상태로 변경
        this.status = RentalStatus.REPORTED;

        // 신고 완료 시 대여 중이던 물품을 다시 반납 처리
        this.returnedAt = LocalDateTime.now();

        if (item != null) {
            item.setState(new AvailableState());
        }

        // 문제 발생 시 대여자에게 패널티 적용
        if (borrower != null) {
            policy.apply(borrower);
        }

        ReportManager.getInstance().addReport(this);
    }

    public void resolve() {
        this.resolved = true;
    }

    public User getOwner() {
        return owner;
    }

    public User getBorrower() {
        return borrower;
    }

    public Item getItem() {
        return item;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public PenaltyPolicy getPendingPolicy() {
        return pendingPolicy;
    }

    public String getReportDetail() {
        return reportDetail;
    }

    public boolean isResolved() {
        return resolved;
    }

    public String getStatusText() {
        switch (status) {
            case REQUESTED:
                return "대여 요청됨";
            case APPROVED:
                return "대여 승인됨";
            case RENTING:
                return "대여 중";
            case COMPLETED:
                return "거래 완료";
            case REPORTED:
                return "문제 신고됨";
            case CANCELLED:
                return "대여 요청 취소됨";
            default:
                return "알 수 없음";
        }
    }
}