package DataSource;

import Model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private Connection connection;
    private UserMapper userMapper;

    @BeforeEach // Connection to db before every test
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:Blogbuster.db");
        userMapper = new UserMapper();
        // connection = DriverManager.getConnection("jdbc:sqlite::memory:"); // In-memory database
    }

    @AfterEach
    void deleteTestUser() throws SQLException {
        String deleteSQL = "DELETE FROM Users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, "Tester"); // The test username to delete
            pstmt.executeUpdate();
        }
    }

    @Test // Find specific user that doesn't get deleted like Tester does.
    void testReadUserDataFindBlogbuster() throws SQLException {
        // Arrange: Read current Users and put into list
        List<User> userList = userMapper.readUserData();

        // Act: Searches list for blogbuster
        boolean found = false;
        for (User item : userList) {
            if (item instanceof User) {
                if ("blogbuster".equals(item.getUsername())) {
                    found = true;
                    break;
                }
            }
        }
        // Assert: Checks if user is found
        // System.out.println("User found: " + found);
        assertTrue(found);
    }

    @Test
    void testReadUserDataFindTester() throws SQLException {
         // Arrange: Creates Tester and readsUserData to put in list
        String insertSQL = "INSERT INTO Users VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setInt(1,1); // TEST ID NUMBER
            pstmt.setString(2, "Tester"); // username
            pstmt.setInt(3, 69696969); // Phonenumber
            pstmt.setString(4, "Test"); // Password
            pstmt.setString(5, "Tester@gmail.com");
            pstmt.setInt(6, 500); // Balance
            pstmt.setInt(7, 1); // Membership
            pstmt.setInt(8, 10); // Punchcard
            pstmt.executeUpdate();
        }
        List<User> userList = userMapper.readUserData();

        // Act: Searches list for Tester
        boolean found = false;
        for (User item : userList) {
            if (item instanceof User) {
                if ("Tester".equals(item.getUsername())) {
                    found = true;
                    break;
                }
            }
        }
        // Assert: Checks if user found
        // System.out.println("User found: " + found);
        assertTrue(found);
    }




    @Test // Using Tester to avoid UNIQUE constraint. Tester gets deleted after Test and won't show in db.
    void testSaveUserDataAddTester() throws SQLException {
        // Arrange: Create a User object to save
        User user = new User("Tester", "Tester", "Tester@gmail.com");

        // Act: Save the user data
        userMapper.saveUserData(user);

        // Assert: Verify that the user was inserted into the database
        String selectSQL = "SELECT username, password FROM Users WHERE username = 'Tester'";
        try (Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(selectSQL)) {
            assertTrue(rs.next());
            assertEquals("Tester", rs.getString("username"));
            assertEquals("Tester", rs.getString("password"));
        }
    }

    @Test
    void testGetUserWithActiveMembership() throws SQLException {
        // Arrange: Insert a test user with an active membership
        String insertSQL = "INSERT INTO Users (username, password, balance, membership) VALUES (?, ?, ?, ?)";
        userMapper.getUserMembership("Tester");

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, "Tester"); // Username
            pstmt.setString(2, "test"); // Password
            pstmt.setInt(3, 1000);  // Balance
            pstmt.setInt(4, 1); // Membership value
            pstmt.executeUpdate();
        }
        // Act: Retrieve membership using getUserMembership
        int actualMembership = userMapper.getUserMembership("Tester");

        // Assert: Verify the membership value is correct
        assertEquals(1, actualMembership, "The membership value for the user should be 1.");
    }

    @Test
    void testGetUserBalanceFromUserWithFunds() throws SQLException {
        // Arrange: Insert a test user with funds on his account
        String insertSQL = "INSERT INTO Users (username, password, balance, membership) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, "Tester"); // Username
            pstmt.setString(2, "test"); // Password
            pstmt.setInt(3, 500); // Balance
            pstmt.setInt(4, 0); // Membership value
            pstmt.executeUpdate();
        }

        // Act: Retrieve membership using getUserMembership
        int actualBalance = userMapper.getUserBalance("Tester");

        // Assert: Verify that user's balance is correct
        // System.out.println("Tester's Balance: " + actualBalance);
        assertEquals(500, actualBalance, "The membership value for the user should be 500.");
    }

    @Test
    void testGetUserPunchcardBalanceWith5() throws SQLException {
        // Arrange: Insert a test user with a punchcard balance of 5
        String insertSQL = "INSERT INTO Users (username, password, balance, membership, punchcard) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, "Tester"); // Username
            pstmt.setString(2, "test"); // Password
            pstmt.setInt(3, 500); // Balance
            pstmt.setInt(4, 1); // Membership. 1 = active
            pstmt.setInt(5, 5); // Punchcard balance
            pstmt.executeUpdate();
        }

        // Act: Retrieve punchcard balance with method
        int actualPunchcardBalance = userMapper.getUserPunchcardBalance("Tester");

        // Arrange: Verify that tester's punchcard balance is 5.
        // System.out.println("Tester's Balance: " + actualPunchcardBalance);
        assertEquals(5, actualPunchcardBalance, "The punchcard for the user should be 5.");
    }

    @Test // TODO: Currently setting Tester's ID as 1. Do we care about this after db cleanup?
    void getUserIdOfTesterWithId1() throws SQLException {
        // Arrange: Create Test user with ID 1.
        String insertSQL = "INSERT INTO Users VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setInt(1,1); // TEST ID NUMBER
            pstmt.setString(2, "Tester"); // username
            pstmt.setInt(3, 69696969); // Phonenumber
            pstmt.setString(4, "Test"); // Password
            pstmt.setString(5, "Tester@gmail.com");
            pstmt.setInt(6, 500); // Balance
            pstmt.setInt(7, 1); // Membership
            pstmt.setInt(8, 10); // Punchcard
            pstmt.executeUpdate();
        }

        // Act: Retrieve userID with method.
        int actualUserID = userMapper.getUserID("Tester");

        // Arrange  : Verify that tester's userID is 1.
        // System.out.println("Tester's ID " + actualUserID);
        assertEquals(1, actualUserID, "The user ID should of Tester should be 1. ");
    }
}

// Unfinished test methods below the line.
// -------------------------------------------------------------------------------------------

    /*
    @Test
    void testUpdateUserBalanceWithdraw() throws SQLException {

        // Arrange: Adding setting up a testUser and adding it to database
        String username = "testUser";
        int initialBalance = 1000;
        int amount = 200;
        boolean withdrawal = true;

        User user = new user();

        user.setUsername("testUser");
        user.setBalance("testUser");
        int amount = 200;
        boolean isWithdrawal = true;
        String selectSQL = "SELECT username, password FROM Users WHERE username = 'daller'";
        dbConnector.updateUserBalance();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Users (username, balance) VALUES (?, ?) ON DUPLICATE KEY UPDATE balance = ?")) {
            pstmt.setString(1, username);
            pstmt.setInt(2, initialBalance);
            pstmt.setInt(3, initialBalance);
            pstmt.executeUpdate();
        }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    */
