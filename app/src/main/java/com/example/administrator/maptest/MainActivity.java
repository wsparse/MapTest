package com.example.administrator.maptest;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements BDLocationListener {
    MapView mapView; // 测试分支
    int a =10;
    BaiduMap baiduMap;  //百度地图
    LocationClient mLocationClient; //定位客户端
    LocationClientOption mLocationClientOption; //定位客户端参数

    MyOrientationSensorListener myOrientationSensorListener;  //自定义方向传感器监听器。

    LatLng latLag;//初次定义的经纬度

    boolean isFirstLocation = true;  //是否初次定位

    LocationUtil util;  //定位工具类
    BitmapDescriptor mIconDescriptor; //定位方向图标。
    BitmapDescriptor mMarkerIcon;  //标记图标


    private float mCurrentX; //当前X轴方向的度数。

    MyLocationConfiguration.LocationMode mapType; //百度地图启动模式

    List<OverlayOptions>  markerList = new ArrayList<>(); //标记覆盖物图层MarkerOptions集合对象

    List<LatLng> latLngList = new ArrayList<>();//存储在地图上点击过的经纬度点的集合。

    Utils utils = new Utils();  //关于对地图调用的系列方法的工具类对象。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActionBar actionBar = getActionBar();  //隐藏标题栏。
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_main);
        initView();
        intData();
        setListener();
    }

    private void setListener() {
        utils.setMapListener(baiduMap,markerList,mMarkerIcon,latLngList);

        utils.setMarkerListener(baiduMap,getApplicationContext());
    }


    private void intData() {
        mLocationClient = new LocationClient(this);
        mLocationClientOption = new LocationClientOption();
        mLocationClientOption.setCoorType("bd09ll");
        mLocationClientOption.setIsNeedAddress(true);
        mLocationClientOption.setScanSpan(1000);
        mLocationClientOption.setOpenGps(true);
        mLocationClient.setLocOption(mLocationClientOption);
        mLocationClient.registerLocationListener(this);
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.mapview);
       /* mapView.showScaleControl(false);//设置是否显fdsdfs示比例尺5555
        mapView.showZoomControls(false);//设置是否显示缩放比例
        mapView.removeViewAt(1); //移除百度地图图标
*/
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true); //设置地图可以定位
        MapStatusUpdate statusUpadate = MapStatusUpdateFactory.zoomTo(15f);
        baiduMap.setMapStatus(statusUpadate);
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);


        mIconDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_category_item_arrow_down);
        mMarkerIcon = BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka);

        myOrientationSensorListener = new MyOrientationSensorListener(this);
        myOrientationSensorListener.setOnOrientationListener(new MyOrientationSensorListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
        Log.e("ws", "设置了地图的初始状态");


    }

    @Override
    protected void onStart() {
        super.onStart();
        baiduMap.setMyLocationEnabled(true);
        mLocationClient.start();  //开启定位
        myOrientationSensorListener.start(); //开启方向传感器

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationClient.isStarted()) {
            mLocationClient.stop();//停止定位
            myOrientationSensorListener.stop();//停止方向传感器
            baiduMap.setMyLocationEnabled(false);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        Log.e("ws", "mapView执行了onResum方法！");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        Log.e("ws", "mapView执行了onPause方法！");
    }

    @Override
    protected void onDestroy() {  //与Activity生命周期保持一致。
        super.onDestroy();
        mapView.onDestroy();

        mLocationClient.unRegisterLocationListener(this);
        mLocationClient.stop(); //停止定位
        Log.e("ws", "mapView执行了onDestory方法！");
    }

    Marker mMarker;

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {

        //注意 这个 LatLng，它用与设置 标记的位置和 Map的中心点的位置 。
        //final LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());

        //Log.e("ws", "定位type=" + bdLocation.getLocType());

       // baiduMap.clear();

        if (isFirstLocation) {
            util = new LocationUtil(MainActivity.this);
            util.startLoction();
            util.setLocationDataListener(new LocationUtil.LoactionDataListener() {
                @Override
                public void onResult(BDLocation bdlocation) {
                    latLag = new LatLng(bdlocation.getLatitude(), bdlocation.getLongitude());
                    MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLag);
                    baiduMap.setMapStatus(msu);
                    Log.e("ws", "定位到当前的位置成功..............onResult");
                    isFirstLocation = false;
                }

                @Override
                public void onMyLocationDataResult(MyLocationData myLocationData) {

                }
            });
        }
        latLag = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        MyLocationData data = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                .direction(mCurrentX)
                .latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude())
                .build();
        baiduMap.setMyLocationData(data);

        /*MarkerOptions markerOptions = new MarkerOptions();
        BitmapDescriptor bdes = BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka);
        markerOptions.position(latLag).draggable(true).icon(bdes);
        baiduMap.addOverlay(markerOptions);
*/
        MyLocationConfiguration configuration = new MyLocationConfiguration(mapType, true, mIconDescriptor);
        baiduMap.setMyLocationConfigeration(configuration);
        /*if (mMarker == null) {
            MarkerOptions options = new MarkerOptions();
            options.position(latLng);
            options.draggable(true);  //设置可否拖动

            //必须要设置option的icon，而且 这个 icon 必须是BitmapDescriptor的。
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.icon_marka);
            options.icon(bitmapDescriptor);

            mMarker = (Marker) baiduMap.addOverlay(options); //加载标记点
        } else {
            mMarker.setPosition(latLng);
        }

       */
    }

    private void centerToLocation() {
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLag);
        baiduMap.animateMapStatus(msu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.common: //常规地图
                baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.weixing:  //卫星地图
                baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.traffic:  //交通地图
                if (baiduMap.isTrafficEnabled()) {
                    baiduMap.setTrafficEnabled(false);
                    item.setTitle("实时交通(off)");
                } else {
                    baiduMap.setTrafficEnabled(true);
                    item.setTitle("实时交通(on)");
                }
                break;
            case R.id.location_to: //定位到当前经纬度
                centerToLocation();
                break;
            case R.id.common_mode:  //地图启动模式（常规模式）
                mapType = MyLocationConfiguration.LocationMode.NORMAL;
                break;
            case R.id.follow_mode: //跟随模式
                mapType = MyLocationConfiguration.LocationMode.FOLLOWING;
                break;
            case R.id.compass_mode: //罗盘模式
                mapType = MyLocationConfiguration.LocationMode.COMPASS;
                break;
            case R.id.add_overlay: //添加覆盖图层，并且地图跟新到以最后一个点击的点为中心处。
                addOverlays();
                //baiduMap.setMapStatus();
                break;
            case R.id.draw_line:  //绘制标记点中的经纬度点组成的集合的轨迹路线。
                utils.drawLine(baiduMap,markerList);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addOverlays() {
        baiduMap.clear(); //先清除图层上的内容。
        if(markerList.size() != 0){
            baiduMap.addOverlays(markerList);
        }else{
            Toast.makeText(getApplicationContext(),"还没有数据源",Toast.LENGTH_LONG).show();
        }
        LatLng position = ((MarkerOptions) markerList.get(markerList.size() - 1)).getPosition();
        MapStatusUpdate msu  = MapStatusUpdateFactory.newLatLng(position);
        baiduMap.animateMapStatus(msu);
    }


}
