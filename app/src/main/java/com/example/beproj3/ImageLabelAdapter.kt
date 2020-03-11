package com.example.beproj3

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import kotlinx.android.synthetic.main.item_row.view.*

class ImageLabelAdapter(private val firebaseVisionList: List<Any>, private val isCloud: Boolean) : RecyclerView.Adapter<ImageLabelAdapter.ItemHolder>() {
    lateinit var context: Context

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindCloud(currentItem: FirebaseVisionImageLabel) {
            when {
                currentItem.confidence > .70 -> itemView.itemAccuracy.setTextColor(ContextCompat.getColor(context, R.color.cyan))
                currentItem.confidence < .30 -> itemView.itemAccuracy.setTextColor(ContextCompat.getColor(context, R.color.blue))
                else -> itemView.itemAccuracy.setTextColor(ContextCompat.getColor(context, R.color.cyan))
            }
            itemView.itemName.text = currentItem.text

            Log.e("item name:",itemView.itemName.text.toString())
            itemView.itemAccuracy.text = "Probability : ${(currentItem.confidence * 100).toInt()}%"
        }

        fun bindDevice(currentItem: FirebaseVisionImageLabel) {
            when {
                currentItem.confidence > .70 -> itemView.itemAccuracy.setTextColor(ContextCompat.getColor(context, R.color.cyan))
                currentItem.confidence < .30 -> itemView.itemAccuracy.setTextColor(ContextCompat.getColor(context, R.color.blue))
                else -> itemView.itemAccuracy.setTextColor(ContextCompat.getColor(context, R.color.cyan))
            }
            itemView.itemName.text = currentItem.text
            Log.e("item name:",itemView.itemName.toString())

            itemView.itemAccuracy.text = "Probability : ${(currentItem.confidence * 100).toInt()}%"
        }

    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val currentItem = firebaseVisionList[position]
        if (isCloud)
            holder.bindCloud(currentItem as FirebaseVisionImageLabel)
        else
            holder.bindDevice(currentItem as FirebaseVisionImageLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        context = parent.context
        return ItemHolder(LayoutInflater.from(context).inflate(R.layout.item_row, parent, false))
    }

    override fun getItemCount() = firebaseVisionList.size
}