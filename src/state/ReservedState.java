package state;

import domain.Item;

public class ReservedState implements ItemState {

    @Override
    public void requestRental(Item item) {
        System.out.println("이미 예약된 물품입니다.");
    }

    @Override
    public void startRental(Item item) {
        item.setState("rented");
    }

    @Override
    public void returnItem(Item item) {
        System.out.println("아직 대여가 시작되지 않았습니다.");
    }

    @Override
    public String getStateName() {
        return "예약됨";
    }
}