package com.infinite_loop.Classes;

import java.sql.*;

/**
 * The Analysis class provides functionality to analyze a user's spending habits and budget usage.
 *
 * This class is used to assess how the user's total expenses in different categories compare to their budget limits,
 * providing insights on whether the user is under budget or over budget.
 */
public class Analysis {
    private User user;
    private Connection c;

    /**
     * This constructor initializes an Analysis instance by associating it with a specific user and database connection
     *
     * @param user An object representing the currently logged-in user.
     * @param c    The Connection object used to interact with the database.
     */
    public Analysis(User user, Connection c) {
        this.user = user;
        this.c = c;
    }

    /**
     * Analyzes the logged-in user's budget and expenses to provide feedback on spending compared to budget limits for each category.
     * <p>
     * This method joins the "budgets" and "expenses" tables to get the categories, its budget's amount and
     * calculate the total expenses of this category.
     * The method then prints an analysis indicating how much of the budget has been spent and whether the user is within
     * or exceeding the limits.
     * <p>
     * The analysis is displayed as follows:
     * - If the total expenses are within the budget, it shows the percentage of the budget spent and the remaining amount.
     * - If the total expenses exceed the budget, it shows the percentage and the amount over budget.
     */
    public void analytics() {
        try {
            String sql = String.format(""" 
                    Select b.budgetcategory, b.budgetamount, IFnull(SUM(ex.expenseamount), 0) as totalexpense
                    From budgets b 
                    Left join expenses ex 
                    ON b.budgetcategory = ex.expensecategory ANd ex.useremail = b.useremail 
                    where b.userEmail = '%s'
                    group by b.budgetcategory
                    """, user.email);
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String category = rs.getString("budgetCategory");
                double totalspent = rs.getDouble("totalexpense");
                double totalbudget = rs.getDouble("budgetamount");
                if (totalspent <= totalbudget) {
                    System.out.println("You spent " + totalspent + " in " + category + " category" + ", Which is " +
                            String.format("%.2f", (totalspent / totalbudget) * 100) + "% of your budget, You have " + (totalbudget - totalspent) +
                            " till you reached your budget's limit\n");
                } else if (totalspent > totalbudget) {
                    System.out.println("You spent " + totalspent + " in " + category + " category" + ", You exceeded your budget by "
                            + String.format("%.2f", (((totalspent / totalbudget) * 100) - 100)) + "%" + " which is " + (totalspent - totalbudget));
                }

            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
