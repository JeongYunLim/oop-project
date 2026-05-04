package domain;

import state.AvailableState;
import state.ItemState;

public class Item {
    private String name;
    private String category;
    private String location;
    private int price;
    private ItemState state;

    public Item(String name, String category, String location, int price) {
        this.name = name;
        this.category = category;
        this.location = location;
        this.price = price;
        this.state = new AvailableState();
    }

    public void requestRental() {
        state.requestRental(this);
    }

    public void startRental() {
        state.startRental(this);
    }

    public void returnItem() {
        state.returnItem(this);
    }

    public void setState(ItemState state) {
        this.state = state;
    }

    public String getStateName() {
        return state.getStateName();
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public int getPrice() {
        return price;
    }
}