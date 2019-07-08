package com.example.aye_pet.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.aye_pet.R;
import com.example.aye_pet.entity.FirebaseDatabaseHelper;
import com.example.aye_pet.entity.Pet;
import com.example.aye_pet.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PetProfileActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView name, type, gender, size, age, vaccinated, dewormed, neutered, location, description;
    private Button btn_email, btn_call, btn_edit, btn_delete;

    private User currentUser;
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
        btn_delete = findViewById(R.id.PetProfile_deleteButton);

        if(pet.getOwnerId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            btn_email.setVisibility(View.GONE);
            btn_call.setVisibility(View.GONE);
        }else {
            btn_edit.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
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

        FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentUser = dataSnapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PetProfileActivity.this);
                builder.setCancelable(true)
                        .setTitle(pet.getName())
                        .setMessage("Email My Owner?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendEmailToOwner();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();

            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PetProfileActivity.this);
                builder.setCancelable(true)
                        .setTitle(pet.getName())
                        .setMessage("Call My Owner?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                phoneCallToOwner();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PetProfileActivity.this, UpdatePetActivity.class);
                intent.putExtra("PET",pet);
                intent.putExtra("KEY",petKey);
                startActivity(intent);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PetProfileActivity.this);
                builder.setCancelable(true)
                        .setTitle("Pet Profile")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new FirebaseDatabaseHelper().deletePet(petKey, new FirebaseDatabaseHelper.DataStatus() {
                                    @Override
                                    public void DataIsLoaded(List<Pet> pets, List<String> keys) {

                                    }

                                    @Override
                                    public void DataIsInserted() {

                                    }

                                    @Override
                                    public void DataIsUpdated() {

                                    }

                                    @Override
                                    public void DataIsDeleted() {
                                        Toast.makeText(PetProfileActivity.this, pet.getName() + "'s profile has been deleted", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
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
                subject = "[Aye!Pet] Request to adopt pet: " + pet.getName();
                text = "Dear "+ ownerUserProfile.getLastName() + "\n\nI'm interested in adopting your pet.\nPet Name : " + pet.getName() +
                        "\nPost Date : "+ pet.getPostedDate() + "\n\nLook forward to your favourable reply.\n\n\nThank you.\n\nRegards,\n"+
                        currentUser.getFirstName() +" "+ currentUser.getLastName();

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, text);

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    Toast.makeText(PetProfileActivity.this,
                            "Email sent successfully", Toast.LENGTH_SHORT).show();
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
