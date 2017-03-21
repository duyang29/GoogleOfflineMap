package com.yangdu.offlinegooglemapdemo.settings;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yangdu.offlinegooglemapdemo.BaseActivity;
import com.yangdu.offlinegooglemapdemo.R;
import com.yangdu.offlinegooglemapdemo.network.DownloadProgressListener;
import com.yangdu.offlinegooglemapdemo.network.FileDownloader;
import com.yangdu.offlinegooglemapdemo.utils.ZipStatusCallback;
import com.yangdu.offlinegooglemapdemo.utils.ZipUtil;
import com.yunwei.library.utils.IToastUtil;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yangdu.offlinegooglemapdemo.R.string.error;

/**
 * @author yangdu
 * @version V1.0
 * @package com.yangdu.offlinegooglemapdemo.settings
 * @description 下载界面
 * @date 13/03/2017
 * @time 4:11 PM
 **/
public class DownloadActivity extends BaseActivity implements ZipStatusCallback{

    private static final String TAG = "DownloadActivity";

    @BindView(R.id.path)
    EditText downloadpathText;
    @BindView(R.id.resultView)
    TextView resultView;
    @BindView(R.id.downloadbar)
    ProgressBar progressBar;
    @BindView(R.id.button)
    Button btnDownload;
    @BindView(R.id.btn_unzip)
    Button btnUnzip;
    @BindView(R.id.progressbar_unzip)
    ProgressBar progressbarUnzip;
    @BindView(R.id.tv_unzip_result)
    TextView tvUnzipResult;

    /**
     * 当Handler被创建会关联到创建它的当前线程的消息队列，该类用于往消息队列发送消息
     * 消息队列中的消息由当前线程内部进行处理
     */
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    progressBar.setProgress(msg.getData().getInt("size"));
                    float num = (float) progressBar.getProgress() / (float) progressBar.getMax();
                    int result = (int) (num * 100);
                    resultView.setText(result + "%");

                    if (progressBar.getProgress() == progressBar.getMax()) {
                        IToastUtil.showToast(DownloadActivity.this, R.string.success);
                    }
                    break;
                case -1:
                    IToastUtil.showToast(DownloadActivity.this, error);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button, R.id.btn_unzip})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                String path = downloadpathText.getText().toString();
                Log.i(TAG, Environment.getExternalStorageState() + "------" + Environment.MEDIA_MOUNTED);

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    download(path, Environment.getExternalStorageDirectory());//开启下载任务
                } else {
                    IToastUtil.showToast(DownloadActivity.this, R.string.sdcarderror);
                    int minutes = Calendar.getInstance().get(Calendar.MINUTE);
                    Log.i(TAG, " " + getResources().getQuantityString(R.plurals.minutes, minutes));
                }
                break;
            case R.id.btn_unzip:
                unzipFile();
                break;
        }
    }

    /**
     * 主线程(UI线程)
     * 对于显示控件的界面更新只是由UI线程负责，如果是在非UI线程更新控件的属性值，更新后的显示界面不会反映到屏幕上
     *
     * @param path
     * @param savedir
     */
    private void download(final String path, final File savedir) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileDownloader loader = new FileDownloader(DownloadActivity.this, path, savedir, 3);//这里默认开启3个子线程
                progressBar.setMax(loader.getFileSize());//设置进度条的最大刻度为文件的长度

                try {
                    loader.download(new DownloadProgressListener() {

                        @Override
                        public void showDownloadStarted() {

                        }

                        @Override
                        public void showDownloadHandling(int size,int totalSize) {//实时获知文件已经下载的数据长度
                            Message msg = new Message();
                            msg.what = 1;
                            msg.getData().putInt("size", size);
                            handler.sendMessage(msg);//发送消息
                        }

                        @Override
                        public void showDownloadError(String url, String error) {

                        }

                        @Override
                        public void showDownloadFinished(File downloadFile) {
                            //开始解压文件
                        }
                    });
                } catch (Exception e) {
                    handler.obtainMessage(-1).sendToTarget();
                }
            }
        }).start();
    }

    private void unzipFile() {
        progressbarUnzip.setMax(100);
        File unzipFile = new File(Environment.getExternalStorageDirectory() + File.separator + "Original.zip");
        String unzipPath = Environment.getExternalStorageDirectory()+File.separator+"Unzip";
        ZipUtil.unZipFileWithProgress(unzipFile, unzipPath, this, "", false);
    }

    @Override
    public void showZipStatusStarted() {
        tvUnzipResult.setText("正在解析压缩包...");
    }

    @Override
    public void showZipStatusHandling(int percent) {
        progressbarUnzip.setProgress(percent);
        float num = (float) progressbarUnzip.getProgress() / (float) progressbarUnzip.getMax();
        int result = (int) (num * 100);
        tvUnzipResult.setText(result + "%");

        if (progressBar.getProgress() == progressBar.getMax()) {
            IToastUtil.showToast(DownloadActivity.this, R.string.success);
        }
    }

    @Override
    public void showZipStatusError(String error) {
        tvUnzipResult.setText("解压失败"+error);
    }

    @Override
    public void showZipStatusCompleted(String unzipPath) {
        tvUnzipResult.setText("解压完成" + unzipPath);
    }
}
