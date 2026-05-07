package transaction;

import domain.Item;
import domain.User;
import state.AvailableState;
import state.ReservedState;
import state.RentedState;
import strategy.PenaltyPolicy;

public class Rental extends Transaction {

    public Rental(User owner, User borrower, Item item) {
        super(owner, borrower, item);
    }

    @Override
    public void request() {
        if (item.isAvailable()) {
            new AvailableState().requestRental(item);
            status = RentalStatus.REQUESTED;
        } else {
            System.out.println("현재 대여 요청이 불가능한 물품입니다.");
        }
    }

    @Override
    public void approve() {
        if (item.isAvailable()) {
            new AvailableState().requestRental(item);
        }

        if (item.isReserved()) {
            status = RentalStatus.APPROVED;
        } else {
            System.out.println("예약 승인 가능한 상태가 아닙니다.");
        }
    }

    @Override
    public void start() {
        if (item.isReserved()) {
            new ReservedState().startRental(item);
            status = RentalStatus.RENTING;
        } else {
            System.out.println("대여 시작 가능한 상태가 아닙니다.");
        }
    }

    @Override
    public void completeReturn() {
        if (item.isRented()) {
            new RentedState().returnItem(item);
            status = RentalStatus.COMPLETED;

            borrower.getTemperature().tempIncrease(0.3);
            owner.getTemperature().tempIncrease(0.2);
        } else {
            System.out.println("반납 가능한 상태가 아닙니다.");
        }
    }

    public void reportProblem(PenaltyPolicy policy) {
        policy.apply(borrower);
        status = RentalStatus.REPORTED;
    }
}