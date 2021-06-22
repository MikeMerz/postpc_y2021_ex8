package com.example.ex8

import android.app.Application

class CalcApp :Application(){
    lateinit var db:CalcHolderImpl
    override fun onCreate() {
        super.onCreate()
        if(db==null)
        {
            db = CalcHolderImpl(this.applicationContext)
        }
    }

}