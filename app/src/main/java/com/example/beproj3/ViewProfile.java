package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.beproj3.Adapters.AllContactsAdapter;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ViewProfile extends AppCompatActivity {

    String his_id,acti,his_name;

    ImageView back ,profile_pic;
    TextView name,email,lang ,back2,block_text;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageRef;
    Toolbar tolb;

    LinearLayout main ,block ,removei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        back = findViewById(R.id.back);
        back2 = findViewById(R.id.back2);
        profile_pic = findViewById(R.id.image_profile);
        name = findViewById(R.id.his_name);
        email = findViewById(R.id.his_email);
        lang = findViewById(R.id.his_lang);
        block_text = findViewById(R.id.block_text);

        tolb = findViewById(R.id.mainbaro);
        setSupportActionBar(tolb);

        main = findViewById(R.id.linea);
        block = findViewById(R.id.block_layout);
        removei = findViewById(R.id.remove_block);


        Intent intent = getIntent();
        his_id = intent.getStringExtra("UskaId");
        acti = intent.getStringExtra("Activity");

        //his_name
        DatabaseReference def = FirebaseDatabase.getInstance().getReference("Users").child(his_id).child("name");
        def.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                his_name =dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        //main LL of block ,remove should be shown ?
        DatabaseReference refo = FirebaseDatabase.getInstance().getReference("Contact_list").child(firebaseUser.getUid()).child(his_id);
        refo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    main.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(his_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                email.setText(user.getEmail());
                lang.setText(user.getLang());
                if(user.getImage_url()!=null)
                Glide.with(getApplicationContext()).load(user.getImage_url()).into(profile_pic);
                else    Glide.with(getApplicationContext()).load(R.drawable.ic_face_black_24dp).into(profile_pic);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(acti.equals("Contacts"))
                {
                    Intent i = new Intent(ViewProfile.this ,AllContacts.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
                else if(acti.equals("sent")){
                    Intent i = new Intent(ViewProfile.this ,AllSent.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
                else if(acti.equals("received")){
                    Intent i = new Intent(ViewProfile.this ,AllReceived.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
                else if(acti.equals("Main")){
                    Intent i = new Intent(ViewProfile.this ,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
                else if(acti.equals("chat_history")){
                    Intent i = new Intent(ViewProfile.this ,chat_history.class);
                    Intent intent = getIntent();

                    i.putExtra("UskaId",his_id);
                    i.putExtra("UskaNaam",his_name);
                    i.putExtra("My",firebaseUser.getUid());

                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            }
        });

        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(acti.equals("Contacts"))
                    startActivity(new Intent(ViewProfile.this,AllContacts.class));
                else if(acti.equals("sent"))
                    startActivity(new Intent(ViewProfile.this,AllSent.class));
                else if(acti.equals("received"))
                    startActivity(new Intent(ViewProfile.this,AllReceived.class));
            }
        });

        DatabaseReference bapo3 = FirebaseDatabase.getInstance().getReference("block_list").child(firebaseUser.getUid());
        bapo3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dss : dataSnapshot.getChildren()){
                        SentModel user = dss.getValue(SentModel.class);
                        if(user.getUid().equals(his_id)){
                            //he is in block list
                            block_text.setText("Unblock");
                        }
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //block listener
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference bapo = FirebaseDatabase.getInstance().getReference("block_list").child(firebaseUser.getUid());
                bapo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot dss : dataSnapshot.getChildren()){
                                SentModel user = dss.getValue(SentModel.class);
                                if(user.getUid().equals(his_id)){
                                    //he is now in block list
                                    block_text.setText("Unblock");
                                }
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                if(block_text.getText().equals("Unblock")){
                    AlertDialog alertDialog = new AlertDialog.Builder(ViewProfile.this).create();
                    alertDialog.setTitle("Do you want to unblock " + name.getText().toString() + " ?");
                    alertDialog.setMessage("You will be able to receive calls from " + name.getText().toString());
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //unblock him
                            DatabaseReference bapo2 = FirebaseDatabase.getInstance().getReference("block_list").child(firebaseUser.getUid());
                            bapo2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        for(DataSnapshot dss : dataSnapshot.getChildren()){
                                            SentModel user = dss.getValue(SentModel.class);
                                            if(user.getUid().equals(his_id)){
                                                //he is in block list
                                                dss.getRef().removeValue();
                                                block_text.setText("Block");
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else {
                    AlertDialog alertDialog = new AlertDialog.Builder(ViewProfile.this).create();
                    alertDialog.setTitle("Do you really want to block " + name.getText().toString() + " ?");
                    alertDialog.setMessage("You won't be able to receive any calls from " + name.getText().toString());
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference rep = FirebaseDatabase.getInstance().getReference("block_list").child(firebaseUser.getUid());
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("uid", his_id);
                            rep.push().setValue(result);
                            block_text.setText("Unblock");
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        });

        //remove connection
        removei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(ViewProfile.this).create();
                alertDialog.setTitle("Do you really want to remove " + name.getText().toString() + "?");
                alertDialog.setMessage(name.getText().toString()+ " won't be your in your contact list any more");
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if he is in ur blocked ones remove him from there
                        //unblock him
                        DatabaseReference bapo2 = FirebaseDatabase.getInstance().getReference("block_list").child(firebaseUser.getUid());
                        bapo2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot dss : dataSnapshot.getChildren()){
                                        SentModel user = dss.getValue(SentModel.class);
                                        if(user.getUid().equals(his_id)){
                                            //he is in block list
                                            dss.getRef().removeValue();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        //automatically if u r also in his list you will be unblocked
                        DatabaseReference bapo3 = FirebaseDatabase.getInstance().getReference("block_list").child(his_id);
                        bapo3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot dss : dataSnapshot.getChildren()){
                                        SentModel user = dss.getValue(SentModel.class);
                                        if(user.getUid().equals(firebaseUser.getUid())){
                                            //I am in his block list
                                            dss.getRef().removeValue();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        //remove him as ur connection
                        DatabaseReference conn_ = FirebaseDatabase.getInstance().getReference("Contact_list").child(firebaseUser.getUid()).child(his_id);
                        conn_.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    dataSnapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        //rem urself as his conn
                        DatabaseReference conn1_ = FirebaseDatabase.getInstance().getReference("Contact_list").child(his_id).child(firebaseUser.getUid());
                        conn1_.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    dataSnapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        Toast.makeText(ViewProfile.this, "Contact removed successfully!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ViewProfile.this ,MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }
                });
                alertDialog.show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

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

}
