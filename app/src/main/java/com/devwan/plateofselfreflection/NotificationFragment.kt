package com.devwan.plateofselfreflection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devwan.plateofselfreflection.databinding.CardNotificationBinding
import com.devwan.plateofselfreflection.databinding.FragmentNotificationBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotificationFragment : Fragment() {
    private lateinit var mContext: Context
    private lateinit var getResult: ActivityResultLauncher<Intent>
    private var _binding : FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private val notificationViewModel : NotificationViewModel by viewModels(
        factoryProducer = { SavedStateViewModelFactory(activity?.application, this) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        getResult = getActivityResultLauncher()

        initRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationViewModel.comment.observe(viewLifecycleOwner){
            (binding.recyclerViewNotification.adapter as NotificationAdapter).setData(getSnapshotList(it))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getActivityResultLauncher() : ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                notificationViewModel.getNewCommentList()
            }
        }
    }

    private fun initRecyclerView(){
        binding.recyclerViewNotification.apply {
            layoutManager = LinearLayoutManager(activity?.application)
            adapter = NotificationAdapter(mContext, emptyList() , getResult)
        }
    }

    private fun getSnapshotList(documents : QuerySnapshot) : List<DocumentSnapshot>{
        var snapshotList = mutableListOf<DocumentSnapshot>()
        documents.forEach { document ->
            snapshotList.add(document)
        }
        return snapshotList.sortedBy { (it["uploadTime"] as Timestamp).toDate() }
    }
}

class NotificationAdapter(private val mContext: Context, private var commentList: List<DocumentSnapshot>, private val resultLauncher : ActivityResultLauncher<Intent>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding : CardNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        bindData(viewHolder, commentList[position])
    }

    override fun getItemCount() = commentList.size

    private fun bindData(viewHolder: ViewHolder, commentSnapshot: DocumentSnapshot) {
        val comment = commentSnapshot["comment"] as String
        val nickName = commentSnapshot["nickName"] as String
        val uploadTime = Plate.getUploadTimeText((commentSnapshot["uploadTime"] as Timestamp).toDate())

        viewHolder.binding.apply {
            textViewCommentText.text = comment
            textViewCommentNickname.text = nickName
            textViewCommentUploadTime.text = uploadTime
        }
    }

    fun setData(newData: List<DocumentSnapshot>) {
        commentList = newData.sortedByDescending { (it["uploadTime"] as Timestamp).toDate() }
        notifyDataSetChanged()
    }
}