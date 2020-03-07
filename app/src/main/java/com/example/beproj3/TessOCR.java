package com.example.beproj3;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class TessOCR {
    private TessBaseAPI mTess;

    public TessOCR(String language) {
        // TODO Auto-generated constructor stub

        mTess = new TessBaseAPI();
        String datapath = Environment.getExternalStorageDirectory() +"/";
//        String language = "mar";
        File dir = new File(datapath + "tessdata/");
        if (!dir.exists())
            dir.mkdirs();
        Log.e("In path",datapath);
        mTess.setDebug(true);

        mTess.init(datapath, language);
    }

    public String getOCRResult(Bitmap bitmap) {

        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();

        return result;
    }

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }
}
