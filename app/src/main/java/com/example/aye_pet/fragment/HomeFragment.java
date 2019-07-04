package com.example.aye_pet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aye_pet.PetRecyclerViewAdapter;
import com.example.aye_pet.R;
import com.example.aye_pet.entity.FirebaseDatabaseHelper;
import com.example.aye_pet.entity.Pet;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView mPets_RecyclerView;
    private ProgressBar mPets_ProgressBar;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mPets_RecyclerView = (RecyclerView)view.findViewById(R.id.Home_PetRecyclerView);
        mPets_ProgressBar = (ProgressBar)view.findViewById(R.id.Home_loading_pets_pb);

        new FirebaseDatabaseHelper().readPets( new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Pet> pets, List<String> keys) {

                PetRecyclerViewAdapter adapter = new PetRecyclerViewAdapter(getActivity(), pets, keys);
                mPets_RecyclerView.setAdapter(adapter);
                mPets_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

        return view;
    }

}
