package com.example.personalsafetysystem.UserDashboard;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.personalsafetysystem.LoginActivity;
import com.example.personalsafetysystem.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Button btn_start_route;
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    //map search view
    private SearchView mapSearchView ;

    private ImageView iv_get_location,btnLogout;

    //pop up start route menu
    Dialog myDialog;
    private EditText etFromLocation,etToLocation ;
    private Button btnGetDirection ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        btnLogout = findViewById(R.id.logout);
        btn_start_route = findViewById(R.id.btn_start_route);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastlocation();
        getCurrentLocation();

        //handle the log out button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //handle the start route button
        myDialog = new Dialog(this);
        btn_start_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Uri uri = Uri.parse("https://www.google.com/maps/dir/"+currentLocation.getLatitude()+","+currentLocation.getLongitude()+"/"+currentLocation.getLatitude()+","+currentLocation.getLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setPackage("com.google.android.apps.maps");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch (ActivityNotFoundException exception ){
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps&hl=en&gl=US");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }


            }
        });





    }

    //the shop up start route menu


    public void ShowPopup() {
        myDialog.setContentView(R.layout.activity_start_route);
        etFromLocation =(EditText) myDialog.findViewById(R.id.etFromLocation);
        etToLocation =(EditText) myDialog.findViewById(R.id.etToLocation);
        btnGetDirection = (Button) myDialog.findViewById(R.id.btnGetDirection);
        btnGetDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userLocation = etFromLocation.getText().toString();
                String userDestination = etToLocation.getText().toString();

                if (userLocation.equals("") || userDestination.equals("")) {
                    Toast.makeText(TrackingActivity.this,"Please enter your location and destination", Toast.LENGTH_SHORT).show();
                }else {
                    getDirections(userLocation, userDestination);
                }
            }

            private void getDirections(String from, String to) {
                try{
                    Uri uri = Uri.parse("https://www.google.com/maps/dir/"+from+"/"+to);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setPackage("com.google.android.apps.maps");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch (ActivityNotFoundException exception ){
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps&hl=en&gl=US");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void getLastlocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }

        DatabaseReference gpsReference = FirebaseDatabase.getInstance().getReference("GPS");
        gpsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String latitudeString = dataSnapshot.child("LAT").getValue(String.class);
                    String longitudeString = dataSnapshot.child("LNG").getValue(String.class);

                    if (latitudeString != null && longitudeString != null) {
                        double latitude = Double.parseDouble(latitudeString);
                        double longitude = Double.parseDouble(longitudeString);
                        currentLocation = new Location("FirebaseData");
                        currentLocation.setLatitude(latitude);
                        currentLocation.setLongitude(longitude);

                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        mapFragment.getMapAsync(TrackingActivity.this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Error retrieving data from Firebase: " + error.getMessage());
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    currentLocation = location;

                    // Use the latitude and longitude values
                    // For example, update UI or perform further operations
                }
            }
        });
    }




    //get current location
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap ;
        LatLng sydney = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(sydney).title("My Location"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastlocation();
            }else {
                Toast.makeText(this,"Location permission is denied, please allow the permission",Toast.LENGTH_SHORT).show();

            }
        }

    }


}