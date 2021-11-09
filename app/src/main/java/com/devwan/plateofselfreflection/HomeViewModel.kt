package com.devwan.plateofselfreflection

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class HomeViewModel ( savedStateHandle: SavedStateHandle) : ViewModel() {

    val firestoreRepo : FirestoreRepository = FirestoreRepository()
    val userId : String? = savedStateHandle["uid"]

    private var _nickName = MutableLiveData<String>()
    val nickName : LiveData<String> = _nickName
    private var _overcomeNum = MutableLiveData<Long>()
    val overcomeNum : LiveData<Long> = _overcomeNum
    private var _plateNum = MutableLiveData<Long>()
    val plateNum : LiveData<Long> = _plateNum

    init {
        viewModelScope.launch {
            _nickName.value = firestoreRepo.getMyNickName()
            firestoreRepo.getMyPlateState(_nickName, _overcomeNum, _plateNum)
        }
        //getMotiList
    }

    fun setMyNickName(newNickName : String){
        firestoreRepo.setMyNickName(newNickName)
    }
}