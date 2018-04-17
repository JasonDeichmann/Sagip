package com.example.leebet_pc.saggip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class PastCrimeReportsActivity extends AppCompatActivity {
    private List<CrimeReportModel> crimeReportModelList = new ArrayList<>();
    private CrimeReportsAdapter crimeReportsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_crime_reports);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Past Crime Reports");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView_crime_reports = (RecyclerView) findViewById(R.id.recyclerView_crime_reports);
        crimeReportsAdapter = new CrimeReportsAdapter(crimeReportModelList);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_crime_reports.setLayoutManager(rLayoutManager);
        recyclerView_crime_reports.setItemAnimator(new DefaultItemAnimator());
        recyclerView_crime_reports.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView_crime_reports.setAdapter(crimeReportsAdapter);

        prepareData();
    }

    private void prepareData(){
        CrimeReportModel crimeReportModel = new CrimeReportModel("Date: January 1, 2018", "Location: Makati, Manila", "Details: Bla bla bla...");
        crimeReportModelList.add(crimeReportModel);

        crimeReportModel = new CrimeReportModel("Date: January 22, 2018", "Location: Malate, Manila", "Details: Scary person was following me.");
        crimeReportModelList.add(crimeReportModel);

        crimeReportsAdapter.notifyDataSetChanged();
    }
}
