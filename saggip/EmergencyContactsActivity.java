package com.example.leebet_pc.saggip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private DBHelper mydb;
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

        mydb = new DBHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Emergency Contacts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(EmergencyContactsActivity.this, AddContactActivity.class);
                startActivityForResult(getIntent, 69);
            }
        });
        FloatingActionButton delete = (FloatingActionButton) findViewById(R.id.del);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenDelete = getDeleteContacts();
                for(EmergencyContactModel con: chosenDelete){
                    mydb.deleteContact(con.getNumber());
                }
                emergencyContactsAdapter.notifyDataSetChanged();
                prepareData();
            }
        });
        recyclerView_contacts = (RecyclerView) findViewById(R.id.recyclerView_contacts);
        emergencyContactsAdapter = new EmergencyContactsAdapter(emergencyContactModelList);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_contacts.setLayoutManager(rLayoutManager);
        recyclerView_contacts.setItemAnimator(new DefaultItemAnimator());
        recyclerView_contacts.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
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
/*
        for (EmergencyContactModel con : names){
            names_str.add(con.getName());
        }
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                String photo = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        int phoneType = pCur.getInt(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.TYPE));
                        String phoneNumber = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        switch (phoneType) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                Log.e(name + "(mobile number)", phoneNumber + photo);
                                EmergencyContactModel emergencyContactModel = new EmergencyContactModel(name, phoneNumber, photo);
                               if (names_str.contains(name)) {
                                   emergencyContactModelList.add(emergencyContactModel);
                                }
                                completeContactlList.add(emergencyContactModel);
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                Log.e(name + "(home number)", phoneNumber);
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                Log.e(name + "(work number)", phoneNumber);
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                Log.e(name + "(other number)", phoneNumber);
                                break;
                            default:
                                break;
                        }
                    }
                    pCur.close();
                }
            }
        }

*/
    }
}
