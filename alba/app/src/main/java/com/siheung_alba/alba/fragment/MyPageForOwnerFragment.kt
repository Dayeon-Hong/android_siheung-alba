package com.siheung_alba.alba.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.siheung_alba.alba.R
import com.siheung_alba.alba.activity.OwnerResumeActivity
import com.siheung_alba.alba.activity.OwnerUploadActivity
import com.siheung_alba.alba.activity.ResumeUploadActivity
import com.siheung_alba.alba.adapter.JobAdapter
import com.siheung_alba.alba.model.JobModel

class MyPageForOwnerFragment : Fragment() {

    private val db = Firebase.firestore
    private val colJobRef = db.collection("job")
    private val itemList = arrayListOf<JobModel>()
    private val adapter = JobAdapter(itemList)
    private val user_id = "apple" // 유저 아이디

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_my_page_for_owner, container, false)
        val uploadBtn : Button = view.findViewById(R.id.btn_upload)
        val toolbar : Toolbar = view.findViewById(R.id.ownerPageToolbar)

        toolbar.title = "마이페이지"
        toolbar.inflateMenu(R.menu.owner_page_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.owner_page_menu -> {
                    val intent = Intent(activity, OwnerResumeActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        setHasOptionsMenu(true)

        uploadBtn.setOnClickListener {
            val intent = Intent(activity, OwnerUploadActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val jobList = view?.findViewById<RecyclerView>(R.id.job_list)
        jobList?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        jobList?.adapter = adapter

        colJobRef
            .get()
            .addOnSuccessListener { result ->
                itemList.clear()
                for(document in result) {
                    if (document.data["user_id"].toString() == user_id) {
                        val item = JobModel(
                            document.data["title"].toString(),
                            document.data["add_text"].toString(),
                            document.data["term"].toString(),
                            document.data["money"].toString(),
                            document.data["nation"].toString(),
                            document.data["sex"].toString(),
                            document.data["updated_at"].toString()
                        )
                        itemList.add(item)
                    }
                    adapter.notifyDataSetChanged() // 리사이클러 뷰 갱신
                }
            }
            .addOnFailureListener { exception ->
                android.util.Log.w("MainActivity", "Error getting documents: $exception")
            }
    }

}