package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beproj3.Adapters.AllContactsAdapter;
import com.example.beproj3.Models.User;
import com.example.beproj3.Models.UserStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.calling.Call;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AllContacts extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ArrayList<User> userArrayList;
    DatabaseReference reference;
    TextView usrname;
    boolean bullo = false;
    EditText searcho;

    //khalti che 4 button
    Button my_contacts ,my_users , sent_acti ,received_acti;
    String my_id ,my_name ,uska_id ,uska_name ,usrname_string ,usrname_string_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contacts);



        searcho = findViewById(R.id.search);

        recyclerView = findViewById(R.id.recyclerView2);
        usrname = findViewById(R.id.username_in_allcontacts);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        usrname_string = firebaseUser.getUid();

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
                Intent i = new Intent(AllContacts.this ,MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        //my_users tab
        sent_acti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bullo = true;
                Intent i = new Intent(AllContacts.this,AllSent.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        //my_received tab
        received_acti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bullo = true;
                Intent i = new Intent(AllContacts.this,AllReceived.class);
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

        //fetch the searched users
        searcho.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(searcho.getText().toString().length() > 0)
                    fetchSearchedUsers(searcho.getText().toString());
                else if(searcho.getText().toString().length() == 0)
                    fetchAllUsers();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
//if(user.getName().toLowerCase().contains(s.toLowerCase()))
//
    }

    private void fetchSearchedUsers(String toString) {
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<User> userArrayList = new ArrayList<>();
                userArrayList.clear();

                if(!dataSnapshot.exists()){
                    Toast.makeText(AllContacts.this, "No users registered", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e("all contacts efore","userarraylist:"+userArrayList.toString());

                for(DataSnapshot dss : dataSnapshot.getChildren()){

                    User user = dss.getValue(User.class);
                    Log.e("user and fireid:",user.getUserid()+" --" +firebaseUser.getUid());

                    if(!user.getUserid().equals(firebaseUser.getUid()) ){
                        if(user.getName().toLowerCase().contains(toString.toLowerCase()))
                            userArrayList.add(user);
                        Log.e("Added:",user.getEmail());
                    }

                    AllContactsAdapter adapter = new AllContactsAdapter(AllContacts.this ,userArrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                //Log.e("all contacts","userarraylist:"+userArrayList.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllContacts.this, "error h :"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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


    //fetch users in recyclerview
    private void fetchAllUsers() {
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<User> userArrayList = new ArrayList<>();
                userArrayList.clear();

                if(!dataSnapshot.exists()){
                    Toast.makeText(AllContacts.this, "No users registered", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e("all contacts efore","userarraylist:"+userArrayList.toString());

                for(DataSnapshot dss : dataSnapshot.getChildren()){

                    User user = dss.getValue(User.class);
                    Log.e("user and fireid:",user.getUserid()+" --" +firebaseUser.getUid());

                    if(!user.getUserid().equals(firebaseUser.getUid())){

                        userArrayList.add(user);
                        Log.e("Added:",user.getEmail());
                    }

                        AllContactsAdapter adapter = new AllContactsAdapter(AllContacts.this ,userArrayList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                }

                //Log.e("all contacts","userarraylist:"+userArrayList.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllContacts.this, "error h :"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendReq(User user){
        String my_id = firebaseUser.getUid();
        String uska_id = user.getUserid();
        String uska_naam = user.getName();

        Toast.makeText(this, "Request sent to "+uska_naam, Toast.LENGTH_SHORT).show();
    }

    public void viewProfile(User user) {
        String my_id = firebaseUser.getUid();
        String uska_id = user.getUserid();
        String uska_naam = user.getName();

        Toast.makeText(this, "View profile of "+uska_naam, Toast.LENGTH_SHORT).show();

        Log.e("Pass id:",user.getUserid());

        Intent i = new Intent(AllContacts.this ,ViewProfile.class);
        i.putExtra("UskaId",uska_id);
        i.putExtra("Activity","Contacts");
        bullo = true;
        startActivity(i);
    }

    public void  acc(){
        bullo = true;
        Intent i = new Intent(AllContacts.this,AllReceived.class);
        startActivity(i);
        finish();
    }
}
