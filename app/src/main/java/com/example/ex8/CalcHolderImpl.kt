package com.example.ex8

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.work.WorkManager
import java.io.Serializable
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class CalcHolderImpl(context: Context) :Serializable
{
    private var allList: MutableList<CalcItem> = ArrayList()
    private var curContext:Context =context
    private  var sp = context.getSharedPreferences("local_change",Context.MODE_PRIVATE)
    private  var allIdSet:MutableSet<String> ?= null

    init{
        initFromSP()
    }

    fun initFromSP()
    {
        allIdSet = sp.getStringSet("allIdSet",HashSet()) as MutableSet<String>
        for(it in allIdSet!!)
        {
            var item = CalcItem()
//            item.setId(sp.getInt(it+"calcValue", -2))
            item.setFirstRoot(sp.getInt(it+"firstRoot",-1))
            item.setCalcValue(sp.getInt(it+"calcValue",-1))
            item.setSecondRoot(sp.getInt(it+"secondRoot",-3))
            item.setProgress(sp.getInt(it+"progress",0))
            item.setStatus(sp.getBoolean(it+"status",true))
            item.setId(it.toInt())
            item.threadID=(UUID.fromString(sp.getString(it+"threadID","")))
            allList.add(item)
        }
        allList.sortWith(sortComperator())
        //TODO ADD SORT BY LOWEST CALVAL TO HIGHEST WITH SORT FUNCTION USING COMPARATOR
//        val res = allIdSet.sortedBy{cur->cur.}
    }
    fun updateSP(item: CalcItem)
    {
        allIdSet?.add(item.getId().toString())
        val edit = sp.edit()
        edit.putInt(item.getId().toString()+"calcValue",item.getCalcValue())
        edit.putInt(item.getId().toString()+"firstRoot",item.getFirstRoot())
        edit.putInt(item.getId().toString()+"secondRoot",item.getSecondRoot())
        edit.putInt(item.getId().toString()+"progress",item.getProgress())
        edit.putBoolean(item.getId().toString()+"status",item.getStatus())
        edit.putString(item.getId().toString()+"threadID",item.threadID.toString())
        edit.putStringSet("allIdSet",allIdSet)
        edit.apply()
    }
    fun deleteFromSP(item:CalcItem)
    {
        allIdSet?.remove(item.getId().toString())
        val edit = sp.edit()
        edit.remove(item.getId().toString()+"calcValue")
        edit.remove(item.getId().toString()+"firstRoot")
        edit.remove(item.getId().toString()+"secondRoot")
        edit.remove(item.getId().toString()+"progress")
        edit.remove(item.getId().toString()+"status")
//        edit.remove(item.getId().toString()+"lastCalc")
        edit.remove(item.getId().toString()+"threadID")
        edit.putStringSet("allIdSet",allIdSet)
        edit.apply()
    }
    fun addNewCalc(item :CalcItem)
    {
//        val item = CalcItem()
//        item.setCalcValue(item)
        item.setStatus(true)
//        item.setId(item)
//        allList.add(item)
        updateSP(item)
        sendBroadCast("added_item",0);

    }
    fun deleteCalc(item: CalcItem)
    {
        val old_pos = allList.indexOf(item)
        allList.remove(item)
        deleteFromSP(item)
        sendBroadCast("deleted_item",old_pos)
    }
    fun cancelCalc(item: CalcItem)
    {
        val old_pos = allList.indexOf(item)
        allList.remove(item)
        deleteFromSP(item)
        WorkManager.getInstance(curContext).cancelWorkById(item.threadID)
        sp.edit().remove(item.getId().toString()+"lastCalc").apply()
        sp.edit().remove(item.getId().toString()+"lastCalc").apply()
        sendBroadCast("deleted_item",old_pos)
    }
    fun saveState(): Serializable {
        val state = TodoState()
        state.savedItems = allList
        return state
    }
    fun loadState(state:Serializable?)
    {
        val state2 = state as TodoState
        allList = state2.savedItems!!
    }
    fun extractLastCalcFromSP(id:Int):Int
    {
        return sp.getInt(id.toString()+"lastCalc",-1)
    }
    fun saveLastInSP(value:String,last:Int)
    {
        sp.edit().putInt(value+"lastCalc",last).apply()
    }
    fun startNewCalc(item:CalcItem):Boolean
    {
        var tempCalc:CalcItem?=null
        for(singleCalc in allList)
        {
            if(singleCalc.getCalcValue() == item.getCalcValue())
            {
                tempCalc = singleCalc
            }
        }
        if(tempCalc ==null)
        {
            allList.add(0,item)
            allList.sortWith(sortComperator())
            updateSP(item)
            sendBroadCast("newCalc",allList.indexOf(item))
            return true
        }
        else
        {
            Toast.makeText(curContext,"Value already Calced",Toast.LENGTH_LONG).show()
            return false
        }
    }
    fun finishedCalc(calcItem: CalcItem,workManager: WorkManager)
    {
        calcItem.setStatus(false)
        workManager.cancelWorkById(calcItem.threadID)
//        updateSP(calcItem)
        allList.sortWith(sortComperator())
        sendBroadCast("calcDone", allList.indexOf(calcItem))
    }
    fun updateProgressSP(calcItem: CalcItem)
    {
//        updateSP(calcItem)
        sp.edit().putInt(calcItem.getId().toString()+"progress",calcItem.getProgress())
        sendBroadCast("progressUpdated", allList.indexOf(calcItem))
    }
    fun extractSecondRootCalcFromSP(id:Int):Int
    {
        return sp.getInt(id.toString()+"secondRoot",-1)
    }
    class sortComperator:Comparator<CalcItem?>
    {
        override fun compare(p0: CalcItem?, p1: CalcItem?): Int {
            return if (p0!!.getStatus() == p1!!.getStatus()) {
                if (p0.getCalcValue() < (p1.getCalcValue())) {
                    -1
                } else{
                    1
                }
            } else {
                if (p0.getStatus()) {
                    1
                } else {
                    -1
                }
            }
        }
        }
    fun getCurrentItems():MutableList<CalcItem>{return allList}
    private fun sendBroadCast(keyWord: String, old_pos: Int) {
        val broad = Intent("db_change")
        broad.putExtra(keyWord, old_pos) //TODO MAYBE DOES PROBLEMS
        curContext.sendBroadcast(broad)
    }
    class TodoState : Serializable {
        var savedItems: MutableList<CalcItem>? = null
    }
    }
