package Auth;

import java.sql.*;
import java.util.Scanner;

public class Auth {
    public Auth() {
        if (!settedUp) {
            setUp();
            settedUp = true;
        }
    }

    public User run() {
        Scanner s = new Scanner(System.in);

        System.out.println("Welcome to infinite loop!\n");

        User user = getCurrentUser();

        if (user == null) {
            System.out.println("Do you want to (1) login or (2) sign up ?");
            System.out.print("Enter your option (1 or 2): ");

            String opt = "";

            while (true) {

                opt = s.nextLine().trim();

                if (opt.equals("1")) {
                    break;
                } else if (opt.equals("2")) {
                    break;
                } else {
                    System.out.print("Error! please enter 1 or 2 only: ");
                }
            }

            String userName = "";
            if (opt.equals("2")) {
                System.out.print("Enter your user name: ");
                userName = s.nextLine();
            }

            String email = "";
            System.out.print("Enter your email: ");

            while (true) {
                email = s.nextLine().trim();

                if (email.isEmpty()) {
                    System.out.print("Email can't be empty! enter again: ");
                } else {
                    break;
                }
            }

            String mobilePhone = "";
            if (opt.equals("2")) {
                System.out.print("Enter your mobile phone: ");
                mobilePhone = s.nextLine();
            }

            String password = "";
            System.out.print("Enter password: ");

            while (true) {
                password = s.nextLine();

                if (password.length() < 6) {
                    System.out.print("password can't be less than 6 chars! enter again: ");
                } else {
                    break;
                }
            }

            if (opt.equals("1")) {
                user = logIn(email, password);
                if (user == null) {
                    System.out.println("Credentials are invalid!");
                    System.exit(1);
                }
                return user;
            }

            if (opt.equals("2")) {
                user = signUp(new User(userName, email, mobilePhone, password));
                if (user == null) {
                    System.out.println("This user already exists!");
                    System.exit(1);
                }
                return user;
            }
        } else {
            System.out.println("You are logged in as " + user.email + ", do you want to (1) continue or (2) log out ?");
            System.out.print("Enter your option (1 or 2): ");
            String opt = "";

            while (true) {

                opt = s.nextLine().trim();

                if (opt.equals("1")) {
                    break;
                } else if (opt.equals("2")) {
                    break;
                } else {
                    System.out.print("Error! please enter 1 or 2 only: ");
                }
            }

            if (opt.equals("1")) {
                return user;
            } else {
                logOut();
                return run();
            }
        }
        return null;
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

    public User logIn(String email, String password) {
        User user = getUser(email, false, password);

        if (user != null) {
            updateSession(user.email);
        }

        return user;
    }

    public User signUp(User user) {
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
    /* You shouldn't care about the following */
    /*******************************************************************************************************/

    private Connection c = null;
    private static boolean settedUp = false;

    private void setUp() {
        createDB();
        createUsersTable();
    }

    private void createDB() {

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:infinite-loop.db");
        } catch (Exception e) {
            // System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);

            // throw an error that database can't be created.
        }
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

}
