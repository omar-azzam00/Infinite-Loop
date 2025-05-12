package Classes;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class Budget {
    private User user;
    private Connection c;
    private Scanner s;
    private Expense expense;
    public Budget(User user, Connection c) {
        this.user = user;
        this.c = c;
        this.s = new Scanner(System.in);
        this.expense = new Expense(user, c);
        createBudgetTable();
    }

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

    public void creat_budget() {
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
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            double budgetAmount;
            while (true) {
                System.out.println("Enter budget amount: ");
                budgetAmount = s.nextDouble();
                if (budgetAmount == 0.0) {
                    System.out.println("Amount cannot be empty or a Zero!");
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
                break;
            }
            String sql = String.format("INSERT INTO budgets(budgetID, budgetCategory, budgetAmount, userEmail) VALUES('%s','%s','%f','%s');",
                    budgetID, budgetCategory, budgetAmount, user.email);
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

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
                String sql = String.format("Update budgets set budgetCategory = '%s' WHERE budgetID = '%s'", newCategory, id);
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                valid = true;
                System.out.println("Updated Successfully");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

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
                if (newAmount == 0.0) {
                    continue;
                }
                s.nextLine();
                String sql = String.format("Update budgets set budgetamount = '%f' WHERE budgetID = '%s'", newAmount, id);
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                valid = true;
                System.out.println("Updated Successfully");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void delete_budget(String id) {
        try {
            String sql = String.format("delete from budgets where budgetid = '%s'", id);
            Statement st = c.createStatement();
            st.executeUpdate(sql);
            System.out.println("Deleted Successfully");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean validate_id(String id) {
        try {
            String sql = String.format("SELECT userEmail FROM budgets WHERE budgetid = '%s'", id);
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

    public void display_budget() {
        try {
            String sql = String.format("Select * from budgets where userEmail = '%s'", user.email);
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                String id = rs.getString("budgetid");
                String category = rs.getString("budgetCategory");
                double amount = rs.getDouble("budgetamount");
                System.out.println("ID: " + id + ", Catrgiry: " + category + ", Amount: " + amount);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
