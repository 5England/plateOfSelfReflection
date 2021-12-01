package com.devwan.plateofselfreflection

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.devwan.plateofselfreflection.databinding.CardPlateBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

class MyPlateAdapter(mContext: Context, plateList: List<DocumentSnapshot>, private val myPlateViewModel : MyPlateViewModel
) : PlateAdapter(mContext, plateList){

    override fun bindData(plateSnapshot: DocumentSnapshot) {
        super.bindData(plateSnapshot)

        getBinding().imgIsOvercome.setOnClickListener{
                initIsOvercomeAlertDialog(plateSnapshot)
            }

        getBinding().layoutCardView.setOnLongClickListener {
            initUpdateAlertDialog(plateSnapshot)
            true
        }
    }

    override fun initCardViewClickListener(
        binding: CardPlateBinding,
        plateSnapshot: DocumentSnapshot
    ) {
        super.initCardViewClickListener(binding, plateSnapshot)
        binding.layoutCardView.setOnClickListener{
            val intent = Intent(mContext, PlateActivity::class.java)
            mContext.startActivity(intent.putExtra("snapshotId", plateSnapshot.id))
        }
    }

    private fun initIsOvercomeAlertDialog(plateSnapshot : DocumentSnapshot){
        val dlg = AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
        if(plateSnapshot["isOvercome"] as Boolean){
            dlg.apply {
                setTitle("반성이 부족하셨나요?")
                setMessage("원하면 반성을 취소할 수 있어요.                 ")
                setPositiveButton("아니요", DialogInterface.OnClickListener { dialog, which -> })
                setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                    myPlateViewModel.checkIsOvercome(plateSnapshot)
                })
                show()
            }
        }else{
            myPlateViewModel.checkIsOvercome(plateSnapshot)
            dlg.apply {
                setTitle("개선 후기 작성")
                setMessage("반성 개선에 대한 후기를 작성해 사람들에게 도움을 줄 수 있어요.")
                setPositiveButton("괜찮아요", DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(mContext, "나중에 다시 작성하실 수 있어요.", Toast.LENGTH_SHORT).show()
                })
                setNegativeButton("작성", DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(mContext, UploadFeedbackActivity::class.java)
                    intent.putExtra("snapshotId", plateSnapshot.id)
                    mContext.startActivity(intent)
                })
                show()
            }
        }
    }

    private fun initUpdateAlertDialog(plateSnapshot : DocumentSnapshot){
        val dlg = AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
        dlg.apply {
            setTitle("수정/삭제")
            setMessage("수정 및 삭제 시, 복구할 수 없어요.                         ")
            setPositiveButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                myPlateViewModel.deletePlate(plateSnapshot)
            })
            setNegativeButton("수정", DialogInterface.OnClickListener { dialog, which ->
                val intent = Intent(mContext, UpdatePlateActivity::class.java).apply {
                    putExtra("snapshotId", plateSnapshot.id)
                    putExtra("snapshotTitle", plateSnapshot["title"].toString())
                    putExtra("snapshotMainText", plateSnapshot["mainText"].toString())
                }
                mContext.startActivity(intent)
            })
            show()
        }
    }
}

