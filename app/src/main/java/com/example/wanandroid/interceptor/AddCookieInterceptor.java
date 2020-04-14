package com.example.wanandroid.interceptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 将Cookie添加到请求头
 */
public class AddCookieInterceptor implements Interceptor {
    private static final String COOKIE_PREF="cookie_pref";
    private Context mContext;

    public AddCookieInterceptor(Context context){
        mContext=context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request=chain.request();
        Request.Builder builder=request.newBuilder();
        Log.d("TAG","url:"+request.url().toString());
        Log.d("TAG","host:"+request.url().host());
        String cookie=getCookie(request.url().toString(),request.url().host());
        Log.d("TAG","Cookie:"+cookie);
        //将cookie添加到请求的header中
        if (!TextUtils.isEmpty(cookie)){
            builder.addHeader("Cookie",cookie);
        }

        return chain.proceed(builder.build());
    }

    private String getCookie(String url, String host) {
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(COOKIE_PREF,Context.MODE_PRIVATE);
        //返回url或者host对应的cookie缓存
        if (!TextUtils.isEmpty(url)&&sharedPreferences.contains(url)&&
                !TextUtils.isEmpty(sharedPreferences.getString(url,""))
        ){
            Log.d("TAG","url cookie:"+sharedPreferences.getString(url,""));
            return sharedPreferences.getString(url,"");
        }else if (!TextUtils.isEmpty(host)&&sharedPreferences.contains(host)&&
                !TextUtils.isEmpty(sharedPreferences.getString(host,""))){
            Log.d("TAG","host cookie:"+sharedPreferences.getString(host,""));
            return sharedPreferences.getString(host,"");
        }
        return null;
    }
}
