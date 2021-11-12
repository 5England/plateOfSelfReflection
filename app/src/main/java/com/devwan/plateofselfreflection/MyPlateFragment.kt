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

class MyPlateFragment : Fragment() {

    private lateinit var mContext : Context
    private var _binding : FragmentMyPlateBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private val viewModel : MyPlateViewModel by viewModels(
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.plate.observe(viewLifecycleOwner){
            (binding.recyclerViewMyPlate.adapter as MyPlateAdapter).setData(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView(){
        binding.recyclerViewMyPlate.apply {
            layoutManager = LinearLayoutManager(activity?.application)
            adapter = MyPlateAdapter(mContext, emptyList(),
                onClickIsOvercome = {
                    viewModel.checkIsOvercome(it)
                },
                onClickDelete = {
                    viewModel.deletePlate(it)
                })
        }
    }
}

