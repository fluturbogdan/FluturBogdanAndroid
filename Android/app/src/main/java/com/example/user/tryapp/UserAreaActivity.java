package com.example.user.tryapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserAreaActivity extends AppCompatActivity {

    TextView etUserId;
    String id;
    int index_elemnt_for_update;

    EditText etComment, etSite;

    ArrayList<List_of_items> list_of_items = null;// = new ArrayList<List_of_items>();
    SitesAdapter adapter_db_items, adapter_server_items;
    ArrayList<List_of_items> list_items_server;
    HashMap<String, String> contact_for_update = new HashMap<>();

    private DataBaseHelperForListOfItems db_helper_list = new DataBaseHelperForListOfItems(this);//for working with db

    private SimpleAdapter simple_adapter;
    private String TAG = UserAreaActivity.class.getSimpleName();
    ListView lvBookmarkedSites;
    private ProgressDialog pDialog;
    private static String url = "http://10.10.100.22:80/mobile/BringSites.php";
    ArrayList<HashMap<String, String>> sitesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        //etUserId = (TextView) findViewById(R.id.etUsername);
//-----------------------------------------------------------------------
        lvBookmarkedSites = (ListView) findViewById(R.id.lvSites);
        sitesList = new ArrayList<>();
//-----------------------------------------------------------------------
        etComment = (EditText) findViewById(R.id.etComment);
        etSite = (EditText) findViewById(R.id.etSite);
//-----------------------------------------------------------------------
        //TextView site_link_from_list = (TextView) findViewById(R.id.site_link);
        //site_link_from_list.setMovementMethod(LinkMovementMethod.getInstance());
//-----------------------------------------------------------------------
        Intent intent = getIntent();
        //String name = intent.getStringExtra("name");
        //String username = intent.getStringExtra("username");
        id = intent.getStringExtra("id");//id ce vine de la login -> user actual logat
        System.out.println("id de la login => " + id);
       // url = url +"?id=" + id;
        db_delete_selection();
        uptade_selection();//doar adauga elementele selectate in text fields

        //do something with that info
        Boolean check_network_availability_status = isNetworkAvailable();
        if (check_network_availability_status == false){
            /*List_of_items li_mock = new List_of_items();
            li_mock.setLink_site("www.cs.ubbcluj.ro");
            li_mock.setComment("facultatea la care studiez");
            li_mock.setUser_id(Integer.parseInt(id));*/
            request_from_db_list_sites_and_comments(id);
            //db_insert_site_comment(li_mock);
            //populatingListView(li_mock);

        }else {

            bring_info_server();
        }
       // delete_selection();
    }


    public boolean isNetworkAvailable() {
        //checking for internet
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void request_from_db_list_sites_and_comments(String id){

        list_of_items = db_helper_list.selectSitesAndCommentsByUserId(id);
        // Create the adapter to convert the array to views
         adapter_db_items = new SitesAdapter(this, list_of_items);
        // Attach the adapter to a ListView
        lvBookmarkedSites.setAdapter(adapter_db_items);
    }

    public void populatingListView(List_of_items li){
        System.out.println("Am intrat metoda pe populare LV !!!");
        System.out.println("Comment nou de pus in LV: " + li.getComment());
        adapter_db_items.add(li);
        adapter_db_items.notifyDataSetChanged();
        System.out.println("Am iesit metoda pe populare LV !!!");
        //lvBookmarkedSites.setAdapter(adapter_db_items);
        // Or even append an entire new collection
        // Fetching some data, data has now returned
        // If data was JSON, convert to ArrayList of User objects.
        //JSONArray jsonArray = ...;
        //ArrayList<List_of_items> newUsers = User.fromJson(jsonArray)
       // adapter.addAll(newUsers);
    }

    public void db_insert_site_comment(List_of_items li){
        //db_helper_list.delete_all_from_table();
        db_helper_list.insertSiteAndComment(li);
    }

    public void insert_lv_server_side(){
        List_of_items li = new List_of_items();

        String site = etSite.getText().toString();//luate de la campurile de input
        String comment = etComment.getText().toString();//luate de la campurile de input

        li.setUser_id(Integer.parseInt(id));//id user curent
        li.setComment(comment);
        li.setLink_site(site);
        try {
            adapter_server_items.add(li);//adaugam in adaptor
            adapter_server_items.notifyDataSetChanged();// notficam pentru schimabrrile facute
        }catch (NullPointerException e){

        }

        HashMap<String, String> contact = new HashMap<>();//asta ca sa putem sa apelam request senderul pentru server
        try {
            for (int i = 0; i < adapter_server_items.getCount(); i++) {
                contact.put("link_site", adapter_server_items.getItem(i).getLink_site());
                contact.put("comment", adapter_server_items.getItem(i).getComment());
                contact.put("user_id", id);
            }
        }catch (NullPointerException as){

        }
        send_server_list(contact);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        //makes visible the icon on the layout
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public void offline_insert(List_of_items li)
    {
        if ((etSite.getText().toString().equals("") || etComment.getText().toString().equals("")) && (etSite.getText().toString().length() == 0 || etComment.getText().toString().length() == 0)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
            builder.setMessage("Fields can't be empty! ")
                    .setNegativeButton("Retry", null)
                    .create()
                    .show();
        } else {
            populatingListView(li);
            db_insert_site_comment(li);
            etSite.setText("");
            etComment.setText("");
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        HashMap<String, String> dict= null;// = new HashMap<String, String>();
        List_of_items li = new List_of_items();
        li.setLink_site(etSite.getText().toString());
        li.setComment(etComment.getText().toString());
        li.setUser_id(Integer.parseInt(id));
        switch (item.getItemId()) {
            case R.id.add_site_and_comment: {
                Boolean check_network_availability_status = isNetworkAvailable();
                if (check_network_availability_status == false) {
                    //offline support
                    offline_insert(li);

                } else {
                    List_of_items li_2 = new List_of_items();
                    li_2.setLink_site(etSite.getText().toString());
                    li_2.setComment(etComment.getText().toString());
                    li_2.setUser_id(Integer.parseInt(id));
                    insert_lv_server_side();//insertul pentru SERVER side
                    //offline_insert(li);//asta pentru sincronizare server BD SQLite
                    db_helper_list.insertSiteAndComment(li_2);
                    etSite.setText("");
                    etComment.setText("");
                }

                return true;
            }
            case R.id.send_email: {
                sendEmail();
            }
            case R.id.update: {
                on_press_update_btn();
            }
            case R.id.clear_all:
                //db_clear_all();
            case R.id.display_chart: {
                Intent intent = new Intent(getBaseContext(), DisplayChart.class);
                startActivity(intent);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void db_clear_all(){
        adapter_db_items.clear();
        db_helper_list.delete_all_from_table();
    }

    private void on_press_update_btn() {
        Boolean check_network_availability_status = isNetworkAvailable();
        if (check_network_availability_status == false) {
            List_of_items li_to_update = new List_of_items();
            li_to_update.setLink_site(etSite.getText().toString());
            li_to_update.setComment(etComment.getText().toString());
            li_to_update.setUser_id(Integer.parseInt(id));
            Boolean status = db_helper_list.update_row(li_to_update, Integer.toString(index_elemnt_for_update));
            System.out.println("STATUS update query -> " + status);
            //adapter_db_items.add(li_to_update);//adaugam in adaptor
            //adapter_db_items.notifyDataSetChanged();// notficam pentru schimabrrile facute
            request_from_db_list_sites_and_comments(id);
        }else{
            contact_for_update.put("user_id", id);
            contact_for_update.put("link_site", etSite.getText().toString());
            contact_for_update.put("comment", etComment.getText().toString());
            request_server_to_update(contact_for_update);
            List_of_items li = new List_of_items();
            li.setUser_id(Integer.parseInt(id));//id user curent
            li.setComment(etComment.getText().toString());
            li.setLink_site(etSite.getText().toString());
            adapter_server_items.add(li);//adaugam in adaptor
            adapter_server_items.notifyDataSetChanged();// notficam pentru schimabrrile facute

            //for syncing purpose only
            Boolean status = db_helper_list.update_row(li, Integer.toString(index_elemnt_for_update));
            System.out.println("STATUS update query from local DB ->  " + status);
        }
        etSite.setText("");
        etComment.setText("");

    }
    private void uptade_selection(){
        lvBookmarkedSites.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int arg2, long arg3) {

                Boolean check_network_availability_status = isNetworkAvailable();
                List_of_items list_of_item = (List_of_items) lvBookmarkedSites.getItemAtPosition(arg2);
                etSite.setText(list_of_item.getLink_site());
                etComment.setText(list_of_item.getComment());

                if (check_network_availability_status == false) {
                    index_elemnt_for_update = arg2 + 1;
                    list_of_items.remove(arg2);// stergem elementul
                    adapter_db_items.notifyDataSetChanged();
                }else{
                    index_elemnt_for_update = arg2 + 1; //only for syncimg purposes
                    contact_for_update.put("old_link_site", list_of_item.getLink_site());
                    contact_for_update.put("old_comment", list_of_item.getComment());
                    list_items_server.remove(arg2);//stergelemetul
                    adapter_server_items.notifyDataSetChanged();
                }
            }

        });
    }

   /* public void sync_offline_online(List_of_items li){
        ArrayList<List_of_items> all_comments_sites = db_helper_list.selectSitesAndCommentsByUserId(id);
        for(int i = 0; i< all_comments_sites.size(); i++)
        {
            if(if_2_li_equal(all_comments_sites.get(i),li)== true)
            {

            }
        }
    }*/
    private void db_delete_selection() {

        lvBookmarkedSites.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int arg2, long arg3) {

                // Can't manage to remove an item here
                Boolean check_network_availability_status = isNetworkAvailable();
                if (check_network_availability_status == false) {
                    list_of_items.remove(arg2);//where arg2 is position of item you click
                    System.out.println("id element long press" + arg2);
                    adapter_db_items.notifyDataSetChanged();
                    List_of_items item_to_delete = db_helper_list.searchId(arg2 + 1);
                    db_helper_list.delete_one_row(item_to_delete);
                    return false;
                } else {
                    List_of_items item_to_delete = db_helper_list.searchId(arg2 + 1);
                    db_helper_list.delete_one_row(item_to_delete);
                    HashMap<String, String> contact = new HashMap<>();
                    List_of_items list_of_items = (List_of_items) lvBookmarkedSites.getItemAtPosition(arg2);
                    contact.put("link_site", list_of_items.getLink_site());
                    contact.put("comment", list_of_items.getComment());
                    contact.put("user_id", id);
                    request_server_to_delete(contact);
                    list_items_server.remove(arg2);
                    adapter_server_items.notifyDataSetChanged();
                    return false;
                }
            }
        });
    }

    public void request_server_to_update(HashMap<String, String> dict) {
        //send server request to delete one entry
        String CREATE_URL = "http://10.10.100.22:80/mobile/Update.php";
        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST,CREATE_URL,dict,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            System.out.println("status " + status);
                            if(status == true){
                                Log.d(TAG, "Status is true -> was updated from BD server");
                                Toast.makeText(UserAreaActivity.this, "The selected item was updated :)", Toast.LENGTH_SHORT).show();

                            }else
                            {
                                Log.d(TAG, "Status is FALSE -> the selected item was NOT updated from SERVER DB!!!");
                                Toast.makeText(UserAreaActivity.this, "The selected item was NOT permanently updated.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                            builder.setMessage(e.toString())
                                    .setNegativeButton("Retry",null)
                                    .create()
                                    .show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                builder.setMessage(error.toString())
                        .setNegativeButton("Retry",null)
                        .create()
                        .show();
            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    public void request_server_to_delete(HashMap<String, String> dict) {
        //send server request to delete one entry

        String CREATE_URL = "http://10.10.100.22:80/mobile/Delete.php";
        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST,CREATE_URL,dict,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if(status == true){
                                Log.d(TAG, "Status is true -> was deleted from BD server");
                                Toast.makeText(UserAreaActivity.this, "The selected item was deleted.", Toast.LENGTH_SHORT).show();

                            }else
                            {
                                Log.d(TAG, "Status is FALSE -> the selected item was not delete from SERVER DB!!!");
                                Toast.makeText(UserAreaActivity.this, "The selected item was NOT permanently deleted.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                            builder.setMessage(e.toString())
                                    .setNegativeButton("Retry",null)
                                    .create()
                                    .show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                builder.setMessage(error.toString())
                        .setNegativeButton("Retry",null)
                        .create()
                        .show();
            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    public void send_server_list(HashMap<String, String> dict) {
        //sends a to server for saving, before that must collect the id_user

        String user_id = id;//de bagat in map
        dict.put("user_id",user_id);
        String CREATE_URL = "http://10.10.100.22:80/mobile/Create.php";
        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST,CREATE_URL,dict,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");
                            if(status == true){
                                Log.d(TAG, "Status is true -> was added in BD from server");
                                Toast.makeText(UserAreaActivity.this, "The item was added.", Toast.LENGTH_SHORT).show();
                            }else{
                                Log.d(TAG, "Status is true -> was NOT added in BD from server");
                                Toast.makeText(UserAreaActivity.this, "The item was NOT added.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                            builder.setMessage(e.toString())
                                    .setNegativeButton("Retry",null)
                                    .create()
                                    .show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                builder.setMessage(error.toString())
                        .setNegativeButton("Retry",null)
                        .create()
                        .show();
            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);


    }


    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {""};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.d(TAG,"Finished sending email...");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(UserAreaActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Async task class to get json by making HTTP call
     */
    /*
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(UserAreaActivity.this);
            pDialog.setMessage("Please wait to bring the list");
            pDialog.setCancelable(false);
            pDialog.show();
            if(pDialog != null)
                pDialog.dismiss();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            List_of_items site = new List_of_items();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            list_items_server = new ArrayList<List_of_items>();
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("sites");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        //String id = c.getString("id");
                        String link_site = c.getString("link_site");
                        String comment = c.getString("comment");

                         site.setLink_site(c.getString("link_site"));
                         site.setComment(c.getString("comment"));
                         site.setUser_id(Integer.parseInt(id));

                        list_items_server.add(site);
                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        //contact.put("id", id);
                        contact.put("link_site", link_site);
                        contact.put("comment", comment);
                        //contact.put("mobile", mobile);

                        // adding contact to contact list
                        sitesList.add(contact);

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            /*if (pDialog.isShowing())
                pDialog.dismiss();*/
            /**
             * Updating parsed JSON data into ListView
             * */
          /*  adapter = new SimpleAdapter(
                    UserAreaActivity.this, sitesList,
                    R.layout.list_item, new String[]{"link_site", "comment"}, new int[]{R.id.site_link,
                    R.id.comment});


            adapter_server_items = new SitesAdapter(UserAreaActivity.this,list_items_server);
            lvBookmarkedSites.setAdapter(adapter); //var ant
            lvBookmarkedSites.setAdapter(adapter_server_items);
        }

    }*/
            public ArrayList<List_of_items> get_all_from_db_local(){
                return db_helper_list.selectSitesAndCommentsByUserId(id);
            }

     public boolean if_2_li_equal(List_of_items li1, List_of_items li2){
        if(li1.getUser_id() == li2.getUser_id() && li1.getComment().equals(li2.getComment()) && li1.getLink_site().equals(li2.getLink_site())){
            return true;
        }
        return false;
     }
     public ArrayList<List_of_items> compare_2_lists(ArrayList<List_of_items> list){
         if(list.size() == get_all_from_db_local().size()){
             for(int i = 0; i< get_all_from_db_local().size();i++)
             {
                 if(if_2_li_equal(get_all_from_db_local().get(i),list.get(i))== false){
                    list.get(i).setUser_id(get_all_from_db_local().get(i).getUser_id());
                    list.get(i).setComment(get_all_from_db_local().get(i).getComment());
                    list.get(i).setLink_site(get_all_from_db_local().get(i).getLink_site());

                 }
             }
             return list;
         }else{return get_all_from_db_local();}

     }

     public void rqst_update_server_for_syncing(ArrayList<List_of_items> li, ArrayList<List_of_items> li2){
         for(int i = 0; i<li2.size();i++) {
             HashMap<String, String> dict = new HashMap<String, String>();
             dict.put("user_id", id);
             dict.put("link_site", li2.get(i).getLink_site());
             dict.put("comment", li2.get(i).getComment());
             send_server_list(dict);
         }

         for(int i = 0; i<li.size();i++) {
             HashMap<String, String> dict2 = new HashMap<String, String>();
             dict2.put("user_id", id);
             dict2.put("link_site", li.get(i).getLink_site());
             dict2.put("comment", li.get(i).getComment());
             request_server_to_delete(dict2);//bug
         }
     }
     public ArrayList<List_of_items> make_clone(ArrayList<List_of_items> to_clone){
         ArrayList<List_of_items> clone = new ArrayList<List_of_items>();
         for(int i = 0; i<to_clone.size();i++) {
            clone.add(to_clone.get(i));
         }
         return clone;
     }
     public void bring_info_server(){
                //getting the info from input form

                final List_of_items site_for_server_rqst = new List_of_items();
                final ArrayList<List_of_items> partial_list_items_server = new  ArrayList<List_of_items>();
                String GET_URL = "http://10.10.100.22:80/mobile/BringSites.php";//"http://localhost:8082/exemple/appAndroid/Login.php";//"http://192.168.0.102/exemple/appAndroid/Login.php";//"http://localhost:8082/exemple/appAndroid/Login.php";"http://10.0.2.2/exemple/appAndroid/Login.php";
                Map<String,String> params = new HashMap<String,String>();
                params.put("id",id);
                System.out.println("id din bring sites " + id);
                final CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, GET_URL, params,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        // Getting JSON Array node
                                        JSONArray contacts = response.getJSONArray("sites");
                                        // looping through All Contacts
                                        for (int i = 0; i < contacts.length(); i++) {
                                            JSONObject c = contacts.getJSONObject(i);
                                            /*site_for_server_rqst.setLink_site(c.getString("link_site"));
                                            site_for_server_rqst.setComment(c.getString("comment"));
                                            System.out.println("comments from sites " + site_for_server_rqst.getComment());
                                            site_for_server_rqst.setUser_id(Integer.parseInt(id));*/
                                            String site = c.getString("link_site");
                                            String comment = c.getString("comment");
                                            int id_2 = Integer.parseInt(id);
                                            partial_list_items_server.add(new List_of_items(site,comment,id_2));
                                        }
                                        list_items_server = new ArrayList<List_of_items>();
                                        ArrayList<List_of_items> list_for_old_values = make_clone(partial_list_items_server);
                                        list_items_server = compare_2_lists(partial_list_items_server);//for syncing purposes only
                                        rqst_update_server_for_syncing(list_for_old_values,list_items_server);

                                        for (int i = 0 ;i < list_items_server.size();i++){
                                            System.out.println("lista dupa modificari  " + list_items_server.get(i).getComment());
                                        }
                                        adapter_server_items = new SitesAdapter(UserAreaActivity.this,list_items_server);
                                        lvBookmarkedSites.setAdapter(adapter_server_items);
                                    } catch (JSONException e) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                                        builder.setMessage(e.toString())
                                                .setNegativeButton("Retry", null)
                                                .create()
                                                .show();
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                            builder.setMessage(error.toString())
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                        }
                    });
                    jsonObjectRequest.setShouldCache(false);
                    Volley.newRequestQueue(this).add(jsonObjectRequest);

                }



}
