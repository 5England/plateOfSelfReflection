package com.devwan.plateofselfreflection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.devwan.plateofselfreflection.databinding.ActivityReviewBinding
import com.google.android.play.core.review.ReviewManagerFactory

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
            val manager = ReviewManagerFactory.create(this)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // We got the ReviewInfo object
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener { _ ->
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                    }
                } else {
                    // There was some problem, log or handle the error code.
                }
            }
        }
    }
}