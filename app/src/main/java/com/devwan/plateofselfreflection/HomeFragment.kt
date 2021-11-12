package com.devwan.plateofselfreflection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import com.devwan.plateofselfreflection.databinding.FragmentHomeBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    private lateinit var onAuthServiceListener : OnAuthServiceListener
    private lateinit var mContext: Context
    private lateinit var getResult: ActivityResultLauncher<Intent>
    private lateinit var motivationList : QuerySnapshot
    private var motivationIndex : Int = 0
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

        getResult = getActivityResultLauncher()

        initBtnSignOut()

        initBtnCreateUpdateActivity()

        initBtnRefreshMotivation()

        initCreateReviewActivity()

        initMotivationList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.stateSnapshot.observe(viewLifecycleOwner){
            binding.apply {
                nickName.text = it["nickName"].toString()
                textViewAllPlateNum.text = it["allPlateNum"].toString()
                textViewOvercomePlateNum.text = it["overcomePlateNum"].toString()
                setProgressBar(it["allPlateNum"] as Long, it["overcomePlateNum"] as Long)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getActivityResultLauncher() : ActivityResultLauncher<Intent>{
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                homeViewModel.getMyPlateStateSnapshot()
            }
        }
    }

    private fun initBtnSignOut(){
        binding.btnSignOut.setOnClickListener{
            onAuthServiceListener.signOut()
        }
    }

    private fun initBtnCreateUpdateActivity(){
        binding.btnCreateUpdateNickNameActivity.setOnClickListener {
            val intent = Intent(mContext, UpdateNickNameActivity::class.java)
            getResult.launch(intent)
        }
    }

    private fun initBtnRefreshMotivation(){
        binding.btnRefreshMotivation.setOnClickListener {
            if(motivationIndex == motivationList.size() - 1){
                motivationIndex = 0
            }else{
                motivationIndex++
            }
            setMotivation()
        }
    }

    private fun initCreateReviewActivity(){
        binding.btnNewMessage.setOnClickListener {
            val intent = Intent(mContext, ReviewActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initMotivationList(){
         CoroutineScope(Dispatchers.Main).launch {
             val firebaseRepo : FirebaseRepo = FirebaseRepo()
             var motivationQuerySnapshot : QuerySnapshot? = firebaseRepo.getMotivationListSnapshot()
             motivationQuerySnapshot?.let {
                motivationList = it
                motivationIndex = (Math.random() * motivationList.size()).toInt()
                setMotivation()
            }
        }
    }

    private fun setMotivation(){
        var motivationSnapshot : DocumentSnapshot = motivationList.elementAt(motivationIndex)
        binding.apply {
            textViewMotivationFirst.text = motivationSnapshot["first"].toString()
            textViewMotivationSecond.text = motivationSnapshot["second"].toString()
            textViewMotivationNickName.text = motivationSnapshot["nickName"].toString()
        }
    }

    private fun setProgressBar(allPlateNum : Long?, overcomePlateNum : Long?){
        allPlateNum?.let {
            binding.apply {
                if(allPlateNum.toInt() == 0){
                    cpbCirclebar.progress = 0
                }else{
                    overcomePlateNum?.let {
                        cpbCirclebar.progress = ( overcomePlateNum.toDouble() / allPlateNum.toInt() * 100 ).toInt()
                    }
                }
            }
        }
    }
}