package Classes;

import java.util.Scanner;

public class LoginUI {
    public static AuthController auth = new AuthController();
    Scanner s = new Scanner(System.in);

    public User menu() {
        User user = auth.getCurrentUser();

        if (user == null) {
            System.out.println("Do you want to (1) login or (2) sign up ?");
            System.out.print("Enter your option (1 or 2): ");

            int opt;
            try {
                opt = s.nextInt();

            } catch (Exception e) {
                opt = -1;
            }

            System.out.println("");
            switch (opt) {
                case 1:
                    s.nextLine();
                    return showLoginForm();
                case 2:
                    s.nextLine();
                    return showSignUpForm();
                default:
                    System.err.println("Invalid Input!");
                    System.exit(1);
            }
        } else {
            System.out.println("You are logged in as " + user.email + ", do you want to (1) continue or (2) log out ?");
            System.out.print("Enter your option (1 or 2): ");

            int opt;
            try {
                opt = s.nextInt();

            } catch (Exception e) {
                opt = -1;
            }

            System.out.println("");
            switch (opt) {
                case 1:
                    s.nextLine();
                    return user;
                case 2:
                    s.nextLine();
                    auth.logOut();
                    return menu();
                default:
                    System.err.println("Invalid Input!");
                    System.exit(1);
            }
        }

        // should never be executed!
        return null;

    }

    public User showLoginForm() {
        System.out.print("Enter your email: ");
        String email = s.nextLine();
        System.out.print("Enter password: ");
        String password = s.nextLine();

        if (!validateLogin(email, password)) {
            System.exit(1);
            return null;
        }

        User user = auth.authenticate(email, password);

        System.out.println("");
        if (user == null) {
            System.err.println("Invalid credentials!");
            System.exit(1);
        }

        return user;
    }

    public User showSignUpForm() {
        System.out.print("Enter your user name: ");
        String userName = s.nextLine();
        System.out.print("Enter your email: ");
        String email = s.nextLine();
        System.out.print("Enter your mobile phone: ");
        String mobilePhone = s.nextLine();
        System.out.print("Enter password: ");
        String password = s.nextLine();

        if (!validateSignUp(userName, email, mobilePhone, password)) {
            System.exit(1);
            return null;
        }

        User user = auth.createUser(new User(userName, email, mobilePhone, password));

        System.out.println("");
        if (user == null) {
            System.err.println("Error happened while creating account!");
            System.exit(1);
        }

        return user;
    }

    public Boolean validateLogin(String email, String password) {
        if (email.isEmpty()) {
            System.out.println("");
            System.err.println("Email can't be empty!");
            return false;
        }

        if (password.length() < 6) {
            System.out.println("");
            System.err.println("password length can't be less than 6!");
            return false;
        }

        return true;
    }

    public Boolean validateSignUp(String userName, String email, String mobilePhone, String password) {
        if (userName.isEmpty()) {
            System.out.println("");
            System.err.println("user name can't be empty!");
            return false;
        }

        if (email.isEmpty()) {
            System.out.println("");
            System.err.println("Email can't be empty!");
            return false;
        }

        if (password.length() < 6) {
            System.out.println("");
            System.err.println("password length can't be less than 6!");
            return false;
        }

        return true;
    }
}
