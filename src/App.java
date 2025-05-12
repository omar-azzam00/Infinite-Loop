import Classes.*;

import java.util.Scanner;

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
