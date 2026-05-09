package manager;

import transaction.Rental;
import java.util.ArrayList;

public class ReportManager {

    private static ReportManager instance = new ReportManager();
    private ArrayList<Rental> reportedRentals = new ArrayList<>();

    private ReportManager() {}

    public static ReportManager getInstance() { return instance; }

    public void addReport(Rental rental) { reportedRentals.add(rental); }

    public ArrayList<Rental> getReports() { return new ArrayList<>(reportedRentals); }
}
