package DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import Model.*;
import Client.*;
import DataSource.*;

class MediaMapperTest {
    private Connection connection;
    private DBConnector dbConnector;
    private MediaMapper mediaMapper;

    @BeforeEach
        // Create a new instance of DBConnector for each test.
    void setUp() throws SQLException {
        dbConnector = new DBConnector();
        connection = DriverManager.getConnection("jdbc:sqlite:Blogbuster.db");
        // Initialize MediaMapper with the required dependencies
        mediaMapper = new MediaMapper();

        // connection = DriverManager.getConnection("jdbc:sqlite::memory:"); // In-memory database
    }

    @Test
    void readMediaDataForSpecificMovie() throws SQLException {
        // Arrange: Read db and arrange in list.
        List<MediaItem> mediaList = mediaMapper.readMediaData("movie");
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
        // System.out.println("Movie found: " + found); // Passed test doesn't showcase anything. Just to ensure correct test
        assertTrue(found, "The Shawshank Redemption should be in the database."); // Error message if not true
    }

    @Test
    void testReadMediaDataForSpecificSeries() throws SQLException {
        // Call the method to read movies
        List<MediaItem> mediaList = mediaMapper.readMediaData("series");
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
        // System.out.println("Series found: " + found); // Passed test doesn't showcase anything. Just to ensure correct test
        assertTrue(found, "The Sopranos should be in the database.");
    }


    @Test
    void testGetTypeForMovie() {
        String type = mediaMapper.getType("movie");
        // System.out.println(type);
        assertEquals("Movie", type, "The type for 'movie' should be 'Movie'");
    }

    @Test
    void testGetTypeForSeries() {
        String type = mediaMapper.getType("series");
        // System.out.println(type);
        assertEquals("Series", type, "The type for 'series' should be 'Series'");
    }

    @Test
    void testGetTypeForAudioBook() {
        String type = mediaMapper.getType("audiobook");
        // System.out.println(type);
        assertEquals("Audiobook", type, "The type for 'audioBook' should be 'AudioBook'");
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

}