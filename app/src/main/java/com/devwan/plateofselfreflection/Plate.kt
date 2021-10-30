package com.devwan.plateofselfreflection

import com.google.firebase.Timestamp

data class Plate(val uid : String,
                 val title : String,
                 val mainText : String,
                 val uploadTime : Timestamp,
                 val isOvercome : Boolean = false,
                 val like : Long = 0,
                 val userLikeUid : Map<String, Boolean> = mapOf<String, Boolean>()){}
