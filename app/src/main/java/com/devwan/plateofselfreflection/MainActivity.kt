package com.devwan.plateofselfreflection

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), OnAuthServiceListener{

    private val firebaseAuthentication = FirebaseAuthentication(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(isLoggedIn()) {
            initNavigationBar()
        }else{
            firebaseAuthentication.signIn()
        }
    }

    private fun isLoggedIn() : Boolean{
        var isLoggedIn : Boolean = false
        val uid : String? = getSharedPreferences("authSP", Context.MODE_PRIVATE).getString("uid", null)
        uid?.let { isLoggedIn = true }
        return isLoggedIn
    }

    private fun initNavigationBar() {
        val bnv = findViewById<BottomNavigationView>(R.id.bnv_main)

        bnv.run {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.icon_home -> changeFragment(HomeFragment())
                    R.id.icon_feed -> changeFragment(AllPlateFragment())
                    R.id.icon_myFeed -> changeFragment(MyPlateFragment())
                }
                true
            }
            selectedItemId = R.id.icon_home
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    override fun onSignInComplete() {
        initNavigationBar()
    }

    override fun signOut() {
        firebaseAuthentication.signOut()
    }
}