package com.devwan.plateofselfreflection

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.devwan.plateofselfreflection.databinding.ActivityPlateBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*


class PlateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlateBinding
    private val firebaseRepo: FirebaseRepo = FirebaseRepo()
    private var isLiked: Boolean = false
    private var like: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlateBinding.inflate(layoutInflater)

        val snapshotId: String = intent.getStringExtra("snapshotId").toString()

        refreshPlate(snapshotId)

        initBtnLike(snapshotId)

        initBtnUploadComment(snapshotId)

        initBtnFinishActivity()

        setResult(RESULT_OK, intent)
    }

    private fun refreshPlate(snapshotId: String) {
        binding.apply {
            GlobalScope.launch(Dispatchers.Main) {
                val plate: DocumentSnapshot? = firebaseRepo.getPlate(snapshotId)
                val commentList : List<DocumentSnapshot> = firebaseRepo.getCommentList(snapshotId)
                plate?.apply {
                    textViewNickname.text = plate["nickName"] as String
                    if (textViewNickname.text.toString().length >= 4) {
                        textViewNickname.textSize = 16.0f
                        textViewDish.textSize = 16.0f
                    }
                    textViewUploadTime.text =
                        Plate.getUploadTimeText((plate["uploadTime"] as Timestamp).toDate())
                    textViewTitle.text = plate["title"] as String
                    textViewMainText.text = plate["mainText"] as String
                    like = plate["like"] as Long
                    textViewLike.text = like.toString()

                    if (plate["isOvercome"] as Boolean) {
                        imageViewIsOvercome.setImageResource(R.drawable.icon_cardplate_isovercome_true)
                        textViewIsOvercomeMessage.text = "개선된 반성이에요."
                        textViewFeedBack.text = plate["feedBack"] as String
                    }

                    val likeUidMap: Map<String, Boolean> =
                        plate["likeUidMap"] as Map<String, Boolean>
                    isLiked = if (likeUidMap.containsKey(firebaseRepo.getUid())) {
                        likeUidMap[firebaseRepo.getUid()] as Boolean
                    } else {
                        false
                    }

                    if (isLiked) {
                        btnLike.setImageResource(R.drawable.icon_plateactivity_liked_true)
                    }

                    if(commentList.isNotEmpty()){
                        textViewNoComment.visibility = View.GONE
                        initListViewComment(commentList)
                    }
                }
                setContentView(binding.root)
            }
        }
    }

    private fun initListViewComment(commentList: List<DocumentSnapshot>) {
        val baseLayout : LinearLayout = binding.layoutCommentList
        baseLayout.removeAllViews()
        val inflater = baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val newCommentList : List<DocumentSnapshot> = commentList.sortedBy { (it["uploadTime"] as Timestamp).toDate() }

        newCommentList.forEach { document ->
            val commentLayout : View = inflater.inflate(R.layout.card_comment,null)
            baseLayout.addView(commentLayout)
            commentLayout.findViewById<TextView>(R.id.textView_commentText).text = document["comment"] as String
            commentLayout.findViewById<TextView>(R.id.textView_commentNickname).text = document["nickName"] as String
            commentLayout.findViewById<TextView>(R.id.textView_commentUploadTime).text = Plate.getUploadTimeText((document["uploadTime"] as Timestamp).toDate())
        }
    }

    private fun initBtnLike(snapshotId: String) {
        binding.apply {
            btnLike.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    firebaseRepo.likePlate(snapshotId)
                }
                if (isLiked) {
                    btnLike.setImageResource(R.drawable.icon_plateactivity_liked_false)
                    textViewLike.text = (--like).toString()
                } else {
                    btnLike.setImageResource(R.drawable.icon_plateactivity_liked_true)
                    textViewLike.text = (++like).toString()
                }
                isLiked = !isLiked
            }
        }
    }

    private fun initBtnUploadComment(snapshotId: String) {
        binding.apply {
            btnUploadComment.setOnClickListener {
                if (editTextComment.text.isNotBlank()) {
                    GlobalScope.launch(Dispatchers.Main) {
                        firebaseRepo.uploadComment(snapshotId, editTextComment.text.toString(), Timestamp.now())
                        val commentList : List<DocumentSnapshot> = firebaseRepo.getCommentList(snapshotId)
                        textViewNoComment.visibility = View.GONE
                        initListViewComment(commentList)

                        editTextComment.text.clear()
                        val manager: InputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        manager.hideSoftInputFromWindow(
                            currentFocus!!.windowToken,
                            InputMethodManager.HIDE_NOT_ALWAYS
                        )

                        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }
                } else {
                    Toast.makeText(baseContext, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initBtnFinishActivity() {
        binding.btnFinishActivity.setOnClickListener {
            finish()
        }
    }
}