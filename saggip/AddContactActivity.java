package com.example.leebet_pc.saggip;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class AddContactActivity extends AppCompatActivity {
    private ArrayList<EmergencyContactModel> emergencyContactModelList = new ArrayList<>();
    private ArrayList<EmergencyContactModel> filteredList = new ArrayList<>();

    private ArrayList<String> chosen = new ArrayList<>();
    private ArrayList<String> current = new ArrayList<>();

    private EmergencyContactsAdapter emergencyContactsAdapter;

    private static final int REQUEST_READ_CONTACTS = 444;
    private static final int CONTACT_RESULT = 69;
    private ProgressDialog pDialog;
    private Handler updateBarHandler;
    private String numbers = "";

    ArrayList<String> contactList;
    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);



        final EditText et = (EditText) findViewById(R.id.search);



        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // big bad betboi

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }

        });


        mydb = new DBHelper(this);
        List<EmergencyContactModel> currentEmergencyContactModelList = mydb.getAllContactList();

        for (EmergencyContactModel con: currentEmergencyContactModelList) {
            current.add(con.getName());
        }

        Button submitBtn = (Button) findViewById(R.id.sumbit);
        Button cancelBtn = (Button) findViewById(R.id.cancel);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent putIntent = new Intent();
                chosen = getChosenContacts();
                putIntent.putStringArrayListExtra("chosenContacts",chosen);
                setResult(RESULT_CANCELED, putIntent); // set to cancel first want to check if right theory
                finish();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        RecyclerView recyclerView_contacts = (RecyclerView) findViewById(R.id.recyclerView_contacts);
        emergencyContactsAdapter = new EmergencyContactsAdapter(filteredList);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_contacts.setLayoutManager(rLayoutManager);
        recyclerView_contacts.setItemAnimator(new DefaultItemAnimator());
        recyclerView_contacts.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView_contacts.setAdapter(emergencyContactsAdapter);

        prepareData();
        filteredList.addAll(emergencyContactModelList);
        emergencyContactsAdapter.notifyDataSetChanged();
    }
    private void cloneTo(ArrayList<EmergencyContactModel> copyee, ArrayList<EmergencyContactModel> copyer){
        copyer.clear();
        copyer.addAll(copyee);
    }
    private void filter(String s) {

        ArrayList<EmergencyContactModel> copyList = new ArrayList<>();

        copyList.addAll(filteredList);
        filteredList.clear();

        if (s.isEmpty()) {
            filteredList.addAll(emergencyContactModelList);
        } else {
            s = s.toLowerCase();
            for (int i = 0; i<copyList.size(); i++) {
                // Adapt the if for your usage
                if (copyList.get(i).getName().toLowerCase().contains(s) || copyList.get(i).getNumber().toLowerCase().contains(s) )
                {
                    filteredList.add(copyList.get(i));
                }
            }
        }
        emergencyContactsAdapter.notifyDataSetChanged();
    }
    private ArrayList<String> getChosenContacts(){
        ArrayList<String> temp = new ArrayList<>();
        for (EmergencyContactModel model: emergencyContactModelList) {
            if(model.isSelected()){
                temp.add(model.getName());
                Log.e("(WEW)", model.getName()+ " " +model.getNumber() + " " + model.getPhoto_uri() );
                mydb.insertContact(model.getName(),model.getNumber(),model.getPhoto_uri());
            }
        }
        return temp;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CONTACT_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                // Do something with the contact here (bigger example below)
            }
        }
    }

    private void prepareData(){
        Log.e("(mobile number)", "Data Prepared");
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

                                EmergencyContactModel emergencyContactModel = new EmergencyContactModel(name, phoneNumber,photo);

                                if(current.contains(name)){}
                                else{
                                    emergencyContactModelList.add(emergencyContactModel);
                                }

                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                // Log.e(name + "(home number)", phoneNumber);
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                // Log.e(name + "(work number)", phoneNumber);
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                //Log.e(name + "(other number)", phoneNumber);
                                break;
                            default:
                                break;
                        }
                    }
                    pCur.close();
                }
            }
        }

        // emergencyContactsAdapter.notifyDataSetChanged();
    }
}
