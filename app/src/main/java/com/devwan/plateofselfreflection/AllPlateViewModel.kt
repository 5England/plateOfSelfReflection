package com.devwan.plateofselfreflection

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class AllPlateViewModel ( savedStateHandle: SavedStateHandle) : ViewModel() {

    val firestoreRepo : FirestoreRepository = FirestoreRepository()
    val userId : String? = savedStateHandle["uid"]

    private var _plate = MutableLiveData<List<DocumentSnapshot>>()
    val plate : LiveData<List<DocumentSnapshot>> = _plate

    init {
        getPlateList()
    }

    fun getPlateList(){
        viewModelScope.launch {
            _plate.value = firestoreRepo.getPlateList()
        }
    }
}