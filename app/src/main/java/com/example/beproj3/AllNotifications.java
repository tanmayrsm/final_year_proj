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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beproj3.Adapters.AllContactsAdapter;
import com.example.beproj3.Adapters.AllNotificationsAdapter;
import com.example.beproj3.Models.Notts;
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

public class AllNotifications extends AppCompatActivity {
    RecyclerView rsv ;
    TextView username ,nono;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ArrayList<Notts> nottsArrayList;
    DatabaseReference reference;

    ImageView top ,delo;

    String username_string;
    Toolbar wtg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_notifications);

        nono = findViewById(R.id.no_contacts);

        rsv = findViewById(R.id.recyclerView44);
        username = findViewById(R.id.username_in_allnotifications);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        rsv.setHasFixedSize(true);
        rsv.setLayoutManager(new LinearLayoutManager(this));

        wtg = findViewById(R.id.simple_toolbar2);
        setSupportActionBar(wtg);
        getSupportActionBar().setTitle("");


        top = findViewById(R.id.dp_back);
        delo = findViewById(R.id.del__);

        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AllNotifications.this,MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        delo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference rio = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
                rio.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            AlertDialog alertDialog = new AlertDialog.Builder(AllNotifications.this).create();
                            alertDialog.setTitle("Clear Notifications ");
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setMessage("Are you sure to clear all notifications ? ");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    dataSnapshot.getRef().removeValue();
                                    Intent i = new Intent(AllNotifications.this ,MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                    Toast.makeText(AllNotifications.this, "All notifications cleared", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AllNotifications.this, "No notifications to clear", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        //set username on top
        DatabaseReference usr = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(firebaseUser.getUid()).child("email");
        usr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username_string = dataSnapshot.getValue().toString();
                username.setText(username_string);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        fetchAllNotifications();
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

    private void fetchAllNotifications() {
        ArrayList<Notts> nottsArrayList = new ArrayList<>();
        nottsArrayList.clear();

        reference = FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid());

        //get missed calls
        reference.child("calling_nott").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(!dataSnapshot.exists()){
                    Toast.makeText(AllNotifications.this, "No notts for call", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        Notts notts = dss.getValue(Notts.class);

//                      DatabaseReference chomu = (DatabaseReference) dss.getChildren();
                        Log.e("Notts:", dss.getValue().toString() + " --");

                        if (!notts.getUid().equals(firebaseUser.getUid())) {

                            nottsArrayList.add(notts);
                            Log.e("Added:", notts.getUid());
                        }

                        AllNotificationsAdapter adapter = new AllNotificationsAdapter(AllNotifications.this, nottsArrayList);
                        rsv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();


                    }
                }
                //Log.e("all contacts","userarraylist:"+userArrayList.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllNotifications.this, "error h :"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //get busy calls
        reference.child("busy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(!dataSnapshot.exists()){
                    //Toast.makeText(AllNotifications.this, "No notts for busy", Toast.LENGTH_SHORT).show();
                    //rsv.setVisibility(View.GONE);
                    //nono.setVisibility(View.VISIBLE);
                    return;
                }
                // Log.e("all contacts efore","userarraylist:"+userArrayList.toString());
                else {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        Notts notts = dss.getValue(Notts.class);

//                      DatabaseReference chomu = (DatabaseReference) dss.getChildren();
                        Log.e("Notts:", dss.getValue().toString() + " --");

                        if (!notts.getUid().equals(firebaseUser.getUid())) {

                            nottsArrayList.add(notts);

                            Log.e("Added:", notts.getUid());
                        }

                        AllNotificationsAdapter adapter = new AllNotificationsAdapter(AllNotifications.this, nottsArrayList);
                        rsv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllNotifications.this, "error h :"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //get received req
        reference.child("received_req").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(!dataSnapshot.exists()){
                    Toast.makeText(AllNotifications.this, "No notts for received", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Log.e("all contacts efore","userarraylist:"+userArrayList.toString());
                else {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
//
                        Notts notts = dss.getValue(Notts.class);

//                      DatabaseReference chomu = (DatabaseReference) dss.getChildren();
                        Log.e("Notts:", dss.getValue().toString() + " --");

                        if (!notts.getUid().equals(firebaseUser.getUid())) {

                            nottsArrayList.add(notts);
                            Log.e("Added:", notts.getUid());
                        }

                        AllNotificationsAdapter adapter = new AllNotificationsAdapter(AllNotifications.this, nottsArrayList);
                        rsv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllNotifications.this, "error h :"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //get accpeted req
        reference.child("accepted_req").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(!dataSnapshot.exists()){
                    Toast.makeText(AllNotifications.this, "No notts for accepted", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Log.e("all contacts efore","userarraylist:"+userArrayList.toString());
                else{
                for(DataSnapshot dss : dataSnapshot.getChildren()){
                    Notts notts = dss.getValue(Notts.class);

//                      DatabaseReference chomu = (DatabaseReference) dss.getChildren();
                    Log.e("Notts:",dss.getValue().toString()+" --");

                    if(!notts.getUid().equals(firebaseUser.getUid())){

                        nottsArrayList.add(notts);
                        Log.e("Added:",notts.getUid());
                    }

                    AllNotificationsAdapter adapter = new AllNotificationsAdapter(AllNotifications.this ,nottsArrayList);
                    rsv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                }

                Log.e("all notts","nottsarray list:"+nottsArrayList.toString());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllNotifications.this, "error h :"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    rsv.setVisibility(View.GONE);
                    nono.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
