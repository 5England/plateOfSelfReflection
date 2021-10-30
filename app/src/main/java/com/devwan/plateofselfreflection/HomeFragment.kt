package com.devwan.plateofselfreflection

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class HomeFragment : Fragment() {

    lateinit var onAuthServiceListener : OnAuthServiceListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        onAuthServiceListener = context as OnAuthServiceListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView : View = inflater.inflate(R.layout.fragment_home, container, false)

        val signOutButton = rootView.findViewById<Button>(R.id.btn_signout)
        signOutButton.setOnClickListener{
            onAuthServiceListener.signOut()
        }

        return rootView
    }
}