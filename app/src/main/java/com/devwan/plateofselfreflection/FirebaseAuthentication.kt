package com.devwan.plateofselfreflection

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class FirebaseAuthentication(private val activity: AppCompatActivity){

    private lateinit var prevUid : String

    //sign-in

    private val signInLauncher = activity.registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    fun signIn(){
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build()
        )
        val signInIntent = getSignInIntent(providers)
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult){
        val response = result.idpResponse
        if (result.resultCode == Activity.RESULT_OK) {
            val onAuthServiceListener : OnAuthServiceListener = activity as OnAuthServiceListener
            onAuthServiceListener.onSignInComplete()
        } else {
            activity.finish()
        }
    }

    //link-GoogleAccount

    private val linkGoogleLauncher = activity.registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onLinkGoogleResult(res)
    }

    fun linkGoogleAccount() {
        Firebase.auth.uid?.let {
            prevUid = it
        }

        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val signInIntent = getSignInIntent(providers)
        linkGoogleLauncher.launch(signInIntent)
    }

    private fun onLinkGoogleResult(result: FirebaseAuthUIAuthenticationResult){
        val response = result.idpResponse
        if (result.resultCode == Activity.RESULT_OK) {
            val onAuthServiceListener : OnAuthServiceListener = activity as OnAuthServiceListener
            Firebase.auth.uid?.let {
                onAuthServiceListener.updateNewUid(prevUid, it)
            }
        } else {
            activity.finish()
        }
    }

    //sign-out

    fun signOut() {
        Firebase.auth.signOut()
        signIn()
    }

    fun resignAccount(){
        val user = Firebase.auth.currentUser!!

        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signIn()
                }
            }
    }

    private fun getSignInIntent(providers : ArrayList<AuthUI.IdpConfig>) : Intent {
        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.icon_authlogo)
            .setTheme(R.style.SignInTheme)
            .setIsSmartLockEnabled(false)
            .setLockOrientation(true)
            .build()
    }
}