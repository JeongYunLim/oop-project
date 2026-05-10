package strategy;

import domain.User;

public abstract class PenaltyPolicy {
    public abstract void apply(User user);
    public abstract String getPenaltyName();
    public abstract double getPenaltyAmount();
}