package app.domain;

public class Temperature {
    private double degree;

    public Temperature() {
        this.degree = 36.5;
    }

    public void increase(double amount) {
        this.degree += amount;
    }

    public void decrease(double amount) {
        this.degree -= amount;
        if (this.degree < 0) this.degree = 0;
    }

    public double getDegree() {
        return degree;
    }
}