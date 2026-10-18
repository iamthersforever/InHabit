package inhabit;

import java.sql.*;
import java.util.*;

// Data Access Object for COMPLAINT and VOTE tables
public class ComplaintDAO {
    // In-memory list for offline demo execution
    private static List<Complaint> mockComplaints = new ArrayList<>();
    private static Map<Integer, Set<String>> mockVotes = new HashMap<>();
    private static List<LostAndFoundItem> mockLostAndFound = new ArrayList<>();

    static {
        seedMockComplaints();
    }

    // Seed data directly from InHabitDB.sql for offline demonstration
    private static void seedMockComplaints() {
        mockComplaints.clear();
        mockVotes.clear();
        mockLostAndFound.clear();

        // 1. Complaint 1001 (Filed by Raghav Sarda - PRN 257122235)
        Complaint c1 = new Complaint(1001, "Fortinet captive portal timeout on 3rd floor Wi-Fi",
                "Cyberoam/Fortinet login gateway times out continuously on 3rd floor access points during peak hours. Internet access is inaccessible for study work.",
                "Internet", "Alpha-304", "In Progress", false, "257122235", "STF111", "2026-09-24 16:40:00");
        c1.setVoteCount(2);

        // 2. Complaint 1002 (Filed by Aniruddha Pratap Singh - PRN 257122247)
        Complaint c2 = new Complaint(1002, "Solar hot water pipeline cold in washroom",
                "Solar hot water line running cold in washrooms 201 to 210 for the past three consecutive mornings. Students are unable to get hot water during winter mornings.",
                "Plumbing", "Beta-201", "Under Review", false, "257122247", "STF108", "2026-09-25 07:15:00");
        c2.setVoteCount(2);

        // 3. Complaint 1003 (Mess Issue - Has 30 Genuine votes in InHabitDB.sql, concerning!)
        Complaint c3 = new Complaint(1003, "Mess dinner paneer gravy sour and undercooked rotis",
                "Wednesday evening dinner paneer tasted sour and rotis were undercooked; multiple students reported stomach issues and food hygiene concerns.",
                "Mess", "Central Mess", "Open", true, "257122001", "STF113", "2026-09-26 21:10:00");
        c3.setVoteCount(30);

        // 4. Complaint 1004
        Complaint c4 = new Complaint(1004, "Balcony sliding window latch broken",
                "Sliding window latch in room 108 is damaged; window does not lock securely against monsoon rain and wind.",
                "Cleanliness", "Delta-108", "Open", false, "257122006", null, "2026-09-27 11:30:00");
        c4.setVoteCount(0);

        // 5. Complaint 1005
        Complaint c5 = new Complaint(1005, "Lift 1 erratic jerking between 2nd and 3rd floor",
                "Passenger elevator shakes abruptly between 2nd and 3rd floors before leveling at the landing. Needs urgent inspection by lift maintenance agency.",
                "Electrical", "Alpha-Ground", "In Progress", false, "257122013", "STF106", "2026-09-27 18:25:00");
        c5.setVoteCount(2);

        // 6. Complaint 1006
        Complaint c6 = new Complaint(1006, "Charlie-Study AC excessive rattling noise",
                "Split air conditioner in study room emits loud mechanical vibrating noise when compressor turns on.",
                "Electrical", "Charlie-Study", "Resolved", false, "257126003", "STF107", "2026-09-28 14:10:00");
        c6.setVoteCount(1);

        // 7. Complaint 1007
        Complaint c7 = new Complaint(1007, "Beta-406 flush valve leakage",
                "Dual flush valve continuously trickles water into toilet bowl, causing wastage and damp flooring.",
                "Plumbing", "Beta-406", "In Progress", false, "257126004", "STF108", "2026-09-28 16:30:00");
        c7.setVoteCount(0);

        // 8. Complaint 1008
        Complaint c8 = new Complaint(1008, "Delta-401 water cooler filter smell",
                "Drinking water dispenser on 4th floor corridor has unpleasant odor and stale taste; filter replacement needed urgently.",
                "Cleanliness", "Delta-401", "Open", false, "257126008", null, "2026-09-29 08:45:00");
        c8.setVoteCount(0);

        // 9. Complaint 1009
        Complaint c9 = new Complaint(1009, "Alpha-409 blinking tube light fixture",
                "Fluorescent tube light in 4th floor corridor constantly flickers and starter makes buzzing noise.",
                "Electrical", "Alpha-409", "Resolved", false, "257126010", "STF107", "2026-09-29 19:20:00");
        c9.setVoteCount(1);

        // 10. Complaint 1010
        Complaint c10 = new Complaint(1010, "Gate 1 biometric scanner rebooting",
                "Main entrance biometric attendance unit repeatedly restarts on user thumbprint verification during evening rush.",
                "Security", "Main Gate 1", "In Progress", false, "257122002", "STF117", "2026-09-30 20:15:00");
        c10.setVoteCount(1);

        // 11. Complaint 1011
        Complaint c11 = new Complaint(1011, "Charlie wing 2nd floor LAN port disconnected",
                "Wall Ethernet jack RJ45 connector damaged and does not establish connection with hostel subnet.",
                "Internet", "Charlie-205", "In Progress", false, "257122019", "STF112", "2026-10-01 09:00:00");
        c11.setVoteCount(0);

        // 12. Complaint 1012
        Complaint c12 = new Complaint(1012, "Central mess breakfast milk dispenser clogged",
                "Stainless steel hot milk urn tap is clogged with milk scum and drips slowly during breakfast service.",
                "Mess", "Central Mess", "Resolved", false, "257122025", "STF113", "2026-10-01 08:15:00");
        c12.setVoteCount(0);

        // 13. Complaint 1013
        Complaint c13 = new Complaint(1013, "Beta wing washbasin tap leaking heavily",
                "Pillar tap in 3rd floor washroom cannot be shut tightly and leaks a steady stream into the sink.",
                "Plumbing", "Beta-312", "Open", false, "257122030", null, "2026-10-01 11:20:00");
        c13.setVoteCount(0);

        // 14. Complaint 1014
        Complaint c14 = new Complaint(1014, "Delta corridor stairwell emergency light offline",
                "Battery backup emergency LED panel on west stairwell fails to illuminate during scheduled power cut.",
                "Electrical", "Delta-204", "Open", false, "257122045", null, "2026-10-01 14:50:00");
        c14.setVoteCount(0);

        // 15. Complaint 1015
        Complaint c15 = new Complaint(1015, "Charlie wing ground floor exit door magnetic latch loose",
                "Emergency fire exit door magnetic release does not latch properly when closed, staying slightly ajar.",
                "Security", "Charlie-101", "Resolved", false, "257122055", "STF120", "2026-10-01 22:30:00");
        c15.setVoteCount(0);

        mockComplaints.add(c1);
        mockComplaints.add(c2);
        mockComplaints.add(c3);
        mockComplaints.add(c4);
        mockComplaints.add(c5);
        mockComplaints.add(c6);
        mockComplaints.add(c7);
        mockComplaints.add(c8);
        mockComplaints.add(c9);
        mockComplaints.add(c10);
        mockComplaints.add(c11);
        mockComplaints.add(c12);
        mockComplaints.add(c13);
        mockComplaints.add(c14);
        mockComplaints.add(c15);

        // Seed votes for mock
        Set<String> v1 = new HashSet<>();
        v1.add("257126003"); v1.add("257126004");
        mockVotes.put(1001, v1);

        Set<String> v2 = new HashSet<>();
        v2.add("257126005"); v2.add("257126006");
        mockVotes.put(1002, v2);

        Set<String> v3 = new HashSet<>();
        for (int i = 1; i <= 30; i++) {
            v3.add("2571260" + (i < 10 ? "0" + i : i));
        }
        mockVotes.put(1003, v3);

        // Seed Lost & Found Notices from InHabitDB.sql
        mockLostAndFound.add(new LostAndFoundItem("LF101", "Casio fx-991CW ClassWiz Calculator", "Found on Row 3 desk in Programming Lab 2 with initials RS scratched on back cover.", "Lab 2 (Programming Lab)", "Found", "Claimed", "257122235", "2026-09-22 17:15:00"));
        mockLostAndFound.add(new LostAndFoundItem("LF102", "SIT Student ID Card & Lanyard", "Navy blue college lanyard with student ID card of PRN 257122014.", "Central Canteen counter", "Found", "Claimed", "257122247", "2026-09-23 13:30:00"));
        mockLostAndFound.add(new LostAndFoundItem("LF103", "boAt Airdopes 141 ANC Charging Case", "Black matte earbud case (without buds) left on table 4 near entrance.", "Hostel Mess Ground Floor", "Found", "Open", "257122001", "2026-09-25 09:05:00"));
        mockLostAndFound.add(new LostAndFoundItem("LF104", "Set of 3 Godrej Keys on Pulsar Keychain", "Bundle of 3 room keys lost somewhere between basketball court and Block B.", "Sports Complex / Court", "Lost", "Open", "257122005", "2026-09-26 19:40:00"));
        mockLostAndFound.add(new LostAndFoundItem("LF105", "Milton Thermosteel 1000ml Bottle (Grey)", "Left on corner reading desk behind Reference Section.", "Central Library 2nd Floor", "Found", "Open", "257126003", "2026-09-27 16:20:00"));
        mockLostAndFound.add(new LostAndFoundItem("LF106", "Apple 20W USB-C Adapter + Braided Cable", "Left plugged into wall switchboard near whiteboard in Study Room B.", "Hostel Study Room B", "Found", "Claimed", "257126005", "2026-09-27 23:10:00"));
        mockLostAndFound.add(new LostAndFoundItem("LF107", "Logitech M221 Silent Wireless Mouse", "Charcoal grey wireless mouse left beside PC terminal 18.", "CAD / Graphics Lab", "Found", "Open", "257122009", "2026-09-28 12:45:00"));
        mockLostAndFound.add(new LostAndFoundItem("LF108", "Titan Rectangle Blue Eyeglasses in Case", "Prescription specs in black Titan hard case lost near amphitheater steps.", "College Amphitheater", "Lost", "Open", "257126007", "2026-09-29 18:00:00"));
        mockLostAndFound.add(new LostAndFoundItem("LF109", "Classmate Pulse Spiral Notebook (DBMS)", "Single line spiral register containing ER diagram lecture notes left on bench.", "Academic Block Corridor", "Found", "Open", "257122012", "2026-09-30 11:20:00"));
        mockLostAndFound.add(new LostAndFoundItem("LF110", "Hero Cycle 4-digit Number Combination Lock", "Black steel cable lock with silver dial found hanging on rack.", "Hostel Bicycle Parking", "Found", "Open", "257126011", "2026-09-30 17:35:00"));
    }

