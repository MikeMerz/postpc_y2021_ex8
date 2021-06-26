package com.example.ex8

import android.content.Context
import android.util.Log

import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlin.math.sqrt

class RootWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    var curData: Data.Builder = Data.Builder()
    private val waitingTime: Int = 10
    init {
        setProgressAsync(Data.Builder().putInt("PROGRESS",0).build())
    }
    override fun doWork(): Result {
        var numToCalc = inputData.getInt("number", -1)
        var numToCalcSquare = 0.0
        if(numToCalc>0)
        {
            numToCalcSquare = sqrt(numToCalc.toDouble())
        }
        var check = (applicationContext as CalcApp).getdb().extractLastCalcFromSP(numToCalc)
        if(check==-1)
        {
            check=2
        }
        for(i in check .. numToCalcSquare.toInt())
        {
            try {
                setProgressAsync(curData.putInt("PROGRESS",(i*100)/numToCalcSquare.toInt()).build())
//                Log.d("ASDASD",((i*100)/numToCalc).toString())
                Thread.sleep(waitingTime.toLong())
                if(i%10==0)
                {
                    CalcApp.db.saveLastInSP(numToCalc.toString()+"lastCalc",i)
//                    CalcApp.db.saveLastInSP(numToCalc.toString(),i.toInt())
                }
            }catch (e:InterruptedException ){
             e.printStackTrace()
            }
            if(numToCalc%i==0)
            {
                setProgressAsync(curData.putInt("PROGRESS",100).build())
                return Result.success(Data.Builder().putInt("firstRoot",i).putInt("secondRoot",numToCalc/i).build())
            }

        }
        setProgressAsync(curData.putInt("PROGRESS",100).build())
        return Result.success(Data.Builder().putInt("firstRoot",numToCalc).putInt("secondRoot",1).build())
    }

}