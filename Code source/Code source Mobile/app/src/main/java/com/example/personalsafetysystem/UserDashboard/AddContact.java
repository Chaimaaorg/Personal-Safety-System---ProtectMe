package com.example.personalsafetysystem.UserDashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.personalsafetysystem.BottomMenuFragments.ContactsFragment;
import com.example.personalsafetysystem.LoginActivity;
import com.example.personalsafetysystem.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddContact extends AppCompatActivity {

    EditText name, phone;
    Button btnAdd;

    static final int PICK_IMAGE_REQUEST = 1;
    private Button btnSelectImage;
    private Uri imageUri;
    private Button btnBack,btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        name = findViewById(R.id.txtName);
        phone = findViewById(R.id.txtPhone);

        btnAdd = findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchUserByPhone(phone.getText().toString());
            }
        });

        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ContactsFragment.class);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
        }
    }

    private void searchUserByPhone(String phoneNumber) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = usersRef.orderByChild("phone").equalTo(phoneNumber);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userId = dataSnapshot.getChildren().iterator().next().getKey();
                    addUserToContacts(userId);
                } else {
                    Toast.makeText(AddContact.this, "User not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddContact.this, "Error searching user.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserToContacts(String userId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + user.getUid() + "/" + System.currentTimeMillis() + ".jpg");
        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("contacts_list");

                                Map<String, Object> map = new HashMap<>();
                                map.put("name", name.getText().toString());
                                map.put("role","Emergency contact");
                                map.put("phone", phone.getText().toString());
                                map.put("img_url", imageUrl);


                                userRef.push().setValue(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(AddContact.this, "Data inserted successfully.", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddContact.this, "Error while inserting.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddContact.this, "Error while uploading image.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}