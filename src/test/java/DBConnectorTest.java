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
        connection = DriverManager.getConnection("jdbc:sqlite:Blogbuster.db"); // In-memory database
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

    @Test
    void updateUserDallerBalance() throws SQLException {
        String selectSQL = "SELECT username, password FROM Users WHERE username = 'daller'";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int currentBalance = getUserBalance(user.getUsername());
            int newBalance = isWithdrawal ? user.getBalance() - amount : user.getBalance() + amount;
            pstmt.setInt(1, getUserBalance(user.getUsername()) - amount);
            pstmt.setString(2, user.getUsername());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }




}

