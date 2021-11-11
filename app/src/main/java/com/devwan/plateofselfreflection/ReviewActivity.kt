package com.devwan.plateofselfreflection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.devwan.plateofselfreflection.databinding.ActivityReviewBinding
import com.devwan.plateofselfreflection.databinding.ActivityUploadPlateBinding

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFinishActivity.setOnClickListener {
            finish()
        }

        binding.btnReview.setOnClickListener {

        }
    }
}