package inhabit;

import java.util.*;

// Headless Automated Verification Test Suite
public class SmokeTest {
    private static int passed = 0;
    private static int total = 0;

    private static void check(String desc, boolean condition) {
        total++;
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + desc);
        } else {
            System.out.println("  [FAIL] " + desc);
        }
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   InHabit Project Automated Verification Suite");
        System.out.println("   Database: InHabitDB (Raghav, Aniruddha, Admin)");
        System.out.println("=================================================");

        UserDAO userDAO = new UserDAO();
        ComplaintDAO complaintDAO = new ComplaintDAO();

        // Test 1: Authenticate Raghav Sarda with his own credentials
        try {
            User uRaghav = userDAO.authenticate("raghav.sarda.btech25@college.edu.in", "raghav_sarda@235");
            check("Authentication Raghav via official email & password", uRaghav != null);
            check("Raghav is Student instance (Inheritance)", uRaghav instanceof Student);
            check("Raghav PRN is 257122235", "257122235".equals(uRaghav.getPrn()));
            check("Raghav Room is Alpha-101", "Alpha-101".equals(uRaghav.getLocation()));
        } catch (Exception e) {
            check("Authentication Raghav via official email", false);
        }

        // Test 2: Authenticate Aniruddha Singh with his own credentials
        try {
            User uAniruddha = userDAO.authenticate("aniruddha.singh.btech25@college.edu.in", "aniruddha_singh@247");
            check("Authentication Aniruddha via official email & password", uAniruddha != null);
            check("Aniruddha is Student instance", uAniruddha instanceof Student);
            check("Aniruddha PRN is 257122247", "257122247".equals(uAniruddha.getPrn()));
            check("Aniruddha Room is Alpha-102", "Alpha-102".equals(uAniruddha.getLocation()));
            check("Aniruddha Name is Aniruddha Pratap Singh", "Aniruddha Pratap Singh".equals(uAniruddha.getFullName()));
        } catch (Exception e) {
            check("Authentication Aniruddha via official email", false);
        }

        // Test 3: Authenticate Administrator
        try {
            User uAdmin = userDAO.authenticate("admin@college.edu.in", "admin123");
            check("Authentication Admin via admin@college.edu.in", uAdmin != null);
            check("Admin is Staff instance", uAdmin instanceof Staff);
        } catch (Exception e) {
            check("Authentication Admin", false);
        }

        // Test 4: Roommates in Alpha-101 (Raghav's room)
        List<Student> rm101 = userDAO.getRoommates("Alpha-101", "257122235");
        check("Roommates query Alpha-101 returns occupants", !rm101.isEmpty());

        // Test 5: Roommates in Alpha-102 (Aniruddha's room)
        List<Student> rm102 = userDAO.getRoommates("Alpha-102", "257122247");
        check("Roommates query Alpha-102 returns occupants", !rm102.isEmpty());

        // Test 6: Complaint retrieval
        List<Complaint> list = complaintDAO.getAllComplaints();
        check("Retrieved seeded complaints from InHabitDB", list.size() >= 10);

        // Test 7: Raghav's complaints (Ticket 1001 Wi-Fi)
        List<Complaint> raghavComplaints = complaintDAO.getComplaintsByStudent("257122235");
        check("Raghav has filed Ticket 1001 Wi-Fi", !raghavComplaints.isEmpty() && raghavComplaints.get(0).getTicketId() == 1001);

        // Test 8: Aniruddha's complaints (Ticket 1002 Solar water)
        List<Complaint> aniruddhaComplaints = complaintDAO.getComplaintsByStudent("257122247");
        check("Aniruddha has filed Ticket 1002 Solar water", !aniruddhaComplaints.isEmpty() && aniruddhaComplaints.get(0).getTicketId() == 1002);

        // Test 9: Get full complaint details by ID
        Complaint c1001 = complaintDAO.getComplaintById(1001);
        check("Fetch complaint details by ID (Ticket 1001)", c1001 != null && c1001.getDescription().contains("Cyberoam/Fortinet"));

        // Test 10: Status update (Admin capability)
        boolean statusUpdated = complaintDAO.updateStatus(1004, "In Progress");
        check("Admin update complaint status (Ticket 1004)", statusUpdated);

        // Test 11: Mess Issue (Ticket 1003) escalation threshold
        Complaint c1003 = complaintDAO.getComplaintById(1003);
        check("Ticket 1003 has >= 30 votes and is marked CONCERNING", c1003 != null && c1003.getVoteCount() >= 30 && c1003.isConcerning());

        // Test 12: Admin student and staff lists
        List<Student> allStudents = userDAO.getAllStudents();
        List<Staff> allStaff = userDAO.getAllStaff();
        check("Admin can view all 500 students (size = " + allStudents.size() + ")", allStudents.size() >= 500);
        check("Admin can view all staff (20 staff members)", allStaff.size() >= 20);

        // Test 13: Self-voting prevention
        try {
            complaintDAO.voteComplaint(1001, "257122235", "Genuine");
            check("Self-voting prevention enforced", false);
        } catch (InvalidVoteException e) {
            check("Self-voting prevention enforced", true);
        }

        // Test 14: Aniruddha self-voting prevention on Ticket 1002
        try {
            complaintDAO.voteComplaint(1002, "257122247", "Genuine");
            check("Aniruddha self-voting prevention on Ticket 1002", false);
        } catch (InvalidVoteException e) {
            check("Aniruddha self-voting prevention on Ticket 1002", true);
        }

        // Test 15: Search complaints by keyword
        List<Complaint> searchKw = complaintDAO.searchComplaints("Wi-Fi", "All", "All", "All");
        check("Search complaints by keyword 'Wi-Fi' returns Ticket 1001", !searchKw.isEmpty() && searchKw.get(0).getTicketId() == 1001);

        // Test 16: Search complaints by Category separator
        List<Complaint> searchCat = complaintDAO.searchComplaints("", "Plumbing", "All", "All");
        boolean allPlumbing = !searchCat.isEmpty();
        for (Complaint c : searchCat) {
            if (!"Plumbing".equalsIgnoreCase(c.getCategory())) allPlumbing = false;
        }
        check("Category separator filters all 'Plumbing' complaints", allPlumbing);

        // Test 17: Search complaints by Wing separator
        List<Complaint> searchWing = complaintDAO.searchComplaints("", "All", "All", "Wing Alpha");
        boolean allAlpha = !searchWing.isEmpty();
        for (Complaint c : searchWing) {
            if (c.getLocation() == null || !c.getLocation().toLowerCase().startsWith("alpha")) allAlpha = false;
        }
        check("Wing separator filters 'Wing Alpha' complaints", allAlpha);

        // Test 18: Search student directory by query
        List<Student> searchStudents = userDAO.searchStudents("Aniruddha");
        check("Search students by name 'Aniruddha' returns matching student", !searchStudents.isEmpty() && "257122247".equals(searchStudents.get(0).getPrn()));

        // Test 19: Search staff directory by trade/role
        List<Staff> searchElectricians = userDAO.searchStaff("Electrician");
        check("Search staff by trade 'Electrician' returns electricians", searchElectricians.size() >= 2);

        // Test 20: Lost and Found notices retrieval
        List<LostAndFoundItem> lfItems = complaintDAO.getAllLostAndFound();
        check("Retrieve seeded Lost and Found campus notices (10 items)", lfItems.size() >= 10);

        // Test 21: Search Lost and Found by keyword
        List<LostAndFoundItem> searchCalc = complaintDAO.searchLostAndFound("Calculator", "All", "All");
        check("Search Lost and Found for 'Calculator' returns LF101", !searchCalc.isEmpty() && "LF101".equals(searchCalc.get(0).getItemId()));

        // Test 22: Search Lost and Found by Type separator ("Lost" vs "Found")
        List<LostAndFoundItem> lostOnly = complaintDAO.searchLostAndFound("", "Lost", "All");
        boolean allLost = !lostOnly.isEmpty();
        for (LostAndFoundItem item : lostOnly) {
            if (!"Lost".equalsIgnoreCase(item.getItemType())) allLost = false;
        }
        check("Type separator filters 'Lost' notices", allLost);

        // Test 23: Report new Lost and Found notice
        LostAndFoundItem testItem = new LostAndFoundItem(null, "Logitech Wireless Presenter Remote", "Black laser pointer remote left on podium", "Seminar Hall 1", "Found", "Open", "257122235", null);
        boolean reported = complaintDAO.reportLostAndFound(testItem);
        check("Post new Lost and Found notice", reported && testItem.getItemId() != null);

        // Test 24: Claim Lost and Found notice
        boolean claimed = complaintDAO.claimLostAndFound(testItem.getItemId());
        check("Claim Lost and Found item notice", claimed);

        // Test 25: Technician complaint assignment
        boolean assigned = complaintDAO.assignStaff(1005, "STF106");
        List<Complaint> techComplaints = complaintDAO.getComplaintsByAssignedStaff("STF106");
        check("Technician assignment retrieval for STF106", assigned && !techComplaints.isEmpty());

        System.out.println("-------------------------------------------------");
        System.out.println("Results: " + passed + " / " + total + " tests passed (" + (passed * 100 / total) + "%).");
        System.out.println("=================================================");
    }
}
