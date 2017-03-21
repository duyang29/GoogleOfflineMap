package com.yangdu.offlinegooglemapdemo.utils;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;

import java.io.File;

/**
 * @author yangdu
 * @version V1.0
 * @package com.yangdu.offlinegooglemapdemo.utils
 * @description zip解压工具类
 * @date 16/03/2017
 * @time 4:18 PM
 **/
public class ZipUtil {

    private static final String TAG = "ZipUtil";

    public final static int START = 10000;
    public final static int HANDLING = 10001;
    public final static int COMPLETED = 10002;
    public final static int ERROR = 10003;

    public static void unZipFileWithProgress(final File zipFile, final String filePath, final ZipStatusCallback zipStatusCallback, String password,
                                             final boolean isDeleteZip) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (zipStatusCallback == null) {
                    return;
                }
                switch (msg.what) {
                    case START:
                        zipStatusCallback.showZipStatusStarted();
                        Log.i(TAG, "handleMessage: started unzip");
                        break;
                    case HANDLING:
                        int percent = msg.arg1;
                        zipStatusCallback.showZipStatusHandling(percent);
                        Log.i(TAG, "handleMessage: unziped...." + percent + "%");
                        break;
                    case COMPLETED:
                        zipStatusCallback.showZipStatusCompleted(msg.obj!=null?msg.obj.toString():"");/// TODO: 16/03/2017
                        Log.i(TAG, "handleMessage: unzip completed");
                        break;
                    case ERROR:
                        zipStatusCallback.showZipStatusError(msg.obj!=null?msg.obj.toString():"");
                        Log.i(TAG, "handleMessage: error" + msg.obj.toString());
                        break;
                }
            }

        };
        try {
            zipStatusCallback.showZipStatusStarted();
            ZipFile zFile = new ZipFile(zipFile);
            zFile.setFileNameCharset("GBK");

            if (!zFile.isValidZipFile()) {
                throw new ZipException("invalid zip file format");
            }
            final File destDir = new File(filePath); // 解压目录
            if (destDir.isDirectory() && !destDir.exists()) {
                destDir.mkdir();
            }
            if (zFile.isEncrypted()) {
                password = "d2x1z0s6";
                zFile.setPassword(password); // 解压密码
            }

            final ProgressMonitor progressMonitor = zFile.getProgressMonitor();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = null;
                    try {
                        int precentDone = 0;
                        while (true) {
                            Thread.sleep(100);
                            precentDone = progressMonitor.getPercentDone();
                            sendMessage(handler, msg, HANDLING, precentDone, null);//正在解压
                            if (precentDone >= 100) {
                                break;
                            }
                        }
                        sendMessage(handler, msg, COMPLETED, 100, destDir);//解压完成
                    } catch (InterruptedException e) {
                        sendMessage(handler, msg, ERROR, 0, e.getMessage());////解压错误
                    } finally {
                        if (isDeleteZip) {
                            zipFile.delete();//删除原始zip文件
                        }
                    }
                }
            });
            thread.start();
            zFile.setRunInThread(true);//在子线程中执行操作
            zFile.extractAll(filePath); //将文件抽取到解压目录，解压缩
        } catch (Exception e) {
            sendMessage(handler, null, ERROR, 0, e.getMessage());////解压错误
        }
    }

    private static void sendMessage(Handler handler, Message msg, int what, int arg1, @Nullable Object obj) {
        if (handler != null) {
            if (msg==null) {
                msg = new Message();
            }
            msg.what = what;
            msg.obj = obj;
            msg.arg1 = arg1;
            handler.sendMessage(msg);
        }
    }
}
