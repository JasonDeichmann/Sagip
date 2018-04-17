package com.example.leebet_pc.saggip;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.location.LocationListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlertActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;
    private String emergencyMessage = "HELP ME!";

    DBHelper mydb;

    ArrayList<String> numbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        mydb = new DBHelper(this);
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(3, 0, 2);
        audioManager.setStreamVolume(1, 0, 2);
        audioManager.setStreamVolume(2, 0, 2);


        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(this, R.raw.alertsound);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

        sendTexts();

        Button stop = (Button) findViewById(R.id.stopButton);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaPlayer.stop();
                Intent i = new Intent(AlertActivity.this, AlertReportActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void sendTexts(){
        loadMessage();

        numbers = mydb.getAllContacts();

        for(String number : numbers){
            Log.e("wew", number + ", message sent.");
            SmsManager.getDefault().sendTextMessage(""+number, null, emergencyMessage, null, null);
        }
    }

    private void loadMessage(){
        SharedPreferences sp = getSharedPreferences("com.example.leebet_pc.saggip", Activity.MODE_PRIVATE);

        if(sp != null)
            emergencyMessage = sp.getString("EMERGENCY_MESSAGE", "HELP ME!");


    }
}
