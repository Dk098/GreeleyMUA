package GreeleyMUA.backend.response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author michaelmaitland
 */
public class DatabaseManager {

    private String url;
    private static String FILENAME = "greeleysmtp.db";
    private static DatabaseManager databaseManager = null;

    public DatabaseManager() {
        this.url = "jdbc:sqlite:" + System.getProperty("user.home") + "/Desktop/" + FILENAME;
    }

    public static void setFilename(String filename) {
        FILENAME = filename;
    }

    //must call setFilename first
    public static DatabaseManager getInstance() {
        if (databaseManager == null)
            databaseManager = new DatabaseManager();
        return databaseManager;
    }

    protected Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public boolean login(String username, String md5Password) {
        return false;
    }

    public List<String> getMessages(String username, String md5Password) {
        if (!login(username, md5Password)) return null; //bad credentials

        //need to parse out brackets from list toString
        String query = "SELECT data FROM messages WHERE rcptTo LIKE %" + username + "%;";
        List<String> messages = new ArrayList<>();
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                messages.add(rs.getString("data"));
                System.out.println(rs.getString("data"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

}
