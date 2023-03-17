package com.mapd721.secretchat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mapd721.secretchat.R
import com.mapd721.secretchat.logic.StartupTasks

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StartupTasks.run()
    }
}