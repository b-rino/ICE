import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.print.attribute.standard.Media;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DBConnectorTest {
    private DBConnector dbConnector;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Create a new instance of DBConnector and an in-memory SQLite database
        dbConnector = new DBConnector();
        connection = DriverManager.getConnection("jdbc:sqlite:Blogbuster.db");
        //connection = DriverManager.getConnection("jdbc:sqlite::memory:"); // In-memory database
    }

    @Test
    void testReadUserDataFindDilleren() throws SQLException {
        // Arrange: Insert a test user into the database
        List<User> userList = dbConnector.readUserData();
        // System.out.println(userList); // Sout to see if list gets filled
        assertNotNull(userList);
        assertTrue(userList.size() > 0); // Contains at least 1 movie
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
        // Call the method to read movies
        List<MediaItem> mediaList = dbConnector.readMediaData("movie");
        // System.out.println(mediaList); // Check if list gets filled
        assertNotNull(mediaList); // Ensures that the list isn't null
        assertTrue(mediaList.size() > 0); // Contains at least 1 movie
        // Search for "The Shawshank Redemption"
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
        // Assert if movie found
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

    @Test
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

    @Test
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



    // Unfinished test methods below the line. Trying to fix interaction issues with the database
    // -------------------------------------------------------------------------------------------

    @Test
    void testGetUserMembership() throws SQLException {
        // Arrange: Insert a test user with membership value
        User user = new User("test123", "test13");
        // String insertSQL = "INSERT INTO Users (username, password, balance, membership) VALUES (?, ?, ?, ?)";
        dbConnector.saveUserData(user);
        // String selectSQL = "SELECT username, password FROM Users WHERE username = 'test123'";
        // String insertSQL = "SELECT username, password FROM Users WHERE username = 'test123'";
        String insertSQL = "UPDATE Users SET membership = ? WHERE username = ?";
        /*
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            assertTrue(rs.next());
            assertEquals("test123", rs.getString("username"));
            assertEquals("test123", rs.getString("password"));
         */
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, "testUser");
            pstmt.setString(2, "testPassword");
            pstmt.setInt(3, 1000);  // Initial balance (though we're testing membership)
            pstmt.setInt(4, 1);     // Membership value
            pstmt.executeUpdate();
        }
        // Act: Retrieve membership using getUserMembership
        int actualMembership = dbConnector.getUserMembership("testUser");

        // Assert: Verify the membership value is correct
        assertEquals(1, actualMembership, "The membership value for the user should be 1.");
    }



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

