package com.example.leebet_pc.saggip;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EmergencyMessageActivity extends AppCompatActivity {

    EditText emergencyMessage;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Emergency Message");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setSubtitle("Set SMS Blast");

        emergencyMessage = (EditText) findViewById(R.id.emergency_messsage_text);
        loadMessage();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final Button emergencyButton = (Button) findViewById(R.id.emergency_message_button);

        emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emergencyMessage.isEnabled() == false){
                    emergencyMessage.setEnabled(true);
                    emergencyButton.setText("Save");
                    emergencyMessage.setTextColor(getResources().getColor(R.color.white));
                }
                else{
                    emergencyMessage.setEnabled(false);
                    emergencyButton.setText("Edit");
                    saveMessage(emergencyMessage.getText().toString());
                    emergencyMessage.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
    }

    private void saveMessage(String message){

        SharedPreferences prefs = this.getSharedPreferences("com.example.leebet_pc.saggip", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("EMERGENCY_MESSAGE", message);

        editor.commit();

    }
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

    private void loadMessage(){
        SharedPreferences sp = getSharedPreferences("com.example.leebet_pc.saggip", Activity.MODE_PRIVATE);

        if(sp != null)
            message = sp.getString("EMERGENCY_MESSAGE", "Distress alarm triggered with my Sagip application. HELP!");

        emergencyMessage.setText(message);
    }
}
