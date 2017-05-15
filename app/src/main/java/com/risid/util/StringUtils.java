package com.risid.util;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/2/9.
 */

public class StringUtils {
    public static String [] returnImageUrlsFromHtml(String htmlCode) {
        List<String> imageSrcList = new ArrayList<String>();
        Pattern p = Pattern.compile("<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic)\\b)[^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlCode);
        String quote = null;
        String src = null;
        while (m.find()) {
            quote = m.group(1);
            src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("//s+")[0] : m.group(2);
            imageSrcList.add(src);
        }
//        Log.d("img_url", imageSrcList.toString());
        return imageSrcList.toArray(new String[imageSrcList.size()]);
    }
    public static String returnTitle(String html){
        Document document = Jsoup.parse(html);
        Element table = document.getElementsByTag("table").get(0);

        return table.getElementsByTag("td").get(1).text();
    }

    public static String returnDetailFromHtml(String html){
        Document document = Jsoup.parse(html);



        Element drlrimess = document.getElementsByClass("drlrimess").get(0);
        Elements a = drlrimess.getElementsByTag("a");
        for (Element anA : a) {
            String url = anA.attr("href");
            if (url.contains("upload") && !url.contains("jwc")) {
                url = "http://jwc.tyut.edu.cn/" + url;
                anA.attr("href", url);
            }
        }

        Elements img = drlrimess.getElementsByTag("img");
        for (Element anA : img) {
            String url = anA.attr("src");
            if (url.contains("upload") && !url.contains("jwc")) {
                url = "http://jwc.tyut.edu.cn/" + url;
                anA.attr("src", url);
                anA.attr("width", "100%");
            }
        }
        return drlrimess.html();

    }


}
