package com.devwan.plateofselfreflection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class UploadFeedbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_feedback)

        val editFeedbackText = findViewById<EditText>(R.id.edit_feedback_text)
        val btnUploadFeedback = findViewById<TextView>(R.id.btn_upload_feedback)
        val btnFinishActivity = findViewById<ImageButton>(R.id.btn_finish_feedback_activity)

        btnFinishActivity.setOnClickListener {
            Toast.makeText(this, "나중에 다시 작성할 수 있어요", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnUploadFeedback.setOnClickListener {
            if( editFeedbackText.text.isNotBlank()) {
                lateinit var snapshotId : String
                intent.getStringExtra("snapshotId")?.let {
                    snapshotId = it
                }
                val fireStoreRepo : FirestoreRepository = FirestoreRepository()
                fireStoreRepo.uploadFeedback(snapshotId, editFeedbackText.text.toString())
                finish()
            }else{
                Toast.makeText(this, "후기를 작성해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}