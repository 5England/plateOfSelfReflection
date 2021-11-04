package com.devwan.plateofselfreflection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.devwan.plateofselfreflection.databinding.ActivityUploadPlateBinding
import java.sql.Timestamp

class UploadPlateActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUploadPlateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPlateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBtnUploadPlateClickListener()

        binding.btnFinishActivity.setOnClickListener {
            finish()
        }
    }

    private fun initBtnUploadPlateClickListener(){
        binding.btnUploadPlate.setOnClickListener {
            if( binding.editTextTitle.text.isNotBlank() && binding.editTextMainText.text.isNotBlank()) {

                val newPlate = Plate(
                    title = binding.editTextTitle.text.toString(),
                    mainText = binding.editTextMainText.text.toString(),
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