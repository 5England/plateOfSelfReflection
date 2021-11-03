package com.devwan.plateofselfreflection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class AllPlateFragment : Fragment(){
    private lateinit var recyclerView : RecyclerView
    private lateinit var mContext: Context
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var isTimeListType = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private val allPlateViewModel : AllPlateViewModel by viewModels(
            factoryProducer = { SavedStateViewModelFactory(activity?.application, this) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootView : View = inflater.inflate(R.layout.fragment_all_plate, container, false)
        val btnCreateUploadActivity = rootView.findViewById<ImageButton>(R.id.btn_createUploadActivity)
        val btnSetListType = rootView.findViewById<ImageButton>(R.id.btn_setListType)

        recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView_allPlate)
        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(activity?.application)
            this.adapter = com.devwan.plateofselfreflection.AllPlateAdapter(emptyList(), mContext)
        }

        swipeRefreshLayout = rootView.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.apply {
            setOnRefreshListener {
                allPlateViewModel.getAllPlateList()
                swipeRefreshLayout.isRefreshing = false
            }
            setColorSchemeColors(resources.getColor(R.color.orange))
        }

        btnCreateUploadActivity.setOnClickListener {
            val intent = Intent(mContext, UploadPlateActivity::class.java)
            startActivity(intent)
        }

        btnSetListType.setOnClickListener {
            if(isTimeListType) {
                btnSetListType.setImageResource(R.drawable.allplatefragment_icon_getlikelist)
                Toast.makeText(mContext, "통감 순으로 피드를 확인해요.", Toast.LENGTH_SHORT).show()
            }else{
                btnSetListType.setImageResource(R.drawable.allplatefragment_icon_gettimelist)
                Toast.makeText(mContext, "최근 순으로 피드를 확인해요.", Toast.LENGTH_SHORT).show()
            }
            isTimeListType = !isTimeListType
            allPlateViewModel.getAllPlateList()
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allPlateViewModel.plate.observe(viewLifecycleOwner){
            (recyclerView.adapter as AllPlateAdapter).setData(it, isTimeListType)
        }
    }
}