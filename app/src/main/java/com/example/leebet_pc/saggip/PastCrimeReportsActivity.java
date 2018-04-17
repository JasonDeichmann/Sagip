package com.example.leebet_pc.saggip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PastCrimeReportsActivity extends AppCompatActivity {
    private List<CrimeReportModel> crimeReportModelList = new ArrayList<>();
    private CrimeReportsAdapter crimeReportsAdapter;
    private DBHelperPastCrimeReports mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_crime_reports);

        mydb = new DBHelperPastCrimeReports(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle("Distress Alarms History");
        getSupportActionBar().setTitle("Past Crime Reports");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RecyclerView recyclerView_crime_reports = (RecyclerView) findViewById(R.id.recyclerView_crime_reports);
        crimeReportsAdapter = new CrimeReportsAdapter(crimeReportModelList);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_crime_reports.setLayoutManager(rLayoutManager);
        recyclerView_crime_reports.setItemAnimator(new DefaultItemAnimator());
        recyclerView_crime_reports.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView_crime_reports.setAdapter(crimeReportsAdapter);

        prepareData();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

    private void prepareData(){
        crimeReportModelList.addAll(mydb.getAllReportsList());
        Collections.reverse(crimeReportModelList);
        crimeReportsAdapter.notifyDataSetChanged();
    }
}
