package com.example.personalsafetysystem;
import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private Button btn_start_route;
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    //map search view
    private SearchView mapSearchView;

    private ImageView iv_get_location, btnLogout;

    //pop up start route menu
    Dialog myDialog;
    private EditText etFromLocation, etToLocation;
    private Button btnGetDirection;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLogout = view.findViewById(R.id.logout);
        btn_start_route = view.findViewById(R.id.btn_start_route);
        mapSearchView = view.findViewById(R.id.mapSearch);
        iv_get_location = view.findViewById(R.id.iv_get_location);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        getLastlocation();

        //handle the log out button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        //handle the start route button
        myDialog = new Dialog(requireContext());
        btn_start_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopup();
            }
        });

        //handle the get current location button
        iv_get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastlocation();
            }
        });

        // initialize the map search view
        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null) {
                    Geocoder geocoder = new Geocoder(requireContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    } else {
                        Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show();
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

    }

    //the shop up start route menu
    public void ShowPopup() {
        myDialog.setContentView(R.layout.activity_start_route);
        etFromLocation = myDialog.findViewById(R.id.etFromLocation);
        etToLocation = myDialog.findViewById(R.id.etToLocation);
        btnGetDirection = myDialog.findViewById(R.id.btnGetDirection);
        btnGetDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userLocation = etFromLocation.getText().toString();
                String userDestination = etToLocation.getText().toString();

                if (userLocation.isEmpty() || userDestination.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter your location and destination", Toast.LENGTH_SHORT).show();
                } else {
                    getDirections(userLocation, userDestination);
                }
            }

            private void getDirections(String from, String to) {
                try {
                    Uri uri = Uri.parse("https://www.google.com/maps/dir/" + from + "/" + to);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setPackage("com.google.android.apps.maps");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (ActivityNotFoundException exception) {
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
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                    if (mapFragment != null) {
                        mapFragment.getMapAsync(HomeFragment.this);
                    }
                }
            }
        });
    }

    //get current location
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastlocation();
            } else {
                Toast.makeText(requireContext(), "Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
