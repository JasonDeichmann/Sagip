package com.example.leebet_pc.saggip;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmergencyContactsAdapter extends RecyclerView.Adapter<EmergencyContactsAdapter.MyViewHolder>{

    private List<EmergencyContactModel> emergencyContactModelList;
    private DBHelperContacts mydb;


    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, number;
        public ImageView photo;
        public ImageButton delete;
        public View view;


        public MyViewHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.emergency_contact_name);
            number = (TextView) view.findViewById(R.id.emergency_contact_number);
            photo = (CircleImageView) view.findViewById(R.id.emergency_contact_img);
            delete = (ImageButton) view.findViewById(R.id.delete);
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
        mydb = new DBHelperContacts(parent.getContext());
        return new MyViewHolder(itemView);
    }
    public void delete(int position) { //removes the row
        EmergencyContactModel deletedModel = emergencyContactModelList.get(position);
        emergencyContactModelList.remove(position);
        mydb.deleteContact(deletedModel.getNumber());

        notifyItemRemoved(position);

        notifyItemRangeChanged(position,this.getItemCount());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {



        final int defaultColor = Color.parseColor("#757575");
        final EmergencyContactModel emergencyContactModel = emergencyContactModelList.get(position);
        final MyViewHolder hholder = holder;

        if (emergencyContactModel.getPhoto_uri() == null) {
            hholder.photo.setImageResource(R.drawable.default_img);
        } else {
            Uri u = Uri.parse(emergencyContactModel.getPhoto_uri());
            hholder.photo.setImageURI(u);
        }

        hholder.view.setBackgroundColor(defaultColor);
        hholder.view.setBackgroundColor(emergencyContactModel.isSelected() ? Color.CYAN : defaultColor);
        hholder.name.setText(emergencyContactModel.getName());
        hholder.number.setText(emergencyContactModel.getNumber());

        hholder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(position);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount(){
        return emergencyContactModelList.size();
    }
}
