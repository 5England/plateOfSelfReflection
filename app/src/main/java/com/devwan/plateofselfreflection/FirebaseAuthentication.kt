package com.devwan.plateofselfreflection

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FirebaseAuthentication (private val activity : AppCompatActivity){

    private val signInLauncher = activity.registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    fun signIn(){
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )
        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.authlogo_icon)
            .setTheme(R.style.SignInTheme)
            .build()
        signInLauncher.launch(signInIntent)
    }

    // [START auth_fui_result]
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult){
        val response = result.idpResponse
        if (result.resultCode == Activity.RESULT_OK) {
            val onAuthServiceListener : OnAuthServiceListener = activity as OnAuthServiceListener
            onAuthServiceListener.onSignInComplete()
        } else {
            activity.finish()
        }
    }

    fun signOut() {
        Firebase.auth.signOut()
        signIn()
    }
}