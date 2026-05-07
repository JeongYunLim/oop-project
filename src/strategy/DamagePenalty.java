package strategy;

import domain.User;

public class DamagePenalty implements PenaltyPolicy {

    @Override
    public void apply(User user) {
        user.getTemperature().tempDecrease(1.5);
    }

    @Override
    public String getPenaltyName() {
        return "파손 패널티";
    }

    @Override
    public double getPenaltyAmount() {
        return 1.5;
    }
}