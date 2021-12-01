package com.devwan.plateofselfreflection

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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

        initBtnEnterKeyboard()

        initBtnUploadComment(snapshotId)

        initBtnFinishActivity()

        setResult(RESULT_OK, intent)
    }

    private fun refreshPlate(snapshotId: String) {
        binding.apply {
            GlobalScope.launch(Dispatchers.Main) {
                val plate: DocumentSnapshot? = firebaseRepo.getPlate(snapshotId)
                val commentList: List<DocumentSnapshot> = firebaseRepo.getCommentList(snapshotId)
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
                        btnLike.visibility = View.GONE
                        btnUnlike.visibility = View.VISIBLE
                    }else{
                        btnLike.visibility = View.VISIBLE
                        btnUnlike.visibility = View.GONE
                    }

                    if (commentList.isNotEmpty()) {
                        textViewNoComment.visibility = View.GONE
                        initListViewComment(commentList, snapshotId)
                    }else{
                        textViewNoComment.visibility = View.VISIBLE
                        layoutCommentList.visibility = View.GONE
                    }

                    if(plate["category"].toString().isNotBlank()){
                        textViewCategory.text = plate["category"].toString()
                        textViewCategory.visibility = View.VISIBLE
                    }
                }
                setContentView(binding.root)
            }
        }
    }

    private fun initListViewComment(commentList: List<DocumentSnapshot>, snapshotId: String) {
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
            if (FirebaseAuth.getInstance().uid == document["uid"] as String){
                initBtnDeleteMyComment(commentLayout, snapshotId, document)
            }
        }

        binding.layoutCommentList.visibility = View.VISIBLE
    }

    private fun initBtnDeleteMyComment(commentLayout : View, snapshotId : String, document : DocumentSnapshot){
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
                                    firebaseRepo.deleteMyComment(snapshotId, document.id)
                                    refreshPlate(snapshotId)
                                }
                            })
                        show()
                    }
                }
        }
    }

    private fun initBtnLike(snapshotId: String) {
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

    private fun initBtnEnterKeyboard(){
        binding.btnEnterkeyboard.setOnClickListener{
            binding.editTextComment.requestFocus()
	        val imm : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
	        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private fun initBtnUploadComment(snapshotId: String) {
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
                        initListViewComment(commentList, snapshotId)

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