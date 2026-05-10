package strategy;

import domain.User;

public class LatePenalty extends PenaltyPolicy {

    @Override
    public void apply(User user) {
        user.getTemperature().decrease(0.5);
    }

    @Override
    public String getPenaltyName() {
        return "연체 패널티";
    }

    @Override
    public double getPenaltyAmount() {
        return 0.5;
    }
}