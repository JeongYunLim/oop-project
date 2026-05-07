package state;

import domain.Item;

public class AvailableState extends ItemState {

    @Override
    public void requestRental(Item item) {
        item.setState(new ReservedState());
    }

    @Override
    public void startRental(Item item) {
        System.out.println("대여 요청이 먼저 필요합니다.");
    }

    @Override
    public void returnItem(Item item) {
        System.out.println("아직 대여 중인 물품이 아닙니다.");
    }

    @Override
    public String getStateName() {
        return "대여 가능";
    }
}
