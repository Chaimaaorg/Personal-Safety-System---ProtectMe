package com.example.personalsafetysystem;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.personalsafetysystem.UserDashboard.Notifications;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingReceiver extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgReceiver";
    private static final String CHANNEL_ID = "HEARTBEAT_ALERT";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        DatabaseReference heartRef = FirebaseDatabase.getInstance().getReference("HEART");
        heartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String heartbeat = dataSnapshot.child("heartbeat").getValue(String.class);
                    int heartbeatInt = Integer.parseInt(heartbeat);
                    if (heartbeatInt < 60 || heartbeatInt > 100) {
                        sendAlertNotification();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle cancellation errors here
            }
        });
    }

    private void sendAlertNotification() {
        String title = "Heartbeat Alert";
        String text = "Heartbeat value is out of range (60-100)";

        Intent resultIntent = new Intent(this, Notifications.class);
        resultIntent.putExtra("messageBody", text);
        resultIntent.putExtra("messageTitle", title);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        createNotificationChannel();

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_notifs)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, notification.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Heartbeat Alert";
            String channelDescription = "Channel for heartbeat alerts";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
