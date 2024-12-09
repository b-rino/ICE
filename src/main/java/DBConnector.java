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

    public Connection connect() {
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
                String email = rs.getString("email");
                int phoneNumber = rs.getInt("phoneNumber");
                User user = new User(username, password, email, phoneNumber);
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

    public List<MediaItem> readMediaData() {
        String movieSql = "SELECT title, releaseYear, category, rating, NULL AS season, NULL AS episode, 'movie' AS type FROM Movies";
        String seriesSql = "SELECT title, releaseYear, category, rating, season, episode, 'series' AS type FROM Series";
        String sql = movieSql + " UNION ALL " + seriesSql;
        List<MediaItem> mediaList = new ArrayList<>();

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String title = rs.getString("title");
                int releaseYear = rs.getInt("releaseYear");
                String category = rs.getString("category");
                float rating = rs.getFloat("rating");
                String type = rs.getString("type");

                if (type.equals("movie")) {
                    Movie movie = new Movie(title, releaseYear, category, rating);
                    mediaList.add(movie);
                }
                if (type.equals("series")) {
                    int season = rs.getInt("season");
                    int episode = rs.getInt("episode");
                    Series series = new Series(title, releaseYear, category, rating, season, episode);
                    mediaList.add(series);

                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return mediaList;
    }

    public void saveMediaData(MediaItem media) {
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
                    pstmt.setInt(5, series.getSeason());
                    pstmt.setInt(6, series.getEpisode());
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateUserBalance(User user, int amount, boolean isWithdrawal) {
        String sql = "UPDATE Users SET balance = ? WHERE username = ?";


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

    public int getUserBalance(String username) {
        String sql = "SELECT balance FROM users WHERE username = ?";
        int balance = 0;
        try(Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                balance = rs.getInt("balance");
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return balance;
    }

    public void updateUserMembership(User user, String username, int membership) {
        String sql = "UPDATE Users SET membership = ? WHERE username = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, membership);
            pstmt.setString(2, user.getUsername());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getUserMembership(String username) {
        String sql = "SELECT membership FROM users WHERE username = ?";
        int membership = 0;

        try(Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                membership = rs.getInt("membership");
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return membership;
    }

    public void updateUserPunchcard(User user, String username, int punchcard) {
        String sql = "UPDATE Users SET punchcard = ? WHERE username = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, punchcard);
            pstmt.setString(2, user.getUsername());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getUserPunchcardBalance(String username) {

        String sql = "SELECT Punchcard FROM users WHERE username = ?";

        int punchcardBalance = 0;
        try(Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                punchcardBalance = rs.getInt("punchcard");
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return punchcardBalance;
    }

}
