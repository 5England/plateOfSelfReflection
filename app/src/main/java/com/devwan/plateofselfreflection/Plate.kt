package com.devwan.plateofselfreflection

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

data class Plate(val nickName : String = "익명",
                 val category : String,
                 val title : String,
                 val mainText : String,
                 val feedBack : String = "",
                 val uploadTime : Timestamp,
                 val isOvercome : Boolean = false,
                 val notice : Boolean = false,
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
                        if(uploadDate.minutes < 10){
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
                (uploadDate.year + 1900).toString() + "/" + (uploadDate.month + 1).toString() + "/" + uploadDate.date.toString()
            }
        }

        fun getPlate(snapshot: DocumentSnapshot) : Plate{
            return Plate(
                nickName = snapshot["nickName"] as String,
                category = snapshot["category"] as String,
                title = snapshot["title"] as String,
                mainText = snapshot["mainText"] as String,
                feedBack = snapshot["feedBack"] as String,
                uploadTime = snapshot["uploadTime"] as Timestamp,
                isOvercome = snapshot["isOvercome"] as Boolean,
                notice = snapshot["notice"] as Boolean,
                like = snapshot["like"] as Long,
                LikeUidMap = snapshot["likeUidMap"] as Map<String, Boolean>
            )
        }

        fun getStartTimeText(uploadDate : Date) : String {
            return (uploadDate.year + 1900).toString() + "." + (uploadDate.month + 1).toString() + "." + uploadDate.date.toString()
        }

        fun getHelloComment(myAllPlateNum : Int) : String {
            return if(myAllPlateNum == 0){
                "가볍게 반성을 시작해보세요."
            }else{
                when(myAllPlateNum){
                    in 1..2 -> "좋아요. 시작이 반이죠."
                    in 3..4 -> "개선에 집중을 해볼까요?"
                    else -> "정말 잘 하고 있어요!\n계속 발전해봐요."
                }
            }
        }
    }
}
