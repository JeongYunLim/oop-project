package state;

import domain.Item;

public class ItemState {
    public void requestRental(Item item) {}
    public void startRental(Item item) {}
    public void returnItem(Item item) {}
    public String getStateName() { return ""; }
}
