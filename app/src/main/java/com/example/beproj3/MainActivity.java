package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.multidex.MultiDexApplication;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.beproj3.Adapters.AllNotificationsAdapter;
import com.example.beproj3.Adapters.AllReceivedAdapter;
import com.example.beproj3.Adapters.AllUsersAdapter;
import com.example.beproj3.Models.Chats;
import com.example.beproj3.Models.Locatioi;
import com.example.beproj3.Models.Notts;
import com.example.beproj3.Models.Prevalent;
import com.example.beproj3.Models.SentModel;
import com.example.beproj3.Models.User;
import com.example.beproj3.Models.UserStatus;
import com.example.beproj3.Models.Value;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    SinchClient sinchClient;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Call call;
    ArrayList<User> userArrayList;
    DatabaseReference reference;
    TextView usrname;
    ImageView not_bell, prevCalls, searc;
    int no_of_notifications = 0;
    ProgressDialog loadingbar;
    public boolean bullap = false;
    MediaPlayer player;
    boolean lagau = true;
    Toolbar toola;
    EditText searcho;
    TextView nono;

    private Vibrator vib;
    private MediaPlayer mp;
    CircleImageView tool_dp;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;

    LinearLayout my_con, sento, recvo;

    Dialog call_him, receive_him;

    Button my_contacts, my_users, sent_acti, received_acti, no_of_notts;

    String my_id, my_name, uska_ido, uska_name, usrname_string, he_id, usrname_string_email, uska_img2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        call_him = new Dialog(this);
        receive_him = new Dialog(this);


        nono = findViewById(R.id.no_contacts);

        toola = findViewById(R.id.mainbaro);
        setSupportActionBar(toola);
        searc = findViewById(R.id.search_btn);

        my_con = findViewById(R.id.my_users_linear);
        sento = findViewById(R.id.my_sent_linear);
        recvo = findViewById(R.id.my_received_linear);

        //my_users tab
        my_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bullap = true;
                Intent i = new Intent(MainActivity.this, AllContacts.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        sento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bullap = true;
                Intent i = new Intent(MainActivity.this, AllSent.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        recvo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bullap = true;
                Intent i = new Intent(MainActivity.this, AllReceived.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });


        searcho = findViewById(R.id.search);

        recyclerView = findViewById(R.id.recyclerView2);
//        usrname = findViewById(R.id.username);
//
//        usrname.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                bullap = true;
//                startActivity(new Intent(MainActivity.this ,EditProfile.class));
//            }
//        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        userArrayList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        not_bell = findViewById(R.id.notification_bell);
        no_of_notts = findViewById(R.id.notts_no);

        my_contacts = findViewById(R.id.mycontacts);
        my_users = findViewById(R.id.allusers);
        sent_acti = findViewById(R.id.sent);
        received_acti = findViewById(R.id.received);

        prevCalls = findViewById(R.id.call_history);

        tool_dp = findViewById(R.id.dp_toolbar2);
        //set dp on toolbar
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                if (user.getImage_url() != null)
                    Glide.with(getApplicationContext()).load(user.getImage_url()).into(tool_dp);
                else
                    Glide.with(getApplicationContext()).load(R.drawable.ic_face_black_24dp).into(tool_dp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tool_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bullap = true;
                startActivity(new Intent(MainActivity.this, EditProfile.class));
            }
        });


        //previous call history tab
        prevCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bullap = true;
                startActivity(new Intent(MainActivity.this, Allpreviouscalls.class));
            }
        });

        //noti tab
        not_bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bullap = true;
                startActivity(new Intent(MainActivity.this, AllNotifications.class));
            }
        });
        no_of_notts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bullap = true;
                startActivity(new Intent(MainActivity.this, AllNotifications.class));
            }
        });

        //my_users tab
        my_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bullap = true;
                Intent i = new Intent(MainActivity.this, AllContacts.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        sent_acti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bullap = true;
                Intent i = new Intent(MainActivity.this, AllSent.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        received_acti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bullap = true;
                Intent i = new Intent(MainActivity.this, AllReceived.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        //search bar visible
        searc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searcho.getVisibility() == View.GONE)
                    searcho.setVisibility(View.VISIBLE);
                else if (searcho.getVisibility() == View.VISIBLE) {
                    searcho.setVisibility(View.GONE);
                    fetchAllUsers();
                }
            }
        });


        usrname_string = firebaseUser.getUid();
        DatabaseReference usr = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(usrname_string).child("email");
        usr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usrname_string_email = dataSnapshot.getValue().toString();
                //usrname.setText(usrname_string_email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(firebaseUser.getUid())
                .applicationKey("07ef8714-79aa-4193-be6c-70d7eec6ed6a")
                .applicationSecret("kUL0vXIk+EOEx4f2OjS/TA==")
                .environmentHost("clientapi.sinch.com")
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener() {

        });
        sinchClient.start();

        fetchAllUsers();
        set_no_of_notifications();

        //fetch the searched users
        searcho.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searcho.getText().toString().length() > 0)
                    fetchSearchedUsers(searcho.getText().toString());
                else if (searcho.getText().toString().length() == 0)
                    fetchAllUsers();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ///ur location

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


    }


    private void set_no_of_notifications() {
        //no_of_notts.setBackgroundColor(Color.WHITE);

        DatabaseReference refp = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        refp.child("calling_nott").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //Toast.makeText(MainActivity.this, "No notts for call", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        Notts notts = dss.getValue(Notts.class);

//                      DatabaseReference chomu = (DatabaseReference) dss.getChildren();
                        Log.e("Notts:", dss.getValue().toString() + " --");

                        if (!notts.getUid().equals(firebaseUser.getUid())) {
                            if (!notts.isSeen()) {
                                no_of_notifications++;
                                no_of_notts.setText(String.valueOf(no_of_notifications));
                                //no_of_notts.setBackgroundColor(Color.RED);
                                no_of_notts.setVisibility(View.VISIBLE);
                            }
                            Log.e("Added:", notts.getUid());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        refp.child("received_req").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //Toast.makeText(MainActivity.this, "No notts for call", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        Notts notts = dss.getValue(Notts.class);

//                      DatabaseReference chomu = (DatabaseReference) dss.getChildren();
                        Log.e("Notts:", dss.getValue().toString() + " --");

                        if (!notts.getUid().equals(firebaseUser.getUid())) {
                            if (!notts.isSeen()) {
                                no_of_notifications++;
                                no_of_notts.setText(String.valueOf(no_of_notifications));
                                //no_of_notts.setBackgroundColor(Color.RED);
                                no_of_notts.setVisibility(View.VISIBLE);
                            }
                            Log.e("Added:", notts.getUid());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        refp.child("accepted_req").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //Toast.makeText(MainActivity.this, "No notts for call", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        Notts notts = dss.getValue(Notts.class);

//                      DatabaseReference chomu = (DatabaseReference) dss.getChildren();
                        Log.e("Notts:", dss.getValue().toString() + " --");

                        if (!notts.getUid().equals(firebaseUser.getUid())) {
                            if (!notts.isSeen()) {
                                no_of_notifications++;
                                no_of_notts.setText(String.valueOf(no_of_notifications));
                                //no_of_notts.setBackgroundColor(Color.RED);
                                no_of_notts.setVisibility(View.VISIBLE);
                            }
                            Log.e("Added:", notts.getUid());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        refp.child("busy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //Toast.makeText(MainActivity.this, "No notts for call", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        Notts notts = dss.getValue(Notts.class);

//                      DatabaseReference chomu = (DatabaseReference) dss.getChildren();
                        Log.e("Notts:", dss.getValue().toString() + " --");

                        if (!notts.getUid().equals(firebaseUser.getUid())) {
                            if (!notts.isSeen()) {
                                no_of_notifications++;
                                no_of_notts.setText(String.valueOf(no_of_notifications));
                                //no_of_notts.setBackgroundColor(Color.RED);
                                no_of_notts.setVisibility(View.VISIBLE);
                            }
                            Log.e("Added:", notts.getUid());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void fetchAllUsers() {

        DatabaseReference refg = FirebaseDatabase.getInstance().getReference("Contact_list").child(firebaseUser.getUid());

        refg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userArrayList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SentModel sento = snapshot.getValue(SentModel.class);
                    Log.e("My contacts", sento.toString());
                    Log.e("sento uid:", sento.uid);
                    DatabaseReference refi = FirebaseDatabase.getInstance().getReference("Users").child(sento.uid);
                    refi.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            Log.e("User:", user.toString());
                            Log.e("user array list before", userArrayList.toString());

                            userArrayList.add(user);


                            Log.e("MyCOntact1:", userArrayList.toString());
                            AllUsersAdapter adapter = new AllUsersAdapter(MainActivity.this, userArrayList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            recyclerView.setHasFixedSize(true);

                            if (userArrayList.size() == 0) {
                                recyclerView.setVisibility(View.GONE);
                                nono.setVisibility(View.VISIBLE);
                            } else {
                                nono.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Log.e("Main all contacts:", userArrayList.toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userArrayList.clear();
//                for(DataSnapshot dss : dataSnapshot.getChildren()){
//                    User user = dss.getValue(User.class);
//                    if(!user.getUserid().equals(firebaseUser.getUid()))
//                        userArrayList.add(user);
//                }
//
//                AllUsersAdapter adapter = new AllUsersAdapter(MainActivity.this ,userArrayList);
//                recyclerView.setAdapter(adapter);
//
//                adapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(MainActivity.this, "error h :"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void fetchSearchedUsers(String s) {

        DatabaseReference refg = FirebaseDatabase.getInstance().getReference("Contact_list").child(firebaseUser.getUid());

        refg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userArrayList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SentModel sento = snapshot.getValue(SentModel.class);
//                    Log.e("My contacts",sento.toString());
//                    Log.e("sento uid:",sento.uid);
                    DatabaseReference refi = FirebaseDatabase.getInstance().getReference("Users").child(sento.uid);
                    refi.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            Log.e("User:", user.toString());
                            Log.e("user array list before", userArrayList.toString());

                            if (user.getName().toLowerCase().contains(s.toLowerCase()))
                                userArrayList.add(user);

                            Log.e("MyCOntact1 in search:", userArrayList.toString());
                            AllUsersAdapter adapter = new AllUsersAdapter(MainActivity.this, userArrayList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            recyclerView.setHasFixedSize(true);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Log.e("Main all contacts:", userArrayList.toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userArrayList.clear();
//                for(DataSnapshot dss : dataSnapshot.getChildren()){
//                    User user = dss.getValue(User.class);
//                    if(!user.getUserid().equals(firebaseUser.getUid()))
//                        userArrayList.add(user);
//                }
//
//                AllUsersAdapter adapter = new AllUsersAdapter(MainActivity.this ,userArrayList);
//                recyclerView.setAdapter(adapter);
//
//                adapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(MainActivity.this, "error h :"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallProgressing(Call call) {
            Toast.makeText(MainActivity.this, "Call is ringing", Toast.LENGTH_SHORT).show();

            //add to his notifications that I called him
            DatabaseReference chaman2 = FirebaseDatabase.getInstance().getReference("Calls").child(auth.getUid()).child("Call details").child("from").child("uid");
            chaman2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DatabaseReference julia = FirebaseDatabase.getInstance().getReference("Notifications")
                            .child(dataSnapshot.getValue().toString()).child("calling_nott").child(auth.getUid());

                    he_id = dataSnapshot.getValue().toString();
                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                    String res = currentDate + " " + currentTime;

                    Notts n = new Notts(auth.getUid(), false, res, "calling_nott");

                    julia.setValue(n)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Not added in his notts", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //TODO
            // add him to my call history..that I called him
        }

        @Override
        public void onCallEstablished(Call call) {
            DatabaseReference chaman = FirebaseDatabase.getInstance().getReference("Calls").child(auth.getUid()).child("Call details").child("from").child("uid");
            chaman.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //remove my missed call name from notts
                    DatabaseReference julia = FirebaseDatabase.getInstance().getReference("Notifications")
                            .child(dataSnapshot.getValue().toString()).child("calling_nott").child(auth.getUid());
                    julia.removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            // Toast.makeText(MainActivity.this, "Call established and I am : " + auth.getCurrentUser(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, CallGoingActivity.class);
            startActivity(intent);
        }

        @Override
        public void onCallEnded(Call endedCall) {
            Toast.makeText(MainActivity.this, "Call ended", Toast.LENGTH_SHORT).show();
            call = null;
            endedCall.hangup();

        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            if (firebaseUser != null) {
                auth.signOut();
                Paper.book().destroy();

                finish();
                bullap = false;
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        } else if (item.getItemId() == R.id.eng_ocr) {
            bullap = true;
            startActivity(new Intent(MainActivity.this, capture_open_image.class));
        } else if (item.getItemId() == R.id.action_settings) {
            bullap = true;
            startActivity(new Intent(MainActivity.this, EditProfile.class));
        } else if (item.getItemId() == R.id.emergency) {
            bullap = true;
            startActivity(new Intent(MainActivity.this, Emergency.class));
        } else if (item.getItemId() == R.id.prod_search) {
            bullap = true;
            startActivity(new Intent(MainActivity.this, objo_brief_desc.class
            ));
        }
        return super.onOptionsItemSelected(item);
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, final Call incomingcalll) {


            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Calls").child(firebaseUser.getUid()).child("Call details");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("from")) {

                        DatabaseReference ref2 = ref.child("from").child("uid");
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String phone_karne_wale_ka_id = dataSnapshot.getValue().toString();
                                uska_ido = phone_karne_wale_ka_id;

                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child(phone_karne_wale_ka_id).child("name");
                                ref2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String nameo = dataSnapshot.getValue().toString();
                                        /////////////alert ka dialog
//                                        mp = MediaPlayer.create(this, R.raw.sound_clip);
                                        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        vib.vibrate(500);
//                                        mp.start();

//                                        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//                                        if (alarmUri == null) {
//                                            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//                                        }
//                                        Ringtone ringtone = RingtoneManager.getRingtone(MainActivity.this, alarmUri);
//                                        ringtone.play();


                                        player = MediaPlayer.create(MainActivity.this, Settings.System.DEFAULT_RINGTONE_URI);
                                        player.start();

                                        //dialog open
                                        TextView uska_naam2;
                                        ImageView uska_img, end_call, pick_call;
                                        Button busy, later, i_ll_call_later;
                                        receive_him.setContentView(R.layout.receive_call);

                                        uska_naam2 = receive_him.findViewById(R.id.obj_name);
                                        uska_img = receive_him.findViewById(R.id.uska_img);
                                        end_call = receive_him.findViewById(R.id.hangup);
                                        pick_call = receive_him.findViewById(R.id.pick);

                                        busy = receive_him.findViewById(R.id.busy);
                                        later = receive_him.findViewById(R.id.call_me_later);
                                        i_ll_call_later = receive_him.findViewById(R.id.i_will_call);
                                        ;


                                        uska_naam2.setText(nameo);
                                        DatabaseReference rty = FirebaseDatabase.getInstance().getReference("Users").child(uska_ido).child("image_url");
                                        rty.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists())
                                                    uska_img2 = dataSnapshot.getValue().toString();
                                                try {
                                                    Glide.with(MainActivity.this).load(uska_img2).into(uska_img);
                                                } catch (Exception e) {

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        end_call.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                receive_him.dismiss();
                                                try {
                                                    call.hangup();

                                                } catch (Exception e) {

                                                }
                                                player.stop();

                                                DatabaseReference rto = FirebaseDatabase.getInstance().getReference("short_btns").child(uska_ido).child(firebaseUser.getUid());
                                                Value v = new Value("Call ended");
                                                rto.setValue(v);
                                            }
                                        });
                                        pick_call.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                call = incomingcalll;
                                                call.answer();
                                                call.addCallListener(new SinchCallListener());
                                                player.stop();
                                                receive_him.dismiss();
                                            }
                                        });

                                        busy.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                receive_him.dismiss();
                                                try {
                                                    call.hangup();

                                                } catch (Exception e) {

                                                }
                                                player.stop();
                                                DatabaseReference rto = FirebaseDatabase.getInstance().getReference("short_btns").child(uska_ido).child(firebaseUser.getUid());
                                                Value v = new Value("I'm Busy");
                                                rto.setValue(v);
                                            }
                                        });
                                        later.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                receive_him.dismiss();
                                                try {
                                                    call.hangup();

                                                } catch (Exception e) {

                                                }
                                                player.stop();
                                                DatabaseReference rto = FirebaseDatabase.getInstance().getReference("short_btns").child(uska_ido).child(firebaseUser.getUid());
                                                Value v = new Value("Call me Later");
                                                rto.setValue(v);
                                            }
                                        });
                                        i_ll_call_later.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                receive_him.dismiss();
                                                try {
                                                    call.hangup();

                                                } catch (Exception e) {

                                                }
                                                player.stop();
                                                DatabaseReference rto = FirebaseDatabase.getInstance().getReference("short_btns").child(uska_ido).child(firebaseUser.getUid());
                                                Value v = new Value("I'll Call later");
                                                rto.setValue(v);
                                            }
                                        });

                                        receive_him.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                        try {
                                            //show dialog
                                            receive_him.show();
                                        } catch (Exception e) {

                                        }
