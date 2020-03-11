package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.beproj3.Adapters.AllButtons2Adapter;
import com.example.beproj3.Adapters.AllButtonsAdapter;
import com.example.beproj3.Models.wordsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class objo_brief_desc extends AppCompatActivity {
    ImageView im;
    RecyclerView vuo;
    TextView res;
    Button detect,detect_native;
    FloatingActionButton capture ,pick;
    GraphicOverlay mGraphicOverlay;
    ArrayList<wordsModel> words;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_LOAD_IMG = 2;
    String converted = "";
    String base = "";
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Dialog myDialog;

    Uri image;
    String mCameraFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objo_brief_desc);

        im = findViewById(R.id.image_view);
        res = findViewById(R.id.display);
        capture = findViewById(R.id.capture_image);
        detect = findViewById(R.id.detect_text);
        pick = findViewById(R.id.pick_image);
        mGraphicOverlay = findViewById(R.id.graphic_overlay);
        detect_native = findViewById(R.id.see_native_text);

        vuo = findViewById(R.id.main_recycler);
        vuo.setHasFixedSize(true);
        vuo.setLayoutManager(new LinearLayoutManager(this));

        words = new ArrayList<>();


        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res.setText("");
                //detect_native.setVisibility(View.VISIBLE);
                dispatchTakePictureIntent();
            }
        });

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res.setText("");
                //detect_native.setVisibility(View.VISIBLE);
                detectText();
            }
        });

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res.setText("");
                //detect_native.setVisibility(View.VISIBLE);
                dispatchPickPictureIntent();
            }
        });

        detect_native.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm=((BitmapDrawable)im.getDrawable()).getBitmap();
                Log.e("bm - ",String.valueOf(bm));
                FirebaseVisionImage firebaseVisionImage2 = FirebaseVisionImage.fromBitmap(bm);
                // recognizeTextCloud(firebaseVisionImage2);
            }
        });
        myDialog = new Dialog(this);
    }


    private void dispatchPickPictureIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent,RESULT_LOAD_IMG);
    }

    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//        String path = String.valueOf(Environment.getExternalStorageDirectory()) + "/your_name_folder";
