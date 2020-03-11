package com.example.beproj3.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beproj3.MainActivity;
import com.example.beproj3.Models.ConnectionDetails;
import com.example.beproj3.Models.SentModel;
import com.example.beproj3.Models.User;
import com.example.beproj3.Models.wordsModel;
import com.example.beproj3.R;
import com.example.beproj3.capture_open_image;
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

public class AllButtonsAdapter extends RecyclerView.Adapter<AllButtonsAdapter.AllUsersViewholder> {

    Activity context;
    ArrayList<wordsModel> words;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    boolean busy  = false;
    String uska_id;

    DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference().child("Calls");
    DatabaseReference ref_call_user = FirebaseDatabase.getInstance().getReference().child("Calls");
    public AllButtonsAdapter(Activity context ,ArrayList<wordsModel> words){
        this.context = context;
        this.words = words;
    }

    @Override
    public AllUsersViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_button,parent,false);
        AllUsersViewholder allUsersAdapter = new AllUsersViewholder(view);

        return allUsersAdapter;
    }

    @Override
    public void onBindViewHolder(AllUsersViewholder holder, int position) {

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        wordsModel wp = words.get(position);

        //add model file and send to dB and then retreive here
        holder.textop.setText(wp.getWord());
//        //set online status
//        Log.e("users id:",user.getUserid());
//        DatabaseReference provo = FirebaseDatabase.getInstance().getReference("User_status").child(user.getUserid()).child("status");
//        provo.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    if(dataSnapshot.getValue().toString().equals("online")){
//                        holder.onlinu.setVisibility(View.VISIBLE);
//                    }else{
//                        holder.onlinu.setVisibility(View.INVISIBLE);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public class AllUsersViewholder extends RecyclerView.ViewHolder{
        Button textop;
        String my_name ,my_id;

        public AllUsersViewholder(View itemView) {
            super(itemView);
            textop = itemView.findViewById(R.id.main_button);




            textop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    wordsModel wo = words.get(getAdapterPosition());

                    auth = FirebaseAuth.getInstance();
                    firebaseUser = auth.getCurrentUser();

//                    String name_conn_to = user.getName();
//                    String uska_id = user.getUserid();
                    //Toast.makeText(context, "Conn to : " + name_conn_to, Toast.LENGTH_SHORT).show();

                    ((capture_open_image)context).viewWord(wo);
                }
            });

        }
    }
}
