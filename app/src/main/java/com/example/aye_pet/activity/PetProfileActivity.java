package com.example.aye_pet.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.aye_pet.R;
import com.example.aye_pet.entity.Pet;
import com.example.aye_pet.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PetProfileActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView name, type, gender, size, age, vaccinated, dewormed, neutered, location, description;
    private Button btn_email, btn_call, btn_edit;

    private Pet pet;
    private String petKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            pet = (Pet) bundle.getSerializable("PET");
            petKey = bundle.getString("KEY");
        }else {
            Toast.makeText(this, "[Pet Profile] Fail to get PROFILE" , Toast.LENGTH_SHORT).show();
            finish();
        }

        imageView =findViewById(R.id.PetProfile_imageView);
        name = findViewById(R.id.PetProfile_name);
        type =findViewById(R.id.PetProfile_type);
        gender = findViewById(R.id.PetProfile_gender);
        size = findViewById(R.id.PetProfile_size);
        age = findViewById(R.id.PetProfile_age);
        vaccinated = findViewById(R.id.PetProfile_vaccinated);
        dewormed = findViewById(R.id.PetProfile_dewormed);
        neutered =findViewById(R.id.PetProfile_neutered);
        location = findViewById(R.id.PetProfile_location);
        description = findViewById(R.id.PetProfile_description);
        btn_email = findViewById(R.id.PetProfile_emailButton);
        btn_call = findViewById(R.id.PetProfile_phoneButton);
        btn_edit = findViewById(R.id.PetProfile_editButton);

        if(pet.getOwnerId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            btn_email.setVisibility(View.GONE);
            btn_call.setVisibility(View.GONE);
        }else {
            btn_edit.setVisibility(View.GONE);
        }

        Glide.with(this).load(pet.getImageURL()).into(imageView);
        name.setText(pet.getName());
        type.setText(pet.getType());
        gender.setText(pet.getGender());
        size.setText(pet.getSize());
        age.setText(pet.getAge());
        vaccinated.setText(pet.getVaccinated());
        dewormed.setText(pet.getDewormed());
        neutered.setText(pet.getNeutered());
        location.setText(pet.getLocation());
        description.setText(pet.getDescription());


        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailToOwner();
            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneCallToOwner();
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PetProfileActivity.this, UpdatePetActivity.class);
                intent.putExtra("PET",pet);
                intent.putExtra("KEY",petKey);
                startActivity(intent);
                finish();
            }
        });
    }

    protected void sendEmailToOwner() {
        Log.i("Send email", "");

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("user");
        myRef.child(pet.getOwnerId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User ownerUserProfile = dataSnapshot.getValue(User.class);

                String[] TO = {ownerUserProfile.getEmail()};
                String subject, text;
                subject = "";
                text = "";

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, text);

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    finish();
                    Log.i("Finished sending email", "");
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(PetProfileActivity.this,
                            "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("[PetProfileActivity] Failed to read Owner Profile." + error.toException());
            }
        });
    }

    protected void phoneCallToOwner() {
        Log.i("Send email", "");

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("user");
        myRef.child(pet.getOwnerId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User ownerUserProfile = dataSnapshot.getValue(User.class);

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+ownerUserProfile.getPhone()));//change the number
                startActivity(callIntent);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("[PetProfileActivity] Failed to read Owner Profile." + error.toException());
            }
        });
    }
}
