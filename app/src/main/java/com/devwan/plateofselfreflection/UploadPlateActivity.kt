package com.devwan.plateofselfreflection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import java.sql.Timestamp

class UploadPlateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_plate)

        val editTitle = findViewById<EditText>(R.id.edit_title)
        val editMainText = findViewById<EditText>(R.id.edit_main_text)
        val btnUploadPlate = findViewById<TextView>(R.id.btn_upload_plate)
        val btnFinishActivity = findViewById<ImageButton>(R.id.btn_finish_activity)

        btnFinishActivity.setOnClickListener {
            finish()
        }

        btnUploadPlate.setOnClickListener {
            if( editTitle.text.isNotBlank() && editMainText.text.isNotBlank()) {

                val newPlate = Plate(
                    title = editTitle.text.toString(),
                    mainText = editMainText.text.toString(),
                    uploadTimestamp = com.google.firebase.Timestamp.now()
                )

                val fireStoreRepo : FirestoreRepository = FirestoreRepository()
                fireStoreRepo.uploadPlate(newPlate)

                finish()

            }else{
                Toast.makeText(this, "제목, 내용을 모두 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}