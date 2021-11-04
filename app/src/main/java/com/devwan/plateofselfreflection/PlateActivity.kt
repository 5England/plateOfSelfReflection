package com.devwan.plateofselfreflection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.devwan.plateofselfreflection.databinding.ActivityPlateBinding

class PlateActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPlateBinding
    private var isLiked : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isLiked = intent.getBooleanExtra("isLiked", true)
        initView()
        initBtnLikeClickListener()
    }

    private fun initView(){
        binding.textViewNickname.text = intent.getStringExtra("nickName")
        binding.textViewUploadTime.text = intent.getStringExtra("uploadTime")
        binding.textViewTitle.text = intent.getStringExtra("title")
        binding.textViewMainText.text = intent.getStringExtra("mainText")
        if(intent.getBooleanExtra("isOvercome", false)){
            binding.imageViewIsOvercome.setImageResource(R.drawable.cardplate_icon_isovercome_true)
            binding.textViewIsOvercomeMessage.text = "개선이 된 반성이에요."
            binding.textViewFeedBack.text = intent.getStringExtra("feedBack")
        }
        binding.textViewLike.text = intent.getLongExtra("like", 0).toString()
        if(isLiked){
            binding.btnLike.setImageResource(R.drawable.plateactivity_icon_liked)
        }
    }

    private fun initBtnLikeClickListener(){
        binding.btnLike.setOnClickListener {
            val currentLike = binding.textViewLike.text.toString().toInt()
            if(isLiked){
                binding.btnLike.setImageResource(R.drawable.plateactivity_icon_notliked)
                binding.textViewLike.text = (currentLike - 1).toString()
            }else{
                binding.btnLike.setImageResource(R.drawable.plateactivity_icon_liked)
                binding.textViewLike.text = (currentLike + 1).toString()
            }
            isLiked = !isLiked
        }
    }
}