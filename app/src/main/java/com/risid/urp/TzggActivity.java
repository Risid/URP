package com.risid.urp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.googlecode.leptonica.android.Pix;
import com.risid.Interface.GetNetData;
import com.risid.adapter.TzggAdapter;
import com.risid.adapter.XfjdAdapter;
import com.risid.models.TzggModels;
import com.risid.util.netUtil;
import com.risid.util.urlUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


public class TzggActivity extends SwipeBackAppActivity implements GetNetData {
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private RecyclerView rv_tzgg;
    private TzggAdapter tzggAdapter;
    private String nextUrl = "tzgg.htm";
    private List<TzggModels> list_tz = new ArrayList<>();
    private int count = 0;
    private boolean isLoading = false;
    private
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            switch (msg.what) {
                case 1:
                    if (nextUrl.equals("tzgg.htm")){
                        tzggAdapter.notifyItemRangeInserted(list_tz.size() - count, count);

                    }else {
                        setRecyclerView();
                    }



                    break;
                case 2:
                    Snackbar.make(rv_tzgg, "获取数据失败", Snackbar.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    private void setRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        tzggAdapter = new TzggAdapter(this, list_tz);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(tzggAdapter);
        alphaAdapter.setFirstOnly(true);

        alphaAdapter.setDuration(500);

        rv_tzgg.setLayoutManager(mLayoutManager);
        rv_tzgg.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
//        SlideInUpAnimator animator = new SlideInUpAnimator(new LandingAnimator());
//        rv_tzgg.setItemAnimator(new FadeInAnimator());
//        rv_tzgg.getItemAnimator().setAddDuration(1000);

        rv_tzgg.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rv_tzgg.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastPosition;

                //当前状态为停止滑动状态SCROLL_STATE_IDLE时
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                    lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();


                    //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
                    //如果相等则说明已经滑动到最后了
                    if(lastPosition == recyclerView.getLayoutManager().getItemCount()-1 && !isLoading){
                        getInfo(nextUrl);
                    }

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tzgg);
        initView();
        getInfo(nextUrl);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressDialog = new ProgressDialog(TzggActivity.this);
        progressDialog.setMessage(getString(R.string.getData));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
        rv_tzgg = (RecyclerView) findViewById(R.id.rv_tzgg);


    }

    public void getInfo(final String page) {
        isLoading = true;
        new Thread() {
            public void run() {
                netUtil.getPostData(urlUtil.URL_JWC + page, "", TzggActivity.this);

            }
        }.start();
    }

    @Override
    public void getDataSuccess(String Data) {


        Document document = Jsoup.parse(Data);

        nextUrl = document.getElementsByClass("Next").get(0).attr("href");
        Elements es = document.select("[style=height: 310px]").select("table[align=center]").select("tbody").select("tr");
        count = es.size();
        // Log.d("内容",es.toString());
        for (int i = 0; i < es.size(); i++) {
            Elements elements = es.get(i).getElementsByTag("span");

            String linkHref = elements.select("a").attr("href");
            String time = elements.get(1).text();
            String title = elements.get(0).text();

            TzggModels tzggModels = new TzggModels(title, time, linkHref);
            list_tz.add(tzggModels);
        }
        handler.sendEmptyMessage(1);

    }

    @Override
    public void getDataFail() {

    }

    @Override
    public void getDataSession() {

    }

    @Override
    public void getCookie(String data) {

    }
}
