package com.devwan.plateofselfreflection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.devwan.plateofselfreflection.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

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
            setCustomAnimations(R.anim.enter_from_down, R.anim.exit_to_down)
            replace(R.id.container, fragment)
            commit()
        }
    }
}