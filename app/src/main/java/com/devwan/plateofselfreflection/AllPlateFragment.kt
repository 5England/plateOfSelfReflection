package com.devwan.plateofselfreflection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

class AllPlateFragment : Fragment() {

    private lateinit var mContext: Context
    var isTimeListType = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private val allPlateViewModel : AllPlateViewModel by viewModels(
        factoryProducer = { SavedStateViewModelFactory(activity?.application,this) }
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
        val btnGetList = rootView.findViewById<Button>(R.id.btn_getList)

        btnCreateUploadActivity.setOnClickListener {
            val intent = Intent(mContext, UploadPlateActivity::class.java)
            startActivity(intent)
        }

        btnSetListType.setOnClickListener {
            if(isTimeListType) {
                btnSetListType.setImageResource(R.drawable.allplatefragment_icon_getlikelist)
                Toast.makeText(mContext, "통감 순으로 피드를 확인해요.", Toast.LENGTH_SHORT).show()
                isTimeListType = false
            }else{
                btnSetListType.setImageResource(R.drawable.allplatefragment_icon_gettimelist)
                Toast.makeText(mContext, "최근 순으로 피드를 확인해요.", Toast.LENGTH_SHORT).show()
                isTimeListType = true
            }
        }

        btnGetList.setOnClickListener {
            //allPlateViewModel.getList()
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allPlateViewModel.plate.observe(viewLifecycleOwner){

        }
    }
}