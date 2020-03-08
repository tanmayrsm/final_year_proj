package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.beproj3.Adapters.AllChatsAdapter;
import com.example.beproj3.Models.Chats;
import com.example.beproj3.Models.SmartSuggestionList;
import com.example.beproj3.Models.User;
import com.example.beproj3.Models.UserStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseSmartReply;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestion;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CallGoingActivity extends AppCompatActivity implements RecognitionListener{
    Button end ,view_conv_text ,view_lang ,b11,b21,b31;
    Call call;
    User user;
    EditText chat_edittext;
    ImageButton send;
    LinearLayout chat_layout;
    Switch s1;

    RecyclerView rv;

    Button hide_chato;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    TextView connected_name ,first_txtvu;

    SinchClient sinchClient;

    private static final int REQUEST_RECORD_PERMISSION = 100;
    private int maxLinesInput = 10;
    private TextView returnedText ,result;
    public ToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "CallGoingActivity";
    boolean listening = false;
    boolean convert_kary_real_time_text = true;

    private final List<Chats> chatlist = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private AllChatsAdapter chatsAdapter;
    public String uskaId,uska_name,mera,mera_lang;
    public DatabaseReference refi;
    public String getkey_value = null;

    String phone_karne_wale_ka_id ,randomString ,start_time ,end_time;

    public String my_lang_name ,my_lang_code ,fb_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_going);

        end = findViewById(R.id.end_call_btn);
        connected_name = findViewById(R.id.conn_name);
        first_txtvu = findViewById(R.id.ram);

        b11 = findViewById(R.id.b1);
        b21 = findViewById(R.id.b2);
        b31 = findViewById(R.id.b3);

        chat_edittext = findViewById(R.id.chat_text);
        send = findViewById(R.id.btn_send);
        hide_chato = findViewById(R.id.view_chat_btn);

        chat_layout = findViewById(R.id.ttl);

        //view_lang = findViewById(R.id.change_lang);


        //chota history window
        rv = findViewById(R.id.recyclerview3);


        result = findViewById(R.id.hi);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        view_conv_text = findViewById(R.id.view_text_btn);

        returnedText = (TextView) findViewById(R.id.textView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        //view_lang = findViewById(R.id.change_lang);
        s1 = findViewById(R.id.switch1);
        randomString = RandomString();

        DatabaseReference langName = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("lang");
        langName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                my_lang_name = dataSnapshot.getValue().toString();
                if(my_lang_name == "English"){}
                    //view_lang.setVisibility(View.GONE);
                else{
                    //view_lang.setText("View in "+my_lang_name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //get his native lang code from dB
        DatabaseReference langcode = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("my_lang");
        langcode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                my_lang_code = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //get his native lang fb code from dB
        DatabaseReference fbcode = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("fb_val");
        fbcode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fb_code = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(this, "My lang name:"+my_lang_name, Toast.LENGTH_SHORT).show();

        //get user name and set 'connected to' :on screen
        DatabaseReference usr = FirebaseDatabase.getInstance().getReference()
                .child("Calls").child(firebaseUser.getUid()).child("Call details");
        usr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("from")){
                    /////from ka naam
                    DatabaseReference ref2 = usr.child("to").child("uid");
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            phone_karne_wale_ka_id = dataSnapshot.getValue().toString();

                            //start putting chats in dB for history

                            DatabaseReference hist = FirebaseDatabase.getInstance().getReference("prev_Calls");
                            DatabaseReference hist2 = FirebaseDatabase.getInstance().getReference("prev_Calls");

                            HashMap<String, Object> result = new HashMap<>();
                            result.put("start", start_time);
                            result.put("end","");
                            result.put("uid",phone_karne_wale_ka_id);

                            HashMap<String, Object> result2 = new HashMap<>();
                            result2.put("start", start_time);
                            result2.put("end","");
                            result2.put("uid",firebaseUser.getUid());

                            //Log.e("phonewlring:",phone_karne_wale_ka_id+" -->"+randomString);
                            hist.child(firebaseUser.getUid()).child(start_time).updateChildren(result);
                            hist2.child(phone_karne_wale_ka_id).child(start_time).updateChildren(result2);


                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(phone_karne_wale_ka_id).child("name");
                            ref2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String nameo = dataSnapshot.getValue().toString();
                                    /////////////set conn name
                                    connected_name.setText(nameo);
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
                }
                else if(dataSnapshot.hasChild("to")){
                    /////to ka naam
                    DatabaseReference ref2 = usr.child("to").child("uid");

                    result.setVisibility(View.INVISIBLE);

                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            phone_karne_wale_ka_id = dataSnapshot.getValue().toString();

                            //start putting chats in dB for history

                            DatabaseReference hist = FirebaseDatabase.getInstance().getReference("prev_Calls");
                            DatabaseReference hist2 = FirebaseDatabase.getInstance().getReference("prev_Calls");

                            HashMap<String, Object> result = new HashMap<>();
                            result.put("start", start_time);
                            result.put("end","");
                            result.put("uid",phone_karne_wale_ka_id);

                            HashMap<String, Object> result2 = new HashMap<>();
                            result2.put("start", start_time);
                            result2.put("end","");
                            result2.put("uid",firebaseUser.getUid());

                            //Log.e("phonewlring:",phone_karne_wale_ka_id+" -->"+randomString);
                            hist.child(firebaseUser.getUid()).child(start_time).updateChildren(result);
                            hist2.child(phone_karne_wale_ka_id).child(start_time).updateChildren(result2);



                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(phone_karne_wale_ka_id).child("name");
                            ref2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String nameo = dataSnapshot.getValue().toString();
                                    /////////////text view name set
                                    connected_name.setText(nameo);
                                    /////////////
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

                    /////
                }
                else{
                    Toast.makeText(CallGoingActivity.this, "call error", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CallGoingActivity.this ,MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //view_lang.setText("View in "+my_lang_name);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jugni2(chat_edittext.getText().toString() ,firebaseUser.getUid());
                chat_edittext.setText(" ");
            }
        });

        b11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jugni2(b11.getText().toString() ,firebaseUser.getUid());
                b11.setVisibility(View.GONE);
                b21.setVisibility(View.GONE);
                b31.setVisibility(View.GONE);
            }
        });

        b21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jugni2(b21.getText().toString() ,firebaseUser.getUid());
                b11.setVisibility(View.GONE);
                b21.setVisibility(View.GONE);
                b31.setVisibility(View.GONE);
            }
        });

        b31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jugni2(b31.getText().toString() ,firebaseUser.getUid());
                b11.setVisibility(View.GONE);
                b21.setVisibility(View.GONE);
                b31.setVisibility(View.GONE);
            }
        });

        view_conv_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result.getVisibility() == View.INVISIBLE){
                    result.setVisibility(View.VISIBLE);
                    view_conv_text.setText("Hide converted text");
                }
                else if(result.getVisibility() == View.VISIBLE){
                    result.setVisibility(View.INVISIBLE);
                    view_conv_text.setText("View converted text");
                }
            }
        });

        hide_chato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chat_layout.getVisibility() == View.INVISIBLE){
                    chat_layout.setVisibility(View.VISIBLE);
                    hide_chato.setText("Hide chat");
                }
                else if(chat_layout.getVisibility() == View.VISIBLE){
                    chat_layout.setVisibility(View.INVISIBLE);
                    hide_chato.setText("Do chat");
                }
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                end_time = currentDate + " " + currentTime;


                //set end time call
                DatabaseReference hist = FirebaseDatabase.getInstance().getReference("prev_Calls");
                DatabaseReference hist2 = FirebaseDatabase.getInstance().getReference("prev_Calls");

                HashMap<String, Object> result = new HashMap<>();
                result.put("start", start_time);
                result.put("end",end_time);


                //Log.e("phonewlring:",phone_karne_wale_ka_id+" -->"+randomString);
                hist.child(firebaseUser.getUid()).child(start_time).updateChildren(result);
                hist2.child(phone_karne_wale_ka_id).child(start_time).updateChildren(result);

                Toast.makeText(CallGoingActivity.this, "Call ended", Toast.LENGTH_SHORT).show();
                if (call != null)
                    call.hangup();
                else if (call == null){
                    Intent intent = new Intent(CallGoingActivity.this ,MainActivity.class);
                    startActivity(intent);

                    //set their status to free
                    DatabaseReference busy = FirebaseDatabase.getInstance().getReference("Busy").child(firebaseUser.getUid());
                    busy.removeValue();

                    //TODO
                    // add him to call history
                    DatabaseReference busy2 = FirebaseDatabase.getInstance().getReference("Busy").child(uskaId);
                    busy2.removeValue();


                    //idhar problem aata call end pe
                        call = sinchClient.getCallClient().callUser(user.getUserid());
                        call.addCallListener(new CallGoingActivity.SinchCallListener());
                        call.hangup();


                }

                Intent intent = new Intent(CallGoingActivity.this ,MainActivity.class);
                startActivity(intent);
            }
        });

        //switch button
        s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    s1.setText("English");

                }else{
                    DatabaseReference langName = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("lang");
                    langName.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            my_lang_name = dataSnapshot.getValue().toString();
                            if(my_lang_name == "English"){}

                            //view_lang.setVisibility(View.GONE);
                            else{
                                //view_lang.setText("View in "+my_lang_name)
                                s1.setText(my_lang_name);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                }
        });


        ///toggle listener
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    listening = true;
                    convert_kary_real_time_text = false;
                    if(s1.isChecked()){
                        //on native mode....do his transcribtion in native..real time in native...cjats in native
                        DatabaseReference langcode = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("my_lang");
                        langcode.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                my_lang_code = dataSnapshot.getValue().toString();
                                start(my_lang_code);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                    else if(s1.getVisibility() == View.GONE || !s1.isChecked() ){       //if a english user or one with disabled native mode
                        //in english mode
                        start("en_GB");
                    }
                    //start("hi");
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    ActivityCompat.requestPermissions
                            (CallGoingActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                } else {
                    listening = false;
                    convert_kary_real_time_text = true;
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    turnOf();
                }
            }
        });

        returnedText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                ////////chng text and add to fb

                DatabaseReference usr2 = FirebaseDatabase.getInstance().getReference()
                        .child("Calls").child(firebaseUser.getUid()).child("Call details");

                usr2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("from")){
                            /////from ka uid
                            DatabaseReference ref3 = usr.child("from").child("uid");
                            ref3.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    phone_karne_wale_ka_id = dataSnapshot.getValue().toString();

                                    //phone kisko kiya

                                    DatabaseReference kiso_kiya = FirebaseDatabase.getInstance().getReference()
                                            .child("Calls").child(firebaseUser.getUid()).child("Call details").child("to").child("uid");
                                    kiso_kiya.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            String kisko_kiya = dataSnapshot.getValue().toString();
                                            final String so = s.toString();
                                            if(!so.equals(" ") && !so.equals("Say something") && !so.equals("\n")) {
                                                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                                String res = currentDate + " " + currentTime;

                                                DatabaseReference hammand = FirebaseDatabase.getInstance().getReference()
                                                        .child("Chats");

                                                String kisne_bola = firebaseUser.getUid().toString();

                                                Chats chat = new Chats(so, res ,kisne_bola ,"voice","");

                                                hammand.child(firebaseUser.getUid()).setValue(chat)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (!task.isSuccessful()) {
                                                                    Toast.makeText(CallGoingActivity.this, "Speech gya nhi dB me", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                hammand.child(phone_karne_wale_ka_id).setValue(chat)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (!task.isSuccessful()) {
                                                                    Toast.makeText(CallGoingActivity.this, "Speech gya nhi dB me", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                ////
                                                DatabaseReference hammand2 = FirebaseDatabase.getInstance().getReference()
                                                        .child("Chats");

                                                Chats chat2 = new Chats(so, res ,kisne_bola ,"voice","");

                                                hammand2.child(kisko_kiya).setValue(chat2)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (!task.isSuccessful()) {
                                                                    Toast.makeText(CallGoingActivity.this, "Speech gya nhi dB me", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                hammand2.child(kisko_kiya).setValue(chat2)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (!task.isSuccessful()) {
                                                                    Toast.makeText(CallGoingActivity.this, "Speech gya nhi dB me", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
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

                            /////
                        }
                        else if(dataSnapshot.hasChild("to")){
                            /////to ka naam
                            DatabaseReference ref3 = usr.child("to").child("uid");
                            ref3.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    phone_karne_wale_ka_id = dataSnapshot.getValue().toString();

                                    final String so = s.toString();
                                    if(!so.equals(" ") && !so.equals("Say something") && !so.equals("\n")) {
                                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                        String res = currentDate + " " + currentTime;

                                        DatabaseReference hammand = FirebaseDatabase.getInstance().getReference()
                                                .child("Chats");

                                        String kisne_bola;
                                        kisne_bola = firebaseUser.getUid();

                                        Chats chat = new Chats(so, res ,kisne_bola,"voice","");

                                        hammand.child(firebaseUser.getUid()).setValue(chat)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (!task.isSuccessful()) {
                                                            Toast.makeText(CallGoingActivity.this, "Speech gya nhi dB me", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                        DatabaseReference hammand2 = FirebaseDatabase.getInstance().getReference()
                                                .child("Chats");
                                        hammand2.child(phone_karne_wale_ka_id).setValue(chat)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (!task.isSuccessful()) {
                                                            Toast.makeText(CallGoingActivity.this, "Speech gya nhi dB me", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            /////
                        }
                        else{
                            Toast.makeText(CallGoingActivity.this, "call error", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CallGoingActivity.this ,MainActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                ///////added
            }
            @Override
            public void afterTextChanged(Editable s) {
//                if(s1.isChecked()){
//                    String fb_code;
//                    DatabaseReference fbcode = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("fb_code");
//                    fbcode.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            my_lang_code = dataSnapshot.getValue().toString();
//                            start(my_lang_code);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                    identifyLanguage2("en",);
//                }

            }
        });

        DatabaseReference Chato = FirebaseDatabase.getInstance().getReference("Chats");
        Chato.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(firebaseUser.getUid())){{
                    DatabaseReference chats = Chato.child(firebaseUser.getUid());

                    DatabaseReference chudail = chats.child("chat");

                    String zid = result.getText().toString();

                    chudail.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(!s1.isChecked())
                            {
                                Toast.makeText(CallGoingActivity.this, "aaya re else if me", Toast.LENGTH_SHORT).show();
                                result.setText(dataSnapshot.getValue().toString());
                                identifyLanguage("en");
                            }

                            else if(s1.isChecked())
                            {
                                Toast.makeText(CallGoingActivity.this, "aaya re else if me", Toast.LENGTH_SHORT).show();
                                result.setText(dataSnapshot.getValue().toString());
                                DatabaseReference fbcode = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("fb_val");
                                fbcode.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        fb_code = dataSnapshot.getValue().toString();
                                        identifyLanguage(fb_code);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(CallGoingActivity.this, "Cant display from firebase", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //// recycler view
        chatsAdapter = new AllChatsAdapter(CallGoingActivity.this,chatlist);
        linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(chatsAdapter);


    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallProgressing(Call call) {
            Toast.makeText(CallGoingActivity.this, "Call is ringing", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallEstablished(Call call) {
            Toast.makeText(CallGoingActivity.this, "Call established", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCallEnded(Call endedCall) {
            Toast.makeText(CallGoingActivity.this, "Call ended", Toast.LENGTH_SHORT).show();
            call = null;
            endedCall.hangup();

        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }

    public void start(String code){
        Toast.makeText(this, "Lang ayi h :" + code, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
//                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //get his native lang from dB
        DatabaseReference langName = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("lang");
        langName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                my_lang_name = dataSnapshot.getValue().toString();
                if(my_lang_name == "English"){}

                //view_lang.setVisibility(View.GONE);
                else{
                    //view_lang.setText("View in "+my_lang_name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        //get his native lang code from dB
        DatabaseReference langcode = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("my_lang");
        langcode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                my_lang_code = dataSnapshot.getValue().toString();
                Toast.makeText(CallGoingActivity.this, "ALLLLL IN :"+my_lang_code, Toast.LENGTH_SHORT).show();
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE ,my_lang_code );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //get his native lang fb code from dB
        DatabaseReference fbcode = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("fb_val");
        fbcode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fb_code = dataSnapshot.getValue().toString();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxLinesInput);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
//        if(my_lang_name == "English"){
//            Toast.makeText(this, "Say in english", Toast.LENGTH_SHORT).show();
//            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");
//        }
//        else if(my_lang_name != "English"){
//            Toast.makeText(this, "Say in "+my_lang_name, Toast.LENGTH_SHORT).show();
//            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, my_lang_code);
//        }
//        else{
//            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");
//        }

//        marathi - mr_IN
//        hindi - hi_IN
//        eng(UK) - en_GB
//        bn_IN [Bengali (India)]
//        ta_IN [Tamil (India)]
//        te_IN [Telugu (India)]

//        es_ [Spanish]
    }
    public void setStatus(String status){
        DatabaseReference df = FirebaseDatabase.getInstance().getReference("User_status").child(firebaseUser.getUid());
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String res = currentDate + " " + currentTime;

        UserStatus ui = new UserStatus(res ,status ,firebaseUser.getUid());

        df.setValue(ui);
    }

    protected void onStart() {
        super.onStart();

        setStatus("online");


        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        start_time = currentDate + " " + currentTime;

        DatabaseReference usr = FirebaseDatabase.getInstance().getReference()
                .child("Calls").child(firebaseUser.getUid()).child("Call details");
        //discard or show ...view in native language
        //view_lang = findViewById(R.id.change_lang);
        s1 = findViewById(R.id.switch1);

        DatabaseReference langName = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("lang");
        langName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                my_lang_name = dataSnapshot.getValue().toString();
                if(my_lang_name.equals("English"))
                    s1.setVisibility(View.GONE);
                else{
                    s1.setText(my_lang_name);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //get his native lang code from dB
        DatabaseReference langcode = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("my_lang");
        langcode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                my_lang_code = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //get his native lang fb code from dB
        DatabaseReference fbcode = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("fb_val");
        fbcode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fb_code = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //get user name  and uska id
        usr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("from")){
                    /////from ka naam
                    DatabaseReference ref2 = usr.child("to").child("uid");
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            phone_karne_wale_ka_id = dataSnapshot.getValue().toString();
                            uskaId = phone_karne_wale_ka_id;

                            //check if smart reply exists
                            DatabaseReference chk_exists = FirebaseDatabase.getInstance().getReference()
                                    .child("smart_reply").child(firebaseUser.getUid());
                            chk_exists.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        ///get reply in boxes

                                        DatabaseReference get_reply = FirebaseDatabase.getInstance().getReference()
                                                .child("smart_reply").child(firebaseUser.getUid()).child(uskaId);
                                        get_reply.child("msg1").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                b11 = findViewById(R.id.b1);
                                                if(dataSnapshot.exists()) {
                                                    if (dataSnapshot.getValue().toString().equals("")) {
                                                        b11.setVisibility(View.GONE);
                                                    } else {
                                                        b11.setVisibility(View.VISIBLE);
                                                        b11.setText(dataSnapshot.getValue().toString());
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        get_reply.child("msg2").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                b21 = findViewById(R.id.b2);
                                                if(dataSnapshot.exists()) {
                                                if(dataSnapshot.getValue().toString().equals("")){
                                                    b21.setVisibility(View.GONE);
                                                }
                                                else{
                                                    b21.setVisibility(View.VISIBLE);
                                                    b21.setText(dataSnapshot.getValue().toString());
                                                }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        get_reply.child("msg3").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                b31 = findViewById(R.id.b3);
                                                if(dataSnapshot.exists()) {
                                                if(dataSnapshot.getValue().toString().equals("")){
                                                    b31.setVisibility(View.GONE);
                                                }
                                                else{
                                                    b31.setVisibility(View.VISIBLE);
                                                    b31.setText(dataSnapshot.getValue().toString());
                                                }}
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

                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(phone_karne_wale_ka_id).child("name");
                            ref2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String nameo = dataSnapshot.getValue().toString();
                                    uska_name = nameo;

                                    ////change recycler view hist
                                    mera = firebaseUser.getUid();

                                    refi = FirebaseDatabase.getInstance().getReference("Call_history").child(mera).child(uskaId);
                                    refi.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                            Chats chat = dataSnapshot.getValue(Chats.class);
                                            chatlist.add(chat);
                                            chatsAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else if(dataSnapshot.hasChild("to")){
                    DatabaseReference ref2 = usr.child("to").child("uid");
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            phone_karne_wale_ka_id = dataSnapshot.getValue().toString();

                            uskaId = phone_karne_wale_ka_id;

                            //check if smart reply exists
                            DatabaseReference chk_exists = FirebaseDatabase.getInstance().getReference()
                                    .child("smart_reply").child(firebaseUser.getUid());
                            chk_exists.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        ///get reply in boxes

                                        DatabaseReference get_reply = FirebaseDatabase.getInstance().getReference()
                                                .child("smart_reply").child(firebaseUser.getUid()).child(uskaId);
                                        get_reply.child("msg1").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                b11 = findViewById(R.id.b1);
                                                if(dataSnapshot.getValue().toString().equals("")){
                                                    b11.setVisibility(View.GONE);
                                                }
                                                else{
                                                    b11.setVisibility(View.VISIBLE);
                                                    b11.setText(dataSnapshot.getValue().toString());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        get_reply.child("msg2").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                b21 = findViewById(R.id.b2);
                                                if(dataSnapshot.getValue().toString().equals("")){
                                                    b21.setVisibility(View.GONE);
                                                }
                                                else{
                                                    b21.setVisibility(View.VISIBLE);
                                                    b21.setText(dataSnapshot.getValue().toString());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        get_reply.child("msg3").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                b31 = findViewById(R.id.b3);
                                                if(dataSnapshot.getValue().toString().equals("")){
                                                    b31.setVisibility(View.GONE);
                                                }
                                                else{
                                                    b31.setVisibility(View.VISIBLE);
                                                    b31.setText(dataSnapshot.getValue().toString());
                                                }
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

                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(phone_karne_wale_ka_id).child("name");
                            ref2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String nameo = dataSnapshot.getValue().toString();
                                    uska_name = nameo;

                                    //change recycler view
                                    mera = firebaseUser.getUid();

                                    refi = FirebaseDatabase.getInstance().getReference("Call_history").child(mera).child(uskaId);
                                    refi.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                            Chats chat = dataSnapshot.getValue(Chats.class);
                                            chatlist.add(chat);
                                            chatsAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    /////
                }
                else{
                    Toast.makeText(CallGoingActivity.this, "call error", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CallGoingActivity.this ,MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //set their status to busy
        DatabaseReference busy = FirebaseDatabase.getInstance().getReference("Busy").child(firebaseUser.getUid());

        HashMap<String, Object> result = new HashMap<>();
        result.put(firebaseUser.getUid(), "busy");
        busy.push().setValue(result);

    }

    public void turnOf(){
        speech.stopListening();
        speech.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(CallGoingActivity.this, "start talk...", Toast
                            .LENGTH_SHORT).show();
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(CallGoingActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (speech != null) {
//            speech.destroy();
//            Log.i(LOG_TAG, "destroy");
//        }
    }


    public void onReadyForSpeech(Bundle bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
        if(!listening){
            turnOf();
        }
    }
    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.i(LOG_TAG, "onBufferReceived: " + bytes);

    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");

        //Toast.makeText(this, "End of speech", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        returnedText.setText(errorMessage);
        speech.startListening(recognizerIntent);

    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text = result + "\n";
        //returnedText.setText(text);
        speech.startListening(recognizerIntent);

        //if(text!=" ")
        //    Log.e(LOG_TAG, "onResults="+text+ "#############################################################");

        Toast.makeText(this, "Ye kesa hai twice", Toast.LENGTH_SHORT).show();
        jugni(returnedText.getText().toString());



        //Log.e("Toggle at:",String.valueOf(toggleButton.isChecked()) + " at "+ firebaseUser.getUid());

        //String who_said = "";
        show_smart(returnedText.getText().toString(),firebaseUser.getUid());

    }

    private void show_smart(String message, String uid) {

        DatabaseReference usr = FirebaseDatabase.getInstance().getReference()
                .child("Calls").child(firebaseUser.getUid()).child("Call details");

        //get user name  and uska id
        usr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("from")){
                    /////from ka naam
                    DatabaseReference ref2 = usr.child("to").child("uid");
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            phone_karne_wale_ka_id = dataSnapshot.getValue().toString();
                            uskaId = phone_karne_wale_ka_id;

                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(phone_karne_wale_ka_id).child("name");


                            ref2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String nameo = dataSnapshot.getValue().toString();
                                    uska_name = nameo;

                                    ////change recycler view hist
                                    mera = firebaseUser.getUid();
                                    Log.e("show","smart reply");
                                    Log.e("msg",message);


                                    ArrayList<FirebaseTextMessage> conversation = new ArrayList<FirebaseTextMessage>();
                                    conversation.add(FirebaseTextMessage.createForRemoteUser(
                                            message, System.currentTimeMillis() ,"Dadi"));

                                    FirebaseSmartReply smartReply = FirebaseNaturalLanguage.getInstance().getSmartReply();
                                    smartReply.suggestReplies(conversation)
                                            .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                                                @Override
                                                public void onSuccess(SmartReplySuggestionResult result) {
                                                    if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                                                        // The conversation's language isn't supported, so the
                                                        // the result doesn't contain any suggestions.
                                                        // Toast.makeText(CallGoingActivity.this, "Na hua reply 1", Toast.LENGTH_SHORT).show();
                                                        Log.e("Lang not support","NO");
                                                        DatabaseReference y = FirebaseDatabase.getInstance().getReference().child("smart_reply").
                                                                child(uskaId).child(mera);

                                                        SmartSuggestionList sop = new SmartSuggestionList("","","");
                                                        DatabaseReference hammand = FirebaseDatabase.getInstance().getReference().child("smart_reply").
                                                                child(uskaId);
                                                        hammand.child(mera).setValue(sop)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (!task.isSuccessful()) {
                                                                            Toast.makeText(CallGoingActivity.this, "Smart reply nhi gya re", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                                                        // Task completed successfully
                                                        // ...
                                                        Log.e("Text in box:",returnedText.getText().toString());
                                                        String bam = result.getSuggestions().toString();
                                                        Log.e("Bam:",bam);
                                                        // [SmartReplySuggestion{text=How are you?, confidence=0.18774909},
                                                        // SmartReplySuggestion{text=😊, confidence=0.0033598393},
                                                        // SmartReplySuggestion{text=😟, confidence=-0.05351025}]

                                                        // SmartReplySuggestion suggestion1  = result.getSuggestions()[0].getText();
                                                        String[] jogi = new String[3];
                                                        int p = 0;
                                                        //b11.setText(bam);
                                                        for (SmartReplySuggestion suggestion : result.getSuggestions()) {
                                                            Log.e("Reply:",suggestion.getText());
                                                            jogi[p++] = suggestion.getText();
                                                        }

                                                        DatabaseReference y = FirebaseDatabase.getInstance().getReference().child("smart_reply").
                                                                child(uskaId).child(mera);

                                                        SmartSuggestionList sop = new SmartSuggestionList(jogi[0],jogi[1],jogi[2]);
                                                        DatabaseReference hammand = FirebaseDatabase.getInstance().getReference().child("smart_reply").
                                                                child(uskaId);
                                                        hammand.child(mera).setValue(sop)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (!task.isSuccessful()) {
                                                                            Toast.makeText(CallGoingActivity.this, "Smart reply nhi gya re", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Task failed with an exception
                                                    // ...
                                                    Toast.makeText(CallGoingActivity.this, "Na hua reply", Toast.LENGTH_SHORT).show();
                                                    Log.e("Failure","smart reply");
                                                }
                                            });
//                                    DatabaseReference refi;
//
//                                    refi = FirebaseDatabase.getInstance().getReference("Call_history").child(mera).child(uskaId);

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
                }
                else if(dataSnapshot.hasChild("to")){
                    DatabaseReference ref2 = usr.child("to").child("uid");
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            phone_karne_wale_ka_id = dataSnapshot.getValue().toString();

                            uskaId = phone_karne_wale_ka_id;

                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(phone_karne_wale_ka_id).child("name");
                            ref2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String nameo = dataSnapshot.getValue().toString();
                                    uska_name = nameo;

                                    //change recycler view
                                    mera = firebaseUser.getUid();
                                    Log.e("show","smart reply");
                                    Log.e("msg",message);


                                    ArrayList<FirebaseTextMessage> conversation = new ArrayList<FirebaseTextMessage>();
                                    conversation.add(FirebaseTextMessage.createForRemoteUser(
                                            message, System.currentTimeMillis() ,"Dadi"));

                                    FirebaseSmartReply smartReply = FirebaseNaturalLanguage.getInstance().getSmartReply();
                                    smartReply.suggestReplies(conversation)
                                            .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                                                @Override
                                                public void onSuccess(SmartReplySuggestionResult result) {
                                                    if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                                                        // The conversation's language isn't supported, so the
                                                        // the result doesn't contain any suggestions.
                                                        // Toast.makeText(CallGoingActivity.this, "Na hua reply 1", Toast.LENGTH_SHORT).show();
                                                        Log.e("Lang not support","NO");
                                                    } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                                                        // Task completed successfully
                                                        // ...
                                                        Log.e("Text in box:",returnedText.getText().toString());
                                                        String bam = result.getSuggestions().toString();
                                                        Log.e("Bam:",bam);
                                                        // [SmartReplySuggestion{text=How are you?, confidence=0.18774909},
                                                        // SmartReplySuggestion{text=😊, confidence=0.0033598393},
                                                        // SmartReplySuggestion{text=😟, confidence=-0.05351025}]

                                                        // SmartReplySuggestion suggestion1  = result.getSuggestions()[0].getText();
                                                        String[] jogi = new String[3];
                                                        int p = 0;
                                                        //b11.setText(bam);
                                                        for (SmartReplySuggestion suggestion : result.getSuggestions()) {
                                                            Log.e("Reply:",suggestion.getText());
                                                            jogi[p++] = suggestion.getText();
                                                        }

                                                        DatabaseReference y = FirebaseDatabase.getInstance().getReference().child("smart_reply").
                                                                child(uskaId).child(mera);

                                                        SmartSuggestionList sop = new SmartSuggestionList(jogi[0],jogi[1],jogi[2]);
                                                        DatabaseReference hammand = FirebaseDatabase.getInstance().getReference().child("smart_reply").
                                                                child(uskaId);
                                                        hammand.child(mera).setValue(sop)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (!task.isSuccessful()) {
                                                                            Toast.makeText(CallGoingActivity.this, "Smart reply nhi gya re", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Task failed with an exception
                                                    // ...
                                                    Toast.makeText(CallGoingActivity.this, "Na hua reply", Toast.LENGTH_SHORT).show();
                                                    Log.e("Failure","smart reply");
                                                }
                                            });
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

                    /////
                }
                else{
                    Toast.makeText(CallGoingActivity.this, "call error", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CallGoingActivity.this ,MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        Log.e("Message",message);
        Log.e("Uid",uid);
        Log.e("Mera id",firebaseUser.getUid());



    }

    private void jugni(String man) {

        String yermark = man;
        Log.e(LOG_TAG,"final:"+yermark+" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

        if(yermark!=" ") {

            DatabaseReference usr3 = FirebaseDatabase.getInstance().getReference()
                    .child("Calls").child(firebaseUser.getUid()).child("Call details");

            usr3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("from")) {
                        /////from ka uid
                        DatabaseReference ref3 = usr3.child("from").child("uid");
                        ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                phone_karne_wale_ka_id = dataSnapshot.getValue().toString();

                                //phone kisko kiya
                                DatabaseReference kiso_kiya = FirebaseDatabase.getInstance().getReference()
                                        .child("Calls").child(firebaseUser.getUid()).child("Call details").child("to").child("uid");
                                kiso_kiya.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String kisko_kiya = dataSnapshot.getValue().toString();

                                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chats").child(firebaseUser.getUid());

                                        dr.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                DatabaseReference call_hist = FirebaseDatabase.getInstance().getReference().child("Call_history")
                                                        .child(firebaseUser.getUid()).child(kisko_kiya);
                                                call_hist.push().setValue(dataSnapshot.getValue());

                                                //start putting chats in dB for history

                                                DatabaseReference hist = FirebaseDatabase.getInstance().getReference("prev_Calls").child(firebaseUser.getUid()).child(start_time).child("Chats");
                                                DatabaseReference hist2 = FirebaseDatabase.getInstance().getReference("prev_Calls").child(phone_karne_wale_ka_id).child(start_time).child("Chats");

                                                HashMap<String, Object> result = new HashMap<>();
//                                                result.put("start", start_time);
//                                                result.put("end","");
                                                //result.put("Chats",dataSnapshot.getValue());
                                                hist.push().setValue(dataSnapshot.getValue());
                                                hist2.push().setValue(dataSnapshot.getValue());

                                                /////uske db me bhi daal apna chat

                                                DatabaseReference call_hist2 = FirebaseDatabase.getInstance().getReference().child("Call_history")
                                                        .child(kisko_kiya).child(firebaseUser.getUid());
                                                call_hist2.push().setValue(dataSnapshot.getValue());

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
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        /////
                    }
                    else if (dataSnapshot.hasChild("to")) {
                        /////to ka naam
                        DatabaseReference ref3 = usr3.child("to").child("uid");
                        ref3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                phone_karne_wale_ka_id = dataSnapshot.getValue().toString();

                                //phone kisko kiya
                                DatabaseReference kiso_kiya = FirebaseDatabase.getInstance().getReference()
                                        .child("Calls").child(firebaseUser.getUid()).child("Call details").child("to").child("uid");
                                kiso_kiya.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String kisko_kiya = dataSnapshot.getValue().toString();

                                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chats").child(firebaseUser.getUid());

                                        dr.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                DatabaseReference call_hist = FirebaseDatabase.getInstance().getReference().child("Call_history")
                                                        .child(firebaseUser.getUid()).child(kisko_kiya);
                                                call_hist.push().setValue(dataSnapshot.getValue());

                                                /////uske db me bhi daal apna chat

                                                DatabaseReference call_hist2 = FirebaseDatabase.getInstance().getReference().child("Call_history")
                                                        .child(kisko_kiya).child(firebaseUser.getUid());
                                                call_hist2.push().setValue(dataSnapshot.getValue());


                                                DatabaseReference hist = FirebaseDatabase.getInstance().getReference("prev_Calls").child(firebaseUser.getUid()).child(start_time).child("Chats");
                                                DatabaseReference hist2 = FirebaseDatabase.getInstance().getReference("prev_Calls").child(phone_karne_wale_ka_id).child(start_time).child("Chats");

                                                HashMap<String, Object> result = new HashMap<>();
//                                                result.put("start", start_time);
//                                                result.put("end","");
                                                //result.put("Chats",dataSnapshot.getValue());
                                                hist.push().setValue(dataSnapshot.getValue());
                                                hist2.push().setValue(result);

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


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        /////
                    }
                    else {
                        Toast.makeText(CallGoingActivity.this, "call error", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CallGoingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    //////////insert chats in dB
    private void jugni2(String man ,String who_said) {

        String yermark = man;

        if(yermark!=" ") {

            DatabaseReference usr3 = FirebaseDatabase.getInstance().getReference()
                    .child("Calls").child(firebaseUser.getUid()).child("Call details");

            usr3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("from")) {
                        /////from ka uid
                        DatabaseReference ref3 = usr3.child("from").child("uid");
                        ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                phone_karne_wale_ka_id = dataSnapshot.getValue().toString();

                                //phone kisko kiya
                                DatabaseReference kiso_kiya = FirebaseDatabase.getInstance().getReference()
                                        .child("Calls").child(firebaseUser.getUid()).child("Call details").child("to").child("uid");
                                kiso_kiya.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String kisko_kiya = dataSnapshot.getValue().toString();

                                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chats").child(firebaseUser.getUid());

                                        dr.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                DatabaseReference call_hist = FirebaseDatabase.getInstance().getReference().child("Call_history")
                                                        .child(firebaseUser.getUid()).child(kisko_kiya);
                                                //call_hist.push().setValue(dataSnapshot.getValue());
                                                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                                String res = currentDate + " " + currentTime;

                                                HashMap<String, Object> result = new HashMap<>();
                                                result.put("chat", man);
                                                result.put("time", res);
                                                result.put("type", "chat");
                                                result.put("who_tells", who_said);
                                                result.put("url","");

                                                call_hist.push().setValue(result);

                                                /////uske db me bhi daal apna chat

                                                DatabaseReference call_hist2 = FirebaseDatabase.getInstance().getReference().child("Call_history")
                                                        .child(kisko_kiya).child(firebaseUser.getUid());
                                                //call_hist2.push().setValue(dataSnapshot.getValue());
                                                HashMap<String, Object> result2 = new HashMap<>();
                                                result2.put("chat", man);
                                                result2.put("time", res);
                                                result2.put("type", "chat");
                                                result2.put("who_tells", who_said);
                                                result2.put("url","");

                                                call_hist2.push().setValue(result2);


                                                DatabaseReference hist = FirebaseDatabase.getInstance().getReference("prev_Calls").child(firebaseUser.getUid()).child(start_time).child("Chats");
                                                DatabaseReference hist2 = FirebaseDatabase.getInstance().getReference("prev_Calls").child(phone_karne_wale_ka_id).child(start_time).child("Chats");

                                                HashMap<String, Object> result22 = new HashMap<>();
//                                                result.put("start", start_time);
//                                                result.put("end","");
                                                //result22.put("Chats",result2);
                                                hist.push().setValue(result2);
                                                hist2.push().setValue(result2);


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
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        /////
                    }
                    else if (dataSnapshot.hasChild("to")) {
                        /////to ka naam
                        DatabaseReference ref3 = usr3.child("to").child("uid");
                        ref3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                phone_karne_wale_ka_id = dataSnapshot.getValue().toString();

                                //phone kisko kiya
                                DatabaseReference kiso_kiya = FirebaseDatabase.getInstance().getReference()
                                        .child("Calls").child(firebaseUser.getUid()).child("Call details").child("to").child("uid");
                                kiso_kiya.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String kisko_kiya = dataSnapshot.getValue().toString();

                                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Chats").child(firebaseUser.getUid());

                                        dr.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                DatabaseReference call_hist = FirebaseDatabase.getInstance().getReference().child("Call_history")
                                                        .child(firebaseUser.getUid()).child(kisko_kiya);
                                                //call_hist.push().setValue(dataSnapshot.getValue());
                                                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                                String res = currentDate + " " + currentTime;

                                                HashMap<String, Object> result = new HashMap<>();
                                                result.put("chat", man);
                                                result.put("time", res);
                                                result.put("type", "chat");
                                                result.put("who_tells", who_said);
                                                result.put("url","");

                                                call_hist.push().setValue(result);

                                                /////uske db me bhi daal apna chat

                                                DatabaseReference call_hist2 = FirebaseDatabase.getInstance().getReference().child("Call_history")
                                                        .child(kisko_kiya).child(firebaseUser.getUid());
                                                //call_hist2.push().setValue(dataSnapshot.getValue());
                                                HashMap<String, Object> result2 = new HashMap<>();
                                                result2.put("chat", man);
                                                result2.put("time", res);
                                                result2.put("type", "chat");
                                                result2.put("who_tells", who_said);
                                                result2.put("url","");

                                                call_hist2.push().setValue(result2);

                                                DatabaseReference hist = FirebaseDatabase.getInstance().getReference("prev_Calls").child(firebaseUser.getUid()).child(start_time).child("Chats");
                                                DatabaseReference hist2 = FirebaseDatabase.getInstance().getReference("prev_Calls").child(phone_karne_wale_ka_id).child(start_time).child("Chats");

                                                HashMap<String, Object> result22 = new HashMap<>();
//                                                result.put("start", start_time);
//                                                result.put("end","");
                                                //result22.put("Chats",result2);
                                                hist.push().setValue(result2);
                                                hist2.push().setValue(result2);

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


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        /////
                    }
                    else {
                        Toast.makeText(CallGoingActivity.this, "call error", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CallGoingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onPartialResults(Bundle results) {
        Log.i(LOG_TAG, "onPartialResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        String zam = "";
        for (String result : matches){
            text += result + "\n";
            zam = result;}
//        if(text!=" ")
//            Log.e(LOG_TAG, "onPartialResults="+zam + "....................................");
        returnedText.setText(text);

    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onEvent");

    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "Say something";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                turnOf();
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    //change real time language
    private void identifyLanguage(String conv_to) {
        String sourceText = result.getText().toString();

        FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance()
                .getLanguageIdentification();

        //mSourceLang.setText("Detecting..");

        identifier.identifyLanguage(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s.equals("und")){
                    Toast.makeText(getApplicationContext(),"Language Not Identified", Toast.LENGTH_SHORT).show();
                    result.setText(sourceText);
                }
                else {
                    getLanguageCode(s);
                }
            }
        });

    }

    private void getLanguageCode(String language) {

        int langCode;
        switch (language){
            case "hi":
                langCode = FirebaseTranslateLanguage.HI;
                //mSourceLang.setText("Hindi");
                break;
            case "mr":
                langCode = FirebaseTranslateLanguage.MR;
                //mSourceLang.setText("Marathi");

                break;
            case "bn":
                langCode = FirebaseTranslateLanguage.BN;
                //mSourceLang.setText("Bengali");
                break;

            case "ta":
                langCode = FirebaseTranslateLanguage.TA;
                //mSourceLang.setText("Tamil");
                break;

            case "te":
                langCode = FirebaseTranslateLanguage.TE;
                //mSourceLang.setText("Telugu");
                break;

            case "en":
                langCode = FirebaseTranslateLanguage.EN;
                //mSourceLang.setText("English ha");
                break;
            default:
                langCode = 0;
        }
        translateText(langCode);
    }

    private void translateText(int langCode) {
        //mTranslatedText.setText("Translating..");
        DatabaseReference fbcode = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("fb_val");
        fbcode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fb_code = dataSnapshot.getValue().toString();
                int langCode2;
                switch (fb_code){
                    case "hi":
                        langCode2 = FirebaseTranslateLanguage.HI;
                        //mSourceLang.setText("Hindi");
                        break;
                    case "mr":
                        langCode2 = FirebaseTranslateLanguage.MR;
                        //mSourceLang.setText("Marathi");

                        break;
                    case "bn":
                        langCode2 = FirebaseTranslateLanguage.BN;
                        //mSourceLang.setText("Bengali");
                        break;

                    case "ta":
                        langCode2 = FirebaseTranslateLanguage.TA;
                        //mSourceLang.setText("Tamil");
                        break;

                    case "te":
                        langCode2 = FirebaseTranslateLanguage.TE;
                        //mSourceLang.setText("Telugu");
                        break;

                    case "en":
                        langCode2 = FirebaseTranslateLanguage.EN;
                        //mSourceLang.setText("English ha");
                        break;
                    default:
                        langCode2 = 0;
                }
                if(langCode == langCode2){
                    Toast.makeText(CallGoingActivity.this, "Same lang on both sides", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(CallGoingActivity.this, "lc1 and2 : " + langCode + " " +langCode2, Toast.LENGTH_SHORT).show();

                FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                        //from language
                        .setSourceLanguage(langCode)
                        // to language
                        .setTargetLanguage(langCode2)
                        .build();

                final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                        .getTranslator(options);

                FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                        .build();



                translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        translator.translate(result.getText().toString()).addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                result.setText(s);
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //change real time language for smart reply
    private void identifyLanguage2(String conv_to) {
        String sourceText = result.getText().toString();

        FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance()
                .getLanguageIdentification();

        //mSourceLang.setText("Detecting..");

        identifier.identifyLanguage(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s.equals("und")){
                    Toast.makeText(getApplicationContext(),"Language Not Identified", Toast.LENGTH_SHORT).show();
                    result.setText(sourceText);
                }
                else {
                    getLanguageCode(s);
                }
            }
        });

    }

    private void getLanguageCode2(String language) {

        int langCode;
        switch (language){
            case "hi":
                langCode = FirebaseTranslateLanguage.HI;
                //mSourceLang.setText("Hindi");
                break;
            case "mr":
                langCode = FirebaseTranslateLanguage.MR;
                //mSourceLang.setText("Marathi");

                break;
            case "bn":
                langCode = FirebaseTranslateLanguage.BN;
                //mSourceLang.setText("Bengali");
                break;

            case "ta":
                langCode = FirebaseTranslateLanguage.TA;
                //mSourceLang.setText("Tamil");
                break;

            case "te":
                langCode = FirebaseTranslateLanguage.TE;
                //mSourceLang.setText("Telugu");
                break;

            case "en":
                langCode = FirebaseTranslateLanguage.EN;
                //mSourceLang.setText("English ha");
                break;
            default:
                langCode = 0;
        }
        translateText(langCode);
    }

    private void translateText2(int langCode) {
        //mTranslatedText.setText("Translating..");
        DatabaseReference fbcode = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("fb_val");
        fbcode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fb_code = dataSnapshot.getValue().toString();
                int langCode2;
                switch (fb_code){
                    case "hi":
                        langCode2 = FirebaseTranslateLanguage.HI;
                        //mSourceLang.setText("Hindi");
                        break;
                    case "mr":
                        langCode2 = FirebaseTranslateLanguage.MR;
                        //mSourceLang.setText("Marathi");

                        break;
                    case "bn":
                        langCode2 = FirebaseTranslateLanguage.BN;
                        //mSourceLang.setText("Bengali");
                        break;

                    case "ta":
                        langCode2 = FirebaseTranslateLanguage.TA;
                        //mSourceLang.setText("Tamil");
                        break;

                    case "te":
                        langCode2 = FirebaseTranslateLanguage.TE;
                        //mSourceLang.setText("Telugu");
                        break;

                    case "en":
                        langCode2 = FirebaseTranslateLanguage.EN;
                        //mSourceLang.setText("English ha");
                        break;
                    default:
                        langCode2 = 0;
                }
                if(langCode == langCode2){
                    Toast.makeText(CallGoingActivity.this, "Same lang on both sides", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(CallGoingActivity.this, "lc1 and2 : " + langCode + " " +langCode2, Toast.LENGTH_SHORT).show();

                FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                        //from language
                        .setSourceLanguage(langCode)
                        // to language
                        .setTargetLanguage(langCode2)
                        .build();

                final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                        .getTranslator(options);

                FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                        .build();



                translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        translator.translate(result.getText().toString()).addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                result.setText(s);
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Java program generate a random AlphaNumeric String
// using Math.random() method

    public String RandomString(){

            int n = 27;

            // chose a Character random from this String
            String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "0123456789"
                    + "abcdefghijklmnopqrstuvxyz";

            // create StringBuffer size of AlphaNumericString
            StringBuilder sb = new StringBuilder(n);

            for (int i = 0; i < n; i++) {

                // generate a random number between
                // 0 to AlphaNumericString variable length
                int index
                        = (int)(AlphaNumericString.length()
                        * Math.random());

                // add Character one by one in end of sb
                sb.append(AlphaNumericString
                        .charAt(index));
            }
            return sb.toString();
    }

}
