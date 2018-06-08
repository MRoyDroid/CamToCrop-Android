package com.mithuroy.camtocrop

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.ColorRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class CameraActivity : AppCompatActivity() {

    companion object {
        private const val ACTIVITY_START_CAMERA_APP = 1000
        private const val ACTIVITY_START_UCROP = 1001
        private const val REQUEST_EXTERNAL_STORAGE_RESULT = 1002
        const val REQUEST_IMAGE_PATH = 1234
        private const val CROP_ENABLED = "CROP_ENABLED"
        private const val TOOLBAR_COLOR = "TOOLBAR_COLOR"
        private const val STATUS_BAR_COLOR = "STATUS_BAR_COLOR"
        private const val RATIO_X = "RATIO_X"
        private const val RATIO_Y = "RATIO_Y"
        const val IMAGE_PATH = "IMAGE_PATH"
    }

    private var x = 1f
    private var y = 1f
    private var toolbarColor = android.R.color.holo_blue_bright
    private var statusBarColor = android.R.color.holo_blue_dark
    private var isCropEnabled = true

    @JvmOverloads
    fun start(activity: Activity, requestCode: Int = REQUEST_IMAGE_PATH) {
        val intent = getIntent(activity)
        activity.startActivityForResult(intent, requestCode)
    }

    @JvmOverloads
    fun start(fragment: Fragment, requestCode: Int = REQUEST_IMAGE_PATH) {
        val intent = getIntent(fragment.context as Activity)
        fragment.startActivityForResult(intent, requestCode)
    }

    private fun getIntent(activity: Activity): Intent {
        val intent = Intent(activity, CameraActivity::class.java)
        intent.putExtra(CROP_ENABLED, isCropEnabled)
        intent.putExtra(TOOLBAR_COLOR, toolbarColor)
        intent.putExtra(STATUS_BAR_COLOR, statusBarColor)
        intent.putExtra(RATIO_X, x)
        intent.putExtra(RATIO_Y, y)
        return intent
    }

    fun setAspectRatio(x: Float, y: Float): CameraActivity {
        this.x = x
        this.y = y
        return this
    }

    fun setCropEnabled(isCropEnabled: Boolean): CameraActivity {
        this.isCropEnabled = isCropEnabled
        return this
    }

    fun setToolbarColor(@ColorRes toolbarColor: Int): CameraActivity {
        this.toolbarColor = toolbarColor
        return this
    }

    fun setStatusBarColor(@ColorRes statusBarColor: Int): CameraActivity {
        this.statusBarColor = statusBarColor
        return this
    }

    private var mImageFileLocation: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        onClickTakePhoto()
    }

    private fun onClickTakePhoto() {
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            callCameraApp()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(
                            this,
                            "External storage permission required to save images",
                            Toast.LENGTH_SHORT
                    ).show()
                }
                requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_EXTERNAL_STORAGE_RESULT
                )
            } else {
                callCameraApp()
            }
        }
    }

    private fun callCameraApp() {
        val callCameraApplicationIntent = Intent()
        callCameraApplicationIntent.action = MediaStore.ACTION_IMAGE_CAPTURE

        var photoFile: File? = null
        try {
            photoFile = createImageFile()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        val authorities = "${applicationContext.packageName}.fileprovider"
        if (photoFile != null) {
            val imageUri = FileProvider.getUriForFile(this, authorities, photoFile)
            callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP)
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val imageFileName = "${generateRandomNumber()}${generateRandomNumber()}"
        val storageDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        val image = File.createTempFile(imageFileName, ".jpg", storageDirectory)
        mImageFileLocation = image.absolutePath

        return image
    }

    private fun generateRandomNumber(): String {
        val instance: MessageDigest?
        var hexString: StringBuilder? = null
        try {
            instance = MessageDigest.getInstance("MD5")
            val messageDigest = instance!!.digest(System.nanoTime().toString().toByteArray())
            hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                val hex = Integer.toHexString(0xFF and aMessageDigest.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return hexString!!.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ACTIVITY_START_CAMERA_APP -> {
                    Log.i(IMAGE_PATH, mImageFileLocation)
                    if (intent.getBooleanExtra(CROP_ENABLED, isCropEnabled)) {
                        startUCrop(mImageFileLocation)
                    } else {
                        intent.putExtra(IMAGE_PATH, mImageFileLocation)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }

                ACTIVITY_START_UCROP -> {
                    if (data != null) {
                        val uri = UCrop.getOutput(data)
                        intent.putExtra(IMAGE_PATH, uri?.path)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE_RESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callCameraApp()
            } else {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun startUCrop(imagePath: String) {
        val uCrop = UCrop.of(Uri.fromFile(File(imagePath)), Uri.fromFile(File(imagePath)))
        val options = UCrop.Options()
        options.setToolbarColor(
                ResourcesCompat.getColor(
                        resources,
                        intent.getIntExtra(TOOLBAR_COLOR, toolbarColor),
                        theme
                )
        )
        options.setStatusBarColor(
                ResourcesCompat.getColor(
                        resources,
                        intent.getIntExtra(STATUS_BAR_COLOR, statusBarColor),
                        theme
                )
        )
        options.setHideBottomControls(true)
        uCrop.withOptions(options)
        uCrop.withAspectRatio(
                intent.getFloatExtra(RATIO_X, x),
                intent.getFloatExtra(RATIO_Y, y)
        )
        uCrop.start(this, ACTIVITY_START_UCROP)
    }

}
