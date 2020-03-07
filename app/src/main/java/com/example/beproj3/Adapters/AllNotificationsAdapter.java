package com.example.beproj3.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beproj3.AllContacts;
import com.example.beproj3.Models.Notts;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllNotificationsAdapter extends RecyclerView.Adapter<AllNotificationsAdapter.AllUsersViewholder>{

        Activity context;
        ArrayList<Notts> nottsArrayList;
        FirebaseUser firebaseUser;
        FirebaseAuth auth;
        String profile_img_url = null;

        public AllNotificationsAdapter(Activity context ,ArrayList<Notts> nottsArrayList){
            this.context = context;
            if (nottsArrayList.size()!=0) {
                this.nottsArrayList = nottsArrayList;
                Log.e("Adapter list:", String.valueOf(this.nottsArrayList));
            }
        }

        @Override
        public AllUsersViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_notts,parent,false);
            AllUsersViewholder allUsersAdapter = new AllUsersViewholder(view);
            return allUsersAdapter;
        }

        @Override
        public void onBindViewHolder(AllUsersViewholder holder, int position) {
            Notts notts = nottsArrayList.get(position);

            DatabaseReference usero = FirebaseDatabase.getInstance().getReference("Users").child(notts.getUid()).child("name");

            //set username
            usero.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    holder.user_ka_naam.setText(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference usero2 = FirebaseDatabase.getInstance().getReference("Users").child(notts.getUid()).child("image_url");

            //set prof image
            usero2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                        profile_img_url = dataSnapshot.getValue().toString();
                    if(profile_img_url!=null)
                        Glide.with(context).load(profile_img_url).into(holder.prof_image);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            auth = FirebaseAuth.getInstance();
            firebaseUser = auth.getCurrentUser();

            if(notts.isSeen())
                holder.ll1.setBackgroundColor(Color.WHITE);

            holder.type.setText(notts.getType());
            holder.time.setText(notts.getTime());

            if(notts.getType().equals("received_req"))
                holder.type_image.setImageResource(R.drawable.ic_received_req);
            else if(notts.getType().equals("accepted_req"))
                holder.type_image.setImageResource(R.drawable.ic_accepted_req);
            else
                holder.type_image.setImageResource(R.drawable.ic_phone_missed_);

            DatabaseReference seen_ko_true = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid())
                    .child(notts.getType()).child(notts.getUid());

            seen_ko_true.child("seen").setValue(true);

        }

        @Override
        public int getItemCount() {
            return nottsArrayList.size();
        }

        public class AllUsersViewholder extends RecyclerView.ViewHolder{
            TextView user_ka_naam ,type ,time;
            ImageView type_image;
            LinearLayout ll1;
            CircleImageView prof_image;

            public AllUsersViewholder(View itemView) {
                super(itemView);
                user_ka_naam = itemView.findViewById(R.id.itemName);
                prof_image = itemView.findViewById(R.id.itemImage);
                time = itemView.findViewById(R.id.tym);
                type = itemView.findViewById(R.id.itemType);
                type_image = itemView.findViewById(R.id.type_img);
                ll1 = itemView.findViewById(R.id.color_linear_layout);
            }
        }

}
