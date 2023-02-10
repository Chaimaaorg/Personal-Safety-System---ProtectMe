package com.example.personalsafetysystem.UserDashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalsafetysystem.databinding.FragmentPharmacyBinding;
import com.example.personalsafetysystem.databinding.PharmacyListItemBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PharmacyFragment extends Fragment {

    View view;
    RecyclerView recyclerView;

    public double latitude;
    public double longitude;

    public PharmacyFragment() {
        // Required empty public constructor
    }

    FragmentPharmacyBinding binding;



    private final OkHttpClient client = new OkHttpClient();
    ArrayList<Pharmacy> pharmacys = new ArrayList<>();
    PharmacyAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentPharmacyBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Help centers");

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PharmacyAdapter();
        binding.recyclerView.setAdapter(adapter);
        getPharmacys();

    }



    public void getLocation(){
        GpsTracker gpsTracker = new GpsTracker(getContext());
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

        }else{
            gpsTracker.showSettingsAlert();
        }
    }




    class GpsTracker extends Service implements LocationListener {
        private Context mContext = getContext();

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
                locationManager.removeUpdates(GpsTracker.this);
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

    void getPharmacys(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()

                .url("https://api.foursquare.com/v3/places/search?ll" +latitude + "2%C" + longitude + "&radius=10000&categories=17035")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "fsq3fjzrfyRFgWStaJyz0RFwirzXJz2OCC50eyMwjVGYhF0=")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String body = response.body().string();
                    Log.d("demo", "onResponse: " + body);

                    try {
                        JSONObject rootJson = new JSONObject(body);
                        JSONArray pharmacysJsonArray = rootJson.getJSONArray("results");
                        pharmacys.clear();

                        for (int i = 0; i < pharmacysJsonArray.length(); i++) {
                            JSONObject pharmacyJsonObject = pharmacysJsonArray.getJSONObject(i);
                            Pharmacy pharmacy = new Pharmacy(pharmacyJsonObject);
                            pharmacys.add(pharmacy);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                } else {

                }
            }
        });
    }

    class PharmacyAdapter extends RecyclerView.Adapter<PharmacyAdapter.PharmacyViewHolder>{
        @NonNull
        @Override
        public PharmacyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PharmacyViewHolder(PharmacyListItemBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PharmacyViewHolder holder, int position) {
            Pharmacy pharmacy = pharmacys.get(position);
            holder.setupUI(pharmacy);
        }

        @Override
        public int getItemCount() {
            return pharmacys.size();
        }

        class PharmacyViewHolder extends RecyclerView.ViewHolder{
            PharmacyListItemBinding mBinding;


            Pharmacy mPharmacy;
            public PharmacyViewHolder(PharmacyListItemBinding vhBinding) {
                super(vhBinding.getRoot());
                mBinding = vhBinding;

                mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.gotoPharmacyDetails(mPharmacy);
                    }
                });

            }



            public void setupUI(Pharmacy pharmacy){
                this.mPharmacy = pharmacy;
                mBinding.textViewName.setText(mPharmacy.getName());
                mBinding.textViewDistance.setText(mPharmacy.getDistance());
                mBinding.textViewContact.setText(mPharmacy.getContact());
                Picasso.get().load(mPharmacy.getImage()).into(mBinding.profileImg);



            }
        }

    }


    PharmacysListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (PharmacysListener) context;
    }

    interface PharmacysListener{
        void gotoPharmacyDetails(Pharmacy pharmacy);
    }



}


