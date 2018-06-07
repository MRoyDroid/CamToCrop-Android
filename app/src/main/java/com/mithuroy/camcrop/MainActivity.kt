package com.mithuroy.camcrop

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.mithuroy.camtocrop.CameraActivity
import com.mithuroy.camtocrop.CameraActivity.Companion.IMAGE_PATH
import com.mithuroy.camtocrop.CameraActivity.Companion.REQUEST_IMAGE_PATH

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CameraActivity().start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_IMAGE_PATH -> Log.d("FilePath", data.getStringExtra(IMAGE_PATH))
            }
        }
    }
}
