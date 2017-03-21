package com.yangdu.offlinegooglemapdemo.network;

import java.io.File;

/**
 * @package com.yangdu.offlinegooglemapdemo.network
 * @description 下载进程进度回调接口
 * @author yangdu
 * @date 13/03/2017
 * @time 3:55 PM
 * @version V1.0
 **/
public interface DownloadProgressListener {
    void showDownloadStarted();
    void showDownloadHandling(int size,int totalSize);
    void showDownloadError(String url,String error);
    void showDownloadFinished(File downloadFile);
}
