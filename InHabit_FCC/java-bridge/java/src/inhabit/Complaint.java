package inhabit;

import java.util.*;

// Complaint model representing the COMPLAINT table in InHabitDB
public class Complaint {
    private int ticketId;
    private String title;
    private String description;
    private String category;
    private String location;
    private String status;
    private boolean concerning;
    private String createdByPrn;
    private String filedBy;
    private boolean wardenConfirmed;
    private boolean studentConfirmed;
    private String assignedStaffId;
    private String createdAt;
    private int voteCount;

    public Complaint() {
        this.status = "Open";
        this.concerning = false;
        this.wardenConfirmed = false;
        this.studentConfirmed = false;
        this.voteCount = 0;
    }

    public Complaint(int ticketId, String title, String description, String category,
                     String location, String status, boolean concerning,
                     String createdByPrn, String assignedStaffId, String createdAt) {
        this.ticketId = ticketId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.status = status;
        this.concerning = concerning;
        this.createdByPrn = createdByPrn;
        this.assignedStaffId = assignedStaffId;
        this.createdAt = createdAt;
        this.voteCount = 0;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isConcerning() {
        return concerning;
    }

    public void setConcerning(boolean concerning) {
        this.concerning = concerning;
    }

    public String getCreatedByPrn() {
        return createdByPrn;
    }

    public void setCreatedByPrn(String createdByPrn) {
        this.createdByPrn = createdByPrn;
    }

    public String getFiledBy() {
        return filedBy;
    }

    public void setFiledBy(String filedBy) {
        this.filedBy = filedBy;
    }

    public boolean isWardenConfirmed() {
        return wardenConfirmed;
    }

    public void setWardenConfirmed(boolean wardenConfirmed) {
        this.wardenConfirmed = wardenConfirmed;
    }

    public boolean isStudentConfirmed() {
        return studentConfirmed;
    }

    public void setStudentConfirmed(boolean studentConfirmed) {
        this.studentConfirmed = studentConfirmed;
    }

    public String getAssignedStaffId() {
        return assignedStaffId;
    }

    public void setAssignedStaffId(String assignedStaffId) {
        this.assignedStaffId = assignedStaffId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
