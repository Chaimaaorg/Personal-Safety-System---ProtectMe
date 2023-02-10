package com.example.personalsafetysystem.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.personalsafetysystem.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SMSListAdapter extends CursorAdapter {

    public SMSListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate the layout for each list item
        return LayoutInflater.from(context).inflate(R.layout.sms_item_view, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Retrieve the views from the layout
        TextView addressTextView = view.findViewById(R.id.address_text_view);
        TextView bodyTextView = view.findViewById(R.id.body_text_view);
        TextView dateTextView = view.findViewById(R.id.date_text_view);

        // Retrieve the SMS data from the cursor
        String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
        String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
        String dateString = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));

        // Transform the date string to a Date object
        Date date = null;
        if (dateString != null && !dateString.isEmpty()) {
            try {
                long timestamp = Long.parseLong(dateString);
                date = new Date(timestamp);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Format the Date object to a desired date string format
        String formattedDate = formatDate(date, "dd/MM/yyyy HH:mm:ss");

        // Set the SMS data on the views
        addressTextView.setText(address);
        bodyTextView.setText(body);
        dateTextView.setText(formattedDate);
    }
    private String formatDate(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
            return sdf.format(date);
        }
        return "";
    }





}

