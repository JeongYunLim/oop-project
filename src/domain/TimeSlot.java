package domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 물품 대여 가능 시간 구간을 나타내는 클래스
 
 */
public class TimeSlot {

    // ── 시간 구간 ────────────────────────────────────
    private LocalDateTime startTime; // 대여 가능 시작 시각
    private LocalDateTime endTime;   // 대여 종료 시각

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("MM/dd HH:mm");


    // ════════════════════════════════════════════════
    //  생성자
    // ════════════════════════════════════════════════

    /**
     * @throws IllegalArgumentException 종료 시각이 시작 시각보다 앞이거나 같을 때
     */
    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("시작/종료 시각은 null일 수 없습니다.");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("종료 시각은 시작 시각보다 늦어야 합니다.");
        }
        this.startTime = startTime;
        this.endTime   = endTime;
    }


    // ════════════════════════════════════════════════
    //  시간 계산 메서드
    // ════════════════════════════════════════════════

    /** 대여 시간(시간 단위, 절사) — 가격 계산 기준 */
    public int getDurationHours() {
        return (int) ChronoUnit.HOURS.between(startTime, endTime);
    }

    /** 총 대여 금액 = 시간당 가격 × 대여 시간 */
    public int getTotalPrice(int pricePerHour) {
        return getDurationHours() * pricePerHour;
    }


    // ════════════════════════════════════════════════
    //  구간 확인 메서드
    // ════════════════════════════════════════════════

    /** 현재 시각이 대여 가능 구간(시작 이상 ~ 종료 이하)인지 확인 */
    public boolean isAvailableNow() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }

    /**
     * 두 TimeSlot이 겹치는지 확인 — ItemManager의 중복 예약 방지에 사용
     * 끝과 시작이 딱 맞닿는 경우(연속 예약)는 겹치지 않는 것으로 처리
     */
    public boolean overlaps(TimeSlot other) {
        if (other == null) return false;
        return this.startTime.isBefore(other.endTime)
            && other.startTime.isBefore(this.endTime);
    }


    // ════════════════════════════════════════════════
    //  Getter / Setter
    // ════════════════════════════════════════════════

    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime()   { return endTime; }



    // ════════════════════════════════════════════════
    //  toString — UI 표시 및 디버깅용
    // ════════════════════════════════════════════════

    /** 예: "05/03 14:00 ~ 17:00 (3시간)" / "05/03 09:00 ~ 09:30 (30분)" */
    @Override
    public String toString() {
        long totalMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
        long hours   = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        String duration;
        if (minutes == 0) {
            duration = hours + "시간";
        } else if (hours == 0) {
            duration = minutes + "분";
        } else {
            duration = hours + "시간 " + minutes + "분";
        }

        return startTime.format(FMT) + " ~ " + endTime.format(FMT) + " (" + duration + ")";
    }
}
