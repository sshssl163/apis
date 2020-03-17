package com.xb.interactiveadapplication.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.xb.interactiveadapplication.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class LocationUtils {



    /**
     * 定位：权限判断
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Location getLocation(Context context) {
        //检查定位权限
        ArrayList<String> permissions = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        //判断
        if (permissions.size() == 0) {//有权限，直接获取定位
           return getLastKnownLocation(context);
        } else {//没有权限，获取定位权限
//            requestPermissions(permissions.toArray(new String[permissions.size()]), 2);
//
            Log.d(MainActivity.TAG, "没有定位权限");
        }
        return  null;
    }

    /**
     * 定位：得到位置对象
     * @return
     */
    private static Location getLastKnownLocation(Context context) {
        //获取地理位置管理器
        LocationManager mLocationManager = (LocationManager) context.getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