//                                        if(!receive_him.isShowing())
//                                        {
//
//                                        }else if(receive_him.isShowing()){
//                                            receive_him.dismiss();
//                                            receive_him.show();
//                                        }

//                                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
//                                        alertDialog.setTitle("Incoming");
//                                        alertDialog.setMessage("call mail id : " + nameo);
//                                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Reject", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.dismiss();
//                                                try{
//                                                    call.hangup();
//
//                                                }catch (Exception e){
//
//                                                }
//                                                player.stop();
//                                            }
//                                        });
//
//                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Pick", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                call = incomingcalll;
//                                                call.answer();
//                                                call.addCallListener(new SinchCallListener());
//                                                player.stop();
//                                            }
//                                        });
//                                        alertDialog.show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "busy hai", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!bullap)
            setStatus("offline");
        Log.e("stop called", "stopp");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!bullap)
            setStatus("offline");
        Log.e("destroy called", "destroy");
    }

    @Override
    protected void onStart() {
        super.onStart();

        bullap = false;
        setStatus("online");

        no_of_notifications = 0;
        //no_of_notts.setBackgroundColor(Color.WHITE);
        no_of_notts.setText("");
        no_of_notts.setVisibility(View.GONE);
    }

    public void setStatus(String status) {
        DatabaseReference df = FirebaseDatabase.getInstance().getReference("User_status").child(firebaseUser.getUid());
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String res = currentDate + " " + currentTime;

        UserStatus ui = new UserStatus(res, status, firebaseUser.getUid());

        df.setValue(ui);
    }

    public void callUser(User user, boolean busy, boolean blocked) {
        if (call == null) {
            // boolean busy2 ;
            call = sinchClient.getCallClient().callUser(user.getUserid());

            call.addCallListener(new SinchCallListener());


            if (busy == true) {
                //Toast.makeText(this, "busy h : " + user.getName(), Toast.LENGTH_SHORT).show();
                //call.hangup();
                AlertDialog alertDialogCall = new AlertDialog.Builder(MainActivity.this).create();
                alertDialogCall.setTitle("Busy..");
                alertDialogCall.setMessage(user.getName() + " is Busy");
                alertDialogCall.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //call.hangup();
                    }
                });
                alertDialogCall.setCanceledOnTouchOutside(false);
                alertDialogCall.show();

                //add to his notifications that I called him
                DatabaseReference julia2 = FirebaseDatabase.getInstance().getReference("Notifications")
                        .child(user.getUserid()).child("busy").child(firebaseUser.getUid());

                //he_id = dataSnapshot.getValue().toString();
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String res = currentDate + " " + currentTime;

                Notts n = new Notts(firebaseUser.getUid(), false, res, "Busy user");

                julia2.setValue(n)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Not added in his notts", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                openCallerDialog(call, user);
            }
        }
    }

    public void viewUser(User user) {
        String my_id = firebaseUser.getUid();
        String uska_id = user.getUserid();

        Intent i = new Intent(MainActivity.this, chat_history.class);
        i.putExtra("My", my_id);
        i.putExtra("UskaId", uska_id);
        i.putExtra("UskaNaam", user.getName());
        bullap = true;

        startActivity(i);
    }


    public void viewProfile(User user) {
        String my_id = firebaseUser.getUid();
        String uska_id = user.getUserid();

        Intent i = new Intent(MainActivity.this, ViewProfile.class);
        i.putExtra("UskaId", uska_id);
        i.putExtra("Activity", "Main");
        bullap = true;

        startActivity(i);
    }


    private void openCallerDialog(final Call call, final User user) {


        //dialog open
        TextView uska_naam2, his_msg;
        ImageView uska_img, end_call;
        Button okay;
        //Button busy ,later ,i_ll_call_later;
        call_him.setContentView(R.layout.call_);

        uska_naam2 = call_him.findViewById(R.id.obj_name);
        his_msg = call_him.findViewById(R.id.uska_msg);
        okay = call_him.findViewById(R.id.ok);


        uska_img = call_him.findViewById(R.id.uska_img);
        end_call = call_him.findViewById(R.id.end_call);


        uska_naam2.setText(user.getName());
        DatabaseReference rty = FirebaseDatabase.getInstance().getReference("Users").child(user.getUserid()).child("image_url");
        rty.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    uska_img2 = dataSnapshot.getValue().toString();
                Glide.with(MainActivity.this).load(uska_img2).into(uska_img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference rto = FirebaseDatabase.getInstance().getReference("short_btns").child(firebaseUser.getUid()).child(user.getUserid()).child("value");
        rto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    his_msg.setText(dataSnapshot.getValue().toString());
                    okay.setVisibility(View.VISIBLE);
                    end_call.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_him.dismiss();
                try {
                    call.hangup();

                } catch (Exception e) {

                }
                //remove call ended frm dB
                DatabaseReference refo = FirebaseDatabase.getInstance().getReference("short_btns").child(firebaseUser.getUid()).child(user.getUserid());
                refo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            dataSnapshot.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                DatabaseReference refo2 = FirebaseDatabase.getInstance().getReference("short_btns").child(user.getUserid()).child(firebaseUser.getUid());
                refo2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            dataSnapshot.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        end_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_him.dismiss();
                try {
                    call.hangup();

                } catch (Exception e) {

                }
            }
        });

        call_him.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        call_him.show();


