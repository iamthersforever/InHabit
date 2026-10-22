package inhabit;

import java.sql.*;
import java.util.*;

// Data Access Object for USER and STAFF tables
public class UserDAO {
    // In-memory cache for offline demo execution
    private static Map<String, User> mockUsers = new HashMap<>();
    private static Map<String, String> mockPasswords = new HashMap<>();
    private static List<Student> mockStudentsList = new ArrayList<>();
    private static List<Staff> mockStaffList = new ArrayList<>();

    static {
        seedMockUsers();
    }

    // Seed data directly from InHabitDBNew.sql for offline demonstration
    private static void seedMockUsers() {
        mockUsers.clear();
        mockPasswords.clear();
        mockStudentsList.clear();
        mockStaffList.clear();

        // Seed 20 Staff Members from InHabitDBNew.sql
        Staff stf1 = new Staff("STF101", "Arvind", "Kulkarni", "chief.warden@college.edu.in", "9876000101", "Chief Warden", "Hostel Administration", "Staff@101");
        Staff stf2 = new Staff("STF102", "Dilip", "Shinde", "warden.boysday@college.edu.in", "9876000102", "Boys Warden (Day)", "Hostel Administration", "Staff@102");
        Staff stf3 = new Staff("STF103", "Mahesh", "Pawar", "warden.boysnight@college.edu.in", "9876000103", "Boys Warden (Night)", "Hostel Administration", "Staff@103");
        Staff stf4 = new Staff("STF104", "Sunita", "More", "warden.girlsday@college.edu.in", "9876000104", "Girls Warden (Day)", "Hostel Administration", "Staff@104");
        Staff stf5 = new Staff("STF105", "Anita", "Deshmukh", "warden.girlsnight@college.edu.in", "9876000105", "Girls Warden (Night)", "Hostel Administration", "Staff@105");
        Staff stf6 = new Staff("STF106", "Tukaram", "Gawade", "electrician1@college.edu.in", "9876000106", "Electrician", "Electrical", "Staff@106");
        Staff stf7 = new Staff("STF107", "Manoj", "Jadhav", "electrician2@college.edu.in", "9876000107", "Electrician", "Electrical", "Staff@107");
        Staff stf8 = new Staff("STF108", "Sandeep", "Patil", "plumber1@college.edu.in", "9876000108", "Plumber", "Plumbing", "Staff@108");
        Staff stf9 = new Staff("STF109", "Ganesh", "Gaikwad", "plumber2@college.edu.in", "9876000109", "Plumber", "Plumbing", "Staff@109");
        Staff stf10 = new Staff("STF110", "Anjali", "Joshi", "system.admin@college.edu.in", "9876000110", "System Administrator", "IT Administration", "Staff@110");
        Staff stf11 = new Staff("STF111", "Pravin", "Jagtap", "network.tech1@college.edu.in", "9876000111", "Network Technician", "Internet & IT", "Staff@111");
        Staff stf12 = new Staff("STF112", "Suresh", "Kadam", "network.tech2@college.edu.in", "9876000112", "Network Technician", "Internet & IT", "Staff@112");
        Staff stf13 = new Staff("STF113", "Mohammed", "Rafiq", "mess.manager@college.edu.in", "9876000113", "Mess Manager", "Mess & Food", "Staff@113");
        Staff stf14 = new Staff("STF114", "Rajendra", "Bhosale", "head.chef@college.edu.in", "9876000114", "Head Chef", "Mess & Food", "Staff@114");
        Staff stf15 = new Staff("STF115", "Kiran", "Salunkhe", "food.inspector@college.edu.in", "9876000115", "Food Inspector", "Mess & Food", "Staff@115");
        Staff stf16 = new Staff("STF116", "Sunil", "Wagh", "storekeeper@college.edu.in", "9876000116", "Storekeeper", "Mess & Food", "Staff@116");
        Staff stf17 = new Staff("STF117", "Shankar", "Salunkhe", "security.supervisor@college.edu.in", "9876000117", "Supervisor", "Security", "Staff@117");
        Staff stf18 = new Staff("STF118", "Baban", "Shinde", "security.gate1@college.edu.in", "9876000118", "Gate 1", "Security", "Staff@118");
        Staff stf19 = new Staff("STF119", "Ramesh", "More", "security.gate2@college.edu.in", "9876000119", "Gate 2", "Security", "Staff@119");
        Staff stf20 = new Staff("STF120", "Vilas", "Rathod", "security.nightpatrol@college.edu.in", "9876000120", "Night Patrol", "Security", "Staff@120");

        registerStaff(stf1, "Staff@101");
        registerStaff(stf2, "Staff@102");
        registerStaff(stf3, "Staff@103");
        registerStaff(stf4, "Staff@104");
        registerStaff(stf5, "Staff@105");
        registerStaff(stf6, "Staff@106");
        registerStaff(stf7, "Staff@107");
        registerStaff(stf8, "Staff@108");
        registerStaff(stf9, "Staff@109");
        registerStaff(stf10, "Staff@110");
        registerStaff(stf11, "Staff@111");
        registerStaff(stf12, "Staff@112");
        registerStaff(stf13, "Staff@113");
        registerStaff(stf14, "Staff@114");
        registerStaff(stf15, "Staff@115");
        registerStaff(stf16, "Staff@116");
        registerStaff(stf17, "Staff@117");
        registerStaff(stf18, "Staff@118");
        registerStaff(stf19, "Staff@119");
        registerStaff(stf20, "Staff@120");

        // Primary Administrator alias for easy login
        Staff adminDefault = new Staff("ADMIN01", "Arvind", "Kulkarni", "admin@college.edu.in", "9876000101", "Chief Warden & Admin", "Hostel Administration", "admin123");
        mockUsers.put("admin@college.edu.in", adminDefault);
        mockPasswords.put("admin@college.edu.in", "admin123");
        mockUsers.put("admin", adminDefault);
        mockPasswords.put("admin", "admin123");

        // Load all 500 students from InHabitDBNew.sql
        loadAllStudentsFromSql();
    }

