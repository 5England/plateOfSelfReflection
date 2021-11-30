package com.devwan.plateofselfreflection

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.devwan.plateofselfreflection.databinding.ActivityMainBinding
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnAuthServiceListener{

    private lateinit var binding : ActivityMainBinding
    private val firebaseAuthentication = FirebaseAuthentication(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (FirebaseAuth.getInstance().currentUser == null) {
            firebaseAuthentication.signIn()
        }else{
            initNavigationBar()
        }
    }

    override fun onSignInComplete() {
        setContentView(binding.root)
        initNavigationBar()
    }

    override fun signOut() {
        firebaseAuthentication.signOut()
    }

    override fun linkGoogleAccount(){
        firebaseAuthentication.linkGoogleAccount()
    }

    override fun updateNewUid(prevUid : String, newUid : String){
        GlobalScope.launch(Dispatchers.Main) {
            val firebaseRepo = FirebaseRepo()
            firebaseRepo.updateNewUid(prevUid, newUid)
            setContentView(binding.root)
            initNavigationBar()
        }
    }

    override fun resignAccount() {
        GlobalScope.launch(Dispatchers.Main) {
            val firebaseRepo = FirebaseRepo()
            firebaseRepo.deleteMyAllData()
            firebaseAuthentication.resignAccount()
        }
    }

    private fun initNavigationBar() {
        binding.bottomNavigationView.run {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.icon_home -> changeFragmentWithAnim(HomeFragment())
                    R.id.icon_feed -> changeFragmentWithAnim(AllPlateFragment())
                    R.id.icon_myFeed -> changeFragmentWithAnim(MyPlateFragment())
                    R.id.icon_info -> changeFragmentWithAnim(InfoFragment())
                }
                true
            }
            selectedItemId = R.id.icon_home
        }
    }

    private fun changeFragmentWithAnim(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
            commit()
        }
    }

    fun changeAllPlateFragment(){
        binding.bottomNavigationView.selectedItemId = R.id.icon_feed
    }
}