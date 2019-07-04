package com.example.aye_pet.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aye_pet.PetRecyclerViewAdapter;
import com.example.aye_pet.R;
import com.example.aye_pet.entity.FirebaseDatabaseHelper;
import com.example.aye_pet.entity.Pet;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MyPetListActivity extends AppCompatActivity {

    private RecyclerView mPets_RecyclerView;
    private ProgressBar mPets_ProgressBar;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pet_list);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mPets_ProgressBar = (ProgressBar)findViewById(R.id.MyPetList_loading_pd);

        mPets_RecyclerView = (RecyclerView)findViewById(R.id.MyPetList_recyclerView);
        new FirebaseDatabaseHelper().readMyPets(currentUserId, new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Pet> pets, List<String> keys) {
                PetRecyclerViewAdapter adapter = new PetRecyclerViewAdapter(MyPetListActivity.this, pets, keys);
                mPets_RecyclerView.setAdapter(adapter);
                mPets_RecyclerView.setLayoutManager(new LinearLayoutManager(MyPetListActivity.this));
                mPets_ProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }
}
