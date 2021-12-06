package com.devwan.plateofselfreflection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import com.devwan.plateofselfreflection.databinding.FragmentHomeBinding
import com.dinuscxj.progressbar.CircleProgressBar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var motivationList : QuerySnapshot
    private var motivationIndex : Int = 0
    lateinit var binding : FragmentHomeBinding

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
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initBtnSearchPlate()

        initBtnAllCategoryPlate()

        initBtnRefreshMotivation()

        initMotivationList()

        initBtnHomeNavigation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.myStateSnapshot.observe(viewLifecycleOwner){
            initMyStateView(it)
        }
        homeViewModel.allStateSnapshot.observe(viewLifecycleOwner){
            initAllStateView(it)
        }
    }

    private fun initMyStateView(snapshot : DocumentSnapshot){
        binding.apply {
            nickName.text = "안녕하세요. " + snapshot["nickName"].toString() + "님!"
            textViewMyAllPlateNum.text = snapshot["allPlateNum"].toString()
            textViewMyOvercomePlateNum.text = snapshot["overcomePlateNum"].toString()
            textViewMyStartDate.text = Plate.getStartTimeText((snapshot["startTime"] as Timestamp).toDate())
            textViewPlateComment.text = Plate.getHelloComment((snapshot["allPlateNum"] as Long).toInt())
            setProgressBar(cpbMyCircleBar ,snapshot["allPlateNum"] as Long, snapshot["overcomePlateNum"] as Long)
        }
    }

    private fun initAllStateView(snapshot : DocumentSnapshot){
        binding.apply {
            textViewAllPlateNum.text = snapshot["allPlateNum"].toString()
            textViewOvercomePlateNum.text = snapshot["overcomePlateNum"].toString()
            setProgressBar(cpbAllCircleBar, snapshot["allPlateNum"] as Long, snapshot["overcomePlateNum"] as Long)
        }
    }

    private fun initBtnHomeNavigation(){
        binding.btnSwitchAllPlateFragment.setOnClickListener {
            (activity as MainActivity).changeAllPlateFragment()
        }

        binding.btnSwitchNotificationFragment.setOnClickListener {
            (activity as MainActivity).changeNotificationFragment()
        }

        binding.btnWritePlate.setOnClickListener {
            val intent = Intent(mContext, SelectPlateCategoryActivity::class.java)
            startActivity(intent)
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

    private fun initBtnAllCategoryPlate(){
        binding.apply {
            initBtnCategoryPlate(btnCategoryWork)
            initBtnCategoryPlate(btnCategoryLife)
            initBtnCategoryPlate(btnCategoryHabit)
            initBtnCategoryPlate(btnCategoryHealth)
            initBtnCategoryPlate(btnCategorySpend)
        }
    }

    private fun initBtnCategoryPlate(btnCategory : LinearLayout){
        var category : String = ""

        when(btnCategory.id){
            R.id.btn_category_work -> category = "업무"
            R.id.btn_category_life -> category = "라이프"
            R.id.btn_category_habit -> category = "습관"
            R.id.btn_category_health -> category = "건강"
            R.id.btn_category_spend -> category = "소비"
        }

        btnCategory.setOnClickListener{
            val intent = Intent(mContext, SearchPlateActivity::class.java)
                .putExtra("keyword", category)
                .putExtra("isCategory", true)
            startActivity(intent)
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