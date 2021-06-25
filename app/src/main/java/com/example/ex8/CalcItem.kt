package com.example.ex8

import android.app.Application
import java.util.*

class CalcItem() : Application() {
    private var id:Int=-1
    var threadID:UUID=UUID.randomUUID()
    private var calcValue=-1
    private var firstRoot:Int=-1
    private var secondRoot:Int=-1
    private var progress:Int =0
    private var status:Boolean = false

    fun getCalcValue(): Int {return calcValue}
    fun getId():Int{return id}
    fun getProgress():Int{return progress}
    fun getStatus():Boolean{return status}
    fun getFirstRoot(): Int {return firstRoot}
    fun getSecondRoot(): Int {return secondRoot}
    fun setCalcValue(value:Int){calcValue = value }
    fun setFirstRoot(firstRoot2:Int){firstRoot = firstRoot2}
    fun setStatus(state:Boolean){status = state}
    fun setSecondRoot(firstRoot2:Int){secondRoot = firstRoot2}
    fun setProgress(newProg:Int){progress=newProg}
    fun setId(curId:Int){
        if (curId != null) {
            id=curId
        }
    }

}