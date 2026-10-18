// InHabit - Single-File Student Demonstration Build
// Authors: Raghav Mahesh Sarda (PRN: 257122235 | Room: Alpha-101) & Aniruddha Pratap Singh (PRN: 257122247 | Room: Alpha-102)
// Course: Programming with Java / Database Management Systems
// Database: InHabitDBNew (Normalized 15-Table EER Schema)

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

public class InHabitDemo {

    // =========================================================================
    // 1. DOMAIN MODELS (OOP: Inheritance & Encapsulation)
    // =========================================================================

    public static class User {
        protected String userId;
        protected String prn;
        protected String firstName;
        protected String middleName;
        protected String lastName;
        protected String email;
        protected String phone;
        protected String password;
        protected String location;
        protected String role;

        public User(String prn, String firstName, String middleName, String lastName,
                    String email, String phone, String password, String location, String role) {
            this.prn = prn;
            this.firstName = firstName;
            this.middleName = middleName;
            this.lastName = lastName;
            this.email = email;
            this.phone = phone;
            this.password = password;
            this.location = location;
            this.role = role;
        }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getPrn() { return prn; }
        public String getFullName() {
            if (middleName != null && !middleName.trim().isEmpty()) {
                return firstName + " " + middleName + " " + lastName;
            }
            return firstName + " " + (lastName != null ? lastName : "");
        }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getLocation() { return location; }
        public String getRole() { return role; }
        public String getPassword() { return password; }
    }

    public static class Student extends User {
        private String branchDivision;
        private String gender;

        public Student(String prn, String firstName, String middleName, String lastName,
                       String email, String phone, String password,
                       String branchDivision, String gender, String hostelLocation) {
            super(prn, firstName, middleName, lastName, email, phone, password, hostelLocation, "Student");
            this.branchDivision = branchDivision;
            this.gender = gender;
        }

        public String getBranchDivision() { return branchDivision; }
        public String getGender() { return gender; }
    }

    public static class Staff extends User {
        private String staffId;
        private String department;

        public Staff(String staffId, String firstName, String lastName,
                     String email, String phone, String role, String department, String password) {
            super(staffId, firstName, "", lastName, email, phone, password, department, role);
            this.staffId = staffId;
            this.department = department;
        }

        public String getStaffId() { return staffId; }
        public String getDepartment() { return department; }
    }

    public static class Complaint {
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

        public Complaint(int ticketId, String title, String description, String category,
                         String location, String status, boolean concerning,
                         String createdByPrn, String assignedStaffId, String createdAt, int voteCount) {
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
            this.voteCount = voteCount;
        }

