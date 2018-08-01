package com.example.notificationtest;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.common.BaseAcivity;


public class SetInfoActivity extends BaseAcivity {

    private TextView tv_atc_title;
    private TextView tv_atc_msg;
    private CheckBox cb_atc;
    private TextView tv_atc_cancle;
    private TextView tv_atc_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_cta_view);

        tv_atc_title = findViewById(R.id.tv_atc_title);
        tv_atc_msg = findViewById(R.id.tv_atc_msg);
        cb_atc = findViewById(R.id.cb_atc);
        tv_atc_cancle = findViewById(R.id.tv_atc_cancle);
        tv_atc_cancle.setOnClickListener(onClickListener);
        tv_atc_ok = findViewById(R.id.tv_atc_ok);
        tv_atc_ok.setOnClickListener(onClickListener);
        bindData();
    }

    /**绑定数据*/
    private void bindData(){
        String title = getResources().getString(R.string.cta_dialog_title);
        String msg1 = getResources().getString(R.string.cta_dialog_message_net_title);
        String subMsg1 = getResources().getString(R.string.cta_dialog_message_net_content);
        String msg2 = getResources().getString(R.string.cta_dialog_message_vpn_title);
        String subMsg2 = getResources().getString(R.string.cta_dialog_message_vpn_content);
        String msg3 = getResources().getString(R.string.cta_dialog_message_games_title);
        String subMsg3 = getResources().getString(R.string.cta_dialog_message_games_content);


        String msg = "<font color='#191919'>" + msg1 + "</font>";
        msg += "<br/>";
        msg += "<font color='#666666'>" + subMsg1 + "</font>";
        msg += "<br/><br/>";
        msg += "<font color='#191919'>" + msg2 + "</font>";
        msg += "<br>";
        msg += "<font color='#666666'>" + subMsg2 + "</font>";
        msg += "<br/><br/>";
        msg += "<font color='#191919'>" + msg3 + "</font>";
        msg += "<br>";
        msg += "<font color='#666666'>" + subMsg3 + "</font>";
        tv_atc_title.setText(title);
        tv_atc_msg.setText(Html.fromHtml(msg));
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_atc_cancle:
                    finish();
                    break;

                case R.id.tv_atc_ok:
                    finish();
                    break;
            }
        }
    };

}
