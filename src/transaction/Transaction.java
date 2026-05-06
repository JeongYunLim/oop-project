package transaction;

import domain.Item;
import domain.User;

public abstract class Transaction {
    protected User owner;
    protected User borrower;
    protected Item item;
    protected RentalStatus status;

    public Transaction(User owner, User borrower, Item item) {
        this.owner = owner;
        this.borrower = borrower;
        this.item = item;
        this.status = RentalStatus.REQUESTED;
    }

    public abstract void request();
    public abstract void approve();
    public abstract void start();
    public abstract void completeReturn();

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
            default:
                return "알 수 없음";
        }
    }
}
