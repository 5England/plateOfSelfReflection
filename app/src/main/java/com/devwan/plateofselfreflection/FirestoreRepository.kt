package com.devwan.plateofselfreflection

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

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
            "likeUidMap" to newPlate.LikeUidMap
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
}