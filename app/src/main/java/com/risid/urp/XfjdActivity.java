package com.risid.urp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.risid.Interface.GetNetData;
import com.risid.adapter.XfjdAdapter;
import com.risid.adapter.XjxxAdaper;
import com.risid.util.Sp;
import com.risid.util.netUtil;
import com.risid.util.urlUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.baidu.mobstat.StatService;
//import org.jsoup.select.Elements;

public class XfjdActivity extends SwipeBackAppActivity implements GetNetData {
    private String cookie;
    private String tv;
    private ProgressDialog progressDialog;
    private RecyclerView rv_xfjd;

    private Toolbar toolbar;
    private String zjh;
    private String pwd;
    private Sp sp;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case urlUtil.DATA_FAIL:
                    progressDialog.dismiss();

                    Toast.makeText(XfjdActivity.this, getString(R.string.getDataTimeOut), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case urlUtil.DATA_SUCCESS:
                    if (tv == null) {
                        System.out.println("空");
                    } else {
                        progressDialog.dismiss();
                        Document doc = Jsoup.parse(tv);


                        if (!tv.equals("")){
                            try {
                                JSONArray jsonArray=new JSONArray(tv);

                                JSONObject xsxx=jsonArray.getJSONObject(0);

                                List<String> stringList = new ArrayList<>();
                                stringList.add(xsxx.get("xh").toString());
                                stringList.add(xsxx.get("xm").toString());
                                stringList.add(xsxx.get("bjh").toString());
                                stringList.add(xsxx.get("yqzxf").toString());
                                stringList.add(xsxx.get("yxzzsjxf").toString());
                                stringList.add(xsxx.get("yxzxf").toString());
                                stringList.add(xsxx.get("cbjgxf").toString());
                                stringList.add(xsxx.get("sbjgxf").toString());
                                stringList.add(xsxx.get("pjxfjd").toString());
                                stringList.add(xsxx.get("gpabjpm").toString());
                                stringList.add(xsxx.get("gpazypm").toString());
                                stringList.add(xsxx.get("pjcj").toString());
                                stringList.add(xsxx.get("pjcjbjpm").toString());
                                stringList.add(xsxx.get("pjcjzypm").toString());
                                stringList.add(xsxx.get("jqxfcj").toString());
                                stringList.add(xsxx.get("jqbjpm").toString());
                                stringList.add(xsxx.get("jqzypm").toString());
                                stringList.add(xsxx.get("tjsj").toString());
                                setRecyclerView(stringList);

                            } catch (JSONException ex) {
                                // 异常处理代码
                                Toast.makeText(XfjdActivity.this, "排名的教务处可能不行了", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Toast.makeText(XfjdActivity.this, "登录超时，请退出后登陆", Toast.LENGTH_SHORT).show();
                            finish();

                        }

                    }
                    break;
            }
        }
    };

    private void setRecyclerView(List<String> lists) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        XfjdAdapter xjxxAdaper = new XfjdAdapter(this, lists);
        rv_xfjd.setLayoutManager(mLayoutManager);
        rv_xfjd.setAdapter(xjxxAdaper);
        rv_xfjd.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xfjd);
        rv_xfjd = (RecyclerView) findViewById(R.id.rv_xfjd);

        sp = new Sp(XfjdActivity.this);
        // cookie = intent.getStringExtra("cookie");
//        cookie = sp.getCookieJD();
        zjh = sp.getZjh();
        pwd = sp.getMm();
        initView();
        getInfo();
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

        progressDialog = new ProgressDialog(XfjdActivity.this);
        progressDialog.setMessage(getString(R.string.getData));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void getInfo() {
        new Thread() {
            public void run() {
                String url = urlUtil.URL_XFDJ;
                Map<String, String> loginMap = new HashMap<>();
                loginMap.put("u", zjh);
                loginMap.put("p", pwd);
                loginMap.put("r", "on");
                netUtil.doPost(url + urlUtil.URL_XFJD_LOGIN, null, loginMap, XfjdActivity.this);

                cookie = sp.getCookieJD();
                Map<String, String> xfjdMap = new HashMap<>();
                xfjdMap.put("xh", zjh);
                xfjdMap.put("sort", "jqzypm,xh");
                xfjdMap.put("do", "xsgrcj");
                netUtil.doPost(url+urlUtil.URL_XFDJ_QUERY, cookie, xfjdMap, XfjdActivity.this);
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
        Sp sp = new Sp(this);
        sp.setCookieJD(data);
    }

}
