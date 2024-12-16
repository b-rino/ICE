package DataSource;

import Model.MediaItem;
import Model.Series;
import UI.*;
import Model.*;
import Client.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    private DBConnector DBConnector = new DBConnector();

    public List<User> readUserData() {
        String sql = "SELECT username, password FROM Users"; // Needs to be the same as columns.
        List<User> userData = new ArrayList<>();

        try (Connection conn = DBConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                User user = new User(username, password, email);
                userData.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return userData;
    }

    public void saveUserData(User user) {
        String sql = "INSERT INTO Users (username, password) VALUES (?,?)";

        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void deleteUserData(User user) {
        String sql = "DELETE FROM Users WHERE username = ?";

        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateUserBalance(User user, int amount, boolean isWithdrawal) {
        String sql = "UPDATE Users SET balance = ? WHERE username = ?";


        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int currentBalance = getUserBalance(user.getUsername());
            int newBalance = isWithdrawal ? currentBalance - amount : currentBalance + amount;
            pstmt.setInt(1, newBalance);
            pstmt.setString(2, user.getUsername());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getUserBalance(String username) {
        String sql = "SELECT balance FROM users WHERE username = ?";
        int balance = 0;
        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                balance = rs.getInt("balance");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return balance;
    }

    public void updateUserMembership(User user, int membership) {
        String sql = "UPDATE Users SET membership = ? WHERE username = ?";

        try (Connection conn = DBConnector.connect();
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

        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                membership = rs.getInt("membership");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return membership;
    }


    public void updateUserPunchcard(User user, int punchcard) {
        String sql = "UPDATE Users SET punchcard = ? WHERE username = ?";

        try (Connection conn = DBConnector.connect();
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
        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                punchcardBalance = rs.getInt("punchcard");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return punchcardBalance;
    }

    public int getUserID(String username) {
        String sql = "SELECT UserID FROM users WHERE username = ?";

        int ID = 0;
        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ID = rs.getInt("UserID");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ID;
    }

    public List<MediaItem> getPersonalList(User user) {
        List<MediaItem> personalMediaList = new ArrayList<>();
        String sql = "SELECT p.MovieID, p.SeriesID, p.AudioID, " +
                "m.title AS movieTitle, m.category AS movieCategory, m.rating AS movieRating, m.releaseYear AS movieReleaseYear, " +
                "s.title AS seriesTitle, s.category AS seriesCategory, s.rating AS seriesRating, s.releaseYear AS seriesReleaseYear, s.season, s.episode, " +
                "a.Title AS audioTitle,a.ReleaseYear AS audioReleaseYear,a.category AS audioCategory, a.Rating AS audioRating, a.Author AS audioAuthor " +
                "FROM PersonalMediaLists p " + "LEFT JOIN Movies m ON p.MovieID = m.movieId " +
                "LEFT JOIN Series s ON p.SeriesID = s.seriesId " +
                "LEFT JOIN Audiobooks a ON p.AudioID = a.audioId " +
                "WHERE p.UserID = ?";

        int userID = getUserID(user.getUsername());

        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int movieId = rs.getInt("MovieID");
                int seriesId = rs.getInt("SeriesID");
                int audioId = rs.getInt("audioID");
                if (movieId != 0) {
                    String movieTitle = rs.getString("movieTitle");
                    int releaseYear = rs.getInt("movieReleaseYear");
                    String category = rs.getString("movieCategory");
                    int rating = rs.getInt("movieRating");
                    Movie movie = new Movie(movieTitle, releaseYear, category, rating);
                    movie.setId(movieId);
                    personalMediaList.add(movie);
                }
                if (seriesId != 0) {
                    String seriesTitle = rs.getString("seriesTitle");
                    int releaseYear = rs.getInt("seriesReleaseYear");
                    String category = rs.getString("seriesCategory");
                    int rating = rs.getInt("seriesRating");
                    int season = rs.getInt("season");
                    int episode = rs.getInt("episode");
                    Series series = new Series(seriesTitle, releaseYear, category, rating, season, episode);
                    series.setId(seriesId);
                    personalMediaList.add(series);
                }
                if (audioId != 0) {
                    String audioTitle = rs.getString("audioTitle");
                    int releaseYear = rs.getInt("audioReleaseYear");
                    String category = rs.getString("audioCategory");
                    int rating = rs.getInt("audioRating");
                    String author = rs.getString("audioAuthor");
                    Audiobooks audiobook = new Audiobooks(audioTitle, releaseYear, category, rating, author);
                    audiobook.setId(audioId);
                    personalMediaList.add(audiobook);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching personal media list: " + e.getMessage());
            e.printStackTrace();
        }
        return personalMediaList;
    }

    public void addToPersonalList(User user, int ID, String sql) {
        String MovieSql = "INSERT INTO PersonalMediaLists (UserID, MovieID, added_timestamp) VALUES (?, ?, ?)";
        String SeriesSql = "INSERT INTO PersonalMediaLists (UserID, SeriesID, added_timestamp) VALUES (?, ?, ?)";
        String AudioSql = "INSERT INTO PersonalMediaLists (UserID, audioID, added_timestamp) VALUES (?, ?, ?)";
        String actualSql = null;
        long currentTime = System.currentTimeMillis() / 1000L;

        if (sql.equalsIgnoreCase("movie")) {
            actualSql = MovieSql;
        }
        if (sql.equalsIgnoreCase("series")) {
            actualSql = SeriesSql;
        }
        if (sql.equalsIgnoreCase("audiobook")) {
            actualSql = AudioSql;
        }

        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(actualSql)) {
            pstmt.setInt(1, getUserID(user.getUsername()));
            pstmt.setInt(2, ID);
            pstmt.setLong(3, currentTime);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

