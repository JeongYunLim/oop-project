package strategy;

import domain.User;

public class NoReturnPenalty implements PenaltyPolicy {

    @Override
    public void apply(User user) {
        user.getTemperature().tempDecrease(3.0);
    }

    @Override
    public String getPenaltyName() {
        return "미반납 패널티";
    }

    @Override
    public double getPenaltyAmount() {
        return 3.0;
    }
}