package com.devwan.plateofselfreflection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.lifecycleScope
import com.devwan.plateofselfreflection.databinding.FragmentHomeBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    private lateinit var onAuthServiceListener : OnAuthServiceListener
    private lateinit var mContext: Context
    private lateinit var getResult: ActivityResultLauncher<Intent>
    private lateinit var motiList : QuerySnapshot
    private var motiIndex : Int = 0
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        onAuthServiceListener = context as OnAuthServiceListener
    }

    private val homeViewModel : HomeViewModel by viewModels(
        factoryProducer = { SavedStateViewModelFactory(activity?.application, this) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.apply {
            btnSignOut.setOnClickListener{
                onAuthServiceListener.signOut()
            }

            btnChangeNickName.setOnClickListener {
                val intent = Intent(mContext, UpdateNickNameActivity::class.java)
                getResult.launch(intent)
            }

            btnRefreshMoti.setOnClickListener {
                if(motiIndex == motiList.size() - 1){
                    motiIndex = 0
                }else{
                    motiIndex++
                }
                setMoti()
            }

            btnNewMessage.setOnClickListener {
                val intent = Intent(mContext, ReviewActivity::class.java)
                startActivity(intent)
            }
        }

        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                homeViewModel.getMyPlateStateSnapshot()
            }
        }

        initMotiList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.stateSnapshot.observe(viewLifecycleOwner){
            binding.apply {
                nickName.text = it["nickName"].toString()
                plateNum.text = it["allPlateNum"].toString()
                overcomeNum.text = it["overcomePlateNum"].toString()
                setProgressBar(it["allPlateNum"] as Long, it["overcomePlateNum"] as Long)
                layoutHomeFragment.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setProgressBar(plateNum : Long?, overcomeNum : Long?){
        plateNum?.let {
            binding.apply {
                if(plateNum.toInt() == 0){
                    cpbCirclebar.progress = 0
                }else{
                    overcomeNum?.let {
                        cpbCirclebar.progress = ( overcomeNum.toDouble() / plateNum.toInt() * 100 ).toInt()
                    }
                }
            }
        }
    }

    private fun initMotiList(){
         CoroutineScope(Dispatchers.Main).launch {
            val firestoreRepo : FirestoreRepository = FirestoreRepository()
            var motiQuerySnapshot : QuerySnapshot? = firestoreRepo.getMotiListSnapshot()
            motiQuerySnapshot?.let {
                motiList = it
                motiIndex = (Math.random() * motiList.size()).toInt()
                setMoti()
            }
        }
    }

    private fun setMoti(){
        var motiSnapshot : DocumentSnapshot = motiList.elementAt(motiIndex)
        binding.apply {
            textViewFirstMoti.text = motiSnapshot["first"].toString()
            textViewSecondMoti.text = motiSnapshot["second"].toString()
            textViewNickNameMoti.text = motiSnapshot["nickName"].toString()
        }
    }
}