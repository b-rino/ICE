import javax.print.attribute.standard.Media;
import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class DBConnector {

    private static final String URL = "jdbc:sqlite:Blogbuster.db";

    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public List<User> readUserData() {
        String sql = "SELECT username, password FROM Users";
        List<User> userData = new ArrayList<>();

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                User user = new User(username, password);
                userData.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return userData;
    }

    public void saveUserData(User user) {
        String sql = "INSERT INTO Users (username, password) VALUES (?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public List<Media> readMediaData() {
        String movieSql = "SELECT movie_id AS ID, title, releaseYear, category, rating, 'movie' AS type FROM Movies";
        String seriesSql = "SELECT series_id AS id, title, releaseYear, category, rating, 'series' AS type FROM Series";
        String sql = movieSql + " UNION ALL " + seriesSql;
        List<Media> mediaList = new ArrayList<>();

        try (Connection conn = this.connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)){

            while (rs.next()) {
                String title = rs.getString("title");
                int releaseYear = rs.getInt("releaseYear");
                String category = rs.getString("category");
                float rating = rs.getFloat("rating");
                String type = rs.getString("type");

                if (type.equals("movie")) {
                    Movie movie = new Movie(title, releaseYear, category, rating);
                    mediaList.add(movie);
                } else if (type.equals("series")) {
                    int season = rs.getInt("season");
                    int episode = rs.getInt("episode");
                    Series series = new Series(title, releaseYear, category, rating, season, episode);
                    mediaList.add(series);

                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
            return mediaList;
        }

    public void saveMediaData(Media media) {
        String movieSql = "INSERT INTO Movies (title, releaseYear, category, rating, type) VALUES (?,?,?,?, 'movie')";
        String seriesSql = "INSERT INTO Series (title, releaseYear, category, rating, season, episode, type) VALUES (?, ?, ?, ?, ?, ?, 'series')";

        try (Connection conn = this.connect()) {
            if (media instanceof Movie) {
                try (PreparedStatement pstmt = conn.prepareStatement(movieSql)) {
                    pstmt.setString(1, media.getTitle());
                    pstmt.setInt(2, media.getReleaseYear());
                    pstmt.setString(3, media.getCategory());
                    pstmt.setFloat(4, media.getRating());
                    pstmt.executeUpdate();
                }
            } else if (media instanceof Series) {
                try (PreparedStatement pstmt = conn.prepareStatement(seriesSql)) {
                    Series series = (Series) media;
                    pstmt.setString(1, media.getTitle());
                    pstmt.setInt(2, media.getReleaseYear());
                    pstmt.setString(3, media.getCategory());
                    pstmt.setFloat(4, media.getRating());
                    pstmt.setInt(5, series.getSeasons());
                    pstmt.setInt(6, series.getEpisodes());
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
