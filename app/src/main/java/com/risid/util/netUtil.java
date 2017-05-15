package com.risid.util;

import com.risid.Interface.GetNetData;
import com.risid.Interface.GetZpInterface;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class netUtil {
    public static void doPost(String url, String cookie, Map<String, String> postMap, final GetNetData getNetData) {

        Set<Map.Entry<String, String>> entries = postMap.entrySet();

        FormBody.Builder formBodyBuilder = new FormBody.Builder();

        for(Map.Entry<String, String> entry : entries){
            formBodyBuilder.add(entry.getKey(), entry.getValue());
        }

        OkHttpClient client = new OkHttpClient();
        OkHttpClient client1 = client.newBuilder().readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS).build();
        Request.Builder requestBuilder= new Request.Builder().url(url).post(formBodyBuilder.build());
        if (cookie != null){
            requestBuilder.addHeader("Cookie", cookie);
        }
        Request request = requestBuilder.build();
        Response response;


        try {
            response = client1.newCall(request).execute();
            if (response.code() == 200 || response.code() == 500 ) {
                getNetData.getDataSuccess( response.body().string());
            }else {
                getNetData.getDataFail();
            }
        } catch (IOException e) {
            getNetData.getDataFail();
            e.printStackTrace();
        }
    }
    public static void getPostData(String url, String cookie, final GetNetData getData) {


        OkHttpClient client = new OkHttpClient();
        OkHttpClient client1 = client.newBuilder().readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS).build();
        Request.Builder requestBuilder= new Request.Builder().url(url);
        if (cookie != null){
            requestBuilder.addHeader("Cookie", cookie);
        }
        Request request = requestBuilder.build();
        Response response;


        try {
            response = client1.newCall(request).execute();
            if (response.code() == 200 || response.code() == 500) {
                getData.getDataSuccess(new String(response.body().bytes(), Charset.forName("gb2312")));
            }else {
                getData.getDataFail();
            }
        } catch (IOException e) {
            getData.getDataFail();
            e.printStackTrace();
        }
    }


    public static void getZp(String url, String cookie, final GetZpInterface getZpInterface) {
        OkHttpClient client = new OkHttpClient();
        OkHttpClient client1 = client.newBuilder().readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS).build();
        Request.Builder requestBuilder= new Request.Builder().url(url);
        if (cookie != null){
            requestBuilder.addHeader("Cookie", cookie);
        }
        Request request = requestBuilder.build();
        Response response;


        try {
            response = client1.newCall(request).execute();
            if (response.code() == 200 || response.code() == 500) {
                getZpInterface.getZpSuccess(response.body().bytes());
            }else {
                getZpInterface.getZpFail();
            }
        } catch (IOException e) {
            getZpInterface.getZpFail();
            e.printStackTrace();
        }
    }
}

