package com.task.utils

import android.app.Application
import com.task.AppDatabase
import com.task.roomDB.DefaultGroupDao

class ApplicationGlobal : Application() {
    companion object {
        var db: DefaultGroupDao? = null
    }

    override fun onCreate() {
        super.onCreate()
        db=  AppDatabase.getInstance(this)?.userDao()!!
    }
}