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

        init {
            isOvercome = view.findViewById(R.id.img_isOvercome)
            title = view.findViewById(R.id.text_title)
            mainText = view.findViewById(R.id.text_mainText)
            uploadTime = view.findViewById(R.id.text_uploadTime)
            like = view.findViewById(R.id.plate_like)
            cardView = view.findViewById(R.id.layout_cardView)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_plate, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val plate = plateList[position]
        bindData(viewHolder, plateList[position])
    }

    override fun getItemCount() = plateList.size

    fun setData(newData: List<DocumentSnapshot>) {
        plateList = newData.sortedByDescending { (it["uploadTime"] as Timestamp).toDate() }
        notifyDataSetChanged()
    }

    private fun bindData(viewHolder: ViewHolder, plateDocumentSnapshot: DocumentSnapshot) {
        val title: String = plateDocumentSnapshot["title"].toString() ?: ""
        val mainText: String = plateDocumentSnapshot["mainText"].toString() ?: ""
        val like: String = plateDocumentSnapshot["like"].toString() ?: ""
        val uploadTime: String = Plate.getUploadTimeText((plateDocumentSnapshot["uploadTime"] as Timestamp).toDate())
        val isOvercome: Boolean = plateDocumentSnapshot["isOvercome"] as Boolean

        viewHolder.title?.text = title
        viewHolder.mainText?.text = mainText
        viewHolder.like?.text = like
        viewHolder.uploadTime?.text = uploadTime
        if (isOvercome) viewHolder.isOvercome?.setImageResource(R.drawable.cardplate_icon_isovercome_true)
        else viewHolder.isOvercome?.setImageResource(R.drawable.cardplate_icon_isovercome_false)

        viewHolder.isOvercome?.setOnClickListener{
            val dlg = AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
            if(plateDocumentSnapshot["isOvercome"] as Boolean){
                dlg.apply {
                    setTitle("반성이 부족하셨나요?")
                    setMessage("원하면 반성을 취소할 수 있어요.                 ")
                    setPositiveButton("아니요", DialogInterface.OnClickListener { dialog, which -> })
                    setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                        onClickIsOvercome.invoke(plateDocumentSnapshot)
                    })
                    show()
                }
            }else{
                onClickIsOvercome.invoke(plateDocumentSnapshot)
                dlg.apply {
                    setTitle("극복 후기 작성")
                    setMessage("반성 극복에 대한 후기를 작성해 사람들에게 도움을 줄 수 있어요.")
                    setPositiveButton("괜찮아요", DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(mContext, "나중에 다시 작성하실 수 있어요.", Toast.LENGTH_SHORT).show()
                    })
                    setNegativeButton("작성", DialogInterface.OnClickListener { dialog, which ->
                        val intent = Intent(mContext, UploadFeedbackActivity::class.java)
                        intent.putExtra("snapshotId", plateDocumentSnapshot.id)
                        mContext.startActivity(intent)
                    })
                    show()
                }
            }
        }

        viewHolder.cardView?.setOnLongClickListener {
            val dlg = AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
            dlg.apply {
                setTitle("수정/삭제")
                setMessage("수정/삭제 시 복구할 수 없어요.                         ")
                setPositiveButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                    onClickDelete.invoke(plateDocumentSnapshot)
                })
                setNegativeButton("수정", DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(mContext, UpdatePlateActivity::class.java).apply {
                        putExtra("snapshotId", plateDocumentSnapshot.id)
                        putExtra("snapshotTitle", plateDocumentSnapshot["title"].toString())
                        putExtra("snapshotMainText", plateDocumentSnapshot["mainText"].toString())
                    }
                    mContext.startActivity(intent)
                })
                show()
            }
            true
        }

        viewHolder.cardView?.setOnClickListener{
            val intent = Intent(mContext, PlateActivity::class.java)
            mContext.startActivity(intent.putExtra("snapshotId", plateDocumentSnapshot.id))
        }
    }
}
