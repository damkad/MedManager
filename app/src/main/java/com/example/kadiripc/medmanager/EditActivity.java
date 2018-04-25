package com.example.kadiripc.medmanager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.kadiripc.medmanager.model.medicine;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.kadiripc.medmanager.MainActivity.KEY_DRUG_ID;

public class EditActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {
    private static final String TAG = "drug details";
    private ListenerRegistration mdrugRegistration;


    @BindView(R.id.frequency_entry_category_edit)
    Spinner spinner;

    @BindView(R.id.drug_top_card)
    ImageView imageView;

    private Calendar mcalendar;
    @BindView(R.id.start_date_entry_category_edit)
    EditText startdate;
    @BindView(R.id.stop_date_entry_category_edit)
    EditText stopdate;

    private int day, month, year;


    @BindView(R.id.drug_entry_category_edit)
    EditText drug_name;

    @BindView(R.id.comment_entry_category_edit)
    EditText drug_comment;

    @BindView(R.id.button_search)
    Button button;

    private static final String DRUG_AILMENT = "name";

    FirebaseFirestore db;
    DocumentReference mDrugRef;
    String drugsId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        ButterKnife.bind(this);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolsbar);
        setSupportActionBar(toolbar);

        Glide.with(imageView.getContext()).load(R.drawable.drug).into(imageView);
        db = FirebaseFirestore.getInstance();

        hideKeyboard();

        // Get restaurant ID from extras

        drugsId = getIntent().getExtras().getString(KEY_DRUG_ID);

        mDrugRef = db.collection("drugsData").document(drugsId);

        button.setText("UPDATE");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String data_drug_name = drug_name.getText().toString();
                String data_start_date = startdate.getText().toString();
                String data_stop_date = stopdate.getText().toString();
                String data_prescription = drug_comment.getText().toString();
                // update the user profile information in Firebase database.
                if (TextUtils.isEmpty(data_drug_name) || TextUtils.isEmpty(data_start_date) || TextUtils.isEmpty(data_stop_date)
                        || TextUtils.isEmpty(data_prescription)) {
                    Helper.displayMessageToast(EditActivity.this, "All fields must be filled");
                    return;
                }
                UpdateDrugs();
                finish();
            }
        });

        mcalendar = Calendar.getInstance();
        day = mcalendar.get(Calendar.DAY_OF_MONTH);
        year = mcalendar.get(Calendar.YEAR);
        month = mcalendar.get(Calendar.MONTH);

        startdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard();
                    DateDialog1();
                }
            }
        });
        stopdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard();
                    DateDialog2();
                }
            }
        });

    }


    private void onDrugLoaded(medicine med) {
        if (med == null) {
            return;
        }
        drug_name.setText(med.getDrug_name());
        startdate.setText(med.getFrom_date());
        stopdate.setText(med.getTo_date());
        drug_comment.setText(med.getPresciption());
        spinner.setSelection(med.getTime()-1);
    }


    private void UpdateDrugs() {


        String data_drug_name = drug_name.getText().toString();
        String data_start_date = startdate.getText().toString();
        String data_stop_date = stopdate.getText().toString();
        String data_prescription = drug_comment.getText().toString();
        //int time = Integer.valueOf(spinner.getSelectedItem().toString());
        int spinnerValue = getSpinnerValue();

        long timestamp = convertDate(data_start_date);

        WriteBatch batch = db.batch();

        String sh = data_drug_name.substring(0, 1);


        medicine med = new medicine(data_drug_name, spinnerValue, data_start_date, data_stop_date, data_prescription, sh, timestamp);
        DocumentReference drugsData = db.collection("drugsData").document(drugsId);
        batch.set(drugsData, med);
        batch.commit().addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditActivity.this, "drugs registered", Toast.LENGTH_LONG).show();
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditActivity.this, "Error" + e.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private long convertDate(String data_start_date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = null;
        try {
            date1 = dateFormat.parse(data_start_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1.getTime();

    }


    public void DateDialog1() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                startdate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
            }
        };
        DatePickerDialog dpDialog = new DatePickerDialog(this, listener, year, month, day);
        dpDialog.show();
    }

    public void DateDialog2() {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                stopdate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
            }
        };
        DatePickerDialog dpDialog = new DatePickerDialog(this, listener, year, month, day);
        dpDialog.show();
    }


    @OnClick(R.id.button_cancel)
    public void onCancelClicked(View view) {
        finish();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (snapshot != null || !snapshot.exists()) {
            onDrugLoaded(snapshot.toObject(medicine.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mDrugRef == null) {
            return;
        }
        mdrugRegistration = mDrugRef.addSnapshotListener(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mdrugRegistration != null) {
            mdrugRegistration.remove();
            mdrugRegistration = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_items:
                delete_item();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void delete_item() {
        WriteBatch batch = db.batch();
        DocumentReference drugsData = db.collection("drugsData").document(drugsId);
        batch.delete(drugsData);
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),
                        "Event document has been deleted",
                        Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }

    public int getSpinnerValue() {
        String selectedValue = (String) spinner.getSelectedItem();

        if ("1".equals(selectedValue)) {
            return 1;
        } else if ("2".equals(selectedValue)) {
            return 2;
        } else if ("3".equals(selectedValue)) {
            return 3;
        } else return -1;
    }

}



