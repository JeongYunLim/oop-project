package manager;

import domain.Item;
import domain.Location;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * 전체 물품 목록을 관리하는 매니저 클래스

 */
public class ItemManager {


    public ItemManager() {}


    // ── 데이터 저장소 ─────────────────────────────────
    private ArrayList<Item> items = new ArrayList<>();
    private int nextId = 1;  // 물품 등록 시 자동 증가 ID


    // ════════════════════════════════════════════════
    //  기본 CRUD
    // ════════════════════════════════════════════════

    /** 물품 등록 — ID를 자동 부여하고 목록에 추가 */
    public void addItem(Item item) {
        item.setItemId(nextId++);
        items.add(item);
    }

    /** 전체 목록 반환 (원본 보호를 위해 복사본 반환) */
    public ArrayList<Item> getItems() {
        return new ArrayList<>(items);
    }

    /** ID로 단건 조회 — 없으면 null 반환 */
    public Item getItemById(int itemId) {
        for (Item item : items) {
            if (item.getItemId() == itemId) return item;
        }
        return null;
    }

    /** 물품 삭제 — 삭제 성공 여부 반환 */
    public boolean removeItem(int itemId) {
        return items.removeIf(item -> item.getItemId() == itemId);
    }


    // ════════════════════════════════════════════════
    //  검색 / 필터
    // ════════════════════════════════════════════════

    /** 이름 부분 일치 검색 */
    public ArrayList<Item> searchByName(String keyword) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.getName().contains(keyword)) {
                result.add(item);
            }
        }
        return result;
    }

    /** 카테고리 필터 (대소문자 무시) */
    public ArrayList<Item> filterByCategory(String category) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 건물 위치 필터
     * Location.isSameBuilding()을 활용해 대소문자 무시 비교
     */
    public ArrayList<Item> filterByBuilding(String building) {
        Location target = new Location(building, "");
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.getLocation() != null && item.getLocation().isSameBuilding(target)) {
                result.add(item);
            }
        }
        return result;
    }

    /** 대여 가능한 물품만 필터 */
    public ArrayList<Item> filterByAvailable() {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (item.isAvailable()) {
                result.add(item);
            }
        }
        return result;
    }


    // ════════════════════════════════════════════════
    //  정렬
    // ════════════════════════════════════════════════

    /** 가격 낮은 순 정렬 */
    public ArrayList<Item> sortByPrice() {
        ArrayList<Item> result = new ArrayList<>(items);
        result.sort(Comparator.comparingInt(Item::getPricePerHour));
        return result;
    }

    /** 최신 등록순 정렬 (registeredAt 내림차순) */
    public ArrayList<Item> sortByLatest() {
        ArrayList<Item> result = new ArrayList<>(items);
        result.sort(Comparator.comparing(Item::getRegisteredAt).reversed());
        return result;
    }
}
