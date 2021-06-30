package com.example.ex8

import android.content.*
import android.os.Bundle
import android.app.Application
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*


class MainActivity : AppCompatActivity() {
    var holderImpl:CalcHolderImpl?=null
    lateinit var receiverDBChange: BroadcastReceiver
    lateinit var builder:AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val curWorkManager = WorkManager.getInstance(CalcApp.app)
        this.holderImpl = CalcApp.db
        attachWorkers(holderImpl!!,curWorkManager)
        val adapter = CalcAdapterImpl(holderImpl!!)
        val calcRecyler :RecyclerView = findViewById(R.id.recyler)
        calcRecyler.adapter =adapter
        calcRecyler.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        var input:String
        findViewById<Button>(R.id.newCalc).setOnClickListener{
            builder = AlertDialog.Builder(this)
            val viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_input,null)
            val inputField = viewInflated.findViewById<EditText>(R.id.input)
            builder.setView(viewInflated)
            builder.apply { setPositiveButton("Start",DialogInterface.OnClickListener{dialog,id->
                input = inputField.text.toString()
                val temp = CalcItem()
                temp.setCalcValue(input.toInt())
                temp.setId(input.toInt())
                holderImpl!!.addNewCalc(temp)
                if(!input.isEmpty() && input.toInt()>0)
                {
                    if(holderImpl!!.startNewCalc(temp))
                    {
                        attachWorker(temp,curWorkManager)
                    }
                }
            })
                setNegativeButton("Cancel",DialogInterface.OnClickListener{dialog,id->
                    dialog.dismiss()
                })
            }
            builder.create()
            builder.show()
        }
        adapter.onDeleteCallback={position->
            holderImpl!!.deleteCalc(holderImpl!!.getCurrentItems().get(position))
        }
        adapter.onCancelCallback={position->
            holderImpl!!.cancelCalc(holderImpl!!.getCurrentItems().get(position))
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
    private fun attachWorkers(calc:CalcHolderImpl,workManage:WorkManager)
    {
        workManage.cancelAllWorkByTag("calc_a_lot_of_roots")
        for(singleCalc in calc.getCurrentItems())
        {
            if(singleCalc.getStatus())
            {
                attachWorker(singleCalc,workManage)
            }
        }
    }
    fun getAlertDialog():AlertDialog{return builder.create()}
    private fun attachWorker(calc: CalcItem,workManage: WorkManager)
    {
        val curWorker= OneTimeWorkRequest.Builder(RootWorker::class.java).setInputData(
            Data.Builder().putInt("number",calc.getCalcValue()).build()).addTag("calc_a_lot_of_roots").build()
        calc.threadID = curWorker.id
        holderImpl!!.updateSP(calc)
        workManage.enqueueUniqueWork(calc.getCalcValue().toString(),ExistingWorkPolicy.KEEP,curWorker)
        val workInfoLiveData: LiveData<WorkInfo> = workManage.getWorkInfoByIdLiveData(curWorker.id)
        workInfoLiveData.observe(this,androidx.lifecycle.Observer {workInfo->
            if(workInfo==null){return@Observer}
            else if(workInfo.state==WorkInfo.State.SUCCEEDED)
            {
                val firstRoot = workInfo.outputData.getInt("firstRoot",-1)
                val secondRoot = workInfo.outputData.getInt("secondRoot",-1)
                calc.setFirstRoot(firstRoot)
                calc.setSecondRoot(secondRoot)
                holderImpl!!.finishedCalc(calc,workManage)

            }
            if(workInfo.progress.getInt("PROGRESS",0)>calc.getProgress())
            {
                calc.setProgress(workInfo.progress.getInt("PROGRESS",0))
                holderImpl!!.updateProgressSP(calc)
            }
        })
    }

}