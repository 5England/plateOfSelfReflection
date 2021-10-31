package com.devwan.plateofselfreflection

import com.google.firebase.Timestamp
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
            val uploadYear = uploadDate.year
            val uploadMonth = uploadDate.month
            val uploadDay = uploadDate.day

            return if(nowDate.year == uploadYear){
                if(nowDate.month == uploadMonth && nowDate.day == uploadDay) {
                    uploadDate.hours.toString() + ":" + uploadDate.minutes.toString()
                }else{
                    uploadMonth.toString() + "/" + uploadDay.toString()
                }
            }else{
                uploadYear.toString() + "/" + uploadMonth.toString() + "/" + uploadDay.toString()
            }
        }
    }
}
