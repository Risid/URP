package com.risid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.risid.Interface.GetNetData;
import com.risid.urp.R;
import com.risid.util.Sp;
import com.risid.util.netUtil;
import com.risid.util.urlUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Risid on 2017/3/23.
 */

public class LoginThread extends Thread implements GetNetData{
    private Context context;
    private Handler handler;
    private Sp sp;
    private String cookie;
    private Map<String, String> loginMap;
    private final static String URP = "urp";
    private String yzm;
    public LoginThread(Context context, Map<String, String> loginMap, Handler handler){
        this.loginMap = loginMap;
        this.context = context;
        this.handler = handler;
        sp = new Sp(context);
    }

    @Override
    public void run() {
        super.run();
        if (sp.getZjh() == null){
            return;
        }
        getYZM();



    }



    private void getYZM(){
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
                    return;
                }

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                if (bitmap == null || bitmap.getHeight() == 0){

                    return;
                }
                Message message = new Message();

                message.what = urlUtil.PIC_SUCCESS;
                message.obj = bitmap;
                cookie = response.header("set-cookie");
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response.close();

                TessBaseAPI baseApi = new TessBaseAPI();

                //初始化OCR的训练数据路径与语言
                baseApi.init("/data/data/" + context.getPackageName() + "/" , URP);
                baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
                Pix pix = ReadFile.readBitmap(bitmap);
                pix = Binarize.sauvolaBinarizeTiled(pix);

                Bitmap after = WriteFile.writeBitmap(pix);

                baseApi.setImage(after);

                yzm = baseApi.getUTF8Text().replace(" ", "");
                loginMap.put("v_yzm", yzm);
                login();

            }
        });
    }

    private void login() {
        netUtil.doPost(
                urlUtil.URL + urlUtil.URL_LOGIN, cookie, loginMap, this);
    }

    @Override
    public void getDataSuccess(String Data) {

            Document doc = Jsoup.parse(Data);
            String title = doc.title();

            if (context.getString(R.string.title_success).equals(title)) {
                sp.setCookie(cookie);
                handler.sendEmptyMessage(urlUtil.LOGIN_SUCCESS);

            }else if (context.getText(R.string.webTitle).equals(title)) {
                String error = doc.select("[class=errorTop]").text();
                if (error.contains("验证码错误")){
                    getYZM();

                }

            }


    }

    @Override
    public void getDataFail() {
        handler.sendEmptyMessage(urlUtil.DATA_FAIL);

    }

    @Override
    public void getDataSession() {

    }

    @Override
    public void getCookie(String data) {

    }
}
