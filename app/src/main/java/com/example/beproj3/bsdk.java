package com.example.beproj3;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.example.beproj3.tessTwo.TESS_DATA;

public class bsdk {

        public static final String PACKAGE_NAME = "com.example.beproj3";
        public static final String DATA_PATH = Environment
                .getExternalStorageDirectory().toString() + "/tessdata/";



        public static final String lang = "ben";

        private static final String TAG = "TESSERACT";
        private AssetManager assetManager;

        private TessBaseAPI mTess;

        public bsdk(AssetManager assetManager) {

            Log.i("Data path2", DATA_PATH);

            this.assetManager = assetManager;


            String[] paths = new String[] { DATA_PATH + "/"+PACKAGE_NAME, DATA_PATH + "tessdata/" };

            for (String path : paths) {
                Log.e("Path arr:",path);

                File dir = new File(path);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        Log.e(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                        return;
                    } else {
                        Log.e(TAG, "Created directory " + path + " on sdcard");
                    }
                }
            }

            if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
                try {
                    InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                    OutputStream out = new FileOutputStream(new File(DATA_PATH + "tessdata/", lang + ".traineddata"));
                    Log.e("lang:",lang);

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    Log.e(TAG, "Copied " + lang + " traineddata");
                } catch (Exception e) {
                    Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
                }
            }
            else{
               Log.e("File exists",DATA_PATH + "tessdata/" + lang + ".traineddata");
            }

            mTess = new TessBaseAPI();
            //mTess.init("/storage/emulated/0/tessdata/", "eng");
            mTess.setDebug(true);
            mTess.init(DATA_PATH, lang);

        }


        public String getResults(Bitmap bitmap)
        {
            mTess.setImage(bitmap);
            String result = mTess.getUTF8Text();
            return result;
        }

        public void onDestroy() {
            if (mTess != null)
                mTess.end();
        }

}
