package com.risid.Interface;

/**
 * Created by risid on 2015/11/18.
 */
public interface GetNetData {
    //成功获取到数据
    void getDataSuccess(String Data);
    //获取数据失败
    void getDataFail();
    //Session到期
    void getDataSession();

    void getCookie(String data);
}
