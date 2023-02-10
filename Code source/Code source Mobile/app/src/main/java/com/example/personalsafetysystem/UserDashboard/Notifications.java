package com.example.personalsafetysystem.UserDashboard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.personalsafetysystem.Adapters.SMSListAdapter;
import com.example.personalsafetysystem.LoginActivity;
import com.example.personalsafetysystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Notifications extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_READ_SMS = 1;

    private ListView smsListView;
    private SMSListAdapter adapter;

    private SearchView mapSearchView;
    private Button btnBack,btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        smsListView = findViewById(R.id.sms_list_view);

        // Check for SMS read permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    PERMISSION_REQUEST_READ_SMS);
        } else {
            // Permission already granted, load SMS messages
            loadSmsMessages(Telephony.Sms.DATE + " DESC");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load SMS messages
                loadSmsMessages(Telephony.Sms.DATE + " DESC");
            }
        }
    }

    private void loadSmsMessages(String sortColumn) {
        // Get the SMS content URI
        Uri smsUri = Telephony.Sms.CONTENT_URI;

        // Define the columns to retrieve from the SMS table
        String[] projection = {
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE
        };

        // Specify the phone number to filter the SMS messages
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        Query query = ref.orderByChild("role").equalTo("Principal user");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String phoneNumber = userSnapshot.child("phone").getValue(String.class);
                        if (phoneNumber != null) {
                            // Filter the SMS messages to include only the specified phone number
                            String selection = Telephony.Sms.ADDRESS + " = ?";
                            String[] selectionArgs = {phoneNumber};

                            // Query the SMS content provider with the specified filter
                            Cursor cursor = getContentResolver().query(smsUri, projection, selection, selectionArgs, sortColumn);

                            if (cursor != null) {
                                // Create a new SMSListAdapter with the cursor
                                adapter = new SMSListAdapter(Notifications.this, cursor);
                                smsListView.setAdapter(adapter);
                            }
                        }
                    }
                } else {
                    // No user with role "Principal user" found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

}
