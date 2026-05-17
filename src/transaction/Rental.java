 package transaction;

import domain.Item;
import domain.User;
import manager.RentalManager;
import manager.ReportManager;
import strategy.PenaltyPolicy;
import java.time.LocalDateTime;


public class Rental extends Transaction {

    private RentalStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime returnedAt;

    private PenaltyPolicy pendingPolicy;
    private String reportDetail = "";
    private boolean resolved = false;

    public Rental(User owner, User borrower, Item item) {
        super(owner, borrower, item);
        this.status = RentalStatus.REQUESTED;
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

        // 물품 상태 변경은 Item에게 맡김
        item.requestRental();

        RentalManager.getInstance().addRental(this);
    }

    @Override
    public void approve() {
        if (status != RentalStatus.REQUESTED) {
            return;
        }

        status = RentalStatus.APPROVED;
    }

    @Override
    public void start() {
        if (status != RentalStatus.APPROVED) {
            return;
        }

        status = RentalStatus.RENTING;
        startedAt = LocalDateTime.now();

        // 물품 상태 변경은 Item에게 맡김
        item.startRental();
    }

    public void completeReturn() {
        if (status != RentalStatus.RENTING) {
            return;
        }

        status = RentalStatus.COMPLETED;
        returnedAt = LocalDateTime.now();

        // 물품 상태 변경은 Item에게 맡김
        item.returnItem();

        borrower.getTemperature().increase(0.3);
        owner.getTemperature().increase(0.3);
    }

    public boolean cancelRequest(User user) {
        if (user == null) {
            return false;
        }

        if (borrower == null) {
            return false;
        }

        // 대여 요청한 사람만 취소 가능
        if (!user.getId().equals(borrower.getId())) {
            return false;
        }

        // 승인 전 요청 상태에서만 취소 가능
        if (status != RentalStatus.REQUESTED) {
            return false;
        }

        status = RentalStatus.CANCELLED;
        returnedAt = LocalDateTime.now();

        if (item != null) {
            item.returnItem();
        }

        return true;
    }

    public void reportProblem(PenaltyPolicy policy, String detail) {
        this.pendingPolicy = policy;
        this.reportDetail = detail == null ? "" : detail;
        this.resolved = false;

        status = RentalStatus.REPORTED;
        returnedAt = LocalDateTime.now();

        // 신고 완료 시에도 물품은 다시 대여 가능 상태로 반납 처리
        if (item != null) {
            item.returnItem();
        }

        // 패널티 정책은 PenaltyPolicy 다형성을 통해 적용
        if (borrower != null) {
            policy.apply(borrower);
        }

        ReportManager.getInstance().addReport(this);
    }

    public void resolve() {
        this.resolved = true;
    }

    public RentalStatus getStatus() {
        return status;
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

    @Override
    public String getStatusText() {
        switch (status) {
            case REQUESTED:
                return "대여 요청됨";
            case APPROVED:
                return "예약 승인됨";
            case RENTING:
                return "대여 중";
            case COMPLETED:
                return "반납 완료";
            case REPORTED:
                return "문제 신고됨";
            case CANCELLED:
                return "대여 요청 취소됨";
            default:
                return "알 수 없음";
        }
    }
}