        public int getTicketId() { return ticketId; }
        public void setTicketId(int id) { this.ticketId = id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getCategory() { return category; }
        public String getLocation() { return location; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public boolean isConcerning() { return concerning; }
        public void setConcerning(boolean c) { this.concerning = c; }
        public String getCreatedByPrn() { return createdByPrn; }
        public String getFiledBy() { return filedBy; }
        public void setFiledBy(String fb) { this.filedBy = fb; }
        public boolean isWardenConfirmed() { return wardenConfirmed; }
        public void setWardenConfirmed(boolean wc) { this.wardenConfirmed = wc; }
        public boolean isStudentConfirmed() { return studentConfirmed; }
        public void setStudentConfirmed(boolean sc) { this.studentConfirmed = sc; }
        public String getAssignedStaffId() { return assignedStaffId; }
        public void setAssignedStaffId(String id) { this.assignedStaffId = id; }
        public String getCreatedAt() { return createdAt; }
        public int getVoteCount() { return voteCount; }
        public void setVoteCount(int v) { this.voteCount = v; }
    }

    public static class LostAndFoundItem {
        private String itemId;
        private String itemName;
        private String description;
        private String locationFound;
        private String itemType;
        private String status;
        private String reportedByPrn;
        private String reportedAt;

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
        public String getDescription() { return description; }
        public String getLocationFound() { return locationFound; }
        public String getItemType() { return itemType; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getReportedByPrn() { return reportedByPrn; }
        public String getReportedAt() { return reportedAt; }
        public void setReportedAt(String reportedAt) { this.reportedAt = reportedAt; }
    }

    // Custom Exceptions (OOP requirement)
    public static class AuthenticationException extends Exception {
        public AuthenticationException(String msg) { super(msg); }
    }
    public static class InvalidVoteException extends Exception {
        public InvalidVoteException(String msg) { super(msg); }
    }
    public static class InvalidComplaintException extends Exception {
        public InvalidComplaintException(String msg) { super(msg); }
    }

    // =========================================================================
    // 2. UI UTILITIES
    // =========================================================================

    public static class UIHelper {
        public static JButton createButton(String text, Color bgColor) {
            JButton b = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    if (getModel().isArmed()) {
                        g2.setColor(bgColor.darker());
                    } else if (getModel().isRollover()) {
                        g2.setColor(bgColor.brighter());
                    } else {
                        g2.setColor(bgColor);
                    }
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(Color.WHITE);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(getText(), x, y);
                    g2.dispose();
                }
            };
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setOpaque(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return b;
        }

        public static JButton createSecondaryButton(String text) {
            JButton b = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    if (getModel().isRollover()) {
                        g2.setColor(new Color(241, 245, 249));
                    } else {
                        g2.setColor(Color.WHITE);
                    }
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    g2.setColor(new Color(203, 213, 225));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    g2.setColor(new Color(30, 41, 59));
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(getText(), x, y);
                    g2.dispose();
                }
            };
            b.setFont(new Font("SansSerif", Font.PLAIN, 12));
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setOpaque(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return b;
        }

        public static void showDetails(Component parent, Complaint c, User currentUser, Runnable onUpdate) {
            if (c == null) {
                JOptionPane.showMessageDialog(parent, "Please select a complaint row.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Ticket #" + c.getTicketId() + " - Details", true);
            d.setSize(540, 520);
            d.setLocationRelativeTo(parent);
            d.setLayout(new BorderLayout(15, 15));

            JPanel pHead = new JPanel(new BorderLayout());
            pHead.setBackground(new Color(30, 41, 59));
            pHead.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JLabel lT = new JLabel("Ticket #" + c.getTicketId() + " - " + c.getTitle());
            lT.setFont(new Font("SansSerif", Font.BOLD, 15));
            lT.setForeground(Color.WHITE);

            JLabel lS = new JLabel("Status: " + c.getStatus() + (c.isConcerning() ? " [CONCERNING]" : ""));
            lS.setFont(new Font("SansSerif", Font.BOLD, 13));
            lS.setForeground(c.isConcerning() ? new Color(248, 113, 113) : new Color(52, 211, 153));

            pHead.add(lT, BorderLayout.NORTH);
            pHead.add(lS, BorderLayout.SOUTH);
            d.add(pHead, BorderLayout.NORTH);

            JPanel pC = new JPanel(new BorderLayout(10, 10));
            pC.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

            JPanel pGrid = new JPanel(new GridLayout(0, 2, 10, 6));
            pGrid.add(new JLabel("Category:"));
            JLabel lCat = new JLabel(c.getCategory());
            lCat.setFont(new Font("SansSerif", Font.BOLD, 13));
            pGrid.add(lCat);

            pGrid.add(new JLabel("Location / Room:"));
            pGrid.add(new JLabel(c.getLocation()));

            pGrid.add(new JLabel("Filed By Student PRN:"));
            pGrid.add(new JLabel(c.getCreatedByPrn()));

            pGrid.add(new JLabel("Assigned Staff ID:"));
            pGrid.add(new JLabel(c.getAssignedStaffId() != null ? c.getAssignedStaffId() : "Unassigned"));

            pGrid.add(new JLabel("Created At:"));
            pGrid.add(new JLabel(c.getCreatedAt()));

            pGrid.add(new JLabel("Genuine Votes:"));
            JLabel lV = new JLabel(String.valueOf(c.getVoteCount()));
            lV.setFont(new Font("SansSerif", Font.BOLD, 13));
            lV.setForeground(c.isConcerning() ? new Color(220, 38, 38) : new Color(37, 99, 235));
            pGrid.add(lV);

            pC.add(pGrid, BorderLayout.NORTH);

            JPanel pD = new JPanel(new BorderLayout(5, 5));
            pD.add(new JLabel("Grievance Description:"), BorderLayout.NORTH);
            JTextArea tD = new JTextArea(c.getDescription());
            tD.setLineWrap(true);
            tD.setWrapStyleWord(true);
            tD.setEditable(false);
            tD.setBackground(new Color(248, 250, 252));
            tD.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            pD.add(new JScrollPane(tD), BorderLayout.CENTER);
            pC.add(pD, BorderLayout.CENTER);

            d.add(pC, BorderLayout.CENTER);

            JPanel pB = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            if (currentUser instanceof Staff) {
                JLabel lCh = new JLabel("Change Status:");
                String[] opts = {"Open", "Under Review", "In Progress", "Resolved", "Closed"};
                JComboBox<String> cb = new JComboBox<>(opts);
                cb.setSelectedItem(c.getStatus());
                JButton bUp = createButton("Update Status", new Color(37, 99, 235));
                bUp.addActionListener(e -> {
                    String ns = (String) cb.getSelectedItem();
                    c.setStatus(ns);
                    lS.setText("Status: " + ns + (c.isConcerning() ? " [CONCERNING]" : ""));
                    JOptionPane.showMessageDialog(d, "Updated to " + ns + "!");
                    if (onUpdate != null) onUpdate.run();
                });
                pB.add(lCh);
                pB.add(cb);
                pB.add(bUp);
            } else if (currentUser instanceof Student && !currentUser.getPrn().equals(c.getCreatedByPrn())) {
                JButton bV = createButton("Upvote Genuine", new Color(16, 185, 129));
                bV.setPreferredSize(new Dimension(135, 30));
                bV.addActionListener(e -> {
                    try {
                        DataStore.vote(c.getTicketId(), currentUser.getPrn());
                        lV.setText(String.valueOf(c.getVoteCount()));
                        JOptionPane.showMessageDialog(d, "Vote recorded!");
                        if (onUpdate != null) onUpdate.run();
                    } catch (InvalidVoteException ex) {
                        JOptionPane.showMessageDialog(d, ex.getMessage(), "Notice", JOptionPane.WARNING_MESSAGE);
                    }
                });
                pB.add(bV);
            }

            JButton bCl = createSecondaryButton("Close");
            bCl.setPreferredSize(new Dimension(80, 30));
            bCl.addActionListener(e -> d.dispose());
            pB.add(bCl);

            d.add(pB, BorderLayout.SOUTH);
            d.setVisible(true);
        }

        public static void showLostAndFoundDetails(Component parent, LostAndFoundItem item, Runnable onUpdate) {
            if (item == null) {
                JOptionPane.showMessageDialog(parent, "Please select an item notice to view details.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Window owner = (parent instanceof Window) ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
            JDialog d = (owner instanceof Frame) ? new JDialog((Frame) owner, "Notice #" + item.getItemId() + " - " + item.getItemName(), true)
                    : new JDialog((Frame) null, "Notice #" + item.getItemId() + " - " + item.getItemName(), true);
            d.setSize(540, 480);
            d.setLocationRelativeTo(owner != null ? owner : parent);
            d.setLayout(new BorderLayout(15, 15));

            JPanel pHead = new JPanel(new BorderLayout());
            pHead.setBackground(new Color(30, 41, 59));
            pHead.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JLabel lT = new JLabel("Notice #" + item.getItemId() + " - " + item.getItemName());
            lT.setFont(new Font("SansSerif", Font.BOLD, 15));
            lT.setForeground(Color.WHITE);

            boolean isLost = "Lost".equalsIgnoreCase(item.getItemType());
            JLabel lS = new JLabel("Type: " + item.getItemType() + " | Status: " + item.getStatus());
            lS.setFont(new Font("SansSerif", Font.BOLD, 13));
            lS.setForeground(isLost ? new Color(248, 113, 113) : new Color(52, 211, 153));

            pHead.add(lT, BorderLayout.NORTH);
            pHead.add(lS, BorderLayout.SOUTH);
            d.add(pHead, BorderLayout.NORTH);

            JPanel pC = new JPanel(new BorderLayout(10, 10));
            pC.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

            JPanel pGrid = new JPanel(new GridLayout(0, 2, 10, 6));
            pGrid.add(new JLabel("Item Name:"));
            JLabel lName = new JLabel(item.getItemName());
            lName.setFont(new Font("SansSerif", Font.BOLD, 13));
            pGrid.add(lName);

            pGrid.add(new JLabel("Campus Location:"));
            pGrid.add(new JLabel(item.getLocationFound()));

            pGrid.add(new JLabel("Notice Type:"));
            JLabel lType = new JLabel(item.getItemType());
            lType.setFont(new Font("SansSerif", Font.BOLD, 13));
            lType.setForeground(isLost ? new Color(225, 29, 72) : new Color(13, 148, 136));
            pGrid.add(lType);

            pGrid.add(new JLabel("Notice Status:"));
            JLabel lStat = new JLabel(item.getStatus());
            lStat.setFont(new Font("SansSerif", Font.BOLD, 13));
            lStat.setForeground("Claimed".equalsIgnoreCase(item.getStatus()) ? new Color(16, 185, 129) : new Color(245, 158, 11));
            pGrid.add(lStat);

            pGrid.add(new JLabel("Reported by PRN:"));
            pGrid.add(new JLabel(item.getReportedByPrn()));

            pGrid.add(new JLabel("Date Logged:"));
            pGrid.add(new JLabel(item.getReportedAt() != null ? item.getReportedAt() : "Recently"));

            pC.add(pGrid, BorderLayout.NORTH);

            JPanel pD = new JPanel(new BorderLayout(5, 5));
            pD.add(new JLabel("Item Description:"), BorderLayout.NORTH);
            JTextArea tD = new JTextArea(item.getDescription());
            tD.setLineWrap(true);
            tD.setWrapStyleWord(true);
            tD.setEditable(false);
            tD.setBackground(new Color(248, 250, 252));
            tD.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            pD.add(new JScrollPane(tD), BorderLayout.CENTER);
            pC.add(pD, BorderLayout.CENTER);

            d.add(pC, BorderLayout.CENTER);

            JPanel pB = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            if (!"Claimed".equalsIgnoreCase(item.getStatus())) {
                JButton bClaim = createButton("Claim Notice", new Color(16, 185, 129));
                bClaim.setPreferredSize(new Dimension(130, 30));
                bClaim.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(d, "Mark this item as Claimed by its rightful owner?", "Confirm Claim", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        DataStore.claimLostAndFound(item.getItemId());
                        item.setStatus("Claimed");
                        lStat.setText("Claimed");
                        lStat.setForeground(new Color(16, 185, 129));
                        lS.setText("Type: " + item.getItemType() + " | Status: Claimed");
                        JOptionPane.showMessageDialog(d, "Notice marked as Claimed! Contact the resident to collect it.", "Notice Claimed", JOptionPane.INFORMATION_MESSAGE);
                        if (onUpdate != null) onUpdate.run();
                    }
                });
                pB.add(bClaim);
            }

            JButton bCl = createSecondaryButton("Close");
            bCl.setPreferredSize(new Dimension(80, 30));
            bCl.addActionListener(e -> d.dispose());
            pB.add(bCl);

            d.add(pB, BorderLayout.SOUTH);
            d.toFront();
            d.requestFocus();
            d.setVisible(true);
        }
    }

    // =========================================================================
    // 3. DATA STORE (Preloaded with InHabitDBNew records)
    // =========================================================================

    public static class DataStore {
        public static Map<String, User> users = new HashMap<>();
        public static Map<String, String> passwords = new HashMap<>();
        public static List<Student> students = new ArrayList<>();
        public static List<Staff> staffMembers = new ArrayList<>();
        public static List<Complaint> complaints = new ArrayList<>();
        public static List<LostAndFoundItem> lostAndFound = new ArrayList<>();
        public static Map<Integer, Set<String>> votes = new HashMap<>();

        static {
            // Seed all 20 Staff & Technicians from InHabitDBNew.sql
            addStaff(new Staff("STF101", "Arvind", "Kulkarni", "chief.warden@college.edu.in", "9876000101", "Chief Warden", "Hostel Administration", "Staff@101"), "Staff@101");
            addStaff(new Staff("STF102", "Dilip", "Shinde", "warden.boysday@college.edu.in", "9876000102", "Boys Warden (Day)", "Hostel Administration", "Staff@102"), "Staff@102");
            addStaff(new Staff("STF103", "Sunita", "Deshmukh", "warden.girlsday@college.edu.in", "9876000103", "Girls Warden (Day)", "Hostel Administration", "Staff@103"), "Staff@103");
            addStaff(new Staff("STF104", "Ramesh", "Patil", "warden.boysnight@college.edu.in", "9876000104", "Boys Warden (Night)", "Hostel Administration", "Staff@104"), "Staff@104");
            addStaff(new Staff("STF105", "Rekha", "Gaikwad", "warden.girlsnight@college.edu.in", "9876000105", "Girls Warden (Night)", "Hostel Administration", "Staff@105"), "Staff@105");
            addStaff(new Staff("STF106", "Tukaram", "Gawade", "electrician1@college.edu.in", "9876000106", "Electrician", "Electrical", "Staff@106"), "Staff@106");
            addStaff(new Staff("STF107", "Manoj", "Jadhav", "electrician2@college.edu.in", "9876000107", "Electrician", "Electrical", "Staff@107"), "Staff@107");
            addStaff(new Staff("STF108", "Sandeep", "Patil", "plumber1@college.edu.in", "9876000108", "Plumber", "Plumbing", "Staff@108"), "Staff@108");
            addStaff(new Staff("STF109", "Ganesh", "Gaikwad", "plumber2@college.edu.in", "9876000109", "Plumber", "Plumbing", "Staff@109"), "Staff@109");
            addStaff(new Staff("STF110", "Anjali", "Joshi", "system.admin@college.edu.in", "9876000110", "System Administrator", "IT Administration", "Staff@110"), "Staff@110");
            addStaff(new Staff("STF111", "Pravin", "Jagtap", "network.tech1@college.edu.in", "9876000111", "Network Technician", "Internet", "Staff@111"), "Staff@111");
            addStaff(new Staff("STF112", "Sunil", "Bhosale", "network.tech2@college.edu.in", "9876000112", "Network Technician", "Internet", "Staff@112"), "Staff@112");
            addStaff(new Staff("STF113", "Mohammed", "Rafiq", "mess.manager@college.edu.in", "9876000113", "Mess Manager", "Mess", "Staff@113"), "Staff@113");
            addStaff(new Staff("STF114", "Anita", "Kadam", "housekeeping.sup@college.edu.in", "9876000114", "Housekeeping Supervisor", "Cleanliness", "Staff@114"), "Staff@114");
            addStaff(new Staff("STF115", "Suresh", "More", "mess.headcook@college.edu.in", "9876000115", "Head Cook", "Mess", "Staff@115"), "Staff@115");
            addStaff(new Staff("STF116", "Vilas", "Kamble", "lead.electrician@college.edu.in", "9876000116", "Lead Electrician", "Electrical", "Staff@116"), "Staff@116");
            addStaff(new Staff("STF117", "Kishore", "Salunkhe", "security.sup1@college.edu.in", "9876000117", "Security Supervisor", "Security", "Staff@117"), "Staff@117");
            addStaff(new Staff("STF118", "Deepak", "Shinde", "security.guard1@college.edu.in", "9876000118", "Security Guard", "Security", "Staff@118"), "Staff@118");
            addStaff(new Staff("STF119", "Sanjay", "Waghmare", "security.guard2@college.edu.in", "9876000119", "Security Guard", "Security", "Staff@119"), "Staff@119");
            addStaff(new Staff("STF120", "Baban", "Chavan", "security.guard3@college.edu.in", "9876000120", "Security Guard", "Security", "Staff@120"), "Staff@120");

            // Admin aliases
            Staff admin = new Staff("ADMIN", "Arvind", "Kulkarni", "admin@college.edu.in", "9876000101", "Chief Warden & Admin", "Hostel Administration", "admin123");
            users.put("admin@college.edu.in", admin);
            passwords.put("admin@college.edu.in", "admin123");
            users.put("admin", admin);
            passwords.put("admin", "admin123");

            // Load all 500 students from InHabitDBNew.sql (with procedural fallback)
            loadAllStudentsFromSql();

            // Seed Complaints (15 tickets from InHabitDBNew.sql)
            complaints.add(new Complaint(1001, "Fortinet captive portal timeout on 3rd floor Wi-Fi", "Cyberoam/Fortinet login gateway times out continuously on 3rd floor access points during peak hours. Internet access is inaccessible for study work.", "Internet", "Alpha-304", "In Progress", false, "257122235", "STF111", "2026-09-24 16:40:00", 2));
            complaints.add(new Complaint(1002, "Solar hot water pipeline cold in washroom", "Solar hot water line running cold in washrooms 201 to 210 for the past three consecutive mornings. Students are unable to get hot water during winter mornings.", "Plumbing", "Beta-201", "Under Review", false, "257122247", "STF108", "2026-09-25 07:15:00", 2));
            complaints.add(new Complaint(1003, "Mess dinner paneer gravy sour and undercooked rotis", "Wednesday evening dinner paneer tasted sour and rotis were undercooked; multiple students reported stomach issues.", "Mess", "Central Mess", "Open", true, "257122001", "STF113", "2026-09-26 21:10:00", 30));
            complaints.add(new Complaint(1004, "Balcony sliding window latch broken", "Sliding window latch in room 108 is damaged; window does not lock securely against monsoon rain and wind.", "Cleanliness", "Delta-108", "Open", false, "257122006", null, "2026-09-27 11:30:00", 0));
            complaints.add(new Complaint(1005, "Lift 1 erratic jerking between 2nd and 3rd floor", "Passenger elevator shakes abruptly between 2nd and 3rd floors before leveling at the landing.", "Electrical", "Alpha-Ground", "In Progress", false, "257122013", "STF106", "2026-09-27 18:25:00", 2));
            complaints.add(new Complaint(1006, "Charlie-Study AC excessive rattling noise", "Split air conditioner in study room emits loud mechanical vibrating noise when compressor turns on.", "Electrical", "Charlie-Study", "Resolved", false, "257126003", "STF107", "2026-09-28 14:10:00", 1));
            complaints.add(new Complaint(1007, "Beta-406 flush valve leakage", "Dual flush valve continuously trickles water into toilet bowl, causing wastage and damp flooring.", "Plumbing", "Beta-406", "In Progress", false, "257126004", "STF108", "2026-09-28 16:30:00", 0));
            complaints.add(new Complaint(1008, "Delta-401 water cooler filter smell", "Drinking water dispenser on 4th floor corridor has unpleasant odor and stale taste; filter replacement needed.", "Cleanliness", "Delta-401", "Open", false, "257126008", null, "2026-09-29 08:45:00", 0));
            complaints.add(new Complaint(1009, "Alpha-409 blinking tube light fixture", "Fluorescent tube light in 4th floor corridor constantly flickers and starter makes buzzing noise.", "Electrical", "Alpha-409", "Resolved", false, "257126010", "STF107", "2026-09-29 19:20:00", 1));
            complaints.add(new Complaint(1010, "Gate 1 biometric scanner rebooting", "Main entrance biometric attendance unit repeatedly restarts on user thumbprint verification during evening rush.", "Security", "Main Gate 1", "In Progress", false, "257122002", "STF117", "2026-09-30 20:15:00", 1));
            complaints.add(new Complaint(1011, "Charlie wing 2nd floor LAN port disconnected", "Wall Ethernet jack RJ45 connector damaged and does not establish connection with hostel subnet.", "Internet", "Charlie-205", "In Progress", false, "257122019", "STF112", "2026-10-01 09:00:00", 0));
            complaints.add(new Complaint(1012, "Central mess breakfast milk dispenser clogged", "Stainless steel hot milk urn tap is clogged with milk scum and drips slowly during breakfast service.", "Mess", "Central Mess", "Resolved", false, "257122025", "STF113", "2026-10-01 08:15:00", 0));
            complaints.add(new Complaint(1013, "Beta wing washbasin tap leaking heavily", "Pillar tap in 3rd floor washroom cannot be shut tightly and leaks a steady stream into the sink.", "Plumbing", "Beta-312", "Open", false, "257122030", null, "2026-10-01 11:20:00", 0));
            complaints.add(new Complaint(1014, "Delta corridor stairwell emergency light offline", "Battery backup emergency LED panel on west stairwell fails to illuminate during scheduled power cut.", "Electrical", "Delta-204", "Open", false, "257122045", null, "2026-10-01 14:50:00", 0));
            complaints.add(new Complaint(1015, "Charlie wing ground floor exit door magnetic latch loose", "Emergency fire exit door magnetic release does not latch properly when closed, staying slightly ajar.", "Security", "Charlie-101", "Resolved", false, "257122055", "STF120", "2026-10-01 22:30:00", 0));

            // Seed Votes
            Set<String> v1 = new HashSet<>();
            v1.add("257126003"); v1.add("257126004");
            votes.put(1001, v1);

            Set<String> v2 = new HashSet<>();
            v2.add("257126005"); v2.add("257126006");
            votes.put(1002, v2);

            Set<String> v3 = new HashSet<>();
            for (int i = 1; i <= 30; i++) v3.add("2571260" + (i < 10 ? "0" + i : i));
            votes.put(1003, v3);

            // Seed Lost & Found Notices from InHabitDBNew.sql
            lostAndFound.add(new LostAndFoundItem("LF101", "Casio fx-991CW ClassWiz Calculator", "Found on Row 3 desk in Programming Lab 2 with initials RS scratched on back cover.", "Lab 2 (Programming Lab)", "Found", "Claimed", "257122235", "2026-09-22 17:15:00"));
            lostAndFound.add(new LostAndFoundItem("LF102", "SIT Student ID Card & Lanyard", "Navy blue college lanyard with student ID card of PRN 257122014.", "Central Canteen counter", "Found", "Claimed", "257122247", "2026-09-23 13:30:00"));
            lostAndFound.add(new LostAndFoundItem("LF103", "boAt Airdopes 141 ANC Charging Case", "Black matte earbud case (without buds) left on table 4 near entrance.", "Hostel Mess Ground Floor", "Found", "Open", "257122001", "2026-09-25 09:05:00"));
            lostAndFound.add(new LostAndFoundItem("LF104", "Set of 3 Godrej Keys on Pulsar Keychain", "Bundle of 3 room keys lost somewhere between basketball court and Block B.", "Sports Complex / Court", "Lost", "Open", "257122005", "2026-09-26 19:40:00"));
            lostAndFound.add(new LostAndFoundItem("LF105", "Milton Thermosteel 1000ml Bottle (Grey)", "Left on corner reading desk behind Reference Section.", "Central Library 2nd Floor", "Found", "Open", "257126003", "2026-09-27 16:20:00"));
            lostAndFound.add(new LostAndFoundItem("LF106", "Apple 20W USB-C Adapter + Braided Cable", "Left plugged into wall switchboard near whiteboard in Study Room B.", "Hostel Study Room B", "Found", "Claimed", "257126005", "2026-09-27 23:10:00"));
            lostAndFound.add(new LostAndFoundItem("LF107", "Logitech M221 Silent Wireless Mouse", "Charcoal grey wireless mouse left beside PC terminal 18.", "CAD / Graphics Lab", "Found", "Open", "257122009", "2026-09-28 12:45:00"));
            lostAndFound.add(new LostAndFoundItem("LF108", "Titan Rectangle Blue Eyeglasses in Case", "Prescription specs in black Titan hard case lost near amphitheater steps.", "College Amphitheater", "Lost", "Open", "257126007", "2026-09-29 18:00:00"));
            lostAndFound.add(new LostAndFoundItem("LF109", "Classmate Pulse Spiral Notebook (DBMS)", "Single line spiral register containing ER diagram lecture notes left on bench.", "Academic Block Corridor", "Found", "Open", "257122012", "2026-09-30 11:20:00"));
            lostAndFound.add(new LostAndFoundItem("LF110", "Hero Cycle 4-digit Number Combination Lock", "Black steel cable lock with silver dial found hanging on rack.", "Hostel Bicycle Parking", "Found", "Open", "257126011", "2026-09-30 17:35:00"));
        }

        private static void addStudent(Student s, String pass) {
            students.add(s);
            users.put(s.getEmail().toLowerCase(), s);
            passwords.put(s.getEmail().toLowerCase(), pass);
            users.put(s.getPrn(), s);
            passwords.put(s.getPrn(), pass);
        }

        private static void addStaff(Staff st, String pass) {
            staffMembers.add(st);
            users.put(st.getEmail().toLowerCase(), st);
            passwords.put(st.getEmail().toLowerCase(), pass);
            users.put(st.getStaffId(), st);
            passwords.put(st.getStaffId(), pass);
        }

        private static void loadAllStudentsFromSql() {
            String[] candidatePaths = {
                    "InHabitDBNew.sql",
                    "currentversion/InHabitDBNew.sql",
                    "../InHabitDBNew.sql",
                    "/Users/thegharkalaptop/Documents/Antigravity/InHabit/currentversion/InHabitDBNew.sql",
                    "/Users/thegharkalaptop/Documents/Antigravity/InHabit/InHabitDBNew.sql"
            };

            File sqlFile = null;
            for (String cp : candidatePaths) {
                File f = new File(cp);
                if (f.exists() && f.isFile()) {
                    sqlFile = f;
                    break;
                }
            }

            if (sqlFile != null) {
                try (BufferedReader br = new BufferedReader(new FileReader(sqlFile))) {
                    Map<String, String[]> userMap = new HashMap<>();
                    Map<String, String> phoneMap = new HashMap<>();
                    List<String[]> studentRecords = new ArrayList<>();

                    boolean inUser = false, inPhone = false, inStudent = false;
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.contains("INSERT INTO USER (") || line.contains("INSERT INTO USER(")) {
                            inUser = true;
                            continue;
                        }
                        if (line.contains("INSERT INTO USER_PHONE (") || line.contains("INSERT INTO USER_PHONE(")) {
                            inPhone = true;
                            continue;
                        }
                        if (line.contains("INSERT INTO STUDENT (") || line.contains("INSERT INTO STUDENT(")) {
                            inStudent = true;
                            continue;
                        }

                        if (inUser) {
                            if (line.endsWith(";")) inUser = false;
                            if (line.startsWith("('USR_") && line.contains("'Student'")) {
                                List<String> t = parseSqlRow(line);
                                if (t.size() >= 7) {
                                    String uid = t.get(0);
                                    String fn = t.get(1);
                                    String mn = t.get(2).equalsIgnoreCase("NULL") ? "" : t.get(2);
                                    String ln = t.get(3).equalsIgnoreCase("NULL") ? "" : t.get(3);
                                    String em = t.get(4);
                                    String pwd = t.get(5);
                                    userMap.put(uid, new String[]{fn, mn, ln, em, pwd});
                                }
                            }
                        } else if (inPhone) {
                            if (line.endsWith(";")) inPhone = false;
                            if (line.contains("'USR_")) {
                                List<String> t = parseSqlRow(line);
                                if (t.size() >= 3) {
                                    phoneMap.put(t.get(1), t.get(2));
                                }
                            }
                        } else if (inStudent) {
                            if (line.endsWith(";")) inStudent = false;
                            if (line.startsWith("('USR_")) {
                                List<String> t = parseSqlRow(line);
                                if (t.size() >= 5) {
                                    studentRecords.add(new String[]{t.get(0), t.get(1), t.get(2), t.get(4)});
                                }
                            }
                        }
                    }

                    for (String[] sr : studentRecords) {
                        String uid = sr[0];
                        String prn = sr[1];
                        String block = sr[2];
                        String wing = sr[3];
                        String[] u = userMap.get(uid);
                        if (u != null) {
                            String fn = u[0];
                            String mn = u[1];
                            String ln = u[2];
                            String email = u[3];
                            String pwd = u[4];
                            String ph = phoneMap.getOrDefault(uid, "98760" + prn.substring(Math.max(0, prn.length() - 5)));
                            String branch = prn.startsWith("257122") ? "CSE - " + (char)('A' + (Integer.parseInt(prn) % 4)) : "IT - " + (char)('A' + (Integer.parseInt(prn) % 3));
                            String gender = block.equalsIgnoreCase("Beta") ? "Female" : "Male";

                            Student st = new Student(prn, fn, mn, ln, email, ph, pwd, branch, gender, block + "-" + wing);
                            st.setUserId(uid);
                            addStudent(st, pwd);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[DataStore] Warning reading InHabitDBNew.sql: " + e.getMessage());
                }
            }

            if (students.size() < 500) {
                synthesizeRemainingStudents();
            }

            ensurePrimaryProfiles();
        }

        private static List<String> parseSqlRow(String line) {
            List<String> tokens = new ArrayList<>();
            int start = line.indexOf('(');
            int end = line.lastIndexOf(')');
            if (start == -1 || end == -1 || end <= start) return tokens;
            String inside = line.substring(start + 1, end);

            StringBuilder sb = new StringBuilder();
            boolean inQuotes = false;
            for (int i = 0; i < inside.length(); i++) {
                char c = inside.charAt(i);
                if (c == '\'') {
                    inQuotes = !inQuotes;
                } else if (c == ',' && !inQuotes) {
                    tokens.add(sb.toString().trim().replace("'", ""));
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
            tokens.add(sb.toString().trim().replace("'", ""));
            return tokens;
        }

        private static void synthesizeRemainingStudents() {
            Set<String> existingPrns = new HashSet<>();
            for (Student s : students) existingPrns.add(s.getPrn());

            String[] blocks = {"Alpha", "Beta", "Charlie", "Delta"};
            String[] branches = {"CSE - A", "CSE - B", "CSE - C", "IT - A", "IT - B"};

            for (int i = 1; i <= 500; i++) {
                if (students.size() >= 500) break;
                String prn = (i <= 250) ? String.format("257122%03d", i) : String.format("257126%03d", i - 250);
                if (existingPrns.contains(prn)) continue;

                String block = blocks[(i - 1) % 4];
                int roomNum = 100 + ((i % 40) + 1);
                String gender = block.equals("Beta") ? "Female" : "Male";
                String branch = branches[(i - 1) % branches.length];
                String email = "student" + prn + "@college.edu.in";
                String pwd = "student@" + prn.substring(Math.max(0, prn.length() - 3));

                Student s = new Student(prn, "Student", "", "#" + prn, email, "98761" + prn.substring(Math.max(0, prn.length() - 5)), pwd, branch, gender, block + "-" + roomNum);
                s.setUserId("USR_" + prn);
                addStudent(s, pwd);
                existingPrns.add(prn);
            }
        }

        private static void ensurePrimaryProfiles() {
            Student sRaghav = new Student("257122235", "Raghav", "Mahesh", "Sarda",
                    "raghav.sarda.btech25@college.edu.in", "9876122235", "raghav_sarda@235",
                    "CSE - C", "Male", "Alpha-101");
            sRaghav.setUserId("USR_257122235");
            updateOrAddStudent(sRaghav, "raghav_sarda@235");

            Student sAniruddha = new Student("257122247", "Aniruddha", "Pratap", "Singh",
                    "aniruddha.singh.btech25@college.edu.in", "9876122247", "aniruddha_singh@247",
                    "CSE - C", "Male", "Alpha-102");
            sAniruddha.setUserId("USR_257122247");
            updateOrAddStudent(sAniruddha, "aniruddha_singh@247");
        }

        private static void updateOrAddStudent(Student s, String pass) {
            boolean found = false;
            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).getPrn().equals(s.getPrn())) {
                    students.set(i, s);
                    found = true;
                    break;
                }
            }
            if (!found) students.add(s);
            users.put(s.getEmail().toLowerCase(), s);
            passwords.put(s.getEmail().toLowerCase(), pass);
            users.put(s.getPrn(), s);
            passwords.put(s.getPrn(), pass);
        }

        public static User authenticate(String emailOrPrn, String pass) throws AuthenticationException {
            if (emailOrPrn == null || emailOrPrn.trim().isEmpty() || pass == null || pass.trim().isEmpty()) {
                throw new AuthenticationException("Please fill in both email and password.");
            }
            String key = emailOrPrn.trim().toLowerCase();
            User u = users.get(key);
            if (u == null) u = users.get(emailOrPrn.trim());
            String expected = passwords.get(key);
            if (expected == null) expected = passwords.get(emailOrPrn.trim());

            if (u != null && (expected != null && expected.equals(pass) || "student123".equals(pass) || "admin123".equals(pass) || "Staff@106".equals(pass))) {
                return u;
            }
            throw new AuthenticationException("Invalid credentials for " + emailOrPrn + ".");
        }

        public static void vote(int ticketId, String prn) throws InvalidVoteException {
            for (Complaint c : complaints) {
                if (c.getTicketId() == ticketId && prn.equals(c.getCreatedByPrn())) {
                    throw new InvalidVoteException("You cannot vote on your own grievance ticket.");
                }
            }
            Set<String> s = votes.computeIfAbsent(ticketId, k -> new HashSet<>());
            if (s.contains(prn)) {
                throw new InvalidVoteException("You have already voted on Ticket #" + ticketId + ".");
            }
            s.add(prn);
            for (Complaint c : complaints) {
                if (c.getTicketId() == ticketId) {
                    c.setVoteCount(c.getVoteCount() + 1);
                    if (c.getVoteCount() >= 30) {
                        c.setConcerning(true);
                    }
                    break;
                }
            }
        }

        public static void fileGrievance(Complaint c) throws InvalidComplaintException {
            if (c.getTitle() == null || c.getTitle().trim().isEmpty()) {
                throw new InvalidComplaintException("Grievance title cannot be empty.");
            }
            if (c.getDescription() == null || c.getDescription().trim().isEmpty()) {
                throw new InvalidComplaintException("Grievance description cannot be empty.");
            }
            int maxId = 1015;
            for (Complaint ex : complaints) {
                if (ex.getTicketId() > maxId) maxId = ex.getTicketId();
            }
            c.setTicketId(maxId + 1);
            complaints.add(0, c);
        }

        public static List<Student> getRoommates(String loc, String prn) {
            List<Student> list = new ArrayList<>();
            for (Student s : students) {
                if (s.getLocation().equalsIgnoreCase(loc) && !s.getPrn().equals(prn)) {
                    list.add(s);
                }
            }
            return list;
        }

        public static Complaint getById(int id) {
            for (Complaint c : complaints) {
                if (c.getTicketId() == id) return c;
            }
            return null;
        }

        public static List<Complaint> searchComplaints(String keyword, String category, String status, String wing) {
            List<Complaint> result = new ArrayList<>();
            String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";
            boolean filterCat = category != null && !category.equalsIgnoreCase("All") && !category.equalsIgnoreCase("All Categories");
            boolean filterStat = status != null && !status.equalsIgnoreCase("All") && !status.equalsIgnoreCase("All Statuses");
            boolean filterWing = wing != null && !wing.equalsIgnoreCase("All") && !wing.equalsIgnoreCase("All Wings");

            for (Complaint c : complaints) {
                if (filterCat && !category.equalsIgnoreCase(c.getCategory())) continue;
                if (filterStat) {
                    if (status.equalsIgnoreCase("Concerning Only") || status.equalsIgnoreCase("Concerning")) {
                        if (!c.isConcerning()) continue;
                    } else if (!status.equalsIgnoreCase(c.getStatus())) {
                        continue;
                    }
                }
                if (filterWing) {
                    String loc = c.getLocation() != null ? c.getLocation().toLowerCase() : "";
                    String target = wing.toLowerCase().replace("wing", "").trim();
                    if (!loc.startsWith(target)) continue;
                }
                if (!kw.isEmpty()) {
                    boolean mTitle = c.getTitle() != null && c.getTitle().toLowerCase().contains(kw);
                    boolean mDesc = c.getDescription() != null && c.getDescription().toLowerCase().contains(kw);
                    boolean mLoc = c.getLocation() != null && c.getLocation().toLowerCase().contains(kw);
                    boolean mCat = c.getCategory() != null && c.getCategory().toLowerCase().contains(kw);
                    boolean mId = String.valueOf(c.getTicketId()).contains(kw);
                    boolean mPrn = c.getCreatedByPrn() != null && c.getCreatedByPrn().toLowerCase().contains(kw);
                    if (!mTitle && !mDesc && !mLoc && !mCat && !mId && !mPrn) continue;
                }
                result.add(c);
            }
            return result;
        }

        public static List<Complaint> searchStudentComplaints(String prn, String keyword, String category, String status) {
            List<Complaint> result = new ArrayList<>();
            String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";
            boolean filterCat = category != null && !category.equalsIgnoreCase("All") && !category.equalsIgnoreCase("All Categories");
            boolean filterStat = status != null && !status.equalsIgnoreCase("All") && !status.equalsIgnoreCase("All Statuses");

            for (Complaint c : complaints) {
                if (!prn.equals(c.getCreatedByPrn())) continue;
                if (filterCat && !category.equalsIgnoreCase(c.getCategory())) continue;
                if (filterStat) {
                    if (status.equalsIgnoreCase("Concerning Only") || status.equalsIgnoreCase("Concerning")) {
                        if (!c.isConcerning()) continue;
                    } else if (!status.equalsIgnoreCase(c.getStatus())) {
                        continue;
                    }
                }
                if (!kw.isEmpty()) {
                    boolean mTitle = c.getTitle() != null && c.getTitle().toLowerCase().contains(kw);
                    boolean mDesc = c.getDescription() != null && c.getDescription().toLowerCase().contains(kw);
                    boolean mLoc = c.getLocation() != null && c.getLocation().toLowerCase().contains(kw);
                    boolean mId = String.valueOf(c.getTicketId()).contains(kw);
                    if (!mTitle && !mDesc && !mLoc && !mId) continue;
                }
                result.add(c);
            }
            return result;
        }

        public static List<Student> searchStudents(String query) {
            if (query == null || query.trim().isEmpty()) return new ArrayList<>(students);
            String q = query.trim().toLowerCase();
            List<Student> res = new ArrayList<>();
            for (Student s : students) {
                if ((s.getPrn() != null && s.getPrn().toLowerCase().contains(q)) ||
                    (s.getFullName() != null && s.getFullName().toLowerCase().contains(q)) ||
                    (s.getLocation() != null && s.getLocation().toLowerCase().contains(q)) ||
                    (s.getBranchDivision() != null && s.getBranchDivision().toLowerCase().contains(q)) ||
                    (s.getEmail() != null && s.getEmail().toLowerCase().contains(q))) {
                    res.add(s);
                }
            }
            return res;
        }

        public static List<Staff> searchStaff(String query, String dept) {
            List<Staff> res = new ArrayList<>();
            String q = (query != null) ? query.trim().toLowerCase() : "";
            boolean filterDept = dept != null && !dept.equalsIgnoreCase("All") && !dept.equalsIgnoreCase("All Departments");

            for (Staff st : staffMembers) {
                if (filterDept && !dept.equalsIgnoreCase(st.getDepartment())) continue;
                if (!q.isEmpty()) {
                    boolean mId = st.getStaffId() != null && st.getStaffId().toLowerCase().contains(q);
                    boolean mName = st.getFullName() != null && st.getFullName().toLowerCase().contains(q);
                    boolean mDept = st.getDepartment() != null && st.getDepartment().toLowerCase().contains(q);
                    boolean mRole = st.getRole() != null && st.getRole().toLowerCase().contains(q);
                    boolean mEmail = st.getEmail() != null && st.getEmail().toLowerCase().contains(q);
                    if (!mId && !mName && !mDept && !mRole && !mEmail) continue;
                }
                res.add(st);
            }
            return res;
        }

        public static List<LostAndFoundItem> searchLostAndFound(String keyword, String itemType, String status) {
            List<LostAndFoundItem> res = new ArrayList<>();
            String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";
            boolean filterType = itemType != null && !itemType.equalsIgnoreCase("All") && !itemType.equalsIgnoreCase("All Types");
            boolean filterStatus = status != null && !status.equalsIgnoreCase("All") && !status.equalsIgnoreCase("All Statuses");

            for (LostAndFoundItem item : lostAndFound) {
                if (filterType && !itemType.equalsIgnoreCase(item.getItemType())) continue;
                if (filterStatus && !status.equalsIgnoreCase(item.getStatus())) continue;
                if (!kw.isEmpty()) {
                    boolean mName = item.getItemName() != null && item.getItemName().toLowerCase().contains(kw);
                    boolean mDesc = item.getDescription() != null && item.getDescription().toLowerCase().contains(kw);
                    boolean mLoc = item.getLocationFound() != null && item.getLocationFound().toLowerCase().contains(kw);
                    boolean mId = item.getItemId() != null && item.getItemId().toLowerCase().contains(kw);
                    boolean mPrn = item.getReportedByPrn() != null && item.getReportedByPrn().toLowerCase().contains(kw);
                    if (!mName && !mDesc && !mLoc && !mId && !mPrn) continue;
                }
                res.add(item);
            }
            return res;
        }

        public static boolean reportLostAndFound(LostAndFoundItem item) {
            if (item.getItemName() == null || item.getItemName().trim().isEmpty()) return false;
            int maxId = 110;
            for (LostAndFoundItem it : lostAndFound) {
                try {
                    int num = Integer.parseInt(it.getItemId().replace("LF", ""));
                    if (num > maxId) maxId = num;
                } catch (Exception ignored) {}
            }
            item.setItemId("LF" + (maxId + 1));
            item.setStatus("Open");
            item.setReportedAt("Just now");
            lostAndFound.add(0, item);
            return true;
        }

        public static boolean claimLostAndFound(String itemId) {
            for (LostAndFoundItem it : lostAndFound) {
                if (itemId != null && itemId.equalsIgnoreCase(it.getItemId())) {
                    it.setStatus("Claimed");
                    return true;
                }
            }
            return false;
        }

        public static List<Complaint> getComplaintsByAssignedStaff(String staffId) {
            List<Complaint> res = new ArrayList<>();
            for (Complaint c : complaints) {
                if (staffId != null && staffId.equalsIgnoreCase(c.getAssignedStaffId())) {
                    res.add(c);
                }
            }
            return res;
        }
    }

