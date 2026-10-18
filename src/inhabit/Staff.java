package inhabit;

import java.util.*;

// Staff class extending User (Demonstrates Inheritance for Hostel Administration & Technicians)
public class Staff extends User {
    private String staffId;
    private String department;

    public Staff(String staffId, String firstName, String lastName,
                 String email, String phone, String role, String department, String password) {
        super(staffId, firstName, "", lastName, email, phone, password, department, role);
        this.staffId = staffId;
        this.department = department;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getDepartment() {
        return department;
    }
}
