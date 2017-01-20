package com.example.user.tryapp;

/**
 * Created by user on 12/1/2016.
 */

public class List_of_items {

    String link_site;
    String comment;
    int user_id,id;

    public List_of_items( String link_site, String comment, int user_id) {
       // this.id = id;
        this.link_site = link_site;
        this.comment = comment;
        this.user_id = user_id;
    }

    public List_of_items() {
    }

    public String getLink_site() {
        return link_site;
    }

    public void setLink_site(String link_site) {
        this.link_site = link_site;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

  /*  public int getId(){ return id;}

    public void setId(int id){this.id = id;}*/
}
