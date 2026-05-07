 package domain;

public class Temperature {
    private double degree;

    // 생성자: 초기 온도 설정
    public Temperature() {
        this.degree = 36.5;
    }

    // 팀원 코드 기준 온도 증가 메서드
    public void tempIncrease(double amount) {
        this.degree += amount;
    }

    // 팀원 코드 기준 온도 감소 메서드
    public void tempDecrease(double amount) {
        this.degree -= amount;

        if (this.degree < 0) {
            this.degree = 0;
        }
    }

    // 팀원 코드 기준 온도 반환 메서드
    public double getDegree() {
        return degree;
    }

    // 기존 코드 호환용 메서드
    public void increase(double amount) {
        tempIncrease(amount);
    }

    public void decrease(double amount) {
        tempDecrease(amount);
    }

    public double getValue() {
        return getDegree();
    }
}