package com.devwan.plateofselfreflection

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class MyPlateViewModel () : ViewModel() {

    private val firebaseRepo : FirebaseRepo = FirebaseRepo()

    private var _plate = MutableLiveData<List<DocumentSnapshot>>()
    val plate : LiveData<List<DocumentSnapshot>> = _plate
    private var _myStateSnapshot = MutableLiveData<DocumentSnapshot>()
    val myStateSnapshot : LiveData<DocumentSnapshot> = _myStateSnapshot

    init {
        firebaseRepo.listenMyPlateList(_plate)
        firebaseRepo.listenMyPlateState(_myStateSnapshot)
    }

    fun checkIsOvercome(plate : DocumentSnapshot){
        firebaseRepo.checkIsOvercome(plate)
    }

    fun deletePlate(plate : DocumentSnapshot){
        firebaseRepo.deletePlate(plate)
    }
}