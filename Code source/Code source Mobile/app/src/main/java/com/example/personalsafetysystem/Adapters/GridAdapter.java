package com.example.personalsafetysystem.Adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.personalsafetysystem.Model.User;
import com.example.personalsafetysystem.R;
import com.example.personalsafetysystem.UserDashboard.DetailActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class GridAdapter extends FirebaseRecyclerAdapter<User,GridAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public GridAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull User model) {
        holder.name.setText(model.getName());
        holder.number.setText(model.getPhone());
        //holder.email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        //holder.email.setText(model.getEmail());


        Glide.with(holder.img.getContext()).load(model.getImg_url()).placeholder(com.google.firebase.appcheck.interop.R.drawable.common_google_signin_btn_icon_dark).circleCrop().error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal).into(holder.img);

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("contacts_list").child(getRef(position).getKey()).child("img_url");

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String img_url = dataSnapshot.getValue(String.class);
                        String name = ((TextView) view.findViewById(R.id.nametext)).getText().toString();
                        String number = ((TextView) view.findViewById(R.id.numbertext)).getText().toString();
                        Intent intent = new Intent(view.getContext(), DetailActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("phone", number);
                        intent.putExtra("img_url", img_url);
                        view.getContext().startActivity(intent);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });


            }
        });



    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mycontact,parent,false);
        return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        CircleImageView img;
        TextView name,number,email,service;
        Button btnEdit,btnDelete;

        LinearLayout itemLayout;
        public myViewHolder(@NonNull View itemView)
        {
            super(itemView);

            img = (CircleImageView)itemView.findViewById(R.id.img1);
            name = (TextView) itemView.findViewById(R.id.nametext);
            number = (TextView) itemView.findViewById(R.id.numbertext);
            //email = (TextView) itemView.findViewById(R.id.emailtext);
            itemLayout = (LinearLayout) itemView.findViewById(R.id.item);


        }
    }

}
