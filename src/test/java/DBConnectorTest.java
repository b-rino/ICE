import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.print.attribute.standard.Media;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DBConnectorTest {
    private DBConnector dbConnector;
    private Connection connection;

    @BeforeEach // Create a new instance of DBConnector for each test.
    void setUp() throws SQLException {
        dbConnector = new DBConnector();
        connection = DriverManager.getConnection("jdbc:sqlite:Blogbuster.db");

        // connection = DriverManager.getConnection("jdbc:sqlite::memory:"); // In-memory database
    }

    // TODO: Clean up database for testusers, then call every test's user "Tester"
    @AfterEach
    void deleteTestUser() throws SQLException {
        String deleteSQL = "DELETE FROM Users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, "Tester"); // The test username to delete
            pstmt.executeUpdate();
        }
    }

    @Test
    void testReadUserDataFindDilleren() throws SQLException {
        // Arrange: Insert a test user into the database
        List<User> userList = dbConnector.readUserData();
        // System.out.println(userList); // Sout to see if list gets filled

        assertNotNull(userList);
        assertTrue(userList.size() > 0); // Contains at least 1 movie

        // Act: Searches for diller
        boolean found = false;
        for (User item : userList) {
            if (item instanceof User) {
                if ("diller".equals(item.getUsername())) {
                    // && "dalgalasg".equals(user.getEmail())) { // Do we want to use an email column?
                    found = true;
                    break;
                }
            }
        }
        // Assert: Checks if user is found
        System.out.println("User found: " + found);
        assertTrue(found);
    }


    @Test // UNIQUE constraint but works. Can just change to Tester but won't show in db because of deleteTestUser
    void testSaveUserDataAddDallerDiller() throws SQLException {
        // Arrange: Create a User object to save
        User user = new User("daller", "diller");

        // Act: Save the user data
        dbConnector.saveUserData(user);

        // Assert: Verify that the user was inserted into the database
        String selectSQL = "SELECT username, password FROM Users WHERE username = 'daller'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            assertTrue(rs.next());
            assertEquals("daller", rs.getString("username"));
            assertEquals("diller", rs.getString("password"));
        }
    }

    @Test
    void readMediaDataForSpecificMovie() throws SQLException {
        // Arrange: Read db and arrange in list.
        List<MediaItem> mediaList = dbConnector.readMediaData("movie");
        // System.out.println(mediaList); // Check if list gets filled
        assertNotNull(mediaList); // Ensures that the list isn't null
        assertTrue(mediaList.size() > 0); // Contains at least 1 movie

        // Act: Search for "The Shawshank Redemption"
        boolean found = false;
        for (MediaItem item : mediaList) {
            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                // Assuming these values for the movie we are testing
                if ("The Shawshank Redemption".equals(movie.getTitle()) &&
                        movie.getReleaseYear() == 1994 &&
                        "Drama".equals(movie.getCategory())) { // Skipping rating
                    found = true;
                    break;
                }
            }
        }
        // Assert: Checks if movie is found
        System.out.println("Movie found: " + found); // Passed test doesn't showcase anything. Just to ensure correct test
        assertTrue(found, "The Shawshank Redemption should be in the database."); // Error message if not true
    }

    @Test
    void testReadMediaDataForSpecificSeries() throws SQLException {
        // Call the method to read movies
        List<MediaItem> mediaList = dbConnector.readMediaData("series");
        // System.out.println(mediaList); // Sout for checking if list is filled
        assertNotNull(mediaList); // Ensures that the list isn't null
        assertTrue(mediaList.size() > 0); // Contains at least 1 movie
        // Search for "The Shawshank Redemption"
        boolean found = false;
        for (MediaItem item : mediaList) {
            if (item instanceof Series) {
                Series series = (Series) item;
                // Assuming these values for the movie we are testing
                if ("The Sopranos".equals(series.getTitle()) && series.getEpisode() == 86 && series.getSeason() == 6) { // Skipping rating
                    found = true;
                    break;
                }
            }
        }
        // Assert if series found
        System.out.println("Series found: " + found); // Passed test doesn't showcase anything. Just to ensure correct test
        assertTrue(found, "The Sopranos should be in the database.");
    }

    /*
    @Test // Unsure if method should remain
    void testSaveMovieData() throws SQLException {
        // Arrange: Create a Movie and a Series object to save
        Movie movie = new Movie("TestMovie", 2024, "Drama", 9.5F);

        // Act: Save the movie to the database
        dbConnector.saveMediaData(movie);

        // Assert: Verify that the movie and series were inserted into the database
        String selectMovieSQL = "SELECT title, releaseYear, category, rating FROM Movies WHERE title = 'TestMovie'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectMovieSQL)) {
            assertTrue(rs.next());
            assertEquals("TestMovie", rs.getString("title"));
            assertEquals(2024, rs.getInt("releaseYear"));
            assertEquals("Drama", rs.getString("category"));
        }
    }

    @Test // Unsure if method should remain
    void testSaveSeriesData() throws SQLException {
        // Arrange: Create a Movie and a Series object to save
        Series series = new Series("TestSeries", 2024, "Comedy", 9.2F, 2, 11);

        // Act: Save the movie to the database
        dbConnector.saveMediaData(series);

        // Assert: Verify that the movie and series were inserted into the database
        String selectSeriesSQL = "SELECT title, releaseYear, category, rating, season, episode FROM Series WHERE title = 'TestSeries'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSeriesSQL)) {
            assertTrue(rs.next());
            assertEquals("TestSeries", rs.getString("title"));
            assertEquals(2024, rs.getInt("releaseYear"));
            assertEquals("Comedy", rs.getString("category"));
            assertEquals(9.2F, rs.getFloat("rating"), 0);
            assertEquals(2, rs.getInt("season"));
            assertEquals(11, rs.getInt("episode"));
        }
    }
     */

    @Test
    void testGetUserWithActiveMembership() throws SQLException {
        // Arrange: Insert a test user with an active membership
        String insertSQL = "INSERT INTO Users (username, password, balance, membership) VALUES (?, ?, ?, ?)";
        dbConnector.getUserMembership("Tester");

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, "Tester"); // Username
            pstmt.setString(2, "test"); // Password
            pstmt.setInt(3, 1000);  // Balance
            pstmt.setInt(4, 1); // Membership value
            pstmt.executeUpdate();
        }
        // Act: Retrieve membership using getUserMembership
        int actualMembership = dbConnector.getUserMembership("Tester");

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
        int actualBalance = dbConnector.getUserBalance("Tester");

        // Assert: Verify that user's balance is correct
        System.out.println("Tester's Balance: " + actualBalance); // Sout to show balance
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
        int actualPunchcardBalance = dbConnector.getUserPunchcardBalance("Tester");

        // Arrange: Verify that tester's punchcard balance is 5.
        System.out.println("Tester's Balance: " + actualPunchcardBalance);
        assertEquals(5, actualPunchcardBalance, "The punchcard for the user should be 5.");
    }

    @Test // TODO: Currently setting Tester's ID as 1. Do we care about this after db cleanup?
    void getUserIdOfTesterWithId1() throws SQLException {
        // Arrange:
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
        int actualUserID = dbConnector.getUserID("Tester");
        System.out.println("Tester's ID " + actualUserID);

        // Arrange: Verify that tester's userID is 1.
        assertEquals(1, actualUserID, "The user id for the user should be 23 (Check db...)");
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

}

