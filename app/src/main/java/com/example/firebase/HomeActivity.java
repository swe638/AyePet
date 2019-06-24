package com.example.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {
    private SessionHandler session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Button logoutBtn = findViewById(R.id.btnLogout);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search_pet:
                        Intent intent1 = new Intent(HomeActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.action_map:
                        Intent intent2 = new Intent(HomeActivity.this, LocationActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.action_add:
                        Intent intent3 = new Intent(HomeActivity.this, ListPetActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.action_profile:
                        Intent intent4 = new Intent(HomeActivity.this, AccountActivity.class);
                        startActivity(intent4);
                        break;
                }
                return true;
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_back) {
            Toast.makeText(getApplicationContext(), "Back clicked",
                    Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_chat) {
            Toast.makeText(getApplicationContext(), "Chat clicked",
                    Toast.LENGTH_SHORT).show();

        }
        return true;

}

}
