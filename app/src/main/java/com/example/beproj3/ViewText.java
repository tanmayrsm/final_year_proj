package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

//import com.qoppa.jPDFProcess;
//import com.qoppa.android.pdfViewer.fonts.StandardFontTF;

public class ViewText extends AppCompatActivity {
    TextView tv;
    String text;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Switch sop;
    String orig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_text);

        tv = findViewById(R.id.view_text);
        sop = findViewById(R.id.switch3);

        Intent intent = getIntent();
        text = intent.getStringExtra("Text");

        tv.setText(text);
        orig = tv.getText().toString();

        sop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dome(tv.getText().toString());
                }
                else{
                    tv.setText(orig);
                }
            }
        });



    }
    public void dome(String s){
        identifyLanguage(s);
    }
    private void identifyLanguage(String as) {

        FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance()
                .getLanguageIdentification();

        //mSourceLang.setText("Detecting..");

        identifier.identifyLanguage(as).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s.equals("und")){
                    Toast.makeText(getApplicationContext(),"Language Not Identified", Toast.LENGTH_SHORT).show();
                    tv.setText(as);
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
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();


        DatabaseReference fbcode = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("fb_val");
        fbcode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fb_code;
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
                    Toast.makeText(ViewText.this, "Same lang on both sides", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(ViewText.this, "lc1 and2 : " + langCode + " " +langCode2, Toast.LENGTH_SHORT).show();

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
                        translator.translate(tv.getText().toString()).addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                tv.setText(s);
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                PdfDocument doc = new PdfDocument();
                // extract the text
                //String text = doc.getText();

            }
        });

    }
}
