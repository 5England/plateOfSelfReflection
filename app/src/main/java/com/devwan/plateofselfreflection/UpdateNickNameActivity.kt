package com.devwan.plateofselfreflection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.devwan.plateofselfreflection.databinding.ActivityUpdateNickNameBinding

class UpdateNickNameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateNickNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNickNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBtnFinishActivity()

        initBtnUpdateNickName()
    }

    private fun initBtnFinishActivity(){
        binding.btnFinishActivity.setOnClickListener {
            finish()
        }
    }

    private fun initBtnUpdateNickName(){
        binding.btnUpdateNickName.setOnClickListener {
            if(binding.editTextNickName.text.isNotBlank()){
                updateNickName()
            }else{
                Toast.makeText(baseContext, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateNickName(){
        val newNickName : String = binding.editTextNickName.text.toString().replace(" ", "")
        val firebaseRepo : FirebaseRepo = FirebaseRepo()
        firebaseRepo.setMyNickName(newNickName)
        finish()
    }
}