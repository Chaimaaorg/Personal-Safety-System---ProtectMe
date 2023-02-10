package com.example.personalsafetysystem.UserDashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalsafetysystem.Adapters.GridAdapter;
import com.example.personalsafetysystem.LoginActivity;
import com.example.personalsafetysystem.Model.User;
import com.example.personalsafetysystem.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ListContact extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridAdapter gridAdapter;
    private DatabaseReference contactsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.list_contact);

            recyclerView = findViewById(R.id.rv);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();

            // Référence à la liste des contacts de l'utilisateur actuel
            contactsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("contacts_list");

            Query query = contactsRef;

            FirebaseRecyclerOptions<User> options =
                    new FirebaseRecyclerOptions.Builder<User>()
                            .setQuery(query, User.class)
                            .build();

            gridAdapter = new GridAdapter(options);
            recyclerView.setAdapter(gridAdapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (gridAdapter != null) {
            gridAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (gridAdapter != null) {
            gridAdapter.stopListening();
        }
    }
}