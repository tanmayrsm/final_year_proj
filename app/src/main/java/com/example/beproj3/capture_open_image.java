package com.example.beproj3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.example.beproj3.GraphicOverlay;
import com.example.beproj3.TextGraphic;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class capture_open_image extends AppCompatActivity {
    ImageView im;
    TextView res;
    Button capture,detect,pick,detect_native;
    GraphicOverlay mGraphicOverlay;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_LOAD_IMG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_open_image);

        im = findViewById(R.id.image_view);
        res = findViewById(R.id.display);
        capture = findViewById(R.id.capture_image);
        detect = findViewById(R.id.detect_text);
        pick = findViewById(R.id.pick_image);
        mGraphicOverlay = findViewById(R.id.graphic_overlay);
        detect_native = findViewById(R.id.see_native_text);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res.setText("");
                detect_native.setVisibility(View.VISIBLE);
                dispatchTakePictureIntent();
            }
        });

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res.setText("");
                detect_native.setVisibility(View.VISIBLE);
                detectText();
            }
        });

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res.setText("");
                detect_native.setVisibility(View.VISIBLE);
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
    }


    private void dispatchPickPictureIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent,RESULT_LOAD_IMG);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            im.setImageBitmap(imageBitmap);

            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();

            Task<FirebaseVisionText> result =
                    detector.processImage(firebaseVisionImage)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    //displayTextFromImage(firebaseVisionText);
                                    processTextRecognitionResult(firebaseVisionText);
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(capture_open_image.this, "Error", Toast.LENGTH_SHORT).show();
                                            Log.e("Cant recognnize text",e.getMessage());
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
                FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();

                Task<FirebaseVisionText> result =
                        detector.processImage(firebaseVisionImage)
                                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                    @Override
                                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                        //displayTextFromImage(firebaseVisionText);
                                        processTextRecognitionResult(firebaseVisionText);
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(capture_open_image.this, "Error", Toast.LENGTH_SHORT).show();
                                                Log.e("Cant recognnize pick",e.getMessage());
                                            }
                                        });


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(capture_open_image.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                        processTextRecognitionResult(result);
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
        for(FirebaseVisionText.TextBlock block : texts.getTextBlocks()){
            String text = block.getText();
            String j = res.getText().toString();
            res.setText(j+"\n"+text);
        }
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                    mGraphicOverlay.add(textGraphic);

                }
            }
        }
    }
}
