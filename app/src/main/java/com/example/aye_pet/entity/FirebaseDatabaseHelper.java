package com.example.aye_pet.entity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferemcePets;
    private List<Pet> pets = new ArrayList<>();

    public interface DataStatus{
        void DataIsLoaded(List<Pet> pets, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferemcePets = mDatabase.getReference("pet");
    }

    public void readPets(String queryType, String queryValue, final DataStatus dataStatus){
        Query mQuery;

//        queryType = "All";
//        queryValue = "All";
        System.out.println(queryType + queryValue);

        if(queryType.equals("All") && queryValue.equals("All")){
            mQuery = mReferemcePets;
        } else {
            mQuery = mReferemcePets.orderByChild(queryType).equalTo(queryValue);
        }

        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pets.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Pet pet = keyNode.getValue(Pet.class);
                    pets.add(pet);
                }

                dataStatus.DataIsLoaded(pets,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void readMyPets(String userId, final DataStatus dataStatus){
        mReferemcePets.orderByChild("ownerId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pets.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Pet pet = keyNode.getValue(Pet.class);
                    pets.add(pet);
                }
                dataStatus.DataIsLoaded(pets,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addPet(Pet pet, final DataStatus dataStatus){
        String key = mReferemcePets.push().getKey();
        mReferemcePets.child(key).setValue(pet)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsInserted();
                    }
                });
    }

    public void updatePet(String key, Pet pet, final DataStatus dataStatus){
        mReferemcePets.child(key).setValue(pet)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsUpdated();
                    }
                });
    }

    public void deletePet(String key, final DataStatus dataStatus){
        mReferemcePets.child(key).setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsDeleted();
                    }
                });
    }
}
