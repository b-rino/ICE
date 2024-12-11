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
                User user = item;
                if ("diller".equals(user.getUsername())) {
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


    @Test
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
        dbConnector.getUserMembership("Tester123");

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, "Tester123"); // Username
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
        // dbConnector.getUserBalance("Test12345");

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, "Tester1234567"); // Username
            pstmt.setString(2, "test"); // Password
            pstmt.setInt(3, 500); // Balance
            pstmt.setInt(4, 0); // Membership value
            pstmt.executeUpdate();
        }
        dbConnector.getUserBalance("Test1234567");

        // Act: Retrieve membership using getUserMembership
        int actualBalance = dbConnector.getUserBalance("Test1234567");

        // Assert: Verify that user's balance is correct
        assertEquals(500, actualBalance, "The membership value for the user should be 500.");

    }




    // Unfinished test methods below the line. Trying to fix interaction issues with the database
    // -------------------------------------------------------------------------------------------


    /*
    @Test
    void testGetUserBalance() throws SQLException {
        // Arrange: Insert a test user with a specific balance
        User user = new User("testUser", "testUser");
        dbConnector.saveUserData(user);
        String selectSQL = "SELECT balance FROM Users WHERE username = 'testUser'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            assertTrue(rs.next());
            assertEquals("testUser", rs.getString("username"));
            assertEquals(0, rs.getInt("balance"));
        }
    }

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

        @Test
    void testGetUserBalance() throws SQLException {
        // Arrange: Insert a test user
        String insertSQL = "INSERT INTO Users (username, password, balance) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, "testUser");
            pstmt.setString(2, "testPassword");
            pstmt.setInt(3, 0); // Explicitly set balance to 0
            pstmt.executeUpdate();
        }

        // Act: Retrieve balance using getUserBalance
        int actualBalance = dbConnector.getUserBalance("testUser");

        // Assert: Verify the balance is 0
        assertEquals(0, actualBalance, "The balance for the new user should be 0.");
    }
    */



}