    // Step 1: Fetch all complaints for community feed / admin view
    public List<Complaint> getAllComplaints() {
        Connection con = DBConnection.getConnection();
        if (con != null) {
            List<Complaint> list = new ArrayList<>();
            try {
                String q = "SELECT c.TicketID, c.Title, c.Description, c.Category, c.Status, c.CreatedAt, " +
                           "c.WardenConfirmed, c.StudentConfirmed, c.FiledBy, " +
                           "s.PRN AS StudentPRN, CONCAT(s.BlockName, '-', s.Wing) AS RoomLocation, " +
                           "ta.TechnicianID, ta.WardenID, " +
                           "ec.TicketID AS EscalatedTicketID, " +
                           "COUNT(v.VoteID) AS VoteCount " +
                           "FROM COMPLAINT c " +
                           "LEFT JOIN STUDENT s ON c.FiledBy = s.UserID " +
                           "LEFT JOIN TICKET_ASSIGNMENT ta ON c.TicketID = ta.TicketID " +
                           "LEFT JOIN ESCALATED_COMPLAINT ec ON c.TicketID = ec.TicketID " +
                           "LEFT JOIN VOTE v ON c.TicketID = v.TicketID " +
                           "GROUP BY c.TicketID, c.Title, c.Description, c.Category, c.Status, c.CreatedAt, " +
                           "c.WardenConfirmed, c.StudentConfirmed, c.FiledBy, " +
                           "s.PRN, s.BlockName, s.Wing, ta.TechnicianID, ta.WardenID, ec.TicketID " +
                           "ORDER BY (ec.TicketID IS NOT NULL) DESC, VoteCount DESC, c.CreatedAt DESC";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(q);
                while (rs.next()) {
                    boolean isConc = (rs.getObject("EscalatedTicketID") != null) || (rs.getInt("VoteCount") >= 30);
                    String staffId = rs.getString("TechnicianID");
                    if (staffId == null) staffId = rs.getString("WardenID");
                    Complaint c = new Complaint(
                            rs.getInt("TicketID"),
                            rs.getString("Title"),
                            rs.getString("Description"),
                            rs.getString("Category"),
                            rs.getString("RoomLocation") != null ? rs.getString("RoomLocation") : "Hostel",
                            rs.getString("Status"),
                            isConc,
                            rs.getString("StudentPRN"),
                            staffId,
                            rs.getString("CreatedAt")
                    );
                    c.setVoteCount(rs.getInt("VoteCount"));
                    c.setFiledBy(rs.getString("FiledBy"));
                    c.setWardenConfirmed(rs.getBoolean("WardenConfirmed"));
                    c.setStudentConfirmed(rs.getBoolean("StudentConfirmed"));
                    list.add(c);
                }
                rs.close();
                stmt.close();
                con.close();
                if (!list.isEmpty()) return list;
            } catch (SQLException e) {
                System.out.println("SQL error fetching complaints: " + e.getMessage());
            }
        }
        return new ArrayList<>(mockComplaints);
    }

