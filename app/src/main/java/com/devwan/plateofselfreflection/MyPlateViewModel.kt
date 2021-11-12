package com.devwan.plateofselfreflection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot

class MyPlateViewModel ( savedStateHandle: SavedStateHandle) : ViewModel() {

    private val firebaseRepo : FirebaseRepo = FirebaseRepo()
    val userId : String? = savedStateHandle["uid"]

    private var _plate = MutableLiveData<List<DocumentSnapshot>>()
    val plate : LiveData<List<DocumentSnapshot>> = _plate

    init {
        firebaseRepo.listenMyPlateList(_plate)
    }

    fun checkIsOvercome(plate : DocumentSnapshot){
        firebaseRepo.checkIsOvercome(plate)
    }

    fun deletePlate(plate : DocumentSnapshot){
        firebaseRepo.deletePlate(plate)
    }
}