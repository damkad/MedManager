package com.example.kadiripc.medmanager;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kadiripc.medmanager.model.medicine;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewActivity extends AppCompatActivity {

    @BindView(R.id.drug_entry_category_edit)
    EditText drug_ailment;

    @BindView(R.id.button_search)
    Button button;

    private static final String DRUG_AILMENT = "name";

    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        ButterKnife.bind(this);
        db = FirebaseFirestore.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDrugs();
                finish();
            }
        });

    }

    private void addDrugs() {
        String data_drug_name = drug_ailment.getText().toString();

        WriteBatch batch = db.batch();
        DocumentReference drugsData = db.collection("drugsData").document();

        medicine med = new medicine(data_drug_name, null, null, null, null, null);

        batch.set(drugsData, med);
        batch.commit().addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(NewActivity.this, "drugs registered", Toast.LENGTH_LONG).show();
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewActivity.this, "Error" + e.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @OnClick(R.id.button_cancel)
    public void onCancelClicked(View view) {
        finish();
    }
}
