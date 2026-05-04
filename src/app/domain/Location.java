
package domain;

/**
 * 캠퍼스 내 물품 위치를 나타내는 도메인 클래스
 *
 * 
 * - 건물명 + 세부 장소로 위치를 표현
 * - 같은 건물 여부 비교 메서드를 위치 기반 필터링에 사용
 * - GPS 좌표 확장 여지: latitude, longitude 필드 추가 예정 (지도 연동 시)
 */
public class Location {

    // ── 위치 정보 ────────────────────────────────────
    private String building;    // 건물명 (예: "새빛관", "비마관")
    private String detail;      // 세부 장소 (예: "3층 312호", "1층 로비")
    private String description; // 추가 설명 (예: "엘리베이터 옆", "정문에서 도보 3분")

    // TODO: GPS 좌표 확장 시 아래 필드 추가
    // private double latitude;
    // private double longitude;


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
    //  Getter / Setter
    // ════════════════════════════════════════════════

    public String getBuilding()    { return building; }
    public String getDetail()      { return detail; }
    public String getDescription() { return description; }

    public void setBuilding(String building)       { this.building = building; }
    public void setDetail(String detail)           { this.detail = detail; }
    public void setDescription(String description) { this.description = description; }


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
