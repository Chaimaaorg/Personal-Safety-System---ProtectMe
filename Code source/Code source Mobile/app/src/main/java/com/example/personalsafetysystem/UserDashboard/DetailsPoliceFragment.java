package com.example.personalsafetysystem.UserDashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.personalsafetysystem.R;
import com.example.personalsafetysystem.databinding.PoliceFragmentDetailsBinding;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class DetailsPoliceFragment extends Fragment {

    private static final String ARG_PARAM_CONTACT = "ARG_PARAM_CONTACT";

    private Police mPolice;

    View view;

    int position;
    String fsq_id;
    ImageView images, mmt, bookingcom, oyo, indrive, uber, jumiafood, swiggy, star;
    TextView placename, distance, description, mapdis, address, tv1;
    String dis;
    String latitude, longitude;
    CardView cvMaps;
    ImageView ivbackcoll;
    String name;
    CheckBox like;
    CollapsingToolbarLayout collapsingToolbarLayout;
    LottieAnimationView progressLoaderAnimation;

    ArrayList<Police> polices = new ArrayList<>();





    public DetailsPoliceFragment() {
        // Required empty public constructor
    }


    public static DetailsPoliceFragment newInstance(Police police) {
        DetailsPoliceFragment fragment = new DetailsPoliceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CONTACT, police);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPolice = (Police) getArguments().getSerializable(ARG_PARAM_CONTACT);
        }
    }

    PoliceFragmentDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = PoliceFragmentDetailsBinding.inflate(inflater, container, false);

        view = inflater.inflate(R.layout.police_fragment_details, container, false);
        ivbackcoll = view.findViewById(R.id.ivbackcoll);
        jumiafood = view.findViewById(R.id.zomato);
        indrive = view.findViewById(R.id.indrive);
        uber = view.findViewById(R.id.uber);
        cvMaps = view.findViewById(R.id.cvMaps);
        address = view.findViewById(R.id.description);
        mapdis = view.findViewById(R.id.mapdis);
        mmt = view.findViewById(R.id.mmt);
        oyo = view.findViewById(R.id.oyo);
        bookingcom = view.findViewById(R.id.bookingcom);
        images = view.findViewById(R.id.images);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        placename = view.findViewById(R.id.placename);
        distance = view.findViewById(R.id.distance);
        description = view.findViewById(R.id.description);
        progressLoaderAnimation = view.findViewById(R.id.progress_loader_animation);
        progressLoaderAnimation.setVisibility(View.VISIBLE);




        jumiafood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = "https://food.jumia.ma/restaurants/city/rabat";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getContext().startActivity(intent);
            }
        });


        indrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "https://indriver.com/en/home/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getContext().startActivity(intent);
            }
        });

        uber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = "https://m.uber.com/?client_id=<mHClirme28wuZEzqPiTuviNhVKd-FTtE>&dropoff[latitude]=" + mPolice.getLatitude() + "&dropoff[longitude]=" + mPolice.getLongitude();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getContext().startActivity(intent);
            }
        });


        bookingcom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "https://www.booking.com/index.en-gb.html?aid=378266&label=booking-name-IquAp%2AEbiLS6jPVl_he8yQS461499016258%3Apl%3Ata%3Ap1%3Ap22%2C563%2C000%3Aac%3Aap%3Aneg%3Afi%3Atikwd-65526620%3Alp9062116%3Ali%3Adec%3Adm%3Appccp%3DUmFuZG9tSVYkc2RlIyh9YYriJK-Ikd_dLBPOo0BdMww&sid=bdebd91e8d58e4a126df9e97d82f81de&";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getContext().startActivity(intent);
            }
        });

        mmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "https://www.makemytrip.com/hotels/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getContext().startActivity(intent);
            }
        });



        cvMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.com/maps?q=loc:" + mPolice.getLatitude() + "," + mPolice.getLongitude() + " (" + mPolice.getName() + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getContext().startActivity(intent);

            }
        });


        placename.setText(mPolice.getName());
        distance.setText(mPolice.getDistance());
        mapdis.setText(mPolice.getDistance());
        String url = "https://foursquare.com/v/" + mPolice.getFrq_id();
        description.setText(url);

        Picasso.get().load(mPolice.getImage()).into(ivbackcoll);


        SpannableString spannableString = new SpannableString(url);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Handle the click event here
                // For example, open the URL in a web browser
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        };
        spannableString.setSpan(clickableSpan, 0, url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        description.setText(spannableString);
        description.setMovementMethod(LinkMovementMethod.getInstance());




        return view ;
    }





}

