package com.devwan.plateofselfreflection

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devwan.plateofselfreflection.databinding.ActivityUploadPlateBinding

class UpdatePlateActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUploadPlateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPlateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initBtnUploadPlateClickListener()

        binding.btnFinishActivity.setOnClickListener {
            finish()
        }
    }

    private fun initView(){
        binding.textViewUploadType.text = "반성문 수정"
        binding.editTextTitle.setText(intent.getStringExtra("snapshotTitle").toString())
        binding.editTextMainText.setText(intent.getStringExtra("snapshotMainText").toString())
    }

    private fun initBtnUploadPlateClickListener(){
        binding.btnUploadPlate.setOnClickListener {
            if( binding.editTextTitle.text.isNotBlank() && binding.editTextMainText.text.isNotBlank()) {
                val fireStoreRepo : FirestoreRepository = FirestoreRepository()
                fireStoreRepo.updatePlate(intent.getStringExtra("snapshotId").toString(),
                    binding.editTextTitle.text.toString(),
                    binding.editTextMainText.text.toString()
                )
                finish()
            }else{
                Toast.makeText(this, "제목, 내용을 모두 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}