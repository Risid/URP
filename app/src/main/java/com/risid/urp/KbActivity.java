package com.risid.urp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//import com.baidu.mobstat.StatService;
import com.risid.Interface.GetNetData;
import com.risid.util.Sp;
import com.risid.util.netUtil;
import com.risid.util.urlUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class KbActivity extends AppCompatActivity implements GetNetData{
    private String cookie;
    private String tv;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private TextView[] textViews;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case urlUtil.DATA_FAIL:
                    progressDialog.dismiss();

                    Toast.makeText(KbActivity.this, getString(R.string.getDataTimeOut), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case urlUtil.DATA_SUCCESS:
                    if (tv == null) {
                        System.out.println("空");
                    } else {
                        progressDialog.dismiss();
                        Document doc = Jsoup.parse(tv);
                        if (getString(R.string.webTitle).equals(doc.title())) {
                            Toast.makeText(KbActivity.this, getString(R.string.loginFail),
                                    Toast.LENGTH_SHORT).show();
                            MainActivity lg =new MainActivity();
                            lg.reLogin();

                            finish();

                        } else {
                            Elements es = doc.select("[class=odd]");
                            for (int i = 0; i < 5; i++) {
                                Elements es2 = es.get(i).select("td");
                                textViews[i].setText(es2.text());
                            }

                        }
                    }
                    break;
                case urlUtil.SESSION:
                    Toast.makeText(KbActivity.this, getString(R.string.loginFail), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kb);
        Sp sp = new Sp(KbActivity.this);
        cookie = sp.getCookie();
        getInfo();
        initView();
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
        progressDialog = new ProgressDialog(KbActivity.this);
        progressDialog.setMessage(getString(R.string.getData));
        progressDialog.show();
        textViews = new TextView[5];
        textViews[0] = (TextView) findViewById(R.id.tv_html1);
        textViews[1] = (TextView) findViewById(R.id.tv_html2);
        textViews[2] = (TextView) findViewById(R.id.tv_html3);
        textViews[3] = (TextView) findViewById(R.id.tv_html4);
        textViews[4] = (TextView) findViewById(R.id.tv_html5);
    }

    public void getInfo() {
        new Thread() {
            public void run() {
                netUtil.getPostData(urlUtil.URL + urlUtil.URL_KB, cookie, KbActivity.this);
            }
        }.start();
    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        StatService.onResume(KbActivity.this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        StatService.onPause(KbActivity.this);
//    }

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
}
