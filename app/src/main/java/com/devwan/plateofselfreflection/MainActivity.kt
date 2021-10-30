package com.devwan.plateofselfreflection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initNavigationBar()
    }

    private fun initNavigationBar() {
        val bnv = findViewById<BottomNavigationView>(R.id.main_bnv)

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
}