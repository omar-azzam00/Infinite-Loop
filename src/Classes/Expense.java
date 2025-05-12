package Classes;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;
import java.time.*;
public class Expense {
    private User user;
    private Connection c;
    private Scanner s;

    public Expense(User user, Connection c) {
        this.user = user;
        this.c = c;
        this.s = new Scanner(System.in);
        createExpenseTable();
    }

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
                    } else {
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
                if (expenseamount == 0.0) {
                    System.out.println("Amount cannot be empty or a Zero!");
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
                try{
                    LocalDate.parse(expensedate);
                    break;
                }catch (Exception e) {
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
                        expenseid, expensecategory, expenseamount, expensedate,true, user.email);
            }
            else {
                sql = String.format("INSERT INTO expenses(expenseId, expensecategory, expenseamount, expensedate, isrecurring, userEmail) VALUES('%s','%s','%f','%s','%b','%s');",
                        expenseid, expensecategory, expenseamount, expensedate, false, user.email);
            }
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

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
                System.out.println("Enter new expense category: ");
                String newCategory = s.nextLine().trim();
                if (newCategory.trim().isEmpty()) {
                    continue;
                }
                String sql = String.format("Update expenses set expensecategory = '%s' WHERE expenseid = '%s'", newCategory, id);
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
                String sq = String.format("Select expenseamount from expenses where expenseid = '%s'", id);
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sq);
                if (rs.next()) {
                    double amount = rs.getDouble("expenseamount");
                    System.out.println("Current value: " + amount);
                }
                System.out.println("Enter new expense amount: ");
                double newAmount = s.nextDouble();
                if (newAmount == 0.0) {
                    continue;
                }
                s.nextLine();
                String sql = String.format("Update expenses set expenseamount = '%f' WHERE expenseid = '%s'", newAmount, id);
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                valid = true;
                System.out.println("Updated Successfully");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    public boolean validate_id(String id) {
        try {
            String sql = String.format("SELECT userEmail FROM expenses WHERE expenseid = '%s'", id);
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
    public void display_expenses() {
        try {
            String sql = String.format("Select * from expenses where userEmail = '%s'", user.email);
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                String id = rs.getString("expenseid");
                String category = rs.getString("expensecategory");
                double amount = rs.getDouble("expenseamount");
                System.out.println("ID: " + id + ", Catrgiry: " + category + ", Amount: " + amount);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }
    public void delete_expense(String id) {
        try {
            String sql = String.format("delete from expenses where expenseid = '%s'", id);
            Statement st = c.createStatement();
            st.executeUpdate(sql);
            System.out.println("Deleted Successfully");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