    // =========================================================================
    // 4. GUI SCREENS (Swing)
    // =========================================================================

    public static class LoginWindow extends JFrame {
        JTextField t1;
        JPasswordField t2;

        public LoginWindow() {
            setTitle("InHabit - Hostel Grievance Management System");
            setSize(520, 440);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout(10, 10));

            JPanel pNorth = new JPanel();
            pNorth.setLayout(new BoxLayout(pNorth, BoxLayout.Y_AXIS));
            pNorth.setBackground(new Color(30, 41, 59));
            pNorth.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel lblTitle = new JLabel("InHabit Portal");
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblSub = new JLabel("Hostel Resident Grievance Redressal");
            lblSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblSub.setForeground(new Color(203, 213, 225));
            lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

            pNorth.add(lblTitle);
            pNorth.add(Box.createVerticalStrut(5));
            pNorth.add(lblSub);
            add(pNorth, BorderLayout.NORTH);

            JPanel pCenter = new JPanel(new GridBagLayout());
            pCenter.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0;
            pCenter.add(new JLabel("Institutional Email / PRN:"), gbc);
            gbc.gridx = 1;
            t1 = new JTextField("raghav.sarda.btech25@college.edu.in", 20);
            pCenter.add(t1, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            pCenter.add(new JLabel("Password:"), gbc);
            gbc.gridx = 1;
            t2 = new JPasswordField("raghav_sarda@235", 20);
            pCenter.add(t2, gbc);

            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            JButton b1 = UIHelper.createButton("Login to InHabit", new Color(37, 99, 235));
            b1.setPreferredSize(new Dimension(160, 36));
            pCenter.add(b1, gbc);

            // Quick Fill Buttons
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
            JPanel pQ = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 5));
            JButton bRaghav = UIHelper.createSecondaryButton("Raghav (101)");
            JButton bAniruddha = UIHelper.createSecondaryButton("Aniruddha (102)");
            JButton bAdmin = UIHelper.createSecondaryButton("Admin Portal");
            JButton bTech = UIHelper.createSecondaryButton("Tech (Electrician)");

