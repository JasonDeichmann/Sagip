package com.example.leebet_pc.saggip;

import android.os.Binder;

/**
 * Created by user on 3/7/2018.
 */

public class AlarmBinder extends Binder {

    private AlarmService alarmService;

    public AlarmBinder(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    public AlarmService getAlarmService() {
        return alarmService;
    }
}
