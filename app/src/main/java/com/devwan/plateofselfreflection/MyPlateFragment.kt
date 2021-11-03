package com.devwan.plateofselfreflection

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

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
                    onClickCheckIsOvercome = {
                        viewModel.checkIsOvercome(it)
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

class MyPlateAdapter(private val mContext: Context, private var plateList: List<DocumentSnapshot>,
                     private val onClickCheckIsOvercome : (plate : DocumentSnapshot) -> Unit) :
        RecyclerView.Adapter<MyPlateAdapter.ViewHolder>() {

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
        val plate = plateList[position]
        bindData(viewHolder, plateList[position])
    }

    override fun getItemCount() = plateList.size

    fun setData(newData: List<DocumentSnapshot>) {
        plateList = newData.sortedByDescending { (it["uploadTime"] as Timestamp).toDate() }
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
        else viewHolder.isOvercome?.setImageResource(R.drawable.cardplate_icon_isovercome_false)

        //layout 클릭 시 PlateActivity 생성, 인텐트로 데이터 전송

        viewHolder.isOvercome?.setOnClickListener{
            val dlg = AlertDialog.Builder(mContext, R.style.AlertDialogStyle)

            if(plateDocumentSnapshot["isOvercome"] as Boolean){
                dlg.apply {
                    setTitle("반성이 부족하셨나요?")
                    setMessage("원하면 반성을 취소할 수 있어요.                 ")
                    setPositiveButton("아니요", DialogInterface.OnClickListener { dialog, which -> })
                    setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                        onClickCheckIsOvercome.invoke(plateDocumentSnapshot)
                    })
                    show()
                }
            }else{
                onClickCheckIsOvercome.invoke(plateDocumentSnapshot)
                dlg.apply {
                    setTitle("극복했어요")
                    setMessage("반성 극복에 대한 경험과 팁을 작성해 공유할 수 있어요.")
                    setPositiveButton("아니요", DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(mContext, "나중에 다시 작성하실 수 있어요.", Toast.LENGTH_SHORT).show()
                    })
                    setNegativeButton("작성할게요", DialogInterface.OnClickListener { dialog, which ->
                        //액티비티 띄우기
                    })
                    show()
                }
            }
        }
    }
}
