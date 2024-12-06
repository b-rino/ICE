import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;


public class UserClient {

    DBConnector dbConnector = new DBConnector();

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

    public void createUser() {
        Scanner scanner = new Scanner(System.in);
        // TODO: TextUI implemented and used here instead of scanner
        System.out.println("Please enter your username: ");
        String Username = scanner.nextLine();

        System.out.println("Please enter your phonenumber: ");
        int PhoneNumber = scanner.nextInt();
        scanner.nextLine(); // Clear the newline character from the buffer


        System.out.println("Please enter your password: ");
        String Password = scanner.nextLine();

        System.out.println("Please enter your email: ");
        String Email = scanner.nextLine();

        String sql = "INSERT INTO Users (Username, PhoneNumber, Password, Email) VALUES ('" + Username + "', '" + PhoneNumber + "', '" + Password + "', '" + Email + "')";

        try (   Connection conn = dbConnector.connect();
                Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println(rowsAffected + " row(s) inserted.");
        }
        catch (SQLException e) {
            System.out.println("Error inserting player: " + e.getMessage());
        }
    }
}
