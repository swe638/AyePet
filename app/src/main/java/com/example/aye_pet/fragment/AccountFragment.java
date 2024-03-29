package com.example.aye_pet.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.aye_pet.R;
import com.example.aye_pet.activity.DonationActivity;
import com.example.aye_pet.activity.LoginActivity;
import com.example.aye_pet.activity.MyPetListActivity;
import com.example.aye_pet.activity.ReferFriendActivity;
import com.example.aye_pet.activity.UserProfileActivity;
import com.example.aye_pet.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    ImageView iv_userImage;
    TextView tv_name, tv_userId;
    Button btn_myProfile, btn_myPets, btn_donate, btn_referFriend, btn_logout;

    String currentUserId;

    private Activity mActivity;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        iv_userImage = view.findViewById(R.id.Account_imageView);
        tv_name = view.findViewById(R.id.Account_name);
        tv_userId = view.findViewById(R.id.Account_userId);
        btn_myProfile = view.findViewById(R.id.Account_profileButton);
        btn_myPets = view.findViewById(R.id.Account_myPetButton);
        btn_donate = view.findViewById(R.id.Account_donateUsButton);
        btn_referFriend = view.findViewById(R.id.Account_referFriendButton);
        btn_logout = view.findViewById(R.id.Account_logoutButton);


        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstaceState) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("user");
        myRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                if (mActivity == null) {
                    return;
                }
                Glide.with(mActivity).load(user.getImageURL()).placeholder(R.drawable.ic_account_circle_black_24dp)
                        .error(R.drawable.ic_account_circle_black_24dp).into(iv_userImage);
                tv_name.setText(user.getFirstName());
                tv_userId.setText(currentUserId);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("[UserProfileActivity] Failed to read value." + error.toException());
            }
        });

        btn_myProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(mActivity, UserProfileActivity.class);
                intent.putExtra("TARGET_USER", currentUserId);
                startActivity(intent);
            }
        });

        btn_myPets.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(mActivity, MyPetListActivity.class);
                startActivity(intent);
            }
        });

        btn_donate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(mActivity, DonationActivity.class);
                startActivity(intent);
            }
        });

        btn_referFriend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(mActivity, ReferFriendActivity.class);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setCancelable(true)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(mActivity, LoginActivity.class));
                                mActivity.finish();
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
