package com.example.ex8

import android.content.Context

import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlin.math.sqrt

class RootWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    var curData: Data.Builder = Data.Builder()
    init {
        setProgressAsync(Data.Builder().putInt("PROGRESS",0).build())
    }
    override fun doWork(): Result {
        var numToCalc = inputData.getLong("number", 0)
        var numToCalcSquare = 0.0
        if(numToCalc>0)
        {
            numToCalcSquare = sqrt(numToCalc.toDouble())
        }
        var check = CalcApp().db.extractLastCalcFromSP(numToCalc.toInt())
        if(check==-1)
        {
            check=2
        }
        for(i in check .. numToCalc)
        {
            try {
                setProgressAsync(curData.putInt("PROGRESS",i.toInt()).build())
                if(i.toInt()%5000==0)
                {
                    CalcApp().db.saveLastInSP(numToCalc.toString(),i.toInt())
                }
            }catch (e:InterruptedException ){
             e.printStackTrace()
            }
            if(numToCalc.toInt()%i.toInt()==0)
            {
                var newData :Data = Data.Builder().putLong("root1",i).putLong("root2",numToCalc/i).build()
                setProgressAsync(curData.putInt("PROGRESS",numToCalc.toInt()).build())
                return Result.success(newData)
            }

        }
        setProgressAsync(curData.putInt("PROGRESS",numToCalc.toInt()).build())
        return Result.success()
    }

}