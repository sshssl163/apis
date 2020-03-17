package com.xb.interactiveadapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.xb.interactiveadapplication.net.bean.ClientInfo;
import com.xb.interactiveadapplication.net.bean.ResponseBean;
import com.xb.interactiveadapplication.net.core.BaseRequest;
import com.xb.interactiveadapplication.net.core.GsonResultParse;
import com.xb.interactiveadapplication.net.core.RequestCallback;
import com.xb.interactiveadapplication.net.core.UrlBuilder;
import com.xb.interactiveadapplication.utils.AdTrackersManager;
import com.xb.interactiveadapplication.utils.AdvertisingIdClient;
import com.xb.interactiveadapplication.utils.Config;
import com.xb.interactiveadapplication.utils.GzipUtil;
import com.xb.interactiveadapplication.utils.LocationUtils;
import com.xb.interactiveadapplication.utils.NetWorkUtils;
import com.xb.interactiveadapplication.utils.Sha1;

import java.security.DigestException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import static com.xb.interactiveadapplication.WebActivity.LOAD_URL;

public class MainActivity extends Activity {
    public static final String TAG = "InteractiveAd_Test";
    private Button requestadBtn;
    private SimpleDraweeView img;
    private TextView tv;
    String adId;
    String ci;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestadBtn = findViewById(R.id.requestad_btn);
        img = findViewById(R.id.sdv_b_pic);
        tv = findViewById(R.id.tv);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)//GPS定位
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    adId = AdvertisingIdClient.getGoogleAdId(getApplicationContext());
                    Log.d(TAG, "adid:  " + adId);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            requestadBtn.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "get ad id exception");
                    e.printStackTrace();
                }
            }
        });

        requestadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setText("request ad ...");
                fetchAd();
            }
        });
    }


    /**
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void fetchAd() {
        if (ci == null){
            ci = creatCi();
        }
        String nonce = getRandomString(6);
        long timestamp = System.currentTimeMillis();
        UrlBuilder urlBuilder = new UrlBuilder(Config.REQUEST_URL);
        urlBuilder.addParams("appKey", "demo");
        urlBuilder.addParams("soltId", 1);
        urlBuilder.addParams("ci", ci);
        urlBuilder.addParams("timestamp", timestamp);
        urlBuilder.addParams("nonce", nonce);
        urlBuilder.addParams("deviceId",adId);

        urlBuilder.addParams("signature",createSignature(nonce,timestamp));
        BaseRequest.get(urlBuilder.getUrl(), urlBuilder.getParams(),
                new GsonResultParse<>(Object.class), new RequestCallback<Object>() {
            @Override
            public void onSuccessed(Object responseBean) {

            }

            @Override
            public void onSuccessed(String response) {
                Log.d(TAG,"request successed"+response);
                if (!TextUtils.isEmpty(response)){
                    tv.setText(response);
                    Gson gson = new Gson();
                    ResponseBean bean = gson.fromJson(response, ResponseBean.class);
                    showImg(bean);
                } else {
                    tv.setText("respoine data exception");
                    Log.d(TAG,"respoine data exception");
                }

            }

                    @Override
            public void onFailed(int status, String msg) {
                        tv.setText("respoine data exception");
            }

            @Override
            public void onError() {
                tv.setText("request error");
                Log.d(TAG,"request error");
            }
        });
    }

    private void  showImg(final ResponseBean bean){
        img.setVisibility(View.VISIBLE);
        Uri uri = (bean.getImageUrl() != null) ? Uri.parse(bean.getImageUrl()) : null;
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
//                .setResizeOptions((width > 0 && height > 0) ? new ResizeOptions(width, height) : null)
                .build();
        img.setController(Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .setImageRequest(imageRequest)
                .setOldController(img.getController())
                .setControllerListener(new ControllerListener<ImageInfo>() {
                    @Override
                    public void onSubmit(String id, Object callerContext) {
                    }

                    @Override
                    public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
                        AdTrackersManager.getInstance().reportTrackers(new String[]{bean.getReportExposeUrl()});
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AdTrackersManager.getInstance().reportTrackers(new String[]{bean.getReportClickUrl()});
                                Intent in = new Intent(MainActivity.this,WebActivity.class);
                                in.putExtra(LOAD_URL,bean.getLandingUrl());
                                startActivity(in);
                            }
                        });

                    }

                    @Override
                    public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                        Log.d(TAG,"img onIntermediateImageSet");
                    }

                    @Override
                    public void onIntermediateImageFailed(String id, Throwable throwable) {
                        Log.d(TAG,"img onIntermediateImageFailed");
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        Log.d(TAG,"img onFailure");
                    }

                    @Override
                    public void onRelease(String id) {
                        Log.d(TAG,"图片onRelease");
                    }
                })
                .build());
    }

    private String getRandomString(int length){
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(Config.ROM.charAt(number));
        }
        return sb.toString();
    }
    private String createSignature(String nonce,long timestamp){
        Map<String ,Object> m =new HashMap<>();
        m.put("appKey","demo");
        m.put("appSecret",Config.APPSECRET);
        m.put("ci",ci);
        m.put("deviceId", adId);
        m.put("nonce",nonce);
        m.put("soltId","1");
        m.put("timestamp",timestamp+"");
        String sha1="";
        try {
            sha1 = Sha1.SHA1(m);
        } catch (DigestException e) {
            Log.d(TAG,"Generate signature file exception");
            e.printStackTrace();
        }
        return sha1;
    }
    private String creatCi(){
        final ClientInfo cc = new ClientInfo();
        cc.setAdvertiserId(adId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Location ll = LocationUtils.getLocation(this);
            if (ll != null) {
                cc.setLatitude(ll.getLatitude() + "");
                cc.setLongitude(ll.getLongitude() + "");
            }

        } else {
            Log.d(TAG, "Insufficient mobile version to get positioning");
        }

        cc.setOs("android_" + android.os.Build.VERSION.RELEASE);

        switch(NetWorkUtils.getAPNType(this)){
            case 0 :
                cc.setNetwork(null);
                break;
            case 1 :
                cc.setNetwork("wifi");
                break;
            case 2 :
                cc.setNetwork("3G");
                break;
            case 3 :
                cc.setNetwork("4G");
                break;
            default : //可选
                //语句
        }
        cc.setApps(getAppList());
        Gson gson = new Gson();
        String jsonBDID = gson.toJson(cc);
        return GzipUtil.compress(jsonBDID, "UTF-8");
    }
    private String getAppList() {
        StringBuilder sb =new StringBuilder("");
        PackageManager pm = getPackageManager();
        // Return a List of all packages that are installed on the device.
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
            {
                sb.append(packageInfo.packageName+",");
            } else {
                // 系统应用
            }
        }
        return sb.toString();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //这里实现用户操作，或同意或拒绝的逻辑
        /*grantResults会传进android.content.pm.PackageManager.PERMISSION_GRANTED 或 android.content.pm.PackageManager.PERMISSION_DENIED两个常，前者代表用户同意程序获取系统权限，后者代表用户拒绝程序获取系统权限*/
        if (grantResults.length > 0 && (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED)){
            ci = creatCi();
        }
    }
}
