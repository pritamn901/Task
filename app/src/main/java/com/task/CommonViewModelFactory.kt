package com.task

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.task.ui.MainActivityViewModal
import java.lang.IllegalArgumentException

class CommonViewModelFactory(private  val application: Application, private  val repository: Repository):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainActivityViewModal::class.java))
            return  MainActivityViewModal(application, repository) as T

        throw  IllegalArgumentException("Class not found")
    }
}