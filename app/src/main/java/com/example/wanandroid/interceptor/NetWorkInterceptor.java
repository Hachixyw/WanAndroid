package com.example.wanandroid.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetWorkInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request=chain.request();
        Response response=chain.proceed(request);
        int maxAge=60;
        return response.newBuilder()
                .removeHeader("Prama")
                .removeHeader("Cache-Control")
                .addHeader("Cache-Control","public,max-age="+maxAge)
                .build();
    }
}
