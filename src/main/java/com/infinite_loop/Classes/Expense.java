package com.infinite_loop.Classes;


import java.sql.*;
import java.util.Objects;
import java.util.Scanner;
import java.time.*;

/**
 * This class is responsible for managing user expenses including creating, displaying, updating, and deleting expense records in a database.
 */
public class Expense {
    private User user;
    private Connection c;
    private Scanner s;

    /**
     * Constructs an Expense object with a specified User and database connection.
     * Initializes a Scanner instance and sets up the "expenses" table in the database.
     *
     * @param user An object representing the currently logged-in user.
     * @param c    The Connection object used to interact with the database.
     */
    public Expense(User user, Connection c) {
        this.user = user;
        this.c = c;
        this.s = new Scanner(System.in);
        createExpenseTable();
    }

    /**
     * Creates the "expenses" table in the database if it does not already exist.
     * <p>
     * The table includes the following columns:
     * - expenseid: A unique identifier for the expense (primary key).
     * - expensecategory: The category of the expense stored as text.
     * - expenseamount: The amount for the expense stored as a double.
     * - expensedate: The date of the expense stored.
     * - isrecurring: A boolean that shows whether the expense is recurring.
     * - userEmail: The email of the user associated with the expense stored as text (foreign key refrencing email in the sessions table).
     */
    private void createExpenseTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS expenses (
                       expenseid text PRIMARY KEY NOT NULL,     
                       expensecategory text NOT NULL,
                       expenseamount double NOT NULL,
                       expensedate date NOT NULL,
                       isrecurring boolean,
                       userEmail text NOT NULL,
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
     * Displays a menu to the user for managing expenses and performs actions based on user input.
     * <p>
     * The menu options include:
     * 1. Adding a new expense.
     * 2. Editing an existing expense by ID.
     * 3. Deleting an expense by ID.
     * 4. Displaying all expenses.
     */
    public void menu() {
        System.out.println(" 1.Add Expense\n 2. Edit Expense\n 3. Delete Expense\n 4. Display Expenses\n");
        int option = s.nextInt();
        s.nextLine();
        switch (option) {
            case 1:
                add_expense();
                break;
            case 2:
                try {
                    while (true) {
                        System.out.println("Please enter the ID you want to edit: ");
                        String id = s.nextLine().trim();
                        boolean check = validate_id(id);
                        if (check) {
                            edit_expensesTable(id);
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
                        System.out.println("Please enter the ID you want to delete: ");
                        String id = s.nextLine().trim();
                        boolean check = validate_id(id);
                        if (check) {
                            delete_expense(id);
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                break;
            case 4:
                display_expenses();
                break;
        }
    }

    /**
     * Adds a new expense to the database.
     * <p>
     * This method prompts the user to input details for a new expense including:
     * expense ID, amount, category, date, and whether the expense is recurring.
     * It validates each input to ensure that it is valid before inserting the expense into the database.
     * <p>
     * Behavior and Input Validation:
     * - Prompts the user to enter a unique expense ID and checks if the ID already exists in the database.
     * - Ensures the expense amount is not empty or less than are equal to zero.
     * - Ensures the category is not empty.
     * - Validates the date format to ensure it complies with the 'yyyy-mm-dd' format.
     * - Asks the user whether the expense is recurring and stores the response accordingly.
     */
    public void add_expense() {
        try {
            String expenseid;
            while (true) {
                System.out.println("Enter expense ID: ");
                expenseid = s.nextLine().trim();
                if (expenseid.trim().isEmpty()) {
                    System.out.println("ID cannot be empty!");
                    continue;
                }
                try {
                    String sql = String.format("SELECT expenseid FROM expenses WHERE expenseid = '%s'", expenseid);
                    Statement stmt = c.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    if (rs.next()) {
                        System.out.println("ID already exists");
                        rs.close();
                        stmt.close();
                    } else {
                        rs.close();
                        stmt.close();
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            double expenseamount;
            while (true) {
                System.out.println("Enter expense amount: ");
                expenseamount = s.nextDouble();
                if (expenseamount <= 0.0) {
                    System.out.println("Amount cannot be empty or less than or equal to Zero!");
                    continue;
                }
                s.nextLine();
                break;
            }
            String expensecategory;
            while (true) {
                System.out.println("Enter expense category: ");
                expensecategory = s.nextLine().trim();
                if (expensecategory.trim().isEmpty()) {
                    System.out.println("Category cannot be empty!");
                    continue;
                }
                break;
            }
            String expensedate;
            while (true) {
                System.out.println("Enter expense date in this format'2021-01-01': ");
                expensedate = s.nextLine().trim();
                if (expensedate.trim().isEmpty()) {
                    System.out.println("Date cannot be empty!");
                    continue;
                }
                try {
                    LocalDate.parse(expensedate);
                    break;
                } catch (Exception e) {
                    System.out.println("Invalid Date Format");
                }
            }
            boolean recurring = false;
            while (true) {
                System.out.println("Will this be a recurring expense? (y/n): ");
                String answer = s.nextLine().trim();
                if (answer.trim().isEmpty()) {
                    System.out.println("Answer cannot be empty!");
                    continue;
                }
                if (answer.equals("y")) {
                    recurring = true;
                }
                break;
            }
            String sql = "";
            if (recurring) {
                sql = String.format("INSERT INTO expenses(expenseId, expensecategory, expenseamount, expensedate, isrecurring, userEmail) VALUES('%s','%s','%f','%s','%b','%s');",
                        expenseid, expensecategory, expenseamount, expensedate, true, user.email);
            } else {
                sql = String.format("INSERT INTO expenses(expenseId, expensecategory, expenseamount, expensedate, isrecurring, userEmail) VALUES('%s','%s','%f','%s','%b','%s');",
                        expenseid, expensecategory, expenseamount, expensedate, false, user.email);
            }
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Edits the expense in the "expenses" table for a specific expense ID.
     * Allows the user to update either the category, the amount, or both.
     *
     * @param id The unique identifier of the expense entry to be edited.
     */
    public void edit_expensesTable(String id) {
        System.out.println("What would you like to change?\n 1. Expense Category\n 2. Expense Amount\n 3. Change Both\n");
        int option = s.nextInt();
        s.nextLine();
        switch (option) {
            case 1:
                edit_category(id);
                break;
            case 2:
                edit_amount(id);
                break;
            case 3:
                System.out.println("--- Editing Category ---");
                edit_category(id);
                System.out.println("--- Editing Amount ---");
                edit_amount(id);
                break;
        }
    }

    /**
     * Edits the category of an expense by changing it in the database.
     * Prompts the user to input a new category for the specified expense id.
     * The update operation will only succeed if the entered category is valid.
     *
     * @param id The unique identifier of the expense whose category is to be edited.
     */
    public void edit_category(String id) {
        boolean valid = false;
        while (!valid) {
            try {
                String val = String.format("Select expensecategory from expenses where expenseid = '%s'", id);
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(val);
                if (rs.next()) {
                    String category = rs.getString("expensecategory");
                    System.out.println("Current value: " + category);
                }
                rs.close();
                st.close();
                System.out.println("Enter new expense category: ");
                String newCategory = s.nextLine().trim();
                if (newCategory.trim().isEmpty()) {
                    continue;
                }
                String sql = String.format("Update expenses set expensecategory = '%s' WHERE expenseid = '%s'", newCategory, id);
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
                valid = true;
                System.out.println("Updated Successfully");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Edits the amount of an expense in the database associated with the given expense id.
     * The method retrieves the current expense amount, prompts the user to input a new amount,
     * and updates the expense record if a valid new amount is provided.
     *
     * @param id the unique identifier of the expense to be updated
     */
    public void edit_amount(String id) {
        boolean valid = false;
        while (!valid) {
            try {
                String sq = String.format("Select expenseamount from expenses where expenseid = '%s'", id);
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sq);
                if (rs.next()) {
                    double amount = rs.getDouble("expenseamount");
                    System.out.println("Current value: " + amount);
                }
                rs.close();
                st.close();
                System.out.println("Enter new expense amount: ");
                double newAmount = s.nextDouble();
                if (newAmount <= 0.0) {
                    continue;
                }
                s.nextLine();
                String sql = String.format("Update expenses set expenseamount = '%f' WHERE expenseid = '%s'", newAmount, id);
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
                valid = true;
                System.out.println("Updated Successfully");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Validates whether the given expense id exists in the database and if it is associated with the current user's email.
     *
     * @param id the expense ID to validate
     * @return true if the ID is valid and belongs to the current user, false otherwise
     */
    public boolean validate_id(String id) {

        try {
            String sql = String.format("SELECT userEmail FROM expenses WHERE expenseid = '%s'", id);
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                String email = rs.getString("userEmail");
                rs.close();
                stmt.close();
                if (Objects.equals(email, user.email)) {
                    return true;
                } else {
                    System.out.println("ID invalid");
                    return false;
                }
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("ID invalid");
        return false;
    }

    /**
     * Retrieves and displays all expense records associated with the user's email from the expenses table.
     * Each expense record includes its id, category, and amount, which are displayed to the user.
     */
    public void display_expenses() {
        try {
            String sql = String.format("Select * from expenses where userEmail = '%s'", user.email);
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String id = rs.getString("expenseid");
                String category = rs.getString("expensecategory");
                double amount = rs.getDouble("expenseamount");
                System.out.println("ID: " + id + ", Catrgiry: " + category + ", Amount: " + amount);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    /**
     * Deletes an expense entry from the database based on the provided expense ID.
     *
     * @param id The unique identifier of the expense to be deleted from the database.
     */
    public void delete_expense(String id) {
        try {
            String sql = String.format("delete from expenses where expenseid = '%s'", id);
            Statement st = c.createStatement();
            st.executeUpdate(sql);
            st.close();
            System.out.println("Deleted Successfully");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
