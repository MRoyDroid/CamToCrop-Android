package com.mithuroy.camtocrop

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.mithuroy.camtocrop.util.generateRandomNumber
import com.yalantis.ucrop.UCrop
import java.io.File

/**
 * Created by Mithu on June'08 2018
 */

class GalleryActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_GALLERY = 1005
        const val REQUEST_GALLERY_IMAGE = 1006
        const val IMAGE_PATH = "IMAGE_PATH"
    }

    @JvmOverloads
    fun start(activity: Activity, requestCode: Int = REQUEST_GALLERY_IMAGE) {
        activity.startActivityForResult(Intent(activity, GalleryActivity::class.java), requestCode)
    }

    @JvmOverloads
    fun start(fragment: Fragment, requestCode: Int = REQUEST_GALLERY_IMAGE) {
        fragment.startActivityForResult(Intent(fragment.activity, GalleryActivity::class.java), requestCode)
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .setType("image/*")
            .addCategory(Intent.CATEGORY_OPENABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val mimeTypes = arrayOf("image/jpeg")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }

        startActivityForResult(
            Intent.createChooser(
                intent,
                "Choose Image"
            ), REQUEST_GALLERY
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startGallery()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    val uCrop = UCrop.of(
                        data.data,
                        Uri.fromFile(
                            File(
                                cacheDir,
                                "${generateRandomNumber()}${generateRandomNumber()}.jpg"
                            )
                        )
                    )
                    uCrop.withAspectRatio(1f, 1f)
                    uCrop.start(this, REQUEST_GALLERY_IMAGE)
                }
                REQUEST_GALLERY_IMAGE -> {
                    val uri = UCrop.getOutput(data)
                    intent.putExtra(IMAGE_PATH, uri?.path)
                    setResult(Activity.RESULT_OK, intent)
                    finish()

                }
            }
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}