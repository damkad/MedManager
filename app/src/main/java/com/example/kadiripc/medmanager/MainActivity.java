package com.example.kadiripc.medmanager;

import android.app.Notification;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kadiripc.medmanager.adapter.medicineManagerAdapter;
import com.example.kadiripc.medmanager.viewmodel.MainActivityViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.SimpleTimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements medicineManagerAdapter.OnDrugSelectedListener {
    private static final String TAG = "View";
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    Query mquery;
    FirebaseFirestore db;
    private static final int LIMIT = 100;
    public static final String KEY_DRUG_ID = "key_drug_id";
    medicineManagerAdapter medAdapter;
    @BindView(R.id.view_empty)
    ViewGroup mEmptyView;

    FirebaseAuth firebaseAuth;
    private MainActivityViewModel mViewModel;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

        // View model
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        db = FirebaseFirestore.getInstance();

        // Get ${LIMIT} data
        mquery = db.collection("drugsData")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(LIMIT);
        medAdapter = new medicineManagerAdapter(mquery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setAdapter(medAdapter);

        LinearLayoutManager lM = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lM);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewActivity.class);
                startActivity(intent);
                //NotificationUtils.remindUserToTakeDrugs(MainActivity.this);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                viewProfile();
                break;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                startSignIn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void viewProfile() {
        //if signed in
        if (!shouldStartSignIn()) {
            //if user has field not field profile before
            db.collection("drugsData").document("users").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot snapshot = task.getResult();

                    if (snapshot.exists()) {
                        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(profileIntent);

                    } else {
                        Intent profileIntent = new Intent(MainActivity.this, EditProfileActivity.class);
                        startActivity(profileIntent);
                    }

                }
            });

        }
    }

    //private void deleteAll() {
    //  db.collection("drugsData").document().delete();
    //}


    @Override
    public void OnDrugSelected(DocumentSnapshot documentSnapshot) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra(MainActivity.KEY_DRUG_ID, documentSnapshot.getId());
        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }


    @Override
    public void onStart() {
        super.onStart();

        //Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }


        // Start listening for Firestore updates
        if (medAdapter != null) {
            medAdapter.startListening();
            notifyAlarmManager();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (medAdapter != null) {
            medAdapter.stopListening();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            mViewModel.setIsSigningIn(false);

            if (resultCode != RESULT_OK) {
                if (response == null) {
                    // User pressed the back button.
                    finish();
                } else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSignInErrorDialog(R.string.message_no_network);
                } else {
                    showSignInErrorDialog(R.string.message_unknown);
                }
            }
        }
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(
                        Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        mViewModel.setIsSigningIn(true);
    }

    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }


    private void showSignInErrorDialog(@StringRes int message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.title_sign_in_error)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.option_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startSignIn();
                    }
                })
                .setNegativeButton(R.string.option_exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).create();

        dialog.show();
    }
//method defined to notify alarm manager

    private void notifyAlarmManager() {
        db.collection("drugsData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        //Log.d(TAG, documentSnapshot.getId() + documentSnapshot.getData());
                        String from_date = (String) documentSnapshot.getData().get("from_date");
                        String to_date = (String) documentSnapshot.getData().get("to_date");
                        Long time = (Long) documentSnapshot.getData().get("time");
                        //get todays date
                        //get data where start date is equal or greater than todays date
                        //get corresponding id, freq, end date- start date + a day
                        //get data where end date is equal to todays date plus date difference
                        //store in sharedpreference
                        //notify alarm manager
                        Log.d(TAG, "values are: " + from_date + "  " + to_date + "  " + time + " ");
                    }
                } else {
                    Log.d(TAG, "Error getting document", task.getException());
                }
            }
        });
    }

}