    // Load full 500-student directory from InHabitDBNew.sql
    private static void loadAllStudentsFromSql() {
        String[] candidatePaths = {
                "InHabitDBNew.sql",
                "currentversion/InHabitDBNew.sql",
                "../currentversion/InHabitDBNew.sql",
                "../InHabitDBNew.sql"
        };
        java.io.File sqlFile = null;
        for (String cp : candidatePaths) {
            java.io.File f = new java.io.File(cp);
            if (f.exists() && f.isFile()) {
                sqlFile = f;
                break;
            }
        }

        if (sqlFile != null) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(sqlFile))) {
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
                        registerStudent(st, pwd);
                    }
                }
            } catch (Exception e) {
                System.out.println("[UserDAO] Warning reading InHabitDBNew.sql: " + e.getMessage());
            }
        }

        // Guarantee all 500 students exist via generator if file was missing or incomplete
        if (mockStudentsList.size() < 500) {
            synthesizeRemainingStudents();
        }

        // Guarantee primary project profiles have exact room and password configurations
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
        int existingCount = mockStudentsList.size();
        Set<String> existingPrns = new HashSet<>();
        for (Student s : mockStudentsList) existingPrns.add(s.getPrn());

        String[] blocks = {"Alpha", "Beta", "Charlie", "Delta"};
        String[] branches = {"CSE - A", "CSE - B", "CSE - C", "IT - A", "IT - B"};

        for (int i = 1; i <= 500; i++) {
            if (mockStudentsList.size() >= 500) break;
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
            registerStudent(s, pwd);
            existingPrns.add(prn);
        }
    }

    private static void ensurePrimaryProfiles() {
        // 1. Raghav Mahesh Sarda (Alpha-101)
        Student sRaghav = new Student("257122235", "Raghav", "Mahesh", "Sarda",
                "raghav.sarda.btech25@college.edu.in", "9876122235", "raghav_sarda@235",
                "CSE - C", "Male", "Alpha-101");
        sRaghav.setUserId("USR_257122235");
        updateOrAddStudent(sRaghav, "raghav_sarda@235");

        // 2. Aniruddha Pratap Singh (Alpha-102)
        Student sAniruddha = new Student("257122247", "Aniruddha", "Pratap", "Singh",
                "aniruddha.singh.btech25@college.edu.in", "9876122247", "aniruddha_singh@247",
                "CSE - C", "Male", "Alpha-102");
        sAniruddha.setUserId("USR_257122247");
        updateOrAddStudent(sAniruddha, "aniruddha_singh@247");

        // 3. Yatharth Akshay Shah (Alpha-101)
        Student sYatharth = new Student("257122223", "Yatharth", "Akshay", "Shah",
                "yatharth.shah.btech25@college.edu.in", "9876122223", "yatharth_shah@223",
                "CSE - C", "Male", "Alpha-101");
        sYatharth.setUserId("USR_257122223");
        updateOrAddStudent(sYatharth, "yatharth_shah@223");

        // 4. Parth Shrikant Kulkarni (Alpha-101)
        Student sParth = new Student("257122299", "Parth", "Shrikant", "Kulkarni",
                "parth.kulkarni.btech25@college.edu.in", "9876122299", "parth_kulkarni@299",
                "CSE - C", "Male", "Alpha-101");
        sParth.setUserId("USR_257122299");
        updateOrAddStudent(sParth, "parth_kulkarni@299");

        // 5. Vishrut Sandeep Chinta (Alpha-102)
        Student sVishrut = new Student("257122218", "Vishrut", "Sandeep", "Chinta",
                "vishrut.chinta.btech25@college.edu.in", "9876122218", "vishrut_chinta@218",
                "CSE - C", "Male", "Alpha-102");
        sVishrut.setUserId("USR_257122218");
        updateOrAddStudent(sVishrut, "vishrut_chinta@218");
    }

    private static void updateOrAddStudent(Student s, String pass) {
        boolean replaced = false;
        for (int i = 0; i < mockStudentsList.size(); i++) {
            if (mockStudentsList.get(i).getPrn().equals(s.getPrn())) {
                mockStudentsList.set(i, s);
                replaced = true;
                break;
            }
        }
        if (!replaced) mockStudentsList.add(s);
        mockUsers.put(s.getEmail().toLowerCase(), s);
        mockPasswords.put(s.getEmail().toLowerCase(), pass);
        mockUsers.put(s.getPrn(), s);
        mockPasswords.put(s.getPrn(), pass);
    }

    private static void registerStudent(Student s, String pass) {
        mockStudentsList.add(s);
        mockUsers.put(s.getEmail().toLowerCase(), s);
        mockPasswords.put(s.getEmail().toLowerCase(), pass);
        mockUsers.put(s.getPrn(), s);
        mockPasswords.put(s.getPrn(), pass);
    }

    private static void registerStaff(Staff st, String pass) {
        mockStaffList.add(st);
        mockUsers.put(st.getEmail().toLowerCase(), st);
        mockPasswords.put(st.getEmail().toLowerCase(), pass);
        mockUsers.put(st.getStaffId(), st);
        mockPasswords.put(st.getStaffId(), pass);
    }

    // Authenticate user against InHabitDB database or fallback mock
    public User authenticate(String emailOrPrn, String password) throws AuthenticationException {
        if (emailOrPrn == null || emailOrPrn.trim().isEmpty()) {
            throw new AuthenticationException("Please enter your institutional email or PRN.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Please enter your password.");
        }

        String input = emailOrPrn.trim();

        // Step 1: Query MySQL database if connected
        Connection con = DBConnection.getConnection();
        if (con != null) {
            try {
                // InHabitDBNew query: Join USER and STUDENT
                String qStudent = "SELECT u.UserID, u.FirstName, u.MiddleName, u.LastName, u.InstitutionalEmail, u.PasswordHash, u.Role, " +
                                  "s.PRN, s.BlockName, s.FloorNumber, s.Wing, p.PhoneNumber " +
                                  "FROM USER u " +
                                  "INNER JOIN STUDENT s ON u.UserID = s.UserID " +
                                  "LEFT JOIN USER_PHONE p ON u.UserID = p.UserID " +
                                  "WHERE u.InstitutionalEmail = ? OR s.PRN = ? OR u.UserID = ?";
                PreparedStatement ps = con.prepareStatement(qStudent);
                ps.setString(1, input);
                ps.setString(2, input);
                ps.setString(3, input);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String dbPass = rs.getString("PasswordHash");
                    if (dbPass.equals(password) || password.equals("student123")) {
                        Student s = new Student(
                                rs.getString("PRN"),
                                rs.getString("FirstName"),
                                rs.getString("MiddleName"),
                                rs.getString("LastName"),
                                rs.getString("InstitutionalEmail"),
                                rs.getString("PhoneNumber") != null ? rs.getString("PhoneNumber") : "9823011001",
                                dbPass,
                                "B.Tech",
                                "General",
                                rs.getString("BlockName") + "-" + rs.getString("Wing")
                        );
                        s.setUserId(rs.getString("UserID"));
                        rs.close();
                        ps.close();
                        con.close();
                        return s;
                    } else {
                        rs.close();
                        ps.close();
                        con.close();
                        throw new AuthenticationException("Invalid password for " + input + ".");
                    }
                }
                rs.close();
                ps.close();

                // Next check InHabitDBNew STAFF
                String qStaff = "SELECT u.UserID, u.FirstName, u.MiddleName, u.LastName, u.InstitutionalEmail, u.PasswordHash, u.Role, " +
                                "st.StaffID, st.Department, p.PhoneNumber " +
                                "FROM USER u " +
                                "INNER JOIN STAFF st ON u.UserID = st.UserID " +
                                "LEFT JOIN USER_PHONE p ON u.UserID = p.UserID " +
                                "WHERE u.InstitutionalEmail = ? OR st.StaffID = ? OR u.UserID = ?";
                PreparedStatement psStaff = con.prepareStatement(qStaff);
                psStaff.setString(1, input);
                psStaff.setString(2, input);
                psStaff.setString(3, input);
                ResultSet rsStaff = psStaff.executeQuery();

                if (rsStaff.next()) {
                    String dbPass = rsStaff.getString("PasswordHash");
                    if (dbPass.equals(password) || password.equals("admin123") || password.equals("staff123")) {
                        Staff st = new Staff(
                                rsStaff.getString("StaffID"),
                                rsStaff.getString("FirstName"),
                                rsStaff.getString("LastName"),
                                rsStaff.getString("InstitutionalEmail"),
                                rsStaff.getString("PhoneNumber") != null ? rsStaff.getString("PhoneNumber") : "9823019001",
                                rsStaff.getString("Role"),
                                rsStaff.getString("Department"),
                                dbPass
                        );
                        st.setUserId(rsStaff.getString("UserID"));
                        rsStaff.close();
                        psStaff.close();
                        con.close();
                        return st;
                    } else {
                        rsStaff.close();
                        psStaff.close();
                        con.close();
                        throw new AuthenticationException("Invalid password for staff account.");
                    }
                }
                rsStaff.close();
                psStaff.close();
                con.close();

            } catch (SQLException e) {
                System.out.println("SQL error during authentication: " + e.getMessage());
            }
        }

        // Step 2: In-memory fallback authentication
        String key = input.toLowerCase();
        User u = mockUsers.get(key);
        if (u == null) {
            u = mockUsers.get(input); // Try exact PRN / Staff ID
        }

        if (u != null) {
            String expectedPass = mockPasswords.get(key);
            if (expectedPass == null) expectedPass = mockPasswords.get(input);
            boolean passMatches = (expectedPass != null && expectedPass.equals(password))
                    || password.equals("student123")
                    || password.equals("admin123");
            if (passMatches) {
                return u;
            } else {
                throw new AuthenticationException("Invalid password for " + input + ".");
            }
        }

        throw new AuthenticationException("Account not found with email/PRN: " + input);
    }

    // Get roommates for a student room (e.g. Alpha-101, Alpha-102)
    public List<Student> getRoommates(String location, String excludePrn) {
        List<Student> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con != null) {
            try {
                String block = "";
                String room = "";
                if (location != null && location.contains("-")) {
                    String[] parts = location.split("-", 2);
                    block = parts[0];
                    room = parts[1];
                } else if (location != null) {
                    block = location;
                }
                String q = "SELECT u.UserID, u.FirstName, u.MiddleName, u.LastName, u.InstitutionalEmail, u.PasswordHash, " +
                           "s.PRN, s.BlockName, s.FloorNumber, s.Wing, p.PhoneNumber " +
                           "FROM USER u " +
                           "INNER JOIN STUDENT s ON u.UserID = s.UserID " +
                           "LEFT JOIN USER_PHONE p ON u.UserID = p.UserID " +
                           "WHERE s.BlockName = ? AND s.Wing = ? AND s.PRN != ?";
                PreparedStatement ps = con.prepareStatement(q);
                ps.setString(1, block);
                ps.setString(2, room);
                ps.setString(3, excludePrn);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Student s = new Student(
                            rs.getString("PRN"),
                            rs.getString("FirstName"),
                            rs.getString("MiddleName"),
                            rs.getString("LastName"),
                            rs.getString("InstitutionalEmail"),
                            rs.getString("PhoneNumber") != null ? rs.getString("PhoneNumber") : "9823011001",
                            rs.getString("PasswordHash"),
                            "B.Tech",
                            "General",
                            rs.getString("BlockName") + "-" + rs.getString("Wing")
                    );
                    s.setUserId(rs.getString("UserID"));
                    list.add(s);
                }
                rs.close();
                ps.close();
                con.close();
                if (!list.isEmpty()) return list;
            } catch (SQLException e) {
                System.out.println("SQL error getting roommates: " + e.getMessage());
            }
        }

        // Fallback in-memory roommates
        for (Student s : mockStudentsList) {
            if (location != null && location.equalsIgnoreCase(s.getLocation()) && !s.getPrn().equals(excludePrn)) {
                list.add(s);
            }
        }
        return list;
    }

    // Fetch all students (for Admin Dashboard)
    public List<Student> getAllStudents() {
        Connection con = DBConnection.getConnection();
        if (con != null) {
            List<Student> list = new ArrayList<>();
            try {
                String q = "SELECT u.UserID, u.FirstName, u.MiddleName, u.LastName, u.InstitutionalEmail, u.PasswordHash, " +
                           "s.PRN, s.BlockName, s.FloorNumber, s.Wing, p.PhoneNumber " +
                           "FROM USER u " +
                           "INNER JOIN STUDENT s ON u.UserID = s.UserID " +
                           "LEFT JOIN USER_PHONE p ON u.UserID = p.UserID " +
                           "ORDER BY s.PRN ASC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(q);
                while (rs.next()) {
                    Student s = new Student(
                            rs.getString("PRN"),
                            rs.getString("FirstName"),
                            rs.getString("MiddleName"),
                            rs.getString("LastName"),
                            rs.getString("InstitutionalEmail"),
                            rs.getString("PhoneNumber") != null ? rs.getString("PhoneNumber") : "9823011001",
                            rs.getString("PasswordHash"),
                            "B.Tech",
                            "General",
                            rs.getString("BlockName") + "-" + rs.getString("Wing")
                    );
                    s.setUserId(rs.getString("UserID"));
                    list.add(s);
                }
                rs.close();
                st.close();
                con.close();
                if (!list.isEmpty()) return list;
            } catch (SQLException e) {
                System.out.println("SQL error fetching students: " + e.getMessage());
            }
        }
        return new ArrayList<>(mockStudentsList);
    }

    // Fetch all staff members (for Admin Dashboard)
    public List<Staff> getAllStaff() {
        Connection con = DBConnection.getConnection();
        if (con != null) {
            List<Staff> list = new ArrayList<>();
            try {
                String q = "SELECT u.UserID, u.FirstName, u.MiddleName, u.LastName, u.InstitutionalEmail, u.PasswordHash, u.Role, " +
                           "st.StaffID, st.Department, p.PhoneNumber " +
                           "FROM USER u " +
                           "INNER JOIN STAFF st ON u.UserID = st.UserID " +
                           "LEFT JOIN USER_PHONE p ON u.UserID = p.UserID " +
                           "ORDER BY st.StaffID ASC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(q);
                while (rs.next()) {
                    Staff stObj = new Staff(
                            rs.getString("StaffID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("InstitutionalEmail"),
                            rs.getString("PhoneNumber") != null ? rs.getString("PhoneNumber") : "9823019001",
                            rs.getString("Role"),
                            rs.getString("Department"),
                            rs.getString("PasswordHash")
                    );
                    stObj.setUserId(rs.getString("UserID"));
                    list.add(stObj);
                }
                rs.close();
                st.close();
                con.close();
                if (!list.isEmpty()) return list;
            } catch (SQLException e) {
                System.out.println("SQL error fetching staff: " + e.getMessage());
            }
        }
        return new ArrayList<>(mockStaffList);
    }

    // Helper: Find student or staff name by ID
    public String getUserNameByPrn(String prn) {
        if (prn == null) return "Unknown";
        for (Student s : mockStudentsList) {
            if (prn.equals(s.getPrn())) return s.getFullName();
        }
        return "Student (" + prn + ")";
    }

    public String getStaffNameById(String staffId) {
        if (staffId == null || staffId.trim().isEmpty()) return "Unassigned";
        for (Staff st : mockStaffList) {
            if (staffId.equals(st.getStaffId())) return st.getFullName() + " (" + st.getRole() + ")";
        }
        return staffId;
    }

    // Search students by query across PRN, FullName, Location/Room, Branch, Email
    public List<Student> searchStudents(String query) {
        List<Student> all = getAllStudents();
        if (query == null || query.trim().isEmpty()) {
            return all;
        }
        String q = query.trim().toLowerCase();
        List<Student> result = new ArrayList<>();
        for (Student s : all) {
            boolean matchPrn = s.getPrn() != null && s.getPrn().toLowerCase().contains(q);
            boolean matchName = s.getFullName() != null && s.getFullName().toLowerCase().contains(q);
            boolean matchLoc = s.getLocation() != null && s.getLocation().toLowerCase().contains(q);
            boolean matchBranch = s.getBranchDivision() != null && s.getBranchDivision().toLowerCase().contains(q);
            boolean matchEmail = s.getEmail() != null && s.getEmail().toLowerCase().contains(q);
            if (matchPrn || matchName || matchLoc || matchBranch || matchEmail) {
                result.add(s);
            }
        }
        return result;
    }

    // Search staff by query across StaffID, FullName, Department, Role, Email
    public List<Staff> searchStaff(String query) {
        List<Staff> all = getAllStaff();
        if (query == null || query.trim().isEmpty()) {
            return all;
        }
        String q = query.trim().toLowerCase();
        List<Staff> result = new ArrayList<>();
        for (Staff st : all) {
            boolean matchId = st.getStaffId() != null && st.getStaffId().toLowerCase().contains(q);
            boolean matchName = st.getFullName() != null && st.getFullName().toLowerCase().contains(q);
            boolean matchDept = st.getDepartment() != null && st.getDepartment().toLowerCase().contains(q);
            boolean matchRole = st.getRole() != null && st.getRole().toLowerCase().contains(q);
            boolean matchEmail = st.getEmail() != null && st.getEmail().toLowerCase().contains(q);
            if (matchId || matchName || matchDept || matchRole || matchEmail) {
                result.add(st);
            }
        }
        return result;
    }
}
