package Classes;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

/**
 * The Budget class provides functionality to manage budgets in a database.
 * Each budget is related to a user and contains information such as budget ID, category, and amount.
 * The class allows creating budgets and editing their amount and category.
 */
public class Budget {
    private User user;
    private Connection c;
    private Scanner s;

    /**
     * This constructor initializes an Analysis instance by associating it with a specific user and database connection.
     * It initializes a Scanner instance to prepare for user's input.
     * Additionally, it ensures that the required "budgets" table is created in the database if it doesn't already exist.
     *
     * @param user An object representing the currently logged-in user.
     * @param c The Connection object used to interact with the database.
     */
    public Budget(User user, Connection c) {
        this.user = user;
        this.c = c;
        this.s = new Scanner(System.in);
        createBudgetTable();
    }

    /**
     * Creates the "budgets" table in the database if it does not already exist.
     * The table contains:
     * - budgetId: A unique identifier for each budget (primary key).
     * - budgetCategory: A text field representing the category of the budget.
     * - budgetAmount: A double value representing the amount allocated for the budget.
     * - userEmail: A foreign key representing the email in the "session" table.
     */
    private void createBudgetTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS budgets (
                       budgetId text PRIMARY KEY NOT NULL,     
                       budgetCategory text NOT NULL,
                       budgetAmount double NOT NULL,
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
     * Creates a new budget and adds it to the database.
     * <p>
     * The method makes the user input a budget ID, budget amount, and budget category.
     * Each input is validated to ensure it meets the required criteria:
     * - Budget ID: Must not be empty and it must be unique.
     * - Budget Amount: Must be a non-zero positive value.
     * - Budget Category: Can't be empty and can't be a duplicate of an existing category.
     * <p>
     * Once all inputs meets the required criteria the method inserts a new record into the "budgets" table
     * with the provided budget details and links it to the logged-in user by their email.
     */
    public void creatBudget() {
        try {
            String budgetID;
            while (true) {
                System.out.println("Enter budget ID: ");
                budgetID = s.nextLine().trim();
                if (budgetID.trim().isEmpty()) {
                    System.out.println("ID cannot be empty!");
                    continue;
                }
                try {
                    String sql = String.format("SELECT budgetid FROM budgets WHERE budgetid = '%s'", budgetID);
                    Statement stmt = c.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    if (rs.next()) {
                        System.out.println("ID already exists");
                    } else {
                        rs.close();
                        stmt.close();
                        break;
                    }
                    rs.close();
                    stmt.close();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            double budgetAmount;
            while (true) {
                System.out.println("Enter budget amount: ");
                budgetAmount = s.nextDouble();
                if (budgetAmount <= 0.0) {
                    System.out.println("Amount cannot be empty or less than or equal Zero!");
                    continue;
                }
                s.nextLine();
                break;
            }
            String budgetCategory;
            while (true) {
                System.out.println("Enter budget category: ");
                budgetCategory = s.nextLine().trim();
                if (budgetCategory.trim().isEmpty()) {
                    System.out.println("Category cannot be empty!");
                    continue;
                }
                Statement stmt = c.createStatement();
                String sql = String.format("SELECT budgetCategory FROM budgets WHERE budgetCategory = '%s'", budgetCategory);
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    String budgetCategory1 = rs.getString("budgetCategory");
                    if (budgetCategory1.equals(budgetCategory)) {
                        System.out.println("Category already exists");
                        rs.close();
                        stmt.close();
                        continue;
                    }
                }
                rs.close();
                stmt.close();
                break;
            }
            String sql = String.format("INSERT INTO budgets(budgetID, budgetCategory, budgetAmount, userEmail) VALUES('%s','%s','%f','%s');",
                    budgetID, budgetCategory, budgetAmount, user.email);
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Edits the budget table for the specified budget ID.
     * The user is prompted to choose an option to update either the budget category, the budget amount, or both.
     *
     * @param id The unique identifier of the budget entry that needs to be updated.
     */
    public void edit_budgetTable(String id) {
        System.out.println("What would you like to change?\n 1. Budget Category\n 2. Budget Amount\n 3. Change Both\n");
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

    /** Updates the category of the specified budget.
     * The method gets the current category of the budget displays it to the user and prompts for a new category.
     * Upon receiving valid input, the category is updated in the database.
     * The prompt keeps on showing to the user until the input is valid.
     *
     * @param id The unique identifier of the budget entry whose category is to be updated.
     */
    public void edit_category(String id) {
        boolean valid = false;
        while (!valid) {
            try {
                String val = String.format("Select budgetCategory from budgets where budgetid = '%s'", id);
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(val);
                if (rs.next()) {
                    String category = rs.getString("budgetCategory");
                    System.out.println("Current value: " + category);
                }
                System.out.println("Enter new budget category: ");
                String newCategory = s.nextLine().trim();
                if (newCategory.trim().isEmpty()) {
                    continue;
                }
                rs.close();
                st.close();
                String sql = String.format("Update budgets set budgetCategory = '%s' WHERE budgetID = '%s'", newCategory, id);
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

    /** Updates the amount of the specified budget.
     * The method gets the current amount of the budget displays it to the user and prompts for a new amount.
     * Upon receiving valid input, the amount is updated in the database.
     * The prompt keeps on showing to the user until the input is valid.
     *
     * @param id The unique identifier of the budget entry whose amount is to be updated.
     */
    public void edit_amount(String id) {
        boolean valid = false;
        while (!valid) {
            try {
                String sq = String.format("Select budgetamount from budgets where budgetid = '%s'", id);
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sq);
                if (rs.next()) {
                    double amount = rs.getDouble("budgetamount");
                    System.out.println("Current value: " + amount);
                }
                System.out.println("Enter new budget amount: ");
                double newAmount = s.nextDouble();
                if (newAmount <= 0.0) {
                    System.out.println("Amount cannot be empty or less than or equal Zero!");
                    continue;
                }
                rs.close();
                st.close();
                s.nextLine();
                String sql = String.format("Update budgets set budgetamount = '%f' WHERE budgetID = '%s'", newAmount, id);
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
     * Deletes a budget from the "budgets" table in the database based on the specified budget ID.
     *
     * @param id The unique identifier of the budget to be deleted from the database.
     */
    public void deleteBudget(String id) {
        try {
            String sql = String.format("delete from budgets where budgetid = '%s'", id);
            Statement st = c.createStatement();
            st.executeUpdate(sql);
            st.close();
            System.out.println("Deleted Successfully");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Checks if an id is related to the logged-in user.
     * <p>
     * THe methods gets the user's email of the specified ID and checks if it's the same as the email of the logged-in user.
     * If the ID is valid and associated with the user, it returns true; otherwise, it returns false.
     *
     * @param id The unique budget ID to be validated.
     * @return true if the ID exists in the database and is associated with the user's email and return false otherwise.
     */
    public boolean validate_id(String id) {
        try {
            String sql = String.format("SELECT userEmail FROM budgets WHERE budgetid = '%s'", id);
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
     * Displays all budget entries associated with the current logged-in user.
     * <p>
     * The method fetches all records from the "budgets" table in the database that is associated with the logged-in user.
     * For each retrieved record, the budget ID, category, and amount are displayed to the user.
     */
    public void display_budget() {
        try {
            String sql = String.format("Select * from budgets where userEmail = '%s'", user.email);
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String id = rs.getString("budgetid");
                String category = rs.getString("budgetCategory");
                double amount = rs.getDouble("budgetamount");
                System.out.println("ID: " + id + ", Catrgiry: " + category + ", Amount: " + amount);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
