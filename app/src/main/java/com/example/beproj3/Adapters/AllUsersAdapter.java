package com.example.beproj3.Adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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

import com.bumptech.glide.Glide;
import com.example.beproj3.MainActivity;
import com.example.beproj3.Models.ConnectionDetails;
import com.example.beproj3.Models.SentModel;
import com.example.beproj3.Models.User;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.AllUsersViewholder> {

    Activity context;
    ArrayList<User> userArrayList;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    boolean busy  = false;
    String uska_id;

    DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference().child("Calls");
    DatabaseReference ref_call_user = FirebaseDatabase.getInstance().getReference().child("Calls");


    public AllUsersAdapter(Activity context ,ArrayList<User> userArrayList){
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @Override
    public AllUsersViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users,parent,false);
        AllUsersViewholder allUsersAdapter = new AllUsersViewholder(view);

        return allUsersAdapter;
    }

    @Override
    public void onBindViewHolder(AllUsersViewholder holder, int position) {
        User user = userArrayList.get(position);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        //set online status
        Log.e("users id:",user.getUserid());
        DatabaseReference provo = FirebaseDatabase.getInstance().getReference("User_status").child(user.getUserid()).child("status");
        provo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                if(dataSnapshot.getValue().toString().equals("online")){
                    holder.onlinu.setVisibility(View.VISIBLE);
                }else{
                    holder.onlinu.setVisibility(View.INVISIBLE);
                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference isBusy = FirebaseDatabase.getInstance().getReference("Busy");
        isBusy.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user.getUserid())){
                    holder.call_button.setEnabled(false);
                    holder.call_button.setImageResource(R.drawable.ic_busy_24dp);
                }
                else{
                    holder.call_button.setEnabled(true);
                    holder.call_button.setImageResource(R.drawable.ic_phone_icon_24dp);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //check if he blocked you
        DatabaseReference refu = FirebaseDatabase.getInstance().getReference("block_list").child(user.getUserid());
        refu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dss : dataSnapshot.getChildren()){
                        SentModel user2 = dss.getValue(SentModel.class);
                        if(user2.getUid().equals(firebaseUser.getUid())) {
                            holder.call_button.setImageResource(R.drawable.ic_block_black_24dp);
                            holder.call_button.setEnabled(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //check if you blocked him
        DatabaseReference refu2 = FirebaseDatabase.getInstance().getReference("block_list").child(firebaseUser.getUid());
        refu2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dss : dataSnapshot.getChildren()){
                        SentModel user2 = dss.getValue(SentModel.class);
                        if(user2.getUid().equals(user.getUserid())){
                            holder.call_button.setImageResource(R.drawable.ic_block_black_24dp);
                            holder.call_button.setEnabled(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        Button calling;
        String my_name ,my_id;
        ImageView se_chat ,call_button ,viewChat_img;
        CircleImageView prof_image;
        ImageView onlinu;
        LinearLayout vu;

        public AllUsersViewholder(View itemView) {
            super(itemView);
            user_ka_naam = itemView.findViewById(R.id.itemName);
            calling = itemView.findViewById(R.id.callButto);
            prof_image = itemView.findViewById(R.id.itemImage);
            onlinu = itemView.findViewById(R.id.online);
            vu = itemView.findViewById(R.id.view_him);
            call_button = itemView.findViewById(R.id.callButton);
            viewChat_img = itemView.findViewById(R.id.viewChats);

            call_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    User user = userArrayList.get(getAdapterPosition());
                    auth = FirebaseAuth.getInstance();
                    firebaseUser = auth.getCurrentUser();

                    DatabaseReference isBusy = FirebaseDatabase.getInstance().getReference("Busy");
                    isBusy.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(user.getUserid())){
                                ((MainActivity)context).callUser(user ,true,false);
                            }
                            else{

                                //user not busy
                                String name_conn_to = user.getName();
                                uska_id = user.getUserid();
                                Toast.makeText(context, "Conn to : " + name_conn_to, Toast.LENGTH_SHORT).show();

                                //get my email
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child(firebaseUser.getUid()).child("email");
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        my_name = dataSnapshot.getValue().toString();

                                        Toast.makeText(context, "My name : " + my_name, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                //get my uid
                                DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child(firebaseUser.getUid()).child("userid");
                                reference2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        my_id = dataSnapshot.getValue().toString();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                //set call details ke tu usse call karta...

                                //idhar tere call details me usko ring jaata ye pratit hota
                                boolean ringo = true;
                                boolean conn = false;

                                ConnectionDetails details = new ConnectionDetails(ringo , conn ,uska_id) ;
                                reference3.child(firebaseUser.getUid()).child("Call details").child("to")
                                        .setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                        }
                                        else{
                                            Toast.makeText(context, "Fb error on set call details from sender", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                ConnectionDetails details_ = new ConnectionDetails(ringo , conn ,uska_id) ;
                                reference3.child(firebaseUser.getUid()).child("Call details").child("from")
                                        .setValue(details_).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                        }
                                        else{
                                            Toast.makeText(context, "Fb error on set call details from sender", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                //////////setting me uska
                                ConnectionDetails details1_ = new ConnectionDetails(ringo , conn ,firebaseUser.getUid()) ;
                                reference3.child(uska_id).child("Call details").child("to")
                                        .setValue(details1_).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                        }
                                        else{
                                            Toast.makeText(context, "Fb error on set call details from sender", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                ConnectionDetails details2_ = new ConnectionDetails(ringo , conn ,firebaseUser.getUid()) ;
                                reference3.child(uska_id).child("Call details").child("from")
                                        .setValue(details2_).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                        }
                                        else{
                                            Toast.makeText(context, "Fb error on set call details from sender", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                ///////////

                                //idhar uske call details me aata
                                boolean ringo2 = true;
                                boolean conn2 = false;

                                final ConnectionDetails details2 = new ConnectionDetails(ringo2 , conn2 ,firebaseUser.getUid()) ;
                                //check kar wo dusre call pe h ?
                                DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Calls").child(uska_id)
                                        .child("Call details");
                                UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.hasChild("from")) {
                                            //Toast.makeText(context, "call busy", Toast.LENGTH_SHORT).show();
                                            //busy = true;
                                        }
                                        UsersRef.child("from")
                                                .setValue(details2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                }
                                                else{
                                                    Toast.makeText(context, "Fb error on set call details on receiver side", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }) ;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                ((MainActivity)context).callUser(user ,busy ,false);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            });

            vu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = userArrayList.get(getAdapterPosition());

                    auth = FirebaseAuth.getInstance();
                    firebaseUser = auth.getCurrentUser();

                    String name_conn_to = user.getName();
                    String uska_id = user.getUserid();
                    //Toast.makeText(context, "Conn to : " + name_conn_to, Toast.LENGTH_SHORT).show();

                    ((MainActivity)context).viewProfile(user);
                }
            });

            //see chat on click listener

            se_chat = itemView.findViewById(R.id.see_chat);

            se_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userArrayList.get(getAdapterPosition());

                    auth = FirebaseAuth.getInstance();
                    firebaseUser = auth.getCurrentUser();

                    String name_conn_to = user.getName();
                    String uska_id = user.getUserid();
                    Toast.makeText(context, "Conn to : " + name_conn_to, Toast.LENGTH_SHORT).show();

                    ((MainActivity)context).viewUser(user);
                }
            });

            viewChat_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = userArrayList.get(getAdapterPosition());

                    auth = FirebaseAuth.getInstance();
                    firebaseUser = auth.getCurrentUser();

                    String name_conn_to = user.getName();
                    String uska_id = user.getUserid();
                    Toast.makeText(context, "Conn to : " + name_conn_to, Toast.LENGTH_SHORT).show();

                    ((MainActivity)context).viewUser(user);
                }
            });
        }
    }
}
