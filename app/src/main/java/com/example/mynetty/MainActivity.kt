package com.example.mynetty

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import com.example.mynetty.netty.NettyService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    public fun start(view: View) {
        startService(Intent(this, NettyService::class.java))
    }

    public fun stop(view: View) {
        stopService(Intent(this, NettyService::class.java))
    }
}