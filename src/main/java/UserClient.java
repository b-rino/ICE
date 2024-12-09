import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class UserClient {

    DBConnector dbConnector = new DBConnector();
    private TextUI ui = new TextUI();

    public ArrayList<String> selectUsers() {
        // initialize a List to return the selected data as string elements
        ArrayList<String> data = new ArrayList<>();
        // make the query string
        String sql = "SELECT Username, PhoneNumber, Password, Email FROM Users";

        try {
            Connection conn = dbConnector.connect();
            Statement stmt = conn.createStatement();

            // execute the query
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                //read each row of the result set ( = response from the query execution)
                String row = rs.getString("Username") + ", " + rs.getInt("PhoneNumber") + ", " + rs.getString("Password") + ", " + rs.getString("Email");
                //add the string to the ArrayList
                data.add(row);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return data;
    }
    public User loginMenu() {
        System.out.println("Welcome to BlogBuster. Please create an account or log in.");
        System.out.println("1. Log in\n2. Create Account");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            return login(); // Perform login and return the logged-in user
        } else if (choice == 2) {
            createUser();
            return login(); // Log in the new user
        } else {
            System.out.println("Invalid choice.");
            return loginMenu();
        }
    }

    public void createUser() {
        Scanner scanner = new Scanner(System.in);
        // TODO: TextUI implemented and used here instead of scanner
        System.out.println("Please enter your username: ");
        String Username = scanner.nextLine();

        System.out.println("Please enter your phonenumber: ");
        int PhoneNumber = scanner.nextInt();
        scanner.nextLine(); // Clear the newline


        System.out.println("Please enter your password: ");
        String Password = scanner.nextLine();

        System.out.println("Please enter your email: ");
        String Email = scanner.nextLine();

        String sql = "INSERT INTO Users (Username, PhoneNumber, Password, Email) VALUES ('" + Username + "', '" + PhoneNumber + "', '" + Password + "', '" + Email + "')";

        try (   Connection conn = dbConnector.connect();
                Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println(rowsAffected + " row(s) inserted.");
            System.out.println("Account Created!");
        }
        catch (SQLException e) {
            System.out.println("Error inserting player: " + e.getMessage());
        }
    }
    public User login(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your username: ");
        String username = scanner.nextLine();

        System.out.println("Please enter your password: ");
        String password = scanner.nextLine();

        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?"; //Finding the table in Users where "username" and "password" match what the user inputs. The "?" tells the database we will find the value later

        try (   Connection conn = dbConnector.connect();
                PreparedStatement pstm = conn.prepareStatement(sql)) { //PreparedStatement allows the database to pre-compile the query structure, and it knows that the "?" are placeholders.

            pstm.setString(1, username); //Here, with the "setString" method, we tell the database to take the value of the variable "username" and place it in for the first "?" in the query
            pstm.setString(2, password);

            ResultSet rs = pstm.executeQuery();

            if (rs.next()){
                String dbUsername = rs.getString("username");
                String dbPassword = rs.getString("password");
                String email = rs.getString("email");
                int phoneNumber = rs.getInt("phoneNumber");

                System.out.println("Login successful! Welcome, " + username);
                return new User(dbUsername, dbPassword, email, phoneNumber);
            }else {
                System.out.println("Invalid username or password. Please try again.");
                login();
            }
        }
        catch (SQLException e) {
            System.out.println("Error inserting player: " + e.getMessage());
        }
        return null;
    }

    public void addFunds(){
        dbConnector.connect();
    }

    public void displayAccount(){
        System.out.println("ACCOUNT INFORMATION\n");
        MediaClient mediaClient = new MediaClient();
        ArrayList<String> accountOptions = new ArrayList<>();
        accountOptions.add("1. Add funds");
        accountOptions.add("2. Buy membership");
        accountOptions.add("3. Return to main menu");
        accountOptions.add("4. Delete account");

        for (int i = 0; i < accountOptions.size(); i++) {
            System.out.println(accountOptions.get(i));
        }

        int answer = ui.promptNumeric("Please choose a number ");

        switch (answer) {
            case 1:
                addFunds();
                break;
            case 2:

                break;
            case 3:
                mediaClient.displayMenu();
                break;
            case 4:
                deleteAccount();
                break;
            default:
                System.out.println("Invalid choice - please choose a number between 1 and 4");
                displayAccount();
                break;
        }
    }

    public void deleteAccount(){
    }
}