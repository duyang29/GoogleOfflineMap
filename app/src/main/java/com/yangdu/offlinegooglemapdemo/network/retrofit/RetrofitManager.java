package com.yangdu.offlinegooglemapdemo.network.retrofit;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yangdu on 15/03/2017.
 */
public class RetrofitManager {
    private static RetrofitManager INSTANCE ;
    private static Retrofit mRetrofit;

    public static RetrofitManager getInstance(){
        if (INSTANCE==null) {
            synchronized (RetrofitManager.class){
                if (INSTANCE==null) {
                    INSTANCE = new RetrofitManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 返回Service
     *
     * @return
     */
    public APIService getService() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .client(getClient())
                    .baseUrl("domain")/// TODO: 15/03/2017
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        APIService service = mRetrofit.create(APIService.class);
        return service;
    }

    /**
     * 设置Client请求头
     *
     * @return
     */
    private static OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("Authorization", "bearer " + "token")/// TODO: 15/03/2017
                        .build();
                return chain.proceed(request);
            }
        }).build();
        return client;
    }


}
