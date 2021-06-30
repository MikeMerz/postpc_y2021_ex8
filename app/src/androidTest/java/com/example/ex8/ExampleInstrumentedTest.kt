package com.example.ex8

import android.content.DialogInterface
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase

import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)

class ExampleInstrumentedTest :TestCase(){

    @Test
    fun test_def() {
        val mainActivity = ActivityScenario.launch<MainActivity>(MainActivity::class.java)
        mainActivity.moveToState(Lifecycle.State.CREATED)
        mainActivity.onActivity { it: MainActivity ->
            it.setContentView(R.layout.activity_main)
            val new = it.findViewById<Button>(R.id.newCalc)
            new.performClick()
            val textIn = it.findViewById<EditText>(R.id.input)
            val dialog: androidx.appcompat.app.AlertDialog = it.getAlertDialog()
            val okButton: Button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            textIn.setText("19")
            okButton.performClick()
            assertTrue(it.holderImpl!!.getCurrentItems().size > 0)
        }
    }
//    @Test
//    fun test_when_addingNewCalc_should_add_to_count()
//    {
//        Shadows.shadowOf(Looper.getMainLooper()).idle()
//        activityHolder.create().visible()
//        val curActivity = activityHolder.get()
//        val addCalc = curActivity.findViewById<Button>(R.id.newCalc)
//        val text = curActivity.findViewById<TextInputLayout>(R.id.input)
//        addCalc.performClick()
//        Shadows.shadowOf(Looper.getMainLooper()).idle()
//        val dialog: androidx.appcompat.app.AlertDialog = curActivity.getAlertDialog()
//        val okButton: Button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
//        text.editText!!.setText("11")
//        okButton.performClick()
//        shadowOf(getMainLooper()).idle()
//        val res = curActivity.holderImpl!!.getCurrentItems()[0]
//        TestCase.assertTrue(res.getStatus())
//    }
}
