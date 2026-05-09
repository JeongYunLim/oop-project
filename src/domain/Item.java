
package domain;

import java.time.LocalDateTime;

/**
 * 공유 물품을 나타내는 도메인 클래스
 *
 * 
 * - 물품의 기본 정보를 캡슐화

  
 */
public class Item {

    // ── 식별자 ──────────────────────────────────────
    private int itemId;               // 물품 고유 ID (ItemManager가 부여)

    // ── 기본 정보 ────────────────────────────────────
    private String name;              // 물품명
    private String category;          // 카테고리 (예: "전자기기", "도서", "생활용품")
    private String description;       // 물품 설명

    // ── 대여 조건 ────────────────────────────────────
    private int pricePerHour;         // 시간당 대여 가격 (원)
    private Location location;        // 물품 위치 (Location 객체)
    private TimeSlot timeSlot;        // 대여 가능 시간대 (TimeSlot 객체)

    // ── 상태 ─────────────────────────────────────────
   
    private ItemState state;             // 현재 상태: "available" / "reserved" / "rented"

    // ── 소유자 ───────────────────────────────────────
   
    private User owner;          

    // ── 등록 시각 ────────────────────────────────────
    private LocalDateTime registeredAt; // 등록 시각 (최신순 정렬에 사용)


    // ════════════════════════════════════════════════
    //  생성자
    // ════════════════════════════════════════════════

    /**
     * 기본 생성자 — 필수 정보만 받아 물품 생성
     * 등록 시 state는 자동으로 "available"
     */
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
        this.state        = new AvailableState();       // 등록 즉시 대여 가능 상태
        this.registeredAt = LocalDateTime.now();
    }


    // ════════════════════════════════════════════════
    //  Getter — 외부에서 직접 필드 접근 금지 (캡슐화)
    // ════════════════════════════════════════════════

    public int getItemId()              { return itemId; }
    public String getName()             { return name; }
    public String getCategory()         { return category; }
    public String getDescription()      { return description; }
    public int getPricePerHour()        { return pricePerHour; }
    public Location getLocation()       { return location; }
    public TimeSlot getTimeSlot()       { return timeSlot; }
    public ItemState getState()            { return state; }
    public User getOwner()          { return owner; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }


    // ════════════════════════════════════════════════
    //  Setter — 수정 가능한 필드만 열어둠
    // ════════════════════════════════════════════════

    /** ItemManager가 ID를 부여할 때만 사용 */
    public void setItemId(int itemId)   { this.itemId = itemId; }

    public void setName(String name)    { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPricePerHour(int pricePerHour)  { this.pricePerHour = pricePerHour; }
    public void setLocation(Location location)     { this.location = location; }
    public void setTimeSlot(TimeSlot timeSlot)     { this.timeSlot = timeSlot; }

    //상태 변경 메서드 
   
     public void setState(ItemState state) {
        this.state = state;
    }

    /** 상태 편의 메서드 — 3번 팀원과 협의 후 사용 */
    public boolean isAvailable() { return this.state instanceof AvailableState; }
    public boolean isReserved()  { return this.state instanceof ReservedState; }
    public boolean isRented()    { return this.state instanceof RentedState; }


    // ════════════════════════════════════════════════
    //  toString — 디버깅/목록 출력용
    // ════════════════════════════════════════════════

    
    public String toString() {
        return String.format(
            "[%d] %s | %s | %d원/시간 | 상태: %s",
            itemId, name, category, pricePerHour, state.getStateName()
        );
    }
}

