package state;

import domain.Item;

public class RentedState extends ItemState {

    @Override
    public void requestRental(Item item) {
        System.out.println("현재 대여 중인 물품입니다.");
    }

    @Override
    public void startRental(Item item) {
        System.out.println("이미 대여 중입니다.");
    }

    @Override
    public void returnItem(Item item) {
        item.setState(new AvailableState());
    }

    @Override
    public String getStateName() {
        return "대여 중";
    }
}