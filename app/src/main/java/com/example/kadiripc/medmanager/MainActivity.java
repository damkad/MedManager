package com.example.kadiripc.medmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kadiripc.medmanager.adapter.medicineManagerAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements medicineManagerAdapter.OnDrugSelectedListener {
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    Query mquery;

    public static final String KEY_DRUG_ID = "key_drug_id";
    medicineManagerAdapter medAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
         medAdapter = new medicineManagerAdapter(this);
        recyclerView.setAdapter(medAdapter);

        LinearLayoutManager lM = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lM);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnDrugSelected(DocumentSnapshot documentSnapshot) {
        Intent intent = new Intent(MainActivity.this, NewActivity.class);
        intent.putExtra(MainActivity.KEY_DRUG_ID, documentSnapshot.getId());
        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }


}
