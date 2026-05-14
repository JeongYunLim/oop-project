package domain;

/**
 * 캠퍼스 내 물품 위치를 나타내는 도메인 클래스
 *
 * 
 * - 건물명 + 세부 장소로 위치를 표현
 * - 같은 건물 여부 비교 메서드를 위치 기반 필터링에 사용

 */
public class Location {

    // ── 위치 정보 ────────────────────────────────────
    private String building;    // 건물명 (예: "새빛관", "비마관")
    private String detail;      // 세부 장소 (예: "3층 312호", "1층 로비")
    private String description; // 추가 설명 (예: "엘리베이터 옆", "정문에서 도보 3분")

   

    // ════════════════════════════════════════════════
    //  생성자
    // ════════════════════════════════════════════════

    /** 설명 없는 버전 */
    public Location(String building, String detail) {
        this.building    = building;
        this.detail      = detail;
        this.description = "";
    }

    /** 설명 있는 버전 */
    public Location(String building, String detail, String description) {
        this.building    = building;
        this.detail      = detail;
        this.description = description;
    }


    // ════════════════════════════════════════════════
    //  위치 관련 메서드
    // ════════════════════════════════════════════════

    /** 같은 건물인지 비교 — 위치 기반 필터링에 사용 */ //수정필요함 어떻게 필터처리할지에 따라
    public boolean isSameBuilding(Location other) {
        if (other == null) return false;
        return this.building.equalsIgnoreCase(other.building);
    }


    // ════════════════════════════════════════════════
    //  Getter
    // ════════════════════════════════════════════════

    public String getBuilding()    { return building; }
    public String getDetail()      { return detail; }
    public String getDescription() { return description; }



    // ════════════════════════════════════════════════
    //  toString — 위치 전체 문자열 출렬 / 디버깅용
    // ════════════════════════════════════════════════

    @Override
    public String toString() {
        if (description == null || description.isEmpty()) {
            return building + " " + detail;
        }
        return building + " " + detail + " (" + description + ")";
    }
}
