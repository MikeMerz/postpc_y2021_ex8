package com.example.ex8

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CalcAdapterImpl(holderImpl2:CalcHolderImpl) : RecyclerView.Adapter<CalcHolder>() {
    private  var calculations:CalcHolderImpl = holderImpl2
    var onDeleteCallback : ((Int)->Unit)?=null
    var onCancelCallback : ((Int)->Unit)?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalcHolder {
        val context = parent.getContext()
        return CalcHolder(LayoutInflater.from(context).inflate(R.layout.row_calc, parent, false))
    }

    override fun onBindViewHolder(holder: CalcHolder, position: Int) {
        val calcItem = calculations.getCurrentItems().get(position)
        holder.changeLeftText("The Roots For "+calcItem.getCalcValue())
        holder.changeMidText("Roots are: "+calcItem.getFirstRoot()+"*"+calcItem.getSecondRoot())
        if(calcItem.getStatus())
        {
            holder.cancel!!.visibility = View.VISIBLE
            holder.delete!!.visibility = View.GONE
            holder.midText!!.visibility = View.GONE
            holder.progress!!.visibility = View.VISIBLE
            holder.progress.progress = calcItem.getProgress()
        }
        else
        {
            holder.delete!!.visibility = View.VISIBLE
            holder.cancel!!.visibility = View.GONE
            holder.midText!!.visibility = View.VISIBLE
            holder.progress!!.visibility = View.GONE
        }
        holder.delete.setOnClickListener{
            val callback = onDeleteCallback?:return@setOnClickListener
            if(calcItem != null)
            {
                callback(holder.adapterPosition)
            }
        }
        holder.cancel.setOnClickListener{position->
            val callback = onCancelCallback?:return@setOnClickListener
            if(calcItem != null)
            {
                holder.delete.visibility = View.VISIBLE
                holder.cancel.visibility = View.GONE
                holder.midText.visibility = View.VISIBLE
                holder.progress.visibility = View.GONE
                callback(holder.adapterPosition)
                //TODO WORKER TERMINATION
            }
        }

    }

    override fun getItemCount(): Int {
       return calculations.getCurrentItems().size
    }
}

