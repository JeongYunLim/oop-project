package domain;

public class Temperature {
    private double value;

    public Temperature() {
        this.value = 36.5;
    }

    public void increase(double amount) {
        value += amount;
    }

    public void decrease(double amount) {
        value -= amount;

        if (value < 0) {
            value = 0;
        }
    }

    public double getValue() {
        return value;
    }
}