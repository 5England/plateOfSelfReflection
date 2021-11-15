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

                    val commentList: ArrayList<String> = plate["commentList"] as ArrayList<String>
                    if (commentList.isNotEmpty()) {
                        textViewNoComment.visibility = View.GONE
                        initListViewComment(listViewComment, commentList)
                    }
                }
                setContentView(binding.root)
            }
        }
    }

    private fun initListViewComment(listViewComment: ListView, newCommentList: ArrayList<String>) {
        listViewComment.adapter = PlateCommentAdapter(
            newCommentList, LayoutInflater.from(this@PlateActivity)
        )
        setListViewHeightBasedOnItems(listViewComment)
    }

    private fun setListViewHeightBasedOnItems(listView: ListView): Boolean {
        val listAdapter = listView.adapter
        return if (listAdapter != null) {
            val numberOfItems = listAdapter.count

            // Get total height of all items.
            var totalItemsHeight = 0
            for (itemPos in 0 until numberOfItems) {
                val item = listAdapter.getView(itemPos, null, listView)
                val px = 500 * listView.resources.displayMetrics.density
                item.measure(
                    View.MeasureSpec.makeMeasureSpec(px.toInt(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                totalItemsHeight += item.measuredHeight
            }

            // Get total height of all item dividers.
            val totalDividersHeight = listView.dividerHeight * (numberOfItems)
            // Get padding
            val totalPadding = listView.paddingTop + listView.paddingBottom

            // Set list height.
            val params = listView.layoutParams
            params.height = totalItemsHeight + totalDividersHeight + totalPadding
            listView.layoutParams = params
            listView.requestLayout()
            //setDynamicHeight(listView);
            true
        } else {
            false
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
                        firebaseRepo.uploadComment(snapshotId, editTextComment.text.toString())
                        editTextComment.text.clear()
                        val curSnapshot: DocumentSnapshot? = firebaseRepo.getPlate(snapshotId)

                        curSnapshot?.let {
                            (curSnapshot["commentList"] as List<String>)?.apply {
                                if (listViewComment.adapter == null) {
                                    textViewNoComment.visibility = View.GONE
                                    initListViewComment(listViewComment, ArrayList(this))
                                } else {
                                    initListViewComment(listViewComment, ArrayList(this))
                                }
                            }
                        }

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