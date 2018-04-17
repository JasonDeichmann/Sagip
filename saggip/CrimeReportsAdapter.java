package com.example.leebet_pc.saggip;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CrimeReportsAdapter extends RecyclerView.Adapter<CrimeReportsAdapter.MyViewHolder>{

    private List<CrimeReportModel> crimeReportModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView date, location, details;

        public MyViewHolder(View view){
            super(view);
            date = (TextView) view.findViewById(R.id.crime_report_date);
            location = (TextView) view.findViewById(R.id.crime_report_location);
            details = (TextView) view.findViewById(R.id.crime_report_details);
        }

        public TextView getDate(){
            return date;
        }

        public TextView getLocation(){
            return location;
        }

        public TextView getDetails(){
            return details;
        }

    }

    public CrimeReportsAdapter(List<CrimeReportModel> crimeReportModelList){
        this.crimeReportModelList = crimeReportModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.crime_report_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        CrimeReportModel crimeReportModel = crimeReportModelList.get(position);
        holder.date.setText(crimeReportModel.getDate());
        holder.location.setText(crimeReportModel.getLocation());
        holder.details.setText(crimeReportModel.getDetails());
    }

    @Override
    public int getItemCount(){
        return crimeReportModelList.size();
    }
}
