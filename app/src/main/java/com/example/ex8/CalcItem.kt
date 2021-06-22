package com.example.ex8

import android.app.Application

class CalcItem(): Application() {
    private var id:String=""
    private var calcValue=-1
    private var firstRoot:Int=-1
    private var secondRoot:Int=-1
    private var status:Boolean = false

    fun getCalcValue(): Int {return calcValue}
    fun getId():String{return id}
    fun getStatus():Boolean{return status}
    fun getFirstRoot(): Int {return firstRoot}
    fun getSecondRoot(): Int {return secondRoot}
    fun setCalcValue(value:Int){calcValue = value }
    fun setFirstRoot(firstRoot2:Int){firstRoot = firstRoot2}
    fun setStatus(state:Boolean){status = state}
    fun setSecondRoot(firstRoot2:Int){secondRoot = firstRoot2}
    fun setId(curId:String){id=curId}

}