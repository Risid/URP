package com.risid.urp;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.risid.Interface.GetNetData;
import com.risid.Interface.GetZpInterface;
import com.risid.LoginThread;
import com.risid.adapter.XjxxAdaper;
import com.risid.models.XjxxModels;
import com.risid.util.ImageUtils;
import com.risid.util.MapUtil;
import com.risid.util.Sp;
import com.risid.util.netUtil;
import com.risid.util.urlUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

//import com.baidu.mobstat.StatService;

public class XjxxActivity extends SwipeBackAppActivity implements GetNetData{

    private String cookie;
    private String tv;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private RecyclerView rv_xjxx;
    private List<XjxxModels> lists = new ArrayList<>();
    private Sp sp;

    public void reLogin() {
        progressDialog.show();
        new LoginThread(this, MapUtil.LoginMap(sp.getZjh(), sp.getMm()), handler).start();
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case urlUtil.LOGIN_SUCCESS:
                    getInfo();
                    break;
                case urlUtil.DATA_SUCCESS:

                    if (tv == null) {
                        System.out.println("空");
                    } else {
                        progressDialog.dismiss();
                        Document doc = Jsoup.parse(tv);
                        if (getString(R.string.webTitle).equals(doc.title())) {
                            reLogin();
                        } else {
                            Elements es = doc.select("table[id=tblView]");
                            if (es.size() > 0) {
                                Elements es_2 = es.get(0).select("tr");
                                for (int i = 0; i < es_2.size(); i++) {
                                    Elements es_3 = es_2.get(i).select("td");
                                    for (int j = 0; j < es_3.size() - 1; j++) {
                                        String s[] = es_3.get(j).text().split(":");
                                        j++;
                                        if (es_3.get(j).text().equals("")){

                                            continue;
                                        }


                                        XjxxModels xjxxModels = new XjxxModels();
                                        xjxxModels.setAtrr(s[0]);

//                                        j++;
                                        xjxxModels.setValue(es_3.get(j).text());
                                        lists.add(xjxxModels);



//                                        tv_html.setText(tv_html.getText().toString() + "\n" + es_3.get(j).text());
                                    }
                                }
                                setListView();
                            } else {
                                Toast.makeText(XjxxActivity.this, getString(R.string.getYzmFail), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }
                    break;
                case urlUtil.DATA_FAIL:
                    progressDialog.dismiss();

                    Snackbar.make(rv_xjxx, getString(R.string.getDataTimeOut), Snackbar.LENGTH_SHORT).show();
                    finish();
                    break;
            }

        }
    };

    private void setListView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        XjxxAdaper xjxxAdaper = new XjxxAdaper(this, lists);
        rv_xjxx.setLayoutManager(mLayoutManager);
        rv_xjxx.setAdapter(xjxxAdaper);
        rv_xjxx.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_xjxx);
        sp = new Sp(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rv_xjxx = (RecyclerView) findViewById(R.id.rv_xjxx);

        getInfo();
        progressDialog = new ProgressDialog(XjxxActivity.this);
        progressDialog.setMessage(getString(R.string.getData));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void getInfo() {
        cookie = sp.getCookie();
        new Thread() {
            public void run() {
                netUtil.getPostData(urlUtil.URL + urlUtil.URL_XJXX, cookie, XjxxActivity.this);

            }
        }.start();
    }


    @Override
    public void getDataSuccess(String Data) {
        tv = Data;
        handler.sendEmptyMessage(urlUtil.DATA_SUCCESS);
    }

    @Override
    public void getDataFail() {
        handler.sendEmptyMessage(urlUtil.DATA_FAIL);

    }

    @Override
    public void getDataSession() {
        handler.sendEmptyMessage(urlUtil.SESSION);
    }

    @Override
    public void getCookie(String data) {

    }

}
