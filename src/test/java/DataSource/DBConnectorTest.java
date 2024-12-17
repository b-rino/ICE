package DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DBConnectorTest {
    private DBConnector dbConnector;
    private Connection connection;
    private UserMapper userMapper;
    private MediaMapper mediaMapper;

    @BeforeEach
        // Create a new instance of DBConnector for each test.
    void setUp() throws SQLException {
        dbConnector = new DBConnector();
        connection = DriverManager.getConnection("jdbc:sqlite:Blogbuster.db");

        // connection = DriverManager.getConnection("jdbc:sqlite::memory:"); // In-memory database
    }
}