//        AlertDialog alertDialogCall = new AlertDialog.Builder(MainActivity.this).create();
//        alertDialogCall.setTitle("Alert");
//        alertDialogCall.setMessage("Calling to :"  +user.getName());
//        alertDialogCall.setButton(AlertDialog.BUTTON_NEUTRAL, "Hang Up", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                call.hangup();
//            }
//        });
//        alertDialogCall.show();
    }


    ////////////////location code

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }

    /**
     * If connected get lat and long
     *
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            //Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
            DatabaseReference refto = FirebaseDatabase.getInstance().getReference("Location").child(firebaseUser.getUid());
            Locatioi l = new Locatioi(String.valueOf(currentLongitude) ,String.valueOf(currentLatitude) ,firebaseUser.getUid());
            refto.setValue(l).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Can't update location", Toast.LENGTH_SHORT).show();
                    }else{
                        // Create a Uri from an intent string. Use the result to create an Intent.
                        Uri gmmIntentUri = Uri.parse("geo:"+currentLatitude+","+currentLongitude);

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
                        mapIntent.setPackage("com.google.android.apps.maps");

// Attempt to start an activity that can handle the Intent
                        //startActivity(mapIntent);
                    }
                }
            });
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        DatabaseReference refto = FirebaseDatabase.getInstance().getReference("Location").child(firebaseUser.getUid());
        Locatioi l = new Locatioi(String.valueOf(currentLongitude) ,String.valueOf(currentLatitude) ,firebaseUser.getUid());
        refto.setValue(l).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Can't update location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
