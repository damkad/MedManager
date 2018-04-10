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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kADIRI PC on 4/9/2018.
 */

public class medicineManagerAdapter extends RecyclerView.Adapter<medicineManagerAdapter.MedicineHolder> {

    List<medicine> list;
    Context context;

    public medicineManagerAdapter(Context context) {
        this.context = context;
        list = new ArrayList<medicine>();
        list.add(new medicine("d", null,
                "20-April-18", "28-April-18", "Malaria imfection...from female anophilis mosquito"
                , null));

    }


    @NonNull
    @Override
    public MedicineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MedicineHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.template, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineHolder holder, int position) {
        holder.medicine_name.setText(list.get(position).getDrug_name());
        holder.from_date.setText(list.get(position).getFrom_date());
        holder.to_date.setText(list.get(position).getTo_date());
        holder.ailment.setText(list.get(position).getPresciption());

    }

    @Override
    public int getItemCount() {
        return list.size();
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

        public void onBind(int position) {
            //medicine med = new medicine();
            //int adapterPosition = getAdapterPosition();
            //medicine_name.setText(med.getDrug_name());


        }
    }

}
