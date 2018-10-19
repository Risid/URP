package com.risid.urp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.baidu.mobstat.StatService;
import com.risid.Interface.GetNetData;
import com.risid.LoginThread;
import com.risid.adapter.MianAdapter;
import com.risid.models.MainModels;
import com.risid.util.MapUtil;
import com.risid.util.Sp;
import com.risid.util.netUtil;
import com.risid.util.urlUtil;
// import com.umeng.update.UmengUpdateAgent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GetNetData {
    private String cookie;
    private Toolbar toolbar;
    private List<MainModels> lists;
    private ListView listView;
    private MianAdapter adapter;
    private MainModels[] mainModele;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private TextView tv_head;
    private String tv;
//    private ProgressDialog progressDialog;
    private SwipeRefreshLayout refreshLayout;
    private Sp sp;


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
                        Document doc = Jsoup.parse(tv);
                        if (getString(R.string.webTitleError).equals(doc.title())) {

                            reLogin();

                        } else {
                            Elements es = doc.select("[width=275]");
                            if (es.size() > 0) {
                                String text = "姓名：" + es.get(1).text() + " 学号：" + es.get(0).text();
                                tv_head.setText(text);

                                refreshLayout.setRefreshing(false);
                            }else {
                                reLogin();
                            }
                        }
                    }
                    break;
                case urlUtil.DATA_FAIL:
                    refreshLayout.setRefreshing(false);
                    Snackbar.make(listView, getString(R.string.getDataTimeOut), Snackbar.LENGTH_SHORT).show();

                    break;

            }

        }
    };
    private void copyFileOrDir(String path) {
        AssetManager assetManager = this.getAssets();
        String assets[] = null;
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(path);
            } else {
                String fullPath = "/data/data/" + this.getPackageName() + "/" + path;
                File dir = new File(fullPath);
                if (!dir.exists())
                    if (!dir.mkdir()){
                        return;
                    }
                for (String asset : assets) {
                    copyFileOrDir(path + "/" + asset);
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }

    private void copyFile(String filename) {
        AssetManager assetManager = this.getAssets();

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            String newFileName = "/data/data/" + this.getPackageName() + "/" + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        copyFileOrDir("tessdata");
        sp = new Sp(MainActivity.this);
        if (sp.getZjh() == null){
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


        initView();
        getData();
        getInfo();
    }


    void initView() {


        /**
         * 初始化各种控件
         * */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        drawerToggle.syncState();
        listView = (ListView) findViewById(R.id.lv_main);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);

        tv_head = (TextView) headerView.findViewById(R.id.tv_head_text);

        lists = new ArrayList<>();
        adapter = new MianAdapter(lists, MainActivity.this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //学籍信息
                        start(XjxxActivity.class);
                        break;
                    case 1:
                        //成绩查询
                        start(CjActivity.class);
                        break;
                    case 2:
                        //自主实践
                        start(ZjsjActivity.class);
                        break;
                    case 3:
                        //学分绩点
                        start(XfjdActivity.class);
                        break;
                    case 4:
                        //本学期课表
//                        Snackbar.make(listView, "开发中1111", Snackbar.LENGTH_SHORT).show();
//                         start(KbActivity.class);
                        Intent intent = new Intent();
                        intent.putExtra("URL", urlUtil.URL+urlUtil.URL_KB);
                        intent.setClass(MainActivity.this, WebActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        //一键评教
                        // Snackbar.make(listView, "开发中", Snackbar.LENGTH_SHORT).show();
                        start(JxpgListActivity.class);
                        break;

                    case 6:
                        start(TzggActivity.class);
                        break;
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navItem1:
                       /* String str = urlUtil.URL;
                        Snackbar.make(listView, str, Snackbar.LENGTH_SHORT)
                                .show();*/
//
                        start(SettingsActivity.class);
                        break;

                    case R.id.navItem3:
                        start(AboutActivity.class);
                        break;
                    case R.id.navItem4:
                        //退出
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("").setMessage("退出登录").setNegativeButton(getString(R.string.back), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Sp sp = new Sp(MainActivity.this);
                                sp.setCookie(null);
                                sp.setAuto(false);
                                finish();
                                start(LoginActivity.class);
                            }
                        }).create().show();
                        break;
//                    case R.id.item_update:
//                        UmengUpdateAgent.forceUpdate(MainActivity.this);
//                        break;
                }
                return false;
            }
        });
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_main);

        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInfo();
            }
        });
    }

    private void getData() {
        mainModele = new MainModels[7];
        mainModele[0] = new MainModels(String.valueOf(R.mipmap.xjxx_2), getString(R.string.title_activity_xjxx));
        mainModele[1] = new MainModels(String.valueOf(R.mipmap.cj_2), getString(R.string.cj));
        mainModele[2] = new MainModels(String.valueOf(R.mipmap.zzsj_2), getString(R.string.title_activity_zjsj));
        mainModele[3] = new MainModels(String.valueOf(R.mipmap.xfjd_2), getString(R.string.title_activity_xfjd));
        mainModele[4] = new MainModels(String.valueOf(R.mipmap.kb_2), getString(R.string.bxqkb));
        mainModele[5] = new MainModels(String.valueOf(R.mipmap.pj_2), getString(R.string.yjpj));
        mainModele[6] = new MainModels(String.valueOf(R.mipmap.tzgg_2), getString(R.string.tzgg));
        for (int i = 0; i < mainModele.length; i++) {
            lists.add(mainModele[i]);
        }
        adapter.notifyDataSetChanged();
    }


    void start(Class cls) {
        Intent intent = new Intent();
        // intent.putExtra("cookie", cookie);
        intent.setClass(getApplicationContext(), cls);
        startActivity(intent);
    }

    private long exitTime = 0;


    public boolean onKeyDown(int keyCode, KeyEvent event) {



        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {


            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Snackbar.make(listView, R.string.doubleBack, Snackbar.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           /* case R.id.action_settings:
                String str = urlUtil.URL;
                Snackbar.make(listView, str, Snackbar.LENGTH_SHORT)
                        .show();
                break;
            case R.id.action_about:
                start(AboutActivity.class);
                break;*/
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)
                        ) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                break;
        }

        return true;
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void getInfo() {
        refreshLayout.setRefreshing(true);
        cookie = sp.getCookie();
        new Thread() {
            public void run() {
                netUtil.getPostData(urlUtil.URL + urlUtil.URL_XJXX, cookie, MainActivity.this);
            }

        }.start();
    }

    public void reLogin() {
//        sp.setAuto(false);
//        start(LoginActivity.class);
//        finish();

        new LoginThread(this, MapUtil.LoginMap(sp.getZjh(), sp.getMm()), handler).start();
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
