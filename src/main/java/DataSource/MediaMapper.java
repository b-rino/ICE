package DataSource;

import Model.Audiobooks;
import Model.MediaItem;
import Model.Series;
import UI.*;
import Model.*;
import Client.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MediaMapper {
    private DataSource.DBConnector DBConnector = new DBConnector();


    public List<MediaItem> readMediaData(String sqlQuery) {

        String movieSql = "SELECT title, releaseYear, category, rating, NULL AS season, NULL AS episode, 'Movie' AS type FROM Movies";
        String seriesSql = "SELECT title, releaseYear, category, rating, season, episode, 'Series' AS type FROM Series";
        String audioSql = "SELECT title, author, releaseYear, category, rating,'Audiobook' AS type FROM Audiobooks";
        List<MediaItem> mediaList = new ArrayList<>();

        String actualSqlQuery = null;

        switch (sqlQuery) {
            case "movie":
                actualSqlQuery = movieSql;
                break;
            case "series":
                actualSqlQuery = seriesSql;
                break;
            case "audiobook":
                actualSqlQuery = audioSql;
                break;
        }


        try (Connection conn = DBConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(actualSqlQuery)) {
            while (rs.next()) {
                String title = rs.getString("title");
                int releaseYear = rs.getInt("releaseYear");
                String category = rs.getString("category");
                float rating = rs.getFloat("rating");
                String type = rs.getString("type");


                if (type.equals("Movie")) {
                    Movie movie = new Movie(title, releaseYear, category, rating);
                    mediaList.add(movie);
                }
                if (type.equals("Series")) {
                    int season = rs.getInt("season");
                    int episode = rs.getInt("episode");
                    Series series = new Series(title, releaseYear, category, rating, season, episode);
                    mediaList.add(series);
                }
                if (type.equals("Audiobook")) {
                    String author = rs.getString("author");
                    Audiobooks audiobooks = new Audiobooks(title, releaseYear, category, rating, author);
                    mediaList.add(audiobooks);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return mediaList;
    }

    public String getType(String sql) {
        String sqlMovie = "SELECT Type FROM movies";
        String sqlSeries = "SELECT Type FROM series";
        String sqlAudio = "SELECT Type FROM audiobooks";
        String actualSql = null;

        String type = null;
        switch (sql){
            case "movie":
                actualSql = sqlMovie;
                break;
            case "series":
                actualSql = sqlSeries;
                break;
            case "audiobook":
                actualSql = sqlAudio;
                break;
        }

        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(actualSql)) {
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                type = rs.getString("Type");
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return type;

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
