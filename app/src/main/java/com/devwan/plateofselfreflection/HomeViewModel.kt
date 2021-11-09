package com.devwan.plateofselfreflection

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class HomeViewModel ( savedStateHandle: SavedStateHandle) : ViewModel() {

    val firestoreRepo : FirestoreRepository = FirestoreRepository()
    val userId : String? = savedStateHandle["uid"]

    private var _nickName = MutableLiveData<String>()
    val nickName : LiveData<String> = _nickName
    private var _overcomePlate = MutableLiveData<Long>()
    val overcomePlate : LiveData<Long> = _overcomePlate
    private var _notOvercomePlate = MutableLiveData<Long>()
    val notOvercomePlate : LiveData<Long> = _notOvercomePlate

    init {
        getMyNickName()
//        userRepo.listenMyPlateState(_overcomePlate, _notOvercomePlate)
    }

    fun getMyNickName(){
        viewModelScope.launch {
            _nickName.value = firestoreRepo.getMyNickName()
        }
    }

    fun setMyNickName(newNickName : String){
        firestoreRepo.setMyNickName(newNickName)
    }
}