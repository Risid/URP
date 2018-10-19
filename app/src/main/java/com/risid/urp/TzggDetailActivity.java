package com.risid.urp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.risid.Interface.GetNetData;
import com.risid.other.MJavascriptInterface;
import com.risid.util.StringUtils;
import com.risid.util.netUtil;
import com.risid.util.urlUtil;


/**
 * Created by Risid on 2017/3/25.
 */
@SuppressLint("SetJavaScriptEnabled")
public class TzggDetailActivity extends SwipeBackAppActivity implements GetNetData {

    private Toolbar toolbar;
    private WebView contentWebView;
    private String url;
    private SwipeRefreshLayout refreshLayout;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case urlUtil.DATA_SUCCESS:
                    if (refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                    }

                    contentWebView.getSettings().setJavaScriptEnabled(true);
                    String html = StringUtils.returnDetailFromHtml(msg.obj.toString());
                    toolbar.setTitle(StringUtils.returnTitle(html));

                    contentWebView.loadData(html, "text/html; charset=UTF-8", null);
                    String[] imageUrls = StringUtils.returnImageUrlsFromHtml(html);
                    contentWebView.addJavascriptInterface(new MJavascriptInterface(TzggDetailActivity.this, imageUrls), "imagelistener");
                    contentWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {

                            view.getSettings().setJavaScriptEnabled(true);
                            super.onPageFinished(view, url);
                            addImageClickListener(view);//待网页加载完全后设置图片点击的监听方法
                        }

                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            view.getSettings().setJavaScriptEnabled(true);
                            super.onPageStarted(view, url, favicon);
                        }

                        private void addImageClickListener(WebView webView) {
                            webView.loadUrl("javascript:(function(){" +
                                    "var objs = document.getElementsByTagName(\"img\"); " +
                                    "for(var i=0;i<objs.length;i++)  " +
                                    "{"
                                    + "    objs[i].onclick=function()  " +
                                    "    {  "
                                    + "        window.imagelistener.openImage(this.src);  " +//通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
                                    "    }  " +
                                    "}" +
                                    "})()");
                        }
                    });
                    contentWebView.setDownloadListener(new DownloadListener() {
                        @Override
                        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    });

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tzgg_detail);
        contentWebView = (WebView) findViewById(R.id.webView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_web);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHtml();
            }
        });
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent=getIntent();
        url = intent.getStringExtra("URL");
        getHtml();


    }

    private void getHtml() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                netUtil.getPostData(urlUtil.URL_JWC + url, "", TzggDetailActivity.this);
            }
        }.start();
    }


    @Override
    protected void onDestroy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(TzggDetailActivity.this).clearDiskCache();//清理磁盘缓存需要在子线程中执行
            }
        }).start();
        Glide.get(this).clearMemory();//清理内存缓存可以在UI主线程中进行
        super.onDestroy();
    }

    @Override
    public void getDataSuccess(String Data) {
        Message message = new Message();
        message.obj = Data;
        message.what = urlUtil.DATA_SUCCESS;
        handler.sendMessage(message);
    }

    @Override
    public void getDataFail() {
        Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void getDataSession() {

    }

    @Override
    public void getCookie(String data) {

    }
}
