package com.example.beproj3;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.Text;
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
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.example.beproj3.GraphicOverlay.Graphic;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class TextGraphic2 extends Graphic {

    private static final String TAG = "TextGraphic";
    private static final int TEXT_COLOR = Color.RED;
    private static final float TEXT_SIZE = 34.0f;
    private static final float STROKE_WIDTH = 2.0f;

    private final Paint rectPaint;
    private final Paint textPaint;
    private final Text element;
    private boolean ischeck;


    TextGraphic2(GraphicOverlay overlay, Text element ,boolean ischeck) {

        super(overlay);
        this.element = element;
        this.ischeck = ischeck;

        rectPaint = new Paint();
        rectPaint.setColor(TEXT_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);

        textPaint = new Paint();
        textPaint.setColor(TEXT_COLOR);
        textPaint.setTextSize(TEXT_SIZE);
        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }


    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Log.d(TAG, "on draw text graphic");
        if (element == null) {
            throw new IllegalStateException("Attempting to draw a null text.");
        }
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        // Draws the bounding box around the TextBlock.
        RectF rect = new RectF(element.getBoundingBox());
        canvas.drawRect(rect, rectPaint);

        // Renders the text at the bottom of the box.
        String jasbir = element.getValue();

        if(ischeck){

            FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance()
                    .getLanguageIdentification();

            identifier.identifyLanguage(jasbir).addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String s) {
                    if (s.equals("und")){
                        //Toast.makeText(getApplicationContext(),"Language Not Identified", Toast.LENGTH_SHORT).show();
                        canvas.drawText(jasbir, rect.left, rect.bottom, textPaint);
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
                                        translator.translate(jasbir).addOnSuccessListener(new OnSuccessListener<String>() {
                                            @Override
                                            public void onSuccess(String s) {
                                                canvas.drawText(s, rect.left, rect.bottom, textPaint);
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

        }
        else{
            canvas.drawText(jasbir, rect.left, rect.bottom, textPaint);
        }

    }

//    private String getConvertedText(String jasbir) {
//
//        String converted ;
//
//
//
//
//
//        return "aa";
//    }

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
}
