package com.task.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.task.LocationListData
import com.task.databinding.LocationItemLayoutBinding
import java.util.ArrayList

class LocationListAdapter (var context: Context, var list: ArrayList<LocationListData>,var listner:ItemList): RecyclerView.Adapter<LocationListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: LocationItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
            init {
                binding.ivDelete.setOnClickListener {
                    listner.deleteItem(adapterPosition)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LocationItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvLat.text="LAT ${list[position].lat}"
        holder.binding.tvLong.text="LNG ${list[position].lng}"
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ItemList{
        fun deleteItem(item:Int)
    }
}