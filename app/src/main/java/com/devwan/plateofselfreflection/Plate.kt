package com.devwan.plateofselfreflection

import android.content.Intent
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

data class Plate(val nickName : String = "익명",
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
                        var result : String = uploadDate.hours.toString() + ":"
                        if(uploadDate.hours < 10){
                            result += "0"
                            result += uploadDate.minutes.toString()
                        }else{
                            result += uploadDate.minutes.toString()
                        }
                        result
                    }
                }else{
                    (uploadDate.month + 1).toString() + "/" + uploadDate.date.toString()
                }
            }else{
                (uploadDate.year + 1).toString() + "/" + (uploadDate.month + 1).toString() + "/" + uploadDate.date.toString()
            }
        }
    }
}
