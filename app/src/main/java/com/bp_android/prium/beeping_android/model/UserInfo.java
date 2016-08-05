package com.bp_android.prium.beeping_android.model;

import java.io.Serializable;

/**
 * Created by Vaibhav on 2/25/16.
 */
public class UserInfo implements Serializable{

    private int id;
    private String lastName;
    private String firstName;
    private String birthday;
    private String email;
    private String telephone;
    private int picture_account;

    public UserInfo(String lastName, String firstName, String birthday, String email, String telephone, int picture_account) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.email = email;
        this.telephone = telephone;
        this.picture_account = picture_account;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }



}
