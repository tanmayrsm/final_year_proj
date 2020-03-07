package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beproj3.Adapters.AllChatsAdapter;
import com.example.beproj3.Adapters.AllNativeChatsAdapter;
import com.example.beproj3.Adapters.AllprevCallsAdapter;
import com.example.beproj3.Adapters.AllprevChatsAdapter;
import com.example.beproj3.Models.Chats;
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
import java.util.List;
import java.util.Locale;

public class AllprevMessages extends AppCompatActivity {

    RecyclerView recyclerView ,native_recycler;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ArrayList<Chats> chatArrayList;
    DatabaseReference reference;
    TextView usrname;

    private final List<Chats> nativechatlist = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager, linearLayoutManager2;
    private AllChatsAdapter chatsAdapter;
    private AllNativeChatsAdapter nativeChatsAdapter;

    Switch s2;

    String uska_id ,my_id,start_time ,usrname_string_email;
    Chats chato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allprev_messages);

        recyclerView = findViewById(R.id.recyclerView46);
        native_recycler = findViewById(R.id.native_recyclerview12);

        usrname = findViewById(R.id.username_in_allprev_messages);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        Intent intent = getIntent();
        uska_id = intent.getStringExtra("UskaId");
        start_time = intent.getStringExtra("start_time");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        native_recycler.setHasFixedSize(true);
        native_recycler.setLayoutManager(new LinearLayoutManager(this));

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

        s2 = findViewById(R.id.switch3);

        s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    recyclerView.setVisibility(View.GONE);
                    native_recycler.setVisibility(View.VISIBLE);
                }
                else{
                    recyclerView.setVisibility(View.VISIBLE);
                    native_recycler.setVisibility(View.GONE);
                }
            }
        });

        fetchAllMessages();
    }
    public void setStatus(String status){
        DatabaseReference df = FirebaseDatabase.getInstance().getReference("User_status").child(firebaseUser.getUid());
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String res = currentDate + " " + currentTime;

        UserStatus ui = new UserStatus(res ,status ,firebaseUser.getUid());

        df.setValue(ui);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setStatus("online");
    }

    private void fetchAllMessages() {

        reference = FirebaseDatabase.getInstance().getReference().child("prev_Calls").child(firebaseUser.getUid()).child(start_time).child("Chats");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Chats> chatArrayList = new ArrayList<>();
                chatArrayList.clear();

                if(!dataSnapshot.exists()){
                    Toast.makeText(AllprevMessages.this, "No chats here", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e("all chats before","chatarraylist:"+chatArrayList.toString());

                for(DataSnapshot dss : dataSnapshot.getChildren()){

                    Chats chats = dss.getValue(Chats.class);
                    Log.e("chats2222:",chats.getChat()+" --" +chats.getTime());

                    if(true){
                        chatArrayList.add(chats);
                        Log.e("Added:",chats.getChat());
                        nativechatlist.add(chats);
                    }

                    AllprevChatsAdapter adapter = new AllprevChatsAdapter(AllprevMessages.this ,chatArrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    nativeChatsAdapter = new AllNativeChatsAdapter(nativechatlist);
                    linearLayoutManager2 = new LinearLayoutManager(AllprevMessages.this);
                    native_recycler.setLayoutManager(linearLayoutManager2);

                    native_recycler.setAdapter(nativeChatsAdapter);
                    nativeChatsAdapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllprevMessages.this, "error h :"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
