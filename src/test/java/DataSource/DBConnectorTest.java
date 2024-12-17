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

    @BeforeEach
        // Create a new instance of DBConnector for each test.
    void setUp() throws SQLException {
        dbConnector = new DBConnector();
        connection = DriverManager.getConnection("jdbc:sqlite:Blogbuster.db");
        // connection = DriverManager.getConnection("jdbc:sqlite::memory:"); // In-memory database
    }

    @Test
    void testConnectionIsValid() throws SQLException {
        try {
            // Act: Create connection from the connector
            dbConnector.connect();

            // Assert: Check if the connection is valid
            assertNotNull(connection, "Connection should not be null");
            assertTrue(connection.isValid(2), "Connection should be valid");

            // Close connection after testing.
            connection.close();
        } catch (SQLException e) {
            fail("SQLException occurred: " + e.getMessage());
        }
    }
}

