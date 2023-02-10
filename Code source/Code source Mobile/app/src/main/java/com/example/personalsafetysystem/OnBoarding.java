package com.example.personalsafetysystem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.personalsafetysystem.Adapters.SliderAdapter;
import com.example.personalsafetysystem.Model.SlideModel;

import java.util.ArrayList;
import java.util.List;

public class OnBoarding extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button nextButton;
    private Button finishButton;
    private Button skipButton;
    private SliderAdapter onboardingAdapter;
    private LinearLayout dotsLayout;
    private ImageView[] dots;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        viewPager = findViewById(R.id.sliderViewPager);
        nextButton = findViewById(R.id.next);
        finishButton = findViewById(R.id.finish);
        dotsLayout = findViewById(R.id.dotsLayout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        List<SlideModel> slideModelList = new ArrayList<>();
        slideModelList.add(new SlideModel(R.drawable.emer,R.drawable.make_a_call, "Emergency notice", "Alert your acquaintances in case of danger, they will be able to see everything that happens through your camera. "));
        slideModelList.add(new SlideModel(R.drawable.warning,R.drawable.search_place, "Incident areas", "Look on the map at the areas where incidents have been recorded by other users of the community."));
        slideModelList.add(new SlideModel(R.drawable.share,R.drawable.add_missing_place, "Share experience", "Share with the users of the app the activity that you have carried out at any time and provide safe spaces in the community."));

        onboardingAdapter = new SliderAdapter(slideModelList,getApplicationContext());
        viewPager.setAdapter(onboardingAdapter);

        addDots();
        setCurrentDot(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentDot(position);
                currentPosition = position;
                if (position == onboardingAdapter.getItemCount() - 1) {
                    nextButton.setVisibility(View.GONE);
                    finishButton.setVisibility(View.VISIBLE);
                } else {
                    nextButton.setVisibility(View.VISIBLE);
                    finishButton.setVisibility(View.GONE);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition < onboardingAdapter.getItemCount() - 1) {
                    currentPosition++;
                    viewPager.setCurrentItem(currentPosition);
                }
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void addDots() {
        dots = new ImageView[onboardingAdapter.getItemCount()];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.unselected_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dots[i], params);
        }
    }

    private void setCurrentDot(int position) {
        for (int i = 0; i < dots.length; i++) {
            if (i == position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selected_dot));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.unselected_dot));
            }
        }
    }
}

