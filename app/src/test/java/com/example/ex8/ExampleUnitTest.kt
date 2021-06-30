package com.example.ex8

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Looper.getMainLooper
import android.util.Log
import android.widget.Button
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.android.material.textfield.TextInputLayout
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = CalcApp::class)
class ExampleUnitTest:TestCase() {
    private  var mockHolder:CalcHolderImpl ?= null
    lateinit var activityHolder: ActivityController<MainActivity>
    @Before
    fun setup()
    {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val config = Configuration.Builder().setMinimumLoggingLevel(Log.DEBUG).setExecutor(
            SynchronousExecutor()).build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        activityHolder = Robolectric.buildActivity(MainActivity::class.java)
    }
    @Test
    fun test_when_addingNewCalc_should_add_to_count()
    {
        shadowOf(getMainLooper()).idle()
        activityHolder.create().visible()
        val curActivity = activityHolder.get()
        val addCalc = curActivity.findViewById<Button>(R.id.newCalc)
        val text = curActivity.findViewById<TextInputLayout>(R.id.input)
        addCalc.performClick()
        shadowOf(getMainLooper()).idle()
        val dialog: androidx.appcompat.app.AlertDialog = curActivity.getAlertDialog()
        val okButton: Button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        text.editText!!.setText("11")
        okButton.performClick()
//        shadowOf(getMainLooper()).idle()
        val res = curActivity.holderImpl!!.getCurrentItems()[0]
        assertTrue(res.getStatus())
    }
}