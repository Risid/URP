package com.risid.urp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.risid.Interface.GetNetData;
import com.risid.LoginThread;
import com.risid.adapter.JxpgAdapter;
import com.risid.models.JxpgModels;
import com.risid.models.PgInfoModels;
import com.risid.util.MapUtil;
import com.risid.util.Sp;
import com.risid.util.netUtil;
import com.risid.util.urlUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.baidu.mobstat.StatService;


public class JxpgListActivity extends SwipeBackAppActivity implements GetNetData {
    private String cookie;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private ListView lv_jxpg;
    private String tv;
    private List<JxpgModels> lists;
    private JxpgAdapter adapter;
    private List<PgInfoModels> list_pg;
    private Sp sp;
    private AlertDialog.Builder dialog;
    private int i = 0;
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
                case urlUtil.DATA_FAIL:
                    progressDialog.dismiss();
                    Toast.makeText(JxpgListActivity.this, getString(R.string.getDataTimeOut), Toast.LENGTH_SHORT).show();
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
                            //教学评估列表
                            Elements es = doc.select("[class=odd]");
                            if (es.size() != 0) {
                                for (int i = 0; i < es.size(); i++) {
                                    Elements es_2 = es.get(i).select("td");
                                    String wjmc = es_2.get(0).text();
                                    String pgnr = es_2.get(2).text();
                                    String bpr = es_2.get(1).text();
                                    String sfpg = es_2.get(3).text();
                                    if ("否".equals(sfpg)) {
                                        String pginfo = es_2.get(4).select("img").attr("name");
                                        String[] strs = pginfo.split("#@");
                                        String info_wjbm = strs[0];
                                        String info_bpr = strs[1];
                                        String info_kcm = strs[4];
                                        String info_pgnr = strs[5];
                                        System.out.println(info_bpr);
                                        PgInfoModels pgInfoModels = new PgInfoModels(info_wjbm, info_bpr, info_kcm, info_pgnr);
                                        list_pg.add(pgInfoModels);
                                        Log.d("评估", info_kcm);
                                    }

                                    JxpgModels jxpgModels = new JxpgModels(wjmc, pgnr, bpr, sfpg);
                                    lists.add(jxpgModels);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                        }
                    }
                    break;
                case urlUtil.PG_SUCCESS:
                    Document doc = Jsoup.parse(tv);
                    progressDialog.setMessage(doc.text());
                    i++;
                    startPg(i);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jxpg_list);
        sp = new Sp(JxpgListActivity.this);

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
        progressDialog = new ProgressDialog(JxpgListActivity.this);
        lv_jxpg = (ListView) findViewById(R.id.lv_jxpg_list);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_car);
        //  fab.attachToListView(lv_jxpg);
        dialog = new AlertDialog.Builder(JxpgListActivity.this);
        dialog.setTitle("一键评估");
        dialog.setMessage("是否进行一键评估,可能会耗时几分钟，请耐心等待");
        progressDialog.setMessage(getString(R.string.getData));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();
        lists = new ArrayList<>();
        list_pg = new ArrayList<>();
        adapter = new JxpgAdapter(JxpgListActivity.this, lists);
        lv_jxpg.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list_pg.size() == 0) {
                    dialog.setTitle("一键评估");
                    dialog.setMessage("当前没有需要评估的科目");
                    dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.create();
                    dialog.show();
                } else {
                    dialog.setNegativeButton("开始评估", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            progressDialog.setTitle("正在评估");
                            progressDialog.show();
                            startPg(i);
                        }
                    });
                    dialog.create();
                    dialog.show();
                }

            }
        });
    }

    public void getInfo() {
        cookie = sp.getCookie();
        new Thread() {
            public void run() {
                netUtil.getPostData(urlUtil.URL + urlUtil.URL_JXPG_LIST, cookie, JxpgListActivity.this);
            }
        }.start();
    }


    private void startPg(final int position) {
        final int size = list_pg.size();
        if (size == 0) {
            progressDialog.dismiss();
            dialog.setMessage("所有科目都评估完成!");
            dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    i = 0;
                }
            });
            dialog.setCancelable(true);
            dialog.show();
            return;
        }
        if (position == size) {
            lists.clear();
            list_pg.clear();
            adapter.notifyDataSetChanged();
            getInfo();
            progressDialog.dismiss();
            dialog.setMessage("所有科目都评估完成!");
            dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    i = 0;
                }
            });
            dialog.setCancelable(true);
            dialog.show();
        } else {
            progressDialog.setMessage("正在评估" + i + "/" + size + "\n" + list_pg.get(position).getKcm());
            new Thread() {
                public void run() {
                    //模拟点击评估
                    Map<String, String> clickMap = new HashMap<String, String>();
                    clickMap.put("wjbm", list_pg.get(position).getWjbm());
                    clickMap.put("bpr", list_pg.get(position).getBpr());
                    clickMap.put("pgnr", list_pg.get(position).getPgnr());
                    clickMap.put("oper", "wjShow");


                    loginpg(clickMap);
                    //评估核心代码
                    Map<String, String> pjMap = new HashMap<String, String>();
                    clickMap.put("wjbm", list_pg.get(position).getWjbm());
                    clickMap.put("bpr", list_pg.get(position).getBpr());
                    clickMap.put("pgnr", list_pg.get(position).getPgnr());
                    clickMap.put("0000000136","25_0.95" );
                    clickMap.put("0000000137","25_0.95" );
                    clickMap.put("0000000138","30_0.95" );
                    clickMap.put("0000000139","20_0.95" );


                    clickMap.put("zgpj", getString(R.string.pgnr));
                    login(pjMap);
                }
            }.start();
        }
    }





    public void loginpg(Map<String, String> map) {
        netUtil.doPost(
                urlUtil.URL + urlUtil.URL_PG, cookie, map, new GetNetData() {
                    @Override
                    public void getDataSuccess(String Data) {

                    }

                    @Override
                    public void getDataFail() {

                    }

                    @Override
                    public void getDataSession() {

                    }
                });
    }

    public void login(Map<String, String> map) {
        netUtil.doPost(
                urlUtil.URL + urlUtil.URL_JXPG, cookie, map, new GetNetData() {
                    @Override
                    public void getDataSuccess(String Data) {
                        tv = Data;
                        handler.sendEmptyMessage(urlUtil.PG_SUCCESS);
                    }

                    @Override
                    public void getDataFail() {
                        handler.sendEmptyMessage(urlUtil.DATA_FAIL);
                    }

                    @Override
                    public void getDataSession() {
                        handler.sendEmptyMessage(urlUtil.SESSION);
                    }
                });

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


}
