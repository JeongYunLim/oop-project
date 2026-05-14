package strategy;

import domain.User;

public abstract class PenaltyPolicy {

    // 패널티 적용 방식은 하위 클래스에서 다르게 구현
    public abstract void apply(User user);

    // 패널티 이름 반환
    public abstract String getPenaltyName();

    // 패널티 수치 반환
    public abstract double getPenaltyAmount();
}