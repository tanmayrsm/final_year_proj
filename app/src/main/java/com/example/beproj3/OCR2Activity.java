package com.example.beproj3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OCR2Activity extends AppCompatActivity implements View.OnClickListener {
    private TessOCR mTessOCR;
    private TextView mResult;
    private ProgressDialog mProgressDialog;
    private ImageView mImage;
    private Button mButtonGallery, mButtonCamera;
    private String mCurrentPhotoPath;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_PICK_PHOTO = 2;

    public static final String TESS_DATA = "/tessdata";
    public static final String DATA_PATH = Environment
            .getExternalStorageDirectory().toString() +"/";
    public static String lang = "mar";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr2);

        String lan = getIntent().getStringExtra("lan");

        lang = lan;
        Log.e("lang:",lang);

        File dir = new File(Environment.getExternalStorageDirectory() + "/tessdata/");

        Log.e("dir:",dir.toString());
        if(!dir.exists()){
            if (!dir.mkdir()) {
                Toast.makeText(getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
            }
        }
        try {


            String[] paths = new String[] { DATA_PATH + "tessdata/" };

            for (String path : paths) {
                Log.e("Path arr:",path);

                File dir2 = new File(path);
                if (!dir2.exists()) {
                    if (!dir2.mkdirs()) {
                        Log.e("dir2_line72", "ERROR: Creation of directory " + path + " on sdcard failed");
                        return;
                    } else {
                        Log.e("dir2_line75", "Created directory " + path + " on sdcard");
                    }
                }
            }

            if (!(new File(DATA_PATH + "tessdata/" + lan + ".traineddata")).exists()) {
                try {
                    AssetManager assetManager = getAssets();
                    InputStream in = assetManager.open("tessdata/" + lan + ".traineddata");
                    OutputStream out = new FileOutputStream(new File(DATA_PATH + "tessdata/", lan + ".traineddata"));

                    Log.e("in:",in.toString());
                    Log.e("out:",out.toString());

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    Log.e("line_95", "Copied " + lan + " traineddata");
                } catch (Exception e) {
                    Log.e("line_97", "Was unable to copy " + lan + " traineddata " + e.toString());
                }
            }
            else{
                Log.e("File exists",DATA_PATH + "tessdata/" + lan + ".traineddata");
            }

//            String fileList[] = getAssets().list("");
//            for(String fileName : fileList){
//                String pathToDataFile = dir + "/" + fileName;
//                if(!(new File(pathToDataFile)).exists()){
//                    Log.e("Path to dta:",pathToDataFile);
//                    InputStream in = getAssets().open(fileName);
//                    Log.e("in:",in.toString());
//
//                    OutputStream out = new FileOutputStream(pathToDataFile);
//                    byte [] buff = new byte[1024];
//                    int len ;
//                    while(( len = in.read(buff)) > 0){
//                        out.write(buff,0,len);
//                    }
//                    in.close();
//                    out.close();
//                }
//            }
        }catch(Exception e) {
            Log.e("filelist exception","re");
            Log.e("Actual msg",e.getMessage());
        }

//    } catch (Exception e) {
//        Log.e("Na mile", e.getMessage());
////            AssetManager assetManager = getAssets();
////            bsdk tess = new bsdk(assetManager);
//
////            Bitmap bitmap = ((BitmapDrawable)im.getDrawable()).getBitmap();
//        Log.e("Aya bitmap","AAAA");
//        //tess.getResults(bitmap);
//    }

        mResult = (TextView) findViewById(R.id.tv_result);
        mImage = (ImageView) findViewById(R.id.image);
        mButtonGallery = (Button) findViewById(R.id.bt_gallery);
        mButtonGallery.setOnClickListener(this);
        mButtonCamera = (Button) findViewById(R.id.bt_camera);
        mButtonCamera.setOnClickListener(this);
        Log.e("Bhjne k pele:",lang);
        mTessOCR = new TessOCR(lang);
    }

    private void uriOCR(Uri uri) {
        if (uri != null) {
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                mImage.setImageBitmap(bitmap);
                doOCR(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            Uri uri = (Uri) intent
                    .getParcelableExtra(Intent.EXTRA_STREAM);
            uriOCR(uri);
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        mTessOCR.onDestroy();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1024);
            }
        }
    }

    /**
     * http://developer.android.com/training/camera/photobasics.html
     */
    private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );



        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO Auto-generated method stub
        if (requestCode == 1024
                && resultCode == Activity.RESULT_OK) {
            setPic();
        }
        else if (requestCode == REQUEST_PICK_PHOTO
                && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                uriOCR(uri);
            }
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImage.getWidth();
        int targetH = mImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor << 1;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImage.setImageBitmap(bitmap);
        doOCR(bitmap);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.bt_gallery:
                pickPhoto();
                break;
            case R.id.bt_camera:
                takePhoto();
                break;
        }
    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }

    private void takePhoto() {
        dispatchTakePictureIntent();
    }

    private void doOCR(final Bitmap bitmap) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "Processing",
                    "Doing OCR...", true);
        }
        else {
            mProgressDialog.show();
        }

        new Thread(new Runnable() {
            public void run() {

                final String result = mTessOCR.getOCRResult(bitmap);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (result != null && !result.equals("")) {
                            mResult.setText(result);
                        }

                        mProgressDialog.dismiss();
                    }

                });

            };
        }).start();
    }
}
