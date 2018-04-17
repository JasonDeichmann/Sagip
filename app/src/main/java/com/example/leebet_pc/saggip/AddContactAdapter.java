package com.example.leebet_pc.saggip;

import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddContactAdapter extends RecyclerView.Adapter<AddContactAdapter.MyViewHolder>{

    private List<EmergencyContactModel> emergencyContactModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, number;
        public ImageView photo;
        public View view;
        public LinearLayout contactItem;
        public MyViewHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.emergency_contact_name);
            number = (TextView) view.findViewById(R.id.emergency_contact_number);
            photo = (CircleImageView) view.findViewById(R.id.emergency_contact_img);
            contactItem = (LinearLayout) view.findViewById(R.id.contact_item);
            this.view = view;
        }

        public TextView getName(){
            return name;
        }

        public TextView getNumber(){
            return number;
        }

    }

    public AddContactAdapter(List<EmergencyContactModel> emergencyContactModelList){
        this.emergencyContactModelList = emergencyContactModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_contact_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final EmergencyContactModel emergencyContactModel = emergencyContactModelList.get(position);
        final MyViewHolder hholder = holder;

        if (emergencyContactModel.getPhoto_uri() == null) {
            hholder.photo.setImageResource(R.drawable.default_img);
        } else {
            Uri u = Uri.parse(emergencyContactModel.getPhoto_uri());
            hholder.photo.setImageURI(u);
        }

        hholder.view.setBackgroundColor(emergencyContactModel.isSelected() ? Color.CYAN : Color.WHITE);
        hholder.name.setText(emergencyContactModel.getName());
        hholder.number.setText(emergencyContactModel.getNumber());
        hholder.contactItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
