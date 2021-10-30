package com.devwan.plateofselfreflection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot

class AllPlateViewModel ( savedStateHandle: SavedStateHandle) : ViewModel() {

    val userRepo : FireStoreRepository = FireStoreRepository()
    val userId : String? = savedStateHandle["uid"]

    private var _plate = MutableLiveData<List<DocumentSnapshot>>()
    val plate : LiveData<List<DocumentSnapshot>> = _plate

    init {
        //update
    }
}