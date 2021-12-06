package com.devwan.plateofselfreflection

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.devwan.plateofselfreflection.databinding.ActivityPlateBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*


class PlateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlateBinding
    private val firebaseRepo: FirebaseRepo = FirebaseRepo()
    private var snapshotId: String = ""
    private var isLiked: Boolean = false
    private var like: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlateBinding.inflate(layoutInflater)

        snapshotId = intent.getStringExtra("snapshotId").toString()

        refreshPlate()

        initBtnLike()

        initBtnEnterKeyboard()

        initBtnUploadComment()

        initBtnFinishActivity()

        setResult(RESULT_OK, intent)
    }

    private fun refreshPlate() {
        GlobalScope.launch(Dispatchers.Main) {
            val snapshot: DocumentSnapshot? = firebaseRepo.getPlate(snapshotId)
            val commentList: List<DocumentSnapshot> = firebaseRepo.getCommentList(snapshotId)
            firebaseRepo.updateCommentNotice(snapshotId)
            snapshot?.let {
                setData(Plate.getPlate(it), commentList)
                setContentView(binding.root)
            }
        }
    }

    private fun setData(plate: Plate, commentList: List<DocumentSnapshot>) {
        binding.apply {
            textViewNickname.text = plate.nickName
            if (textViewNickname.text.toString().length >= 4) {
                textViewNickname.textSize = 16.0f
                textViewDish.textSize = 16.0f
            }
            textViewUploadTime.text =
                Plate.getUploadTimeText(plate.uploadTime.toDate())
            textViewTitle.text = plate.title
            textViewMainText.text = plate.mainText
            like = plate.like
            textViewLike.text = like.toString()

            if (plate.isOvercome) {
                imageViewIsOvercome.setImageResource(R.drawable.icon_cardplate_isovercome_true)
                textViewIsOvercomeMessage.text = "개선된 반성이에요."
                textViewFeedBack.text = plate.feedBack
            }

            plate.LikeUidMap.let {
                isLiked = if (it.containsKey(firebaseRepo.getUid())) {
                    it[firebaseRepo.getUid()] as Boolean
                } else {
                    false
                }
                if (isLiked) {
                    btnLike.visibility = View.GONE
                    btnUnlike.visibility = View.VISIBLE
                } else {
                    btnLike.visibility = View.VISIBLE
                    btnUnlike.visibility = View.GONE
                }
            }

            if (commentList.isNotEmpty()) {
                textViewNoComment.visibility = View.GONE
                initListViewComment(commentList)
            } else {
                textViewNoComment.visibility = View.VISIBLE
                layoutCommentList.visibility = View.GONE
            }

            if (plate.category.isNotBlank()) {
                textViewCategory.text = plate.category
                textViewCategory.visibility = View.VISIBLE
            }
        }
    }

    private fun initListViewComment(commentList: List<DocumentSnapshot>) {
        val baseLayout: LinearLayout = binding.layoutCommentList
        baseLayout.removeAllViews()
        val inflater =
            baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val newCommentList: List<DocumentSnapshot> =
            commentList.sortedBy { (it["uploadTime"] as Timestamp).toDate() }

        newCommentList.forEach { document ->
            val commentLayout: View = inflater.inflate(R.layout.card_comment, null)
            baseLayout.addView(commentLayout)
            commentLayout.findViewById<TextView>(R.id.textView_commentText).text =
                document["comment"] as String
            commentLayout.findViewById<TextView>(R.id.textView_commentNickname).text =
                document["nickName"] as String
            commentLayout.findViewById<TextView>(R.id.textView_commentUploadTime).text =
                Plate.getUploadTimeText((document["uploadTime"] as Timestamp).toDate())
            if (FirebaseAuth.getInstance().uid == document["uid"] as String) {
                initBtnDeleteMyComment(commentLayout, document)
            }
        }

        binding.layoutCommentList.visibility = View.VISIBLE
    }

    private fun initBtnDeleteMyComment(commentLayout: View, snapshot: DocumentSnapshot) {
        commentLayout.findViewById<TextView>(R.id.textView_deleteMyComment).let {
            it.visibility = View.VISIBLE
            it.setOnClickListener {
                val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
                dlg.apply {
                    setTitle("댓글 삭제")
                    setMessage("삭제하시겠어요?                                     ")
                    setPositiveButton(
                        "아니요",
                        DialogInterface.OnClickListener { dialog, which -> })
                    setNegativeButton(
                        "삭제",
                        DialogInterface.OnClickListener { dialog, which ->
                            GlobalScope.launch(Dispatchers.Main) {
                                firebaseRepo.deleteMyComment(snapshotId, snapshot.id)
                                refreshPlate()
                            }
                        })
                    show()
                }
            }
        }
    }

    private fun initBtnLike() {
        binding.apply {
            btnLike.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    firebaseRepo.likePlate(snapshotId)
                }
                btnLike.visibility = View.GONE
                btnUnlike.visibility = View.VISIBLE
                textViewLike.text = (++like).toString()
                isLiked = !isLiked
            }

            btnUnlike.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    firebaseRepo.likePlate(snapshotId)
                }
                btnLike.visibility = View.VISIBLE
                btnUnlike.visibility = View.GONE
                textViewLike.text = (--like).toString()
                isLiked = !isLiked
            }
        }
    }

    private fun initBtnEnterKeyboard() {
        binding.btnEnterkeyboard.setOnClickListener {
            binding.editTextComment.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            );
        }
    }

    private fun initBtnUploadComment() {
        binding.apply {
            btnUploadComment.setOnClickListener {
                if (editTextComment.text.isNotBlank()) {
                    GlobalScope.launch(Dispatchers.Main) {
                        firebaseRepo.uploadComment(
                            snapshotId,
                            editTextComment.text.toString(),
                            Timestamp.now()
                        )
                        val commentList: List<DocumentSnapshot> =
                            firebaseRepo.getCommentList(snapshotId)
                        textViewNoComment.visibility = View.GONE
                        initListViewComment(commentList)

                        editTextComment.text.clear()
                        val manager: InputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        manager.hideSoftInputFromWindow(
                            editTextComment.windowToken, 0
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