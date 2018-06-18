package com.mithuroy.camcrop

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mithuroy.camtocrop.CameraActivity
import com.mithuroy.camtocrop.CameraActivity.Companion.IMAGE_PATH
import com.mithuroy.camtocrop.CameraActivity.Companion.REQUEST_IMAGE_PATH
import com.mithuroy.camtocrop.GalleryActivity
import com.mithuroy.camtocrop.GalleryActivity.Companion.REQUEST_GALLERY_IMAGE
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnTakePhoto.setOnClickListener { CameraActivity().start(this) }

        btnOpenGallery.setOnClickListener { GalleryActivity().start(this) }

//        startFragment()
    }

    private fun startFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, MainFragment())
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_IMAGE_PATH -> setImage(data.getStringExtra(IMAGE_PATH))
                REQUEST_GALLERY_IMAGE -> setImage(data.getStringExtra(GalleryActivity.IMAGE_PATH))
            }
        }
    }

    private fun setImage(imagePath: String) {
        imageView.setImageURI(Uri.fromFile(File(imagePath)))
    }
}
