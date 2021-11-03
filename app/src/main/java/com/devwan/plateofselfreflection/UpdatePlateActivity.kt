package com.devwan.plateofselfreflection

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UpdatePlateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_plate)

        val editTitle = findViewById<EditText>(R.id.edit_title)
        val editMainText = findViewById<EditText>(R.id.edit_main_text)
        val btnUpdatePlate = findViewById<TextView>(R.id.btn_upload_plate)
        val btnFinishActivity = findViewById<ImageButton>(R.id.btn_finish_activity)
        val textUploadType = findViewById<TextView>(R.id.textView_uploadType)

        textUploadType.text = "반성문 수정"
        editTitle.setText(intent.getStringExtra("snapshotTitle").toString())
        editMainText.setText(intent.getStringExtra("snapshotMainText").toString())

        btnFinishActivity.setOnClickListener {
            finish()
        }

        btnUpdatePlate.setOnClickListener {
            if( editTitle.text.isNotBlank() && editMainText.text.isNotBlank()) {
                val fireStoreRepo : FirestoreRepository = FirestoreRepository()
                fireStoreRepo.uploadPlate(intent.getStringExtra("snapshotId").toString(),
                    editTitle.text.toString(),
                    editMainText.text.toString()
                )
                finish()
            }else{
                Toast.makeText(this, "제목, 내용을 모두 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}