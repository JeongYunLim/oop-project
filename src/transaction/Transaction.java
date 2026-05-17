package transaction;

import domain.Item;
import domain.User;



public abstract class Transaction {

    protected User owner;
    protected User borrower;
    protected Item item;

    public Transaction(User owner, User borrower, Item item) {
        this.owner = owner;
        this.borrower = borrower;
        this.item = item;
    }

    public abstract void request();

    public abstract void approve();

    public abstract void start();

    public abstract String getStatusText();

    public User getOwner() {
        return owner;
    }

    public User getBorrower() {
        return borrower;
    }

    public Item getItem() {
        return item;
    }


}