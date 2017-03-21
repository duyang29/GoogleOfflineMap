package com.yangdu.offlinegooglemapdemo.settings.mapdownload;

import com.yangdu.offlinegooglemapdemo.settings.mapdownload.data.MapDownloadEntity;

/**
 * @package com.yangdu.offlinegooglemapdemo.settings.mapdownload
 * @description
 * @author yangdu
 * @date 16/03/2017
 * @time 3:55 PM
 * @version V1.0
 **/
public interface MapDownloadContract {

    interface View{
        MapDownloadEntity getMapDownloadParams();
    }

    interface Presenter{
        void startDownload();
    }
}
