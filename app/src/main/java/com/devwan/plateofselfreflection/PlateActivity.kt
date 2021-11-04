package com.devwan.plateofselfreflection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.Timestamp

class PlateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plate)

        val viewNickName : TextView = findViewById(R.id.plate_nickname)
        val viewUploadTime : TextView = findViewById(R.id.plate_uploadTime)
        val viewTitle : TextView = findViewById(R.id.plate_title)
        val viewMainText : TextView = findViewById(R.id.plate_mainText)
        val viewIsOvercome : ImageView = findViewById(R.id.plate_isOvercome)
        val viewLike : TextView = findViewById(R.id.plate_like)
        val viewBtnLike : ImageButton = findViewById(R.id.btn_like)
        var isLiked : Boolean = intent.getBooleanExtra("islLiked", true)

        viewNickName.text = intent.getStringExtra("nickName")
        viewUploadTime.text = intent.getStringExtra("uploadTime")
        viewTitle.text = intent.getStringExtra("title")
        viewMainText.text = intent.getStringExtra("mainText")
        if(intent.getBooleanExtra("isOvercome", false)){
            viewIsOvercome.setImageResource(R.drawable.cardplate_icon_isovercome_true)
            val viewIsOvercomeMessage : TextView = findViewById(R.id.plate_isOvercomeMessage)
            val viewFeedBack : TextView = findViewById(R.id.plate_feedback)
            viewIsOvercomeMessage.text = "개선이 된 반성이에요."
            viewFeedBack.text = intent.getStringExtra("feedBack")
        }
        viewLike.text = intent.getLongExtra("like", 0).toString()
        if(isLiked){
            viewBtnLike.setImageResource(R.drawable.plateactivity_icon_liked)
        }

        viewBtnLike.setOnClickListener {
            val currentLike = viewLike.text.toString().toInt()
            if(isLiked){
                viewBtnLike.setImageResource(R.drawable.plateactivity_icon_notliked)
                viewLike.text = (currentLike - 1).toString()
            }else{
                viewBtnLike.setImageResource(R.drawable.plateactivity_icon_liked)
                viewLike.text = (currentLike + 1).toString()
            }
            isLiked = !isLiked
        }
    }
}