package com.devwan.plateofselfreflection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import com.devwan.plateofselfreflection.databinding.FragmentHomeBinding
import com.dinuscxj.progressbar.CircleProgressBar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var motivationList : QuerySnapshot
    private var motivationIndex : Int = 0
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel : HomeViewModel by viewModels(
        factoryProducer = { SavedStateViewModelFactory(activity?.application, this) }
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        initBtnSearchPlate()

        initBtnRefreshMotivation()

        initMotivationList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.myStateSnapshot.observe(viewLifecycleOwner){
            binding.apply {
                nickName.text = it["nickName"].toString()
                textViewMyAllPlateNum.text = it["allPlateNum"].toString()
                textViewMyOvercomePlateNum.text = it["overcomePlateNum"].toString()
                setProgressBar(cpbMyCircleBar ,it["allPlateNum"] as Long, it["overcomePlateNum"] as Long)
            }
        }
        homeViewModel.allStateSnapshot.observe(viewLifecycleOwner){
            binding.apply {
                textViewAllPlateNum.text = it["allPlateNum"].toString()
                textViewOvercomePlateNum.text = it["overcomePlateNum"].toString()
                setProgressBar(cpbAllCircleBar, it["allPlateNum"] as Long, it["overcomePlateNum"] as Long)
            }
        }
    }

    private fun initBtnSearchPlate(){
        binding.apply {
            btnSearchPlate.setOnClickListener {
                if(editTextSearchPlate.text.isNotBlank()){
                    val intent = Intent(mContext, SearchPlateActivity::class.java)
                        .putExtra("keyword", editTextSearchPlate.text.toString())
                    editTextSearchPlate.text.clear()
                    startActivity(intent)
                }else{
                    Toast.makeText(mContext, "키워드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initBtnRefreshMotivation() {
        binding.btnRefreshMotivation.setOnClickListener {
            if (motivationIndex == motivationList.size() - 1) {
                motivationIndex = 0
            } else {
                motivationIndex++
            }
            setMotivation()
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

    private fun setProgressBar(progressBar: CircleProgressBar, allPlateNum : Long?, overcomePlateNum : Long?){
        allPlateNum?.let {
            if(allPlateNum.toInt() == 0){
                progressBar.progress = 0
            }else{
                overcomePlateNum?.let {
                    progressBar.progress = ( overcomePlateNum.toDouble() / allPlateNum.toInt() * 100 ).toInt()
                }
            }
        }
    }
}