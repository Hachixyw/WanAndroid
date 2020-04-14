package com.example.wanandroid.interceptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 缓存登录信息
 */
public class SaveCookieInterceptor implements Interceptor {
    private static final String COOKIE_PREF="cookie_pref";
    private Context mContext;

    public SaveCookieInterceptor(Context context){
        mContext=context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request=chain.request();
        Response response=chain.proceed(request);
        //可能有多个set-cookie
        if (!response.headers("set-cookie").isEmpty()){
            List<String> cookies=response.headers("set-cookie");
            String cookie=encodeCookie(cookies);
            saveCookie(request.url().toString(),request.url().host(),cookie);
        }
        return response;
    }

    private void saveCookie(String url, String host, String cookie) {
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(COOKIE_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        //url为空抛出异常，不为空就将url的cookie保存
        if (TextUtils.isEmpty(url)){
            throw new NullPointerException("url is null");
        }else {
            editor.putString(url,cookie);
        }
        //如果domain不为空则将cookie缓存给它
        if (!TextUtils.isEmpty(host)){
            editor.putString(host,cookie);
        }

        editor.apply();
    }


    /**
     * 对Cookie进行格式处理
     * @param
     * @return
     */
    private String encodeCookie(List<String> cookies) {
        StringBuilder stringBuilder=new StringBuilder();
        Set<String> set=new HashSet<>();
        //遍历cookies,根据分号进行分割，如果有相同的就跳过，否则存储
        for (String cookie:cookies){
            String[] arr=cookie.split(";");
            for (String s:arr){
                if (!set.contains(s)){
                    set.add(s);
                }
            }
        }
        //遍历set，重新将分号拼接进去
        for (String cookie:set){
            stringBuilder.append(cookie).append(";");
        }
        //最后一个分号的位置
        int last=stringBuilder.lastIndexOf(";");
        //如果最后一个为分号，就将其删除
        if (stringBuilder.length()-1==last){
            stringBuilder.deleteCharAt(last);
        }

        return stringBuilder.toString();
    }

    /**
     * 清除Cookie的持久化
     * @param context
     */
    public static void clearCookies(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(COOKIE_PREF,Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}
