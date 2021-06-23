package com.example.ex8

import android.content.Context
import android.content.Intent
import java.io.Serializable
import java.util.*
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
            item.setId(sp.getInt(it+"calcValue", -2))
            item.setFirstRoot(sp.getInt(it+"firstRoot",-1))
            item.setCalcValue(sp.getInt(it+"calcValue",-1))
            item.setSecondRoot(sp.getInt(it+"secondRoot",-3))
            item.setStatus(sp.getBoolean(it+"status",true))
            item.setId(it.toInt())
            allList.add(item)
        }
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
        edit.putBoolean(item.getId().toString()+"status",item.getStatus())
        edit.putStringSet("allIdSet",allIdSet)
        edit.apply()
    }
    fun deleteFromSP(item:CalcItem)
    {
        val edit = sp.edit()
        edit.remove(item.getId().toString()+"calcValue")
        edit.remove(item.getId().toString()+"firstRoot")
        edit.remove(item.getId().toString()+"secondRoot")
        edit.remove(item.getId().toString()+"status")
        allIdSet?.remove(item.getId().toString())
        edit.apply()
    }
    fun addNewCalc(value :Int)
    {
        val item = CalcItem()
        item.setCalcValue(value)
        item.setStatus(true)
        item.setId(value)
        allList.add(item)
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