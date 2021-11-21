package com.devwan.plateofselfreflection

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class AllPlateViewModel () : ViewModel() {
    private val firebaseRepo : FirebaseRepo = FirebaseRepo()

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