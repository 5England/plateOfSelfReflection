package com.devwan.plateofselfreflection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.devwan.plateofselfreflection.databinding.ActivityUploadPlateBinding
import kotlinx.coroutines.*

class UploadPlateActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUploadPlateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPlateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category : String = intent.getStringExtra("category").toString()

        initBtnUploadPlateClickListener(category)

        initBtnFinishActivity()
    }

    private fun initBtnUploadPlateClickListener(category : String){
        binding.btnUploadPlate.setOnClickListener {
            if( binding.editTextTitle.text.isNotBlank() && binding.editTextMainText.text.isNotBlank()) {

                val newPlate = Plate(
                    title = binding.editTextTitle.text.toString(),
                    category = category,
                    mainText = binding.editTextMainText.text.toString(),
                    uploadTime = com.google.firebase.Timestamp.now()
                )

                val firebaseRepo : FirebaseRepo = FirebaseRepo()
                GlobalScope.launch(Dispatchers.Main) {
                    firebaseRepo.uploadPlate(newPlate)
                    setResult(RESULT_OK, intent)
                    finish()
                }

            }else{
                Toast.makeText(this, "제목, 내용을 모두 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initBtnFinishActivity(){
        binding.btnFinishActivity.setOnClickListener {
            finish()
        }
    }
}