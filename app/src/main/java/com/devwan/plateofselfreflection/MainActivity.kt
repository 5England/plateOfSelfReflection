package com.devwan.plateofselfreflection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.devwan.plateofselfreflection.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), OnAuthServiceListener{

    private lateinit var binding : ActivityMainBinding
    private val firebaseAuthentication = FirebaseAuthentication(this)
    private var curFragmentNum = 0

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

    private fun initNavigationBar() {
        binding.bottomNavigationView.run {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.icon_home -> changeFragment(HomeFragment(), 0)
                    R.id.icon_feed -> changeFragment(AllPlateFragment(), 1)
                    R.id.icon_myFeed -> changeFragment(MyPlateFragment(), 2)
                }
                true
            }
            selectedItemId = R.id.icon_home
        }
    }

    private fun changeFragment(fragment: Fragment, newFragmentNum : Int) {
        supportFragmentManager.beginTransaction().apply {
            if(curFragmentNum < newFragmentNum){
                setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left)
            }else if(curFragmentNum > newFragmentNum){
                setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
            }else{
                setCustomAnimations(R.anim.enter_from_down, R.anim.exit_to_down)
            }

            replace(R.id.container, fragment)
            commit()
        }

        curFragmentNum = newFragmentNum
    }

    override fun onSignInComplete() {
        setContentView(binding.root)
        initNavigationBar()
    }

    override fun signOut() {
        firebaseAuthentication.signOut()
    }
}