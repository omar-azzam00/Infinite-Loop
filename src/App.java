import Classes.User;
import Classes.Budget_Analysis;
import Classes.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Auth auth = new Auth();
        User user = auth.run();

        if (user != null) {
            System.out.println("What page do you want?\n 1. Budget & Analysis Page\n 2. Expense Tracking Page\n 3. Reminder Page\n");
            Scanner choice = new Scanner(System.in);
            int choiceInt = choice.nextInt();
            switch (choiceInt) {
                case 1:
                    Budget_Analysis budget_analysis = new Budget_Analysis(user, auth.getConnection());
                    budget_analysis.menu();
                    break;
                case 2:
                    break;
                case 3:
                    ReminderController reminderController = new ReminderController(user, auth.getConnection());
                    reminderController.menu();
                    break;
                default:
            }
        }
    }
}
