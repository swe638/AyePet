package com.example.aye_pet.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.aye_pet.R;
import com.example.aye_pet.entity.FirebaseDatabaseHelper;
import com.example.aye_pet.entity.Pet;
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
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class AddPetFragment extends Fragment {
    private String userId;
    private Pet newPet;

    private ImageButton imageButton;
    private EditText et_name, et_location, et_description;
    private Spinner s_type, s_gender, s_size, s_age;
    private RadioGroup rg_vaccinated, rg_dewormed, rg_neutered;
    private Button btn_submit;

    private String vaccinated, dewormed, neutered;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Activity mActivity;

    private Uri filePath;
    private String imageURL;
    private final int PICK_IMAGE_REQUEST = 71;

    public AddPetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = firebaseDatabase.getReference("pet");
        storageReference = firebaseStorage.getReference("pet");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();




    }

    @Override
    public void onViewCreated(View view, Bundle savedInstaceState) {

        HideSoftKeyboard(et_name);
        HideSoftKeyboard(et_location);
        HideSoftKeyboard(et_description);

        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                chooseImage();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setCancelable(true)
                        .setTitle("Add New Pet")
                        .setMessage("Confirm?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
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
        rg_vaccinated.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.vaccinated_yes:
                        vaccinated = "Yes";
                        break;
                    case R.id.vaccinated_no:
                        vaccinated = "No";
                        break;
                }
            }
        });
        rg_dewormed.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.dewormed_yes:
                        dewormed = "Yes";
                        break;
                    case R.id.dewormed_no:
                        dewormed = "No";
                        break;
                }
            }
        });
        rg_neutered.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.neutered_yes:
                        neutered = "Yes";
                        break;
                    case R.id.neutered_no:
                        neutered = "No";
                        break;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_pet, container, false);
        imageButton = view.findViewById(R.id.AddPet_imageButton);
        et_name = view.findViewById(R.id.AddPet_name);
        s_type = view.findViewById(R.id.AddPet_type);
        s_gender = view.findViewById(R.id.AddPet_gender);
        s_size = view.findViewById(R.id.AddPet_size);
        s_age = view.findViewById(R.id.AddPet_age);
        et_location = view.findViewById(R.id.AddPet_location);
        et_description = view.findViewById(R.id.AddPet_description);
        rg_vaccinated = view.findViewById(R.id.AddPet_vaccinatedGroup);
        rg_dewormed = view.findViewById(R.id.AddPet_dewormedGroup);
        rg_neutered = view.findViewById(R.id.AddPet_neuteredGroup);
        btn_submit = view.findViewById(R.id.AddPet_submitButton);

        return view;
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getApplicationContext().getContentResolver(), filePath);
                Glide.with(getActivity().getApplicationContext()).load(bitmap).centerCrop().into(imageButton);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private boolean checkPetProfile(){
        String name = et_name.getText().toString().trim();
        String type = s_type.getSelectedItem().toString().trim();
        String gender = s_gender.getSelectedItem().toString().trim();
        String size = s_size.getSelectedItem().toString().trim();
        String age = s_age.getSelectedItem().toString().trim();
        String location = et_location.getText().toString().trim();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String postedDate = formatter.format(date);
        String description = et_description.getText().toString().trim();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(type) && !TextUtils.isEmpty(gender) && !TextUtils.isEmpty(size) && !TextUtils.isEmpty(age)
                && !TextUtils.isEmpty(location) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(vaccinated) && !TextUtils.isEmpty(dewormed) && !TextUtils.isEmpty(neutered)) {
            imageURL = "";
            newPet = new Pet(userId, name, type, gender, size, age, vaccinated, dewormed, neutered, location, postedDate, description, imageURL);
            return true;
        }else{
            Toast.makeText(mActivity, "Please enter all information", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void uploadImage() {
        if(checkPetProfile()==true){
            if(filePath != null)
            {
                final ProgressDialog progressDialog = new ProgressDialog(mActivity);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                final String newPetId = databaseReference.push().getKey();

                final StorageReference ref = storageReference.child(newPetId);

                ref.putFile(filePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            Uri downloadUri = task.getResult();
                            imageURL = downloadUri.toString();
                            newPet.setImageURL(imageURL);
                            uploadPetData();
                            progressDialog.hide();
                        } else {
                            progressDialog.hide();
                            Toast.makeText(mActivity, "upload image failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(mActivity, "Please choose an image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadPetData(){
            new FirebaseDatabaseHelper().addPet(newPet, new FirebaseDatabaseHelper.DataStatus() {
                @Override
                public void DataIsLoaded(List<Pet> pets, List<String> keys) {

                }

                @Override
                public void DataIsInserted() {
                    Toast.makeText(mActivity, "Pet profile has been created successfully", Toast.LENGTH_SHORT).show();
                    System.out.println(imageURL);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeFragment()).commit();
                }

                @Override
                public void DataIsUpdated() {

                }

                @Override
                public void DataIsDeleted() {

                }
            });
    }

    private void HideSoftKeyboard(@NotNull EditText et) {
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager ime = (InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE);
                ime.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }
}
