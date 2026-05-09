package domain;

public class Temperature {
    private double degree;

    
    // 생성자: 초기 온도 설정(기본 온도 36.5로 설정)
    public Temperature() {
        this.degree = 36.5;
    }


    // 온도 증가/감소 메서드
    public void tempIncrease(double amount) {
        this.degree += amount;
    }
    public void tempDecrease(double amount) {
        this.degree -= amount;
        if (this.degree < 0) this.degree = 0;
    }

    
    public double getDegree() { return degree; }

    public void increase(double amount) { tempIncrease(amount); }
    public void decrease(double amount) { tempDecrease(amount); }
    public double getValue()            { return getDegree(); }
}