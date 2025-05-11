import Classes.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        LoginUI loginUI = new LoginUI();
        System.out.println("\nWelcome to Infinite Loop!\n");
        User user = loginUI.menu();

        if (user != null) {
            System.out.println("What page do you want?\n 1. Budget & Analysis Page\n 2. Expense Tracking Page\n 3. Reminder Page\n 4. Income Tracking Page\n");
            Scanner choice = new Scanner(System.in);
            int choiceInt = choice.nextInt();
            switch (choiceInt) {
                case 1:
                    Budget_Analysis budget_analysis = new Budget_Analysis(user, loginUI.auth.getConnection());
                    budget_analysis.menu();
                    break;
                case 2:
                    break;
                case 3:
                    ReminderController reminderController = new ReminderController(user, loginUI.auth.getConnection());
                    reminderController.menu();
                    break;
                case 4:
                    IncomeController incomeController = new IncomeController(user, loginUI.auth.getConnection());
                    incomeController.menu();
                    break;
                default:
            }
        }
    }
}
