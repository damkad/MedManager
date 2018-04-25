package com.example.kadiripc.medmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kadiripc.medmanager.model.UserProfile;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProfileActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {


    @BindView(R.id.edit_profile)
    Button button;


    @BindView(R.id.profile_births)
    TextView ProfileBirthday;

    @BindView(R.id.profile_names)
    TextView ProfileName;


    @BindView(R.id.profile_hobbys)
    TextView ProfileHobby;


    @BindView(R.id.profile_countrys)
    TextView ProfileCountry;


    @BindView(R.id.profile_phones)
    TextView ProfilePhone;


    FirebaseFirestore db;
    private ListenerRegistration mdrugRegistration;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();

        documentReference = db.collection("drugsData").document("users");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,
                        EditProfileActivity.class));
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (snapshot != null || !snapshot.exists()) {
            onUserProfileLoaded(snapshot.toObject(UserProfile.class));
        }

    }


    @Override
    public void onStart() {
        super.onStart();

        mdrugRegistration = documentReference.addSnapshotListener(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mdrugRegistration != null) {
            mdrugRegistration.remove();
            mdrugRegistration = null;
        }
    }

    private void onUserProfileLoaded(UserProfile userProfile) {


        ProfileName.setText(userProfile.getProfileName());
        ProfileCountry.setText(userProfile.getProfileCountry());
        ProfilePhone.setText(userProfile.getProfileNumber());
        ProfileHobby.setText(userProfile.getProfileHobby());
        ProfileBirthday.setText(userProfile.getProfileBirthday());

    }
}