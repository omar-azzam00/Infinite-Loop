package Classes;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class ReminderController {
    private User user;
    private Connection c;
    private Scanner s;

    public ReminderController(User user, Connection c) {
        this.user = user;
        this.c = c;
        this.s = new Scanner(System.in);
        createReminderTable();
    }

    private void createReminderTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS reminders (
                    reminderId TEXT PRIMARY KEY NOT NULL,
                    title TEXT NOT NULL,
                    date DATE NOT NULL,
                    time TIME NOT NULL,
                    userEmail TEXT NOT NULL,
                    FOREIGN KEY (userEmail) REFERENCES session(email)
                );
                """;

        try {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public ReminderController menu() {
        System.out.println("1. Create Reminder\n 2. Edit Reminder\n 3. Delete Reminder\n");
        int option = s.nextInt();
        s.nextLine();
        switch (option) {
            case 1:
                createReminder();
                break;
            case 2:
                try {
                    while (true) {
                        System.out.println("Please enter the Reminder ID you want to edit: ");
                        String id = s.nextLine().trim();
                        boolean check = validateId(id);
                        if (check) {
                            editReminder(id);
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                break;
            case 3:
                try {
                    while (true) {
                        System.out.println("Please enter the Reminder ID you want to delete: ");
                        String id = s.nextLine().trim();
                        boolean check = validateId(id);
                        if (check) {
                            deleteReminder(id);
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                break;
        }
        return null;
    }

    public void createReminder() {
        try {
            String reminderId;
            while (true) {
                System.out.println("Enter Reminder ID: ");
                reminderId = s.nextLine().trim();
                if (reminderId.trim().isEmpty()) {
                    System.out.println("ID cannot be empty!");
                    continue;
                }
                try {
                    String sql = String.format("SELECT reminderId FROM reminders WHERE reminderId = '%s'", reminderId);
                    Statement stmt = c.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    if (rs.next()) {
                        System.out.println("ID already exists");
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            System.out.println("Enter Reminder title: ");
            String title = s.nextLine().trim();
            System.out.println("Enter Reminder date (YYYY-MM-DD): ");
            String date = s.nextLine().trim();
            System.out.println("Enter Reminder time (HH:MM:SS): ");
            String time = s.nextLine().trim();

            String sql = String.format("INSERT INTO reminders(reminderId, title, date, time, userEmail) VALUES('%s', '%s', '%s', '%s', '%s')",
                    reminderId, title, date, time, user.email);
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void editReminder(String id) {
        System.out.println("What would you like to change?\n 1. Title\n 2. Date\n 3. Time\n 4. Change All\n");
        int option = s.nextInt();
        s.nextLine();
        switch (option) {
            case 1:
                editTitle(id);
                break;
            case 2:
                editDate(id);
                break;
            case 3:
                editTime(id);
                break;
            case 4:
                editTitle(id);
                editDate(id);
                editTime(id);
                break;
        }
    }

    public void editTitle(String id) {
        boolean valid = false;
        while (!valid) {
            try {
                String val = String.format("SELECT title FROM reminders WHERE reminderId = '%s'", id);
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(val);
                if (rs.next()) {
                    String title = rs.getString("title");
                    System.out.println("Current value: " + title);
                }
                System.out.println("Enter new Reminder title: ");
                String newTitle = s.nextLine().trim();
                if (newTitle.trim().isEmpty()) {
                    continue;
                }
                String sql = String.format("UPDATE reminders SET title = '%s' WHERE reminderId = '%s'", newTitle, id);
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                valid = true;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void editDate(String id) {
        boolean valid = false;
        while (!valid) {
            try {
                String val = String.format("SELECT date FROM reminders WHERE reminderId = '%s'", id);
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(val);
                if (rs.next()) {
                    String date = rs.getString("date");
                    System.out.println("Current value: " + date);
                }
                System.out.println("Enter new Reminder date (YYYY-MM-DD): ");
                String newDate = s.nextLine().trim();
                if (newDate.trim().isEmpty()) {
                    continue;
                }
                String sql = String.format("UPDATE reminders SET date = '%s' WHERE reminderId = '%s'", newDate, id);
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                valid = true;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void editTime(String id) {
        boolean valid = false;
        while (!valid) {
            try {
                String val = String.format("SELECT time FROM reminders WHERE reminderId = '%s'", id);
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(val);
                if (rs.next()) {
                    String time = rs.getString("time");
                    System.out.println("Current value: " + time);
                }
                System.out.println("Enter new Reminder time (HH:MM:SS): ");
                String newTime = s.nextLine().trim();
                if (newTime.trim().isEmpty()) {
                    continue;
                }
                String sql = String.format("UPDATE reminders SET time = '%s' WHERE reminderId = '%s'", newTime, id);
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                valid = true;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void deleteReminder(String id) {
        try {
            String sql = String.format("DELETE FROM reminders WHERE reminderId = '%s'", id);
            Statement st = c.createStatement();
            st.executeUpdate(sql);
            System.out.println("Deleted Successfully");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean validateId(String id) {
        try {
            String sql = String.format("SELECT userEmail FROM reminders WHERE reminderId = '%s'", id);
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                String email = rs.getString("userEmail");
                if (Objects.equals(email, user.email)) {
                    return true;
                } else {
                    System.out.println("ID invalid");
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("ID invalid");
        return false;
    }
}