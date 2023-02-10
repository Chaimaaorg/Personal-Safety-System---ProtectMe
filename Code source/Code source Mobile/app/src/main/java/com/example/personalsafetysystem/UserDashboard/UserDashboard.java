package com.example.personalsafetysystem.UserDashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.personalsafetysystem.BottomMenuFragments.ContactsFragment;
import com.example.personalsafetysystem.BottomMenuFragments.ProfileFragment;
import com.example.personalsafetysystem.HomeFragment;
import com.example.personalsafetysystem.R;
import com.example.personalsafetysystem.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDashboard extends AppCompatActivity implements HospitalFragment.HospitalsListener, DoctorFragment.DoctorsListener, PoliceFragment.PolicesListener, PharmacyFragment.PharmacysListener {
    private static final int SMS_PERMISSION_REQUEST_CODE = 1;
    public double latitude;
    public double longitude;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);
        setMenuItemColor(binding.bottomNavigationView, R.id.homeBottomMenu, true); // Set the active item color

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.homeBottomMenu:
                    replaceFragment(new HomeFragment());
                    setMenuItemColor(binding.bottomNavigationView, R.id.homeBottomMenu, true);
                    break;
                case R.id.contactsBottomMenu:
                    replaceFragment(new ContactsFragment());
                    setMenuItemColor(binding.bottomNavigationView, R.id.contactsBottomMenu, true);
                    break;
                case R.id.placeBottomMenu:
                    replaceFragment(new PlaceFragment());
                    setMenuItemColor(binding.bottomNavigationView, R.id.placeBottomMenu, true);
                    break;
                case R.id.profileBottomMenu:
                    replaceFragment(new ProfileFragment());
                    setMenuItemColor(binding.bottomNavigationView, R.id.profileBottomMenu, true);
                    break;
            }
            return true;
        });
        binding.emergencyButton.setOnClickListener(view -> {
            checkSMSPermission();
        });
    }

    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
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
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
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
                                    GpsTracker gpsTracker = new GpsTracker(UserDashboard.this);

                                    // Check if location can be obtained
                                    if (gpsTracker.canGetLocation()) {
                                        double latitude = gpsTracker.getLatitude();
                                        double longitude = gpsTracker.getLongitude();

                                        // Use the latitude and longitude values as needed
                                        String message = "I am in a crisis situation please track ! https://www.google.com/maps?q="+latitude+","+longitude;

                                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                                        smsIntent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumber)));
                                        smsIntent.putExtra("sms_body", message);

                                        try {
                                            SmsManager smsManager = SmsManager.getDefault();
                                            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                                            Toast.makeText(getApplicationContext(), "SMS sent successfully", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), "Failed to send SMS", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    } else {
                                        // If location cannot be obtained, show an alert dialog or take appropriate action
                                        gpsTracker.showSettingsAlert();
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "No contacts found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void setMenuItemColor(BottomNavigationView navigationView, int menuItemId, boolean isActive) {
        int activeColor = ContextCompat.getColor(this, R.color.colorPurple);
        int inactiveColor = ContextCompat.getColor(this, R.color.colorWhite);
        int color = isActive ? activeColor : inactiveColor;

        navigationView.getMenu().findItem(menuItemId).getIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void gotoHospitalDetails(Hospital hospital) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, DetailsHospitalFragment.newInstance(hospital))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoDoctorDetails(Doctor doctor) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, DetailsDoctorFragment.newInstance(doctor))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoPoliceDetails(Police police) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, DetailsPoliceFragment.newInstance(police))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoPharmacyDetails(Pharmacy pharmacy) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, DetailsPharmacyFragment.newInstance(pharmacy))
                .addToBackStack(null)
                .commit();
    }
    public void getLocation(){

        UserDashboard.GpsTracker gpsTracker = new UserDashboard.GpsTracker(UserDashboard.this);
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

        }else{
            gpsTracker.showSettingsAlert();
        }
    }


    class GpsTracker extends Service implements LocationListener {
        private Context mContext = UserDashboard.this;

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
                locationManager.removeUpdates(UserDashboard.GpsTracker.this);
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
