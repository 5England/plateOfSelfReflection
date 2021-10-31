package com.devwan.plateofselfreflection

import com.google.firebase.Timestamp

data class Plate(val nickName : String = "(익명)",
                 val title : String,
                 val mainText : String,
                 val feedBack : String = "",
                 val uploadTimestamp : Timestamp,
                 val isOvercome : Boolean = false,
                 val like : Long = 0,
                 val LikeUidMap : Map<String, Boolean> = mapOf<String, Boolean>()){}
