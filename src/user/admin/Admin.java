package user.admin;

import models.Response;
import user.User_Interface;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import database.routes.Admin_Route;
import models.Customer;
import models.Room;

import java.util.ArrayList;
import java.util.List;

public class Admin implements User_Interface, Admin_Interface {

    @JsonProperty("admin_id")
    private int adminId;

    @JsonProperty("admin_name")
    private String adminName;

    @JsonProperty("admin_password")
    private String adminPassword;

    @JsonProperty("admin_contact")
    private String adminContact;

    @JsonProperty("admin_username")
    private String adminUsername;

    private Admin_Route admin_route;


    // Default constructor (required for deserialization)
    public Admin() {
        admin_route = new Admin_Route();
    }

    // Parameterized constructor (optional)
    public Admin(int adminId, String adminName, String adminPassword, String adminContact, String adminUsername) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.adminPassword = adminPassword;
        this.adminContact = adminContact;
        this.adminUsername = adminUsername;

        admin_route = new Admin_Route();
    }

    // Getters and setters
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getAdminContact() {
        return adminContact;
    }

    public void setAdminContact(String adminContact) {
        this.adminContact = adminContact;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String hashing(String password){

        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Apply the hash function
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array into a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // Print the hashed password in hexadecimal format
//            System.out.println("Original password: " + password);
//            System.out.println("Hashed password: " + hexString.toString());
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(); // Handle error if SHA-256 algorithm is not available
        }
        return "";
    }

    public int login(String username, String password){
        if(username.isEmpty() || password.isEmpty())return 0;
//        password encryption
        String hashedPassword = hashing(password);
//        Check if username and password exist
        return admin_route.login(username,hashedPassword);
    }

    @Override
    public List<Customer> get_customer_details() {
//        Retrieves all current available customers from the table.
        List<Customer> customers = admin_route.get_customers();
        return customers;
    }

    @Override
    public List<Room> check_availability(String checkin, String checkout){
        if(checkin.isEmpty() || checkout.isEmpty()){
            return null;
        }
        return admin_route.check_availability(checkin,checkout);
//        return 1;
    }

    @Override
    public List<Customer> get_audit_details() {
//        retrieves all customers from history
        List<Customer> customers = admin_route.get_audit_table();
        return customers;
    }

    public List<Room> get_room_details(String contact){
        if(contact.isEmpty())return null;

        return admin_route.get_room_details(contact);
    }

    @Override
    public int pay_expenses(int room_no) {
        if (room_no<=0)
            return 0;

        return admin_route.pay_expenses(room_no);
    }

    @Override
    public Response check_out_customer(String contact) {
        return admin_route.checkout_customer(contact);
    }

    //------------
    @Override
    public int book_customer(String check_in, String check_out, String address, String cust_name, String contact, int cnt1, int cnt2) {
        if(check_in.isEmpty() || check_out.isEmpty() || address.isEmpty() || cust_name.isEmpty() || contact.isEmpty())
            return 0;
        return admin_route.book_room(check_in,check_out, address,cust_name,contact,cnt1,cnt2);
    }

    @Override
    public List<Room> check_in_customer(String adhaar_no, String contact) {
        List<Room> alloted_rooms = new ArrayList<>();
        if(adhaar_no.isEmpty() || contact.isEmpty() )
            return null;

        if(admin_route.checkin_customer(adhaar_no,contact)==0)return null;

        return get_room_details(contact);
    }

    @Override
    public int register_staff(String name, String contact, String username, String password){
//        password get encrypted
        if(name.isEmpty() || contact.isEmpty() || username.isEmpty() || password.isEmpty())return 0;
        String hashedPassword = hashing(password);
        return admin_route.register_staff(name,contact,username,hashedPassword);
    }

    @Override
    public int cancel_booking(String contact){
        if(contact.isEmpty())return 0;
        return admin_route.cancel_booking(contact);
    }
}
