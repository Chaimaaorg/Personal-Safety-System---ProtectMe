package com.example.personalsafetysystem.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.personalsafetysystem.Model.User;
import com.example.personalsafetysystem.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends FirebaseRecyclerAdapter<User,ContactAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ContactAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull User model) {
        holder.name.setText(model.getName());
        holder.number.setText(model.getPhone());
        holder.email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        //holder.email.setText(model.getEmail());


        Glide.with(holder.img.getContext()).load(model.getImg_url()).placeholder(com.google.firebase.appcheck.interop.R.drawable.common_google_signin_btn_icon_dark).circleCrop().error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal).into(holder.img);





    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contactof_emerg_contact,parent,false);
        return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        CircleImageView img;
        TextView name,number,email,service;
        Button btnEdit,btnDelete;

        RelativeLayout itemLayout;
        public myViewHolder(@NonNull View itemView)
        {
            super(itemView);

            img = (CircleImageView)itemView.findViewById(R.id.img1);
            name = (TextView) itemView.findViewById(R.id.nametext);
            number = (TextView) itemView.findViewById(R.id.numbertext);
            email = (TextView) itemView.findViewById(R.id.emailtext);



            itemLayout = (RelativeLayout) itemView.findViewById(R.id.item);


        }
    }

}
