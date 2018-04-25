package com.example.kadiripc.medmanager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import com.example.kadiripc.medmanager.firebase.FirebaseStorageHelper;
import com.example.kadiripc.medmanager.model.UserProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;


public class EditProfileActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private static final int PIC = 1;
    private static final int REQUEST_READ_PERMISSION = 120;
    ListenerRegistration registration;
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    Uri selectedImageUri;
    private ImageView profileImage;

    private EditText editProfileName;

    private EditText editProfileCountry;

    private EditText editProfilePhoneNumber;

    private EditText editProfileHobby;

    private EditText editProfileBirthday;
    FirebaseFirestore db;
    DocumentReference document;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setTitle("Edit Profile Information");

        db = FirebaseFirestore.getInstance();
        document = db.collection("drugsData").document("users");

       id = document.getId();


        editProfileName = findViewById(R.id.profile_name);
        editProfileCountry = findViewById(R.id.profile_country);
        editProfilePhoneNumber = findViewById(R.id.profile_phone);
        editProfileHobby = findViewById(R.id.profile_hobby);
        editProfileBirthday = findViewById(R.id.profile_birth);
        profileImage = findViewById(R.id.profile_image);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, PIC);
            }
        });

        Button saveEditButton = findViewById(R.id.save_edit_button);
        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                upDateUser();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Helper.SELECT_PICTURE) {
            selectedImageUri = data.getData();

            FirebaseStorageHelper storageHelper = new FirebaseStorageHelper(this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
                return;
            }
            storageHelper.saveProfileImageToCloud(id, selectedImageUri, profileImage);
        }
    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        assert cursor != null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(projection[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Sorry!!!, you can't use this app without granting this permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (snapshot != null || !snapshot.exists()) {

            onLoadUser(snapshot.toObject(UserProfile.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (document == null) {
            return;
        }
        registration = document.addSnapshotListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }

    private void upDateUser() {

        String profileName = editProfileName.getText().toString();
        String profileCountry = editProfileCountry.getText().toString();
        String profilePhoneNumber = editProfilePhoneNumber.getText().toString();
        String profileHobby = editProfileHobby.getText().toString();
        String profileBirthday = editProfileBirthday.getText().toString();

        // update the user profile information in Firebase database.
        if (TextUtils.isEmpty(profileName) || TextUtils.isEmpty(profileCountry) || TextUtils.isEmpty(profilePhoneNumber)
                || TextUtils.isEmpty(profileHobby) || TextUtils.isEmpty(profileBirthday)) {
            Helper.displayMessageToast(EditProfileActivity.this, "All fields must be filled");
            return;
        }


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            //signIn;
        } else {
            String userId = user.getProviderId();
            String id = user.getUid();
            String profileEmail = user.getEmail();

            //FirebaseUserEntity userEntity = new FirebaseUserEntity(id, profileEmail, profileName, profileCountry, profilePhoneNumber, profileBirthday, profileHobby);
            //FirebaseDatabaseHelper firebaseDatabaseHelper = new FirebaseDatabaseHelper();
            //firebaseDatabaseHelper.createUserInFirebaseDatabase(id, userEntity);
            WriteBatch batch = db.batch();


            DocumentReference document = db.collection("drugsData").document("users");
            UserProfile userProfile = new UserProfile(profileName, profileCountry, profileEmail,
                    profileHobby, profileBirthday, userId, id, profilePhoneNumber);
            batch.set(document, userProfile);

            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(EditProfileActivity.this,
                            "Successfully registered", Toast.LENGTH_LONG).show();
                }
            });
            finish();
            startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));

        }
    }

    private void onLoadUser(UserProfile userProfile) {
        if (userProfile == null) {
            return;
        }

        editProfileName.setText(userProfile.getProfileName());
        editProfileCountry.setText(userProfile.getProfileCountry());
        editProfilePhoneNumber.setText(userProfile.getProfileNumber());
        editProfileHobby.setText(userProfile.getProfileHobby());
        editProfileBirthday.setText(userProfile.getProfileBirthday());
    }
}