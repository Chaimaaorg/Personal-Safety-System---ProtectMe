package com.example.personalsafetysystem.BottomMenuFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalsafetysystem.Adapters.GridAdapter;
import com.example.personalsafetysystem.HomeFragment;
import com.example.personalsafetysystem.LoginActivity;
import com.example.personalsafetysystem.Model.User;
import com.example.personalsafetysystem.R;
import com.example.personalsafetysystem.UserDashboard.AddContact;
import com.example.personalsafetysystem.UserDashboard.ContactDashboard;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

// ...

public class ContactsFragment extends Fragment {
    RecyclerView recyclerView;
    GridAdapter contactAdapter;
    Button btnBack,btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_contact, container, false);

        btnBack = view.findViewById(R.id.btnBack);
        btnLogout = view.findViewById(R.id.btnLogout);
        recyclerView = view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            String uid = user.getUid();

            Query query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(uid)
                    .child("contacts_list");

            FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                    .setQuery(query, User.class)
                    .build();


            contactAdapter = new GridAdapter(options);
            recyclerView.setAdapter(contactAdapter);
        }
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), AddContact.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), LoginActivity.class));

            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (contactAdapter != null) {
            contactAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (contactAdapter != null) {
            contactAdapter.stopListening();
        }
    }
}
