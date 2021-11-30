package com.devwan.plateofselfreflection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.devwan.plateofselfreflection.databinding.FragmentAllPlateBinding

class AllPlateFragment : Fragment(){

    private lateinit var mContext: Context
    private lateinit var getResult: ActivityResultLauncher<Intent>
    private var _binding : FragmentAllPlateBinding? = null
    private val binding get() = _binding!!
    private var isTimeListType = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private val allPlateViewModel : AllPlateViewModel by viewModels(
            factoryProducer = { SavedStateViewModelFactory(activity?.application, this) }
    )

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllPlateBinding.inflate(inflater, container, false)

        getResult = getActivityResultLauncher()

        initRecyclerView()

        initSwipeLayout()

        initBtnSetListTypeClickListener()

        initBtnLaunchUploadPlateActivity()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allPlateViewModel.plate.observe(viewLifecycleOwner){
            (binding.recyclerViewAllPlate.adapter as AllPlateAdapter).setData(it, isTimeListType)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getActivityResultLauncher() : ActivityResultLauncher<Intent>{
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                allPlateViewModel.getAllPlateList()
            }
        }
    }

    private fun initRecyclerView(){
        binding.recyclerViewAllPlate.apply {
            layoutManager = LinearLayoutManager(activity?.application)
            adapter = AllPlateAdapter(mContext, emptyList() , getResult)
        }
    }

    private fun initSwipeLayout(){
        binding.swipeLayout.apply {
            setOnRefreshListener {
                allPlateViewModel.getAllPlateList()
                binding.swipeLayout.isRefreshing = false
            }
            setColorSchemeColors(resources.getColor(R.color.green2))
        }
    }

    private fun initBtnSetListTypeClickListener(){
        binding.btnSetListType.setOnClickListener {
            if(isTimeListType) {
                binding.btnSetListType.setImageResource(R.drawable.icon_allplatefragment_getlikelist)
                Toast.makeText(mContext, "통감 순으로 피드를 확인해요.", Toast.LENGTH_SHORT).show()
            }else{
                binding.btnSetListType.setImageResource(R.drawable.icon_allplatefragment_gettimelist)
                Toast.makeText(mContext, "최근 순으로 피드를 확인해요.", Toast.LENGTH_SHORT).show()
            }
            isTimeListType = !isTimeListType
            allPlateViewModel.getAllPlateList()
        }
    }

    private fun initBtnLaunchUploadPlateActivity(){
        binding.btnCreateUploadActivity.setOnClickListener {
            val intent = Intent(mContext, SelectPlateCategoryActivity::class.java)
            getResult.launch(intent)
        }
    }
}