package com.example.personalsafetysystem.UserDashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.personalsafetysystem.BottomMenuFragments.ContactsFragment;
import com.example.personalsafetysystem.LoginActivity;
import com.example.personalsafetysystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends AppCompatActivity {

    CircleImageView photo_contact;
    Button btnBack, btnLogout, btnDelete;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_contact_emerg);

        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        btnDelete = findViewById(R.id.btnDelete);

        String name = getIntent().getStringExtra("name");
        String number = getIntent().getStringExtra("phone");
        String imageUrl = getIntent().getStringExtra("img_url");
        TextView nameTextView = findViewById(R.id.txtName);
        nameTextView.setText(name);

        TextView mobileTextView = findViewById(R.id.txtPhone);
        mobileTextView.setText(number);

        photo_contact = findViewById(R.id.imageView);
        Glide.with(this).load(imageUrl)
                .placeholder(com.google.firebase.appcheck.interop.R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop().error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(photo_contact);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactsFragment.class);
                startActivity(intent);
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

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("Are you sure?");
                builder.setMessage("Deleted Data can't be undone");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String contactId = getIntent().getStringExtra("phone");

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = user.getUid();

                        DatabaseReference contactsRef = FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(uid)
                                .child("contacts_list");

                        Query query = contactsRef.orderByChild("phone").equalTo(contactId);

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(DetailActivity.this, "Contact deleted successfully.", Toast.LENGTH_SHORT).show();
                                                        // Rediriger vers la liste des contacts ou fermer l'activité
                                                        // en fonction de votre logique de navigation
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(DetailActivity.this, "Error deleting contact.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.makeText(DetailActivity.this, "Contact not found.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(DetailActivity.this, "Error deleting contact.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(DetailActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
    }
}