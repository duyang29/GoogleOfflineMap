package com.yangdu.offlinegooglemapdemo;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.esri.android.map.event.OnZoomListener;
import com.esri.core.geometry.Point;
import com.yangdu.offlinegooglemapdemo.settings.DownloadActivity;
import com.yunwei.library.utils.IActivitySkipUtils;
import com.yunwei.map.MapView;
import com.yunwei.map.entity.MPointEntity;
import com.yunwei.map.utils.ILngLatMercator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnZoomListener {

    private static final String TAG = "MainActivity";
    @BindView(R.id.mapview)
    MapView mMapView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private Point pt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //Toolbar
        initToolbar();

        //114.06,22.57
        MPointEntity centerPt = ILngLatMercator.lonLat2WebMercator(114.06, 22.57);
        pt = new Point(centerPt.getX(), centerPt.getY());
        mMapView.updateCurrentLocation(pt);
        mMapView.setExtent(pt);
        mMapView.setOnZoomListener(this);

    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getResources().getString(R.string.action_map_manage));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setLogo(getResources().getDrawable(R.mipmap.ic_launcher, getTheme()));
        }else{
            mToolbar.setLogo(getResources().getDrawable(R.mipmap.ic_launcher));
        }
//        mToolbar.inflateMenu(R.menu.menu_main);//该方法不能显示menu
        mToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }


    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_map_settings:
                    IActivitySkipUtils.startActivity(MainActivity.this, DownloadActivity.class, null);
                    break;
                case R.id.action_search:
                    break;
            }
            return true;
        }
    };

    @Override
    public void preAction(float v, float v1, double v2) {

    }

    @Override
    public void postAction(float v, float v1, double v2) {
        if (mMapView.getScale()<=8000) {
            mMapView.setLocRangeLayerVisibility(true);
            mMapView.openLocRangeLayer(pt);
        }else{
            mMapView.setLocRangeLayerVisibility(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MENU:
//                IToastUtil.showToast(MainActivity.this, "menu");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
