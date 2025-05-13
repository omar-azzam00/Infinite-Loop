package com.infinite_loop.Classes;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

/**
 * Manages income records for a user, including adding, editing, and deleting income entries in a database.
 * This class interacts with the database to store and manipulate income data associated with a user.
 */
public class IncomeController {
    private User user;
    private Connection c;
    private Scanner s;

    /**
     * Constructor for the IncomeController class that initializes the user, database connection, 
     * and sets up a Scanner for user input. It also creates the incomes table if it doesn't exist.
     *
     * @param user the user object associated with the income records
     * @param c the database connection used to interact with the incomes table
     */
    public IncomeController(User user, Connection c) {
        this.user = user;
        this.c = c;
        this.s = new Scanner(System.in);
        createIncomeTable();
    }

    /**
     * Creates the incomes table in the database if it doesn't already exist.
     * The table stores income details such as ID, source, amount, date, and the associated user's email.
     */
    private void createIncomeTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS incomes (
                    incomeId TEXT PRIMARY KEY NOT NULL,
                    source TEXT NOT NULL,
                    amount DOUBLE NOT NULL,
                    date DATE NOT NULL,
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
     * Displays the main menu for managing income records and handles user input to perform operations.
     * <p>
     * This method provides the following options:
     * 1. Add Income: Allows the user to create a new income record and store it in the database.
     * 2. Edit Income: Enables the user to modify an existing income record by its unique ID.
     * 3. Delete Income: Removes an existing income record identified by its unique ID.
     * <p>
     * For operations requiring an income ID (Edit/Delete), this method validates the user-provided ID
     * to ensure an existing income record is being modified or removed.
     *
     * @return null (this method does not return a meaningful value)
     */
    public IncomeController menu() {
        System.out.println("1. Add Income\n 2. Edit Income\n 3. Delete Income\n");
        int option = s.nextInt();
        s.nextLine();
        switch (option) {
            case 1:
                addIncome();
                break;
            case 2:
                try {
                    while (true) {
                        System.out.println("Please enter the Income ID you want to edit: ");
                        String id = s.nextLine().trim();
                        boolean check = validateId(id);
                        if (check) {
                            editIncome(id);
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
                        System.out.println("Please enter the Income ID you want to delete: ");
                        String id = s.nextLine().trim();
                        boolean check = validateId(id);
                        if (check) {
                            deleteIncome(id);
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
     * Adds a new income record by collecting details from the user and storing them in the database.
     * <p>
     * This method:
     * - Prompts the user to enter a unique income ID and validates that it doesn't already exist.
     * - Collects the income source, amount, and date from the user, ensuring they are valid.
     * - Inserts the new income record into the database with the associated user's email.
     */
    public void addIncome() {
        try {
            String incomeId;
            while (true) {
                System.out.println("Enter Income ID: ");
                incomeId = s.nextLine().trim();
                if (incomeId.trim().isEmpty()) {
                    System.out.println("ID cannot be empty!");
                    continue;
                }
                try {
                    String sql = String.format("SELECT incomeId FROM incomes WHERE incomeId = '%s'", incomeId);
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
            System.out.println("Enter Income source: ");
            String source = s.nextLine().trim();
            if (source.trim().isEmpty()) {
                System.out.println("Source cannot be empty!");
                return;
            }

            double amount;
            while (true) {
                System.out.println("Enter Income amount: ");
                amount = s.nextDouble();
                if (amount <= 0) {
                    System.out.println("Amount must be greater than 0!");
                    continue;
                }
                s.nextLine();
                break;
            }

            System.out.println("Enter Income date (YYYY-MM-DD): ");
            String date = s.nextLine().trim();
            if (date.trim().isEmpty()) {
                System.out.println("Date cannot be empty!");
                return;
            }

            String sql = String.format("INSERT INTO incomes(incomeId, source, amount, date, userEmail) VALUES('%s', '%s', %f, '%s', '%s')",
                    incomeId, source, amount, date, user.email);
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("Income added successfully!");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Edits an existing income record by allowing the user to modify its source, amount, or date.
     * <p>
     * This method provides the following options:
     * 1. Source: Modifies the income's source.
     * 2. Amount: Updates the income's amount.
     * 3. Date: Changes the income's date.
     * 4. Change All: Updates all fields (source, amount, and date).
     *
     * @param id the unique ID of the income record to be edited
     */
    public void editIncome(String id) {
        System.out.println("What would you like to change?\n 1. Source\n 2. Amount\n 3. Date\n 4. Change All\n");
        int option = s.nextInt();
        s.nextLine();
        switch (option) {
            case 1:
                editSource(id);
                break;
            case 2:
                editAmount(id);
                break;
            case 3:
                editDate(id);
                break;
            case 4:
                editSource(id);
                editAmount(id);
                editDate(id);
                break;
        }
    }

    /**
     * Updates the source of an existing income record in the database.
     * <p>
     * This method:
     * - Retrieves and displays the current source of the income record.
     * - Prompts the user to enter a new source and validates that it is not empty.
     * - Updates the income's source in the database.
     *
     * @param id the unique ID of the income record to update
     */
    public void editSource(String id) {
        boolean valid = false;
        while (!valid) {
            try {
                String val = String.format("SELECT source FROM incomes WHERE incomeId = '%s'", id);
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(val);
                if (rs.next()) {
                    String source = rs.getString("source");
                    System.out.println("Current value: " + source);
                }
                System.out.println("Enter new Income source: ");
                String newSource = s.nextLine().trim();
                if (newSource.trim().isEmpty()) {
                    System.out.println("Source cannot be empty!");
                    continue;
                }
                String sql = String.format("UPDATE incomes SET source = '%s' WHERE incomeId = '%s'", newSource, id);
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                valid = true;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Updates the amount of an existing income record in the database.
     * <p>
     * This method:
     * - Retrieves and displays the current amount of the income record.
     * - Prompts the user to enter a new amount and validates that it is greater than 0.
     * - Updates the income's amount in the database.
     *
     * @param id the unique ID of the income record to update
     */
    public void editAmount(String id) {
        boolean valid = false;
        while (!valid) {
            try {
                String val = String.format("SELECT amount FROM incomes WHERE incomeId = '%s'", id);
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(val);
                if (rs.next()) {
                    double amount = rs.getDouble("amount");
                    System.out.println("Current value: " + amount);
                }
                System.out.println("Enter new Income amount: ");
                double newAmount = s.nextDouble();
                if (newAmount <= 0) {
                    System.out.println("Amount must be greater than 0!");
                    s.nextLine();
                    continue;
                }
                s.nextLine();
                String sql = String.format("UPDATE incomes SET amount = %f WHERE incomeId = '%s'", newAmount, id);
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                valid = true;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Updates the date of an existing income record in the database.
     * <p>
     * This method:
     * - Retrieves and displays the current date of the income record.
     * - Prompts the user to enter a new date and validates that it is not empty.
     * - Updates the income's date in the database.
     *
     * @param id the unique ID of the income record to update
     */
    public void editDate(String id) {
        boolean valid = false;
        while (!valid) {
            try {
                String val = String.format("SELECT date FROM incomes WHERE incomeId = '%s'", id);
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(val);
                if (rs.next()) {
                    String date = rs.getString("date");
                    System.out.println("Current value: " + date);
                }
                System.out.println("Enter new Income date (YYYY-MM-DD): ");
                String newDate = s.nextLine().trim();
                if (newDate.trim().isEmpty()) {
                    System.out.println("Date cannot be empty!");
                    continue;
                }
                String sql = String.format("UPDATE incomes SET date = '%s' WHERE incomeId = '%s'", newDate, id);
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                valid = true;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Deletes an income record from the database based on its unique ID.
     *
     * @param id the unique ID of the income record to be deleted
     */
    public void deleteIncome(String id) {
        try {
            String sql = String.format("DELETE FROM incomes WHERE incomeId = '%s'", id);
            Statement st = c.createStatement();
            st.executeUpdate(sql);
            System.out.println("Income deleted successfully!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Validates whether an income ID exists and belongs to the current user.
     *
     * @param id the unique ID of the income record to validate
     * @return true if the ID exists and belongs to the user, false otherwise
     */
    public boolean validateId(String id) {
        try {
            String sql = String.format("SELECT userEmail FROM incomes WHERE incomeId = '%s'", id);
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