package com.devwan.plateofselfreflection

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

class MyPlateAdapter(private val mContext: Context, private var plateList: List<DocumentSnapshot>,
                     private val onClickIsOvercome : (plate : DocumentSnapshot) -> Unit,
                     private val onClickDelete : (plate : DocumentSnapshot) -> Unit) :
    RecyclerView.Adapter<MyPlateAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var isOvercome: ImageView? = null
        var title: TextView? = null
        var mainText: TextView? = null
        var uploadTime: TextView? = null
        var like: TextView? = null
        var cardView : LinearLayout? = null
        var nickName : TextView? = null
        var category : TextView? = null

        init {
            isOvercome = view.findViewById(R.id.img_isOvercome)
            title = view.findViewById(R.id.text_title)
            mainText = view.findViewById(R.id.text_mainText)
            uploadTime = view.findViewById(R.id.text_uploadTime)
            like = view.findViewById(R.id.plate_like)
            cardView = view.findViewById(R.id.layout_cardView)
            nickName = view.findViewById(R.id.text_nickName)
            category = view.findViewById(R.id.text_category)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_plate, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        bindData(viewHolder, plateList[position])
    }

    override fun getItemCount() = plateList.size

    private fun bindData(viewHolder: ViewHolder, plateSnapshot: DocumentSnapshot) {
        val title: String = plateSnapshot["title"].toString() ?: ""
        val mainText: String = plateSnapshot["mainText"].toString() ?: ""
        val like: String = plateSnapshot["like"].toString() ?: ""
        val uploadTime: String = Plate.getUploadTimeText((plateSnapshot["uploadTime"] as Timestamp).toDate())
        val isOvercome: Boolean = plateSnapshot["isOvercome"] as Boolean
        val nickName : String = plateSnapshot["nickName"].toString()
        val category : String = plateSnapshot["category"].toString()

        viewHolder.apply {
            if(title.length >= 16){
                this.title?.text = title.substring(0, 15) + ".."
            }else{
                this.title?.text = title
            }
            this.mainText?.text = mainText
            this.like?.text = like
            this.uploadTime?.text = uploadTime
            this.nickName?.text = nickName

            if (isOvercome){
                this.isOvercome?.setImageResource(R.drawable.icon_cardplate_isovercome_true)
            } else {
                this.isOvercome?.setImageResource(R.drawable.icon_cardplate_isovercome_false)
            }

            this.isOvercome?.setOnClickListener{
                initIsOvercomeAlertDialog(plateSnapshot)
            }

            this.cardView?.setOnLongClickListener {
                initUpdateAlertDialog(plateSnapshot)
                true
            }

            this.cardView?.setOnClickListener{
                val intent = Intent(mContext, PlateActivity::class.java)
                mContext.startActivity(intent.putExtra("snapshotId", plateSnapshot.id))
            }
            this.category?.text = category
        }
    }

    private fun initIsOvercomeAlertDialog(plateSnapshot : DocumentSnapshot){
        val dlg = AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
        if(plateSnapshot["isOvercome"] as Boolean){
            dlg.apply {
                setTitle("반성이 부족하셨나요?")
                setMessage("원하면 반성을 취소할 수 있어요.                 ")
                setPositiveButton("아니요", DialogInterface.OnClickListener { dialog, which -> })
                setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                    onClickIsOvercome.invoke(plateSnapshot)
                })
                show()
            }
        }else{
            onClickIsOvercome.invoke(plateSnapshot)
            dlg.apply {
                setTitle("개선 후기 작성")
                setMessage("반성 개선에 대한 후기를 작성해 사람들에게 도움을 줄 수 있어요.")
                setPositiveButton("괜찮아요", DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(mContext, "나중에 다시 작성하실 수 있어요.", Toast.LENGTH_SHORT).show()
                })
                setNegativeButton("작성", DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(mContext, UploadFeedbackActivity::class.java)
                    intent.putExtra("snapshotId", plateSnapshot.id)
                    mContext.startActivity(intent)
                })
                show()
            }
        }
    }

    private fun initUpdateAlertDialog(plateSnapshot : DocumentSnapshot){
        val dlg = AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
        dlg.apply {
            setTitle("수정/삭제")
            setMessage("수정 및 삭제 시, 복구할 수 없어요.                         ")
            setPositiveButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                onClickDelete.invoke(plateSnapshot)
            })
            setNegativeButton("수정", DialogInterface.OnClickListener { dialog, which ->
                val intent = Intent(mContext, UpdatePlateActivity::class.java).apply {
                    putExtra("snapshotId", plateSnapshot.id)
                    putExtra("snapshotTitle", plateSnapshot["title"].toString())
                    putExtra("snapshotMainText", plateSnapshot["mainText"].toString())
                }
                mContext.startActivity(intent)
            })
            show()
        }
    }

    fun setData(newData: List<DocumentSnapshot>) {
        plateList = newData.sortedByDescending { (it["uploadTime"] as Timestamp).toDate() }
        notifyDataSetChanged()
    }
}
