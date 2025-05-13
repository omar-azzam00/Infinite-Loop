package com.infinite_loop.Classes;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

/**
 * Manages reminders for a user, including creating, editing, and deleting reminders in a database.
 * This class interacts with the database to store and manipulate reminder data associated with a user.
 */
public class ReminderController {
    private User user;
    private Connection c;
    private Scanner s;

    /**
     * Constructor for the ReminderController class that initializes the user, database connection, 
     * and sets up a Scanner for user input. It also creates the reminders table if it doesn't exist.
     *
     * @param user the user object associated with the reminders
     * @param c the database connection used to interact with the reminders table
     */
    public ReminderController(User user, Connection c) {
        this.user = user;
        this.c = c;
        this.s = new Scanner(System.in);
        createReminderTable();
    }

    /**
     * Creates the reminders table in the database if it doesn't already exist.
     * The table stores reminder details such as ID, title, date, time, and the associated user's email.
     */
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

    /**
     * Displays the main menu for managing reminders and handles user input to perform operations.
     * <p>
     * This method provides the following options:
     * 1. Create Reminder: Allows the user to create a new reminder and store it in the database.
     * 2. Edit Reminder: Enables the user to modify an existing reminder by its unique ID.
     * 3. Delete Reminder: Removes an existing reminder identified by its unique ID.
     * <p>
     * For operations requiring a reminder ID (Edit/Delete), this method validates the user-provided ID
     * to ensure an existing reminder is being modified or removed.
     *
     * @return null (this method does not return a meaningful value)
     */
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

    /**
     * Creates a new reminder by collecting details from the user and storing them in the database.
     * <p>
     * This method:
     * - Prompts the user to enter a unique reminder ID and validates that it doesn't already exist.
     * - Collects the reminder title, date, and time from the user.
     * - Inserts the new reminder into the database with the associated user's email.
     */
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

    /**
     * Edits an existing reminder by allowing the user to modify its title, date, or time.
     * <p>
     * This method provides the following options:
     * 1. Title: Modifies the reminder's title.
     * 2. Date: Updates the reminder's date.
     * 3. Time: Changes the reminder's time.
     * 4. Change All: Updates all fields (title, date, and time).
     *
     * @param id the unique ID of the reminder to be edited
     */
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

    /**
     * Updates the title of an existing reminder in the database.
     * <p>
     * This method:
     * - Retrieves and displays the current title of the reminder.
     * - Prompts the user to enter a new title and validates that it is not empty.
     * - Updates the reminder's title in the database.
     *
     * @param id the unique ID of the reminder to update
     */
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

    /**
     * Updates the date of an existing reminder in the database.
     * <p>
     * This method:
     * - Retrieves and displays the current date of the reminder.
     * - Prompts the user to enter a new date and validates that it is not empty.
     * - Updates the reminder's date in the database.
     *
     * @param id the unique ID of the reminder to update
     */
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

    /**
     * Updates the time of an existing reminder in the database.
     * <p>
     * This method:
     * - Retrieves and displays the current time of the reminder.
     * - Prompts the user to enter a new time and validates that it is not empty.
     * - Updates the reminder's time in the database.
     *
     * @param id the unique ID of the reminder to update
     */
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

    /**
     * Deletes a reminder from the database based on its unique ID.
     *
     * @param id the unique ID of the reminder to be deleted
     */
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

    /**
     * Validates whether a reminder ID exists and belongs to the current user.
     *
     * @param id the unique ID of the reminder to validate
     * @return true if the ID exists and belongs to the user, false otherwise
     */
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