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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

class AllPlateFragment : Fragment(), AllPlateAdapter.OnGetListType {

    private lateinit var recyclerView : RecyclerView
    private lateinit var mContext: Context
    var isTimeListType = true

    override fun isTimeTypeList(): Boolean {
        return isTimeListType
    }

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
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView_allPlate)
        val btnCreateUploadActivity = rootView.findViewById<ImageButton>(R.id.btn_createUploadActivity)
        val btnSetListType = rootView.findViewById<ImageButton>(R.id.btn_setListType)
        val btnGetList = rootView.findViewById<Button>(R.id.btn_getList)

        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(activity?.application)
            this.adapter = com.devwan.plateofselfreflection.AllPlateAdapter(emptyList(), mContext)
        }

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
            (recyclerView.adapter as AllPlateAdapter).setData(it)
        }
    }
}

class AllPlateAdapter(private var plateList: List<DocumentSnapshot>, private var mContext : Context) :
        RecyclerView.Adapter<AllPlateAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var isOvercome: ImageView? = null
        var title: TextView? = null
        var mainText: TextView? = null
        var uploadTime: TextView? = null
        var like: TextView? = null

        init {
            isOvercome = view.findViewById(R.id.img_isOvercome)
            title = view.findViewById(R.id.text_title)
            mainText = view.findViewById(R.id.text_mainText)
            uploadTime = view.findViewById(R.id.text_uploadTime)
            like = view.findViewById(R.id.text_like)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.card_plate, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        bindData(viewHolder, plateList[position])
    }

    override fun getItemCount() = plateList.size

    interface OnGetListType{ fun isTimeTypeList() : Boolean }
    private val onGetListType : OnGetListType = mContext as OnGetListType

    fun setData(newData: List<DocumentSnapshot>) {
        if(onGetListType.isTimeTypeList()){
            plateList = getTimeTypeList(newData)
        }else{
            plateList = getLikeTypeList(newData)
        }

        notifyDataSetChanged()
    }

    private fun bindData(viewHolder: ViewHolder, plateDocumentSnapshot: DocumentSnapshot) {
        val title: String = plateDocumentSnapshot["title"].toString() ?: ""
        val mainText: String = plateDocumentSnapshot["mainText"].toString() ?: ""
        val like: String = plateDocumentSnapshot["like"].toString() ?: ""
        val uploadTime: String = Plate.getUploadTimeText((plateDocumentSnapshot["uploadTime"] as Timestamp).toDate())
        val isOvercome: Boolean = plateDocumentSnapshot["isOvercome"] as Boolean

        viewHolder.title?.text = title
        viewHolder.mainText?.text = mainText
        viewHolder.like?.text = like
        viewHolder.uploadTime?.text = uploadTime
        if (isOvercome) viewHolder.isOvercome?.setImageResource(R.drawable.cardplate_icon_isovercome_true)

        //layout 클릭 시 PlateActivity 생성, 인텐트로 데이터 전송
    }

    private fun getTimeTypeList(newData: List<DocumentSnapshot>) : List<DocumentSnapshot>{
        return newData.sortedByDescending { (it["uploadTime"] as Timestamp).toDate() }
    }

    private fun getLikeTypeList(newData: List<DocumentSnapshot>) : List<DocumentSnapshot>{
        return newData.sortedByDescending { (it["uploadTime"] as Timestamp).toDate() }.sortedByDescending { it["like"] as Long }
    }
}