package Classes;

import java.sql.*;

public class AuthController {
    public AuthController() {
        if (!settedUp) {
            setUp();
            settedUp = true;
        }
    }

    // it was log in previously.
    public User authenticate(String email, String password) {
        User user = getUser(email, false, password);

        if (user != null) {
            updateSession(user.email);
        }

        return user;
    }

    // it was signUp previously
    public User createUser(User user) {
        try {
            Statement stmt = c.createStatement();

            if (user.userName == null || user.email == null || user.mobilePhone == null || user.password == null
                    || user.email == "" || user.password.length() < 6) {
                return null;
            }

            String sql = String.format(
                    "INSERT INTO users(userName, email, mobilePhone, password) VALUES ('%s', '%s', '%s', '%s');",
                    user.userName, user.email, user.mobilePhone, user.password);

            stmt.executeUpdate(sql);
            stmt.close();

            updateSession(user.email);

            return user;
        } catch (Exception e) {
            // System.out.println(e);
            return null;
        }
    }

    public void logOut() {
        try {
            Statement stmt = c.createStatement();

            String sql = "DELETE FROM session WHERE 1 = 1";

            stmt.executeUpdate(sql);

            stmt.close();
        } catch (Exception e) {
            // System.out.println(e);
        }
    }

    /*******************************************************************************************************/
    /* here is low level db interactions */
    /*******************************************************************************************************/

    private Connection c = null;
    private static boolean settedUp = false;

    private void setUp() {
        createDB();
        createUsersTable();
    }

    public User getCurrentUser() {
        String sql = "SELECT * FROM session;";
        ResultSet result;

        try {
            Statement stmt = c.createStatement();
            result = stmt.executeQuery(sql);

            if (!result.next()) {
                stmt.close();
                return null;

            } else {
                String email = result.getString("email");
                return getUser(email, true, null);
            }
        } catch (Exception e) {
            // System.out.println(e);
        }

        return null;
    }

    private void createDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:infinite-loop.db");
            System.out.println("Database connection established.");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
            // throw an error that database can't be created.
        }
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        // System.out.println("Opened database successfully");
    }

    private void createUsersTable() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS users (
                    userName TEXT NOT NULL,
                    email TEXT PRIMARY KEY NOT NULL,
                    mobilePhone TEXT NOT NULL,
                    password TEXT NOT NULL
                );
                    CREATE TABLE IF NOT EXISTS session (
                    email TEXT PRIMARY KEY NOT NULL,
                    FOREIGN KEY (email) REFERENCES users(email)
                );
                CREATE IF NOT EXISTS budgets (
                       budgetId text PRIMARY KEY NOT NULL,     
                       category text NOT NULL,
                       limit double NOT NULL,
                       FOREIGN KEY (email) REFERENCES users(email)
                );
                                """;

        try {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            // System.out.println(e);
        }

    }

    private User getUser(String email, boolean alreadyLoggedIn, String password) {
        String sql;

        try {
            Statement stmt = c.createStatement();

            if (alreadyLoggedIn) {
                sql = String.format("SELECT * FROM users WHERE email = '%s';", email);

            } else {
                sql = String.format("SELECT * FROM users WHERE email = '%s' AND password = '%s';", email, password);

            }

            ResultSet userResult = stmt.executeQuery(sql);

            if (!userResult.next()) {
                return null;
            }

            return new User(userResult.getString("userName"), userResult.getString("email"),
                    userResult.getString("mobilePhone"), userResult.getString("password"));
        } catch (Exception e) {
            // System.out.println(e);
            return null;
        }
    }

    private void updateSession(String email) {
        logOut();

        try {
            Statement stmt = c.createStatement();

            String sql = String.format("INSERT INTO session(email) VALUES ('%s');", email);

            stmt.executeUpdate(sql);
            stmt.close();

        } catch (Exception e) {
            // System.out.println(e);
        }
    }

    public Connection getConnection() {
        return c;
    }

}
