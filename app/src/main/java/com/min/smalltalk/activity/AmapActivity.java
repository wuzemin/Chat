package com.min.smalltalk.activity;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.min.mylibrary.util.T;
import com.min.smalltalk.R;

public class AmapActivity extends AppCompatActivity implements LocationSource, AMapLocationListener {

    private TextView tv_normal, tv_satellite, tv_night, tv_navi;
    private EditText et_search;


    private MapView mapView;
    private AMap aMap;
    //声明mLocationOption对象
    LocationSource.OnLocationChangedListener mListener;
    AMapLocationClient mLocationClient;
    AMapLocationClientOption mLocationOption;

    private int map_type;
    private float zoom = 18f;

    private LatLonPoint start_latPoint;

    private LatLonPoint end_latPoint;

    private Marker mEndMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap);
        initView();
        // setToolBar(toolbar,"");
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initData();
        initClick();
    }

    private void initView() {
        //  toolbar= (Toolbar) findViewById(R.id.toolbar);
        /*collapsingToolbar= (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBar= (AppBarLayout) findViewById(R.id.app_bar);*/

        mapView = (MapView) findViewById(R.id.map);
        tv_normal = (TextView) findViewById(R.id.tv_normal);
        tv_satellite = (TextView) findViewById(R.id.tv_satellite);
        tv_night = (TextView) findViewById(R.id.tv_night);
        tv_navi = (TextView) findViewById(R.id.tv_vani);
        et_search= (EditText) findViewById(R.id.et_search);
    }

    private void initData() {

        aMap = mapView.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeWidth(1);
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        aMap.setTrafficEnabled(true);// 显示实时交通状况
        //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 卫星地图模式
        mEndMarker = aMap.addMarker(new MarkerOptions().icon
                (BitmapDescriptorFactory.fromBitmap
                        (BitmapFactory.decodeResource(getResources(), R.mipmap.location))));

    }

    private void initClick() {
        tv_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map_type == 0)
                    return;
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 卫星地图模式
                map_type = 0;
            }
        });
        tv_satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map_type == 1)
                    return;
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
                map_type = 1;
            }
        });
        tv_night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map_type == 2)
                    return;
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);// 卫星地图模式
                map_type = 2;
            }
        });
        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == start_latPoint) {
                    return;
                }
                /*Intent intent = new Intent(AmapActivity.this, EndActivity.class);
                intent.putExtra("latPoint", start_latPoint);
                startActivity(intent);*/

            }
        });
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                end_latPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
                mEndMarker.setPosition(latLng);
                tv_navi.setVisibility(View.VISIBLE);
            }
        });
        tv_navi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null==end_latPoint){
                    T.showShort(AmapActivity.this,"请选择终点后，在出发...");
                    return;
                }
                /*Intent intent=new Intent(AmapActivity.this, MainActivity.class);
                intent.putExtra("start_latPoint",start_latPoint);
                intent.putExtra("end_latPoint",end_latPoint);
                startActivity(intent);*/
            }
        });
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setOnceLocationLatest(true);
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            //设置定位间隔时间
            mLocationOption.setInterval(5000);
            mLocationClient.startLocation();//启动定位
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                start_latPoint = new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude());
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
