package com.example.kadiripc.medmanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.kadiripc.medmanager.R;
import com.example.kadiripc.medmanager.model.medicine;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kADIRI PC on 4/9/2018.
 */

public class medicineManagerAdapter extends FirestoreAdapter<medicineManagerAdapter.MedicineHolder> {


    Query query;

    OnDrugSelectedListener mdrugsListener;

    public interface OnDrugSelectedListener {
        void OnDrugSelected(DocumentSnapshot documentSnapshot);
    }

    public medicineManagerAdapter(Query query, OnDrugSelectedListener drugsListener) {
        super(query);
        mdrugsListener = drugsListener;

    }


    @NonNull
    @Override
    public MedicineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MedicineHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.template, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineHolder holder, int position) {
        holder.onBind(getSnapshot(position), mdrugsListener);

    }


    class MedicineHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.drug_image)
        ImageView imageView;

        @BindView(R.id.drug_name)
        TextView medicine_name;

        @BindView(R.id.drug_from_date)
        TextView from_date;

        @BindView(R.id.drug_to_date)
        TextView to_date;

        @BindView(R.id.drug_ailment)
        TextView ailment;

        @BindView(R.id.time_layout)
        TextView textView;

        public MedicineHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(final DocumentSnapshot snapshot, final OnDrugSelectedListener listener) {
            //medicine med = new medicine();
            //int adapterPosition = getAdapterPosition();
            //medicine_name.setText(med.getDrug_name());
            medicine med = snapshot.toObject(medicine.class);

            medicine_name.setText(med.getDrug_name());
            from_date.setText(med.getFrom_date());
            to_date.setText(med.getTo_date());
            ailment.setText(med.getPresciption());
            String photo = med.getPhoto();
            imageView.setImageDrawable(getPhotoDrawable(photo));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnDrugSelected(snapshot);
                }
            });
            int time = med.getTime();


            if (time == 1){
                textView.setText("Frequency: Morning");
            } else if(time == 2){
                textView.setText("Frequency: Morning and Evening");
            }
            else if (time == 3)
                textView.setText("Frequency: Morning, Afternoon and Evening");

                textView.setTextSize(12);

            }
        }






    public TextDrawable getPhotoDrawable(String string) {
        ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
        int color = colorGenerator.getRandomColor();
        if (string.isEmpty() || string == null) {
            string = "N";
        }
        return TextDrawable.builder().buildRound(string.toUpperCase(), color);
    }




}



