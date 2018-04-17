package com.example.leebet_pc.saggip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmergencyContactsActivity extends AppCompatActivity {
    private List<EmergencyContactModel> emergencyContactModelList = new ArrayList<>();

    private List<EmergencyContactModel> completeContactlList = new ArrayList<>();
    private ArrayList<EmergencyContactModel> names = new ArrayList<>();

    private ArrayList<String> names_str = new ArrayList<>();
    private EmergencyContactsAdapter emergencyContactsAdapter;
    private RecyclerView recyclerView_contacts;
    private DBHelperContacts mydb;
    private static final int REQUEST_READ_CONTACTS = 444;
    private static final int CONTACT_RESULT = 69;
    private ProgressDialog pDialog;
    private Handler updateBarHandler;

    private ArrayList<EmergencyContactModel> chosenDelete = new ArrayList<>();
    private ArrayList<String> chosen = new ArrayList<>();
    private ArrayList<String> current = new ArrayList<>();

    SQLiteDatabase contacts;

    ArrayList<String> contactList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

//        contacts.execSQL("CREATE TABLE IF NOT EXISTS Contacts(Username VARCHAR,Password VARCHAR);");
        mydb = new DBHelperContacts(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contacts");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setSubtitle("Emergency Contacts");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });





        recyclerView_contacts = (RecyclerView) findViewById(R.id.recyclerView_contacts);
        emergencyContactsAdapter = new EmergencyContactsAdapter(emergencyContactModelList);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_contacts.setLayoutManager(rLayoutManager);
        recyclerView_contacts.setItemAnimator(new DefaultItemAnimator());
        recyclerView_contacts.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 0));
        recyclerView_contacts.setAdapter(emergencyContactsAdapter);

        prepareData();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CONTACT_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                chosen = data.getStringArrayListExtra("chosenContacts");
                addNewContacts();
            }
            else{
                prepareData();
            }
        }
    }
    private void addNewContacts(){
        for (String name: chosen) {
            EmergencyContactModel contact = contactExistsByName(name);
            if(contact != null){
                emergencyContactModelList.add(contact);
            }
        }
        emergencyContactsAdapter.notifyDataSetChanged();
    }
    private ArrayList<EmergencyContactModel> getDeleteContacts(){
        ArrayList<EmergencyContactModel> temp = new ArrayList<>();
        for (EmergencyContactModel model: emergencyContactModelList) {
            if(model.isSelected()){
                temp.add(model);
            }
        }
        return temp;
    }
    private EmergencyContactModel contactExistsByName(String name){
        for (EmergencyContactModel model: completeContactlList) {
            if(model.getName().equals(name)){
                return model;
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

    private void prepareData(){
        //names.add("Baby");
        //names.add("Jason Deichmann");
        //names.add("Sherleen Chua");
        emergencyContactModelList.clear();

        completeContactlList = mydb.getAllContactList();
        for (EmergencyContactModel con: completeContactlList) {
            emergencyContactModelList.add(con);
        }
        emergencyContactsAdapter.notifyDataSetChanged();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);

        MenuItem item = menu.findItem(R.id.action_add);
        Drawable icon = getResources().getDrawable(R.drawable.ic_contact_add);
        icon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        item.setIcon(icon);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:



                Intent getIntent = new Intent(EmergencyContactsActivity.this, AddContactActivity.class);
                startActivityForResult(getIntent, 69);
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
