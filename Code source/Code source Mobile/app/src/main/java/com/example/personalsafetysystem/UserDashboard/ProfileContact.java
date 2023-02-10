package com.example.personalsafetysystem.UserDashboard;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.personalsafetysystem.LoginActivity;
import com.example.personalsafetysystem.Model.User;
import com.example.personalsafetysystem.R;
import com.example.personalsafetysystem.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileContact extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    Uri imageUri;
    private CircleImageView img;
    private Button btnBack,btnLogout;
    private TextView btnEdit,btnEditPwd,textNameTitle,textRole;
    private EditText textName,textPhone,textEmail1,textPassword,textConfimrPassword;
    private DatabaseReference userRef;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_contact);
        btnEdit = findViewById(R.id.btnEdit);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditPwd = findViewById(R.id.btnEditPwd);


        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        textName = findViewById(R.id.textName);
        textPhone = findViewById(R.id.textPhone);
        textEmail1 = findViewById(R.id.textEmail);
        textRole = findViewById(R.id.textRole);
        textNameTitle = findViewById(R.id.textName1);
        img = findViewById(R.id.img1);


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    textName.setText(user.getName());
                    textNameTitle.setText(user.getName());
                    textPhone.setText(user.getPhone());
                    textRole.setText(user.getRole());
                    textEmail1.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    Glide.with(img.getContext()).load(user.getImg_url()).placeholder(com.google.firebase.appcheck.interop.R.drawable.common_google_signin_btn_icon_dark).circleCrop().error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal).into(img);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileContact.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData(view);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutMethod();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileContact.this, ContactDashboard.class));
                finish();
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
        btnEditPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileContact.this);
                View dialogView = LayoutInflater.from(ProfileContact.this).inflate(R.layout.update_popup_pwd, null);
                builder.setView(dialogView);

                EditText textPassword = dialogView.findViewById(R.id.txtPassword);
                EditText textConfirmPassword = dialogView.findViewById(R.id.txtConfimrPassword);

                Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                // Increase dialog size
                Window window = alertDialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newPassword = textPassword.getText().toString();
                        String confirmPassword = textConfirmPassword.getText().toString();

                        if (newPassword.equals(confirmPassword)) {
                            // update the user's password
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProfileContact.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    } else {
                                        Toast.makeText(ProfileContact.this, "Failed to update password.", Toast.LENGTH_SHORT).show();
                                        Exception e = task.getException();
                                        Log.e("Update Password", "Error: " + e.getMessage());
                                    }

                                }
                            });
                        } else {
                            Toast.makeText(ProfileContact.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            CircleImageView imageView =findViewById(R.id.img1);
            imageView.setImageURI(imageUri);
        }
    }

    public void saveUserData(View view) {
        String name = textName.getText().toString();
        String phone = textPhone.getText().toString();
        String textEmail = textEmail1.getText().toString();
        if(imageUri!=null)
        {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + System.currentTimeMillis() + ".jpg");
            storageRef.putFile(imageUri);
        }


        if (user != null) {
            user.setName(name);
            user.setPhone(phone);
            if(imageUri!=null)
            {
                user.setImg_url(imageUri.toString());

            }


            FirebaseAuth.getInstance().getCurrentUser().updateEmail(textEmail);
            userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileContact.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileContact.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void signOutMethod()
    {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
