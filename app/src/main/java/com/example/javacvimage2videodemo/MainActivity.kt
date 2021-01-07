package com.example.javacvimage2videodemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.javacvimage2videodemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)
        binding.act = this
        checkPermissionAndRequest(this)
    }
    public fun checkPermissionAndRequest(context: Activity){
        val list: MutableList<String> = arrayListOf();
        val permissions: List<String> = arrayListOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissions.forEach { str ->
            if (ActivityCompat.checkSelfPermission(context, str) != PackageManager.PERMISSION_GRANTED) {
                list.add(str)
            }
        }
        if (list.size > 0) {
            ActivityCompat.requestPermissions(context, list.toTypedArray(), 10001)
        }
    }

    fun image2Video(){
        startActivity(Intent(this,ImageToVideoActivity::class.java))
    }

}