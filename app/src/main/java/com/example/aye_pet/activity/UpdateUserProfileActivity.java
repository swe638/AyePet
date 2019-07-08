package com.example.aye_pet.activity;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.aye_pet.R;
import com.example.aye_pet.entity.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class UpdateUserProfileActivity extends AppCompatActivity {
    private String userId;
    private User oldProfile;
    private String email;
    private User newProfile;

    private EditText et_fname, et_lname, et_address, et_phone;
    private TextView et_bday;
    private RadioGroup rg_gender;
    private String gender;
    private Calendar myCalendar;
    private Button btn_submit;
    private ImageButton ib_image;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private Uri filePath;
    private String imageURL;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_profile);

        et_fname = findViewById(R.id.FTime_fname);
        et_lname = findViewById(R.id.FTime_lname);
        rg_gender = findViewById(R.id.FTime_vaccinatedGroup);
        et_bday = findViewById(R.id.FTime_birthday);
        et_address = findViewById(R.id.FTime_address);
        et_phone = findViewById(R.id.FTime_phone);
        btn_submit = findViewById(R.id.FTime_button_submit);
        ib_image = findViewById(R.id.FTime_imageButton);
        imageURL = "null";

        if(getIntent()!=null && getIntent().getExtras()!=null){
            Bundle bundle = getIntent().getExtras();
            if(!bundle.getSerializable("USER_PROFILE").equals(null)){
                oldProfile = (User) bundle.getSerializable("USER_PROFILE");

                imageURL = oldProfile.getImageURL();
                Glide.with(getApplicationContext()).load(oldProfile.getImageURL()).into(ib_image);
                et_fname.setText(oldProfile.getFirstName());
                et_lname.setText(oldProfile.getLastName());
                et_bday.setText(oldProfile.getBirthday());
                et_phone.setText(oldProfile.getPhone());
                et_address.setText(oldProfile.getAddress());
                if (oldProfile.getGender().equals("Male")){
                    rg_gender.check(rg_gender.getChildAt(0).getId());
                } else if (oldProfile.getGender().equals("Female")){
                    rg_gender.check(rg_gender.getChildAt(1).getId());
                }
                gender = oldProfile.getGender();
            }
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference("user");
        storageReference = firebaseStorage.getReference("user");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        myCalendar = Calendar.getInstance();

        HideSoftKeyboard(et_fname);
        HideSoftKeyboard(et_lname);
        HideSoftKeyboard(et_address);
        HideSoftKeyboard(et_phone);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        et_bday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(UpdateUserProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        ib_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        rg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.FTime_radio_male:
                        gender = "Male";
                        break;
                    case R.id.FTime_radio_female:
                        gender = "Female";
                        break;
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateUserProfileActivity.this);
                builder.setCancelable(true)
                        .setTitle("Update User Profile")
                        .setMessage("Submit?")
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadImage();
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

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_bday.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean checkNewUserProfile(){
        String fname = et_fname.getText().toString().trim();
        String lname = et_lname.getText().toString().trim();
        String bday = et_bday.getText().toString().trim();
        String address = et_address.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();

        if(!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(lname) && !TextUtils.isEmpty(bday)
                && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(gender) ) {
            if (phone.matches("^(?=(?:[0]){1})(?=[0-9]{9,11}).*")) {
                newProfile = new User(email, fname, lname, gender, bday, address, phone, imageURL);
                return true;
            }else {
                Toast.makeText(UpdateUserProfileActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(UpdateUserProfileActivity.this, "Please enter full information", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void uploadUserData(){
        databaseReference.child(userId).setValue(newProfile);
        if(getIntent()==null && getIntent().getExtras()==null) {
            startActivity(new Intent(UpdateUserProfileActivity.this, MainActivity.class));
        }
        finish();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ib_image.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if(checkNewUserProfile()==true) {
            if (filePath != null) {

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                final StorageReference ref = storageReference.child(userId);

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
                            newProfile.setImageURL(imageURL);
                            uploadUserData();
                        } else {
                            Toast.makeText(UpdateUserProfileActivity.this, "Upload image failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                if (!imageURL.equals("null")) {
                    newProfile.setImageURL(imageURL);
                } else {
                    newProfile.setImageURL("");
                }
                uploadUserData();
            }
        }
    }
}
