package com.devwan.plateofselfreflection

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class MyPlateViewModel ( savedStateHandle: SavedStateHandle) : ViewModel() {

    private val firebaseRepo : FirebaseRepo = FirebaseRepo()
    val userId : String? = savedStateHandle["uid"]

    private var _plate = MutableLiveData<List<DocumentSnapshot>>()
    val plate : LiveData<List<DocumentSnapshot>> = _plate
    private var _myStateSnapshot = MutableLiveData<DocumentSnapshot>()
    val myStateSnapshot : LiveData<DocumentSnapshot> = _myStateSnapshot

    init {
        firebaseRepo.listenMyPlateList(_plate)
        getMyPlateStateSnapshot()
    }

    fun checkIsOvercome(plate : DocumentSnapshot){
        firebaseRepo.checkIsOvercome(plate)
    }

    fun deletePlate(plate : DocumentSnapshot){
        firebaseRepo.deletePlate(plate)
    }

    fun getMyPlateStateSnapshot(){
        viewModelScope.launch {
            firebaseRepo.getMyPlateStateSnapshot(_myStateSnapshot)
        }
    }
}