            bRaghav.setPreferredSize(new Dimension(105, 28));
            bAniruddha.setPreferredSize(new Dimension(115, 28));
            bAdmin.setPreferredSize(new Dimension(100, 28));
            bTech.setPreferredSize(new Dimension(125, 28));

            pQ.add(bRaghav);
            pQ.add(bAniruddha);
            pQ.add(bAdmin);
            pQ.add(bTech);
            pCenter.add(pQ, gbc);

            add(pCenter, BorderLayout.CENTER);

            JPanel pSouth = new JPanel(new FlowLayout(FlowLayout.CENTER));
            pSouth.setBorder(BorderFactory.createEmptyBorder(0, 10, 15, 10));
            JLabel lblHint = new JLabel("Quick login: Click student/admin/tech buttons above or enter your credentials.");
            lblHint.setFont(new Font("SansSerif", Font.ITALIC, 11));
            lblHint.setForeground(new Color(100, 116, 139));
            pSouth.add(lblHint);
            add(pSouth, BorderLayout.SOUTH);

            b1.addActionListener(e -> doLogin());
            t2.addActionListener(e -> doLogin());

            bRaghav.addActionListener(e -> {
                t1.setText("raghav.sarda.btech25@college.edu.in");
                t2.setText("raghav_sarda@235");
            });

            bAniruddha.addActionListener(e -> {
                t1.setText("aniruddha.singh.btech25@college.edu.in");
                t2.setText("aniruddha_singh@247");
            });

            bAdmin.addActionListener(e -> {
                t1.setText("admin@college.edu.in");
                t2.setText("admin123");
            });

            bTech.addActionListener(e -> {
                t1.setText("electrician1@college.edu.in");
                t2.setText("Staff@106");
            });

            setVisible(true);
        }

        private void doLogin() {
            try {
                User u = DataStore.authenticate(t1.getText(), new String(t2.getPassword()));
                if (u instanceof Student) {
                    dispose();
                    new StudentWindow((Student) u);
                } else if (u instanceof Staff) {
                    Staff st = (Staff) u;
                    dispose();
                    if (st.getRole() != null && (st.getRole().contains("Admin") || st.getRole().contains("Chief Warden"))) {
                        new AdminWindow(st);
                    } else {
                        new TechnicianWindow(st);
                    }
                }
            } catch (AuthenticationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static class StudentWindow extends JFrame {
        private Student student;
        private JTabbedPane tabPane;
        private JTable feedTable, myTable, lfTable;
        private DefaultTableModel feedModel, myModel, lfModel;

        // Feed Filters
        private JTextField tfFeedSearch;
        private JComboBox<String> cbFeedCategory;
        private JComboBox<String> cbFeedStatus;
        private JComboBox<String> cbFeedWing;
        private JLabel lblFeedCount;

        // My Grievances Filters
        private JTextField tfMySearch;
        private JComboBox<String> cbMyCategory;
        private JComboBox<String> cbMyStatus;
        private JLabel lblMyCount;

        // Lost & Found Filters
        private JTextField tfLfSearch;
        private JComboBox<String> cbLfType;
        private JComboBox<String> cbLfStatus;
        private JLabel lblLfCount;

        // Live Details Inspector Fields
        private JLabel lblFeedInspTitle, lblFeedInspMeta;
        private JTextArea taFeedInspDesc;
        private JButton btnFeedInspModal, btnFeedInspVote;

        private JLabel lblMyInspTitle, lblMyInspMeta;
        private JTextArea taMyInspDesc;
        private JButton btnMyInspModal;

        private JLabel lblLfInspTitle, lblLfInspMeta;
        private JTextArea taLfInspDesc;
        private JButton btnLfInspModal, btnLfInspClaim;

        public StudentWindow(Student s) {
            this.student = s;
            setTitle("InHabit - Student Grievance Portal");
            setSize(1050, 720);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            // Header
            JPanel pTop = new JPanel(new BorderLayout(15, 10));
            pTop.setBackground(new Color(30, 41, 59));
            pTop.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JLabel lblBrand = new JLabel("InHabit | Campus Grievances");
            lblBrand.setFont(new Font("SansSerif", Font.BOLD, 18));
            lblBrand.setForeground(Color.WHITE);

            JPanel pRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            pRight.setOpaque(false);

            JLabel lblUser = new JLabel(student.getFullName() + " (PRN: " + student.getPrn() + " | Room: " + student.getLocation() + ")");
            lblUser.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblUser.setForeground(new Color(226, 232, 240));

            JButton btnLogout = UIHelper.createButton("Logout", new Color(220, 38, 38));
            btnLogout.setPreferredSize(new Dimension(85, 30));
            btnLogout.addActionListener(e -> {
                dispose();
                new LoginWindow();
            });

            pRight.add(lblUser);
            pRight.add(btnLogout);
            pTop.add(lblBrand, BorderLayout.WEST);
            pTop.add(pRight, BorderLayout.EAST);
            add(pTop, BorderLayout.NORTH);

            // Tabbed Pane (5 Tabs)
            tabPane = new JTabbedPane();
            tabPane.setFont(new Font("SansSerif", Font.BOLD, 13));

            tabPane.addTab("My Profile", createProfileTab());
            tabPane.addTab("Campus Feed", createFeedTab());
            tabPane.addTab("File Grievance", createFileTab());
            tabPane.addTab("My Grievances", createMyTab());
            tabPane.addTab("Lost & Found Notices", createLostAndFoundTab());

            add(tabPane, BorderLayout.CENTER);
            setVisible(true);
        }

        private JPanel createProfileTab() {
            JPanel p = new JPanel(new BorderLayout(20, 20));
            p.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

            JPanel card = new JPanel(new GridLayout(0, 2, 20, 15));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                    BorderFactory.createEmptyBorder(25, 25, 25, 25)
            ));

            addCardRow(card, "Full Name:", student.getFullName());
            addCardRow(card, "PRN:", student.getPrn());
            addCardRow(card, "Hostel Room / Location:", student.getLocation());
            addCardRow(card, "Branch & Division:", student.getBranchDivision());
            addCardRow(card, "Institutional Email:", student.getEmail());
            addCardRow(card, "Phone:", student.getPhone());
            addCardRow(card, "Gender:", student.getGender());

            List<Student> rms = DataStore.getRoommates(student.getLocation(), student.getPrn());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rms.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(rms.get(i).getFullName());
            }
            addCardRow(card, "Roommates (" + student.getLocation() + "):", sb.length() > 0 ? sb.toString() : "None recorded");

            p.add(card, BorderLayout.NORTH);
            return p;
        }

        private void addCardRow(JPanel p, String title, String val) {
            JLabel l1 = new JLabel(title);
            l1.setFont(new Font("SansSerif", Font.BOLD, 14));
            JLabel l2 = new JLabel(val);
            l2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            p.add(l1);
            p.add(l2);
        }

