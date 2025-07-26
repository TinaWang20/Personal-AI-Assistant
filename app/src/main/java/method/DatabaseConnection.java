package method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres?prepareThreshold=0";
    private static final String USER = "postgres.xlytkpfpdocjgcsymcwh";
    private static final String PASSWORD = "o5rNnQbnBV0PBgeb";

    // Establishes and returns a connection to the database
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            throw e;
        }
    }
}