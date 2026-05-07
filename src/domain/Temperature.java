package domain;

public class Temperature {
    private double degree;

    public Temperature() {
        this.degree = 36.5;
    }

    public void tempIncrease(double amount) {
        this.degree += amount;
    }

    public void tempDecrease(double amount) {
        this.degree -= amount;
        if (this.degree < 0) {
            this.degree = 0;
        }
    }

    public double getDegree() {
        return degree;
    }

    // 기존 코드와의 호환용 메서드
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