package com.example.personalsafetysystem.BottomMenuFragments;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.personalsafetysystem.Model.User;
import com.example.personalsafetysystem.R;
import com.example.personalsafetysystem.StepCounterHelper;
import com.example.personalsafetysystem.UserDashboard.UserDashboard;
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
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private static final int SMS_PERMISSION_REQUEST_CODE = 1;

    Dialog myDialog;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private CircleImageView img, imgPopup;
    private Button btnEdit;
    private LinearLayout btnHeartbeatsPopup;
    private EditText edtPassword, edtEmail, edtPhone;
    private TextView textName, textNamePopup, textEmailPopup, textEmail1, textPhone, heartBeatsTextView;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private User user;
    private StepCounterHelper stepCounterHelper;
    private TextView stepCountTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnEdit = view.findViewById(R.id.btnEdit);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        stepCountTextView = view.findViewById(R.id.NbrOfSteps);
        heartBeatsTextView = view.findViewById(R.id.Heartbeats);
        stepCounterHelper = new StepCounterHelper(requireContext(), stepCountTextView);
        stepCounterHelper.start();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        }

        textName = view.findViewById(R.id.textName);
        textPhone = view.findViewById(R.id.edtPhone);
        textEmail1 = view.findViewById(R.id.textEmail);
        img = view.findViewById(R.id.profile_image);
        btnHeartbeatsPopup = view.findViewById(R.id.btnHeartbeatsPopup);
        textNamePopup = view.findViewById(R.id.textNamePopup);
        textEmailPopup = view.findViewById(R.id.textEmailPopup);
        imgPopup = view.findViewById(R.id.imgPopup);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    textName.setText(user.getName());
                    textPhone.setText(user.getPhone());
                    textEmail1.setText(currentUser.getEmail());
                    edtEmail.setText(currentUser.getEmail());
                    Glide.with(requireContext()).load(user.getImg_url()).placeholder(R.drawable.common_google_signin_btn_icon_dark).circleCrop().error(R.drawable.common_google_signin_btn_icon_dark_normal).into(img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();
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

        getHeartbeats();

        btnHeartbeatsPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(img.getContext())
                        .setContentHolder(new ViewHolder(R.layout.heartbeats_popup))
                        .setExpanded(true, 1200)
                        .setContentBackgroundResource(android.R.color.transparent)
                        .create();

                View dialogView = dialogPlus.getHolderView();
                TextView txtclose = dialogView.findViewById(R.id.txtclose);
                TextView name = dialogView.findViewById(R.id.textNamePopup);
                TextView email = dialogView.findViewById(R.id.textEmailPopup);
                CircleImageView imgPopup = dialogView.findViewById(R.id.imgPopup);
                name.setText(user.getName());
                email.setText(currentUser.getEmail());
                Glide.with(requireContext()).load(user.getImg_url()).placeholder(R.drawable.common_google_signin_btn_icon_dark).circleCrop().error(R.drawable.common_google_signin_btn_icon_dark_normal).into(imgPopup);
                txtclose.setText("X");
                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogPlus.dismiss();
                    }
                });
                dialogPlus.show();
            }
        });
        checkSMSPermission();

    }

    private void getHeartbeats() {
        DatabaseReference heartRef = FirebaseDatabase.getInstance().getReference("HEART");
        heartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String heartbeat = dataSnapshot.child("heartbeat").getValue(String.class);
                    heartBeatsTextView.setText(heartbeat);
                    int heartbeatInt = Integer.parseInt(heartbeat);
                    if(heartbeatInt <60 || heartbeatInt>100)
                    {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle cancellation errors here
            }
        });
    }
    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, send the SMS
            sendSMS();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, send the SMS
                sendSMS();
            } else {
                // Permission is denied, show a toast message
                Toast.makeText(requireContext(), "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void sendSMS() {
        DatabaseReference refContacts = FirebaseDatabase.getInstance().getReference().child("Users");
        refContacts.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contacts_list")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                            for (DataSnapshot contactSnapshot : dataSnapshot.getChildren()) {
                                // Retrieve the contact info
                                String phoneNumber = contactSnapshot.child("phone").getValue(String.class);
                                if (phoneNumber != null) {
                                    // Create an instance of GpsTracker
                                    ProfileFragment.GpsTracker gpsTracker = new ProfileFragment.GpsTracker(requireContext());

                                    // Check if location can be obtained
                                    if (gpsTracker.canGetLocation()) {
                                        double latitude = gpsTracker.getLatitude();
                                        double longitude = gpsTracker.getLongitude();

                                        // Use the latitude and longitude values as needed
                                        String message = "I am experiencing a bradycardia or tachycardia ! https://www.google.com/maps?q="+latitude+","+longitude;

                                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                                        smsIntent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumber)));
                                        smsIntent.putExtra("sms_body", message);

                                        try {
                                            SmsManager smsManager = SmsManager.getDefault();
                                            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                                            Toast.makeText(requireContext(), "SMS sent successfully", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Toast.makeText(requireContext(), "Failed to send SMS", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    } else {
                                        // If location cannot be obtained, show an alert dialog or take appropriate action
                                        gpsTracker.showSettingsAlert();
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "No contacts found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(requireContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            img.setImageURI(imageUri);
        }
    }

    public void saveUserData() {
        String name = textName.getText().toString();
        String phone = textPhone.getText().toString();
        String textEmail = edtEmail.getText().toString();

        if (currentUser != null) {
            if (imageUri != null) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + currentUser.getUid() + "/" + System.currentTimeMillis() + ".jpg");
                storageRef.putFile(imageUri);
            }

            if (user != null) {
                user.setName(name);
                user.setPhone(phone);

                if (imageUri != null) {
                    user.setImg_url(imageUri.toString());
                }
                currentUser.updateEmail(textEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(requireContext(), "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(requireContext(), "Failed to save user data.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(requireContext(), "Failed to update email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        stepCounterHelper.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        stepCounterHelper.stop();
    }
    class GpsTracker extends Service implements LocationListener {
        private Context mContext = requireContext();

        // flag for GPS status
        boolean isGPSEnabled = false;

        // flag for network status
        boolean isNetworkEnabled = false;

        // flag for GPS status
        boolean canGetLocation = false;

        Location location; // location
        double latitude; // latitude
        double longitude; // longitude

        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

        // Declaring a Location Manager
        protected LocationManager locationManager;

        public GpsTracker(Context context) {

            this.mContext = context;
            getLocation();
        }

        @SuppressLint("MissingPermission")
        public Location getLocation() {
            try {
                locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                } else {
                    this.canGetLocation = true;
                    // First get location from Network Provider
                    if (isNetworkEnabled) {
                        //check the network permission
                        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                        }
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }

                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            //check the network permission
                            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                            }
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return location;
        }

        /**
         * Stop using GPS listener
         * Calling this function will stop using GPS in your app
         * */

        @SuppressLint("MissingPermission")
        public void stopUsingGPS(){
            if(locationManager != null){
                locationManager.removeUpdates(ProfileFragment.GpsTracker.this);
            }
        }

        /**
         * Function to get latitude
         * */

        public double getLatitude(){
            if(location != null){
                latitude = location.getLatitude();
            }

            // return latitude
            return latitude;
        }

        /**
         * Function to get longitude
         * */

        public double getLongitude(){
            if(location != null){
                longitude = location.getLongitude();
            }

            // return longitude
            return longitude;
        }

        /**
         * Function to check GPS/wifi enabled
         * @return boolean
         * */

        public boolean canGetLocation() {
            return this.canGetLocation;
        }

        /**
         * Function to show settings alert dialog
         * On pressing Settings button will lauch Settings Options
         * */

        public void showSettingsAlert(){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }

        @Override
        public void onLocationChanged(Location location) {
            latitude = getLatitude();
            longitude = getLongitude();

        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }
    }

}
