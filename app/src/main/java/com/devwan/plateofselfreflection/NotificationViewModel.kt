package com.devwan.plateofselfreflection

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch

class NotificationViewModel() : ViewModel() {
    private val firebaseRepo : FirebaseRepo = FirebaseRepo()

    private var _comment = MutableLiveData<MutableList<DocumentSnapshot>>()
    val comment : LiveData<MutableList<DocumentSnapshot>> = _comment

    init {
        getNewCommentList()
    }

    fun getNewCommentList(){
        viewModelScope.launch {
            firebaseRepo.getNewCommentList(_comment)
        }
    }
}