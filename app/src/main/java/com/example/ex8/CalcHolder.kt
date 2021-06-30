package com.example.ex8

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalcHolder(itemView: View
) : RecyclerView.ViewHolder(itemView) {
     val delete :ImageView?=itemView.findViewById(R.id.imageView)
     val cancel :ImageView? = itemView.findViewById(R.id.imageView2)
     val progress: ProgressBar? =itemView.findViewById(R.id.progressBar)
     val leftText:TextView?=itemView.findViewById(R.id.textView2)
     val midText:TextView?=itemView.findViewById(R.id.textView)
    fun  changeMidText(text:String)
    {
        if (midText != null) {
            midText.setText(text)
        }
    }
    fun  changeLeftText(text:String)
    {
        if (leftText != null) {
            leftText.setText(text)
        }
    }
}