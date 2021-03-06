package com.devwan.plateofselfreflection

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

class FirebaseRepo {
    private val db = Firebase.firestore
    private val uid = Firebase.auth.uid.toString()
    private val allNumId = "allPlateNumState"

    fun getUid(): String {
        return uid
    }

    suspend fun uploadPlate(newPlate: Plate) {
        coroutineScope {
            db.collection("profile").document(uid).get().addOnSuccessListener {
                val nickName = it["nickName"].toString()

                val newData = hashMapOf(
                    "uid" to uid,
                    "nickName" to nickName,
                    "category" to newPlate.category,
                    "title" to newPlate.title,
                    "mainText" to newPlate.mainText,
                    "isOvercome" to newPlate.isOvercome,
                    "notice" to newPlate.notice,
                    "feedBack" to newPlate.feedBack,
                    "uploadTime" to newPlate.uploadTime,
                    "like" to newPlate.like,
                    "likeUidMap" to newPlate.LikeUidMap,
                )

                db.collection("plate")
                    .add(newData)
                    .addOnSuccessListener { documentReference ->
                        plusAllPlateNum()
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error adding document", e)
                    }
            }.await()
        }
    }

    suspend fun getAllPlateList(): List<DocumentSnapshot> {
        var snapshotList: MutableList<DocumentSnapshot> = mutableListOf<DocumentSnapshot>()

        coroutineScope {
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

    fun listenMyPlateList(liveDataPlateList: MutableLiveData<List<DocumentSnapshot>>) {
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

    fun listenMyPlateState(_myStateSnapshot : MutableLiveData<DocumentSnapshot>){
        db.collection("profile")
            .document(uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    _myStateSnapshot.value = snapshot
                }
            }
    }

    suspend fun getSearchPlateList(keyword: String): List<DocumentSnapshot> {
        var snapshotList: MutableList<DocumentSnapshot> = mutableListOf<DocumentSnapshot>()

        coroutineScope {
            db.collection("plate")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if ((document["title"].toString().contains(keyword))) {
                            snapshotList.add(document)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }.await()

        return snapshotList
    }

    suspend fun getCategoryPlateList(category : String): List<DocumentSnapshot> {
        var snapshotList: MutableList<DocumentSnapshot> = mutableListOf<DocumentSnapshot>()

        coroutineScope {
            db.collection("plate")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener { documents ->
                    documents.forEach { document ->
                        snapshotList.add(document)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }.await()

        return snapshotList
    }

    fun checkIsOvercome(plate: DocumentSnapshot) {
        val isOvercome = plate["isOvercome"] as Boolean

        db.collection("plate").document(plate.id)
            .update("isOvercome", !(plate["isOvercome"] as Boolean))
            .addOnSuccessListener {
                if (isOvercome) {
                    minusOvercomePlateNum()
                } else {
                    plusOvercomePlateNum()
                }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error updating document", e)
            }
    }

    fun uploadFeedback(snapshotId: String, feedbackText: String) {
        db.collection("plate").document(snapshotId)
            .update("feedBack", feedbackText)
            .addOnSuccessListener {
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot successfully updated!"
                )
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }

    fun deletePlate(plate: DocumentSnapshot) {
        val isOvercome = plate["isOvercome"] as Boolean

        db.collection("plate").document(plate.id)
            .delete()
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!")
                minusAllPlateNum()
                if (isOvercome) {
                    minusOvercomePlateNum()
                }
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }

    fun updatePlate(snapshotId: String, newTitle: String, newMainText: String) {
        val document = db.collection("plate").document(snapshotId)

        document.update("title", newTitle)
            .addOnSuccessListener {
                Log.d(
                    ContentValues.TAG, "DocumentSnapshot successfully deleted!"
                )
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }

        document.update("mainText", newMainText)
            .addOnSuccessListener {
                Log.d(
                    ContentValues.TAG, "DocumentSnapshot successfully deleted!"
                )
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }

    suspend fun likePlate(snapshotId: String) {
        val plateDocument = db.collection("plate").document(snapshotId)
        lateinit var likeUidMap: MutableMap<String, Boolean>
        var like: Long = 0

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

        if (likeUidMap.containsKey(uid)) {
            likeUidMap[uid]?.let {
                if (it) {
                    likeUidMap.set(uid, false)
                    likeUidMap.toMap()
                    plateDocument.update("like", (like - 1.toLong()))
                    plateDocument.update("likeUidMap", likeUidMap)
                } else {
                    likeUidMap.set(uid, true)
                    likeUidMap.toMap()
                    plateDocument.update("like", (like + 1.toLong()))
                    plateDocument.update("likeUidMap", likeUidMap)
                }
            }
        } else {
            likeUidMap.put(uid, true)
            likeUidMap.toMap()
            plateDocument.update("like", (like + 1.toLong()))
            plateDocument.update("likeUidMap", likeUidMap)
        }
    }

    suspend fun getPlate(snapshotId: String): DocumentSnapshot? {
        var plateSnapshot: DocumentSnapshot? = null

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

    suspend fun getCommentList(snapshotId : String) : List<DocumentSnapshot>{
        var commentList: MutableList<DocumentSnapshot> = mutableListOf<DocumentSnapshot>()

        coroutineScope {
            db.collection("plate").document(snapshotId).collection("comments")
                .get()
                .addOnSuccessListener { documents ->
                    documents.forEach { document ->
                        commentList.add(document)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }.await()

        return commentList.toList()
    }

    suspend fun uploadComment(snapshotId: String, comment: String, commentUploadTime : Timestamp) {
        val plateDocument = db.collection("plate").document(snapshotId)

        coroutineScope {
            plateDocument.get().addOnSuccessListener { it ->
                 val isMyPlate : Boolean = when(it["uid"] as String){
                    uid -> true
                    else -> false
                }

                db.collection("profile").document(uid).get().addOnSuccessListener { myProfileSnapshot ->
                    val newComment = hashMapOf(
                        "uid" to uid,
                        "nickName" to myProfileSnapshot["nickName"] as String,
                        "comment" to comment,
                        "uploadTime" to commentUploadTime,
                        "plateId" to snapshotId,
                        "notice" to !isMyPlate
                    )
                    plateDocument.collection("comments").add(newComment)
                    if(!isMyPlate){
                        plateDocument.update("notice", true)
                    }
                }
            }
        }.await()
    }

    fun updateCommentNotice(snapshotId: String){
        val plateDocument = db.collection("plate").document(snapshotId)
        plateDocument.get().addOnSuccessListener {
            if(uid == it["uid"] as String){
                plateDocument.update("notice", false)
                plateDocument.collection("comments").whereEqualTo("notice", true).get().addOnSuccessListener { documents ->
                    documents.forEach { document ->
                        plateDocument.collection("comments").document(document.id).update("notice", false)
                    }
                }
            }
        }
    }

    suspend fun getNewCommentList(_comment : MutableLiveData<MutableList<DocumentSnapshot>>){
        coroutineScope{
            var newCommentList = mutableListOf<DocumentSnapshot>()

            db.collection("plate").whereEqualTo("uid", uid).whereEqualTo("notice", true).get()
                .addOnSuccessListener { plates ->
                    launch {
                        plates.forEach { plate ->
                            db.collection("plate").document(plate.id).collection("comments")
                                .whereEqualTo("notice", true).get().addOnSuccessListener { comments ->
                                    comments.forEach { document ->
                                        newCommentList.add(document)
                                    }
                                }.await()
                        }
                        _comment.value = newCommentList
                    }
                }.await()
        }
    }




//        coroutineScope{
//            var newCommentList = mutableListOf<DocumentSnapshot>()
//
//            db.collection("plate").whereEqualTo("uid", uid).whereEqualTo("notice", true).get()
//                .addOnSuccessListener { plates ->
//                    plates.forEach { plate ->
//                        db.collection("plate").document(plate.id).collection("comments")
//                            .whereEqualTo("notice", true).get().addOnSuccessListener { comments ->
//                            comments.forEach { document ->
//                                newCommentList.add(document)
//                            }
//                        }
//                    }
//                    _comment.value = newCommentList
//                }
//        }.await()

    suspend fun deleteMyComment(plateId : String, commentId : String){
        coroutineScope {
            db.collection("plate").document(plateId).collection("comments").document(commentId).delete()
        }
    }

    suspend fun getMyPlateStateSnapshot(_myStateSnapshot: MutableLiveData<DocumentSnapshot>) {
        val docRef = db.collection("profile").document(uid)

        coroutineScope {
            docRef
                .get()
                .addOnSuccessListener {
                    if (it["nickName"] == null) {
                        val newData = hashMapOf(
                            "nickName" to "??????",
                            "overcomePlateNum" to 0,
                            "allPlateNum" to 0,
                            "startTime" to Timestamp.now()
                        )
                        docRef.set(newData)
                    }
                }
        }.await()

        db.collection("profile").document(uid)
            .get().addOnSuccessListener {
                _myStateSnapshot.value = it
            }
    }

    fun getAllPlateStateSnapshot(_allStateSnapshot: MutableLiveData<DocumentSnapshot>) {
        db.collection("profile").document(allNumId)
            .get().addOnSuccessListener {
                _allStateSnapshot.value = it
            }
    }

    fun setMyNickName(newNickName: String) {
        val docRef = db.collection("profile").document(uid)
        docRef.update("nickName", newNickName)
    }

    suspend fun getMotivationListSnapshot(): QuerySnapshot? {
        var querySnapshot: QuerySnapshot? = null

        coroutineScope {
            db.collection("moti")
                .get()
                .addOnSuccessListener { documents ->
                    querySnapshot = documents
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }.await()

        return querySnapshot
    }

    private fun plusAllPlateNum() {
        val myDocRef = db.collection("profile").document(uid)
        myDocRef.get().addOnSuccessListener {
            val newAllPlateNum: Long = it["allPlateNum"] as Long + 1
            myDocRef.update("allPlateNum", newAllPlateNum)
        }

        val allDocRef = db.collection("profile").document(allNumId)
        allDocRef.get().addOnSuccessListener {
            val newAllPlateNum: Long = it["allPlateNum"] as Long + 1
            allDocRef.update("allPlateNum", newAllPlateNum)
        }
    }

    private fun minusAllPlateNum() {
        val myDocRef = db.collection("profile").document(uid)
        myDocRef.get().addOnSuccessListener {
            val newAllPlateNum: Long = it["allPlateNum"] as Long - 1
            myDocRef.update("allPlateNum", newAllPlateNum)
        }

        val allDocRef = db.collection("profile").document(allNumId)
        allDocRef.get().addOnSuccessListener {
            val newAllPlateNum: Long = it["allPlateNum"] as Long - 1
            allDocRef.update("allPlateNum", newAllPlateNum)
        }
    }

    private fun plusOvercomePlateNum() {
        val myDocRef = db.collection("profile").document(uid)
        myDocRef.get().addOnSuccessListener {
            val newAllPlateNum: Long = it["overcomePlateNum"] as Long + 1
            myDocRef.update("overcomePlateNum", newAllPlateNum)
        }

        val allDocRef = db.collection("profile").document(allNumId)
        allDocRef.get().addOnSuccessListener {
            val newAllPlateNum: Long = it["overcomePlateNum"] as Long + 1
            allDocRef.update("overcomePlateNum", newAllPlateNum)
        }
    }

    private fun minusOvercomePlateNum() {
        val myDocRef = db.collection("profile").document(uid)
        myDocRef.get().addOnSuccessListener {
            val newAllPlateNum: Long = it["overcomePlateNum"] as Long - 1
            myDocRef.update("overcomePlateNum", newAllPlateNum)
        }

        val allDocRef = db.collection("profile").document(allNumId)
        allDocRef.get().addOnSuccessListener {
            val newAllPlateNum: Long = it["overcomePlateNum"] as Long - 1
            allDocRef.update("overcomePlateNum", newAllPlateNum)
        }
    }

    suspend fun updateNewUid(prevUid: String, newUid: String) {
        val plateCollection = db.collection("plate")
        val profileCollection = db.collection("profile")

        coroutineScope {
            plateCollection
                .whereEqualTo("uid", prevUid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        plateCollection.document(document.id)
                            .update("uid", newUid)
                    }
                }

            profileCollection.document(prevUid)
                .get().addOnSuccessListener {
                    val newData = hashMapOf(
                        "nickName" to it["nickName"],
                        "overcomePlateNum" to it["overcomePlateNum"],
                        "allPlateNum" to it["allPlateNum"],
                        "startTime" to it["startTime"]
                    )
                    profileCollection.document(newUid).set(newData)
                    profileCollection.document(prevUid).delete()
                }
        }.await()
    }

    suspend fun deleteMyAllData() {
        val profileCollection = db.collection("profile")
        val plateCollection = db.collection("plate")

        coroutineScope {
            profileCollection.document(uid).get().addOnSuccessListener { myPlateState ->
                profileCollection.document(allNumId).get().addOnSuccessListener { allPlateState ->
                    profileCollection.document(allNumId).update(
                        "allPlateNum",
                        ((allPlateState["allPlateNum"] as Long) - (myPlateState["allPlateNum"] as Long))
                    )
                    profileCollection.document(allNumId).update(
                        "overcomePlateNum",
                        ((allPlateState["overcomePlateNum"] as Long) - (myPlateState["overcomePlateNum"] as Long))
                    ).addOnSuccessListener { profileCollection.document(uid).delete() }
                }
            }

            plateCollection.whereEqualTo("uid", uid).get().addOnSuccessListener { documents ->
                for (document in documents) {
                    plateCollection.document(document.id).delete()
                }
            }
        }.await()
    }
}