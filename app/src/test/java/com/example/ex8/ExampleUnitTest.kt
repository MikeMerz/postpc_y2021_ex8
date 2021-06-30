package com.example.ex8


import android.content.Context
import android.os.Looper.getMainLooper
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import junit.framework.TestCase
import org.junit.Assert.assertEquals

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Before
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)

@Config(sdk = [28], application = CalcApp::class)
class ExampleUnitTest {
    @Before
    fun setup()
    {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }
    @Test
    fun test_addItem_should_succed() {
        shadowOf(getMainLooper()).idle()
        val mainActivity = ActivityScenario.launch<MainActivity>(MainActivity::class.java)
        shadowOf(getMainLooper()).idle()
        mainActivity.moveToState(Lifecycle.State.CREATED)
        shadowOf(getMainLooper()).idle()
        mainActivity.onActivity { it: MainActivity ->
            shadowOf(getMainLooper()).idle()
            it.setContentView(R.layout.activity_main)
            val item = CalcItem()
            item.setCalcValue(19)
            CalcApp.db.getCurrentItems().add(item)
            TestCase.assertTrue(it.holderImpl!!.getCurrentItems().size > 0)
        }
    }
    @Test
    fun test_calc_should_work()
    {
        shadowOf(getMainLooper()).idle()
        val mainActivity = ActivityScenario.launch<MainActivity>(MainActivity::class.java)
        shadowOf(getMainLooper()).idle()
        mainActivity.moveToState(Lifecycle.State.CREATED)
        shadowOf(getMainLooper()).idle()
        mainActivity.onActivity { it: MainActivity ->
            shadowOf(getMainLooper()).idle()
            it.setContentView(R.layout.activity_main)
            val item = CalcItem()
            item.setCalcValue(19)
            item.setFirstRoot(19)
            item.setSecondRoot(1)
            CalcApp.db.getCurrentItems().add(item)
            assertEquals(19,CalcApp.db.getCurrentItems()[0].getFirstRoot())
            assertEquals(1,CalcApp.db.getCurrentItems()[0].getSecondRoot())
        }
    }
    @Test
    fun test_emptydb_at_init() {
        shadowOf(getMainLooper()).idle()
        val mainActivity = ActivityScenario.launch<MainActivity>(MainActivity::class.java)
        shadowOf(getMainLooper()).idle()
        mainActivity.moveToState(Lifecycle.State.CREATED)
        shadowOf(getMainLooper()).idle()
        mainActivity.onActivity { it: MainActivity ->
            shadowOf(getMainLooper()).idle()
            it.setContentView(R.layout.activity_main)
            assertEquals(0, CalcApp.db.getCurrentItems().size)
        }
    }
}