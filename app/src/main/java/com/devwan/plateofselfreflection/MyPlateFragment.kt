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

class MyPlateFragment : Fragment() {

    private lateinit var mContext : Context
    private lateinit var recyclerView : RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private val viewModel : MyPlateViewModel by viewModels(
        factoryProducer = { SavedStateViewModelFactory(activity?.application,this) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView : View = inflater.inflate(R.layout.fragment_my_plate, container, false)

        recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView_myPlate)
        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(activity?.application)
            this.adapter = MyPlateAdapter(mContext, emptyList(),
                onClickIsOvercome = {
                    viewModel.checkIsOvercome(it)
                },
                onClickDelete = {
                    viewModel.deletePlate(it)
                })
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.plate.observe(viewLifecycleOwner){
            (recyclerView.adapter as MyPlateAdapter).setData(it)
        }
    }
}

