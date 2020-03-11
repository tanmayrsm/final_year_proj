package com.example.beproj3

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_extra_obj.*
import kotlinx.android.synthetic.main.activity_obj_detect.*

class obj_detect : BaseCameraActivity() {

    private var itemsList: ArrayList<Any> = ArrayList()
    private lateinit var itemAdapter: ImageLabelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBottomSheet(R.layout.activity_obj_detect)
        rvLabel.layoutManager = LinearLayoutManager(this)
    }

    private fun getLabelsFromDevice(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance().onDeviceObjectDetector
        itemsList.clear()
        detector.processImage(image)
                .addOnSuccessListener {
                    // Task completed successfully
                    fabProgressCircle.hide()
                    itemsList.addAll(it)
                    itemAdapter = ImageLabelAdapter(itemsList, false)
                    rvLabel.adapter = itemAdapter
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                    Log.e("item:",it.toString())
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    fabProgressCircle.hide()
                    Toast.makeText(baseContext,"Sorry, something went wrong!",Toast.LENGTH_SHORT).show()
                }
    }

    private fun getLabelsFromClod(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance()
                .onDeviceObjectDetector
        itemsList.clear()
        detector.processImage(image)
                .addOnSuccessListener {
                    // Task completed successfully
                    fabProgressCircle.hide()
                    itemsList.addAll(it)
                    itemAdapter = ImageLabelAdapter(itemsList, true)
                    rvLabel.adapter = itemAdapter
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    fabProgressCircle.hide()
                    Toast.makeText(baseContext,"Sorry, something went wrong!",Toast.LENGTH_SHORT).show()
                }
    }

    override fun onClick(v: View?) {
        fabProgressCircle.show()
        cameraView.captureImage { cameraKitImage ->
            // Get the Bitmap from the captured shot
            getLabelsFromClod(cameraKitImage.bitmap)
            runOnUiThread {
                showPreview()
                imagePreview.setImageBitmap(cameraKitImage.bitmap)
            }
        }
    }

}
