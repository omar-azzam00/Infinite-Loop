package com.infinite_loop.Classes;


/**
 * The User class represents a user with basic information such as username, email, mobile phone number, and password.
 */
public class User {
    public String userName;
    public String email;
    public String mobilePhone;
    public String password;

    public User(String userName, String email, String mobilePhone, String password) {
        this.userName = userName;
        this.email = email;
        this.mobilePhone = mobilePhone;
        this.password = password;
    }
}