package com.devwan.plateofselfreflection

import android.content.Intent
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

data class Plate(val nickName : String = "(익명)",
                 val title : String,
                 val mainText : String,
                 val feedBack : String = "",
                 val uploadTimestamp : Timestamp,
                 val isOvercome : Boolean = false,
                 val like : Long = 0,
                 val LikeUidMap : Map<String, Boolean> = mapOf<String, Boolean>()){

    companion object {
        fun getUploadTimeText(uploadDate : Date) : String {
            val nowDate = Timestamp.now().toDate()

            return if(nowDate.year == uploadDate.year){
                if(nowDate.month == uploadDate.month && nowDate.date == uploadDate.date) {
                    if(nowDate.hours == uploadDate.hours){
                        if(nowDate.minutes - uploadDate.minutes <= 1){
                            "방금 전"
                        }else{
                            (nowDate.minutes - uploadDate.minutes).toString() + "분 전"
                        }
                    }else{
                    uploadDate.hours.toString() + ":" + uploadDate.minutes.toString()
                    }
                }else{
                    (uploadDate.month + 1).toString() + "/" + uploadDate.date.toString()
                }
            }else{
                (uploadDate.year + 1).toString() + "/" + (uploadDate.month + 1).toString() + "/" + uploadDate.date.toString()
            }
        }

        fun getIntentForPlateActivity(intent : Intent, snapshot : DocumentSnapshot) : Intent {
            intent.apply {
                putExtra("nickName", snapshot["nickName"].toString())
                putExtra("uploadTime", getUploadTimeText((snapshot["uploadTime"] as Timestamp).toDate()))
                putExtra("title", snapshot["title"].toString())
                putExtra("mainText", snapshot["mainText"].toString())
                putExtra("isOvercome", snapshot["isOvercome"] as Boolean)
                putExtra("feedBack", snapshot["feedBack"].toString())
                putExtra("like", snapshot["like"] as Long)
                putExtra("snapshotId", snapshot.id)
            }

            val firestoreRepo : FirestoreRepository = FirestoreRepository()
            val uid : String = firestoreRepo.getUid()
            val likeUidMap : Map<String, Boolean> = snapshot["likeUidMap"] as Map<String, Boolean>
            var isLiked : Boolean = if(likeUidMap.containsKey(uid)){
                likeUidMap[uid] as Boolean
            }else{
                false
            }

            intent.putExtra("isLiked", isLiked)

            return intent
        }
    }
}
