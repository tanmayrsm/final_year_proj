package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.beproj3.Models.Chats;
import com.example.beproj3.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Regsiter extends AppCompatActivity {
    EditText name ,email ,password;
    Button reg;
    FirebaseAuth auth;
    DatabaseReference reference ,reference2;
    Spinner spinner1;
    String[] my_lango = {"en_GB", "hi_IN", "mr_IN", "bn_IN", "ta_IN", "te_IN"};
    String[] fb_vals  = {"en","hi","mr","bn","ta","te"};
    String[] lang = {"English","Hindi","Marathi","Bengali","Tamil","Telugu"};
    String my,fb,lango;
    ProgressDialog loadingbar;
    int index;
    //    marathi - mr_IN
//        hindi - hi_IN
//        eng(UK) - en_GB
//        bn_IN [Bengali (India)]
//        ta_IN [Tamil (India)]
//        te_IN [Telugu (India)]


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regsiter);

        name = findViewById(R.id.reg_name);
        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_pass);

        spinner1 = findViewById(R.id.spin1);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        //reference2 = FirebaseDatabase.getInstance().getReference().child("Calls");

        reg = findViewById(R.id.register);


        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = spinner1.getSelectedItem().toString();
                index = getIndexOf(lang,text);

                loadingbar = new ProgressDialog(Regsiter.this);
                loadingbar.setTitle("Making new Profile");
                loadingbar.setMessage("Please wait...");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                my = my_lango[index];
                fb = fb_vals[index];
                lango = lang[index];


                auth = FirebaseAuth.getInstance();
                reference = FirebaseDatabase.getInstance().getReference().child("Users");
                //reference2 = FirebaseDatabase.getInstance().getReference().child("Calls");

//              ArrayAdapter<String> adapter = new ArrayAdapter<>(Regsiter.this, android.R.layout.simple_list_item_1, my_lango);
//              spinnerDropDownView.setAdapter(adapter);


                final String name_str = name.getText().toString();
                final String email_str= email.getText().toString();
                final String pass_str = password.getText().toString();

                auth.createUserWithEmailAndPassword(email_str,pass_str).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            //Toast.makeText(Regsiter.this, "fb_lang:"+fb, Toast.LENGTH_SHORT).show();

                            User user = new User(name_str,email_str,pass_str,firebaseUser.getUid(),my,fb,lango ,"");
                            reference.child(firebaseUser.getUid()).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                getLanguageCode(fb);
                                                Toast.makeText(Regsiter.this, "User successfully added", Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(Regsiter.this ,MainActivity.class);
                                                loadingbar.dismiss();
                                                startActivity(i);
                                                finish();
                                            }
                                            else{
                                                String error = task.getException().getMessage();
                                                loadingbar.dismiss();
                                                Toast.makeText(Regsiter.this, "User not registered" + error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                            String res = currentDate + " " + currentTime;

                            DatabaseReference hammand = FirebaseDatabase.getInstance().getReference()
                                    .child("Chats");


                            Chats chat = new Chats(" ", res ," " ," "," ");

                            hammand.child(firebaseUser.getUid()).setValue(chat)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                //Toast.makeText(Regsiter.this, "Speech gya nhi dB me", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else{
                            String error2 = task.getException().getMessage();
                            Toast.makeText(Regsiter.this, "Connection error: "+error2, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
    public static int getIndexOf(String[] strings, String item) {
        for (int i = 0; i < strings.length; i++) {
            if (item.equals(strings[i])) return i;
        }
        return -1;
    }
    private void getLanguageCode(String language) {

        int langCode;
        switch (language){
            case "hi":
                langCode = FirebaseTranslateLanguage.HI;

                break;
            case "mr":
                langCode = FirebaseTranslateLanguage.MR;


                break;
            case "bn":
                langCode = FirebaseTranslateLanguage.BN;

                break;

            case "ta":
                langCode = FirebaseTranslateLanguage.TA;

                break;

            case "te":
                langCode = FirebaseTranslateLanguage.TE;

                break;

            case "en":
                langCode = FirebaseTranslateLanguage.EN;

                break;
            default:
                langCode = 0;
        }
        download(langCode);
        if(langCode!=FirebaseTranslateLanguage.EN)
            download(FirebaseTranslateLanguage.EN);                 //extra backup lang
    }

    public void download(int langCode){
        FirebaseModelManager modelManager = FirebaseModelManager.getInstance();
        FirebaseTranslateRemoteModel frModel =
                new FirebaseTranslateRemoteModel.Builder(langCode).build();
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        modelManager.download(frModel, conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        Toast.makeText(Regsiter.this, "Ha model downloaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error.
                    }
                });
    }
}
