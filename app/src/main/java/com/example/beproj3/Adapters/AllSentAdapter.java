package com.example.beproj3.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beproj3.AllContacts;
import com.example.beproj3.AllSent;
import com.example.beproj3.MainActivity;
import com.example.beproj3.Models.ConnectionDetails;
import com.example.beproj3.Models.User;
import com.example.beproj3.Models.sent_req_model;
import com.example.beproj3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllSentAdapter extends RecyclerView.Adapter<AllSentAdapter.AllUsersViewholder> {

    Activity context;
    ArrayList<User> userArrayList;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;

    public AllSentAdapter(Activity context ,ArrayList<User> userArrayList){
        this.context = context;

        this.userArrayList = userArrayList;
        Log.e("Adapter list:",String.valueOf(this.userArrayList));
    }

    @Override
    public AllUsersViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_sent,parent,false);
        AllUsersViewholder allUsersAdapter = new AllUsersViewholder(view);

        return allUsersAdapter;
    }

    @Override
    public void onBindViewHolder(AllUsersViewholder holder, int position) {
        User user = userArrayList.get(position);
        holder.user_ka_naam.setText(user.getName());

        if(user.getImage_url()!=null)
            Glide.with(context).load(user.getImage_url()).into(holder.prof_image);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
    }

    @Override
    public int getItemCount() {
        Log.e("Item count in all sent:", String.valueOf(userArrayList.size()));return userArrayList.size();
    }

    public class AllUsersViewholder extends RecyclerView.ViewHolder{
        TextView user_ka_naam;
        Button del_send_req;
        String my_name ,my_id;
        ImageButton se_profile;
        CircleImageView prof_image;
        LinearLayout LL;


        public AllUsersViewholder(View itemView) {
            super(itemView);
            user_ka_naam = itemView.findViewById(R.id.itemName);
            del_send_req = itemView.findViewById(R.id.delButton);
            se_profile = itemView.findViewById(R.id.see_profile_);
            prof_image = itemView.findViewById(R.id.itemImage);
            LL = itemView.findViewById(R.id.LLo);


//          add a condition to disappear send req button

            del_send_req.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userArrayList.get(getAdapterPosition());

                    auth = FirebaseAuth.getInstance();
                    firebaseUser = auth.getCurrentUser();

                    DatabaseReference chaman = FirebaseDatabase.getInstance().getReference("conn_req").child(firebaseUser.getUid()).child("sent")
                            .child(user.getUserid());
                    chaman.removeValue();
                    DatabaseReference chaman2 = FirebaseDatabase.getInstance().getReference("conn_req").child(user.getUserid()).child("received")
                            .child(firebaseUser.getUid());
                    chaman2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(context, "Deleted request to "+ user.getName(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });

            //see chat on click listener

            se_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userArrayList.get(getAdapterPosition());

                    ((AllSent)context).viewProfile(user);
                }
            });

            LL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = userArrayList.get(getAdapterPosition());

                    ((AllSent)context).viewProfile(user);
                }
            });
        }
    }
}
