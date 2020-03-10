package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class AllprevMessages extends AppCompatActivity {

    RecyclerView recyclerView ,native_recycler;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ArrayList<Chats> chatArrayList;
    DatabaseReference reference;
    TextView usrname;

    Toolbar bewda;
    ImageView del_ ,dp_tool ,backo ,loc ,send_;
    TextView usre ,curr_status;
    CircleImageView cir;



    private final List<Chats> nativechatlist = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager, linearLayoutManager2;
    private AllChatsAdapter chatsAdapter;
    private AllNativeChatsAdapter nativeChatsAdapter;

    Switch s2;

    String uska_id ,my_id,start_time ,usrname_string_email ,uska_name ,uska_pic;
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

        //toolbar settings
        bewda = findViewById(R.id.barprofilee);
        setSupportActionBar(bewda);

        loc = findViewById(R.id._location);
        send_ = findViewById(R.id.send_btn);
        loc.setVisibility(View.INVISIBLE);
        send_.setVisibility(View.INVISIBLE);

        del_ = findViewById(R.id.del__);
        usre = findViewById(R.id.text_toolbar);
        dp_tool = findViewById(R.id.dp_toolbar);
        curr_status = findViewById(R.id.curr_status);
        backo = findViewById(R.id._backo);

        getSupportActionBar().setTitle("");


        Intent intent = getIntent();
        uska_id = intent.getStringExtra("UskaId");
        start_time = intent.getStringExtra("start_time");

        DatabaseReference deaf = FirebaseDatabase.getInstance().getReference("Users").child(uska_id);
        deaf.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uska_name = dataSnapshot.getValue().toString();
                usre.setText(uska_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        deaf.child("image_url").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Glide.with(getApplicationContext()).load(dataSnapshot.getValue().toString()).into(dp_tool);

                }else{
                    Glide.with(getApplicationContext()).load(R.drawable.ic_face_black_24dp).into(dp_tool);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        backo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AllprevMessages.this ,Allpreviouscalls.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
        //set current status
        DatabaseReference rt = FirebaseDatabase.getInstance().getReference("User_status").child(uska_id);
        rt.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("online")){
                    curr_status.setText("online");
                }else{
                    rt.child("time").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            curr_status.setText(dataSnapshot.getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //del chat hist
        del_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference refo = FirebaseDatabase.getInstance().getReference("prev_Calls").child(firebaseUser.getUid()).child(start_time);
                refo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            AlertDialog alertDialog = new AlertDialog.Builder(AllprevMessages.this).create();
                            alertDialog.setTitle("Delete chat history with "+uska_name);
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setMessage("Do you really want to delete this chat history with "+uska_name+" ?");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    dataSnapshot.getRef().removeValue();
                                    Intent i = new Intent(AllprevMessages.this ,Allpreviouscalls.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                }
                            });

                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.show();
                        }else{
                            Toast.makeText(AllprevMessages.this, "Chats are empty", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });




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
