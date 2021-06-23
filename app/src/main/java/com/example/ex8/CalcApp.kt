package com.example.ex8

import android.app.Application
import android.content.Context

class CalcApp :Application(){
    lateinit var db:CalcHolderImpl
    lateinit var ins:Context
    override fun onCreate() {
        super.onCreate()
        ins = applicationContext
        if(db==null)
        {
            db = CalcHolderImpl(this.applicationContext)
        }
    }
    fun returnIns():Context{return ins}

}