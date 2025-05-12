package Classes;

import java.util.Scanner;

public class budgetAnalysis {
    Budget budget;
    Analysis analysis;
    private Scanner s;

    public budgetAnalysis(Budget budget, Analysis analysis) {
        this.budget = budget;
        this.analysis = analysis;
        this.s = new Scanner(System.in);
    }

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
                    budget.creat_budget();
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
                                budget.delete_budget(id);
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
