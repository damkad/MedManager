package com.example.kadiripc.medmanager.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kadiripc.medmanager.R;
import com.example.kadiripc.medmanager.model.medicine;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kADIRI PC on 4/9/2018.
 */

public class medicineManagerAdapter extends FirestoreAdapter<medicineManagerAdapter.MedicineHolder> {

    Context context;
    Query query;

    OnDrugSelectedListener mdrugsListener;
    public interface OnDrugSelectedListener{
        void OnDrugSelected(DocumentSnapshot documentSnapshot);
    }

    public medicineManagerAdapter(Query query, OnDrugSelectedListener drugsListener) {
        super(query);
        mdrugsListener = drugsListener;

    }


    @NonNull
    @Override
    public MedicineHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType)  {
        return new MedicineHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.template, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineHolder holder, int position) {
        holder.onBind(getSnapshot(position), mdrugsListener);

    }







    class MedicineHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.drug_name)
        TextView medicine_name;

        @BindView(R.id.drug_from_date)
        TextView from_date;

        @BindView(R.id.drug_to_date)
        TextView to_date;

        @BindView(R.id.drug_ailment)
        TextView ailment;

        @BindView(R.id.time_layout)
        LinearLayout time_layout;

        public MedicineHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void onBind(final DocumentSnapshot snapshot, final OnDrugSelectedListener listener) {
            //medicine med = new medicine();
            //int adapterPosition = getAdapterPosition();
            //medicine_name.setText(med.getDrug_name());
            medicine med = snapshot.toObject(medicine.class);

            ailment.setText(med.getDrug_name());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnDrugSelected(snapshot);
                }
            });


        }


        }
    }


