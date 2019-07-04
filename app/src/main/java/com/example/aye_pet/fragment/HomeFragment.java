package com.example.aye_pet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

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
    private Spinner mPets_querySpinner, mPets_queryValueSpinner;
    private String queryType, queryValue;
;
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
        mPets_querySpinner = (Spinner)view.findViewById(R.id.Home_querySpinner);
        mPets_queryValueSpinner = (Spinner)view.findViewById(R.id.Home_queryValueSpinner);
        queryType = mPets_querySpinner.getSelectedItem().toString().trim();
        queryValue = mPets_queryValueSpinner.getSelectedItem().toString().trim();

        mPets_querySpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        mPets_queryValueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                queryType = mPets_querySpinner.getSelectedItem().toString().trim();
                queryValue = mPets_queryValueSpinner.getSelectedItem().toString().trim();

                readData();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            if(parent.equals(mPets_querySpinner)){
                mPets_queryValueSpinner.setClickable(true);
                if(mPets_querySpinner.getSelectedItem().toString().trim().equals("All"))
                {
                    String [] stringsArray = getResources().getStringArray(R.array.pet_all);
                    ArrayAdapter <String> s1 = new ArrayAdapter <String> (getActivity(),android.R.layout.simple_spinner_dropdown_item, stringsArray);
                    s1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mPets_queryValueSpinner.setAdapter(s1);
                }
                else  if(mPets_querySpinner.getSelectedItem().toString().trim().equals("type"))
                {
                    String [] stringsArray = getResources().getStringArray(R.array.pet_type);
                    ArrayAdapter <String> s2 = new ArrayAdapter <String> (getActivity(),android.R.layout.simple_spinner_item,stringsArray);
                    s2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mPets_queryValueSpinner.setAdapter(s2);
                }
                else  if(mPets_querySpinner.getSelectedItem().toString().trim().equals("gender"))
                {
                    String [] stringsArray = getResources().getStringArray(R.array.pet_gender);
                    ArrayAdapter <String> s3 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,stringsArray);
                    s3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mPets_queryValueSpinner.setAdapter(s3);
                }
                else  if(mPets_querySpinner.getSelectedItem().toString().trim().equals("size"))
                {
                    String [] stringsArray = getResources().getStringArray(R.array.pet_size);
                    ArrayAdapter <String> s4 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,stringsArray);
                    s4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mPets_queryValueSpinner.setAdapter(s4);
                }
                else  if(mPets_querySpinner.getSelectedItem().toString().trim().equals("age"))
                {
                    String [] stringsArray = getResources().getStringArray(R.array.pet_age);
                    ArrayAdapter <String> s5 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,stringsArray);
                    s5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mPets_queryValueSpinner.setAdapter(s5);
                }

                queryType = mPets_querySpinner.getSelectedItem().toString().trim();
                queryValue = mPets_queryValueSpinner.getSelectedItem().toString().trim();

                readData();
            }
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }


    private void readData(){

        new FirebaseDatabaseHelper().readPets(queryType,queryValue, new FirebaseDatabaseHelper.DataStatus() {
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
    }

}
