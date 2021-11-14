package com.devwan.plateofselfreflection

import com.google.firebase.auth.FirebaseUser

interface OnAuthServiceListener {
    fun signOut()
    fun onSignInComplete()
    fun linkGoogleAccount()
    fun updateNewUid(prevUid : String, newUid : String)
}