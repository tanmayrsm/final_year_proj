package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
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


import java.io.IOException;
import java.util.List;

public class eng_ocr_vid extends AppCompatActivity {
    SurfaceView s;
    TextView tv;
    Button nat,clr;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    FirebaseAuth auth;
    GraphicOverlay mGraphicOverlay;
    boolean ischeck = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case RequestCameraPermissionID:{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                    try {
                        cameraSource.start(s.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eng_ocr_vid);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        s = findViewById(R.id.surf_view);
        tv = findViewById(R.id.display2);
        nat = findViewById(R.id.see_native_text2);
        mGraphicOverlay = findViewById(R.id.graphic_overlay);
        clr = findViewById(R.id.clear_screen);

        clr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGraphicOverlay.clear();
            }
        });

        mGraphicOverlay.clear();

        nat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ischeck = !ischeck;
            }
        });


        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.e("Exception", "Not operational re");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            s.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                            ActivityCompat.requestPermissions(eng_ocr_vid.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                            return;}

                        cameraSource.start(s.getHolder());

                    }catch (Exception e){

                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size() != 0){

                        tv.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i = 0; i< items.size();i++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                if(!ischeck || nat.getVisibility()==View.GONE)
                                    tv.setText(stringBuilder.toString());
                                else{
                                    /////
                                    String james = stringBuilder.toString();

                                    FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance()
                                            .getLanguageIdentification();

                                    identifier.identifyLanguage(james).addOnSuccessListener(new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            if (s.equals("und")){
                                                //Toast.makeText(getApplicationContext(),"Language Not Identified", Toast.LENGTH_SHORT).show();
                                                tv.setText(james);
                                            }
                                            else {

                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").
                                                        child(firebaseUser.getUid()).child("fb_val");

                                                ref.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        int to = getLangCode(dataSnapshot.getValue().toString());

                                                        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                                                                //from language
                                                                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                                                // to language
                                                                .setTargetLanguage(to)
                                                                .build();

                                                        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                                                                .getTranslator(options);

                                                        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                                                                .build();

                                                        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                translator.translate(james).addOnSuccessListener(new OnSuccessListener<String>() {
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

                                                    }
                                                });

                                            }
                                        }
                                    });
                                    /////
                                }
                            }
                        });
                    }
                    ///new code
                    mGraphicOverlay.clear();

                    final StringBuilder strBuilder = new StringBuilder();
                    for (int i = 0; i < items.size(); i++)
                    {
                        TextBlock item = items.valueAt(i);
                        strBuilder.append(item.getValue());
                        strBuilder.append("/");
                        // The following Process is used to show how to use lines & elements as well
                        for (int j = 0; j < items.size(); j++) {
                            TextBlock textBlock = items.valueAt(j);
                            strBuilder.append(textBlock.getValue());
                            strBuilder.append("/");
                            for (Text line : textBlock.getComponents()) {

                                //extract scanned text lines here
                                Log.v("lines", line.getValue());
                                strBuilder.append(line.getValue());
                                strBuilder.append("/");

                                for (Text element : line.getComponents()) {
                                    //extract scanned text words here
                                    Log.v("element", element.getValue());
                                    strBuilder.append(element.getValue());
                                    GraphicOverlay.Graphic textGraphic = new TextGraphic2(mGraphicOverlay, element ,ischeck);
                                    mGraphicOverlay.add(textGraphic);
                                }
                            }
                        }
                    }
                    Log.v("strBuilder.toString()", strBuilder.toString());
                }
            });
        }

    }

    private int getLangCode(String val) {
        int langCode;
        switch (val){
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
        return  langCode;
    }

    @Override
    protected void onStart() {
        super.onStart();
        nat = findViewById(R.id.see_native_text2);

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").
                child(firebaseUser.getUid()).child("my_lang");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("en_GB")){
                    nat.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
