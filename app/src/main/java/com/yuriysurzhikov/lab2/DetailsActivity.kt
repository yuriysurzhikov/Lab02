package com.yuriysurzhikov.lab2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.yuriysurzhikov.lab2.model.EmailObject
import de.hdodenhof.circleimageview.CircleImageView

class DetailsActivity : AppCompatActivity() {

    private var emailObject: EmailObject? = null

    private lateinit var emailAddressInput: EditText
    private lateinit var subjectInput: EditText
    private lateinit var bodyInput: EditText

    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        emailAddressInput = findViewById(R.id.email_et)
        subjectInput = findViewById(R.id.subject_et)
        bodyInput = findViewById(R.id.message_et)
        submitButton = findViewById(R.id.button_confirm)
        cancelButton = findViewById(R.id.button_cancel)

        submitButton.setOnClickListener(submitListener)
        cancelButton.setOnClickListener(cancelListener)

        intent?.let {
            emailObject = it.getParcelableExtra(EMAIL_OBJECT_NAME)
            setupViews()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }

    private fun setupViews() {
        if (emailObject != null) {
            emailAddressInput.setText(emailObject?.address, TextView.BufferType.NORMAL)
            bodyInput.setText(emailObject?.body, TextView.BufferType.NORMAL)
            subjectInput.setText(emailObject?.subject, TextView.BufferType.NORMAL)
        }
    }

    private val submitListener = View.OnClickListener {
        val result = Intent()
        if (emailObject == null) {
            val email = EmailObject(
                emailAddressInput.text?.toString(),
                subjectInput.text?.toString(),
                bodyInput.text?.toString(),
                null
            )
            result.putExtra(EMAIL_OBJECT_NAME, email)
        } else {
            emailObject?.address = emailAddressInput.text?.toString()
            emailObject?.subject = subjectInput.text?.toString()
            emailObject?.body = bodyInput.text?.toString()
            result.putExtra(EMAIL_OBJECT_NAME, emailObject)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private val cancelListener = View.OnClickListener {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    companion object {
        const val EMAIL_OBJECT_NAME = "new_email_object"
    }
}