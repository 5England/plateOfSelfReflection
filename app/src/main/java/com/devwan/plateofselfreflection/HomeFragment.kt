package com.devwan.plateofselfreflection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import com.devwan.plateofselfreflection.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var onAuthServiceListener : OnAuthServiceListener
    private lateinit var mContext: Context
    private lateinit var getResult: ActivityResultLauncher<Intent>
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        onAuthServiceListener = context as OnAuthServiceListener
    }

    private val viewModel : HomeViewModel by viewModels(
        factoryProducer = { SavedStateViewModelFactory(activity?.application, this) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnSignOut.setOnClickListener{
            onAuthServiceListener.signOut()
        }

        binding.btnChangeNickName.setOnClickListener {
            val intent = Intent(mContext, UpdateNickNameActivity::class.java)
            getResult.launch(intent)
        }

        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                viewModel.getMyPlateStateSnapshot()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.stateSnapshot.observe(viewLifecycleOwner){
            binding.nickName.text = it["nickName"] as String
            binding.plateNum.text = it["allPlateNum"].toString()
            binding.overcomeNum.text = it["overcomePlateNum"].toString()
            setProgressBar(it["allPlateNum"] as Long, it["overcomePlateNum"] as Long)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setProgressBar(plateNum : Long?, overcomeNum : Long?){
        plateNum?.let {
            if(plateNum.toInt() == 0){
                binding.cpbCirclebar.progress = 0
            }else{
                overcomeNum?.let {
                    binding.cpbCirclebar.progress = ( overcomeNum.toDouble() / plateNum.toInt() * 100 ).toInt()
                }
            }
        }
    }
}