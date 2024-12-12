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
        String sql = "SELECT username, password FROM Users"; // Needs to be the same as columns.
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

    public void deleteUserData(User user) {
        String sql = "DELETE FROM Users WHERE username = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

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


            try (Connection conn = this.connect();
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


    public void updateUserBalance(User user, int amount, boolean isWithdrawal) {
        String sql = "UPDATE Users SET balance = ? WHERE username = ?";


        try (Connection conn = this.connect();
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

    public void updateUserMembership(User user, int membership) {
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

    public void updateUserPunchcard(User user, int punchcard) {
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

    public int getUserID(String username) {
        String sql = "SELECT UserID FROM users WHERE username = ?";

        int ID = 0;
        try(Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                ID = rs.getInt("UserID");
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return ID;
    }


    public void addToPersonalList(User user, int ID, String sql) {
        String MovieSql = "INSERT INTO PersonalMediaLists (UserID, MovieID, added_timestamp) VALUES (?, ?, ?)";
        String SeriesSql = "INSERT INTO PersonalMediaLists (UserID, SeriesID, added_timestamp) VALUES (?, ?, ?)";
        String AudioSql = "INSERT INTO PersonalMediaLists (UserID, audioID, added_timestamp) VALUES (?, ?, ?)";
        String actualSql = null;
        long currentTime = System.currentTimeMillis() / 1000L;

        if (sql.equalsIgnoreCase("movie")){
            actualSql = MovieSql;
        }
        if (sql.equalsIgnoreCase("series")){
            actualSql = SeriesSql;
        }
        if (sql.equalsIgnoreCase("audiobook")){
            actualSql = AudioSql;
        }

        try(Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(actualSql)){
            pstmt.setInt(1, getUserID(user.getUsername()));
            pstmt.setInt(2, ID);
            pstmt.setLong(3, currentTime);
            pstmt.executeUpdate();

        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
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

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                int movieId = rs.getInt("MovieID");
                int seriesId = rs.getInt("SeriesID");
                int audioId = rs.getInt("audioID");
                if (movieId != 0){
                    String movieTitle = rs.getString("movieTitle");
                    int releaseYear = rs.getInt("movieReleaseYear");
                    String category = rs.getString("movieCategory");
                    int rating = rs.getInt("movieRating");
                    Movie movie = new Movie(movieTitle,releaseYear,category,rating);
                    movie.setId(movieId);
                    personalMediaList.add(movie);
                }
                if (seriesId != 0){
                    String seriesTitle = rs.getString("seriesTitle");
                    int releaseYear = rs.getInt("seriesReleaseYear");
                    String category = rs.getString("seriesCategory");
                    int rating = rs.getInt("seriesRating");
                    int season = rs.getInt("season");
                    int episode = rs.getInt("episode");
                    Series series = new Series(seriesTitle,releaseYear,category,rating,season,episode);
                    series.setId(seriesId);
                    personalMediaList.add(series);
                }
                if (audioId != 0) {
                    String audioTitle = rs.getString("audioTitle");
                    int releaseYear = rs.getInt("audioReleaseYear");
                    String category = rs.getString("audioCategory");
                    int rating = rs.getInt("audioRating");
                    String author = rs.getString("audioAuthor");
                    Audiobooks audiobook = new Audiobooks(audioTitle,releaseYear,category,rating,author);
                    audiobook.setId(audioId);
                    personalMediaList.add(audiobook);
                }
            }
        }catch (SQLException e){
            System.err.println("Error fetching personal media list: " + e.getMessage());
            e.printStackTrace();
        }
        return personalMediaList;
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

        try (Connection conn = this.connect();
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


        //TODO: Disse 3 metoder bruges ikke indtil videre og bør slettes hvis ikke de kommer i brug!
        /*public int getMovieID(String title) {
        String sql = "SELECT MovieID FROM Movies WHERE username = ?";

        int ID = 0;

        try(Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                ID = rs.getInt("MovieID");
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return ID;
    }

    public int getSeriesID(String title) {
        String sql = "SELECT SeriesID FROM Series WHERE username = ?";

        int ID = 0;

        try(Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                ID = rs.getInt("SeriesID");
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return ID;
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
    }*/
}
