package com.task.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.task.R
import com.task.databinding.FragmentListBinding


class ListFragment : Fragment(), LocationListAdapter.ItemList {
    lateinit var adapter:LocationListAdapter
    lateinit var binding:FragmentListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this
        binding=FragmentListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        adapter=LocationListAdapter(requireContext(),(requireActivity() as MainActivity).locationList,this)
        binding.rlList.adapter=adapter
    }

    override fun deleteItem(item: Int) {
        var ListItem=(requireActivity() as MainActivity).locationList[item]
        (requireActivity() as MainActivity).mainViewModal?.deleteLocation(ListItem.id)
        (requireActivity() as MainActivity).locationList.remove(ListItem)
        adapter.notifyItemRemoved(item)
    }

}