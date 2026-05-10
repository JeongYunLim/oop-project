package strategy;

import domain.User;

public class NoReturnPenalty extends PenaltyPolicy {

    @Override
    public void apply(User user) {
        user.getTemperature().decrease(3.0);
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