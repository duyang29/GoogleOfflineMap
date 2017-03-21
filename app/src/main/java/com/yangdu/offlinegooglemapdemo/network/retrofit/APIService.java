package com.yangdu.offlinegooglemapdemo.network.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * @package com.yangdu.offlinegooglemapdemo.network.retrofit
 * @description API接口定义
 * @author yangdu
 * @date 15/03/2017
 * @time 3:56 PM
 * @version V1.0
 **/
public interface APIService {

    /**
     * 下载文件
     * @param url
     * @return
     */
    @GET
    Call<ResponseBody> downloadFile(@Url String url);
}
