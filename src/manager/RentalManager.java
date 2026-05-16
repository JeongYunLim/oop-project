 package manager;

import transaction.Rental;

import java.util.ArrayList;

public class RentalManager {

    private static RentalManager instance = new RentalManager();

    private ArrayList<Rental> rentals = new ArrayList<>();

    private RentalManager() {}

    public static RentalManager getInstance() {
        return instance;
    }

    public void addRental(Rental rental) {
        if (rental == null) {
            return;
        }

        if (!rentals.contains(rental)) {
            rentals.add(rental);
        }
    }

    public ArrayList<Rental> getAllRentals() {
        return new ArrayList<>(rentals);
    }

    public ArrayList<Rental> getRentalsByOwner(String ownerId) {
        ArrayList<Rental> result = new ArrayList<>();

        if (ownerId == null) {
            return result;
        }

        for (Rental rental : rentals) {
            if (rental.getOwner() != null
                    && ownerId.equals(rental.getOwner().getId())) {
                result.add(rental);
            }
        }

        return result;
    }

    public ArrayList<Rental> getRentalsByBorrower(String borrowerId) {
        ArrayList<Rental> result = new ArrayList<>();

        if (borrowerId == null) {
            return result;
        }

        for (Rental rental : rentals) {
            if (rental.getBorrower() != null
                    && borrowerId.equals(rental.getBorrower().getId())) {
                result.add(rental);
            }
        }

        return result;
    }
}