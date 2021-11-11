package com.devwan.plateofselfreflection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
        setResult(RESULT_OK, intent)

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

                    val commentList : ArrayList<String> = plate["commentList"] as ArrayList<String>
                    if(commentList.isNotEmpty()){
                        textViewNoComment.visibility = View.GONE
                        initListViewComment(listViewComment, commentList)
                    }
                }
                setContentView(binding.root)
            }
        }
    }

    private fun initListViewComment(listViewComment: ListView, newCommentlist: ArrayList<String>){
        listViewComment.adapter = CommentListAdapter(
            newCommentlist, LayoutInflater.from(this@PlateActivity)
        )
        updateListViewParams(listViewComment)
    }

    private fun updateListViewParams(listViewComment: ListView){
        var totalHeight = 0
        for (i in 0 until listViewComment.adapter.getCount()) {
            val listItem: View = listViewComment.adapter.getView(i, null, listViewComment)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params: ViewGroup.LayoutParams = listViewComment.getLayoutParams()
        params.height = totalHeight + listViewComment.getDividerHeight() * (listViewComment.adapter.getCount() - 1)
        listViewComment.setLayoutParams(params)
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
                    GlobalScope.launch(Dispatchers.Main) {
                        firestoreRepo.uploadComment(snapshotId, editTextComment.text.toString())
                        editTextComment.text.clear()
                        val curSnapshot : DocumentSnapshot? = firestoreRepo.getPlate(snapshotId)

                        curSnapshot?.let {
                            (curSnapshot["commentList"] as List<String>)?.apply {
                                if(listViewComment.adapter == null){
                                    textViewNoComment.visibility = View.GONE
                                    initListViewComment(listViewComment, ArrayList(this))
                                }else{
                                    (listViewComment.adapter as CommentListAdapter).setData(ArrayList(this))
                                    updateListViewParams(listViewComment)
                                }
                            }
                        }

                        val manager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        manager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }
                } else {
                    Toast.makeText(baseContext, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

class CommentListAdapter(
    private var commentList: ArrayList<String>,
    private val layoutInflater: LayoutInflater
) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.card_comment, null)
            holder = ViewHolder()
            holder.comment = view.findViewById(R.id.textView_commentText)
            view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }
        holder.comment?.setText(commentList.get(position))

        return view
    }


    override fun getItem(position: Int): Any {
        // 그리고자 하는 아이템 리스트의 하나(포지션에 해당하는)
        return commentList.get(position)
    }

    override fun getItemId(position: Int): Long {
        // 해당 포지션에 위치해 있는 아팀뷰의 아이디 설정
        return position.toLong()
    }

    override fun getCount(): Int {
        // 그리고자 하는 아이템 리스트의 전체 갯수
        return commentList.size
    }

    fun setData(newData: ArrayList<String>) {
        commentList = newData
        notifyDataSetChanged()
    }
}

class ViewHolder {
    var comment : TextView? = null
}