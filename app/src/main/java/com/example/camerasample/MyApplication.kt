package com.example.camerasample

import android.app.Application
import android.content.Context
import io.realm.Realm

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()

        //Realmの初期化
        Realm.init(this)

        appContext = this
    }


    companion object{
        lateinit var appContext:Context
    }
}