package com.risid.urp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.risid.util.Sp;
import com.risid.util.urlUtil;

import java.net.CookieHandler;
import java.net.CookieManager;

//import com.baidu.mobstat.StatService;

public class WebActivity extends AppCompatActivity {
    private WebView web;
    private String url;
    private String cookie;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        web = (WebView) findViewById(R.id.web);
        final ProgressBar bar = (ProgressBar)findViewById(R.id.myProgressBar);
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Intent intent=getIntent();
        if (intent.getStringExtra("URL")==null){
            url = "http://www.risid.com";
        }else if (intent.getStringExtra("URL").contains("xkAction")){
            //课表打开界面置cookie
            android.webkit.CookieManager cookieManager= android.webkit.CookieManager.getInstance();
            Sp sp=new Sp(WebActivity.this);
            cookie=sp.getCookie();
            cookieManager.setCookie(urlUtil.URL, cookie);
            url=intent.getStringExtra("URL");
        }else {
            url = urlUtil.URL_JWC +intent.getStringExtra("URL");
        }





        web.loadUrl(url);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

        });
//        Sp sp =new Sp(WebActivity.this);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBuiltInZoomControls(true);
//        CookieSyncManager.createInstance(sp.getCookie());
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    bar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

        });
    }

}
