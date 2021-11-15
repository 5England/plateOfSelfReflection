package com.devwan.plateofselfreflection

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class HomeViewModel ( savedStateHandle: SavedStateHandle) : ViewModel() {

    private val firebaseRepo : FirebaseRepo = FirebaseRepo()
    val userId : String? = savedStateHandle["uid"]

    private var _myStateSnapshot = MutableLiveData<DocumentSnapshot>()
    val myStateSnapshot : LiveData<DocumentSnapshot> = _myStateSnapshot
    private var _allStateSnapshot = MutableLiveData<DocumentSnapshot>()
    val allStateSnapshot : LiveData<DocumentSnapshot> = _allStateSnapshot

    init {
        getMyPlateStateSnapshot()
        getAllPlateStateSnapshot()
    }

    private fun getMyPlateStateSnapshot(){
        viewModelScope.launch {
            firebaseRepo.getMyPlateStateSnapshot(_myStateSnapshot)
        }
    }

    private fun getAllPlateStateSnapshot(){
        firebaseRepo.getAllPlateStateSnapshot(_allStateSnapshot)
    }
}