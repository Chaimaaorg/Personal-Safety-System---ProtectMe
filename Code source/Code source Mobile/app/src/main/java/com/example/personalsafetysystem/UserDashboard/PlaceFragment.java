package com.example.personalsafetysystem.UserDashboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.example.personalsafetysystem.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PlaceFragment extends Fragment implements HospitalFragment.HospitalsListener, DoctorFragment.DoctorsListener , PoliceFragment.PolicesListener , PharmacyFragment.PharmacysListener {

    public PlaceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_place, container, false);

        BottomNavigationView bottomNav = rootView.findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HospitalFragment()).commit();
        }

        return rootView;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_hospital:
                            selectedFragment = new HospitalFragment();
                            break;
                        case R.id.nav_police:
                            selectedFragment = new PoliceFragment();
                            break;
                        case R.id.nav_doctor:
                            selectedFragment = new DoctorFragment();
                            break;
                        case R.id.nav_pharmacy:
                            selectedFragment = new PharmacyFragment();
                            break;
                    }

                    getChildFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };

    @Override
    public void gotoHospitalDetails(Hospital hospital) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.rootView, DetailsHospitalFragment.newInstance(hospital))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoDoctorDetails(Doctor doctor) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.rootView, DetailsDoctorFragment.newInstance(doctor))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoPoliceDetails(Police police) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.rootView, DetailsPoliceFragment.newInstance(police))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoPharmacyDetails(Pharmacy pharmacy) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.rootView, DetailsPharmacyFragment.newInstance(pharmacy))
                .addToBackStack(null)
                .commit();
    }
}