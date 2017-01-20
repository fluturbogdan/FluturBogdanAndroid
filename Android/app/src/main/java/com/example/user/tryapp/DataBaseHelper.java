package com.example.user.tryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by user on 11/30/2016.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
//in cazde ceva eroare sintactica -> https://www.youtube.com/watch?v=NT1qxmqH1eM

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contacts.db";
    private static final String TABLE_NAME = "contacts";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    SQLiteDatabase db;

    private static final String TABLE_CREATE = "create table contacts (id integer primary key not null ,"+
            "name text not null , email text not null , username text not null , password text not null);";

    public DataBaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS" + TABLE_NAME;
        db.execSQL(query);
        this.onCreate(db);
    }

    public void insertContact(Contact c){
        //delete_all_from_table();
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "select * from contacts";
        Cursor cursor = db.rawQuery(query,null);
        int count = cursor.getCount();
        count++;
        values.put(COLUMN_ID,count);
        values.put(COLUMN_NAME,c.getName());
        values.put(COLUMN_USERNAME,c.getUsername());
        values.put(COLUMN_EMAIL,c.getEmail());
        values.put(COLUMN_PASSWORD,c.getPassword());

        db.insert(TABLE_NAME, null, values);
        db.close();

    }

    public void delete_all_from_table(){
        db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null,null);

    }

    public String searchUsername(String username) {
        db = this.getReadableDatabase();
        String query = "select username from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        String db_username, username_satus = "not found";

        if(cursor.moveToFirst())
        {
            do {

                db_username = cursor.getString(0);
                if(db_username.equals(username)) {
                    username_satus = "Found it must choose another username";
                    break;
                }
            }while(cursor.moveToNext());
        }
        db.close();
        return  username_satus;
    }

    public String searchPass(String username, String input_password) {
        //search for password
        db = this.getReadableDatabase();
        String query = "select username,password,id from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        String db_username, db_password,db_id="not found";


        if(cursor.moveToFirst())
        {
            do {

                db_username = cursor.getString(0);
                db_password = cursor.getString(1);
                if(db_username.equals(username) && db_password.equals(input_password)) {
                    db_id = cursor.getString(2).toString();
                    break;
                }
            }while(cursor.moveToNext());
        }
        db.close();
        return  db_id;
    }

    public void printUsers(ArrayList<Contact> to_print){
        for(int i=0;i<to_print.size();i++)
        {
            System.out.println("USERNAME: "+ to_print.get(i).getUsername() + "   --->  PASSWORD: " + to_print.get(i).getPassword());
        }

    }

    public ArrayList<Contact> selectAllUsers(){
        //fucntie de verifacare
        //sa vad ca se adauga otul in BD
        //apeleaza si o functie ce printeaza ARrayLis<Contact> mai sus definita
        db = this.getReadableDatabase();
        String query = "select username,password,email,name from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);


        ArrayList<Contact> users = new ArrayList<Contact>();

        if (cursor .moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                String username = cursor.getString(0);
                String password = cursor.getString(1);
                System.out.println(username);
                String email = cursor.getString(2);
                String name = cursor.getString(3);

                Contact user = new Contact();
                user.setUsername(username);
                user.setPassword(password);
                user.setName(name);
                user.setEmail(email);

                users.add(user);
                cursor.moveToNext();
            }
        }
        printUsers(users);
        db.close();
        return users;
    }
}
