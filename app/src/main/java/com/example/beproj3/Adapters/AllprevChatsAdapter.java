package com.example.beproj3.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beproj3.Allpreviouscalls;
import com.example.beproj3.Models.Chats;
import com.example.beproj3.Models.prevCalls;
import com.example.beproj3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllprevChatsAdapter extends RecyclerView.Adapter<AllprevChatsAdapter.AllUsersViewholder> {

    Activity context;
    ArrayList<Chats> chatArrayList;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;

    String uska_img_url = "";


    public AllprevChatsAdapter(Activity context, ArrayList<Chats> chatArrayList) {
        this.context = context;
        this.chatArrayList = chatArrayList;
        Log.e("Adapter list:", String.valueOf(this.chatArrayList));
    }

    @Override
    public AllprevChatsAdapter.AllUsersViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_layout, parent, false);
        AllprevChatsAdapter.AllUsersViewholder allUsersAdapter = new AllprevChatsAdapter.AllUsersViewholder(view);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        return allUsersAdapter;
    }

    @Override
    public void onBindViewHolder(AllprevChatsAdapter.AllUsersViewholder holder, int position) {
        String messageSenderId = firebaseUser.getUid();

        Chats chat = chatArrayList.get(position);
        String id = chat.getWho_tells();
        String time = chat.getTime();
        String message = chat.getChat();
        String type = chat.getType();
        String url = chat.getUrl();


        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Users").child(id).child("image_url");

        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    uska_img_url = dataSnapshot.getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //watch 50 no to add image
        holder.uska_msg.setVisibility(View.INVISIBLE);
        holder.uska_image.setVisibility(View.INVISIBLE);
        holder.uska_time.setVisibility(View.INVISIBLE);

        if(id.equals(firebaseUser.getUid())){
            //its sender
            holder.mera_msg.setBackgroundResource(R.drawable.me_user);
            if(!url.equals("")){
                holder.mera_msg.setText("PDF");
                holder.mera_msg.setBackgroundResource(R.drawable.pdf_background);
            }
            else    holder.mera_msg.setText(message);
            try {
                holder.mera_time.setText(datto(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else{
            holder.mera_msg.setVisibility(View.INVISIBLE);
            holder.mera_time.setVisibility(View.INVISIBLE);

            holder.uska_msg.setVisibility(View.VISIBLE);
            holder.uska_image.setVisibility(View.VISIBLE);
            holder.uska_time.setVisibility(View.VISIBLE);

            Log.e("Uska img url:",uska_img_url);

            if(uska_img_url!=null){
                Glide.with(context).load(uska_img_url).into(holder.uska_image);
            }

            else    holder.uska_msg.setBackgroundResource(R.drawable.other_user);

            if(!url.equals("")){
                holder.uska_msg.setText("PDF");
                holder.uska_msg.setBackgroundResource(R.drawable.pdf_background);
            }
            else    holder.uska_msg.setText(message);
            try {
                holder.uska_time.setText(datto(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        Log.e("Item count:", String.valueOf(chatArrayList.size()));
        return chatArrayList.size();
    }

    public class AllUsersViewholder extends RecyclerView.ViewHolder {
        public TextView uska_msg ,mera_msg ,uska_time ,mera_time;
        public CircleImageView uska_image;

        public AllUsersViewholder(View itemView) {
            super(itemView);
            uska_msg = itemView.findViewById(R.id.receiver_msgs);
            uska_time = itemView.findViewById(R.id.time_received_receiver);
            uska_image = itemView.findViewById(R.id.profile_img);

            mera_msg = itemView.findViewById(R.id.sender_msgs);
            mera_time = itemView.findViewById(R.id.time_sender);

//            if(user.getImage_url()!=null)
//                Glide.with(context).load(user.getImage_url()).into(holder.prof_image);

            mera_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public String datto(String in_date) throws ParseException {

        String input = in_date;
        //Date/time pattern of input date
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
        Date date = null;
        String output = null;
        try{
            //Conversion of input String to date
            date= df.parse(input);
            //old date format to new date format
            output = outputformat.format(date);
            System.out.println(output);
        }catch(ParseException pe){
            pe.printStackTrace();
        }
        return output;
    }
}

