package com.example.ex8

import android.content.Context
import android.util.Log

import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlin.math.sqrt

class RootWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    var curData: Data.Builder = Data.Builder()
    private val waitingTime: Int = 100
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
        for(i in check .. numToCalc)
        {
            try {
                setProgressAsync(curData.putInt("PROGRESS",(i*100)/numToCalc).build())
                Log.d("ASDASD",((i*100)/numToCalc).toString())
                Thread.sleep(waitingTime.toLong())
                if(i%5000==0)
                {
                    (applicationContext as CalcApp).db.saveLastInSP(numToCalc.toString()+"lastCalc",i)
                    CalcApp().db.saveLastInSP(numToCalc.toString(),i.toInt())
                }
            }catch (e:InterruptedException ){
             e.printStackTrace()
            }
            if(numToCalc%i==0)
            {
                setProgressAsync(curData.putInt("PROGRESS",100).build())
                return Result.success(Data.Builder().putInt("firstRoot",i).putInt("secondRoot",numToCalc/i).build())
//                var newData :Data = Data.Builder().putLong("root1",i).putLong("root2",numToCalc/i).build()
//                setProgressAsync(curData.putInt("PROGRESS",numToCalc.toInt()).build())
//                return Result.success(newData)
            }

        }
        setProgressAsync(curData.putInt("PROGRESS",100).build())
        return Result.success(Data.Builder().putInt("firstRoot",numToCalc).putInt("secodRoot",1).build())
    }

}