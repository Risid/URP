package com.risid.util;

/**
 * Created by risid on 2015/9/14.
 */
public class urlUtil {
    public static String JWCURL = "http://219.226.101.61/jiawu3.html";
    //主url

    public static String URL = "http://202.207.247.49/";
//    public static String URL2 = "http://202.207.247.44:8064/";
//    public static String URL3 = "http://202.207.247.44:8065/";
//    public static String URL4 = "http://202.207.247.44:8069/";
    //login
    public final static String URL_LOGIN = "loginAction.do";
    //学籍信息
    public final static String URL_XJXX = "xjInfoAction.do?oper=xjxx";
    //学分绩点
//    public final static String URL_XFDJ = "http://202.207.247.42/";
    public final static String URL_XFDJ = "https://stu.tyut.risid.com/";

    public final static String URL_XFDJ_QUERY = "Hander/Cj/CjAjax.ashx";

    //实践成绩
    public final static String URL_ZJSJ = "xszzsjcjbAction.do?oper=viewByStudent";
    ////本学期课表
    public final static String URL_KB = "xkAction.do?actionType=6";

    //验证码
    public final static String URL_YZM = "validateCodeAction.do";
    //照片
    public final static String URL_ZP = "xjInfoAction.do?oper=img";
    //方案成绩
    public final static String URL_FA = "gradeLnAllAction.do?type=ln&oper=fainfo&fajhh=1734";
    //全部及格成绩
    public final static String URL_QB = "gradeLnAllAction.do?type=ln&oper=qbinfo";
    //不及格成绩
    public final static String URL_BJG = "gradeLnAllAction.do?type=ln&oper=bjg";
    //通知公告
    public final static String URL_TZGG = "tzgg.htm";
    //jwc网站
    public final static String URL_JWC = "http://jwc.tyut.edu.cn/";
    //评教列表
    public final static String URL_JXPG_LIST = "jxpgXsAction.do?oper=listWj&pageSize=300";
    //具体评估
    public final static String URL_JXPG = "jxpgXsAction.do?oper=wjpg";
    //评估页面
    public final static String URL_PG = "jxpgXsAction.do";
    //返回数据错误
    public final static String NET_FAIL = "FAIL";
    //session失效
    public final static String SESSION_FAIL = "SESSION_FAIL";

    public final static String URL_XFJD_LOGIN = "Hander/LoginAjax.ashx";

    //handler   返回数据成功
    public final static int DATA_SUCCESS = 0x01;
    //handler 返回数据失败
    public final static int DATA_FAIL = 0x02;
    //handler 返回验证码失败
    public final static int PIC_FAIL = 0x03;
    //handler session 失效
    public final static int SESSION = 0x04;
    //handler 验证码成功
    public final static int PIC_SUCCESS = 0x05;
    //评估成功
    public final static int PG_SUCCESS = 0x06;

    public final static int LOGIN_SUCCESS = 0x07;

    public final static int LOGIN_FAIL = 0x08;
}
