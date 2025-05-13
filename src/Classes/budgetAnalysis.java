package Classes;

import java.util.Scanner;

public class budgetAnalysis {
    Budget budget;
    Analysis analysis;
    private Scanner s;

    /**
     * Constructor for the budgetAnalysis class that initializes the budget, analysis, and sets up a Scanner for user input.
     *
     * @param budget   the budget object to be managed or analyzed
     * @param analysis the analysis object to perform data analysis
     */
    public budgetAnalysis(Budget budget, Analysis analysis) {
        this.budget = budget;
        this.analysis = analysis;
        this.s = new Scanner(System.in);
    }

    /**
     * Displays the main menu for the budget app allowing the user to manage budgets or perform analysis.
     * <p>
     * Depending on the user's choice this method provides the following options:
     * - Analysis: Accesses analysis operations.
     * - Budget: Provides another options for creating, editing, deleting, or displaying budgets.
     * <p>
     * Other options for managing budgets include:
     * 1. Create Budget: Creates a new budget and stores it in the database.
     * 2. Edit Budget: Allows the user to modify an existing budget by its unique ID.
     * 3. Delete Budget: Removes an existing budget identified by its unique ID.
     * 4. Display Budgets: Lists all registered budgets in the system.
     * <p>
     * For budget management requiring an ID (Edit/Delete), this method validates the user-provided ID
     * to ensure an existing budget is being modified or removed.
     */
    public void menu() {
        System.out.println("What do you want?\n 1. Analysis\n 2. Budget\n");
        int option2 = s.nextInt();
        s.nextLine();
        if (option2 == 2) {
            System.out.println(" 1. Creat Budget\n 2. Edit Budget\n 3. Delete Budget\n 4. Display Budgets\n");
            int option = s.nextInt();
            s.nextLine();
            switch (option) {
                case 1:
                    budget.creatBudget();
                    break;
                case 2:
                    try {
                        while (true) {
                            System.out.println("Please enter the ID you want to edit: ");
                            String id = s.nextLine().trim();
                            boolean check = budget.validate_id(id);
                            if (check) {
                                budget.edit_budgetTable(id);
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
                            boolean check = budget.validate_id(id);
                            if (check) {
                                budget.deleteBudget(id);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    break;
                case 4:
                    budget.display_budget();
                    break;
            }
        } else if (option2 == 1) {
            analysis.analytics();
        }
    }
}
