package com.example.aye_pet;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aye_pet.activity.UpdatePetActivity;
import com.example.aye_pet.entity.Pet;

import java.util.List;


public class RecyclerView_Config {
    private Context mContext;
    private PetsAdapter mPetsAdapter;

    public void setConfig(RecyclerView recyclerView, Context context, List<Pet> pets, List<String> keys){
        mContext = context;
        mPetsAdapter = new PetsAdapter(pets, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mPetsAdapter);
    }

    class PetItemView extends RecyclerView.ViewHolder{
        private ImageView mPetImage;
        private TextView mName;
        private TextView mLocation;
        private TextView mDescription;

        private Pet pet;
        private String key;

        public PetItemView (ViewGroup parent){
            super(LayoutInflater.from(mContext).inflate(R.layout.pet_list_item, parent, false));

            mPetImage = itemView.findViewById(R.id.imageView_pet);
            mName = itemView.findViewById(R.id.pet_list_name);
            mLocation = itemView.findViewById(R.id.pet_list_location);
            mDescription = itemView.findViewById(R.id.pet_list_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UpdatePetActivity.class);
                    intent.putExtra("PET", pet);
                    intent.putExtra("KEY", key);
                    mContext.startActivity(intent);

                }
            });
        }

        public void bind(Pet pet, String key){
            Glide.with(mContext).load(pet.getImageURL()).into(mPetImage);
            mName.setText(pet.getName());
            mLocation.setText(pet.getLocation());
            mDescription.setText(pet.getDescription());
            this.pet = pet;
            this.key = key;
        }
    }

    class PetsAdapter extends RecyclerView.Adapter<PetItemView>{
        private List<Pet> mPetList;
        private List<String> mKeys;

        public PetsAdapter(List<Pet> mPetList, List<String> mKeys) {
            this.mPetList = mPetList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public PetItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PetItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PetItemView holder, int position) {
            holder.bind(mPetList.get(position), mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mPetList.size();
        }
    }
}
