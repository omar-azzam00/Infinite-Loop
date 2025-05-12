package Classes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Analysis {
    private User user;
    private Connection c;

    public Analysis(User user, Connection c) {
        this.user = user;
        this.c = c;
    }

    public void analytics() {
        try {
            String sql = String.format(""" 
                    Select b.budgetcategory, IFNULL(MAX(b.budgetamount),0) As totalbudget, IFNULL(SUM(ex.expenseamount), 0) as totalexpense
                    From budgets b 
                    Left join expenses ex 
                    ON b.budgetcategory = ex.expensecategory And ex.useremail = b.useremail 
                    where b.userEmail = '%s'
                    group by b.budgetcategory
                    """, user.email);
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String category = rs.getString("budgetCategory");
                double totalspent = rs.getDouble("totalexpense");
                double totalbudget = rs.getDouble("totalbudget");
                if (totalspent <= totalbudget) {
                    System.out.println("You spent " + totalspent + " in " + category + " category" + ", Which is " +
                            String.format("%.2f", (totalspent / totalbudget) * 100) + "% of your budget, You have " + (totalbudget - totalspent) +
                            " till you reached your budget's limit\n");
                } else if (totalspent > totalbudget) {
                    System.out.println("You spent " + totalspent + " in " + category + " category" + ", You exceeded your budget by "
                            + String.format("%.2f", (((totalspent / totalbudget) * 100) - 100)) + "%");
                }

            }
            stmt.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
