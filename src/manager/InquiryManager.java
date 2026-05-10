package manager;

import domain.Inquiry;
import java.util.ArrayList;

public class InquiryManager {

    private static InquiryManager instance = new InquiryManager();
    private ArrayList<Inquiry> inquiries = new ArrayList<>();

    private InquiryManager() {}

    public static InquiryManager getInstance() { return instance; }

    public void addInquiry(Inquiry inquiry) { inquiries.add(inquiry); }

    public ArrayList<Inquiry> getInquiriesByItem(int itemId) {
        ArrayList<Inquiry> result = new ArrayList<>();
        for (Inquiry i : inquiries) {
            if (i.getItemId() == itemId) result.add(i);
        }
        return result;
    }

    public ArrayList<Inquiry> getInquiriesByUser(String userId) {
        ArrayList<Inquiry> result = new ArrayList<>();
        for (Inquiry i : inquiries) {
            if (userId.equals(i.getFromUserId())) result.add(i);
        }
        return result;
    }

    public ArrayList<Inquiry> getInquiriesByOwner(String ownerId) {
        ArrayList<Inquiry> result = new ArrayList<>();
        for (Inquiry i : inquiries) {
            if (ownerId.equals(i.getToUserId())) result.add(i);
        }
        return result;
    }
}
