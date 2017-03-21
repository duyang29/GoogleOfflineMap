package com.yangdu.offlinegooglemapdemo.utils;

/**
 * Created by yangdu on 16/03/2017.
 */

public interface ZipStatusCallback {

    void showZipStatusStarted();
    void showZipStatusHandling(int percent);
    void showZipStatusError(String error);
    void showZipStatusCompleted(String unzipPath);

}
