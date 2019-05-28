package com.kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.kotlin_main.*

class KTActivity : AppCompatActivity() {

    companion object {
        var URI = CommonDefine.HOST + "KTActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kotlin_main)

        btn_test.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
            Thread(Runnable {
                try {
                    Thread.sleep(2000)
                    MyViewModel.init(application).post(AccountBean("lining", "1234567"))
                }catch (e: Exception){
                }
            }).start()
        })

        MyViewModel.init(application).observe(this, Observer<AccountBean> {
            tv_content.text = it?.id + it?.name
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        Log.i("lining", "-KTActivity--onBackPressed-----")
    }

}
