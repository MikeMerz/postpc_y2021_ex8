package com.example.ex8

import android.content.*
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*


class MainActivity : AppCompatActivity() {
    var holderImpl:CalcHolderImpl?=null
    lateinit var receiverDBChange: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val app: CalcApp = getApplicationContext() as CalcApp
        var curWorkManager = WorkManager.getInstance(this)
        this.holderImpl = CalcHolderImpl(this.applicationContext)
        if (holderImpl == null) {
            holderImpl = CalcHolderImpl(this)
        }
        val adapter = CalcAdapterImpl(holderImpl!!)
        val calcRecyler :RecyclerView = findViewById(R.id.recyler)
        calcRecyler.adapter =adapter
        calcRecyler.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        var input:String
        findViewById<Button>(R.id.newCalc).setOnClickListener{
            val builder = AlertDialog.Builder(this)
            val viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_input,null)
            val inputField = viewInflated.findViewById<EditText>(R.id.input)
            builder.setView(viewInflated)
            builder.apply { setPositiveButton("Start",DialogInterface.OnClickListener{dialog,id->
                input = inputField.text.toString()
                holderImpl!!.addNewCalc(input.toInt())
                val will = OneTimeWorkRequest.Builder(RootWorker::class.java).setInputData(Data.Builder().putLong("CalcValue",input.toLong()).build()).addTag("calc_a_lot_of_roots").build()
                curWorkManager.enqueueUniqueWork(input,ExistingWorkPolicy.REPLACE,will)
                val temp = CalcItem()
                temp.setCalcValue(input.toInt())
                temp.threadID = will.id


            })
                setNegativeButton("Cancel",DialogInterface.OnClickListener{dialog,id->
                    dialog.dismiss()
                })
            }
            builder.create()
            builder.show()
            //TODO ADD WORKER TO START AND SHOW ON U
        }
        adapter.onDeleteCallback={position->
            holderImpl!!.deleteCalc(holderImpl!!.getCurrentItems().get(position))
        }
        adapter.onCancelCallback={position->
            holderImpl!!.deleteCalc(holderImpl!!.getCurrentItems().get(position))
        }

        receiverDBChange = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                if (intent.action != null && intent.action == "db_change") {
                    if (intent.getIntExtra("added_item", -1) != -1) {
                        adapter.notifyItemInserted(intent.getIntExtra("added_item", -1))
                    } else if (intent.getIntExtra("item_to_done", -1) != -1) {
                        adapter.notifyItemMoved(
                            intent.getIntExtra("item_to_done", -1),
                            holderImpl!!.getCurrentItems().size - 1
                        )
                    } else if (intent.getIntExtra("item_to_progress", -1) != -1) {
                        adapter.notifyItemMoved(intent.getIntExtra("item_to_done", -1), 0)
                    } else if (intent.getIntExtra("deleted_item", -1) != -1) {
                        adapter.notifyItemRemoved(intent.getIntExtra("deleted_item", -1))
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
        registerReceiver(receiverDBChange, IntentFilter("db_change"))

//        findViewById<Button>()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiverDBChange)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        holderImpl!!.loadState(savedInstanceState.getSerializable("toDo"))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("toDo",holderImpl!!.saveState())
    }

}