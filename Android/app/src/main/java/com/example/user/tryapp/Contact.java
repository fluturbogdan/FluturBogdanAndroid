package com.example.user.tryapp;

/**
 * Created by user on 11/30/2016.
 */

public class Contact {
    //int id;
    String name,username,email,password;

    public Contact( String name, String username, String email, String password) {
       // this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Contact() {

    }

   /*public void setId(int id) {
        this.id = id;
    }*/

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

  /*  public int getId() {

        return id;
    }*/

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
