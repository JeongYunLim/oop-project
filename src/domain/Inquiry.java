package domain;

import java.time.LocalDateTime;

public class Inquiry {

    private static int nextId = 1;

    private int inquiryId;
    private String fromUserId;
    private String toUserId;
    private int itemId;
    private String message;
    private LocalDateTime createdAt;

    public Inquiry(String fromUserId, String toUserId, int itemId, String message) {
        this.inquiryId   = nextId++;
        this.fromUserId  = fromUserId;
        this.toUserId    = toUserId;
        this.itemId      = itemId;
        this.message     = message;
        this.createdAt   = LocalDateTime.now();
    }

    public int getInquiryId()       { return inquiryId; }
    public String getFromUserId()   { return fromUserId; }
    public String getToUserId()     { return toUserId; }
    public int getItemId()          { return itemId; }
    public String getMessage()      { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
