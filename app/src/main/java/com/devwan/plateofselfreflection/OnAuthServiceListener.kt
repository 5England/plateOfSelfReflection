package com.devwan.plateofselfreflection

interface OnAuthServiceListener {
    fun signOut()
    fun onSignInComplete()
    fun linkGoogleAccount()
    fun updateNewUid(prevUid : String, newUid : String)
    fun resignAccount()
}