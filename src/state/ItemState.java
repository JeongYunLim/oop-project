package state;

import domain.Item;

public interface ItemState {
    void requestRental(Item item);
    void startRental(Item item);
    void returnItem(Item item);
    String getStateName();
}