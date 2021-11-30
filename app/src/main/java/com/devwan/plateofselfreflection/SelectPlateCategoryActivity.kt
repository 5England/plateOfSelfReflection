package com.devwan.plateofselfreflection

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.devwan.plateofselfreflection.databinding.ActivityReviewBinding
import com.devwan.plateofselfreflection.databinding.ActivitySelectPlateCategoryBinding

class SelectPlateCategoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySelectPlateCategoryBinding
    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectPlateCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getResult = getActivityResultLauncher()

        initBtnFinishActivity()

        initBtnSelectCategory()
    }

    private fun getActivityResultLauncher() : ActivityResultLauncher<Intent>{
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun initBtnFinishActivity(){
        binding.btnFinishActivity.setOnClickListener {
            finish()
        }
    }

    private fun initBtnSelectCategory(){
        binding.apply {
            initSetOnClickListener(btnFree)
            initSetOnClickListener(btnWork)
            initSetOnClickListener(btnLife)
            initSetOnClickListener(btnPersonal)
            initSetOnClickListener(btnHealth)
            initSetOnClickListener(btnSpend)
        }
    }

    private fun initSetOnClickListener(categoryBtn : TextView){
        categoryBtn.setOnClickListener {
            val intent = Intent(this, UploadPlateActivity::class.java)
            if(categoryBtn.text == "자유"){
                getResult.launch(intent.putExtra("category", ""))
            }else{
                getResult.launch(intent.putExtra("category", categoryBtn.text.toString()))
            }
        }
    }
}