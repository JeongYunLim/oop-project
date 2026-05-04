package transaction;

import domain.Item;
import domain.User;
import strategy.PenaltyPolicy;

public class Rental extends Transaction {

    public Rental(User owner, User borrower, Item item) {
        super(owner, borrower, item);
    }

    @Override
    public void request() {
        item.requestRental();
        status = RentalStatus.REQUESTED;
    }

    @Override
    public void approve() {
        if (!item.getStateName().equals("예약됨")) {
            item.requestRental();
        }

        status = RentalStatus.APPROVED;
    }

    @Override
    public void start() {
        item.startRental();
        status = RentalStatus.RENTING;
    }

    @Override
    public void completeReturn() {
        item.returnItem();
        status = RentalStatus.COMPLETED;

        borrower.getTemperature().increase(0.3);
        owner.getTemperature().increase(0.2);
    }

    public void reportProblem(PenaltyPolicy policy) {
        policy.apply(borrower);
        status = RentalStatus.REPORTED;
    }
}
