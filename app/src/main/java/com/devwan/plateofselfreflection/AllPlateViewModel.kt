package com.devwan.plateofselfreflection

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class AllPlateViewModel ( savedStateHandle: SavedStateHandle) : ViewModel() {

    private val firebaseRepo : FirebaseRepo = FirebaseRepo()
    val userId : String? = savedStateHandle["uid"]

    private var _plate = MutableLiveData<List<DocumentSnapshot>>()
    val plate : LiveData<List<DocumentSnapshot>> = _plate

    init {
        getAllPlateList()
    }

    fun getAllPlateList(){
        viewModelScope.launch {
            _plate.value = firebaseRepo.getAllPlateList()
        }
    }
}