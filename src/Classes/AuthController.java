package Classes;

import java.sql.*;

public class AuthController {
    /**
     * Creates an AuthController object and calls setUp if no object has not been
     * created by this class before.
     */
    public AuthController() {
        if (!settedUp) {
            setUp();
            settedUp = true;
        }
    }

    /**
     * tries to log in the user with the provided credentials
     * 
     * @param email    user's email.
     * @param password user's password.
     * 
     * @return the user object with its data if successful, null otherwise.
     */
    public User authenticate(String email, String password) {
        User user = getUser(email, false, password);

        if (user != null) {
            updateSession(user.email);
        }

        return user;
    }

    /**
     * it tries to create an account with the provided user object
     * 
     * @param user an user object containing user's data
     * 
     * @return the user object with its data if successful, null otherwise.
     */
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

    /**
     * logs out the current user and clears the session table.
     */
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

    /**
     * it gets a connection with the database and configure it with the required
     * tables.
     * 
     * Note that it is a low level function that shouldn't be called by you.
     * 
     */
    private void setUp() {
        createDB();
        createUsersTable();
    }

    /**
     * @return the current logged in user, if there is none it returns null.
     */
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

    /**
     * it gets a connection with database creating it if it doesn't exist.
     * 
     * Note that it is a low level function that shouldn't be called by you.
     */
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

    /**
     * It creates users table and session table with the provided schemas.
     * 
     * Note that it is a low level function that shouldn't be called by you.
     */

    private void createUsersTable() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS users (
                    userName TEXT UNIQUE NOT NULL,
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

    /**
     * This is the backend for both authenticate and getCurrentUser methods.
     * 
     * Note that it is a low level function that shouldn't be called by you.
     * 
     * @param email           user email
     * @param alreadyLoggedIn if this is true the password field will be discarded
     *                        if not it will be used.
     * @param password        user's password.
     * 
     * @return user object with its data if successful and null otherwise.
     */
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

    /**
     * This method is used by authenticate.
     * 
     * it updates the session with the provided email, so the user doesn't need to
     * rewrite his data every time
     * 
     * Note that it is a low level function that shouldn't be called by you.
     * 
     * @param email user email
     */
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

    /**
     * 
     * @return the database connection.
     */
    public Connection getConnection() {
        return c;
    }

}
