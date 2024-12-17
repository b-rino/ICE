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

        String movieSql = "SELECT MovieId AS id, title, releaseYear, category, rating, NULL AS season, NULL AS episode, 'Movie' AS type FROM Movies";
        String seriesSql = "SELECT SeriesId AS id, title, releaseYear, category, rating, season, episode, 'Series' AS type FROM Series";
        String audioSql = "SELECT AudioId as id, title, author, releaseYear, category, rating,'Audiobook' AS type FROM Audiobooks";
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
                int id = rs.getInt("id");
                String title = rs.getString("title");
                int releaseYear = rs.getInt("releaseYear");
                String category = rs.getString("category");
                float rating = rs.getFloat("rating");
                String type = rs.getString("type");


                if (type.equals("Movie")) {
                    Movie movie = new Movie(title, releaseYear, category, rating);
                    movie.setId(id);
                    mediaList.add(movie);
                }
                if (type.equals("Series")) {
                    int season = rs.getInt("season");
                    int episode = rs.getInt("episode");
                    Series series = new Series(title, releaseYear, category, rating, season, episode);
                    series.setId(id);
                    mediaList.add(series);
                }
                if (type.equals("Audiobook")) {
                    String author = rs.getString("author");
                    Audiobooks audiobooks = new Audiobooks(title, releaseYear, category, rating, author);
                    audiobooks.setId(id);
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

}
