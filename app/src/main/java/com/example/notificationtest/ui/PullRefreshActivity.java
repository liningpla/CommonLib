package com.example.notificationtest.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.common.BaseAcivity;
import com.common.log.SDLog;
import com.example.notificationtest.R;


public class PullRefreshActivity extends BaseAcivity {


    private WebView webview_test;
    private PullRefreshLayout swipeRefreshLayout;

    private boolean isCharge = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_refresh);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_WATER_DROP);
        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isCharge){
                    SDLog.i( "  loadUrl  = "+"https://www.qq.com/");
                    webview_test.loadUrl("https://www.qq.com/");
                }else{
                    SDLog.i("lining", "  loadUrl  = "+"http://www.baidu.com");
                    webview_test.loadUrl("http://www.baidu.com");
                }
                isCharge = !isCharge;
            }
        });
        int[] mColorSchemeColors = new int[]{
                Color.rgb(0x9F, 0x79, 0xEE),
                Color.rgb(0x37, 0x5B, 0xF1),
                Color.rgb(0xF7, 0xD2, 0x3E),
                Color.rgb(0x34, 0xA3, 0x50)
        };
        swipeRefreshLayout.setColorSchemeColors(mColorSchemeColors);

        webview_test = findViewById(R.id.webview_test);
        webview_test.setWebViewClient(new WebViewController());
        webview_test.loadUrl("http://www.baidu.com");
    }

    public class WebViewController extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}
