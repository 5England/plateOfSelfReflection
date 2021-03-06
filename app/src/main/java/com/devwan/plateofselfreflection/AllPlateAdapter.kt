package com.devwan.plateofselfreflection

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.devwan.plateofselfreflection.databinding.CardPlateBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

class AllPlateAdapter(mContext: Context, plateList: List<DocumentSnapshot>,
                           private val getResult : ActivityResultLauncher<Intent>) : PlateAdapter(mContext, plateList){

    override fun initCardViewClickListener(
        binding: CardPlateBinding,
        plateSnapshot: DocumentSnapshot
    ) {
        binding.layoutCardView.setOnClickListener{
            val intent = Intent(mContext, PlateActivity::class.java)
            getResult.launch(intent.putExtra("snapshotId", plateSnapshot.id))
        }
    }

    fun setData(newData: List<DocumentSnapshot>, isListType: Boolean) {
        plateList = if(isListType){
            newData.sortedByDescending { (it["uploadTime"] as Timestamp).toDate() }
        }else{
            newData.sortedByDescending { (it["uploadTime"] as Timestamp).toDate() }.sortedByDescending { it["like"] as Long }
        }
        notifyDataSetChanged()
    }
}