package com.devwan.plateofselfreflection

import androidx.lifecycle.*
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch

class NotificationViewModel() : ViewModel() {
    private val firebaseRepo : FirebaseRepo = FirebaseRepo()

    private var _comment = MutableLiveData<QuerySnapshot>()
    val comment : LiveData<QuerySnapshot> = _comment

    init {
        getNewCommentList()
    }

    fun getNewCommentList(){
        viewModelScope.launch {
            firebaseRepo.getNewCommentList(_comment)
        }
    }
}