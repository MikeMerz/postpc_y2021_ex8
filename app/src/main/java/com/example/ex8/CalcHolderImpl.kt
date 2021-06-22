package com.example.ex8

import android.content.Context
import android.content.Intent
import java.io.Serializable


class CalcHolderImpl(context: Context) :Serializable
{
    private val allList: MutableList<CalcItem> = ArrayList()
    private  var sp = context.getSharedPreferences("local_change",Context.MODE_PRIVATE)
    private  var allIdSet:MutableSet<String> = mutableSetOf()

    init{
        initFromSP()
    }

    fun initFromSP()
    {
        allIdSet = sp.getStringSet("allIdSet",HashSet()) as MutableSet<String>
        if(allIdSet != null)
        {
            allIdSet.forEach{
                var item = CalcItem()
                item.setCalcValue(sp.getInt(item.toString()+"calcValue",-1))
                item.setFirstRoot(sp.getInt(item.toString()+"firstRoot",-1))
                item.setSecondRoot(sp.getInt(item.toString()+"secondRoot",-1))
                item.setStatus(sp.getBoolean(item.toString()+"status",false))
                item.setId(item.toString())
                allList.add(item)
            }
        }
        //TODO ADD SORT BY LOWEST CALVAL TO HIGHEST WITH SORT FUNCTION USING COMPARATOR
//        val res = allIdSet.sortedBy{cur->cur.}
    }
    fun updateSP(item: CalcItem)
    {
        allIdSet.add(item.getId())
        val edit = sp.edit()
        edit.putInt(item.getId()+"calcValue",item.getCalcValue())
        edit.putInt(item.getId()+"firstRoot",item.getFirstRoot())
        edit.putInt(item.getId()+"secondRoot",item.getSecondRoot())
        edit.putBoolean(item.getId()+"status",item.getStatus())
        edit.putStringSet("allIdSet",allIdSet)
        edit.apply()
    }
    fun deleteFromSP(item:CalcItem)
    {
        val edit = sp.edit()
        edit.remove(item.getId()+"calcValue")
        edit.remove(item.getId()+"firstRoot")
        edit.remove(item.getId()+"secondRoot")
        allIdSet.remove(item.getId())
        edit.apply()
    }
    fun getCurrentItems():MutableList<CalcItem>{return allList}
}