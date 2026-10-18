package inhabit;

import java.util.*;

// Base class for all users in the system (OOP concept: Inheritance)
public class User {
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

    public User() {
    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrn() {
        return prn;
    }

    public void setPrn(String prn) {
        this.prn = prn;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        if (middleName != null && !middleName.trim().isEmpty()) {
            return firstName + " " + middleName + " " + lastName;
        }
        return firstName + " " + (lastName != null ? lastName : "");
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
