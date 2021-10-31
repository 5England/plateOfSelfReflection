package com.devwan.plateofselfreflection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory

class AllPlateFragment : Fragment() {

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private val viewModel : AllPlateViewModel by viewModels(
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
        val btnCreateUploadActivity = rootView.findViewById<ImageButton>(R.id.btn_create_upload_activity)

        btnCreateUploadActivity.setOnClickListener {
            val intent = Intent(mContext, UploadPlateActivity::class.java)
            startActivity(intent)
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.plate.observe(viewLifecycleOwner){

        }
    }
}