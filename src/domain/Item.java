package domain;

import java.time.LocalDateTime;
import state.AvailableState;
import state.ItemState;
import state.RentedState;
import state.ReservedState;

public class Item {

    private int itemId;
    private String name;
    private String category;
    private String description;
    private int pricePerHour;
    private Location location;
    private TimeSlot timeSlot;
    private ItemState state;
    private User owner;
    private LocalDateTime registeredAt;

    public Item(String name, String category,
                int pricePerHour, Location location, TimeSlot timeSlot,
                User owner) {
        this.name         = name;
        this.category     = category;
        this.description  = "";
        this.pricePerHour = pricePerHour;
        this.location     = location;
        this.timeSlot     = timeSlot;
        this.owner        = owner;
        this.state        = new AvailableState();
        this.registeredAt = LocalDateTime.now();
    }

    public void requestRental() { state.requestRental(this); }
    public void startRental()   { state.startRental(this); }
    public void returnItem()    { state.returnItem(this); }
    

    public boolean isAvailable() { return this.state instanceof AvailableState; }
    public boolean isReserved()  { return this.state instanceof ReservedState; }
    public boolean isRented()    { return this.state instanceof RentedState; }

    public void setItemId(int itemId)                      { this.itemId = itemId; }
    public void setDescription(String description)         { this.description = description; }
    public void setState(ItemState state)   { this.state = state; }

    public String getStateName()            { return state.getStateName(); }
    public int getItemId()                   { return itemId; }
    public String getName()                  { return name; }
    public String getCategory()              { return category; }
    public String getDescription()           { return description; }
    public int getPricePerHour()             { return pricePerHour; }
    public Location getLocation()            { return location; }
    public TimeSlot getTimeSlot()            { return timeSlot; }
    public User getOwner()                   { return owner; }
    public String getOwnerId()               { return owner != null ? owner.getId() : ""; }
    public LocalDateTime getRegisteredAt()   { return registeredAt; }

    @Override
    public String toString() {
        return String.format(
            "[%d] %s | %s | %d원/시간 | 상태: %s",
            itemId, name, category, pricePerHour, state.getStateName()
        );
    }
}
