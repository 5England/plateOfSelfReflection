package com.devwan.plateofselfreflection

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devwan.plateofselfreflection.databinding.ActivityPlateBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*


class PlateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlateBinding
    private val firestoreRepo: FirestoreRepository = FirestoreRepository()
    private var isLiked: Boolean = false
    private var like: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlateBinding.inflate(layoutInflater)

        val snapshotId: String = intent.getStringExtra("snapshotId") as String

        refreshPlate(snapshotId)
        initBtnLikeClickListener(snapshotId)
        initBtnUploadCommentClickListener(snapshotId)

        binding.btnFinishActivity.setOnClickListener {
            finish()
        }
    }

    private fun refreshPlate(snapshotId: String) {

        binding.apply {

            GlobalScope.launch(Dispatchers.Main) {

                val plate: DocumentSnapshot? = firestoreRepo.getPlate(snapshotId)
                plate?.apply {

                    textViewNickname.text = plate["nickName"] as String
                    textViewUploadTime.text =
                        Plate.getUploadTimeText((plate["uploadTime"] as Timestamp).toDate())
                    textViewTitle.text = plate["title"] as String
                    textViewMainText.text = plate["mainText"] as String
                    like = plate["like"] as Long
                    textViewLike.text = like.toString()
                    if (plate["isOvercome"] as Boolean) {
                        imageViewIsOvercome.setImageResource(R.drawable.cardplate_icon_isovercome_true)
                        textViewIsOvercomeMessage.text = "개선한 반성이에요."
                        textViewFeedBack.text = plate["feedBack"] as String
                    }
                    val likeUidMap: Map<String, Boolean> =
                        plate["likeUidMap"] as Map<String, Boolean>
                    isLiked = if (likeUidMap.containsKey(firestoreRepo.getUid())) {
                        likeUidMap[firestoreRepo.getUid()] as Boolean
                    } else {
                        false
                    }
                    if (isLiked) {
                        btnLike.setImageResource(R.drawable.plateactivity_icon_liked_true)
                    }
                }
                setContentView(binding.root)
            }
        }
    }

    private fun initBtnLikeClickListener(snapshotId: String) {
        binding.apply {
            btnLike.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    firestoreRepo.likePlate(snapshotId)
                }
                if (isLiked) {
                    btnLike.setImageResource(R.drawable.plateactivity_icon_liked_false)
                    textViewLike.text = (--like).toString()
                } else {
                    btnLike.setImageResource(R.drawable.plateactivity_icon_liked_true)
                    textViewLike.text = (++like).toString()
                }
                isLiked = !isLiked
            }
        }
    }

    private fun initBtnUploadCommentClickListener(snapshotId: String) {
        binding.apply {
            btnUploadComment.setOnClickListener {
                if (editTextComment.text.isNotBlank()) {
                    GlobalScope.launch(Dispatchers.IO) {
                        firestoreRepo.uploadComment(snapshotId, editTextComment.text.toString())
                        editTextComment.text.clear()
//                        firestoreRepo.getCommentList(snapshotId)
//                        listViewComment에 새로운 리스트 입력됐다고 notify
                    }

                } else {
                    Toast.makeText(baseContext, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}