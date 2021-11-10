package com.devwan.plateofselfreflection

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class HomeViewModel ( savedStateHandle: SavedStateHandle) : ViewModel() {

    val firestoreRepo : FirestoreRepository = FirestoreRepository()
    val userId : String? = savedStateHandle["uid"]

    private var _stateSnapshot = MutableLiveData<DocumentSnapshot>()
    val stateSnapshot : LiveData<DocumentSnapshot> = _stateSnapshot

    init {
        getMyPlateStateSnapshot()
        //getMotiList
    }

    fun getMyPlateStateSnapshot(){
        viewModelScope.launch {
            firestoreRepo.getMyPlateStateSnapshot(_stateSnapshot)
        }
    }

    fun setMyNickName(newNickName : String){
        firestoreRepo.setMyNickName(newNickName)
    }
}