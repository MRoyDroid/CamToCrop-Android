package com.mithuroy.camtocrop

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
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
        private const val CROP_ENABLED = "CROP_ENABLED"
        private const val TOOLBAR_COLOR = "TOOLBAR_COLOR"
        private const val STATUS_BAR_COLOR = "STATUS_BAR_COLOR"
        private const val RATIO_X = "RATIO_X"
        private const val RATIO_Y = "RATIO_Y"
        const val IMAGE_PATH = "IMAHE_PATH"
    }

    private var x = 1f
    private var y = 1f
    private var toolbarColor = android.R.color.holo_blue_bright
    private var statusBarColor = android.R.color.holo_blue_dark
    private var isCropEnabled = true

    @JvmOverloads
    fun start(activity: Activity, requestCode: Int = REQUEST_GALLERY_IMAGE) {
        val intent = getIntent(activity)
        activity.startActivityForResult(intent, requestCode)
    }

    @JvmOverloads
    fun start(fragment: Fragment, requestCode: Int = REQUEST_GALLERY_IMAGE) {
        val intent = getIntent(fragment.context as Activity)
        fragment.startActivityForResult(intent, requestCode)
    }

    private fun getIntent(activity: Activity): Intent {
        val intent = Intent(activity, GalleryActivity::class.java)
        intent.putExtra(GalleryActivity.CROP_ENABLED, isCropEnabled)
        intent.putExtra(GalleryActivity.TOOLBAR_COLOR, toolbarColor)
        intent.putExtra(GalleryActivity.STATUS_BAR_COLOR, statusBarColor)
        intent.putExtra(GalleryActivity.RATIO_X, x)
        intent.putExtra(GalleryActivity.RATIO_Y, y)
        return intent
    }

    fun setAspectRatio(x: Float, y: Float): GalleryActivity {
        this.x = x
        this.y = y
        return this
    }

    fun setCropEnabled(isCropEnabled: Boolean): GalleryActivity {
        this.isCropEnabled = isCropEnabled
        return this
    }

    fun setToolbarColor(@ColorRes toolbarColor: Int): GalleryActivity {
        this.toolbarColor = toolbarColor
        return this
    }

    fun setStatusBarColor(@ColorRes statusBarColor: Int): GalleryActivity {
        this.statusBarColor = statusBarColor
        return this
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
                    startUCrop(data.data)
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

    private fun startUCrop(source: Uri) {
        val uCrop = UCrop.of(
            source,
            Uri.fromFile(
                File(
                    cacheDir,
                    "${generateRandomNumber()}${generateRandomNumber()}.jpg"
                )
            )
        )
        val options = UCrop.Options()
        options.setToolbarColor(
            ResourcesCompat.getColor(
                resources,
                intent.getIntExtra(GalleryActivity.TOOLBAR_COLOR, toolbarColor),
                theme
            )
        )
        options.setStatusBarColor(
            ResourcesCompat.getColor(
                resources,
                intent.getIntExtra(GalleryActivity.STATUS_BAR_COLOR, statusBarColor),
                theme
            )
        )
        options.setHideBottomControls(true)
        uCrop.withOptions(options)
        uCrop.withAspectRatio(
            intent.getFloatExtra(GalleryActivity.RATIO_X, x),
            intent.getFloatExtra(GalleryActivity.RATIO_Y, y)
        )
        uCrop.start(this, GalleryActivity.REQUEST_GALLERY_IMAGE)
    }
}