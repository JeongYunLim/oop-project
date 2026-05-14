package strategy;

import domain.User;

public class DamagePenalty extends PenaltyPolicy {

    @Override
    public void apply(User user) {
        if (user == null || user.getTemperature() == null) {
            return;
        }

        user.getTemperature().decrease(1.5);
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