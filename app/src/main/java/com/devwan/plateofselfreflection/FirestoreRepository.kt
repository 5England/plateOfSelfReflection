package com.devwan.plateofselfreflection

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.util.*

class FirestoreRepository {
    private val db = Firebase.firestore
    private val uid = Firebase.auth.uid.toString()

    fun getUid() : String{
        return uid
    }

    fun uploadPlate(newPlate : Plate){
        val newData = hashMapOf(
            "uid" to uid,
            "nickName" to newPlate.nickName,
            "title" to newPlate.title,
            "mainText" to newPlate.mainText,
            "isOvercome" to newPlate.isOvercome,
            "feedBack" to newPlate.feedBack,
            "uploadTime" to newPlate.uploadTimestamp,
            "like" to newPlate.like,
            "likeUidMap" to newPlate.LikeUidMap,
            "commentList" to newPlate.commentList
        )

        db.collection("plate")
            .add(newData)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    suspend fun getAllPlateList():List<DocumentSnapshot>{
        var snapshotList : MutableList<DocumentSnapshot> = mutableListOf<DocumentSnapshot>()

        coroutineScope{
            db.collection("plate")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            snapshotList.add(document)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                    }
        }.await()

        return snapshotList
    }

    fun listenMyPlateList(liveDataPlateList : MutableLiveData<List<DocumentSnapshot>>) {
        db.collection("plate")
                .whereEqualTo("uid", uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        liveDataPlateList.value = snapshot.documents
                    }
                }
    }

    fun checkIsOvercome(plate : DocumentSnapshot)
    {
        db.collection("plate").document(plate.id)
                .update("isOvercome", !(plate["isOvercome"] as Boolean))
                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }

    fun uploadFeedback(snapshotId : String, feedbackText : String){
        db.collection("plate").document(snapshotId)
            .update("feedBack", feedbackText)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }

    fun deletePlate(plate : DocumentSnapshot)
    {
        db.collection("plate").document(plate.id)
            .delete()
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }

    fun uploadPlate(snapshotId : String, newTitle : String, newMainText : String){
        val document = db.collection("plate").document(snapshotId)

        document.update("title", newTitle)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }

        document.update("mainText", newMainText)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }

    suspend fun likePlate(snapshotId : String){
        val plateDocument = db.collection("plate").document(snapshotId)
        lateinit var likeUidMap: MutableMap<String, Boolean>
        var like : Long = 0

        coroutineScope {
            plateDocument.get()
                .addOnSuccessListener {
                    likeUidMap = it["likeUidMap"] as MutableMap<String, Boolean>
                    like = it["like"] as Long
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }.await()

        if(likeUidMap.containsKey(uid)) {
            likeUidMap[uid]?.let {
                if(it){
                    likeUidMap.set(uid, false)
                    likeUidMap.toMap()
                    plateDocument.update("like", (like - 1.toLong()))
                    plateDocument.update("likeUidMap", likeUidMap)
                }else{
                    likeUidMap.set(uid, true)
                    likeUidMap.toMap()
                    plateDocument.update("like", (like + 1.toLong()))
                    plateDocument.update("likeUidMap", likeUidMap)
                }
            }
        }else{
            likeUidMap.put(uid, true)
            likeUidMap.toMap()
            plateDocument.update("like", (like + 1.toLong()))
            plateDocument.update("likeUidMap", likeUidMap)
        }
    }

    suspend fun getPlate(snapshotId : String) : DocumentSnapshot?{
        var plateSnapshot : DocumentSnapshot? = null

        coroutineScope {
            db.collection("plate").document(snapshotId)
                .get()
                .addOnSuccessListener {
                    plateSnapshot = it
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }.await()

        return plateSnapshot
    }

    suspend fun uploadComment(snapshotId : String, comment : String) {
        val plateDocument = db.collection("plate").document(snapshotId)
        lateinit var commentList: MutableList<String>

        coroutineScope {
            plateDocument.get()
                .addOnSuccessListener {
                    commentList = it["commentList"] as MutableList<String>
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }.await()

        commentList.add(comment)
        commentList.toList()
        plateDocument.update("commentList", commentList)
    }

    suspend fun getMyNickName() : String {

        val docRef = db.collection("profile").document(uid)
        var snapshot : String = ""
        coroutineScope {
            docRef
                .get()
                .addOnSuccessListener {
                    snapshot = if(it["nickName"] == null){
                        val newData = hashMapOf( "nickName" to "익명"
                            ,"overcomePlateNum" to 0
                            ,"allPlateNum" to 0)
                        docRef.set(newData)
                        "익명"
                    }else{
                        it["nickName"] as String
                    }
                }
        }.await()

        return snapshot
    }

    fun setMyNickName(newNickName : String){
        val docRef = db.collection("profile").document(uid)
        docRef.update("nickName", newNickName)
    }

    fun listenMyPlateState(_nickName : MutableLiveData<String>,
                           _overcomeNum : MutableLiveData<Long>, _plateNum : MutableLiveData<Long>){
        db.collection("profile").document(uid)
            .addSnapshotListener { snapshot, e ->
            if (snapshot != null && snapshot.exists()) {
                _nickName.value = snapshot["nickName"] as String
                _overcomeNum.value = snapshot["overcomePlateNum"] as Long
                _plateNum.value = snapshot["allPlateNum"] as Long
            }
        }

    }
}








