package com.example.personalsafetysystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.personalsafetysystem.Adapters.ContactAdapter;
import com.example.personalsafetysystem.Model.User;
import com.example.personalsafetysystem.UserDashboard.ContactDashboard;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ListUsersForEmergContact extends AppCompatActivity {

    RecyclerView recyclerView;
    ContactAdapter contactAdapter;
    Button btnBack,btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_list_users_for_emerg_contact);
            btnBack = findViewById(R.id.btnBack);
            btnLogout = findViewById(R.id.btnLogout);
            recyclerView = findViewById(R.id.rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("role").equalTo("Principal user");
            FirebaseRecyclerOptions<User> options =
                    new FirebaseRecyclerOptions.Builder<User>()
                            .setQuery(query, User.class)
                            .build();
            contactAdapter = new ContactAdapter(options);
            recyclerView.setAdapter(contactAdapter);
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ContactDashboard.class);
                startActivity(intent);
                finish();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (contactAdapter != null) {
            contactAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (contactAdapter != null) {
            contactAdapter.stopListening();
        }
    }
}
