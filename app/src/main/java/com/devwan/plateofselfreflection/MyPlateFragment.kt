package com.devwan.plateofselfreflection

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devwan.plateofselfreflection.databinding.FragmentMyPlateBinding
import com.dinuscxj.progressbar.CircleProgressBar
import com.google.firebase.firestore.DocumentSnapshot
import org.w3c.dom.Document

class MyPlateFragment : Fragment(){

    private lateinit var mContext : Context
    private var _binding : FragmentMyPlateBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private val myPlateViewModel : MyPlateViewModel by viewModels(
        factoryProducer = { SavedStateViewModelFactory(activity?.application,this) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPlateBinding.inflate(inflater, container, false)

        initRecyclerView()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myPlateViewModel.plate.observe(viewLifecycleOwner){
            (binding.recyclerViewMyPlate.adapter as MyPlateAdapter).setData(it)
        }
        myPlateViewModel.myStateSnapshot.observe(viewLifecycleOwner){
            refreshMyStateView(it)
        }
    }

    private fun refreshMyStateView(snapshot : DocumentSnapshot){
        binding.apply {
            textViewMyAllPlateNum.text = snapshot["allPlateNum"].toString()
            textViewMyOvercomePlateNum.text = snapshot["overcomePlateNum"].toString()
            if((snapshot["allPlateNum"] as Long).toInt() == 0){
                recyclerViewMyPlate.visibility = View.GONE
                textViewEmptyListComment.visibility = View.VISIBLE
            }
        }
    }

    private fun initRecyclerView(){
        binding.recyclerViewMyPlate.apply {
            layoutManager = LinearLayoutManager(activity?.application)
            adapter = MyPlateAdapter(mContext, emptyList(), myPlateViewModel)
        }
    }
}

