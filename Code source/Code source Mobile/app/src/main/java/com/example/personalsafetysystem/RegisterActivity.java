package com.example.personalsafetysystem;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {


    EditText textName, textEmail, textPassword, edtConfirmPassword,textPhone;
    Spinner spinner;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    TextView login;
    String txtFullName, txtEmail, txtName,txtPhone, txtPassword, txtConfirmPassword, selectedRole;

    Button btnSignUpAcc,btnSelectImage;

    String phonePattern = "^\\+(?:[0-9] ?){6,14}[0-9]$";

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        login = findViewById(R.id.login);
        textName = findViewById(R.id.textName);
        textEmail = findViewById(R.id.textEmail);
        textPhone = findViewById(R.id.textPhone);
        textPassword = findViewById(R.id.textPassword);
        spinner = findViewById(R.id.textRole);
        btnSelectImage = findViewById(R.id.btnChooseImage);
        btnSignUpAcc = findViewById(R.id.btnSignUpAcc);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Créer une liste d'options pour le Spinner
        List<String> options = new ArrayList<>();
        options.add("Principal user");
        options.add("Emergency contact");

        // Créer un ArrayAdapter pour alimenter le Spinner avec la liste d'options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);

        // Spécifier un layout pour afficher chaque élément de la liste déroulante
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Appliquer l'ArrayAdapter au Spinner
        spinner.setAdapter(adapter);

        // Ajouter un écouteur pour gérer les sélections de l'utilisateur
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Faire quelque chose lorsque l'utilisateur sélectionne un élément de la liste déroulante
                String selectedOption = options.get(position);
                Log.d("Spinner", "Option selected: " + selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Gérer le cas où aucun élément n'est sélectionné

            }
        });


        btnSignUpAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtName = textName.getText().toString().trim();
                txtPhone = textPhone.getText().toString().trim();
                txtEmail = textEmail.getText().toString().trim();
                txtPassword = textPassword.getText().toString().trim();
                selectedRole = spinner.getSelectedItem().toString();

                if (!TextUtils.isEmpty(txtName)) {
                    if (!TextUtils.isEmpty(txtPhone)) {
                        if (txtPhone.matches(phonePattern)) {
                            if (!TextUtils.isEmpty(txtEmail)) {
                                if (txtEmail.matches(emailPattern)) {
                                    if (!TextUtils.isEmpty(txtPassword)) {
                                                SignUpUser(txtName, txtPhone, txtEmail, txtPassword, selectedRole);

                                    } else {
                                        textPassword.setError("Password Field can't be empty");
                                    }

                                } else {
                                    textEmail.setError("Enter a valid Email Address");
                                }
                            } else {
                                textEmail.setError("Email Field can't be empty");
                            }
                        } else {
                            textPhone.setError("Enter a valid Phone Number");

                        }
                    }else {
                        textPhone.setError("Phone Field can't be empty");

                    }
                }
                else {
                    textName.setError("Name Field can't be empty");
                }
    }

        });
        btnSelectImage = findViewById(R.id.btnChooseImage);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ImageView imageView =findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
        }
    }

    private void SignUpUser(String txtName, String txtPhone, String txtEmail, String txtPassword, String selectedRole) {
        // Create a new Firebase user with the email and password.
        mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If the user is created successfully, upload the image to storage, then add the user to the database.
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + mAuth.getCurrentUser().getUid() + "/" + System.currentTimeMillis() + ".jpg");
                            storageRef.putFile(imageUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                                        downloadUrlTask.addOnSuccessListener(uri -> {
                                            String profileImageUrl = uri.toString();
                                            addNewUserToDatabase(userId,txtName, txtPhone, selectedRole, profileImageUrl);
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegisterActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addNewUserToDatabase(String userId,String txtName, String txtPhone, String selectedRole, String profileImageUrl)
    {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        // Generate a new unique key for the user
        String userKey = usersRef.push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put("name", txtName);
        map.put("role", selectedRole);
        map.put("phone", txtPhone);
        map.put("img_url", profileImageUrl);
        map.put("contacts_list", new HashMap<String, Object>());
        usersRef.setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RegisterActivity.this,"Sign Up Successful !",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this,"Error while inserting.",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    }


