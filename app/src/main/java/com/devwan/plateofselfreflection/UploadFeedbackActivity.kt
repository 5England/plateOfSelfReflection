package com.devwan.plateofselfreflection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.devwan.plateofselfreflection.databinding.ActivityUploadFeedbackBinding

class UploadFeedbackActivity : AppCompatActivity() {

    private lateinit var binding:ActivityUploadFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBtnUploadFeedbackClickListener()

        binding.btnFinishActivity.setOnClickListener {
            Toast.makeText(this, "나중에 다시 작성할 수 있어요", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initBtnUploadFeedbackClickListener(){
        binding.btnUploadFeedback.setOnClickListener {
            if( binding.btnUploadFeedback.text.isNotBlank()) {
                lateinit var snapshotId : String
                intent.getStringExtra("snapshotId")?.let {
                    snapshotId = it
                }
                val fireStoreRepo : FirestoreRepository = FirestoreRepository()
                fireStoreRepo.uploadFeedback(snapshotId, binding.editTextFeedback.text.toString())
                finish()
            }else{
                Toast.makeText(this, "후기를 작성해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}