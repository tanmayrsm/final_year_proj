package com.example.beproj3.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beproj3.AllContacts;
import com.example.beproj3.MainActivity;
import com.example.beproj3.Models.ConnectionDetails;
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

public class AllContactsAdapter extends RecyclerView.Adapter<AllContactsAdapter.AllUsersViewholder> {

    Activity context;
    ArrayList<User> userArrayList;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;

    public AllContactsAdapter(Activity context ,ArrayList<User> userArrayList){
        this.context = context;

        this.userArrayList = userArrayList;
        Log.e("Adapter list:",String.valueOf(this.userArrayList));
    }

    @Override
    public AllUsersViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_contacts,parent,false);
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

        //check if he is in sent requests
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("conn_req").child(firebaseUser.getUid()).child("sent");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Log.e("Andar ka snapshot:",snapshot.child("uid").getValue().toString());
                    String s = snapshot.child("uid").getValue().toString();
                    if(s.equals(user.getUserid())){
                        holder.send_req.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //check if he is in received requests
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("conn_req").child(firebaseUser.getUid()).child("received");
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.e("Key in receive:",dataSnapshot.getValue().toString());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Log.e("Andar ka snapshot:",snapshot.child("uid").getValue().toString());
                    String s = snapshot.child("uid").getValue().toString();
                    if(s.equals(user.getUserid())){
                        holder.send_req.setBackgroundColor(Color.GREEN);
                        holder.send_req.setText("Accept");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //check if he is in my conatct list
        DatabaseReference ref23 = FirebaseDatabase.getInstance().getReference("Contact_list").child(firebaseUser.getUid());
        ref23.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Log.e("snapshot keyyy:",snapshot.getKey());
                    if(user.getUserid().equals(snapshot.getKey())){
                        holder.send_req.setBackgroundColor(Color.BLUE);
                        holder.send_req.setTextColor(Color.WHITE);
                        holder.send_req.setText("Friend");
                        holder.send_req.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        Log.e("Item count:", String.valueOf(userArrayList.size()));return userArrayList.size();
    }

    public class AllUsersViewholder extends RecyclerView.ViewHolder{
        TextView user_ka_naam;
        Button send_req;
        String my_name ,my_id;
        ImageButton se_profile;
        CircleImageView prof_image;

        public AllUsersViewholder(View itemView) {
            super(itemView);
            user_ka_naam = itemView.findViewById(R.id.itemName2);
            send_req = itemView.findViewById(R.id.connectButton);
            se_profile = itemView.findViewById(R.id.see_profile);
            prof_image = itemView.findViewById(R.id.userProfileImage);

//          add a condition to disappear send req button

            send_req.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userArrayList.get(getAdapterPosition());

                    auth = FirebaseAuth.getInstance();
                    firebaseUser = auth.getCurrentUser();

                    if(!send_req.getText().toString().equals("Accept"))
                    {
                        String name_conn_to = user.getName();
                        String uska_id = user.getUserid();
                        //Toast.makeText(context, "Conn to : " + name_conn_to, Toast.LENGTH_SHORT).show();

                        sent_req_model sent = new sent_req_model(uska_id);


                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("conn_req")
                                .child(uska_id).child("received");

                        HashMap<String , String> hashMap2 = new HashMap<>();
                        hashMap2.put("uid" , firebaseUser.getUid());

                        reference2.child(firebaseUser.getUid()).setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(context, "Can't send request", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("conn_req")
                                .child(firebaseUser.getUid()).child("sent");

                        HashMap<String , String> hashMap = new HashMap<>();
                        hashMap.put("uid" , uska_id);

                        reference.child(uska_id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(context, "Can't send request", Toast.LENGTH_SHORT).show();
                                }else{

                                }
                            }
                        });

                        //uske notts me daal that u sent him request -
                        DatabaseReference julia2 = FirebaseDatabase.getInstance().getReference("Notifications")
                                .child(user.getUserid()).child("received_req").child(firebaseUser.getUid());

                        //he_id = dataSnapshot.getValue().toString();
                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                        String res = currentDate + " " + currentTime;

                        Notts n = new Notts(firebaseUser.getUid(),false,res,"received_req");

                        julia2.setValue(n)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(context, "Not added sent req in his notts", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        ((AllContacts)context).sendReq(user);
                    }else{
                        //Goto received req activity
                    }
                }
            });

            //see chat on click listener

            se_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userArrayList.get(getAdapterPosition());

                    ((AllContacts)context).viewProfile(user);
                }
            });
        }
    }
}
