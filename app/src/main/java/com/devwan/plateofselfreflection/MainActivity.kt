package com.devwan.plateofselfreflection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.devwan.plateofselfreflection.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
            selectedItemId = R.id.icon_home
            changeFragment(HomeFragment())
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.icon_home -> if(it.itemId != this.selectedItemId) changeFragment(HomeFragment())
                    R.id.icon_feed -> if(it.itemId != this.selectedItemId) changeFragment(AllPlateFragment())
                    R.id.icon_myFeed -> if(it.itemId != this.selectedItemId) changeFragment(MyPlateFragment())
                    R.id.icon_notification -> if(it.itemId != this.selectedItemId) changeFragment(NotificationFragment())
                    R.id.icon_info -> if(it.itemId != this.selectedItemId) changeFragment(InfoFragment())
                }
                true
            }
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment)
            commit()
        }
    }

    fun changeAllPlateFragment(){
        binding.bottomNavigationView.selectedItemId = R.id.icon_feed
    }

    fun changeNotificationFragment(){
        binding.bottomNavigationView.selectedItemId = R.id.icon_notification
    }
}