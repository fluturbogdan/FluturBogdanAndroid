package com.example.user.tryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by user on 12/1/2016.
 */

public class DataBaseHelperForListOfItems extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sites.db";
    private static final String TABLE_NAME = "sites";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LINK_SITE = "link_site";
    private static final String COLUMN_COMMENT = "comment";
    private static final String COLUMN_USER_ID = "user_id";

    SQLiteDatabase db;

    private static final String TABLE_CREATE = "create table sites (id integer primary key autoincrement not null,"+
            "link_site text not null , comment text not null , user_id integer not null);";

    public DataBaseHelperForListOfItems(Context context){
        //mai creez un db
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    //onCreate and onUpdate genrate automat sunt un must!!!
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

    public void delete_all_from_table(){
        db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null,null);

    }

    public void delete_one_row(List_of_items it){
        System.out.print("Am ajuns aici");
        db = this.getWritableDatabase();
        db.execSQL("delete from "+TABLE_NAME+" where comment='" + it.getComment() +"' and link_site= '"+ it.getLink_site() +"'");
        System.out.print("Am sters site -> "+it.getLink_site());
        db.close();
    }

    public void printSites(ArrayList<List_of_items> to_print){
        for(int i=0;i<to_print.size();i++)
        {
            System.out.println("SITE: "+ to_print.get(i).getLink_site() + "   --->  COMMENT: " + to_print.get(i).getComment());
        }

    }

    public ArrayList<List_of_items> selectSitesAndCommentsByUserId(String id){
        //fucntie de verifacare
        //sa vad ca se adauga otul in BD
        //apeleaza si o functie ce printeaza ARrayLis<List_of_items> mai sus definita
        db = this.getReadableDatabase();
        String query = "select link_site,comment,user_id,id from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);

        //converting the id to int
        int user_id = Integer.parseInt(id);
        ArrayList<List_of_items> sites = new ArrayList<List_of_items>();

        if (cursor .moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                String link_site = cursor.getString(0);
                String comment = cursor.getString(1);
                //System.out.println(link_site);
                int db_id_user = cursor.getInt(2);
                int db_id = cursor.getInt(3);
                if (db_id_user == user_id) {

                    List_of_items site = new List_of_items();

                    site.setLink_site(link_site);
                    site.setComment(comment);
                    site.setUser_id(db_id_user);
                    //site.setId(db_id);
                    sites.add(site);
                }
                cursor.moveToNext();
            }
        }
        printSites(sites);
        db.close();
        return sites;
    }

    public void insertSiteAndComment(List_of_items li){
        //delete_all_from_table();
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "select * from sites";
        Cursor cursor = db.rawQuery(query,null);
       /* int count = cursor.getCount();
        System.out.println("COUNT din bd => " + count);
        count++;
        System.out.println("COUNT din bd AFTER => " + count);*/
        //values.put(COLUMN_ID,li.getId());
        values.put(COLUMN_LINK_SITE,li.getLink_site());
        values.put(COLUMN_COMMENT,li.getComment());
        values.put(COLUMN_USER_ID,li.getUser_id());

        db.insert(TABLE_NAME, null, values);
        db.close();

    }

    public boolean update_row(List_of_items list_to_update, String id){
        db = this.getWritableDatabase();
        System.out.println("ID primit in update_row -> " + id);
        //Cursor cur = db.rawQuery("update " + TABLE_NAME
         //       + " set comment='"+list_to_update.getComment()+"'and link_site='"+ list_to_update.getLink_site() +"' where id='" + id + "'",null);
       // db.close();
        ContentValues args = new ContentValues();
        args.put(COLUMN_LINK_SITE, list_to_update.getLink_site());
        args.put(COLUMN_COMMENT, list_to_update.getComment());
        //Boolean status =
        db.update(TABLE_NAME,args,"id = ?",new String[] { id });//db.update(TABLE_NAME, args, COLUMN_ID + "=" + id, null) > 0;
        System.out.println("Comment val for update => " +  list_to_update.getComment());
        //System.out.println("Status for update => " +  status);
        //List_of_items li_object = searchId(id);
        return true;
    }

    public List_of_items searchId(int id) {
        db = this.getReadableDatabase();
        String query = "select id,link_site,comment,user_id from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        //String db_id="not found";
        List_of_items li_object = new List_of_items();
        int db_id;
        System.out.println("id primit de userArea - > " + id);

        if(cursor.moveToFirst())
        {
            do {

                db_id = cursor.getInt(0);
                System.out.println("ID" + db_id);
                if(db_id == id) {
                    //li_object.setId(db_id);
                    li_object.setLink_site(cursor.getString(1));
                    li_object.setComment(cursor.getString(2));
                    li_object.setUser_id(cursor.getInt(3));
                    System.out.println("Am sa trimit elementul asta  => "+ li_object.getComment() + "the id is ->" + db_id);
                    break;
                }
            }while(cursor.moveToNext());
        }
        db.close();
        return  li_object;
    }


}
