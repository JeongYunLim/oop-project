package strategy;

import domain.User;

public interface PenaltyPolicy {
    void apply(User user);
    String getPenaltyName();
    double getPenaltyAmount();
}
