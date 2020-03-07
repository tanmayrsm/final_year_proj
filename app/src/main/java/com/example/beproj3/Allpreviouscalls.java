package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beproj3.Adapters.AllContactsAdapter;
import com.example.beproj3.Adapters.AllprevCallsAdapter;
import com.example.beproj3.Models.User;
import com.example.beproj3.Models.UserStatus;
import com.example.beproj3.Models.prevCalls;
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

public class Allpreviouscalls extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ArrayList<User> userArrayList;
    DatabaseReference reference;
    TextView usrname;

    String my_id ,my_name ,uska_id ,uska_name ,usrname_string ,usrname_string_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allpreviouscalls);

        recyclerView = findViewById(R.id.recyclerView45);
        usrname = findViewById(R.id.username_in_allprevious);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //set username on top
        DatabaseReference usr = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(firebaseUser.getUid()).child("email");
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

        fetchAllMessages();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    private void fetchAllMessages() {
        reference = FirebaseDatabase.getInstance().getReference().child("prev_Calls").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<prevCalls> prevArrayList = new ArrayList<>();
                prevArrayList.clear();

                if(!dataSnapshot.exists()){
                    Toast.makeText(Allpreviouscalls.this, "No users registered", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e("all prev efore","prevarraylist:"+prevArrayList.toString());

                for(DataSnapshot dss : dataSnapshot.getChildren()){

                    prevCalls prevcalls = dss.getValue(prevCalls.class);
                    Log.e("start and chats:",prevcalls.getStart()+" --" +prevcalls.getChats());

                    if(prevcalls.getChats()!= null){
                        prevArrayList.add(prevcalls);
                        Log.e("Added:",prevcalls.getUid());
                    }

                    AllprevCallsAdapter adapter = new AllprevCallsAdapter(Allpreviouscalls.this ,prevArrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Allpreviouscalls.this, "error h :"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void viewMessage(prevCalls prev) {
        String my_id = firebaseUser.getUid();
        String uska_id = prev.getUid();

        Toast.makeText(this, "View chat of "+uska_id, Toast.LENGTH_SHORT).show();

        Intent i = new Intent(Allpreviouscalls.this ,AllprevMessages.class);
        i.putExtra("UskaId",uska_id);
        i.putExtra("start_time",prev.getStart());
        startActivity(i);
    }
}
