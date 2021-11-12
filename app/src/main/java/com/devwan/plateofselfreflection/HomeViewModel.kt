package com.devwan.plateofselfreflection

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class HomeViewModel ( savedStateHandle: SavedStateHandle) : ViewModel() {

    private val firebaseRepo : FirebaseRepo = FirebaseRepo()
    val userId : String? = savedStateHandle["uid"]

    private var _stateSnapshot = MutableLiveData<DocumentSnapshot>()
    val stateSnapshot : LiveData<DocumentSnapshot> = _stateSnapshot

    init {
        getMyPlateStateSnapshot()
    }

    fun getMyPlateStateSnapshot(){
        viewModelScope.launch {
            firebaseRepo.getMyPlateStateSnapshot(_stateSnapshot)
        }
    }
}