package com.devwan.plateofselfreflection

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch

class HomeViewModel ( savedStateHandle: SavedStateHandle) : ViewModel() {

    val firestoreRepo : FirestoreRepository = FirestoreRepository()
    val userId : String? = savedStateHandle["uid"]

    private var _stateSnapshot = MutableLiveData<DocumentSnapshot>()
    val stateSnapshot : LiveData<DocumentSnapshot> = _stateSnapshot

    init {
        getMyPlateStateSnapshot()
    }

    fun getMyPlateStateSnapshot(){
        viewModelScope.launch {
            firestoreRepo.getMyPlateStateSnapshot(_stateSnapshot)
        }
    }
}