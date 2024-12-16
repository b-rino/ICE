package DataSource;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

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
