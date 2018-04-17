package com.example.leebet_pc.saggip;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.hololo.tutorial.library.TutorialActivity;

import com.hololo.tutorial.library.Step;

public class OnboardDetailsActivity extends TutorialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addFragment(new Step.Builder().setTitle("SAGIP")
                .setContent("Most of the time, when we wander the streets alone. There is always a lurking danger of bad elements nearby and we feel unsafe.")
                .setBackgroundColor(Color.parseColor("#c74a22")) // int background color
                .setDrawable(R.drawable.sagip_hires) // int top drawable
                .setSummary("Sagip Copyright 2018, Deichmenn.")
                .build());
        addFragment(new Step.Builder().setTitle("A Life Saving App")
                .setContent("Sagip exists to provide you and your loved ones assurance that someone will hear your call whenever you are in danger.")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.f1) // int top drawable
                .setSummary("A safe life is a good life.")
                .build());
        addFragment(new Step.Builder().setTitle("In Just 4 Clicks")
                .setContent("By clicking the volume up or down button for four consecutive times, an alarm will blare off at full volume.")
                .setBackgroundColor(Color.parseColor("#263238")) // int background color
                .setDrawable(R.drawable.f2) // int top drawable
                .setSummary("You can save your life, in 4 clicks")
                .build());
        addFragment(new Step.Builder().setTitle("Specially for you")
                .setContent("If the loud alarm falls into deaf ears, the app will automatically blast S.O.S SMS to your selected contacts and plot a report on the web, as well as notify app users nearby.")
                .setBackgroundColor(Color.parseColor("#311B92")) // int background color
                .setDrawable(R.drawable.f3) // int top drawable
                .setSummary("Your safety is the priority ")
                .build());

        addFragment(new Step.Builder().setTitle("Incident Reporting")
                .setContent("After turning off the alarm, you will be prompted to fill out a report to help other people know that a crime incident has occurred in your location. Or you can just report it as a false alarm!")
                .setBackgroundColor(Color.parseColor("#388E3C")) // int background color
                .setDrawable(R.drawable.f8) // int top drawable
                .setSummary("Reporting is caring.")
                .build());
        addFragment(new Step.Builder().setTitle("Add Contacts")
                .setContent("These chosen contacts will receive the emergency SOS SMS once the distress call has been activated.")
                .setBackgroundColor(Color.parseColor("#00695C")) // int background color
                .setDrawable(R.drawable.f4) // int top drawable
                .setSummary("Choose your trusted contacts.")
                .build());
        addFragment(new Step.Builder().setTitle("Emergency Message")
                .setContent("You can edit and customize the emergency SMS.")
                .setBackgroundColor(Color.parseColor("#00695C")) // int background color
                .setDrawable(R.drawable.f5) // int top drawable
                .setSummary("Choose your words of distress!")
                .build());

        addFragment(new Step.Builder().setTitle("Past Crime Reports")
                .setContent("Your past alerts can be viewed in this page.")
                .setBackgroundColor(Color.parseColor("#00695C")) // int background color
                .setDrawable(R.drawable.f6) // int top drawable
                .setSummary("Reflect.")
                .build());
        addFragment(new Step.Builder().setTitle("Heat Maps")
                .setContent("A map of alarm incidents reported are mapped and can be viewed in the default page.")
                .setBackgroundColor(Color.parseColor("#00695C")) // int background color
                .setDrawable(R.drawable.f7) // int top drawable
                .setSummary("Be careful of the hot spots!.")
                .build());
        addFragment(new Step.Builder().setTitle("Credits")
                .setContent("Alert sound by InstrumentalFx \n Link: https://instrumentalfx.co/danger-alarm-free-sound-effect-download/ \n \n Developed by: \n Betlee-Kyle Barraquias \n Tommy Botabara \n Jason Deichmann")
                .setBackgroundColor(Color.parseColor("#00695C")) // int background color
                .build());

    }
    private void saveTutorial(){

        SharedPreferences prefs = this.getSharedPreferences("com.example.leebet_pc.saggip", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("First_time", false);
        editor.commit();
    }
    @Override
    public void finishTutorial() {
        setResult(RESULT_OK);
        saveTutorial();
        overridePendingTransition(R.anim.fade_from,R.anim.fade_to);
        finish();
        overridePendingTransition(R.anim.fade_from,R.anim.fade_to);
    }
}
