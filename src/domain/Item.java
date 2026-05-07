package domain;

import java.time.LocalDateTime;
import state.ItemState;

public class Item {

    private int itemId;
    private String name;
    private String category;
    private String description;
    private int pricePerHour;
    private Location location;
    private TimeSlot timeSlot;
    private String state;
    private String ownerId;
    private LocalDateTime registeredAt;

    public Item(String name, String category, int pricePerHour, Location location, TimeSlot timeSlot, String ownerId) {
        this.name = name;
        this.category = category;
        this.description = "";
        this.pricePerHour = pricePerHour;
        this.location = location;
        this.timeSlot = timeSlot;
        this.ownerId = ownerId;
        this.state = "available";
        this.registeredAt = LocalDateTime.now();
    }

    public Item(String name, String category, String locationText, int price) {
        this.name = name;
        this.category = category;
        this.description = "";
        this.pricePerHour = price;
        this.location = new Location(locationText, "");
        this.timeSlot = null;
        this.ownerId = "guest";
        this.state = "available";
        this.registeredAt = LocalDateTime.now();
    }

    public int getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public int getPricePerHour() {
        return pricePerHour;
    }

    public int getPrice() {
        return pricePerHour;
    }

    public Location getLocation() {
        return location;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public String getState() {
        return state;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPricePerHour(int pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setState(ItemState itemState) {
        if (itemState == null) return;

        String stateName = itemState.getStateName();

        if ("대여 가능".equals(stateName)) {
            this.state = "available";
        } else if ("예약됨".equals(stateName)) {
            this.state = "reserved";
        } else if ("대여 중".equals(stateName)) {
            this.state = "rented";
        }
    }

    public boolean isAvailable() {
        return "available".equals(state);
    }

    public boolean isReserved() {
        return "reserved".equals(state);
    }

    public boolean isRented() {
        return "rented".equals(state);
    }

    public void requestRental() {
        if (isAvailable()) {
            state = "reserved";
        }
    }

    public void startRental() {
        if (isReserved()) {
            state = "rented";
        }
    }

    public void returnItem() {
        if (isRented()) {
            state = "available";
        }
    }

    public String getStateName() {
        if ("available".equals(state)) {
            return "대여 가능";
        } else if ("reserved".equals(state)) {
            return "예약됨";
        } else if ("rented".equals(state)) {
            return "대여 중";
        }
        return state;
    }

    @Override
    public String toString() {
        return String.format(
                "[%d] %s | %s | %d원/시간 | 상태: %s",
                itemId, name, category, pricePerHour, getStateName()
        );
    }
}