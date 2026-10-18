package inhabit;

import java.util.*;

// Student class extending User (Demonstrates Inheritance)
public class Student extends User {
    private String branchDivision;
    private String gender;

    public Student(String prn, String firstName, String middleName, String lastName,
                   String email, String phone, String password,
                   String branchDivision, String gender, String hostelLocation) {
        super(prn, firstName, middleName, lastName, email, phone, password, hostelLocation, "Student");
        this.branchDivision = branchDivision;
        this.gender = gender;
    }

    public String getBranchDivision() {
        return branchDivision;
    }

    public void setBranchDivision(String branchDivision) {
        this.branchDivision = branchDivision;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
