package com.risid.urp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.risid.Interface.GetNetData;
import com.risid.util.MapUtil;
import com.risid.util.Sp;
import com.risid.util.netUtil;
import com.risid.util.urlUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import com.umeng.update.UmengUpdateAgent;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GetNetData {
    private EditText et_user, et_pwd;
    //    private ImageView img_yzm;
//    private ProgressBar pgb_yzm;
    private Button btn_login;
    private String cookie;
    private String zjh;
    private String mm;
    private String tv;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private Sp sp;
    private CheckBox chb_mm;
//    private CheckBox chk_auto;
    private Toolbar toolbar;
    private LinearLayout lin_main;
    private String yzm;
    private Thread imgGetThread;
    private Thread loginThread;
    private final static String URP = "urp";
//    private final static String TESSBASE_PATH = "/storage/emulated/0/Download/";

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case urlUtil.DATA_FAIL:
                    progressDialog.dismiss();
                    // shapeLoadingDialog.dismiss();
                    Snackbar.make(lin_main, getText(R.string.getDataTimeOut), Snackbar.LENGTH_SHORT)
                            .show();
                    break;
                case urlUtil.PIC_SUCCESS:

//                    progressBar.setVisibility(View.INVISIBLE);

                    TessBaseAPI baseApi = new TessBaseAPI();

                    //初始化OCR的训练数据路径与语言
                    baseApi.init("/data/data/" + getPackageName() + "/" , URP);
                    baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
                    Pix pix = ReadFile.readBitmap((Bitmap) msg.obj);
                    pix = Binarize.sauvolaBinarizeTiled(pix);

                    Bitmap after = WriteFile.writeBitmap(pix);

                    baseApi.setImage(after);
//                    img_yzm.setImageBitmap(bitmap);
                    yzm = baseApi.getUTF8Text().replace(" ", "");
                    login();

                    break;
                case urlUtil.DATA_SUCCESS:
                    if (tv == null) {

                        return;
                    } else {
                        Log.d("login",tv);
                        Document doc = Jsoup.parse(tv);
                        String title = doc.title();

                        //shapeLoadingDialog.dismiss();
                        if (getString(R.string.title_success).equals(title)) {
                            progressDialog.dismiss();
                            if (chb_mm.isChecked()) {
                                sp.setZjh(zjh);
                                sp.setMm(mm);
                                sp.setRememberMm(true);
                            } else {
                                sp.setZjh(null);
                                sp.setMm(null);
                            }
//                            if (chk_auto.isChecked()) {
//                                sp.setAuto(true);
//                            }
                            sp.setCookie(cookie);
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();

                        }else if (getText(R.string.webTitle).equals(title)) {
                            String error = doc.select("[class=errorTop]").text();
                            Log.e("error", error);
                            if (error.contains("验证码错误")){

                                getCodePic();
                                return;
                            }
                            progressDialog.dismiss();
                            Snackbar.make(lin_main, error, Snackbar.LENGTH_SHORT)
                                    .show();

                        }
                    }
                    break;
                case urlUtil.PIC_FAIL:
                    Snackbar.make(lin_main, getText(R.string.getYzmFail), Snackbar.LENGTH_SHORT).show();

//                    progressBar.setVisibility(View.INVISIBLE);
                    break;
                case urlUtil.SESSION:
                    Toast.makeText(LoginActivity.this, getString(R.string.loginFail), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        setTitle(getString(R.string.login));
        sp = new Sp(LoginActivity.this);
        if (sp.getAuto()) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            initView();

        }
    }


    private void initView() {
        chb_mm = (CheckBox) findViewById(R.id.chk_mm);
//        chk_auto = (CheckBox) findViewById(R.id.chk_auto);
        et_user = (EditText) findViewById(R.id.et_user);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
//        et_yzm = (EditText) findViewById(R.id.et_yzm);
//        pgb_yzm = (ProgressBar) findViewById(R.id.pgb_yzm);
        btn_login = (Button) findViewById(R.id.btn_login);
//        img_yzm = (ImageView) findViewById(R.id.img_yzm);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        lin_main = (LinearLayout) findViewById(R.id.lin_main);
        //--滑动视图开始
        // ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.list);
        //  scrollView.setScrollViewCallbacks(this);
        //---滑动视图结束
        setSupportActionBar(toolbar);
//        pgb_yzm.setVisibility(View.GONE);
//        progressBar = pgb_yzm;
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getText(R.string.logining));
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (loginThread != null && loginThread.isAlive()){
                    try {
                        Thread.sleep(1000);
                        loginThread.interrupt();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
        //shapeLoadingDialog =new ShapeLoadingDialog(this);
        //shapeLoadingDialog.setLoadingText("加载中...");
        btn_login.setOnClickListener(this);
//        img_yzm.setOnClickListener(this);
//        chk_auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    chb_mm.setChecked(true);
//                }
//            }
//        });
        //记住密码是否开启
        if (sp.getRememberMM()) {
            et_user.setText(sp.getZjh());
            et_pwd.setText(sp.getMm());
            chb_mm.setChecked(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.img_yzm:
//                getCode();
//                break;
            case R.id.btn_login:
//                progressDialog.set(View.VISIBLE);
                getCodePic();
                progressDialog.show();
//                login();
                break;
        }
    }



    private void login() {
        if (loginThread != null && loginThread.isAlive()){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loginThread.interrupt();
        }
        loginThread = new Thread() {
            public void run() {
                zjh = et_user.getText().toString();
                mm = et_pwd.getText().toString();

                Map<String, String> loginMap = MapUtil.LoginMap(zjh, mm, yzm);
                netUtil.doPost(
                        urlUtil.URL + urlUtil.URL_LOGIN, cookie, loginMap, LoginActivity.this);

            }
        };
        loginThread.start();
    }


    //获取验证码
    private void getCodePic(){
        if (imgGetThread != null && imgGetThread.isAlive()){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            imgGetThread.interrupt();
        }
        imgGetThread = new Thread(){
            @Override
            public void run() {
                super.run();
                OkHttpClient mOkHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(urlUtil.URL + urlUtil.URL_YZM).build();
                mOkHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        handler.sendEmptyMessage(urlUtil.PIC_FAIL);
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        InputStream inputStream = response.body().byteStream();
                        if (inputStream == null){
                            handler.sendEmptyMessage(urlUtil.PIC_FAIL);
                        }

                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        if (bitmap == null || bitmap.getHeight() == 0){
                            handler.sendEmptyMessage(urlUtil.PIC_FAIL);
                            return;
                        }
                        Message message = new Message();

                        message.what = urlUtil.PIC_SUCCESS;
                        message.obj = bitmap;
                        cookie = response.header("set-cookie");
                        try {
                            if (inputStream != null){
                                inputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        response.close();

                        handler.sendMessage(message);
                    }
                });
            }
        };
        imgGetThread.start();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent1 = new Intent();
                intent1.setClass(LoginActivity.this, SettingsActivity.class);
                startActivity(intent1);
                break;
            case R.id.action_about:
                Intent intent2 = new Intent();
                intent2.setClass(LoginActivity.this, AboutActivity.class);
                startActivity(intent2);
                break;
        }

        return true;
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
