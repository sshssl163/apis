package com.xb.interactiveadapplication.utils;

import android.util.Log;
import android.webkit.URLUtil;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 广告追踪连接管理
 */
public class AdTrackersManager {

    private static final String TAG = AdTrackersManager.class.getSimpleName();

    // 最大缓存数量
    private static final int MAX_CACHE_SIZE = 200;

    // 上报失败的追踪链接列表

    private static AdTrackersManager sInstance;

    // 已上报的追踪链接，防止重复上报
    private List<String> mReportedTrackers = new ArrayList<>();
    // 上报失败的追踪链接
    private List<String> mReportFailedTrackers = new ArrayList<>();

    // 已成功上报的曝光追踪，防止重复上报
    private Set<String> mReportedImpTrackers = new HashSet<>();


    public static synchronized AdTrackersManager getInstance() {
        if (sInstance == null) {
            sInstance = new AdTrackersManager();
        }

        return sInstance;
    }

    private AdTrackersManager() {
    }

    /**
     * 检查已上报成功的追踪链接缓存数，超过上限则移除较早的
     */
    private void freeCache() {
        long size = mReportedTrackers.size();
        if (size > MAX_CACHE_SIZE) {
            int destSize = MAX_CACHE_SIZE / 100;
            while (mReportedTrackers.size() > destSize) {
                mReportedTrackers.remove(0);
            }
            Log.d(TAG, "freeCache size from " + size + " to " + destSize);

            mReportedImpTrackers.clear();
        }
    }

    /**
     * 增加一条上报失败追踪链接
     * @param tracker
     */
    private void addReportFailedTracker(final String tracker) {
        // 在ui线程中判断mReportFailedTrackers，防止出现多线程操作问题
        // 在io线程中执行保存动作，防止出现ui线程阻塞问题
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(ObservableEmitter<String[]> emitter) throws Exception {
                if (!mReportFailedTrackers.contains(tracker)) {
                    mReportFailedTrackers.add(tracker);
                    Log.d(TAG, "addReportFailedTracker: " + tracker + ", failed count: " + mReportFailedTrackers.size());
                    String[] list = mReportFailedTrackers.toArray(new String[mReportFailedTrackers.size()]);
                    emitter.onNext(list);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String[]>() {
                    @Override
                    public void accept(String[] list) throws Exception {
//                        persisted(list);
                    }
                });
    }

    /**
     * 添加一条已上报追踪链接
     * @param tracker
     */
    private void addReportedTracker(final String tracker) {
        Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> emitter) throws Exception {
                mReportedTrackers.add(tracker);
                freeCache();
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    /**
     * 移除上报失败追踪链接
     * @param tracker
     */
    private void removeReportFailedTacker(final String tracker) {
        // 在ui线程中判断mReportFailedTrackers，防止出现多线程操作问题
        // 在io线程中执行保存动作，防止出现ui线程阻塞问题
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(ObservableEmitter<String[]> emitter) throws Exception {
                if (mReportFailedTrackers.contains(tracker)) {
                    mReportFailedTrackers.remove(tracker);
                    Log.d(TAG, "removeReportFailedTacker: " + tracker + ", failed count: " + mReportFailedTrackers.size());
                    String[] list = mReportFailedTrackers.toArray(new String[mReportFailedTrackers.size()]);
                    emitter.onNext(list);
                }
                emitter.onComplete();
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<String[]>() {
                    @Override
                    public void accept(String[] strings) throws Exception {
                        Log.d(TAG, "removeReportFailedTacker, persisted: " + Thread.currentThread().getName());
//                        persisted(strings);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String[]>() {
                    @Override
                    public void accept(String[] list) throws Exception {
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        if (mReportFailedTrackers.size() > 0) {
                            Log.d(TAG, "removeReportFailedTacker, reportFailedTrackers");
                            reportFailedTrackers();
                        }
                    }
                });
    }

    /**
     * 清除上报失败的追踪链接
     */
    private void clearReportFailedTrackers() {
        // 在ui线程中判断mReportFailedTrackers，防止出现多线程操作问题
        // 在io线程中执行保存动作，防止出现ui线程阻塞问题
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(ObservableEmitter<String[]> emitter) throws Exception {
                mReportFailedTrackers.clear();
                emitter.onNext(new String[0]);
                emitter.onComplete();
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String[]>() {
                    @Override
                    public void accept(String[] list) throws Exception {
//                        persisted(list);
                    }
                });
    }


    /**
     * 上报追踪链接
     * @param trackers
     */
    public void reportTrackers(String[] trackers) {
        for (final String tracker : trackers) {
            if (mReportedTrackers.contains(tracker) && !mReportFailedTrackers.contains(tracker)) {
                Log.d(TAG, "already report: " +tracker);
                continue;
            }
            if (!URLUtil.isNetworkUrl(tracker)) {
                Log.e(TAG, "invalid tracker: " + tracker);
                continue;
            }

            Log.d(TAG, "report: " + tracker);
            addReportedTracker(tracker);

            GetBuilder builder = OkHttpUtils.get()
                    .url(tracker)
                    .id((int) System.currentTimeMillis());

            RequestCall requestCall = builder.build();
            requestCall.execute(new Callback() {
                @Override
                public Object parseNetworkResponse(Response response, int id) throws Exception {
                    return null;
                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.d(TAG, "report: " + tracker + ", error: " + e.getMessage());
                    addReportFailedTracker(tracker);
//                    broadcastTrackerReportFailed(tracker);
                }

                @Override
                public void onResponse(Object response, int id) {
                    Log.d(TAG, "report: " + tracker + ", success");
                    removeReportFailedTacker(tracker);
//                    broadcastTrackerReportSuccessed(tracker);
                }
            });
        }
    }

    public void reportFailedTrackers() {
        if (mReportFailedTrackers.size() == 0) {
            return;
        }

        String[] list = mReportFailedTrackers.toArray(new String[mReportFailedTrackers.size()]);
        Log.d(TAG, "reportFailedTrackers: " + list.length);

        clearReportFailedTrackers();
        reportTrackers(list);
    }
}
