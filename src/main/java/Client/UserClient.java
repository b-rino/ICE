package Client;

import DataSource.UserMapper;
import org.mindrot.jbcrypt.BCrypt;
import DataSource.*;
import UI.*;
import Model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserClient {

    private DBConnector DBConnector = new DBConnector();
    private UserMapper userMapper = new UserMapper();
    private TextUI ui = new TextUI();
    private User currentUser;

    public UserClient(User currentUser) {
        this.currentUser = currentUser;
    }


    public User loginMenu() {
        ui.displayMsg("Welcome to BlogBuster. Please create an account or log in.");
        int choice = ui.promptNumeric("1. Log in\n2. Create Account\n3. Forgot Password");

        if (choice == 1) {
            currentUser = login();
            return currentUser;// Perform login and return the logged-in user
        } else if (choice == 2) {
            createUser();
            currentUser = login();
            return currentUser;
        } else if (choice == 3) {
            forgotPassword();
            return loginMenu();
        } else {
            ui.displayMsg("Invalid choice");
            return loginMenu();
        }
    }

    public void forgotPassword() {
        String enterUsername = ui.promptText("Please enter your username: ");
        if (!doesUsernameExist(enterUsername)) {
            ui.displayMsg("The username does not exist.");
            return;
        }
        String enterEmail = ui.promptText("Please enter your email: ");
        if (enterEmail.isEmpty() || !enterEmail.contains("@")) {
            ui.displayMsg("Email does not exist.");
            return;
        }
        String newPassword = ui.promptText("Please enter your new password (minimum 4 character)");
        if (newPassword.length() < 4) {
            ui.displayMsg("Password is too short, minimum 4 characters long");
            return;
        }
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        String sql = "UPDATE Users SET Password = ? WHERE Username = ?";

        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, enterUsername);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                ui.displayMsg("Your password has been updated successfully!");
            } else {
                ui.displayMsg("An error occurred. Please try again.");
            }
        } catch (SQLException e) {
            ui.displayMsg("Error updating password: " + e.getMessage());
        }
    }

    public void changePassword() {
        String enterUsername = ui.promptText("Please enter your username: ");
        if (!doesUsernameExist(enterUsername)) {
            ui.displayMsg("The username does not exist.");
            return;
        }
        String enterEmail = ui.promptText("Please enter your email: ");
        if (enterEmail.isEmpty() || !enterEmail.contains("@")) {
            changePassword();
            return;
        }

        String newPassword = ui.promptText("Please enter your new password (minimum 4 character)");
        if (newPassword.length() < 4) {
            ui.displayMsg("Password is too short, minimum 4 characters long");
            return;
        }
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        String sql = "UPDATE Users SET Password = ? WHERE Username = ?";

        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, enterUsername);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                ui.displayMsg("Your password has been updated successfully!");
            } else {
                ui.displayMsg("An error occurred. Please try again.");
            }
        } catch (SQLException e) {
            ui.displayMsg("Error updating password: " + e.getMessage());
        }
    }

    public void createUser() {
        String email = ui.promptText("Please enter your email: ");
        if (email.equals("") || !email.contains("@")) {
            ui.displayMsg("Email must not be empty and must contain '@'. Please try again.");
            createUser();
            return;
        }
        String username = ui.promptText("Please enter a username of minimum 1 and maximum 12 characters: ");
        if (username.equals("") || username.length() > 12) {
            ui.displayMsg("Please enter a valid username\n");
            createUser();
            return;
        }

        if (doesUsernameExist(username)) {
            ui.displayMsg("Username already exists\n");
            createUser();
            return;
        }

        String password = ui.promptText("Please enter a password of a minimum 4 characters: ");
        if (password.length() < 4) {
            ui.displayMsg("Please enter a valid password\n");
            createUser();
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "INSERT INTO Users (Username, Password, email) VALUES (?, ?, ?)";

        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            System.out.println("Account Created!");
        } catch (SQLException e) {
            System.out.println("Error inserting player: " + e.getMessage());
        }
    }

    public User login() {
        String username = ui.promptText("Please enter a username: ");
        String password = ui.promptText("Please enter a password: ");


        String sql = "SELECT * FROM Users WHERE username = ?"; //Finding the table in Users where "username" and "password" match what the user inputs. The "?" tells the database we will find the value later

        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) { //PreparedStatement allows the database to pre-compile the query structure, and it knows that the "?" are placeholders.
            pstmt.setString(1, username); //Here, with the "setString" method, we tell the database to take the value of the variable "username" and place it in for the first "?" in the query
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("Password");
                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    ui.displayMsg("\nLogin successful! Welcome " + username + "\n");
                    String dbUsername = rs.getString("Username");
                    String dbPassword = rs.getString("Password");
                    String dbEmail = rs.getString("Email");
                    currentUser = new User(dbUsername, dbPassword, dbEmail);
                    currentUser.setBalance(userMapper.getUserBalance(dbUsername));
                    return currentUser;
                }
                else {
                    ui.displayMsg("Invalid username or password. Please try again.");
                    return loginMenu();
                }

            }
            else {
                ui.displayMsg("Invalid username or password. Please try again.");
                return loginMenu();
            }
        } catch (SQLException e) {
            System.out.println("Error inserting player: " + e.getMessage());
            return null;
        }
    }

    public void addFunds() {
        boolean passwordCheck = false;
        String password = ui.promptText("Please enter your password: ");
        if(BCrypt.checkpw(password, currentUser.getPassword())) {
            passwordCheck = true;
        }
        if (passwordCheck) {
            int amount = ui.promptNumeric("How much do you want to add");
            String sql = "UPDATE Users SET balance = balance + ? WHERE username = ?";
            try (Connection conn = DBConnector.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, amount);
                pstmt.setString(2, currentUser.getUsername());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Successfully added " + amount);
                    displayAccount();
                } else {
                    System.out.println("Failed to add " + amount + " funds.");
                    displayAccount();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            ui.displayMsg("\nPasswords do not match");
            displayAccount();
        }
    }

    public void displayAccount() {
        MediaClient mc = new MediaClient(currentUser);
        String membershipActive;
        int balance = userMapper.getUserBalance(currentUser.getUsername());
        if (userMapper.getUserMembership(currentUser.getUsername()) == 1){
            membershipActive = "ACTIVE";
        }
        else {
            membershipActive = "INACTIVE";
        }
        ui.displayMsg("\nACCOUNT INFORMATION\nUSERNAME:" + currentUser.getUsername() + "   BALANCE:" + balance + "   MEMBERSHIP:" + membershipActive + "   AVAILABLE PUNCHES:" + userMapper.getUserPunchcardBalance(currentUser.getUsername()));
        ArrayList<String> accountOptions = new ArrayList<>();
        accountOptions.add("1. Add funds");
        accountOptions.add("2. Buy membership");
        accountOptions.add("3. Return to main menu");
        accountOptions.add("4. Change Password");
        accountOptions.add("5. Delete account");


        for (int i = 0; i < accountOptions.size(); i++) {
            System.out.println(accountOptions.get(i));
        }

        int answer = ui.promptNumeric("Please choose a number");

        switch (answer) {
            case 1:
                addFunds();
                break;
            case 2:
                buyMembership();
                break;
            case 3:
                mc.displayMenu();
                break;
            case 4:
                changePassword();
                displayAccount();
                break;
            case 5:
                deleteAccount();
                break;
            default:
                System.out.println("Invalid choice - please choose a number between 1 and 4");
                displayAccount();
                break;
        }
    }

    public void deleteAccount() {
        String answer = ui.promptText("Are you sure you want to delete this account? (Y/N)");
        if (answer.equalsIgnoreCase("Y")) {
            ui.displayMsg("Thanks for using BlogBuster. You are always welcome back.\nYour account has been deleted.");
            userMapper.deleteUserData(currentUser);
            System.exit(0);
        }
        else if (answer.equalsIgnoreCase("N")) {
            displayAccount();
        }
        else{
            System.out.println("Invalid choice");
            displayAccount();
        }
    }

    public void buyMembership() {
        MediaClient mc = new MediaClient(currentUser);
        ui.displayMsg("Welcome to Club BlogBuster\nA membership at Club BlogBuster includes:" +
                "\n- Punch card with 10 punches for using content of your choice\n- Extended rental period\n"); //1 min for non-members and 2 min for members
        String answer = ui.promptText("Do you want to buy a membership for 200? (Y/N)");
        if (answer.equalsIgnoreCase("n")) {
            displayAccount();
        }
        if (answer.equalsIgnoreCase("y")) {
            if (userMapper.getUserBalance(currentUser.getUsername()) >= 200 && userMapper.getUserMembership(currentUser.getUsername()) == 0) {
                ui.displayMsg("Congratulations! You are now a member of Club BlogBuster - enjoy your membership\n");
                userMapper.updateUserBalance(currentUser, 200, true);
                userMapper.updateUserPunchcard(currentUser, 10);
                userMapper.updateUserMembership(currentUser, 1);
                mc.displayMenu();
            }
            else if (userMapper.getUserMembership(currentUser.getUsername()) == 1) {
                ui.displayMsg("You are already member of Club BlogBuster and have " + userMapper.getUserPunchcardBalance(currentUser.getUsername()) + " punches left");
                displayAccount();
            }else{
                ui.displayMsg("You have insufficient funds to buy a membership");
                displayAccount();
            }
        }
        else {
            ui.displayMsg("Invalid choice");
            displayAccount();
        }
    }
    private boolean doesUsernameExist(String username){
        String sql = "SELECT COUNT(*) FROM Users WHERE Username = ?";
        try(Connection conn = DBConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()){
            int count = rs.getInt(1);
            return count > 0;
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }


}