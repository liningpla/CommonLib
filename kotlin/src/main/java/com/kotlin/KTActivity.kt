package com.kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class KTActivity : AppCompatActivity() {

    companion object {
        var URI = CommonDefine.HOST + "KTActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kotlin_main)
    }


}
