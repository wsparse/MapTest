package com.example.administrator.maptest;

import android.content.Context;
import android.location.LocationListener;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;


/**
 * Created by Administrator on 2016/11/25 0025.
 */

public class LocationUtil {

    LocationClient mLocationClient;
    LocationClientOption mLocationClientOption;
    MyLocationListener myLocationListener;
    Context context;

    public LocationUtil(Context context){
        this.context = context;
        mLocationClient = new LocationClient(context);
        initOption();
        mLocationClient.setLocOption(mLocationClientOption);
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        //startLoction();
    }

    public LocationClient getmLocationClient(){
        return  mLocationClient;
    }
    private void initOption() {
        mLocationClientOption = new LocationClientOption();
        mLocationClientOption.setIsNeedAddress(true);
        mLocationClientOption.setOpenGps(true);
        mLocationClientOption.setAddrType("all");
        mLocationClientOption.setScanSpan(1000); //扫描间隔时间
        mLocationClientOption.setCoorType("bd09ll");
    }

    public interface LoactionDataListener{
            void onResult(BDLocation bdlocation);
            void onMyLocationDataResult(MyLocationData myLocationData);
    }

    public LoactionDataListener loactionDataListener;

    public LoactionDataListener getLoactionDataListener() {
        return loactionDataListener;
    }

    public void setLocationDataListener(LoactionDataListener loactionDataListener){
        this.loactionDataListener = loactionDataListener;
        Log.e("ws","locationDataListener赋值成功！");
    }

    public void startLoction(){
        mLocationClient.start();
    }


    public void stopLocation(){
        mLocationClient.stop();
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            int type = bdLocation.getLocType();
           // Log.e("ws",".........MylocationListener.....type=............"+type);
            if(type == BDLocation.TypeNetWorkLocation){
                if ( loactionDataListener!= null){
                    //Log.e("ws",".........MylocationListener.....type=............"+type);

                    Log.e("ws",bdLocation.getAddrStr()+"定位地址。。。。。。。。。。。。");
                    Toast.makeText(context,"第一次定位回调成功！",Toast.LENGTH_LONG).show();
                    loactionDataListener.onResult(bdLocation);
                    MyLocationData myLocationData  = new MyLocationData.Builder().accuracy(bdLocation.getRadius()).direction(100)
                            .latitude(bdLocation.getLatitude()).longitude(bdLocation.getLongitude()).build();
                    loactionDataListener.onMyLocationDataResult(myLocationData);
                }
            }
            stopLocation();
        }
    }
}
