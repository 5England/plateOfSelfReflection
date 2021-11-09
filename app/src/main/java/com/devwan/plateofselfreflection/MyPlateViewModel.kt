package com.devwan.plateofselfreflection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot

class MyPlateViewModel ( savedStateHandle: SavedStateHandle) : ViewModel() {

    val firestoreRepo : FirestoreRepository = FirestoreRepository()
    val userId : String? = savedStateHandle["uid"]

    private var _plate = MutableLiveData<List<DocumentSnapshot>>()
    val plate : LiveData<List<DocumentSnapshot>> = _plate

    init {
        firestoreRepo.listenMyPlateList(_plate)
    }

    fun checkIsOvercome(plate : DocumentSnapshot){
        firestoreRepo.checkIsOvercome(plate)
    }

    fun deletePlate(plate : DocumentSnapshot){
        firestoreRepo.deletePlate(plate)
    }
}