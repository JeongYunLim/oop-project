package manager;

import domain.Item;
import domain.Location;

import java.util.ArrayList;
import java.util.Comparator;

public class ItemManager {

    private static ItemManager instance = new ItemManager();

    private ItemManager() {}

    public static ItemManager getInstance() { return instance; }

    private ArrayList<Item> items = new ArrayList<>();
    private int nextId = 1;

    public void addItem(Item item) {
        item.setItemId(nextId++);
        items.add(item);
    }

    public ArrayList<Item> getItems() {
        return new ArrayList<>(items);
    }

    public Item getItemById(int itemId) {
        for (Item item : items) {
            if (item.getItemId() == itemId) return item;
        }
        return null;
    }

    public boolean removeItem(int itemId) {
        return items.removeIf(item -> item.getItemId() == itemId);
    }

    public ArrayList<Item> searchByName(String keyword) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.getName().contains(keyword)) result.add(item);
        }
        return result;
    }

    public ArrayList<Item> filterByCategory(String category) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.getCategory().equalsIgnoreCase(category)) result.add(item);
        }
        return result;
    }

    public ArrayList<Item> filterByBuilding(String building) {
        Location target = new Location(building, "");
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.getLocation() != null && item.getLocation().isSameBuilding(target))
                result.add(item);
        }
        return result;
    }

    public ArrayList<Item> filterByAvailable() {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.isAvailable()) result.add(item);
        }
        return result;
    }

    public ArrayList<Item> getItemsByOwner(String ownerId) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (ownerId.equals(item.getOwnerId())) result.add(item);
        }
        return result;
    }

    public ArrayList<Item> sortByPrice() {
        ArrayList<Item> result = new ArrayList<>(items);
        result.sort(Comparator.comparingInt(Item::getPricePerHour));
        return result;
    }

    public ArrayList<Item> sortByLatest() {
        ArrayList<Item> result = new ArrayList<>(items);
        result.sort(Comparator.comparing(Item::getRegisteredAt).reversed());
        return result;
    }
}
