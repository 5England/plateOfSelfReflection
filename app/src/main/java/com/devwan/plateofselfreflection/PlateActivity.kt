package com.devwan.plateofselfreflection

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import com.devwan.plateofselfreflection.databinding.ActivityPlateBinding
import kotlinx.coroutines.*

class PlateActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPlateBinding
    val firestoreRepo : FirestoreRepository = FirestoreRepository()
    private var isLiked : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val snapshotId : String = intent.getStringExtra("snapshotId") as String
        isLiked = intent.getBooleanExtra("isLiked", false)

        initView(snapshotId)
        initBtnLikeClickListener(snapshotId)

        binding.btnFinishActivity.setOnClickListener {
            finish()
        }

        //isLiked(Boolean) + like(Long) 을 가지고 와야함
        //1. 해당 뷰에 들어왔을 때
        //2. 좋아요를 눌렀을 때
    }

    override fun onDestroy() {
        super.onDestroy()

        intent.putExtra("newLike", "z")
    }

    private fun initView(snapshotId : String){
        binding.apply {
            textViewNickname.text = intent.getStringExtra("nickName")
            textViewUploadTime.text = intent.getStringExtra("uploadTime")
            textViewTitle.text = intent.getStringExtra("title")
            textViewMainText.text = intent.getStringExtra("mainText")

            GlobalScope.launch(Dispatchers.Main) {
                textViewLike.text = firestoreRepo.getLike(snapshotId).toString()
            }

            if(intent.getBooleanExtra("isOvercome", false)){
                imageViewIsOvercome.setImageResource(R.drawable.cardplate_icon_isovercome_true)
                textViewIsOvercomeMessage.text = "개선이 된 반성이에요."
                textViewFeedBack.text = intent.getStringExtra("feedBack")
            }

            if(isLiked){
                btnLike.setImageResource(R.drawable.plateactivity_icon_liked)
            }
        }
    }

    private fun initBtnLikeClickListener(snapshotId : String){
        binding.apply {
            btnLike.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    firestoreRepo.likePlate(snapshotId)
                    textViewLike.text = firestoreRepo.getLike(snapshotId).toString()
                }
                if(isLiked){
                    btnLike.setImageResource(R.drawable.plateactivity_icon_notliked)
                }else{
                    btnLike.setImageResource(R.drawable.plateactivity_icon_liked)
                }
                isLiked = !isLiked
            }
        }
    }
}

