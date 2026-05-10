package state;

import domain.Item;

public abstract class ItemState {
    public abstract void requestRental(Item item);
    public abstract void startRental(Item item);
    public abstract void returnItem(Item item);
    public abstract String getStateName();
}