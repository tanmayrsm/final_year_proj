package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beproj3.Adapters.AllReceivedAdapter;
import com.example.beproj3.Adapters.AllSentAdapter;
import com.example.beproj3.Models.SentModel;
import com.example.beproj3.Models.User;
import com.example.beproj3.Models.UserStatus;
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
import java.util.Locale;

public class AllReceived extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ArrayList<User> userArrayList;
    DatabaseReference reference;
    TextView usrname;
    boolean bullo = false;

    //khalti che 4 button
    Button my_contacts ,my_users , sent_acti ,received_acti;
    String my_id ,my_name ,uska_id ,uska_name ,usrname_string ,usrname_string_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_received);

        recyclerView = findViewById(R.id.recyclerView2);
        usrname = findViewById(R.id.username_in_allsent);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        usrname_string = firebaseUser.getUid();
        userArrayList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        my_contacts = findViewById(R.id.mycontacts);
        my_users = findViewById(R.id.allusers);
        sent_acti = findViewById(R.id.sent);
        received_acti = findViewById(R.id.received);

        //my contacts..call wla tab
        my_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bullo = true;
                Intent i = new Intent(AllReceived.this ,MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
        //my received..call wla tab
        sent_acti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bullo = true;
                Intent i = new Intent(AllReceived.this ,AllSent.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
        //all users
        my_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bullo = true;
                Intent i = new Intent(AllReceived.this ,AllContacts.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        //set username on top
        DatabaseReference usr = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(usrname_string).child("email");
        usr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usrname_string_email = dataSnapshot.getValue().toString();
                usrname.setText(usrname_string_email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fetchAllUsers();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!bullo)
        setStatus("offline");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!bullo)
        setStatus("offline");
    }

    @Override
    protected void onStart() {
        super.onStart();
        bullo = false;
        setStatus("online");
    }
    public void setStatus(String status){
        DatabaseReference df = FirebaseDatabase.getInstance().getReference("User_status").child(firebaseUser.getUid());
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String res = currentDate + " " + currentTime;

        UserStatus ui = new UserStatus(res ,status ,firebaseUser.getUid());

        df.setValue(ui);
    }

    private void fetchAllUsers() {

        DatabaseReference refo = FirebaseDatabase.getInstance().getReference("conn_req").child(firebaseUser.getUid()).child("received");
        refo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //ArrayList<User> userArrayList = new ArrayList<>();
                userArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SentModel sento = snapshot.getValue(SentModel.class);

                    //Log.e("pop",sento.toString());
                    DatabaseReference refi = FirebaseDatabase.getInstance().getReference("Users").child(sento.uid);
                    refi.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            //Log.e("User:",user.toString());
                            userArrayList.add(user);
                            // Log.e("Sending1:",userArrayList.toString());
                            AllReceivedAdapter adapter = new AllReceivedAdapter(AllReceived.this ,userArrayList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Log.e("Sending all received:",userArrayList.toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void viewProfile(User user) {
        String my_id = firebaseUser.getUid();
        String uska_id = user.getUserid();
        String uska_naam = user.getName();

        Toast.makeText(this, "View profile of "+uska_naam, Toast.LENGTH_SHORT).show();

        bullo = true;
        Intent i = new Intent(AllReceived.this ,ViewProfile.class);
        i.putExtra("UskaId",uska_id);
        i.putExtra("Activity","received");
        startActivity(i);
    }
}
