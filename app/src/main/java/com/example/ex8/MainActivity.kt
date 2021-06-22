package com.example.ex8

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    var holderImpl:CalcHolderImpl?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val app: CalcApp = getApplicationContext() as CalcApp
        this.holderImpl = app.db
        if (holderImpl == null) {
            holderImpl = CalcHolderImpl(this)
        }
        val adapter = CalcAdapterImpl(holderImpl!!)
        val calcRecyler :RecyclerView = findViewById(R.id.recyler)
        calcRecyler.adapter =adapter
        calcRecyler.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
    }
}