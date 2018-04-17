package com.example.leebet_pc.saggip;

import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class EmergencyContactsAdapter extends RecyclerView.Adapter<EmergencyContactsAdapter.MyViewHolder>{

    private List<EmergencyContactModel> emergencyContactModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, number;
        public ImageView photo;
        public View view;

        public MyViewHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.emergency_contact_name);
            number = (TextView) view.findViewById(R.id.emergency_contact_number);
            photo = (ImageView) view.findViewById(R.id.emergency_contact_img);
            this.view = view;
        }

        public TextView getName(){
            return name;
        }

        public TextView getNumber(){
            return number;
        }

    }

    public EmergencyContactsAdapter(List<EmergencyContactModel> emergencyContactModelList){
        this.emergencyContactModelList = emergencyContactModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.emergency_contact_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final EmergencyContactModel emergencyContactModel = emergencyContactModelList.get(position);
        final MyViewHolder hholder = holder;

        if (emergencyContactModel.getPhoto_uri() == null) {
            hholder.photo.setImageResource(R.drawable.common_full_open_on_phone);
        } else {
            Uri u = Uri.parse(emergencyContactModel.getPhoto_uri());
            hholder.photo.setImageURI(u);
        }
        hholder.view.setBackgroundColor(emergencyContactModel.isSelected() ? Color.CYAN : Color.WHITE);
        hholder.name.setText(emergencyContactModel.getName());
        hholder.number.setText(emergencyContactModel.getNumber());
        hholder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emergencyContactModel.setSelected(!emergencyContactModel.isSelected());
                hholder.view.setBackgroundColor(emergencyContactModel.isSelected() ? Color.CYAN : Color.WHITE);
            }
        });

    }

    @Override
    public int getItemCount(){
        return emergencyContactModelList.size();
    }
}
