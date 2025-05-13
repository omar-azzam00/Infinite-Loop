package com.infinite_loop.Classes;


import java.util.Scanner;


/**
 * The LoginUI class manages the user interface for login and sign-up flow.
 * It interacts with the AuthController to facilitate authentication including: login, sign-up, user validation, and session management.
 */
public class LoginUI {
    public static AuthController auth = new AuthController();
    Scanner s = new Scanner(System.in);

    /**
     * This method is the main method in the class which should be called.
     * It starts the authentication flow of the program.
     * 
     * Note that in case of any invalid input this method will exit the program with
     * status code 1
     * 
     * @return user object containing user's data
     */
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

    /**
     * This method starts the login flow by asking the user for his email and his
     * password
     * 
     * Note that in the case of invalid credentials this method will exit the
     * program with status code 1
     * 
     * @return user object containing user's data
     */
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

    /**
     * This method starts the sign up flow by asking the user for his data
     * (userName, email, mobilePhone, password)
     * 
     * Note that in the case of any invalid input this method will exit the program
     * with status code 1
     * 
     * @return user object containing user's data
     */
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

    /**
     * Check if the email and the password are valid for login.
     * 
     * @param email    For the email to be valid it shouldn't be empty
     * @param password For the password to be valid its length should be at least 6
     *                 characters
     * 
     * @return true if the parameters are valid, false otherwise.
     */
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

    /**
     * Check if the user object data are valid to create an account.
     * 
     * @param userName    For the userName to be valid it should be unique and not
     *                    empty.
     * @param email       For the email to be valid it should be unique and not
     *                    empty.
     * @param mobilePhone
     *                    Any mobilePhone is valid even if its empty as the user may
     *                    not want to add a mobilePhone.
     * @param password    For the password to be valid its length should be at least
     *                    6 characters
     * 
     * @return true if the parameters are valid, false otherwise.
     */
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
