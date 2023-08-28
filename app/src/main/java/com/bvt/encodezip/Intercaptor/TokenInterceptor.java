package com.bvt.encodezip.Intercaptor;


import android.util.Log;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class TokenInterceptor implements Interceptor {

    private static final String TAG = "TokenInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        Log.d(TAG, "response.code=" + response.code());

        //判断token过期
        if (isTokenExpired(response)) {
            //同步请求方获取最新的Token
            String newToken = getNewToken();
            //使用新的Token，创建新的请求
            Request newRequest = chain.request()
                    .newBuilder()
                    .header("Authorization", "Url.BEARER" +" " + newToken)
                    .build();
            //重新请求
            return chain.proceed(newRequest);
        }
        return response;
    }

    /**
     * 根据Response，判断Token是否失效
     */
    private boolean isTokenExpired(Response response) {
        if (response.code() == 401) {
            return true;
        }
        return false;
    }

    /**
     * 同步请求方式，获取最新的Token
     * 此处需考虑并发问题，多请求时可能同时去刷新token导致刚获取的token马上失效
     */
    static private  synchronized  String getNewToken() throws IOException {
        // 通过获取token的接口，同步请求接口
        String newToken = "";
        //....
        return newToken;
    }


}
