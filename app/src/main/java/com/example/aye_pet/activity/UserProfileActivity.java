package com.example.aye_pet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.aye_pet.R;
import com.example.aye_pet.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    private TextView fname, lname, bday, gender, email, phone, address, userId;
    private ImageView userImage;
    private Button btn_edit;
    private String currentUserID,target_userID;

    private User currentUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        fname = findViewById(R.id.userprofile_fName);
        lname = findViewById(R.id.userprofile_lName);
        bday = findViewById(R.id.userprofile_bday);
        gender = findViewById(R.id.userprofile_gender);
        email = findViewById(R.id.userprofile_email);
        phone = findViewById(R.id.userprofile_phone);
        address = findViewById(R.id.userprofile_address);
        userId = findViewById(R.id.userprofile_userId);
        btn_edit = findViewById(R.id.userprofile_button_edit);
        userImage = findViewById(R.id.userprofile_userImage);


        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        target_userID = getIntent().getStringExtra("TARGET_USER");

        if(!currentUserID.equals(target_userID)){
            btn_edit.setVisibility(View.GONE);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user");
        // Read from the database
        myRef.child(target_userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                currentUserProfile = dataSnapshot.getValue(User.class);


                Glide.with(getApplicationContext()).load(currentUserProfile.getImageURL()).into(userImage);
                fname.setText(currentUserProfile.getFirstName());
                lname.setText(currentUserProfile.getLastName());
                bday.setText(currentUserProfile.getBirthday());
                gender.setText(currentUserProfile.getGender());
                email.setText(currentUserProfile.getEmail());
                phone.setText(currentUserProfile.getPhone());
                address.setText(currentUserProfile.getAddress());
                userId.setText(target_userID);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("[UserProfileActivity] Failed to read value." + error.toException());
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, UpdateUserProfileActivity.class);
                intent.putExtra("USER_PROFILE", currentUserProfile);
                startActivity(intent);
            }
        });
    }
}
