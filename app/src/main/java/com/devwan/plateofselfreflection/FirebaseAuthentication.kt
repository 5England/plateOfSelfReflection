package com.devwan.plateofselfreflection

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FirebaseAuthentication (private val activity : AppCompatActivity){

    private val signInLauncher = activity.registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    fun signIn(){
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build()
        )
        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.icon_authlogo)
            .setTheme(R.style.SignInTheme)
            .setIsSmartLockEnabled(false)
            .setLockOrientation(true)
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
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            if (user.isAnonymous) {
                val dlg = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
                dlg.apply {
                    setTitle("로그아웃")
                    setMessage("익명 사용자의 경우 기존 데이터가 유실될 수 있습니다.")
                    setPositiveButton("취소", DialogInterface.OnClickListener { dialog, which ->

                    })
                    setNegativeButton("로그아웃", DialogInterface.OnClickListener { dialog, which ->
                        Firebase.auth.signOut()
                        signIn()
                    })
                    show()
                }
            } else {
                Firebase.auth.signOut()
                signIn()
            }
        }
    }
}