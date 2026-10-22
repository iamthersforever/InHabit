package inhabit;

// Model representing Lost and Found items and Campus Notices
public class LostAndFoundItem {
    private String itemId;
    private String itemName;
    private String description;
    private String locationFound;
    private String itemType;   // "Lost" or "Found"
    private String status;     // "Open" or "Claimed"
    private String reportedByPrn;
    private String reportedAt;

    public LostAndFoundItem() {}

    public LostAndFoundItem(String itemId, String itemName, String description,
                            String locationFound, String itemType, String status,
                            String reportedByPrn, String reportedAt) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.locationFound = locationFound;
        this.itemType = itemType;
        this.status = status;
        this.reportedByPrn = reportedByPrn;
        this.reportedAt = reportedAt;
    }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocationFound() { return locationFound; }
    public void setLocationFound(String locationFound) { this.locationFound = locationFound; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReportedByPrn() { return reportedByPrn; }
    public void setReportedByPrn(String reportedByPrn) { this.reportedByPrn = reportedByPrn; }

    public String getReportedAt() { return reportedAt; }
    public void setReportedAt(String reportedAt) { this.reportedAt = reportedAt; }
}
