package Classes;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

public class IncomeController {
    private User user;
    private Connection c;
    private Scanner s;

    public IncomeController(User user, Connection c) {
        this.user = user;
        this.c = c;
        this.s = new Scanner(System.in);
        createIncomeTable();
    }

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