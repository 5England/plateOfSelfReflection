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
import kotlinx.coroutines.coroutineScope
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

    suspend fun getSearchPlateList(keyword : String): List<DocumentSnapshot> {
        var snapshotList: MutableList<DocumentSnapshot> = mutableListOf<DocumentSnapshot>()

        coroutineScope {
            db.collection("plate")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if((document["title"].toString().contains(keyword))){
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

    suspend fun uploadComment(snapshotId: String, comment: String) {
        val plateDocument = db.collection("plate").document(snapshotId)

        coroutineScope {
            plateDocument.get()
                .addOnSuccessListener {
                    var commentList: MutableList<String> = it["commentList"] as MutableList<String>
                    commentList.apply {
                        add(comment)
                        commentList.toList()
                        plateDocument.update("commentList", this)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }.await()
    }

    suspend fun getMyPlateStateSnapshot(_myStateSnapshot: MutableLiveData<DocumentSnapshot>) {
        val docRef = db.collection("profile").document(uid)

        coroutineScope {
            docRef
                .get()
                .addOnSuccessListener {
                    if (it["nickName"] == null) {
                        val newData = hashMapOf(
                            "nickName" to "익명", "overcomePlateNum" to 0, "allPlateNum" to 0, "startTime" to Timestamp.now()
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

    fun getAllPlateStateSnapshot(_allStateSnapshot: MutableLiveData<DocumentSnapshot>){
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

    suspend fun updateNewUid(prevUid : String, newUid : String){
        val plateCollection = db.collection("plate")
        val profileCollection = db.collection("profile")

        coroutineScope {
            plateCollection
                .whereEqualTo("uid", prevUid)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents){
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

    suspend fun deleteMyAllData(){
        val plateCollection = db.collection("plate")
        coroutineScope {
            plateCollection.whereEqualTo("uid", uid).get().addOnSuccessListener { documents ->
                for(document in documents){
                    deletePlate(document)
                }
            }
        }.await()
    }

    suspend fun deleteMyProfile(){
        val profileCollection = db.collection("profile")
        coroutineScope {
            profileCollection.document(uid).delete()
        }.await()
    }
}