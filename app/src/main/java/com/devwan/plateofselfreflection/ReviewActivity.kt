package com.devwan.plateofselfreflection

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devwan.plateofselfreflection.databinding.ActivityReviewBinding


class ReviewActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBtnFinishActivity()

        initBtnReview()
    }

    private fun initBtnFinishActivity(){
        binding.btnFinishActivity.setOnClickListener {
            finish()
        }
    }

    private fun initBtnReview(){
        binding.btnReview.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.devwan.plateofselfreflection")
            startActivity(intent)
        }
    }
}