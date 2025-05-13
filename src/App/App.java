package App;

import Classes.*;

import java.util.Scanner;

/**
 * The App.App class represents the home page for our application.
 * It initializes the application and manages the main menu where users can navigate through various system functionalities
 * such as Budget and Analysis, Expense Tracking, Reminder, and Income Tracking.
 * The main menu runs in a loop until the user exits.
 * <p>
 * The program starts by presenting the Login interface allowing users to login or create an account.
 * After authentication, users can navigate between the available options.
 * <p>
 * Menu Features:
 * - Budget and Analysis: Access budgeting and financial analysis.
 * - Expense Tracking: Track and manage expenses.
 * - Reminder: Manage reminders.
 * - Income Tracking: Manage records of income.
 * - Exit: Close the application.
 */
public class App {
    public static void main(String[] args) throws Exception {
        LoginUI loginUI = new LoginUI();
        System.out.println("\nWelcome to Infinite Loop!\n");
        User user = loginUI.menu();
        while (true) {
            if (user != null) {
                System.out.println("What page do you want?\n 1. Budget & Analysis Page\n 2. Expense Tracking Page\n 3. Reminder Page\n 4. Income Tracking Page\n 5. Exit\n");
                Scanner choice = new Scanner(System.in);
                int choiceInt = choice.nextInt();
                switch (choiceInt) {
                    case 1:
                        Budget budget = new Budget(user, LoginUI.auth.getConnection());
                        Analysis analysis = new Analysis(user, LoginUI.auth.getConnection());
                        budgetAnalysis budgetAnalysis = new budgetAnalysis(budget, analysis);
                        budgetAnalysis.menu();
                        break;
                    case 2:
                        Expense expense = new Expense(user, LoginUI.auth.getConnection());
                        expense.menu();
                        break;
                    case 3:
                        ReminderController reminderController = new ReminderController(user, loginUI.auth.getConnection());
                        reminderController.menu();
                        break;
                    case 4:
                        IncomeController incomeController = new IncomeController(user, loginUI.auth.getConnection());
                        incomeController.menu();
                        break;
                    case 5:
                        System.exit(0);
                        break;
                    default:
                }
            }
        }
    }
}
