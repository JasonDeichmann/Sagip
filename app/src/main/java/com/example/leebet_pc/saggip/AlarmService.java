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
    private Timer timerCheckVolume;

    @Override
    public void onCreate() {
        super.onCreate();

        this.alarmBinder = new AlarmBinder(this);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        currentVolume = audioManager.getStreamVolume(2);

        startCheckForAlert();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
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
        stopTimer();
        return super.onUnbind(intent);

    }

    public void startCheckForAlert(){
        timerCheckVolume = new Timer();
        isTimerRunning = true;

        timerCheckVolume.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if(combo == 4){
                    combo = 0;
                    timerCheckVolume.cancel();
                    isTimerRunning = false;
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

    public void stopTimer(){
        if(timerCheckVolume != null)
            timerCheckVolume.cancel();
    }
}
