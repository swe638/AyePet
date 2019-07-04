package com.example.aye_pet;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aye_pet.activity.PetProfileActivity;
import com.example.aye_pet.entity.Pet;

import java.util.List;

public class PetRecyclerViewAdapter extends RecyclerView.Adapter<PetRecyclerViewAdapter.PetViewHolder> {

    private Context mContext;
    private List<Pet> mPetList;
    private List<String> mPetKeys;

    public PetRecyclerViewAdapter(Context mContext, List<Pet> mPetList, List<String> mPetKeys) {
        this.mContext = mContext;
        this.mPetList = mPetList;
        this.mPetKeys = mPetKeys;
    }

    @NonNull
    @Override
    public PetRecyclerViewAdapter.PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.pet_list_item, parent, false);

        return new PetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PetRecyclerViewAdapter.PetViewHolder holder, int position) {
        Pet pet = mPetList.get(position);
        Glide.with(holder.mPetImage.getContext()).load(pet.getImageURL()).into(holder.mPetImage);
        holder.mName.setText(pet.getName());
        holder.mLocation.setText(pet.getLocation());
        holder.mDescription.setText(pet.getDescription());
        holder.pet = pet;
        holder.key = mPetKeys.get(position);

    }

    @Override
    public int getItemCount() {
        return mPetList.size();
    }

    class PetViewHolder extends RecyclerView.ViewHolder {
        ImageView mPetImage;
        TextView mName;
        TextView mLocation;
        TextView mDescription;

        Pet pet;
        String key;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);

            mPetImage = itemView.findViewById(R.id.imageView_pet);
            mName = itemView.findViewById(R.id.pet_list_name);
            mLocation = itemView.findViewById(R.id.pet_list_location);
            mDescription = itemView.findViewById(R.id.pet_list_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent petProfileActivity = new Intent(mContext, PetProfileActivity.class);
                    petProfileActivity.putExtra("PET", pet);
                    petProfileActivity.putExtra("KEY", key);
                    mContext.startActivity(petProfileActivity);
                }
            });
        }
    }
}
