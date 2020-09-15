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
import com.example.beproj3.Allpreviouscalls;
import com.example.beproj3.Models.Notts;
import com.example.beproj3.Models.User;
import com.example.beproj3.Models.prevCalls;
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

public class AllprevCallsAdapter extends RecyclerView.Adapter<AllprevCallsAdapter.AllUsersViewholder> {

    Activity context;
    ArrayList<prevCalls> prevArrayList;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;

    public AllprevCallsAdapter(Activity context, ArrayList<prevCalls> prevArrayList) {
        this.context = context;

        this.prevArrayList = prevArrayList;
        Log.e("Adapter list:", String.valueOf(this.prevArrayList));
    }

    @Override
    public AllprevCallsAdapter.AllUsersViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prev_call_history, parent, false);
        AllprevCallsAdapter.AllUsersViewholder allUsersAdapter = new AllprevCallsAdapter.AllUsersViewholder(view);

        return allUsersAdapter;
    }

    @Override
    public void onBindViewHolder(AllprevCallsAdapter.AllUsersViewholder holder, int position) {
        prevCalls prev = prevArrayList.get(position);

        holder.start_timee.setText(prev.getStart());

        //TODO
        // add exact duration
        holder.Duration.setText(prev.getEnd());

        try{
            //set his name
            DatabaseReference chaman = FirebaseDatabase.getInstance().getReference("Users").child(prev.getUid()).child("name");
            chaman.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    holder.user_ka_naam.setText(dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //set his image
            DatabaseReference chaman2 = FirebaseDatabase.getInstance().getReference("Users").child(prev.getUid()).child("image_url");
            chaman2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Glide.with(context).load(dataSnapshot.getValue().toString()).into(holder.prof_image);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch(Exception e){

        }

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

    }

    @Override
    public int getItemCount() {
        Log.e("Item count:", String.valueOf(prevArrayList.size()));
        return prevArrayList.size();
    }

    public class AllUsersViewholder extends RecyclerView.ViewHolder {
        TextView user_ka_naam, start_timee, Duration;
        LinearLayout ll;

        ImageButton se_chato;
        CircleImageView prof_image;

        public AllUsersViewholder(View itemView) {
            super(itemView);
            user_ka_naam = itemView.findViewById(R.id.itemName);
            start_timee = itemView.findViewById(R.id.start_time);
            Duration = itemView.findViewById(R.id.duration);
            prof_image = itemView.findViewById(R.id.itemImage);
            se_chato = itemView.findViewById(R.id.see_chat2);
            ll = itemView.findViewById(R.id.LLo);


            //see chat on click listener
            se_chato.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prevCalls prev = prevArrayList.get(getAdapterPosition());
                    ((Allpreviouscalls) context).viewMessage(prev);
                }
            });
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    prevCalls prev = prevArrayList.get(getAdapterPosition());
                    ((Allpreviouscalls) context).viewMessage(prev);
                }
            });
        }
    }
}
