package com.yuriysurzhikov.lab2

import android.R.attr
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.yuriysurzhikov.lab2.model.EmailObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private var emailObject: EmailObject? = EmailObject()

    private lateinit var textEmailTo: TextView
    private lateinit var textEmailSubject: TextView
    private lateinit var textEmailBody: TextView

    private lateinit var profileImage: ImageView

    private lateinit var cameraButton: Button
    private lateinit var detailsButton: Button
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textEmailTo = findViewById(R.id.textview_to)
        textEmailSubject = findViewById(R.id.textview_subject)
        textEmailBody = findViewById(R.id.textview_message)

        profileImage = findViewById(R.id.camera_image)

        cameraButton = findViewById(R.id.button_camera)
        cameraButton.setOnClickListener(openCameraListener)
        detailsButton = findViewById(R.id.button_details)
        detailsButton.setOnClickListener(openDetailsListener)
        sendButton = findViewById(R.id.button_send)
        sendButton.setOnClickListener(sendMailListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EMAIL_REQUEST_CODE -> {
                proceedEmailResult(resultCode, data)
            }
            CAMERA_REQUEST_CODE -> {
                proceedCameraResult(resultCode, data)
            }
        }
    }

    private fun proceedEmailResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            emailObject = data?.getParcelableExtra(DetailsActivity.EMAIL_OBJECT_NAME)
            refreshViews()
        } else {
            showSnackbar(detailsButton, getString(R.string.action_canceled))
        }
    }

    private fun proceedCameraResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            profileImage.setImageBitmap(imageBitmap)

            val outputFile = File.createTempFile("File_",".png")
            var fileOutputStream: FileOutputStream? = null
            try {
                fileOutputStream = FileOutputStream(outputFile)
                imageBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                emailObject?.imageUri = FileProvider
                    .getUriForFile(this, "${BuildConfig.APPLICATION_ID}.fileprovider", outputFile)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                fileOutputStream?.flush()
                fileOutputStream?.close()
            }
        } else {
            showSnackbar(cameraButton, getString(R.string.action_canceled))
        }
    }

    private fun showSnackbar(view: View, text: String) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
    }

    private fun refreshViews() {
        textEmailTo.text = String.format(getString(R.string.textview_to_text), emailObject?.address)
        textEmailBody.text = String.format(getString(R.string.textview_message_text), emailObject?.body)
        textEmailSubject.text = String.format(getString(R.string.textview_subject_text), emailObject?.subject)
    }

    private val openDetailsListener = View.OnClickListener {
        val detailsIntent = Intent(this, DetailsActivity::class.java).apply {
            putExtra(DetailsActivity.EMAIL_OBJECT_NAME, emailObject)
        }
        startActivityForResult(detailsIntent, EMAIL_REQUEST_CODE)
    }

    private val openCameraListener = View.OnClickListener {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            showSnackbar(cameraButton, getString(R.string.error_no_found_activity))
        }
    }

    private val sendMailListener = View.OnClickListener {
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailObject?.address))
                putExtra(Intent.EXTRA_SUBJECT, emailObject?.subject)
                putExtra(Intent.EXTRA_TEXT, emailObject?.body)
                putExtra(Intent.EXTRA_STREAM, emailObject?.imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(sendIntent, getString(R.string.email_send_text)))
        } catch (e: ActivityNotFoundException) {
            showSnackbar(sendButton, getString(R.string.error_no_found_activity))
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    companion object {
        const val EMAIL_REQUEST_CODE = 100
        const val CAMERA_REQUEST_CODE = 101
    }
}