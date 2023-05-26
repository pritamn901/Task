package com.task.ui

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.LocationListData
import com.task.Repository
import com.task.utils.ApplicationGlobal.Companion.db
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModal (var application: Application, var repository: Repository): ViewModel(){
    var locationListResponse= MutableLiveData<ArrayList<LocationListData>>()

    fun getLocationList() {
        viewModelScope.launch(Dispatchers.IO)
        {
            withContext(Dispatchers.Main){
                locationListResponse.value=db?.getLocations() as ArrayList<LocationListData>
            }
        }
    }

    fun addLocation(data: ArrayList<LocationListData>){
        viewModelScope.launch {
            db?.addLocation(data)
        }
    }
    fun deleteLocation(id: Int){
        viewModelScope.launch {
            db?.deleteLocation(id)
        }
    }

}