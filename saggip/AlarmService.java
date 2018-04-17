package com.example.leebet_pc.saggip;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmService extends Service {

    private AlarmBinder alarmBinder;
    private AudioManager audioManager;
    private int combo = 0;
    private boolean comboAdded = false;
    private boolean resetComboStarted = false;
    private int currentVolume;
    private boolean isTimerRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();

        this.alarmBinder = new AlarmBinder(this);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        currentVolume = audioManager.getStreamVolume(2);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return this.alarmBinder;


    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void startCheckForAlert(){
        final Timer timerCheckVolume = new Timer();

        timerCheckVolume.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                Log.i("ALERT", "Combo " + combo);

                if(combo == 4){
                    combo = 0;
                    Log.i("ALERT", "PREDATOR INCOMING");
                    timerCheckVolume.cancel();
                    setTimerRunning(false);
                    Intent i = new Intent(AlarmService.this, AlertActivity.class);
                    startActivity(i);

                }

                if(currentVolume < audioManager.getStreamVolume(2) && Math.abs(currentVolume - audioManager.getStreamVolume(2)) == 1){
                    combo++;
                    comboAdded = true;
                }
                else if(currentVolume > audioManager.getStreamVolume(2) && Math.abs(currentVolume - audioManager.getStreamVolume(2)) == 1){
                    combo++;
                    comboAdded = true;
                }
                else
                    comboAdded = false;

                if(comboAdded == true && resetComboStarted == false){
                    resetComboTimer();
                }

                currentVolume = audioManager.getStreamVolume(2);

                if(audioManager.getStreamVolume(2) == 15){
                    audioManager.setStreamVolume(2, 14, 2);
                    combo--;
                }
                else if(audioManager.getStreamVolume(2) == 0){
                    audioManager.setStreamVolume(2, 1, 2);
                    combo--;
                }

                // Log.i("Current Volume", audioManager.getStreamVolume(2)+"");
            }
        }, 0, 100);

    }

    public void resetComboTimer(){
        final Timer resetCombo = new Timer();

        resetCombo.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(resetComboStarted == false)
                    resetComboStarted = true;
                else{
                    combo = 0;
                    resetCombo.cancel();
                    resetComboStarted = false;

                }
            }
        }, 0 , 5000);
    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public void setTimerRunning(boolean timerRunning) {
        isTimerRunning = timerRunning;
    }

}
