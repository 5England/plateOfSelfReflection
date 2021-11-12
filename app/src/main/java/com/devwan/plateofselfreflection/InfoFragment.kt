package com.devwan.plateofselfreflection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devwan.plateofselfreflection.databinding.FragmentInfoBinding


class InfoFragment : Fragment() {

    private lateinit var onAuthServiceListener : OnAuthServiceListener
    private lateinit var mContext: Context
    private var _binding : FragmentInfoBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        onAuthServiceListener = context as OnAuthServiceListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)

        initBtnSignOut()

        initBtnCreateUpdateActivity()

        initCreateReviewActivity()

        return binding.root
    }

    private fun initBtnSignOut(){
        binding.btnSignOut.setOnClickListener{
            onAuthServiceListener.signOut()
        }
    }

    private fun initBtnCreateUpdateActivity(){
        binding.btnCreateUpdateNickNameActivity.setOnClickListener {
            val intent = Intent(mContext, UpdateNickNameActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initCreateReviewActivity(){
        binding.btnNewMessage.setOnClickListener {
            val intent = Intent(mContext, ReviewActivity::class.java)
            startActivity(intent)
        }
    }
}