//        try {
//            File ruta_sd = new File(path);
//            File folder = new File(ruta_sd.getAbsolutePath(), path);
//            boolean success = true;
//            if (!folder.exists()) {
//                success = folder.mkdir();
//            }
//            if (success) {
//                Toast.makeText(capture_open_image.this, "Carpeta Creada...", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception ex) {
//            Log.e("Carpetas", "Error al crear Carpeta a tarjeta SD");
//        }
//
//        Intent i = new Intent(capture_open_image.this, capture_open_image.class);
//        startActivity(i);
//        finish();
        im.invalidate();
        //im.setImageDrawable(null);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("-mm-ss");

        String newPicFile = df.format(date) + ".jpg";
        String outPath = "/sdcard/" + newPicFile;
        File outFile = new File(outPath);

        mCameraFileName = outFile.toString();
        Uri outuri = Uri.fromFile(outFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        words.clear();
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            if (data != null) {
                image = data.getData();
                im.setImageURI(image);
                //im.setVisibility(View.VISIBLE);
            }

            if (image == null && mCameraFileName != null) {
                image = Uri.fromFile(new File(mCameraFileName));
                im.setImageURI(image);
                im.setVisibility(View.VISIBLE);
            }

            File file = new File(mCameraFileName);
            if (!file.exists()) {
                file.mkdir();
            }

            BitmapDrawable drawable = (BitmapDrawable) im.getDrawable();
            Bitmap imageBitmap = drawable.getBitmap();

            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);

            //obj description
            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                    .getOnDeviceImageLabeler();
            labeler.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                @Override
                public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                    for (FirebaseVisionImageLabel label: firebaseVisionImageLabels) {
                        String text = label.getText();
                        String entityId = label.getEntityId();
                        float confidence = label.getConfidence();

                        wordsModel op = new wordsModel();
                        op.setWord(text);
                        words.add(op);
                        //add to adapter
                        AllButtons2Adapter adapter = new AllButtons2Adapter(objo_brief_desc.this ,words);
                        vuo.setAdapter(adapter);
                        adapter.notifyDataSetChanged();


                        Log.e("Label text in capture:",text);
                        //Toast.makeText(capture_open_image.this, "text:"+text, Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        else if(requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK){
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                im.setImageBitmap(selectedImage);

                FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(selectedImage);

                FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                        .getOnDeviceImageLabeler();
                labeler.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                        for (FirebaseVisionImageLabel label: firebaseVisionImageLabels) {
                            String text = label.getText();
                            String entityId = label.getEntityId();
                            float confidence = label.getConfidence();

                                wordsModel op = new wordsModel();
                                op.setWord(text);
                                words.add(op);
                                //add to adapter
                                AllButtons2Adapter adapter = new AllButtons2Adapter(objo_brief_desc.this ,words);
                                vuo.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            Log.e("Label text:",text);
                            //Toast.makeText(capture_open_image.this, "text:"+text, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //Toast.makeText(capture_open_image.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }



    private void displayTextFromImage(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.TextBlock> blockList = firebaseVisionText.getTextBlocks();
        if(blockList.size() == 0){
            Toast.makeText(this, "No Text found", Toast.LENGTH_SHORT).show();
        }
        else{
            // Log.e("Textb lock:", String.valueOf(firebaseVisionText.getTextBlocks()));
//            for(FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()){
//                String text = block.getText();
//                String j = res.getText().toString();
//                res.setText(j+"\n"+text);
//            }

            for(int i = 0 ; i < blockList.size() ; i++)
            {
                List<FirebaseVisionText.Line> lines = blockList.get(i).getLines();
                for(int j = 0 ; i< lines.size() ;j++){
                    List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                    for(int k = 0; k < elements.size() ;k++){
                        GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                        mGraphicOverlay.add(textGraphic);
                    }
                }
            }
        }
    }

    public void detectText() {


    }

    private void recognizeTextCloud(FirebaseVisionImage image) {
        // [START set_detector_options_cloud]
        FirebaseVisionCloudTextRecognizerOptions options = new FirebaseVisionCloudTextRecognizerOptions.Builder()
                .setLanguageHints(Arrays.asList("en", "hi"))
                .build();
        // [END set_detector_options_cloud]

        // [START get_detector_cloud]
//        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
//                .getCloudTextRecognizer();
        // Or, to change the default settings:
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getCloudTextRecognizer(options);
        // [END get_detector_cloud]

        // [START run_detector_cloud]
        Task<FirebaseVisionText> result = detector.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText result) {
                        //processTextRecognitionResult(result);
                        // Task completed successfully
                        // [START_EXCLUDE]
                        // [START get_text_cloud]
//                        for (FirebaseVisionText.TextBlock block : result.getTextBlocks()) {
//                            Rect boundingBox = block.getBoundingBox();
//                            Point[] cornerPoints = block.getCornerPoints();
//                            String text = block.getText();
//
//                            for (FirebaseVisionText.Line line: block.getLines()) {
//                                // ...
//                                for (FirebaseVisionText.Element element: line.getElements()) {
//                                    // ...
//
//                                }
//                            }
//                        }
                        // [END get_text_cloud]
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });
        // [END run_detector_cloud]
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(this, "No text", Toast.LENGTH_SHORT).show();
            return;
        }
        mGraphicOverlay.clear();
        words.clear();
        for(FirebaseVisionText.TextBlock block : texts.getTextBlocks()){
            String text = block.getText();
            String j = res.getText().toString();
            res.setText(j+"\n"+text);

            String[] splited = text.split("\\s+");
            for(int i = 0 ;i < splited.length ;i++){
                wordsModel op = new wordsModel();
                op.setWord(splited[i]);
                words.add(op);
                //add to adapter
                AllButtonsAdapter adapter = new AllButtonsAdapter(objo_brief_desc.this ,words);
                vuo.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void viewWord(wordsModel wo){
        //Toast.makeText(this, wo.getWord(), Toast.LENGTH_SHORT).show();
        base = wo.getWord();
        dome(wo.getWord());

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
                    converted = "undefined";
                    Log.e("Gaavti mean",converted);

                    ShowPopup(base ,converted);
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
                    //Toast.makeText(capture_open_image.this, "Same lang on both sides", Toast.LENGTH_SHORT).show();
                    Log.e("Gaavti mean",converted);

                    ShowPopup(base ,converted);
                    return;
                }
                //Toast.makeText(capture_open_image.this, "lc1 and2 : " + langCode + " " +langCode2, Toast.LENGTH_SHORT).show();

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
                        translator.translate(base).addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                converted = s;
                                //Log.e("Gaavti mean",converted);
                                ShowPopup(base ,converted);
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

    //pop up designo
    public void ShowPopup(String base ,String converted) {
        TextView txtclose;
        TextView obj_name ,prices ,translated;
        Button btnFollow;
        LinearLayout gogle;
        myDialog.setContentView(R.layout.popup_2);

        txtclose = myDialog.findViewById(R.id.txtclose);
        obj_name = myDialog.findViewById(R.id.obj_name);
        prices = myDialog.findViewById(R.id.copi);
        gogle = myDialog.findViewById(R.id.search_google);
        translated = myDialog.findViewById(R.id.traaaans);


        obj_name.setText(base);
        translated.setText(converted);

        prices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //seacrh web
                String query = base;
                Uri uri = Uri.parse("https://www.google.com/search?hl=en&output=search&tbm=shop&psb=1&x=0&y=0&q="+query+"&oq="+query+"&aqs=products-cc..0l10&ved=0ahUKEwinj_yN0ZLoAhXgxzgGHTk7A8QQ-LwECBs");
                Intent gSearchIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(gSearchIntent);
            }
        });
        gogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //seacrh web
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                //String keyword= wo.getWord();
                intent.putExtra(SearchManager.QUERY, base);
                startActivity(intent);
            }
        });
        //txtclose.setText("M");
        btnFollow = (Button) myDialog.findViewById(R.id.btnfollow);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}