    // Step 2: Fetch complaints filed by a specific student
    public List<Complaint> getComplaintsByStudent(String prn) {
        List<Complaint> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con != null) {
            try {
                String q = "SELECT c.TicketID, c.Title, c.Description, c.Category, c.Status, c.CreatedAt, " +
                           "c.WardenConfirmed, c.StudentConfirmed, c.FiledBy, " +
                           "s.PRN AS StudentPRN, CONCAT(s.BlockName, '-', s.Wing) AS RoomLocation, " +
                           "ta.TechnicianID, ta.WardenID, " +
                           "ec.TicketID AS EscalatedTicketID, " +
                           "COUNT(v.VoteID) AS VoteCount " +
                           "FROM COMPLAINT c " +
                           "INNER JOIN STUDENT s ON c.FiledBy = s.UserID " +
                           "LEFT JOIN TICKET_ASSIGNMENT ta ON c.TicketID = ta.TicketID " +
                           "LEFT JOIN ESCALATED_COMPLAINT ec ON c.TicketID = ec.TicketID " +
                           "LEFT JOIN VOTE v ON c.TicketID = v.TicketID " +
                           "WHERE s.PRN = ? OR c.FiledBy = ? " +
                           "GROUP BY c.TicketID, c.Title, c.Description, c.Category, c.Status, c.CreatedAt, " +
                           "c.WardenConfirmed, c.StudentConfirmed, c.FiledBy, " +
                           "s.PRN, s.BlockName, s.Wing, ta.TechnicianID, ta.WardenID, ec.TicketID " +
                           "ORDER BY c.CreatedAt DESC";
                PreparedStatement ps = con.prepareStatement(q);
                ps.setString(1, prn);
                ps.setString(2, prn.startsWith("USR_") ? prn : "USR_" + prn);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    boolean isConc = (rs.getObject("EscalatedTicketID") != null) || (rs.getInt("VoteCount") >= 30);
                    String staffId = rs.getString("TechnicianID");
                    if (staffId == null) staffId = rs.getString("WardenID");
                    Complaint c = new Complaint(
                            rs.getInt("TicketID"),
                            rs.getString("Title"),
                            rs.getString("Description"),
                            rs.getString("Category"),
                            rs.getString("RoomLocation") != null ? rs.getString("RoomLocation") : "Hostel",
                            rs.getString("Status"),
                            isConc,
                            rs.getString("StudentPRN"),
                            staffId,
                            rs.getString("CreatedAt")
                    );
                    c.setVoteCount(rs.getInt("VoteCount"));
                    c.setFiledBy(rs.getString("FiledBy"));
                    c.setWardenConfirmed(rs.getBoolean("WardenConfirmed"));
                    c.setStudentConfirmed(rs.getBoolean("StudentConfirmed"));
                    list.add(c);
                }
                rs.close();
                ps.close();
                con.close();
                if (!list.isEmpty()) return list;
            } catch (SQLException e) {
                System.out.println("SQL error fetching student complaints: " + e.getMessage());
            }
        }

        // Mock fallback
        for (Complaint c : mockComplaints) {
            if (prn != null && prn.equals(c.getCreatedByPrn())) {
                list.add(c);
            }
        }
        return list;
    }

    // Step 3: Get single complaint details by Ticket ID
    public Complaint getComplaintById(int ticketId) {
        Connection con = DBConnection.getConnection();
        if (con != null) {
            try {
                String q = "SELECT c.TicketID, c.Title, c.Description, c.Category, c.Status, c.CreatedAt, " +
                           "c.WardenConfirmed, c.StudentConfirmed, c.FiledBy, " +
                           "s.PRN AS StudentPRN, CONCAT(s.BlockName, '-', s.Wing) AS RoomLocation, " +
                           "ta.TechnicianID, ta.WardenID, " +
                           "ec.TicketID AS EscalatedTicketID, " +
                           "COUNT(v.VoteID) AS VoteCount " +
                           "FROM COMPLAINT c " +
                           "LEFT JOIN STUDENT s ON c.FiledBy = s.UserID " +
                           "LEFT JOIN TICKET_ASSIGNMENT ta ON c.TicketID = ta.TicketID " +
                           "LEFT JOIN ESCALATED_COMPLAINT ec ON c.TicketID = ec.TicketID " +
                           "LEFT JOIN VOTE v ON c.TicketID = v.TicketID " +
                           "WHERE c.TicketID = ? " +
                           "GROUP BY c.TicketID, c.Title, c.Description, c.Category, c.Status, c.CreatedAt, " +
                           "c.WardenConfirmed, c.StudentConfirmed, c.FiledBy, " +
                           "s.PRN, s.BlockName, s.Wing, ta.TechnicianID, ta.WardenID, ec.TicketID";
                PreparedStatement ps = con.prepareStatement(q);
                ps.setInt(1, ticketId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    boolean isConc = (rs.getObject("EscalatedTicketID") != null) || (rs.getInt("VoteCount") >= 30);
                    String staffId = rs.getString("TechnicianID");
                    if (staffId == null) staffId = rs.getString("WardenID");
                    Complaint c = new Complaint(
                            rs.getInt("TicketID"),
                            rs.getString("Title"),
                            rs.getString("Description"),
                            rs.getString("Category"),
                            rs.getString("RoomLocation") != null ? rs.getString("RoomLocation") : "Hostel",
                            rs.getString("Status"),
                            isConc,
                            rs.getString("StudentPRN"),
                            staffId,
                            rs.getString("CreatedAt")
                    );
                    c.setVoteCount(rs.getInt("VoteCount"));
                    c.setFiledBy(rs.getString("FiledBy"));
                    c.setWardenConfirmed(rs.getBoolean("WardenConfirmed"));
                    c.setStudentConfirmed(rs.getBoolean("StudentConfirmed"));
                    rs.close();
                    ps.close();
                    con.close();
                    return c;
                }
                rs.close();
                ps.close();
                con.close();
            } catch (SQLException e) {
                System.out.println("SQL error fetching complaint by id: " + e.getMessage());
            }
        }

        for (Complaint c : mockComplaints) {
            if (c.getTicketId() == ticketId) return c;
        }
        return null;
    }

    // Step 4: File a new complaint
    public boolean fileComplaint(Complaint c) throws InvalidComplaintException {
        if (c.getTitle() == null || c.getTitle().trim().isEmpty()) {
            throw new InvalidComplaintException("Grievance title cannot be empty.");
        }
        if (c.getDescription() == null || c.getDescription().trim().isEmpty()) {
            throw new InvalidComplaintException("Grievance description cannot be empty.");
        }
        if (c.getLocation() == null || c.getLocation().trim().isEmpty()) {
            throw new InvalidComplaintException("Location is required.");
        }

        Connection con = DBConnection.getConnection();
        if (con != null) {
            try {
                String maxQ = "SELECT IFNULL(MAX(TicketID), 1000) + 1 AS NEXT_ID FROM COMPLAINT";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(maxQ);
                int nextId = 1016;
                if (rs.next()) {
                    nextId = rs.getInt("NEXT_ID");
                }
                rs.close();
                st.close();

                c.setTicketId(nextId);
                String filedByUser = c.getFiledBy();
                if (filedByUser == null && c.getCreatedByPrn() != null) {
                    filedByUser = "USR_" + c.getCreatedByPrn();
                }

                String q = "INSERT INTO COMPLAINT (TicketID, Title, Description, Category, Status, CreatedAt, WardenConfirmed, StudentConfirmed, FiledBy) " +
                           "VALUES (?, ?, ?, ?, 'Open', NOW(), FALSE, FALSE, ?)";
                PreparedStatement ps = con.prepareStatement(q);
                ps.setInt(1, c.getTicketId());
                ps.setString(2, c.getTitle());
                ps.setString(3, c.getDescription());
                ps.setString(4, c.getCategory());
                ps.setString(5, filedByUser);
                int rows = ps.executeUpdate();
                ps.close();
                con.close();
                return rows > 0;
            } catch (SQLException e) {
                System.out.println("SQL error inserting complaint: " + e.getMessage());
            }
        }

        // Mock fallback
        int maxId = 1000;
        for (Complaint ex : mockComplaints) {
            if (ex.getTicketId() > maxId) maxId = ex.getTicketId();
        }
        c.setTicketId(maxId + 1);
        c.setStatus("Open");
        c.setConcerning(false);
        c.setCreatedAt("Just now");
        mockComplaints.add(0, c);
        return true;
    }

    // Step 5: Cast community vote on a grievance ticket
    public boolean voteComplaint(int ticketId, String prn, String voteType) throws InvalidVoteException {
        // Prevent voting on own complaint
        for (Complaint c : mockComplaints) {
            if (c.getTicketId() == ticketId && prn != null && (prn.equals(c.getCreatedByPrn()) || (c.getFiledBy() != null && c.getFiledBy().endsWith(prn)))) {
                throw new InvalidVoteException("You cannot vote on your own grievance ticket.");
            }
        }

        Connection con = DBConnection.getConnection();
        if (con != null) {
            try {
                String userId = prn.startsWith("USR_") ? prn : "USR_" + prn;
                String checkQ = "SELECT VoteID FROM VOTE WHERE TicketID = ? AND UserID = ?";
                PreparedStatement psCheck = con.prepareStatement(checkQ);
                psCheck.setInt(1, ticketId);
                psCheck.setString(2, userId);
                ResultSet rsCheck = psCheck.executeQuery();
                if (rsCheck.next()) {
                    rsCheck.close();
                    psCheck.close();
                    con.close();
                    throw new InvalidVoteException("You have already voted on Ticket #" + ticketId + ".");
                }
                rsCheck.close();
                psCheck.close();

                Statement st = con.createStatement();
                ResultSet rsId = st.executeQuery("SELECT IFNULL(MAX(CAST(SUBSTRING(VoteID, 2) AS UNSIGNED)), 0) + 1 AS NEXT_VID FROM VOTE");
                int nextVidNum = 1;
                if (rsId.next()) nextVidNum = rsId.getInt("NEXT_VID");
                rsId.close();
                st.close();

                String insertQ = "INSERT INTO VOTE (VoteID, TicketID, UserID, VoteType, VotedAt) VALUES (?, ?, ?, ?, NOW())";
                PreparedStatement ps = con.prepareStatement(insertQ);
                ps.setString(1, "V" + nextVidNum);
                ps.setInt(2, ticketId);
                ps.setString(3, userId);
                ps.setString(4, voteType != null ? voteType : "Upvote");
                int rows = ps.executeUpdate();
                ps.close();

                String countQ = "SELECT COUNT(*) AS CNT FROM VOTE WHERE TicketID = ?";
                PreparedStatement psCount = con.prepareStatement(countQ);
                psCount.setInt(1, ticketId);
                ResultSet rsCount = psCount.executeQuery();
                if (rsCount.next() && rsCount.getInt("CNT") >= 30) {
                    PreparedStatement psEsc = con.prepareStatement("INSERT IGNORE INTO ESCALATED_COMPLAINT (TicketID, EscalatedAt) VALUES (?, NOW())");
                    psEsc.setInt(1, ticketId);
                    psEsc.executeUpdate();
                    psEsc.close();
                }
                rsCount.close();
                psCount.close();
                con.close();
                return rows > 0;
            } catch (SQLException e) {
                System.out.println("SQL error casting vote: " + e.getMessage());
            }
        }

        // Mock fallback
        Set<String> voters = mockVotes.computeIfAbsent(ticketId, k -> new HashSet<>());
        if (voters.contains(prn)) {
            throw new InvalidVoteException("You have already voted on Ticket #" + ticketId + ".");
        }
        voters.add(prn);
        for (Complaint c : mockComplaints) {
            if (c.getTicketId() == ticketId) {
                c.setVoteCount(c.getVoteCount() + 1);
                if (c.getVoteCount() >= 30) {
                    c.setConcerning(true);
                }
                break;
            }
        }
        return true;
    }

    // Step 6: Update complaint status (used by Admin Dashboard)
    public boolean updateStatus(int ticketId, String newStatus) {
        Connection con = DBConnection.getConnection();
        if (con != null) {
            try {
                String q = "UPDATE COMPLAINT SET Status = ? WHERE TicketID = ?";
                PreparedStatement ps = con.prepareStatement(q);
                ps.setString(1, newStatus);
                ps.setInt(2, ticketId);
                int rows = ps.executeUpdate();
                ps.close();
                con.close();
                return rows > 0;
            } catch (SQLException e) {
                System.out.println("SQL error updating status: " + e.getMessage());
            }
        }

        for (Complaint c : mockComplaints) {
            if (c.getTicketId() == ticketId) {
                c.setStatus(newStatus);
                return true;
            }
        }
        return false;
    }

    // Step 7: Assign staff to ticket (used by Admin Dashboard)
    public boolean assignStaff(int ticketId, String staffId) {
        Connection con = DBConnection.getConnection();
        if (con != null) {
            try {
                String techUser = staffId.startsWith("USR_") ? staffId : "USR_" + staffId;
                String q = "INSERT INTO TICKET_ASSIGNMENT (AssignmentID, TicketID, WardenID, TechnicianID, AssignedAt, ResolutionNotes) " +
                           "VALUES (CONCAT('ASG_', ?), ?, 'USR_STF101', ?, NOW(), 'Assigned via Admin Portal') " +
                           "ON DUPLICATE KEY UPDATE TechnicianID = VALUES(TechnicianID), AssignedAt = NOW()";
                PreparedStatement ps = con.prepareStatement(q);
                ps.setInt(1, ticketId);
                ps.setInt(2, ticketId);
                ps.setString(3, techUser);
                int rows = ps.executeUpdate();
                ps.close();

                PreparedStatement psUp = con.prepareStatement("UPDATE COMPLAINT SET Status = 'In Progress' WHERE TicketID = ?");
                psUp.setInt(1, ticketId);
                psUp.executeUpdate();
                psUp.close();

                con.close();
                return rows > 0;
            } catch (SQLException e) {
                System.out.println("SQL error assigning staff: " + e.getMessage());
            }
        }

        for (Complaint c : mockComplaints) {
            if (c.getTicketId() == ticketId) {
                c.setAssignedStaffId(staffId);
                c.setStatus("In Progress");
                return true;
            }
        }
        return false;
    }

    // Step 8: Auto-escalate complaints exceeding 30 votes
    public int checkAndEscalateConcerning() {
        int escalated = 0;
        Connection con = DBConnection.getConnection();
        if (con != null) {
            try {
                String q = "INSERT IGNORE INTO ESCALATED_COMPLAINT (TicketID, EscalatedAt) " +
                           "SELECT TicketID, NOW() FROM VOTE GROUP BY TicketID HAVING COUNT(VoteID) >= 30";
                Statement st = con.createStatement();
                escalated = st.executeUpdate(q);
                st.close();
                con.close();
                return escalated;
            } catch (SQLException e) {
                System.out.println("SQL error in auto-escalation: " + e.getMessage());
            }
        }

        // Mock fallback
        for (Complaint c : mockComplaints) {
            if (c.getVoteCount() >= 30 && !c.isConcerning()) {
                c.setConcerning(true);
                escalated++;
            }
        }
        return escalated;
    }

    // Step 9: Search complaints with keyword, category separator, status separator, and wing separator
    public List<Complaint> searchComplaints(String keyword, String category, String status, String wing) {
        List<Complaint> all = getAllComplaints();
        List<Complaint> result = new ArrayList<>();

        String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";
        boolean filterCategory = (category != null && !category.equalsIgnoreCase("All") && !category.equalsIgnoreCase("All Categories"));
        boolean filterStatus = (status != null && !status.equalsIgnoreCase("All") && !status.equalsIgnoreCase("All Statuses"));
        boolean filterWing = (wing != null && !wing.equalsIgnoreCase("All") && !wing.equalsIgnoreCase("All Wings"));

        for (Complaint c : all) {
            // Category filter
            if (filterCategory && !category.equalsIgnoreCase(c.getCategory())) {
                continue;
            }

            // Status filter (special check for Concerning Only)
            if (filterStatus) {
                if (status.equalsIgnoreCase("Concerning Only") || status.equalsIgnoreCase("Concerning")) {
                    if (!c.isConcerning()) continue;
                } else if (!status.equalsIgnoreCase(c.getStatus())) {
                    continue;
                }
            }

            // Wing filter (e.g., "Alpha", "Beta", "Charlie", "Delta")
            if (filterWing) {
                String loc = c.getLocation() != null ? c.getLocation().toLowerCase() : "";
                String targetWing = wing.toLowerCase().replace("wing", "").trim();
                if (!loc.startsWith(targetWing)) {
                    continue;
                }
            }

            // Keyword match (matches title, description, location, category, ticketId, createdByPrn)
            if (!kw.isEmpty()) {
                boolean matchTitle = c.getTitle() != null && c.getTitle().toLowerCase().contains(kw);
                boolean matchDesc = c.getDescription() != null && c.getDescription().toLowerCase().contains(kw);
                boolean matchLoc = c.getLocation() != null && c.getLocation().toLowerCase().contains(kw);
                boolean matchCat = c.getCategory() != null && c.getCategory().toLowerCase().contains(kw);
                boolean matchId = String.valueOf(c.getTicketId()).contains(kw);
                boolean matchPrn = c.getCreatedByPrn() != null && c.getCreatedByPrn().toLowerCase().contains(kw);
                if (!matchTitle && !matchDesc && !matchLoc && !matchCat && !matchId && !matchPrn) {
                    continue;
                }
            }

            result.add(c);
        }
        return result;
    }

    // Step 10: Search student's own complaints
    public List<Complaint> searchStudentComplaints(String prn, String keyword, String category, String status) {
        List<Complaint> myComplaints = getComplaintsByStudent(prn);
        List<Complaint> result = new ArrayList<>();

        String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";
        boolean filterCategory = (category != null && !category.equalsIgnoreCase("All") && !category.equalsIgnoreCase("All Categories"));
        boolean filterStatus = (status != null && !status.equalsIgnoreCase("All") && !status.equalsIgnoreCase("All Statuses"));

        for (Complaint c : myComplaints) {
            if (filterCategory && !category.equalsIgnoreCase(c.getCategory())) {
                continue;
            }
            if (filterStatus) {
                if (status.equalsIgnoreCase("Concerning Only") || status.equalsIgnoreCase("Concerning")) {
                    if (!c.isConcerning()) continue;
                } else if (!status.equalsIgnoreCase(c.getStatus())) {
                    continue;
                }
            }
            if (!kw.isEmpty()) {
                boolean matchTitle = c.getTitle() != null && c.getTitle().toLowerCase().contains(kw);
                boolean matchDesc = c.getDescription() != null && c.getDescription().toLowerCase().contains(kw);
                boolean matchLoc = c.getLocation() != null && c.getLocation().toLowerCase().contains(kw);
                boolean matchId = String.valueOf(c.getTicketId()).contains(kw);
                if (!matchTitle && !matchDesc && !matchLoc && !matchId) {
                    continue;
                }
            }
            result.add(c);
        }
        return result;
    }

    // Step 11: Get complaints assigned to a staff member or matching their trade
    public List<Complaint> getComplaintsByAssignedStaff(String staffId) {
        List<Complaint> all = getAllComplaints();
        List<Complaint> assigned = new ArrayList<>();
        for (Complaint c : all) {
            if (staffId != null && staffId.equalsIgnoreCase(c.getAssignedStaffId())) {
                assigned.add(c);
            }
        }
        return assigned;
    }

    // Step 12: Lost and Found Notices
    public List<LostAndFoundItem> getAllLostAndFound() {
        Connection con = DBConnection.getConnection();
        if (con != null) {
            List<LostAndFoundItem> list = new ArrayList<>();
            try {
                String q = "SELECT ItemID, ItemName, Description, LocationFound, ItemType, Status, ReportedAt, ReportedBy FROM LOST_AND_FOUND ORDER BY ReportedAt DESC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(q);
                while (rs.next()) {
                    list.add(new LostAndFoundItem(
                            rs.getString("ItemID"),
                            rs.getString("ItemName"),
                            rs.getString("Description"),
                            rs.getString("LocationFound"),
                            rs.getString("ItemType"),
                            rs.getString("Status"),
                            rs.getString("ReportedBy"),
                            rs.getString("ReportedAt")
                    ));
                }
                rs.close();
                st.close();
                con.close();
                if (!list.isEmpty()) return list;
            } catch (SQLException e) {
                System.out.println("SQL error fetching lost and found: " + e.getMessage());
            }
        }
        return new ArrayList<>(mockLostAndFound);
    }

    // Step 13: Search Lost and Found notices
    public List<LostAndFoundItem> searchLostAndFound(String keyword, String itemType, String status) {
        List<LostAndFoundItem> all = getAllLostAndFound();
        List<LostAndFoundItem> result = new ArrayList<>();

        String kw = (keyword != null) ? keyword.trim().toLowerCase() : "";
        boolean filterType = (itemType != null && !itemType.equalsIgnoreCase("All") && !itemType.equalsIgnoreCase("All Types"));
        boolean filterStatus = (status != null && !status.equalsIgnoreCase("All") && !status.equalsIgnoreCase("All Statuses"));

        for (LostAndFoundItem item : all) {
            if (filterType && !itemType.equalsIgnoreCase(item.getItemType())) {
                continue;
            }
            if (filterStatus && !status.equalsIgnoreCase(item.getStatus())) {
                continue;
            }
            if (!kw.isEmpty()) {
                boolean matchName = item.getItemName() != null && item.getItemName().toLowerCase().contains(kw);
                boolean matchDesc = item.getDescription() != null && item.getDescription().toLowerCase().contains(kw);
                boolean matchLoc = item.getLocationFound() != null && item.getLocationFound().toLowerCase().contains(kw);
                boolean matchId = item.getItemId() != null && item.getItemId().toLowerCase().contains(kw);
                boolean matchPrn = item.getReportedByPrn() != null && item.getReportedByPrn().toLowerCase().contains(kw);
                if (!matchName && !matchDesc && !matchLoc && !matchId && !matchPrn) {
                    continue;
                }
            }
            result.add(item);
        }
        return result;
    }

    // Step 14: Report new Lost and Found item
    public boolean reportLostAndFound(LostAndFoundItem item) {
        if (item.getItemName() == null || item.getItemName().trim().isEmpty()) {
            return false;
        }

        Connection con = DBConnection.getConnection();
        if (con != null) {
            try {
                String maxQ = "SELECT IFNULL(MAX(CAST(SUBSTRING(ItemID, 3) AS UNSIGNED)), 110) + 1 AS NEXT_LF FROM LOST_AND_FOUND";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(maxQ);
                String nextId = "LF111";
                if (rs.next()) {
                    nextId = "LF" + rs.getInt("NEXT_LF");
                }
                rs.close();
                st.close();

                item.setItemId(nextId);
                String repUser = item.getReportedByPrn();
                if (repUser != null && !repUser.startsWith("USR_")) {
                    repUser = "USR_" + repUser;
                }
                String ins = "INSERT INTO LOST_AND_FOUND (ItemID, ItemName, Description, LocationFound, ItemType, Status, ReportedAt, ReportedBy) " +
                             "VALUES (?, ?, ?, ?, ?, 'Open', NOW(), ?)";
                PreparedStatement ps = con.prepareStatement(ins);
                ps.setString(1, item.getItemId());
                ps.setString(2, item.getItemName());
                ps.setString(3, item.getDescription());
                ps.setString(4, item.getLocationFound());
                ps.setString(5, item.getItemType());
                ps.setString(6, repUser);
                int rows = ps.executeUpdate();
                ps.close();
                con.close();
                return rows > 0;
            } catch (SQLException e) {
                System.out.println("SQL error reporting lost and found: " + e.getMessage());
            }
        }

        // Mock fallback
        int maxLF = 110;
        for (LostAndFoundItem it : mockLostAndFound) {
            try {
                int num = Integer.parseInt(it.getItemId().replace("LF", ""));
                if (num > maxLF) maxLF = num;
            } catch (Exception ignored) {}
        }
        item.setItemId("LF" + (maxLF + 1));
        item.setStatus("Open");
        item.setReportedAt("Just now");
        mockLostAndFound.add(0, item);
        return true;
    }

    // Step 15: Claim item
    public boolean claimLostAndFound(String itemId) {
        Connection con = DBConnection.getConnection();
        if (con != null) {
            try {
                String q = "UPDATE LOST_AND_FOUND SET Status = 'Claimed' WHERE ItemID = ?";
                PreparedStatement ps = con.prepareStatement(q);
                ps.setString(1, itemId);
                int rows = ps.executeUpdate();
                ps.close();
                con.close();
                return rows > 0;
            } catch (SQLException e) {
                System.out.println("SQL error claiming lost and found: " + e.getMessage());
            }
        }

        for (LostAndFoundItem it : mockLostAndFound) {
            if (itemId != null && itemId.equalsIgnoreCase(it.getItemId())) {
                it.setStatus("Claimed");
                return true;
            }
        }
        return false;
    }
}
