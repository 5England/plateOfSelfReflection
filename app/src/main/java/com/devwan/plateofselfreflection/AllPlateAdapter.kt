package com.devwan.plateofselfreflection

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

class AllPlateAdapter(private val mContext: Context, private var allPlateList: List<DocumentSnapshot>,
                      private val getResult : ActivityResultLauncher<Intent>) :
    RecyclerView.Adapter<AllPlateAdapter.ViewHolder>() {

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
        bindData(viewHolder, allPlateList[position])
    }

    override fun getItemCount() = allPlateList.size

    private fun bindData(viewHolder: ViewHolder, plateDocumentSnapshot: DocumentSnapshot) {
        val title: String = plateDocumentSnapshot["title"].toString() ?: ""
        val mainText: String = plateDocumentSnapshot["mainText"].toString() ?: ""
        val like: String = plateDocumentSnapshot["like"].toString() ?: ""
        val uploadTime: String = Plate.getUploadTimeText((plateDocumentSnapshot["uploadTime"] as Timestamp).toDate())
        val isOvercome: Boolean = plateDocumentSnapshot["isOvercome"] as Boolean

        viewHolder.apply {
            if(title.length >= 12){
                this.title?.text = title.substring(0, 11) + ".."
            }else{
                this.title?.text = title
            }
            this.mainText?.text = mainText
            this.like?.text = like
            this.uploadTime?.text = uploadTime
            if (isOvercome){
                this.isOvercome?.setImageResource(R.drawable.icon_cardplate_isovercome_true)
            } else {
                this.isOvercome?.setImageResource(R.drawable.icon_cardplate_isovercome_false)
            }
            this.cardView?.setOnClickListener{
                val intent = Intent(mContext, PlateActivity::class.java)
                getResult.launch(intent.putExtra("snapshotId", plateDocumentSnapshot.id))
            }
        }
    }

    fun setData(newData: List<DocumentSnapshot>, newIsTimeListType: Boolean) {
        allPlateList = if(newIsTimeListType){
            newData.sortedByDescending { (it["uploadTime"] as Timestamp).toDate() }
        }else{
            newData.sortedByDescending { (it["uploadTime"] as Timestamp).toDate() }.sortedByDescending { it["like"] as Long }
        }

        notifyDataSetChanged()
    }
}