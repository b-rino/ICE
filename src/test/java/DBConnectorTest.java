import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        connection = DriverManager.getConnection("jdbc:sqlite::memory:"); // In-memory database
        setUpDatabase(connection);
    }

    // Helper method to set up the database schema
    private void setUpDatabase(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Create Users table
            String createTableSQL = "CREATE TABLE IF NOT EXISTS Users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT, " +
                    "password TEXT)";
            stmt.execute(createTableSQL);
        }
    }

    /*
    @Test
    void testReadUserData() throws SQLException {
        // Arrange: Insert a test user into the database
        String insertSQL = "INSERT INTO Users (username, password) VALUES ('testUser', 'password123')";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.executeUpdate();
        }

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='Users'");
            if (rs.next()) {
                System.out.println("Table 'Users' exists!");
            } else {
                System.out.println("Table 'Users' does NOT exist!");
            }
        }

        // Step 2: Verify that the inserted data is present in the Users table
        String checkInsertedDataSQL = "SELECT COUNT(*) FROM Users WHERE username = 'testUser'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkInsertedDataSQL)) {
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Number of users with username 'testUser': " + count);
            }
        }

        // Act: Fetch user data
        List<User> users = dbConnector.readUserData();
        // Assert: Check if the data was retrieved correctly
        assertEquals(5, users.size());
        User user = users.get(0);
        assertEquals("testUser", user.getUsername());
        assertEquals("password123", user.getPassword());
    }

    /*
    @Test
    void testSaveUserData() throws SQLException {
        // Arrange: Create a User object to save
        User user = new User("newUser", "newPassword");

        // Act: Save the user data
        dbConnector.saveUserData(user);

        // Assert: Verify that the user was inserted into the database
        String selectSQL = "SELECT username, password FROM Users WHERE username = 'newUser'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            assertTrue(rs.next());
            assertEquals("newUser", rs.getString("username"));
            assertEquals("newPassword", rs.getString("password"));
        }
    }
    */

    @Test
    void readMediaDataForSpecificMovie() throws SQLException {
        // Call the method to read movies
        List<MediaItem> mediaList = dbConnector.readMediaData("movie");
        // System.out.println(mediaList);
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
    void readMediaDataForSpecificSeries() throws SQLException {
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
}

/*
    @Test
    void testSaveMediaData() throws SQLException {
        // Arrange: Create a Movie and Series object to save
        Movie movie = new Movie("NewMovie", 2023, "Comedy", 7);
        Series series = new Series("NewSeries", 2024, "Sci-Fi", 8, 1, 1);

        // Act: Save the movie and series to the database
        dbConnector.saveMediaData(movie);
        dbConnector.saveMediaData(series);

        // Assert: Verify that the movie and series were inserted into the database
        String selectMovieSQL = "SELECT title, releaseYear, category, rating FROM Movies WHERE title = 'NewMovie'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectMovieSQL)) {
            assertTrue(rs.next());
            assertEquals("NewMovie", rs.getString("title"));
            assertEquals(2023, rs.getInt("releaseYear"));
            assertEquals("Comedy", rs.getString("category"));
        }

        String selectSeriesSQL = "SELECT title, releaseYear, category, rating, season, episode FROM Series WHERE title = 'NewSeries'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSeriesSQL)) {
            assertTrue(rs.next());
            assertEquals("NewSeries", rs.getString("title"));
            assertEquals(2024, rs.getInt("releaseYear"));
            assertEquals("Sci-Fi", rs.getString("category"));
            assertEquals(1, rs.getInt("season"));
            assertEquals(1, rs.getInt("episode"));
        }
    }

    @Test
    void testReadUserDataNoUsers() throws SQLException {
        // Act: Try to read users when there are no records
        List<User> users = dbConnector.readUserData();

        // Assert: Check that the list is empty
        assertTrue(users.isEmpty());
    }
}
*/