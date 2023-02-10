package com.example.personalsafetysystem.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalsafetysystem.Model.SlideModel;
import com.example.personalsafetysystem.R;
import com.example.personalsafetysystem.UserDashboard.ContactDashboard;
import com.example.personalsafetysystem.UserDashboard.UserDashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.OnboardingViewHolder> {

    private List<SlideModel> slideModelList;

    private Context context;
    public SliderAdapter(List<SlideModel> slideModelList, Context context) {
        this.slideModelList = slideModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.setImage(slideModelList.get(position).getImage());
        holder.setIcon(slideModelList.get(position).getIcon());
        holder.setTitle(slideModelList.get(position).getHeading());
        holder.setDescription(slideModelList.get(position).getDescription());
    }


    @Override
    public int getItemCount() {
        return slideModelList.size();
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        private ImageView iconView;

        private TextView titleTextView;
        private TextView descriptionTextView;

        private Button skipButton;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slider_image);
            iconView = itemView.findViewById(R.id.slider_icon);

            titleTextView = itemView.findViewById(R.id.slider_heading);
            descriptionTextView = itemView.findViewById(R.id.slider_desc);

            skipButton = itemView.findViewById(R.id.slider_skip);

            skipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String userId = user.getUid();
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        private Intent userDashboardIntent;
                        private Intent contactDashboardIntent;

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String role = dataSnapshot.child("role").getValue(String.class);
                                userDashboardIntent = new Intent(context, UserDashboard.class);
                                contactDashboardIntent = new Intent(context.getApplicationContext(), ContactDashboard.class);

                                if (role != null) {
                                    Intent intent = role.equals("Principal user") ? userDashboardIntent : contactDashboardIntent;
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add the flag here
                                    context.startActivity(intent);
                                }
                            } else {
                                // L'utilisateur n'existe pas dans la base de données
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Erreur lors de la récupération des données de l'utilisateur
                        }
                    });
                }
            });
        }

        void setImage(int image) {
            imageView.setImageResource(image);
        }

        void setIcon(int icon) {iconView.setImageResource(icon);
        }


        void setTitle(String title) {
            titleTextView.setText(title);
        }

        void setDescription(String description) {
            descriptionTextView.setText(description);
        }
    }
}
