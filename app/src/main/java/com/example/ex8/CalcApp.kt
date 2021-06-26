package com.example.ex8

import android.app.Application
import androidx.work.Configuration

class CalcApp :Application(){
    companion object {
        lateinit var db:CalcHolderImpl}
    private var  ins:CalcApp?= null
    override fun onCreate() {
        super.onCreate()
        ins = this
        db = CalcHolderImpl(this.applicationContext)
    }
    fun getdb(): CalcHolderImpl {return db}
    fun getIns():CalcApp?{return ins}
//    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
//        .setMinimumLoggingLevel(android.util.Log.INFO)
//        .build()}

}