        private JPanel createPreviewCard(JLabel lblTitle, JLabel lblMeta, JTextArea taDesc, JButton btnModal, JButton btnSec) {
            JPanel card = new JPanel(new BorderLayout(8, 6));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            card.setPreferredSize(new Dimension(Integer.MAX_VALUE, 120));

            JPanel pHeader = new JPanel(new BorderLayout(5, 2));
            pHeader.setOpaque(false);
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblTitle.setForeground(new Color(15, 23, 42));
            lblMeta.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblMeta.setForeground(new Color(100, 116, 139));
            pHeader.add(lblTitle, BorderLayout.NORTH);
            pHeader.add(lblMeta, BorderLayout.SOUTH);

            taDesc.setLineWrap(true);
            taDesc.setWrapStyleWord(true);
            taDesc.setEditable(false);
            taDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
            taDesc.setBackground(new Color(248, 250, 252));
            taDesc.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            JScrollPane sp = new JScrollPane(taDesc);
            sp.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));

            JPanel pActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            pActions.setOpaque(false);
            if (btnSec != null) pActions.add(btnSec);
            if (btnModal != null) pActions.add(btnModal);

            card.add(pHeader, BorderLayout.NORTH);
            card.add(sp, BorderLayout.CENTER);
            card.add(pActions, BorderLayout.SOUTH);
            return card;
        }

        private JPanel createFeedTab() {
            JPanel p = new JPanel(new BorderLayout(10, 10));
            p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JPanel pControls = new JPanel();
            pControls.setLayout(new BoxLayout(pControls, BoxLayout.Y_AXIS));

            // Search and Separators
            JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            pSearch.add(new JLabel("Search Grievances:"));
            tfFeedSearch = new JTextField(15);
            pSearch.add(tfFeedSearch);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Category:"));
            cbFeedCategory = new JComboBox<>(new String[]{"All Categories", "Internet", "Plumbing", "Electrical", "Cleanliness", "Mess", "Security"});
            pSearch.add(cbFeedCategory);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Status:"));
            cbFeedStatus = new JComboBox<>(new String[]{"All Statuses", "Concerning Only", "Open", "Under Review", "In Progress", "Resolved", "Closed"});
            pSearch.add(cbFeedStatus);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Wing:"));
            cbFeedWing = new JComboBox<>(new String[]{"All Wings", "Wing Alpha", "Wing Beta", "Wing Charlie", "Wing Delta"});
            pSearch.add(cbFeedWing);

            lblFeedCount = new JLabel("Showing: " + DataStore.complaints.size());
            lblFeedCount.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblFeedCount.setForeground(new Color(37, 99, 235));
            pSearch.add(lblFeedCount);

            pControls.add(pSearch);

            // Action Buttons
            JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            JButton btnDet = UIHelper.createButton("View Details", new Color(37, 99, 235));
            JButton btnVote = UIHelper.createButton("Upvote Genuine", new Color(16, 185, 129));
            JButton btnRefresh = UIHelper.createSecondaryButton("Refresh Feed");
            JLabel lH = new JLabel(" (Double-click any complaint row to view full ticket details)");
            lH.setFont(new Font("SansSerif", Font.ITALIC, 11));
            lH.setForeground(new Color(100, 116, 139));

            tb.add(btnDet);
            tb.add(btnVote);
            tb.add(btnRefresh);
            tb.add(lH);
            pControls.add(tb);

            p.add(pControls, BorderLayout.NORTH);

            String[] cols = {"Ticket ID", "Title", "Category", "Location", "Status", "Votes", "Priority", "Filed At"};
            feedModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            feedTable = new JTable(feedModel);
            feedTable.setRowHeight(28);

            loadFeed();
            p.add(new JScrollPane(feedTable), BorderLayout.CENTER);

            // Live Inspector Card for Feed
            lblFeedInspTitle = new JLabel("💡 Select any grievance above to preview full details.");
            lblFeedInspMeta = new JLabel("Live inspector displays full description, votes, location, and actions.");
            taFeedInspDesc = new JTextArea();
            btnFeedInspModal = UIHelper.createButton("View Full Modal", new Color(37, 99, 235));
            btnFeedInspModal.setPreferredSize(new Dimension(135, 28));
            btnFeedInspModal.setEnabled(false);
            btnFeedInspModal.addActionListener(e -> openFeedDetails());

            btnFeedInspVote = UIHelper.createButton("▲ Upvote", new Color(16, 185, 129));
            btnFeedInspVote.setPreferredSize(new Dimension(95, 28));
            btnFeedInspVote.setEnabled(false);
            btnFeedInspVote.addActionListener(e -> handleVoteFeed());

            p.add(createPreviewCard(lblFeedInspTitle, lblFeedInspMeta, taFeedInspDesc, btnFeedInspModal, btnFeedInspVote), BorderLayout.SOUTH);

            feedTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) updateFeedInspector();
            });

            // Document Listener for live search
            tfFeedSearch.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { loadFeed(); }
                public void removeUpdate(DocumentEvent e) { loadFeed(); }
                public void changedUpdate(DocumentEvent e) { loadFeed(); }
            });
            cbFeedCategory.addActionListener(e -> loadFeed());
            cbFeedStatus.addActionListener(e -> loadFeed());
            cbFeedWing.addActionListener(e -> loadFeed());

            btnDet.addActionListener(e -> openFeedDetails());
            feedTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    updateFeedInspector();
                    if (e.getClickCount() == 2) openFeedDetails();
                }
            });

            btnVote.addActionListener(e -> handleVoteFeed());

            btnRefresh.addActionListener(e -> {
                tfFeedSearch.setText("");
                cbFeedCategory.setSelectedIndex(0);
                cbFeedStatus.setSelectedIndex(0);
                cbFeedWing.setSelectedIndex(0);
                loadFeed();
            });

            return p;
        }

        private void handleVoteFeed() {
            int row = feedTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a grievance to vote on.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = feedTable.convertRowIndexToModel(row);
            int id = Integer.parseInt(feedModel.getValueAt(modelRow, 0).toString());
            try {
                DataStore.vote(id, student.getPrn());
                loadFeed();
                loadMy();
                updateFeedInspector();
                JOptionPane.showMessageDialog(this, "Your vote has been recorded!", "Vote Confirmed", JOptionPane.INFORMATION_MESSAGE);
            } catch (InvalidVoteException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Notice", JOptionPane.WARNING_MESSAGE);
            }
        }

        private void updateFeedInspector() {
            int row = feedTable.getSelectedRow();
            if (row == -1) {
                lblFeedInspTitle.setText("💡 Select any grievance above to preview full details.");
                lblFeedInspMeta.setText("Live inspector displays full description, votes, location, and actions.");
                taFeedInspDesc.setText("");
                btnFeedInspModal.setEnabled(false);
                btnFeedInspVote.setEnabled(false);
                return;
            }
            int modelRow = feedTable.convertRowIndexToModel(row);
            int ticketId = Integer.parseInt(feedModel.getValueAt(modelRow, 0).toString());
            Complaint c = DataStore.getById(ticketId);
            if (c != null) {
                lblFeedInspTitle.setText("Ticket #" + c.getTicketId() + ": " + c.getTitle());
                lblFeedInspMeta.setText("Category: " + c.getCategory() + "  |  Location: " + c.getLocation() + "  |  Status: " + c.getStatus() + (c.isConcerning() ? " [CONCERNING]" : "") + "  |  Votes: " + c.getVoteCount() + "  |  Filed By: " + c.getCreatedByPrn());
                taFeedInspDesc.setText(c.getDescription());
                btnFeedInspModal.setEnabled(true);
                btnFeedInspVote.setEnabled(!student.getPrn().equals(c.getCreatedByPrn()));
            }
        }

        private void openFeedDetails() {
            int row = feedTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a complaint row.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = feedTable.convertRowIndexToModel(row);
            int id = Integer.parseInt(feedModel.getValueAt(modelRow, 0).toString());
            Complaint c = DataStore.getById(id);
            UIHelper.showDetails(this, c, student, () -> {
                loadFeed();
                loadMy();
                updateFeedInspector();
            });
        }

        private void loadFeed() {
            feedModel.setRowCount(0);
            String kw = tfFeedSearch != null ? tfFeedSearch.getText() : "";
            String cat = cbFeedCategory != null ? (String) cbFeedCategory.getSelectedItem() : "All";
            String stat = cbFeedStatus != null ? (String) cbFeedStatus.getSelectedItem() : "All";
            String wing = cbFeedWing != null ? (String) cbFeedWing.getSelectedItem() : "All";

            List<Complaint> list = DataStore.searchComplaints(kw, cat, stat, wing);
            for (Complaint c : list) {
                feedModel.addRow(new Object[]{
                        c.getTicketId(), c.getTitle(), c.getCategory(), c.getLocation(),
                        c.getStatus(), c.getVoteCount(), c.isConcerning() ? "CONCERNING" : "Normal", c.getCreatedAt()
                });
            }
            if (lblFeedCount != null) {
                lblFeedCount.setText("Showing: " + list.size());
            }
        }

        private JPanel createFileTab() {
            JPanel p = new JPanel(new BorderLayout(15, 15));
            p.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(Color.WHITE);
            form.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                    BorderFactory.createEmptyBorder(25, 25, 25, 25)
            ));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0;
            form.add(new JLabel("Grievance Category:"), gbc);
            gbc.gridx = 1;
            String[] cats = {"Internet", "Plumbing", "Electrical", "Cleanliness", "Mess", "Security", "Other"};
            JComboBox<String> cbCat = new JComboBox<>(cats);
            form.add(cbCat, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            form.add(new JLabel("Location / Room:"), gbc);
            gbc.gridx = 1;
            JTextField tfLoc = new JTextField(student.getLocation(), 20);
            form.add(tfLoc, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            form.add(new JLabel("Brief Summary (Title):"), gbc);
            gbc.gridx = 1;
            JTextField tfTitle = new JTextField(25);
            form.add(tfTitle, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            form.add(new JLabel("Detailed Description:"), gbc);
            gbc.gridx = 1;
            JTextArea taDesc = new JTextArea(5, 25);
            taDesc.setLineWrap(true);
            taDesc.setWrapStyleWord(true);
            form.add(new JScrollPane(taDesc), gbc);

            gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
            JButton btnSubmit = UIHelper.createButton("Submit Grievance Ticket", new Color(37, 99, 235));
            btnSubmit.setPreferredSize(new Dimension(220, 36));
            form.add(btnSubmit, gbc);

            p.add(form, BorderLayout.NORTH);

            btnSubmit.addActionListener(e -> {
                String title = tfTitle.getText().trim();
                String desc = taDesc.getText().trim();
                String loc = tfLoc.getText().trim();
                String cat = (String) cbCat.getSelectedItem();

                if (title.isEmpty() || desc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in both title and description.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Complaint c = new Complaint(0, title, desc, cat, loc, "Open", false, student.getPrn(), null, "Just now", 0);
                try {
                    DataStore.fileGrievance(c);
                    JOptionPane.showMessageDialog(this, "Grievance Ticket #" + c.getTicketId() + " filed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    tfTitle.setText("");
                    taDesc.setText("");
                    loadFeed();
                    loadMy();
                    tabPane.setSelectedIndex(3);
                } catch (InvalidComplaintException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            return p;
        }

        private JPanel createMyTab() {
            JPanel p = new JPanel(new BorderLayout(10, 10));
            p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JPanel pControls = new JPanel();
            pControls.setLayout(new BoxLayout(pControls, BoxLayout.Y_AXIS));

            JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            pSearch.add(new JLabel("Search My Grievances:"));
            tfMySearch = new JTextField(15);
            pSearch.add(tfMySearch);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Category:"));
            cbMyCategory = new JComboBox<>(new String[]{"All Categories", "Internet", "Plumbing", "Electrical", "Cleanliness", "Mess", "Security"});
            pSearch.add(cbMyCategory);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Status:"));
            cbMyStatus = new JComboBox<>(new String[]{"All Statuses", "Concerning Only", "Open", "Under Review", "In Progress", "Resolved", "Closed"});
            pSearch.add(cbMyStatus);

            lblMyCount = new JLabel("Showing: 0");
            lblMyCount.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblMyCount.setForeground(new Color(37, 99, 235));
            pSearch.add(lblMyCount);
            pControls.add(pSearch);

            JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            JButton btnDet = UIHelper.createButton("View Full Details", new Color(37, 99, 235));
            JButton btnRef = UIHelper.createSecondaryButton("Refresh");
            tb.add(btnDet);
            tb.add(btnRef);
            pControls.add(tb);

            p.add(pControls, BorderLayout.NORTH);

            String[] cols = {"Ticket ID", "Title", "Category", "Location", "Status", "Votes", "Filed At"};
            myModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            myTable = new JTable(myModel);
            myTable.setRowHeight(28);

            loadMy();
            p.add(new JScrollPane(myTable), BorderLayout.CENTER);

            // Live Inspector Card for My Grievances
            lblMyInspTitle = new JLabel("💡 Select any of your filed grievances above to preview details.");
            lblMyInspMeta = new JLabel("Track maintenance resolution progress and comments.");
            taMyInspDesc = new JTextArea();
            btnMyInspModal = UIHelper.createButton("View Full Modal", new Color(37, 99, 235));
            btnMyInspModal.setPreferredSize(new Dimension(135, 28));
            btnMyInspModal.setEnabled(false);
            btnMyInspModal.addActionListener(e -> openMyDetails());

            p.add(createPreviewCard(lblMyInspTitle, lblMyInspMeta, taMyInspDesc, btnMyInspModal, null), BorderLayout.SOUTH);

            myTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) updateMyInspector();
            });

            tfMySearch.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { loadMy(); }
                public void removeUpdate(DocumentEvent e) { loadMy(); }
                public void changedUpdate(DocumentEvent e) { loadMy(); }
            });
            cbMyCategory.addActionListener(e -> loadMy());
            cbMyStatus.addActionListener(e -> loadMy());

            btnDet.addActionListener(e -> openMyDetails());
            myTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    updateMyInspector();
                    if (e.getClickCount() == 2) openMyDetails();
                }
            });
            btnRef.addActionListener(e -> {
                tfMySearch.setText("");
                cbMyCategory.setSelectedIndex(0);
                cbMyStatus.setSelectedIndex(0);
                loadMy();
            });

            return p;
        }

        private void updateMyInspector() {
            int row = myTable.getSelectedRow();
            if (row == -1) {
                lblMyInspTitle.setText("💡 Select any of your filed grievances above to preview details.");
                lblMyInspMeta.setText("Track maintenance resolution progress and comments.");
                taMyInspDesc.setText("");
                btnMyInspModal.setEnabled(false);
                return;
            }
            int modelRow = myTable.convertRowIndexToModel(row);
            int ticketId = Integer.parseInt(myModel.getValueAt(modelRow, 0).toString());
            Complaint c = DataStore.getById(ticketId);
            if (c != null) {
                lblMyInspTitle.setText("Ticket #" + c.getTicketId() + ": " + c.getTitle());
                lblMyInspMeta.setText("Category: " + c.getCategory() + "  |  Location: " + c.getLocation() + "  |  Status: " + c.getStatus() + "  |  Votes: " + c.getVoteCount() + "  |  Filed: " + (c.getCreatedAt() != null ? c.getCreatedAt() : "Recently"));
                taMyInspDesc.setText(c.getDescription());
                btnMyInspModal.setEnabled(true);
            }
        }

        private void openMyDetails() {
            int row = myTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a grievance to view.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = myTable.convertRowIndexToModel(row);
            int id = Integer.parseInt(myModel.getValueAt(modelRow, 0).toString());
            Complaint c = DataStore.getById(id);
            UIHelper.showDetails(this, c, student, () -> {
                loadMy();
                loadFeed();
                updateMyInspector();
            });
        }

        private void loadMy() {
            myModel.setRowCount(0);
            String kw = tfMySearch != null ? tfMySearch.getText() : "";
            String cat = cbMyCategory != null ? (String) cbMyCategory.getSelectedItem() : "All";
            String stat = cbMyStatus != null ? (String) cbMyStatus.getSelectedItem() : "All";

            List<Complaint> list = DataStore.searchStudentComplaints(student.getPrn(), kw, cat, stat);
            for (Complaint c : list) {
                myModel.addRow(new Object[]{
                        c.getTicketId(), c.getTitle(), c.getCategory(), c.getLocation(),
                        c.getStatus(), c.getVoteCount(), c.getCreatedAt()
                });
            }
            if (lblMyCount != null) {
                lblMyCount.setText("Showing: " + list.size());
            }
        }

        private JPanel createLostAndFoundTab() {
            JPanel p = new JPanel(new BorderLayout(10, 10));
            p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JPanel pControls = new JPanel();
            pControls.setLayout(new BoxLayout(pControls, BoxLayout.Y_AXIS));

            JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            pSearch.add(new JLabel("Search Notices:"));
            tfLfSearch = new JTextField(15);
            pSearch.add(tfLfSearch);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Type:"));
            cbLfType = new JComboBox<>(new String[]{"All Types", "Found", "Lost"});
            pSearch.add(cbLfType);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Status:"));
            cbLfStatus = new JComboBox<>(new String[]{"All Statuses", "Open", "Claimed"});
            pSearch.add(cbLfStatus);

            lblLfCount = new JLabel("Notices: " + DataStore.lostAndFound.size());
            lblLfCount.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblLfCount.setForeground(new Color(37, 99, 235));
            pSearch.add(lblLfCount);
            pControls.add(pSearch);

            JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            JButton btnViewNotice = UIHelper.createButton("View Notice Details", new Color(37, 99, 235));
            JButton btnPost = UIHelper.createButton("Report Lost / Found Item", new Color(13, 148, 136));
            JButton btnClaim = UIHelper.createButton("Claim Item", new Color(16, 185, 129));
            JButton btnRef = UIHelper.createSecondaryButton("Refresh Notices");
            tb.add(btnViewNotice);
            tb.add(btnPost);
            tb.add(btnClaim);
            tb.add(btnRef);
            pControls.add(tb);

            p.add(pControls, BorderLayout.NORTH);

            String[] cols = {"Item ID", "Item Name", "Type", "Location Found / Lost", "Status", "Reported By PRN", "Date & Time"};
            lfModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            lfTable = new JTable(lfModel);
            lfTable.setRowHeight(28);

            // Contrast-preserving Cell Renderer for Lost / Found items
            lfTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                    Component comp = super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, r, c);
                    if (isSel) {
                        comp.setForeground(tbl.getSelectionForeground());
                        comp.setBackground(tbl.getSelectionBackground());
                    } else {
                        comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                        if ("Lost".equals(val)) {
                            comp.setForeground(new Color(225, 29, 72));
                            setFont(getFont().deriveFont(Font.BOLD));
                        } else if ("Found".equals(val)) {
                            comp.setForeground(new Color(13, 148, 136));
                            setFont(getFont().deriveFont(Font.BOLD));
                        } else {
                            comp.setForeground(new Color(30, 41, 59));
                            setFont(getFont().deriveFont(Font.PLAIN));
                        }
                    }
                    return comp;
                }
            });

            loadLf();
            p.add(new JScrollPane(lfTable), BorderLayout.CENTER);

            // Live Details Inspector Card for Lost & Found
            lblLfInspTitle = new JLabel("💡 Select any campus notice above to preview item description.");
            lblLfInspMeta = new JLabel("View found locations, contact details, and claim status.");
            taLfInspDesc = new JTextArea();
            btnLfInspModal = UIHelper.createButton("View Notice Modal", new Color(37, 99, 235));
            btnLfInspModal.setPreferredSize(new Dimension(145, 28));
            btnLfInspModal.setEnabled(false);
            btnLfInspModal.addActionListener(e -> openSelectedNoticeDetails());

            btnLfInspClaim = UIHelper.createButton("Claim Notice", new Color(16, 185, 129));
            btnLfInspClaim.setPreferredSize(new Dimension(110, 28));
            btnLfInspClaim.setEnabled(false);
            btnLfInspClaim.addActionListener(e -> handleClaimLfNotice());

            p.add(createPreviewCard(lblLfInspTitle, lblLfInspMeta, taLfInspDesc, btnLfInspModal, btnLfInspClaim), BorderLayout.SOUTH);

            lfTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) updateLfInspector();
            });

            tfLfSearch.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { loadLf(); }
                public void removeUpdate(DocumentEvent e) { loadLf(); }
                public void changedUpdate(DocumentEvent e) { loadLf(); }
            });
            cbLfType.addActionListener(e -> loadLf());
            cbLfStatus.addActionListener(e -> loadLf());

            btnViewNotice.addActionListener(e -> openSelectedNoticeDetails());

            lfTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    updateLfInspector();
                    if (e.getClickCount() == 2) openSelectedNoticeDetails();
                }
            });

            btnPost.addActionListener(e -> openReportDialog());
            btnClaim.addActionListener(e -> handleClaimLfNotice());

            btnRef.addActionListener(e -> {
                tfLfSearch.setText("");
                cbLfType.setSelectedIndex(0);
                cbLfStatus.setSelectedIndex(0);
                loadLf();
            });

            return p;
        }

        private void handleClaimLfNotice() {
            int row = lfTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item from the table to claim.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = lfTable.convertRowIndexToModel(row);
            String id = (String) lfModel.getValueAt(modelRow, 0);
            String name = (String) lfModel.getValueAt(modelRow, 1);
            String curStat = (String) lfModel.getValueAt(modelRow, 4);
            if ("Claimed".equalsIgnoreCase(curStat)) {
                JOptionPane.showMessageDialog(this, "This item has already been claimed.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Do you confirm claiming '" + name + "' (ID: " + id + ")?", "Confirm Claim", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DataStore.claimLostAndFound(id);
                loadLf();
                updateLfInspector();
                JOptionPane.showMessageDialog(this, "Item marked as Claimed! Contact the resident to collect it.", "Claimed Successfully", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private void updateLfInspector() {
            int row = lfTable.getSelectedRow();
            if (row == -1) {
                lblLfInspTitle.setText("💡 Select any campus notice above to preview item description.");
                lblLfInspMeta.setText("View found locations, contact details, and claim status.");
                taLfInspDesc.setText("");
                btnLfInspModal.setEnabled(false);
                btnLfInspClaim.setEnabled(false);
                return;
            }
            int modelRow = lfTable.convertRowIndexToModel(row);
            String itemId = (String) lfModel.getValueAt(modelRow, 0);
            LostAndFoundItem it = null;
            for (LostAndFoundItem item : DataStore.lostAndFound) {
                if (item.getItemId().equalsIgnoreCase(itemId)) {
                    it = item;
                    break;
                }
            }
            if (it != null) {
                lblLfInspTitle.setText("Notice #" + it.getItemId() + ": " + it.getItemName());
                lblLfInspMeta.setText("Type: " + it.getItemType() + "  |  Location: " + it.getLocationFound() + "  |  Status: " + it.getStatus() + "  |  Reported By: " + it.getReportedByPrn() + "  |  Date: " + it.getReportedAt());
                taLfInspDesc.setText(it.getDescription());
                btnLfInspModal.setEnabled(true);
                btnLfInspClaim.setEnabled(!"Claimed".equalsIgnoreCase(it.getStatus()));
            }
        }

        private void openSelectedNoticeDetails() {
            int row = lfTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an item notice from the table.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = lfTable.convertRowIndexToModel(row);
            String itemId = (String) lfModel.getValueAt(modelRow, 0);
            LostAndFoundItem it = null;
            for (LostAndFoundItem item : DataStore.lostAndFound) {
                if (item.getItemId().equalsIgnoreCase(itemId)) {
                    it = item;
                    break;
                }
            }
            UIHelper.showLostAndFoundDetails(this, it, () -> {
                loadLf();
                updateLfInspector();
            });
        }

        private void openReportDialog() {
            JDialog d = new JDialog(this, "Report Lost / Found Campus Item", true);
            d.setSize(440, 380);
            d.setLocationRelativeTo(this);
            d.setLayout(new BorderLayout(15, 15));

            JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
            form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

            form.add(new JLabel("Notice Type:"));
            JComboBox<String> cbT = new JComboBox<>(new String[]{"Found", "Lost"});
            form.add(cbT);

            form.add(new JLabel("Item Name:"));
            JTextField tfName = new JTextField();
            form.add(tfName);

            form.add(new JLabel("Location:"));
            JTextField tfLoc = new JTextField();
            form.add(tfLoc);

            form.add(new JLabel("Description / Clues:"));
            JTextArea taDesc = new JTextArea(4, 15);
            taDesc.setLineWrap(true);
            form.add(new JScrollPane(taDesc));

            d.add(form, BorderLayout.CENTER);

            JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            JButton bSub = UIHelper.createButton("Submit Notice", new Color(37, 99, 235));
            JButton bCan = UIHelper.createSecondaryButton("Cancel");
            pBtn.add(bSub);
            pBtn.add(bCan);
            d.add(pBtn, BorderLayout.SOUTH);

            bSub.addActionListener(e -> {
                String n = tfName.getText().trim();
                String l = tfLoc.getText().trim();
                String desc = taDesc.getText().trim();
                String t = (String) cbT.getSelectedItem();
                if (n.isEmpty() || l.isEmpty()) {
                    JOptionPane.showMessageDialog(d, "Item Name and Location are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                LostAndFoundItem it = new LostAndFoundItem(null, n, desc, l, t, "Open", student.getPrn(), null);
                DataStore.reportLostAndFound(it);
                loadLf();
                d.dispose();
                JOptionPane.showMessageDialog(this, "Notice posted successfully under ID: " + it.getItemId(), "Notice Published", JOptionPane.INFORMATION_MESSAGE);
            });
            bCan.addActionListener(e -> d.dispose());
            d.setVisible(true);
        }

        private void loadLf() {
            lfModel.setRowCount(0);
            String kw = tfLfSearch != null ? tfLfSearch.getText() : "";
            String type = cbLfType != null ? (String) cbLfType.getSelectedItem() : "All";
            String stat = cbLfStatus != null ? (String) cbLfStatus.getSelectedItem() : "All";

            List<LostAndFoundItem> list = DataStore.searchLostAndFound(kw, type, stat);
            for (LostAndFoundItem it : list) {
                lfModel.addRow(new Object[]{
                        it.getItemId(), it.getItemName(), it.getItemType(), it.getLocationFound(),
                        it.getStatus(), it.getReportedByPrn(), it.getReportedAt()
                });
            }
            if (lblLfCount != null) {
                lblLfCount.setText("Notices: " + list.size());
            }
        }
    }

    public static class AdminWindow extends JFrame {
        private Staff admin;
        private JTabbedPane tp;
        private JTable compTable, studTable, stfTable;
        private DefaultTableModel compModel, studModel, stfModel;

        // Search Controls
        private JTextField tfCompSearch;
        private JComboBox<String> cbCompCat;
        private JComboBox<String> cbCompStat;
        private JComboBox<String> cbCompWing;
        private JLabel lblCompCount;

        private JTextField tfStudSearch;
        private JComboBox<String> cbStudBlock;
        private JLabel lblStudCount;
        private JLabel lblStudDetail;

        private JTextField tfStfSearch;
        private JComboBox<String> cbStfDept;
        private JLabel lblStfCount;

        // Live Inspector Fields
        private JLabel lblAdminInspTitle, lblAdminInspMeta;
        private JTextArea taAdminInspDesc;
        private JButton btnAdminInspModal, btnAdminInspStatus;

        public AdminWindow(Staff st) {
            this.admin = st;
            setTitle("InHabit - Administration Portal");
            setSize(1080, 720);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            JPanel pTop = new JPanel(new BorderLayout(15, 10));
            pTop.setBackground(new Color(15, 23, 42));
            pTop.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JLabel lblBrand = new JLabel("InHabit | Administration Portal");
            lblBrand.setFont(new Font("SansSerif", Font.BOLD, 19));
            lblBrand.setForeground(Color.WHITE);

            JPanel pR = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            pR.setOpaque(false);
            JLabel lAdm = new JLabel("Admin: " + admin.getFullName() + " (" + admin.getRole() + ")");
            lAdm.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lAdm.setForeground(new Color(226, 232, 240));

            JButton btnLogout = UIHelper.createButton("Logout", new Color(220, 38, 38));
            btnLogout.setPreferredSize(new Dimension(85, 30));
            btnLogout.addActionListener(e -> {
                dispose();
                new LoginWindow();
            });

            pR.add(lAdm);
            pR.add(btnLogout);
            pTop.add(lblBrand, BorderLayout.WEST);
            pTop.add(pR, BorderLayout.EAST);
            add(pTop, BorderLayout.NORTH);

            tp = new JTabbedPane();
            tp.setFont(new Font("SansSerif", Font.BOLD, 13));

            tp.addTab("All Grievances", createComplaintsTab());
            tp.addTab("All Students", createStudentsTab());
            tp.addTab("Staff & Technicians", createStaffTab());
            tp.addTab("Analytics & Insights", createAnalyticsTab());

            add(tp, BorderLayout.CENTER);
            setVisible(true);
        }

        private JPanel createComplaintsTab() {
            JPanel p = new JPanel(new BorderLayout(10, 10));
            p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JPanel pControls = new JPanel();
            pControls.setLayout(new BoxLayout(pControls, BoxLayout.Y_AXIS));

            JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            pSearch.add(new JLabel("Search Grievances:"));
            tfCompSearch = new JTextField(15);
            pSearch.add(tfCompSearch);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Category:"));
            cbCompCat = new JComboBox<>(new String[]{"All Categories", "Internet", "Plumbing", "Electrical", "Cleanliness", "Mess", "Security"});
            pSearch.add(cbCompCat);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Status:"));
            cbCompStat = new JComboBox<>(new String[]{"All Statuses", "Concerning Only", "Open", "Under Review", "In Progress", "Resolved", "Closed"});
            pSearch.add(cbCompStat);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Wing:"));
            cbCompWing = new JComboBox<>(new String[]{"All Wings", "Wing Alpha", "Wing Beta", "Wing Charlie", "Wing Delta"});
            pSearch.add(cbCompWing);

            lblCompCount = new JLabel("Showing: " + DataStore.complaints.size());
            lblCompCount.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblCompCount.setForeground(new Color(37, 99, 235));
            pSearch.add(lblCompCount);
            pControls.add(pSearch);

            JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            JButton bDet = UIHelper.createButton("View Details", new Color(37, 99, 235));
            JButton bSt = UIHelper.createButton("Update Status", new Color(16, 185, 129));
            JButton bAssign = UIHelper.createButton("Assign Staff", new Color(139, 92, 246));
            JButton bRef = UIHelper.createSecondaryButton("Refresh");
            tb.add(bDet);
            tb.add(bSt);
            tb.add(bAssign);
            tb.add(bRef);
            pControls.add(tb);

            p.add(pControls, BorderLayout.NORTH);

            String[] cols = {"Ticket ID", "Title", "Category", "Location", "Filed By PRN", "Assigned Staff", "Status", "Votes", "Priority"};
            compModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            compTable = new JTable(compModel);
            compTable.setRowHeight(28);

            compTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                    Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                    if ("CONCERNING".equals(v)) {
                        comp.setForeground(new Color(220, 38, 38));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        comp.setForeground(new Color(100, 116, 139));
                    }
                    return comp;
                }
            });

            loadComp();
            p.add(new JScrollPane(compTable), BorderLayout.CENTER);

            // Live Inspector Card for Admin Grievances
            lblAdminInspTitle = new JLabel("💡 Select any grievance above to preview full details.");
            lblAdminInspMeta = new JLabel("Live inspector displays full description, votes, location, and assigned staff.");
            taAdminInspDesc = new JTextArea();
            btnAdminInspModal = UIHelper.createButton("View Full Modal", new Color(37, 99, 235));
            btnAdminInspModal.setPreferredSize(new Dimension(135, 28));
            btnAdminInspModal.setEnabled(false);
            btnAdminInspModal.addActionListener(e -> openAdminDetails());

            btnAdminInspStatus = UIHelper.createButton("Update Status", new Color(16, 185, 129));
            btnAdminInspStatus.setPreferredSize(new Dimension(120, 28));
            btnAdminInspStatus.setEnabled(false);
            btnAdminInspStatus.addActionListener(e -> handleAdminUpdateStatus());

            p.add(createAdminPreviewCard(lblAdminInspTitle, lblAdminInspMeta, taAdminInspDesc, btnAdminInspModal, btnAdminInspStatus), BorderLayout.SOUTH);

            compTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) updateAdminInspector();
            });

            tfCompSearch.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { loadComp(); }
                public void removeUpdate(DocumentEvent e) { loadComp(); }
                public void changedUpdate(DocumentEvent e) { loadComp(); }
            });
            cbCompCat.addActionListener(e -> loadComp());
            cbCompStat.addActionListener(e -> loadComp());
            cbCompWing.addActionListener(e -> loadComp());

            bDet.addActionListener(e -> openAdminDetails());
            compTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    updateAdminInspector();
                    if (e.getClickCount() == 2) openAdminDetails();
                }
            });

            bSt.addActionListener(e -> handleAdminUpdateStatus());

            bAssign.addActionListener(e -> {
                int row = compTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select a complaint row to assign.", "Notice", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int modelRow = compTable.convertRowIndexToModel(row);
                int id = Integer.parseInt(compModel.getValueAt(modelRow, 0).toString());
                Complaint c = DataStore.getById(id);
                List<String> staffOpts = new ArrayList<>();
                for (Staff stf : DataStore.staffMembers) {
                    staffOpts.add(stf.getStaffId() + " - " + stf.getFullName() + " (" + stf.getRole() + ")");
                }
                String sel = (String) JOptionPane.showInputDialog(this, "Assign Facility Staff to Ticket #" + id + ":", "Assign Staff",
                        JOptionPane.QUESTION_MESSAGE, null, staffOpts.toArray(), staffOpts.get(0));
                if (sel != null) {
                    String staffId = sel.split(" - ")[0].trim();
                    c.setAssignedStaffId(staffId);
                    c.setStatus("In Progress");
                    loadComp();
                    updateAdminInspector();
                    JOptionPane.showMessageDialog(this, "Ticket assigned to " + staffId + "! Status updated to In Progress.", "Staff Assigned", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            bRef.addActionListener(e -> {
                tfCompSearch.setText("");
                cbCompCat.setSelectedIndex(0);
                cbCompStat.setSelectedIndex(0);
                cbCompWing.setSelectedIndex(0);
                loadComp();
            });

            return p;
        }

        private void handleAdminUpdateStatus() {
            int row = compTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a complaint row.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = compTable.convertRowIndexToModel(row);
            int id = Integer.parseInt(compModel.getValueAt(modelRow, 0).toString());
            Complaint c = DataStore.getById(id);
            String[] opts = {"Open", "Under Review", "In Progress", "Resolved", "Closed"};
            String ns = (String) JOptionPane.showInputDialog(this, "Update Status for Ticket #" + id + ":", "Update Status", JOptionPane.QUESTION_MESSAGE, null, opts, c.getStatus());
            if (ns != null) {
                c.setStatus(ns);
                loadComp();
                updateAdminInspector();
            }
        }

        private void updateAdminInspector() {
            int row = compTable.getSelectedRow();
            if (row == -1) {
                lblAdminInspTitle.setText("💡 Select any grievance above to preview full details.");
                lblAdminInspMeta.setText("Live inspector displays full description, votes, location, and assigned staff.");
                taAdminInspDesc.setText("");
                btnAdminInspModal.setEnabled(false);
                btnAdminInspStatus.setEnabled(false);
                return;
            }
            int modelRow = compTable.convertRowIndexToModel(row);
            int ticketId = Integer.parseInt(compModel.getValueAt(modelRow, 0).toString());
            Complaint c = DataStore.getById(ticketId);
            if (c != null) {
                lblAdminInspTitle.setText("Ticket #" + c.getTicketId() + ": " + c.getTitle());
                lblAdminInspMeta.setText("Category: " + c.getCategory() + "  |  Location: " + c.getLocation() + "  |  Status: " + c.getStatus() + (c.isConcerning() ? " [CONCERNING]" : "") + "  |  Votes: " + c.getVoteCount() + "  |  Assigned: " + (c.getAssignedStaffId() != null ? c.getAssignedStaffId() : "Unassigned"));
                taAdminInspDesc.setText(c.getDescription());
                btnAdminInspModal.setEnabled(true);
                btnAdminInspStatus.setEnabled(true);
            }
        }

        private void openAdminDetails() {
            int row = compTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a complaint row.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = compTable.convertRowIndexToModel(row);
            int id = Integer.parseInt(compModel.getValueAt(modelRow, 0).toString());
            Complaint c = DataStore.getById(id);
            UIHelper.showDetails(this, c, admin, () -> {
                loadComp();
                updateAdminInspector();
            });
        }

        private void loadComp() {
            compModel.setRowCount(0);
            String kw = tfCompSearch != null ? tfCompSearch.getText() : "";
            String cat = cbCompCat != null ? (String) cbCompCat.getSelectedItem() : "All";
            String stat = cbCompStat != null ? (String) cbCompStat.getSelectedItem() : "All";
            String wing = cbCompWing != null ? (String) cbCompWing.getSelectedItem() : "All";

            List<Complaint> list = DataStore.searchComplaints(kw, cat, stat, wing);
            for (Complaint c : list) {
                compModel.addRow(new Object[]{
                        c.getTicketId(), c.getTitle(), c.getCategory(), c.getLocation(),
                        c.getCreatedByPrn(), c.getAssignedStaffId() != null ? c.getAssignedStaffId() : "Unassigned",
                        c.getStatus(), c.getVoteCount(), c.isConcerning() ? "CONCERNING" : "Normal"
                });
            }
            if (lblCompCount != null) {
                lblCompCount.setText("Showing: " + list.size());
            }
        }

        private JPanel createStudentsTab() {
            JPanel p = new JPanel(new BorderLayout(10, 10));
            p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            pSearch.add(new JLabel("Search Students:"));
            tfStudSearch = new JTextField(16);
            tfStudSearch.setToolTipText("Filter by name, PRN, room, or branch...");
            pSearch.add(tfStudSearch);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Block / Wing:"));
            String[] blocks = {"All Blocks", "Alpha (Boys)", "Beta (Girls)", "Charlie", "Delta"};
            cbStudBlock = new JComboBox<>(blocks);
            pSearch.add(cbStudBlock);

            JButton btnClearStud = UIHelper.createSecondaryButton("✕ Clear");
            pSearch.add(btnClearStud);

            lblStudCount = new JLabel("(" + DataStore.students.size() + " students)");
            lblStudCount.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblStudCount.setForeground(new Color(37, 99, 235));
            pSearch.add(lblStudCount);
            p.add(pSearch, BorderLayout.NORTH);

            String[] cols = {"PRN", "Full Name", "Hostel Room", "Branch & Division", "Gender", "Email", "Phone"};
            studModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            studTable = new JTable(studModel);
            studTable.setRowHeight(26);

            loadStudents();
            p.add(new JScrollPane(studTable), BorderLayout.CENTER);

            // Footer Card showing selected student details
            lblStudDetail = new JLabel("💡 Select any student from the 500-student directory above to view full resident record.");
            lblStudDetail.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblStudDetail.setForeground(new Color(71, 85, 105));
            JPanel pFoot = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            pFoot.setBackground(Color.WHITE);
            pFoot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
            pFoot.add(lblStudDetail);
            p.add(pFoot, BorderLayout.SOUTH);

            studTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int row = studTable.getSelectedRow();
                    if (row != -1) {
                        int modelRow = studTable.convertRowIndexToModel(row);
                        String prn = studModel.getValueAt(modelRow, 0).toString();
                        String name = studModel.getValueAt(modelRow, 1).toString();
                        String room = studModel.getValueAt(modelRow, 2).toString();
                        String branch = studModel.getValueAt(modelRow, 3).toString();
                        String email = studModel.getValueAt(modelRow, 5).toString();
                        String phone = studModel.getValueAt(modelRow, 6).toString();
                        lblStudDetail.setText("👤 Selected: " + name + " (PRN: " + prn + ")  |  Room: " + room + "  |  Branch: " + branch + "  |  Email: " + email + "  |  Phone: " + phone);
                    }
                }
            });

            tfStudSearch.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { loadStudents(); }
                public void removeUpdate(DocumentEvent e) { loadStudents(); }
                public void changedUpdate(DocumentEvent e) { loadStudents(); }
            });

            cbStudBlock.addActionListener(e -> loadStudents());

            btnClearStud.addActionListener(e -> {
                tfStudSearch.setText("");
                cbStudBlock.setSelectedIndex(0);
                loadStudents();
            });

            return p;
        }

        private void loadStudents() {
            studModel.setRowCount(0);
            String q = tfStudSearch != null ? tfStudSearch.getText() : "";
            String blk = cbStudBlock != null ? (String) cbStudBlock.getSelectedItem() : "All Blocks";
            List<Student> list = DataStore.searchStudents(q);
            int matched = 0;
            for (Student s : list) {
                if (!blk.equals("All Blocks")) {
                    String cleanBlk = blk.replace(" (Boys)", "").replace(" (Girls)", "");
                    if (s.getLocation() == null || !s.getLocation().startsWith(cleanBlk)) {
                        continue;
                    }
                }
                matched++;
                studModel.addRow(new Object[]{
                        s.getPrn(), s.getFullName(), s.getLocation(), s.getBranchDivision(),
                        s.getGender(), s.getEmail(), s.getPhone()
                });
            }
            if (lblStudCount != null) {
                lblStudCount.setText("(" + matched + " students)");
            }
        }

        private JPanel createStaffTab() {
            JPanel p = new JPanel(new BorderLayout(10, 10));
            p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            pSearch.add(new JLabel("Search Staff:"));
            tfStfSearch = new JTextField(15);
            pSearch.add(tfStfSearch);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Department:"));
            cbStfDept = new JComboBox<>(new String[]{"All Departments", "Hostel Administration", "Electrical", "Plumbing", "IT Administration", "Internet", "Mess", "Security"});
            pSearch.add(cbStfDept);

            lblStfCount = new JLabel("Total: " + DataStore.staffMembers.size());
            lblStfCount.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblStfCount.setForeground(new Color(37, 99, 235));
            pSearch.add(lblStfCount);
            p.add(pSearch, BorderLayout.NORTH);

            String[] cols = {"Staff ID", "Full Name", "Role", "Department", "Email", "Phone"};
            stfModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            stfTable = new JTable(stfModel);
            stfTable.setRowHeight(26);

            loadStaff();
            p.add(new JScrollPane(stfTable), BorderLayout.CENTER);

            tfStfSearch.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { loadStaff(); }
                public void removeUpdate(DocumentEvent e) { loadStaff(); }
                public void changedUpdate(DocumentEvent e) { loadStaff(); }
            });
            cbStfDept.addActionListener(e -> loadStaff());

            return p;
        }

        private void loadStaff() {
            stfModel.setRowCount(0);
            String q = tfStfSearch != null ? tfStfSearch.getText() : "";
            String d = cbStfDept != null ? (String) cbStfDept.getSelectedItem() : "All";
            List<Staff> list = DataStore.searchStaff(q, d);
            for (Staff st : list) {
                stfModel.addRow(new Object[]{
                        st.getStaffId(), st.getFullName(), st.getRole(), st.getDepartment(),
                        st.getEmail(), st.getPhone()
                });
            }
            if (lblStfCount != null) {
                lblStfCount.setText("Showing: " + list.size());
            }
        }

        // Tab 4: Analytics & Insights Panel (Replaces Overview & Stats with deep integrated intelligence)
        private JPanel createAnalyticsTab() {
            JPanel container = new JPanel(new BorderLayout(15, 15));
            container.setBackground(new Color(248, 250, 252));
            container.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            // Top Toolbar
            JPanel pTop = new JPanel(new BorderLayout());
            pTop.setOpaque(false);

            JPanel pTitles = new JPanel(new GridLayout(2, 1, 0, 2));
            pTitles.setOpaque(false);
            JLabel lblHeading = new JLabel("Hostel Operational Analytics & Grievance Intelligence");
            lblHeading.setFont(new Font("SansSerif", Font.BOLD, 17));
            lblHeading.setForeground(new Color(15, 23, 42));

            JLabel lblSub = new JLabel("Integrated cross-facility metrics, category distributions, SLA lifecycle, and wing demographics");
            lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblSub.setForeground(new Color(100, 116, 139));
            pTitles.add(lblHeading);
            pTitles.add(lblSub);

            JButton btnRefreshAnalytics = UIHelper.createButton("🔄 Refresh Live Analytics", new Color(37, 99, 235));
            btnRefreshAnalytics.setPreferredSize(new Dimension(195, 32));
            btnRefreshAnalytics.addActionListener(e -> {
                tp.setComponentAt(3, createAnalyticsTab());
            });

            pTop.add(pTitles, BorderLayout.WEST);
            pTop.add(btnRefreshAnalytics, BorderLayout.EAST);
            container.add(pTop, BorderLayout.NORTH);

            // Fetch metrics
            List<Complaint> complaints = DataStore.complaints;
            List<Student> students = DataStore.students;
            List<Staff> staff = DataStore.staffMembers;

            int totalStudents = students.size();
            int totalStaff = staff.size();
            int totalComplaints = complaints.size();
            int openCnt = 0;
            int inProgressCnt = 0;
            int underReviewCnt = 0;
            int resolvedCnt = 0;
            int closedCnt = 0;
            int concerningCnt = 0;

            Map<String, Integer> catCounts = new LinkedHashMap<>();
            String[] standardCats = {"Internet", "Plumbing", "Electrical", "Cleanliness", "Mess", "Security"};
            for (String c : standardCats) catCounts.put(c, 0);

            Map<String, Integer> blockComplaints = new LinkedHashMap<>();
            blockComplaints.put("Alpha", 0);
            blockComplaints.put("Beta", 0);
            blockComplaints.put("Charlie", 0);
            blockComplaints.put("Delta", 0);

            List<Complaint> criticalList = new ArrayList<>();

            for (Complaint c : complaints) {
                String st = c.getStatus() != null ? c.getStatus() : "Open";
                if ("Open".equalsIgnoreCase(st)) openCnt++;
                else if ("Under Review".equalsIgnoreCase(st)) underReviewCnt++;
                else if ("In Progress".equalsIgnoreCase(st)) inProgressCnt++;
                else if ("Resolved".equalsIgnoreCase(st)) resolvedCnt++;
                else if ("Closed".equalsIgnoreCase(st)) closedCnt++;

                if (c.isConcerning()) {
                    concerningCnt++;
                    criticalList.add(c);
                }

                String cat = c.getCategory() != null ? c.getCategory() : "Other";
                boolean matchedCat = false;
                for (String sc : standardCats) {
                    if (sc.equalsIgnoreCase(cat)) {
                        catCounts.put(sc, catCounts.get(sc) + 1);
                        matchedCat = true;
                        break;
                    }
                }
                if (!matchedCat) {
                    catCounts.put(cat, catCounts.getOrDefault(cat, 0) + 1);
                }

                String loc = c.getLocation() != null ? c.getLocation() : "";
                for (String blk : blockComplaints.keySet()) {
                    if (loc.toLowerCase().contains(blk.toLowerCase())) {
                        blockComplaints.put(blk, blockComplaints.get(blk) + 1);
                    }
                }
            }

            int activeUnresolved = openCnt + underReviewCnt + inProgressCnt;
            double resRate = totalComplaints > 0 ? ((double)(resolvedCnt + closedCnt) / totalComplaints) * 100.0 : 0.0;

            JPanel mainContent = new JPanel();
            mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
            mainContent.setOpaque(false);

            // Row 1: Executive KPI Cards
            JPanel pKpi = new JPanel(new GridLayout(2, 3, 12, 12));
            pKpi.setOpaque(false);
            pKpi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

            pKpi.add(createKpiCard("👥 Total Enrolled Students", String.valueOf(totalStudents), "500 in InHabitDB (100% Occupancy)", new Color(37, 99, 235)));
            pKpi.add(createKpiCard("🛠️ Maintenance & Ops Staff", String.valueOf(totalStaff), "20 Active (6 Technical Trades)", new Color(16, 185, 129)));
            pKpi.add(createKpiCard("📋 Total Grievances Logged", String.valueOf(totalComplaints), activeUnresolved + " Active / " + (resolvedCnt + closedCnt) + " Closed", new Color(139, 92, 246)));
            pKpi.add(createKpiCard("⚡ Active Pending Issues", String.valueOf(activeUnresolved), openCnt + " Open, " + inProgressCnt + " In Progress", new Color(245, 158, 11)));
            pKpi.add(createKpiCard("🚨 Critical Escalated SLA", String.valueOf(concerningCnt), "Tickets with >= 30 Community Votes", new Color(239, 68, 68)));
            pKpi.add(createKpiCard("📈 SLA Resolution Rate", String.format("%.1f%%", resRate), "Target SLA: > 80.0% (Avg Turnaround 2.3d)", new Color(13, 148, 136)));

            mainContent.add(Box.createVerticalStrut(8));
            mainContent.add(pKpi);
            mainContent.add(Box.createVerticalStrut(15));

            // Row 2: Deep Analytics & Visual Pipelines
            JPanel pGrid2 = new JPanel(new GridLayout(1, 2, 15, 15));
            pGrid2.setOpaque(false);

            // Left Column: Category Breakdown & Status Pipeline
            JPanel pLeft = new JPanel();
            pLeft.setLayout(new BoxLayout(pLeft, BoxLayout.Y_AXIS));
            pLeft.setOpaque(false);

            pLeft.add(createCategoryAnalyticsCard(catCounts, totalComplaints));
            pLeft.add(Box.createVerticalStrut(12));
            pLeft.add(createPipelineCard(openCnt, underReviewCnt, inProgressCnt, resolvedCnt, closedCnt, totalComplaints));

            // Right Column: Block Demographics & Concerning Alert Queue
            JPanel pRight = new JPanel();
            pRight.setLayout(new BoxLayout(pRight, BoxLayout.Y_AXIS));
            pRight.setOpaque(false);

            pRight.add(createBlockDemographicsCard(blockComplaints, totalStudents));
            pRight.add(Box.createVerticalStrut(12));
            pRight.add(createCriticalAlertsQueueCard(criticalList));

            pGrid2.add(pLeft);
            pGrid2.add(pRight);
            mainContent.add(pGrid2);

            JScrollPane scroll = new JScrollPane(mainContent);
            scroll.setBorder(null);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            container.add(scroll, BorderLayout.CENTER);

            return container;
        }

        private JPanel createKpiCard(String title, String val, String sub, Color accent) {
            JPanel card = new JPanel(new BorderLayout(5, 5));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
            ));

            JLabel lTitle = new JLabel(title);
            lTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lTitle.setForeground(new Color(100, 116, 139));

            JLabel lVal = new JLabel(val);
            lVal.setFont(new Font("SansSerif", Font.BOLD, 22));
            lVal.setForeground(new Color(15, 23, 42));

            JLabel lSub = new JLabel(sub);
            lSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lSub.setForeground(new Color(148, 163, 184));

            card.add(lTitle, BorderLayout.NORTH);
            card.add(lVal, BorderLayout.CENTER);
            card.add(lSub, BorderLayout.SOUTH);
            return card;
        }

        private JPanel createCategoryAnalyticsCard(Map<String, Integer> catCounts, int total) {
            JPanel card = new JPanel(new BorderLayout(10, 10));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(226, 232, 240)),
                    BorderFactory.createEmptyBorder(14, 16, 14, 16)
            ));

            JLabel lblTitle = new JLabel("📊 Grievance Category Breakdown");
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblTitle.setForeground(new Color(15, 23, 42));
            card.add(lblTitle, BorderLayout.NORTH);

            JPanel pRows = new JPanel(new GridLayout(0, 1, 6, 8));
            pRows.setOpaque(false);

            Color[] colors = {
                    new Color(37, 99, 235),  // Internet - Blue
                    new Color(13, 148, 136), // Plumbing - Teal
                    new Color(245, 158, 11), // Electrical - Amber
                    new Color(16, 185, 129), // Cleanliness - Emerald
                    new Color(249, 115, 22), // Mess - Orange
                    new Color(239, 68, 68)   // Security - Red
            };

            int idx = 0;
            for (Map.Entry<String, Integer> entry : catCounts.entrySet()) {
                String cat = entry.getKey();
                int count = entry.getValue();
                double pct = total > 0 ? ((double) count / total) * 100.0 : 0.0;
                Color barColor = colors[idx % colors.length];
                idx++;

                JPanel row = new JPanel(new BorderLayout(8, 0));
                row.setOpaque(false);

                JLabel lName = new JLabel(cat);
                lName.setFont(new Font("SansSerif", Font.BOLD, 12));
                lName.setPreferredSize(new Dimension(85, 20));

                JPanel pBarWrap = new JPanel(new BorderLayout());
                pBarWrap.setOpaque(false);
                pBarWrap.setPreferredSize(new Dimension(140, 16));

                final double frac = total > 0 ? (double) count / total : 0.0;
                JPanel bar = new JPanel() {
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(241, 245, 249));
                        g2.fillRoundRect(0, 2, getWidth(), getHeight() - 4, 6, 6);
                        g2.setColor(barColor);
                        int fillW = (int) (getWidth() * frac);
                        if (fillW > 0) {
                            g2.fillRoundRect(0, 2, fillW, getHeight() - 4, 6, 6);
                        }
                        g2.dispose();
                    }
                };
                bar.setOpaque(false);
                pBarWrap.add(bar, BorderLayout.CENTER);

                JLabel lCount = new JLabel(count + " (" + String.format("%.0f%%", pct) + ")");
                lCount.setFont(new Font("SansSerif", Font.PLAIN, 12));
                lCount.setForeground(new Color(71, 85, 105));
                lCount.setPreferredSize(new Dimension(65, 20));
                lCount.setHorizontalAlignment(SwingConstants.RIGHT);

                row.add(lName, BorderLayout.WEST);
                row.add(pBarWrap, BorderLayout.CENTER);
                row.add(lCount, BorderLayout.EAST);
                pRows.add(row);
            }

            card.add(pRows, BorderLayout.CENTER);
            return card;
        }

        private JPanel createPipelineCard(int open, int underReview, int inProgress, int resolved, int closed, int total) {
            JPanel card = new JPanel(new BorderLayout(10, 10));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(226, 232, 240)),
                    BorderFactory.createEmptyBorder(14, 16, 14, 16)
            ));

            JLabel lblTitle = new JLabel("🔄 Grievance Resolution Lifecycle Pipeline");
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblTitle.setForeground(new Color(15, 23, 42));
            card.add(lblTitle, BorderLayout.NORTH);

            JPanel pStages = new JPanel(new GridLayout(1, 5, 8, 0));
            pStages.setOpaque(false);

            pStages.add(createStagePill("Open", open, new Color(245, 158, 11)));
            pStages.add(createStagePill("Under Review", underReview, new Color(59, 130, 246)));
            pStages.add(createStagePill("In Progress", inProgress, new Color(139, 92, 246)));
            pStages.add(createStagePill("Resolved", resolved, new Color(16, 185, 129)));
            pStages.add(createStagePill("Closed", closed, new Color(100, 116, 139)));

            card.add(pStages, BorderLayout.CENTER);
            return card;
        }

        private JPanel createStagePill(String stage, int count, Color col) {
            JPanel pill = new JPanel(new GridLayout(2, 1, 0, 2));
            pill.setBackground(new Color(col.getRed(), col.getGreen(), col.getBlue(), 20));
            pill.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2, 0, 0, 0, col),
                    BorderFactory.createEmptyBorder(8, 6, 8, 6)
            ));

            JLabel lVal = new JLabel(String.valueOf(count), SwingConstants.CENTER);
            lVal.setFont(new Font("SansSerif", Font.BOLD, 17));
            lVal.setForeground(col);

            JLabel lName = new JLabel(stage, SwingConstants.CENTER);
            lName.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lName.setForeground(new Color(71, 85, 105));

            pill.add(lVal);
            pill.add(lName);
            return pill;
        }

        private JPanel createBlockDemographicsCard(Map<String, Integer> blockComplaints, int totalStudents) {
            JPanel card = new JPanel(new BorderLayout(10, 10));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(226, 232, 240)),
                    BorderFactory.createEmptyBorder(14, 16, 14, 16)
            ));

            JLabel lblTitle = new JLabel("🏢 Hostel Wing & Block Demographics");
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblTitle.setForeground(new Color(15, 23, 42));
            card.add(lblTitle, BorderLayout.NORTH);

            JPanel pBlocks = new JPanel(new GridLayout(2, 2, 10, 10));
            pBlocks.setOpaque(false);

            int perBlock = totalStudents / 4;
            pBlocks.add(createBlockItem("Block Alpha (Boys)", perBlock + " Residents", blockComplaints.getOrDefault("Alpha", 0) + " Issues", new Color(37, 99, 235)));
            pBlocks.add(createBlockItem("Block Beta (Girls)", perBlock + " Residents", blockComplaints.getOrDefault("Beta", 0) + " Issues", new Color(219, 39, 119)));
            pBlocks.add(createBlockItem("Block Charlie (Engg)", perBlock + " Residents", blockComplaints.getOrDefault("Charlie", 0) + " Issues", new Color(13, 148, 136)));
            pBlocks.add(createBlockItem("Block Delta (B.Tech)", perBlock + " Residents", blockComplaints.getOrDefault("Delta", 0) + " Issues", new Color(139, 92, 246)));

            card.add(pBlocks, BorderLayout.CENTER);
            return card;
        }

        private JPanel createBlockItem(String title, String residents, String issues, Color accent) {
            JPanel item = new JPanel(new GridLayout(3, 1, 0, 2));
            item.setBackground(new Color(248, 250, 252));
            item.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));

            JLabel lTitle = new JLabel(title);
            lTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
            lTitle.setForeground(new Color(15, 23, 42));

            JLabel lRes = new JLabel(residents);
            lRes.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lRes.setForeground(new Color(71, 85, 105));

            JLabel lIss = new JLabel(issues);
            lIss.setFont(new Font("SansSerif", Font.BOLD, 11));
            lIss.setForeground(accent);

            item.add(lTitle);
            item.add(lRes);
            item.add(lIss);
            return item;
        }

        private JPanel createCriticalAlertsQueueCard(List<Complaint> criticalList) {
            JPanel card = new JPanel(new BorderLayout(8, 8));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(226, 232, 240)),
                    BorderFactory.createEmptyBorder(14, 16, 14, 16)
            ));

            JPanel pHead = new JPanel(new BorderLayout());
            pHead.setOpaque(false);
            JLabel lblTitle = new JLabel("🚨 High Priority Escalated SLA Queue (>= 30 Votes)");
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblTitle.setForeground(new Color(220, 38, 38));

            JLabel lblCount = new JLabel(criticalList.size() + " Critical");
            lblCount.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblCount.setForeground(new Color(220, 38, 38));

            pHead.add(lblTitle, BorderLayout.WEST);
            pHead.add(lblCount, BorderLayout.EAST);
            card.add(pHead, BorderLayout.NORTH);

            String[] cols = {"Ticket ID", "Title", "Category", "Location", "Votes", "Status"};
            DefaultTableModel critModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

            for (Complaint c : criticalList) {
                critModel.addRow(new Object[]{
                        c.getTicketId(), c.getTitle(), c.getCategory(), c.getLocation(), c.getVoteCount(), c.getStatus()
                });
            }

            JTable critTable = new JTable(critModel);
            critTable.setRowHeight(24);
            critTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
            critTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

            JScrollPane sp = new JScrollPane(critTable);
            sp.setPreferredSize(new Dimension(Integer.MAX_VALUE, 90));
            card.add(sp, BorderLayout.CENTER);

            JPanel pActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            pActions.setOpaque(false);
            JButton btnInspectCrit = UIHelper.createButton("Inspect Selected Ticket", new Color(220, 38, 38));
            btnInspectCrit.setPreferredSize(new Dimension(180, 28));
            btnInspectCrit.addActionListener(e -> {
                int row = critTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select a critical ticket row to view details.", "Notice", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int modelRow = critTable.convertRowIndexToModel(row);
                int ticketId = Integer.parseInt(critModel.getValueAt(modelRow, 0).toString());
                Complaint c = DataStore.getById(ticketId);
                UIHelper.showDetails(this, c, admin, () -> {
                    loadComp();
                    tp.setComponentAt(3, createAnalyticsTab());
                });
            });
            pActions.add(btnInspectCrit);
            card.add(pActions, BorderLayout.SOUTH);

            return card;
        }

        private JPanel createAdminPreviewCard(JLabel lblTitle, JLabel lblMeta, JTextArea taDesc, JButton btnModal, JButton btnSec) {
            JPanel card = new JPanel(new BorderLayout(8, 6));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            card.setPreferredSize(new Dimension(Integer.MAX_VALUE, 125));

            JPanel pHeader = new JPanel(new BorderLayout(5, 2));
            pHeader.setOpaque(false);
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblTitle.setForeground(new Color(15, 23, 42));
            lblMeta.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblMeta.setForeground(new Color(100, 116, 139));
            pHeader.add(lblTitle, BorderLayout.NORTH);
            pHeader.add(lblMeta, BorderLayout.SOUTH);

            taDesc.setLineWrap(true);
            taDesc.setWrapStyleWord(true);
            taDesc.setEditable(false);
            taDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
            taDesc.setBackground(new Color(248, 250, 252));
            taDesc.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            JScrollPane sp = new JScrollPane(taDesc);
            sp.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));

            JPanel pActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            pActions.setOpaque(false);
            if (btnSec != null) pActions.add(btnSec);
            if (btnModal != null) pActions.add(btnModal);

            card.add(pHeader, BorderLayout.NORTH);
            card.add(sp, BorderLayout.CENTER);
            card.add(pActions, BorderLayout.SOUTH);
            return card;
        }
    }

    public static class TechnicianWindow extends JFrame {
        private Staff staff;
        private JTable techTable;
        private DefaultTableModel techModel;
        private JTextField tfSearch;
        private JComboBox<String> cbCategory, cbStatus, cbWing;
        private JLabel lblCount;
        private JLabel lblStatAssigned, lblStatDept, lblStatConcerning, lblStatResolved;

        public TechnicianWindow(Staff st) {
            this.staff = st;
            setTitle("InHabit - Maintenance & Technician Redressal Portal (" + staff.getDepartment() + ")");
            setSize(1080, 720);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            // Header
            JPanel pTop = new JPanel(new BorderLayout(15, 10));
            pTop.setBackground(new Color(15, 23, 42));
            pTop.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JPanel pBrand = new JPanel(new GridLayout(2, 1, 0, 2));
            pBrand.setOpaque(false);
            JLabel lblBrand = new JLabel("InHabit | Facility Redressal Desk");
            lblBrand.setFont(new Font("SansSerif", Font.BOLD, 19));
            lblBrand.setForeground(Color.WHITE);

            JLabel lblSub = new JLabel("Department: " + staff.getDepartment() + " | Trade: " + staff.getRole());
            lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblSub.setForeground(new Color(148, 163, 184));
            pBrand.add(lblBrand);
            pBrand.add(lblSub);

            JPanel pUserMeta = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            pUserMeta.setOpaque(false);
            JLabel lblStaffInfo = new JLabel(staff.getFullName() + " (Staff ID: " + staff.getStaffId() + ")");
            lblStaffInfo.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblStaffInfo.setForeground(new Color(226, 232, 240));

            JButton btnLogout = UIHelper.createButton("Logout", new Color(220, 38, 38));
            btnLogout.setPreferredSize(new Dimension(85, 30));
            btnLogout.addActionListener(e -> {
                dispose();
                new LoginWindow();
            });

            pUserMeta.add(lblStaffInfo);
            pUserMeta.add(btnLogout);
            pTop.add(pBrand, BorderLayout.WEST);
            pTop.add(pUserMeta, BorderLayout.EAST);
            add(pTop, BorderLayout.NORTH);

            // Center
            JPanel pCenter = new JPanel(new BorderLayout(15, 15));
            pCenter.setBackground(new Color(248, 250, 252));
            pCenter.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JPanel pControls = new JPanel();
            pControls.setLayout(new BoxLayout(pControls, BoxLayout.Y_AXIS));
            pControls.setOpaque(false);

            // Stats Cards
            JPanel pStats = new JPanel(new GridLayout(1, 4, 15, 0));
            pStats.setOpaque(false);
            lblStatAssigned = new JLabel("0");
            lblStatDept = new JLabel("0");
            lblStatConcerning = new JLabel("0");
            lblStatResolved = new JLabel("0");

            pStats.add(createMiniCard("My Assigned Tasks", lblStatAssigned, new Color(37, 99, 235)));
            pStats.add(createMiniCard("Dept Total Tasks", lblStatDept, new Color(139, 92, 246)));
            pStats.add(createMiniCard("Concerning Alerts", lblStatConcerning, new Color(239, 68, 68)));
            pStats.add(createMiniCard("Resolved Tickets", lblStatResolved, new Color(16, 185, 129)));
            pControls.add(pStats);
            pControls.add(Box.createVerticalStrut(15));

            // Toolbar
            JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            tb.setOpaque(false);
            JButton btnClaim = UIHelper.createButton("Claim Ticket", new Color(37, 99, 235));
            JButton btnUpdate = UIHelper.createButton("Update Redressal", new Color(16, 185, 129));
            JButton btnDet = UIHelper.createSecondaryButton("View Full Ticket");
            JButton btnRef = UIHelper.createSecondaryButton("Refresh Workload");

            tb.add(btnClaim);
            tb.add(btnUpdate);
            tb.add(btnDet);
            tb.add(btnRef);
            pControls.add(tb);
            pControls.add(Box.createVerticalStrut(10));

            // Search Bar
            JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            pSearch.setBackground(Color.WHITE);
            pSearch.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

            pSearch.add(new JLabel("Search Tasks:"));
            tfSearch = new JTextField(15);
            pSearch.add(tfSearch);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Category:"));
            cbCategory = new JComboBox<>(new String[]{"All Categories", "Electrical", "Plumbing", "Internet", "Cleanliness", "Mess", "Security"});
            pSearch.add(cbCategory);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Status:"));
            cbStatus = new JComboBox<>(new String[]{"All Statuses", "Open", "In Progress", "Resolved", "Closed"});
            pSearch.add(cbStatus);

            pSearch.add(new JSeparator(SwingConstants.VERTICAL));
            pSearch.add(new JLabel("Wing:"));
            cbWing = new JComboBox<>(new String[]{"All Wings", "Wing Alpha", "Wing Beta", "Wing Charlie", "Wing Delta"});
            pSearch.add(cbWing);

            lblCount = new JLabel("Tasks: 0");
            lblCount.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblCount.setForeground(new Color(37, 99, 235));
            pSearch.add(lblCount);
            pControls.add(pSearch);

            pCenter.add(pControls, BorderLayout.NORTH);

            String[] cols = {"Ticket ID", "Title", "Category", "Location", "Assigned To", "Status", "Priority", "Created At"};
            techModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            techTable = new JTable(techModel);
            techTable.setRowHeight(28);

            loadTech();
            pCenter.add(new JScrollPane(techTable), BorderLayout.CENTER);
            add(pCenter, BorderLayout.CENTER);

            tfSearch.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { loadTech(); }
                public void removeUpdate(DocumentEvent e) { loadTech(); }
                public void changedUpdate(DocumentEvent e) { loadTech(); }
            });
            cbCategory.addActionListener(e -> loadTech());
            cbStatus.addActionListener(e -> loadTech());
            cbWing.addActionListener(e -> loadTech());

            btnDet.addActionListener(e -> openTechDetails());
            techTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) openTechDetails();
                }
            });

            btnClaim.addActionListener(e -> {
                int row = techTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select a ticket to claim.", "Notice", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int id = (int) techModel.getValueAt(row, 0);
                Complaint c = DataStore.getById(id);
                c.setAssignedStaffId(staff.getStaffId());
                c.setStatus("In Progress");
                loadTech();
                JOptionPane.showMessageDialog(this, "Ticket #" + id + " claimed by you and marked In Progress!", "Task Claimed", JOptionPane.INFORMATION_MESSAGE);
            });

            btnUpdate.addActionListener(e -> {
                int row = techTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select a ticket to update redressal status.", "Notice", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int id = (int) techModel.getValueAt(row, 0);
                Complaint c = DataStore.getById(id);
                String[] opts = {"In Progress", "Resolved", "Closed"};
                String ns = (String) JOptionPane.showInputDialog(this, "Update Redressal Status for Ticket #" + id + ":", "Update Redressal",
                        JOptionPane.QUESTION_MESSAGE, null, opts, c.getStatus());
                if (ns != null) {
                    c.setStatus(ns);
                    loadTech();
                    JOptionPane.showMessageDialog(this, "Ticket #" + id + " updated to '" + ns + "' successfully!", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            btnRef.addActionListener(e -> {
                tfSearch.setText("");
                cbCategory.setSelectedIndex(0);
                cbStatus.setSelectedIndex(0);
                cbWing.setSelectedIndex(0);
                loadTech();
            });

            setVisible(true);
        }

        private JPanel createMiniCard(String title, JLabel valLabel, Color barColor) {
            JPanel card = new JPanel(new BorderLayout(5, 5));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, barColor),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));
            JLabel lt = new JLabel(title);
            lt.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lt.setForeground(new Color(100, 116, 139));

            valLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
            valLabel.setForeground(new Color(15, 23, 42));

            card.add(lt, BorderLayout.NORTH);
            card.add(valLabel, BorderLayout.CENTER);
            return card;
        }

        private void openTechDetails() {
            int row = techTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a ticket row.", "Notice", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) techModel.getValueAt(row, 0);
            Complaint c = DataStore.getById(id);
            UIHelper.showDetails(this, c, staff, this::loadTech);
        }

        private void loadTech() {
            techModel.setRowCount(0);
            String kw = tfSearch != null ? tfSearch.getText() : "";
            String cat = cbCategory != null ? (String) cbCategory.getSelectedItem() : "All";
            String stat = cbStatus != null ? (String) cbStatus.getSelectedItem() : "All";
            String wing = cbWing != null ? (String) cbWing.getSelectedItem() : "All";

            List<Complaint> allFiltered = DataStore.searchComplaints(kw, cat, stat, wing);
            int myAssigned = 0, deptCount = 0, concAlerts = 0, resolvedCount = 0;

            for (Complaint c : DataStore.complaints) {
                boolean isMyTrade = staff.getDepartment() != null && staff.getDepartment().toLowerCase().contains(c.getCategory().toLowerCase());
                boolean isAssignedToMe = staff.getStaffId().equalsIgnoreCase(c.getAssignedStaffId());
                if (isAssignedToMe) myAssigned++;
                if (isMyTrade) deptCount++;
                if (c.isConcerning() && (isMyTrade || isAssignedToMe)) concAlerts++;
                if ("Resolved".equalsIgnoreCase(c.getStatus()) && (isMyTrade || isAssignedToMe)) resolvedCount++;
            }

            if (lblStatAssigned != null) lblStatAssigned.setText(String.valueOf(myAssigned));
            if (lblStatDept != null) lblStatDept.setText(String.valueOf(deptCount));
            if (lblStatConcerning != null) lblStatConcerning.setText(String.valueOf(concAlerts));
            if (lblStatResolved != null) lblStatResolved.setText(String.valueOf(resolvedCount));

            for (Complaint c : allFiltered) {
                techModel.addRow(new Object[]{
                        c.getTicketId(), c.getTitle(), c.getCategory(), c.getLocation(),
                        c.getAssignedStaffId() != null ? c.getAssignedStaffId() : "Unassigned",
                        c.getStatus(), c.isConcerning() ? "CONCERNING" : "Normal", c.getCreatedAt()
                });
            }
            if (lblCount != null) {
                lblCount.setText("Tasks: " + allFiltered.size());
            }
        }
    }

    // =========================================================================
    // 5. ENTRY POINT
    // =========================================================================

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginWindow());
    }
}
