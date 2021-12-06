package com.devwan.plateofselfreflection

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devwan.plateofselfreflection.databinding.CardPlateBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

open class PlateAdapter(val mContext: Context, var plateList: List<DocumentSnapshot>) :
    RecyclerView.Adapter<PlateAdapter.ViewHolder>() {

    inner class ViewHolder(val binding : CardPlateBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardPlateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        bindData(viewHolder, plateList[position])
    }

    override fun getItemCount() = plateList.size

    open fun bindData(viewHolder: ViewHolder, plateSnapshot: DocumentSnapshot) {
        val title: String = plateSnapshot["title"].toString()
        val mainText: String = plateSnapshot["mainText"].toString()
        val like: String = plateSnapshot["like"].toString()
        val uploadTime: String = Plate.getUploadTimeText((plateSnapshot["uploadTime"] as Timestamp).toDate())
        val isOvercome: Boolean = plateSnapshot["isOvercome"] as Boolean
        val nickName : String = plateSnapshot["nickName"].toString()
        val category : String = plateSnapshot["category"].toString()

        viewHolder.binding.apply {
            textTitle.text = title
            if(title.length >= 16){
                textTitle.text = title.substring(0, 15) + ".."
            }
            textMainText.text = mainText
            plateLike.text = like
            textUploadTime.text = uploadTime
            textNickName.text = nickName
            textCategory.text = category
            imgIsOvercome.apply {
                setImageResource(if(isOvercome){
                    R.drawable.icon_cardplate_isovercome_true
                } else {
                    R.drawable.icon_cardplate_isovercome_false
                })
            }
            initCardViewClickListener(this, plateSnapshot)
            initBtnUpdateMyPlate(this, plateSnapshot)
        }
    }

    open fun initCardViewClickListener(binding : CardPlateBinding, plateSnapshot: DocumentSnapshot){
        binding.layoutCardView.setOnClickListener{
            val intent = Intent(mContext, PlateActivity::class.java).putExtra("snapshotId", plateSnapshot.id)
            mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    open fun initBtnUpdateMyPlate(binding : CardPlateBinding, plateSnapshot: DocumentSnapshot){}

    fun setData(newData: List<DocumentSnapshot>) {
        this.plateList = newData.sortedByDescending { (it["uploadTime"] as Timestamp).toDate() }
        this.notifyDataSetChanged()
    }
}