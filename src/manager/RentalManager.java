package manager;

import transaction.Rental;
import java.util.ArrayList;

public class RentalManager {

    private static RentalManager instance = new RentalManager();
    private ArrayList<Rental> rentals = new ArrayList<>();

    private RentalManager() {}

    public static RentalManager getInstance() { return instance; }

    public void addRental(Rental rental) { rentals.add(rental); }

    public ArrayList<Rental> getRentalsByOwner(String userId) {
        ArrayList<Rental> result = new ArrayList<>();
        for (Rental r : rentals) {
            if (userId.equals(r.getOwner().getId())) result.add(r);
        }
        return result;
    }

    public ArrayList<Rental> getRentalsByBorrower(String userId) {
        ArrayList<Rental> result = new ArrayList<>();
        for (Rental r : rentals) {
            if (userId.equals(r.getBorrower().getId())) result.add(r);
        }
        return result;
    }

    public ArrayList<Rental> getAllRentals() { return new ArrayList<>(rentals); }
}
