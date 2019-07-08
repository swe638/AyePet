package com.example.aye_pet.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.aye_pet.R;
import com.example.aye_pet.entity.FirebaseDatabaseHelper;
import com.example.aye_pet.entity.Pet;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;

public class UpdatePetActivity extends AppCompatActivity {
    private ImageButton imageButton;
    private EditText et_name, et_location, et_description;
    private Spinner s_type, s_gender, s_size, s_age;
    private RadioGroup rg_vaccinated, rg_dewormed, rg_neutered;
    private Button btn_update, btn_delete;
    private String vaccinated, dewormed, neutered;

    private Pet pet;
    private String key;
    private Pet newPet;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private Uri filePath;
    private String imageURL;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pet);

        imageButton = findViewById(R.id.UpdatePet_imageButton);
        et_name = findViewById(R.id.UpdatePet_name);
        s_type = findViewById(R.id.UpdatePet_type);
        s_gender = findViewById(R.id.UpdatePet_gender);
        s_size = findViewById(R.id.UpdatePet_size);
        s_age = findViewById(R.id.UpdatePet_age);
        et_location = findViewById(R.id.UpdatePet_location);
        et_description = findViewById(R.id.UpdatePet_description);
        rg_vaccinated = findViewById(R.id.UpdatePet_vaccinatedGroup);
        rg_dewormed = findViewById(R.id.UpdatePet_dewormedGroup);
        rg_neutered = findViewById(R.id.UpdatePet_neuteredGroup);
        btn_update = findViewById(R.id.UpdatePet_updateButton);

        pet = (Pet) getIntent().getSerializableExtra("PET");
        key = getIntent().getStringExtra("KEY");

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("pet");

        Glide.with(getApplicationContext()).load(pet.getImageURL()).into(imageButton);
        imageURL = pet.getImageURL();
        et_name.setText(pet.getName());
        selectSpinnerItemByValue(s_type, pet.getType());
        selectSpinnerItemByValue(s_gender, pet.getGender());
        selectSpinnerItemByValue(s_size, pet.getSize());
        selectSpinnerItemByValue(s_age, pet.getAge());
        System.out.println(pet.getNeutered()+pet.getDewormed()+pet.getVaccinated());
        selectRadioGroupByIndex(rg_vaccinated, pet.getVaccinated());
        vaccinated = pet.getVaccinated();
        selectRadioGroupByIndex(rg_dewormed, pet.getDewormed());
        dewormed = pet.getDewormed();
        selectRadioGroupByIndex(rg_neutered, pet.getNeutered());
        neutered = pet.getNeutered();
        et_location.setText(pet.getLocation());
        et_description.setText(pet.getDescription());

        HideSoftKeyboard(et_name);
        HideSoftKeyboard(et_location);
        HideSoftKeyboard(et_description);

        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                chooseImage();
            }
        });

        rg_vaccinated.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.UpdatePet_vaccinated_yes:
                        vaccinated = "Yes";
                        break;
                    case R.id.UpdatePet_vaccinated_no:
                        vaccinated = "No";
                        break;
                }
            }
        });
        rg_dewormed.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.UpdatePet_dewormed_yes:
                        dewormed = "Yes";
                        break;
                    case R.id.UpdatePet_dewormed_no:
                        dewormed = "No";
                        break;
                }
            }
        });
        rg_neutered.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.UpdatePet_neutered_yes:
                        neutered = "Yes";
                        break;
                    case R.id.UpdatePet_neutered_no:
                        neutered = "No";
                        break;
                }
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdatePetActivity.this);
                builder.setCancelable(true)
                        .setTitle("Update Pet Profile")
                        .setMessage("Save changes?")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                upload();
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

    private void selectSpinnerItemByValue(Spinner spnr, @NotNull String value) {
        for (int position = 0; position < spnr.getCount(); position++) {
            if(spnr.getItemAtPosition(position).equals(value)) {
                spnr.setSelection(position);
            }
        }
    }

    private void selectRadioGroupByIndex (RadioGroup rg, String value){
        if (value.equals("Yes")){
            rg.check(rg.getChildAt(1).getId());
        } else if (value.equals("No")){
            rg.check(rg.getChildAt(2).getId());
        }
    }

    private void HideSoftKeyboard(@NotNull  EditText et) {
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager ime = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                ime.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(UpdatePetActivity.this.getApplicationContext().getContentResolver(), filePath);
                imageButton.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void upload() {
        if(checkPetProfile()==true) {
            final ProgressDialog progressDialog = new ProgressDialog(UpdatePetActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            if (filePath != null) {
                final StorageReference ref = storageReference.child(key);

                ref.putFile(filePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            imageURL = downloadUri.toString();
                            newPet.setImageURL(imageURL);
                            uploadPetData();
                        } else {
                            progressDialog.hide();
                            Toast.makeText(UpdatePetActivity.this, "upload image failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                uploadPetData();
            }
        }
    }

    private boolean checkPetProfile(){
        String userId = pet.getOwnerId();
        String name = et_name.getText().toString().trim();
        String type = s_type.getSelectedItem().toString().trim();
        String gender = s_gender.getSelectedItem().toString().trim();
        String size = s_size.getSelectedItem().toString().trim();
        String age = s_age.getSelectedItem().toString().trim();
        String location = et_location.getText().toString().trim();
        String postedDate = pet.getPostedDate();
        String description = et_description.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(type) && !TextUtils.isEmpty(gender) && !TextUtils.isEmpty(size) && !TextUtils.isEmpty(age)
                && !TextUtils.isEmpty(location) && !TextUtils.isEmpty(description)) {
            newPet = new Pet(userId, name, type, gender, size, age, vaccinated, dewormed, neutered, location, postedDate, description, imageURL);
            return true;
        } else {
            Toast.makeText(UpdatePetActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void uploadPetData(){
            new FirebaseDatabaseHelper().updatePet(key, newPet, new FirebaseDatabaseHelper.DataStatus() {
                @Override
                public void DataIsLoaded(List<Pet> pets, List<String> keys) {

                }

                @Override
                public void DataIsInserted() {

                }

                @Override
                public void DataIsUpdated() {
                    Toast.makeText(UpdatePetActivity.this, "Pet profile has been Updated successfully", Toast.LENGTH_SHORT).show();
//                    System.out.println(imageURL);
                    finish();
                }

                @Override
                public void DataIsDeleted() {

                }
            });
    }
}
