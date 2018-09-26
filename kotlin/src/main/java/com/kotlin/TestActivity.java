package com.kotlin;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {

    TextView tvContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        tvContent = findViewById(R.id.tv_content);

        MyViewModel.init(getApplication()).observe(this, new Observer<AccountBean>() {
            @Override
            public void onChanged(@Nullable AccountBean accountBean) {
                tvContent.setText(accountBean.getName()+""+accountBean.getId());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Log.i("lining", "-TestActivity--onBackPressed-----");
    }
}
