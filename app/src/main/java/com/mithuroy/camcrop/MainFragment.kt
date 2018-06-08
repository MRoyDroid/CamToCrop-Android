package com.mithuroy.camcrop

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mithuroy.camtocrop.CameraActivity
import com.mithuroy.camtocrop.CameraActivity.Companion.IMAGE_PATH
import com.mithuroy.camtocrop.CameraActivity.Companion.REQUEST_IMAGE_PATH

/**
 * Created by Mithu on June'08 2018
 */

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_main, container, false)
        CameraActivity().start(this)
        return view
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