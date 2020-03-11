package com.example.beproj3.Adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import com.bumptech.glide.Glide;
import com.example.beproj3.AllReceived;
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

public class AllReceivedAdapter extends RecyclerView.Adapter<AllReceivedAdapter.AllUsersViewholder> {

    Activity context;
    ArrayList<User> userArrayList;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    boolean busy  = false;

    DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference().child("Calls");
    DatabaseReference ref_call_user = FirebaseDatabase.getInstance().getReference().child("Calls");

    public AllReceivedAdapter(Activity context ,ArrayList<User> userArrayList){
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @Override
    public AllUsersViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_received,parent,false);
        AllUsersViewholder allUsersAdapter = new AllUsersViewholder(view);

        return allUsersAdapter;
    }

    @Override
    public void onBindViewHolder(AllUsersViewholder holder, int position) {
        User user = userArrayList.get(position);
        holder.user_ka_naam.setText(user.getName());
        if(user.getImage_url()!=null)
            Glide.with(context).load(user.getImage_url()).into(holder.prof_image);
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class AllUsersViewholder extends RecyclerView.ViewHolder{
        TextView user_ka_naam;
        Button accepts,reject;
        String my_name ,my_id;
        ImageButton se_chat;
        CircleImageView prof_image;
        LinearLayout LL;


        public AllUsersViewholder(View itemView) {
            super(itemView);
            user_ka_naam = itemView.findViewById(R.id.itemName);
            accepts = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);
            prof_image = itemView.findViewById(R.id.itemImage);
            LL = itemView.findViewById(R.id.LLo);



            accepts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userArrayList.get(getAdapterPosition());

                    accepts.setText("Request Accepted");
                    accepts.setTextColor(Color.BLACK);
                    reject.setVisibility(View.GONE);
                    accepts.setEnabled(false);
                    accepts.setBackgroundColor(Color.WHITE);
                    auth = FirebaseAuth.getInstance();
                    firebaseUser = auth.getCurrentUser();

                    String name_conn_to = user.getName();
                    String uska_id = user.getUserid();

                    // sent_req_model received = new sent_req_model(uska_id);

                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Contact_list")
                            .child(firebaseUser.getUid());

                    HashMap<String , String> hashMap2 = new HashMap<>();
                    hashMap2.put("uid" , user.getUserid());

                    reference2.child(user.getUserid()).setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(context, "Can't accept request", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "Congrats now you and " + user.getName() + " are friends!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    ////////
                    DatabaseReference chaman = FirebaseDatabase.getInstance().getReference("conn_req").child(firebaseUser.getUid()).child("received")
                            .child(user.getUserid());
                    chaman.removeValue();

                    DatabaseReference chaman2 = FirebaseDatabase.getInstance().getReference("conn_req").child(user.getUserid()).child("sent")
                            .child(firebaseUser.getUid());
                    chaman2.removeValue();

                    ////////uske contact list me apna

                    DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Contact_list")
                            .child(user.userid);

                    HashMap<String , String> hashMap3 = new HashMap<>();
                    hashMap3.put("uid" , firebaseUser.getUid());

                    reference3.child(firebaseUser.getUid()).setValue(hashMap3).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                //Toast.makeText(context, "Can't accept request", Toast.LENGTH_SHORT).show();
                            }else{
                                // Toast.makeText(context, "Congrats now you and " + user.getName() + " are friends!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    //add to his notts ki I accepted his request
                    DatabaseReference julia2 = FirebaseDatabase.getInstance().getReference("Notifications")
                            .child(user.getUserid()).child("accepted_req").child(firebaseUser.getUid());

                    //he_id = dataSnapshot.getValue().toString();
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                    String res = currentDate + " " + currentTime;

                    Notts n = new Notts(firebaseUser.getUid(),false,res,"accepted_req");

                    julia2.setValue(n)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(context, "Not added sent req in his notts", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
            });


            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userArrayList.get(getAdapterPosition());

                    accepts.setText("Request Rejected");
                    accepts.setEnabled(false);
                    accepts.setBackgroundColor(Color.WHITE);
                    accepts.setTextColor(Color.BLACK);

                    reject.setVisibility(View.GONE);

                    auth = FirebaseAuth.getInstance();
                    firebaseUser = auth.getCurrentUser();

                    ////////
                    DatabaseReference chaman = FirebaseDatabase.getInstance().getReference("conn_req").child(firebaseUser.getUid()).child("received")
                            .child(user.getUserid());
                    chaman.removeValue();

                    DatabaseReference chaman2 = FirebaseDatabase.getInstance().getReference("conn_req").child(user.getUserid()).child("sent")
                            .child(firebaseUser.getUid());

                    Log.e("uska rejection id:",user.getUserid());

                    chaman2.removeValue();
                }
            });


            //see chat on click listener

            se_chat = itemView.findViewById(R.id.see_profile__);

            se_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userArrayList.get(getAdapterPosition());

                    auth = FirebaseAuth.getInstance();
                    firebaseUser = auth.getCurrentUser();

                    String name_conn_to = user.getName();
                    String uska_id = user.getUserid();
                    //Toast.makeText(context, "Conn to : " + name_conn_to, Toast.LENGTH_SHORT).show();

                    ((AllReceived)context).viewProfile(user);
                }
            });

            LL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = userArrayList.get(getAdapterPosition());

                    auth = FirebaseAuth.getInstance();
                    firebaseUser = auth.getCurrentUser();

                    String name_conn_to = user.getName();
                    String uska_id = user.getUserid();
                    //Toast.makeText(context, "Conn to : " + name_conn_to, Toast.LENGTH_SHORT).show();

                    ((AllReceived)context).viewProfile(user);
                }
            });
        }
    }
}
