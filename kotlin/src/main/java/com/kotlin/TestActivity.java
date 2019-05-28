package com.kotlin;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

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
