package com.devwan.plateofselfreflection

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.devwan.plateofselfreflection.databinding.FragmentInfoBinding
import com.google.firebase.auth.FirebaseAuth


class InfoFragment : Fragment() {

    private lateinit var onAuthServiceListener : OnAuthServiceListener
    private lateinit var mContext: Context
    private var _binding : FragmentInfoBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        onAuthServiceListener = context as OnAuthServiceListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)

        initBtnCreateUpdateNickNameActivity()

        initBtnCreateReviewActivity()

        initBtnSignOut()

        initBtnLinkGoogleAccount()

        initBtnResign()

        return binding.root
    }

    private fun initBtnCreateUpdateNickNameActivity(){
        binding.btnCreateUpdateNickNameActivity.setOnClickListener {
            val intent = Intent(mContext, UpdateNickNameActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initBtnCreateReviewActivity(){
        binding.btnNewMessage.setOnClickListener {
            val intent = Intent(mContext, ReviewActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initBtnSignOut(){
        binding.btnSignOut.setOnClickListener{
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                if (user.isAnonymous) {
                    val dlg = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
                    dlg.apply {
                        setTitle("로그아웃")
                        setMessage("익명 사용자의 경우 기존 데이터가 유실될 수 있습니다.")
                        setPositiveButton("취소", DialogInterface.OnClickListener { dialog, which ->

                        })
                        setNegativeButton("로그아웃", DialogInterface.OnClickListener { dialog, which ->
                            onAuthServiceListener.signOut()
                        })
                        show()
                    }
                } else {
                    onAuthServiceListener.signOut()
                }
            }
        }
    }

    private fun initBtnLinkGoogleAccount(){
        binding.btnLinkGoogleAccount.setOnClickListener{
            FirebaseAuth.getInstance().currentUser?.let {
                if(it.isAnonymous){
                    val dlg = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
                    dlg.apply {
                        setTitle("계정 전환")
                        setMessage("기존 유저 데이터를 구글 계정으로 통합")
                        setPositiveButton("취소", DialogInterface.OnClickListener { dialog, which ->

                        })
                        setNegativeButton("전환", DialogInterface.OnClickListener { dialog, which ->
                            onAuthServiceListener.linkGoogleAccount()
                        })
                        show()
                    }
                }else{
                    Toast.makeText(mContext, "이미 구글 계정을 사용 중이시네요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initBtnResign(){
        binding.btnResign.setOnClickListener {
            val dlg = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
            dlg.apply {
                setTitle("회원 탈퇴")
                setMessage("기존의 데이터가 모두 삭제됩니다. 그래도 탈퇴하시겠어요?")
                setPositiveButton("취소", DialogInterface.OnClickListener { dialog, which ->

                })
                setNegativeButton("탈퇴", DialogInterface.OnClickListener { dialog, which ->
                    onAuthServiceListener.resignAccount()
                })
                show()
            }
        }
    }
}