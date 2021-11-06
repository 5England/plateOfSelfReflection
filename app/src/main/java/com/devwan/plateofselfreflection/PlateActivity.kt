package com.devwan.plateofselfreflection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.devwan.plateofselfreflection.databinding.ActivityPlateBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*

class PlateActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPlateBinding
    private val firestoreRepo : FirestoreRepository = FirestoreRepository()
    private var isLiked : Boolean = false
    private var like : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlateBinding.inflate(layoutInflater)

        val snapshotId : String = intent.getStringExtra("snapshotId") as String

        refreshPlate(snapshotId)
        initBtnLikeClickListener(snapshotId)

        binding.btnFinishActivity.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        intent.putExtra("newLike", binding.textViewLike.text.toString())
    }

    private fun refreshPlate(snapshotId : String){

        binding.apply {

            GlobalScope.launch(Dispatchers.Main) {

                val plate: DocumentSnapshot? = firestoreRepo.getPlate(snapshotId)
                plate?.apply {

                    textViewNickname.text = plate["nickName"] as String
                    textViewUploadTime.text = Plate.getUploadTimeText((plate["uploadTime"] as Timestamp).toDate())
                    textViewTitle.text = plate["title"] as String
                    textViewMainText.text = plate["mainText"] as String
                    like = plate["like"] as Long
                    textViewLike.text = like.toString()
                    if (plate["isOvercome"] as Boolean) {
                        imageViewIsOvercome.setImageResource(R.drawable.cardplate_icon_isovercome_true)
                        textViewIsOvercomeMessage.text = "개선이 된 반성이에요."
                        textViewFeedBack.text = plate["feedBack"] as String
                    }
                    val likeUidMap : Map<String, Boolean> = plate["likeUidMap"] as Map<String, Boolean>
                    isLiked = if(likeUidMap.containsKey(firestoreRepo.getUid())){
                        likeUidMap[firestoreRepo.getUid()] as Boolean
                    }else{
                        false
                    }
                    if(isLiked){
                        btnLike.setImageResource(R.drawable.plateactivity_icon_liked)
                    }
                }
                setContentView(binding.root)
            }
        }
    }

    private fun initBtnLikeClickListener(snapshotId : String){
        binding.apply {
            btnLike.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    firestoreRepo.likePlate(snapshotId)
                }
                if(isLiked){
                    btnLike.setImageResource(R.drawable.plateactivity_icon_notliked)
                    textViewLike.text = (--like).toString()
                }else{
                    btnLike.setImageResource(R.drawable.plateactivity_icon_liked)
                    textViewLike.text = (++like).toString()
                }
                isLiked = !isLiked
            }
        }
    }
}

