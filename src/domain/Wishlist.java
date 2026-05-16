package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wishlist {

    private static Wishlist instance = new Wishlist();
    private Map<String, ArrayList<Item>> wishMap = new HashMap<>();

    private Wishlist() {}

    public static Wishlist getInstance() { return instance; }

    public void toggle(User user, Item item) {
        String userId = user.getId();
        wishMap.putIfAbsent(userId, new ArrayList<>());
        ArrayList<Item> list = wishMap.get(userId);
        for (Item i : list) {
            if (i.getItemId() == item.getItemId()) {
                list.remove(i);
                return;
            }
        }
        list.add(item);
    }

    public boolean isWished(User user, Item item) {
        ArrayList<Item> list = wishMap.get(user.getId());
        if (list == null) return false;
        for (Item i : list) {
            if (i.getItemId() == item.getItemId()) return true;
        }
        return false;
    }

    public ArrayList<Item> getWishlist(User user) {
        return wishMap.getOrDefault(user.getId(), new ArrayList<>());
    }
}
