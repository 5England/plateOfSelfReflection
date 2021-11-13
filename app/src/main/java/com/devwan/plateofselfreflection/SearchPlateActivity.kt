package com.devwan.plateofselfreflection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.devwan.plateofselfreflection.databinding.ActivitySearchPlateBinding
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchPlateActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchPlateBinding
    private val firebaseRepo: FirebaseRepo = FirebaseRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchPlateBinding.inflate(layoutInflater)

        val keyword: String = intent.getStringExtra("keyword").toString()

        initRecyclerView(keyword)

        initBtnFinishActivity()
    }

    private fun initRecyclerView(keyword : String){
        GlobalScope.launch(Dispatchers.Main) {
            var searchPlateList : List<DocumentSnapshot> = firebaseRepo.getSearchPlateList(keyword)
            if(searchPlateList.isNotEmpty()){
                binding.recyclerViewSearchPlate.apply {
                    layoutManager = LinearLayoutManager(baseContext)
                    adapter = SearchPlateAdapter(baseContext, emptyList())
                    (adapter as SearchPlateAdapter).setData(searchPlateList)
                }
            }else{
                binding.apply {
                    recyclerViewSearchPlate.visibility = View.GONE
                    textViewEmptyListComment.visibility = View.VISIBLE
                }
            }
            setContentView(binding.root)
        }
    }

    private fun initBtnFinishActivity(){
        binding.btnFinishActivity.setOnClickListener {
            finish()
        }
    }
}
