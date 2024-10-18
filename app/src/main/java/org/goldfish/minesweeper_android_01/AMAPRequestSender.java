package org.goldfish.minesweeper_android_01;

import static org.goldfish.minesweeper_android_01.Controller.thrower;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;

/**
 * {@code AMAPRequestSender}单例模式 <br/>
 * 用于发送高德地图定位请求
 */
public class AMAPRequestSender {
    /**
     * 单例模式
     */
    private static AMAPRequestSender instance = null;
    /**
     * 高德地图定位客户端
     */
    private AMapLocationClient locationClient = null;
    /**
     * 上一次请求得到的经纬度
     */
    private Double latitude = null;
    private Double longitude = null;
    /**
     * @param context
     * @throws RuntimeException
     * @see AMAPRequestSender#getInstance(EntranceActivity)
     *
     */

    private AMAPRequestSender(EntranceActivity context) throws RuntimeException {
        try {
            AMapLocationClient.updatePrivacyAgree(context, true);
            AMapLocationClient.updatePrivacyShow(context, true, true);

            locationClient = new AMapLocationClient(context);
            AMapLocationClientOption locationOption = new AMapLocationClientOption();
            locationOption.setOnceLocationLatest(true);
            locationOption.setBeidouFirst(true);
            locationClient.setLocationOption(locationOption);
            locationClient.setLocationListener(location -> {
                /**
                 * 如果定位失败，抛出异常
                 */
                if (location == null) {
                    throw new NullPointerException();
                }
                /**
                 * 获取经纬度
                 */
                EntranceRecorder.getInstance().updateLocation(location.getLatitude(), location.getLongitude());
                /**
                 *
                 */
                context.setLocation();
                /**
                 * 停止定位
                 */
                locationClient.stopLocation();
            });

        } catch (Exception e) {
            String message = "AMAPRequestSender: " + e.getMessage();
            Log.w(thrower, message, e);
        }
    }

    /**
     * 单例模式
     * 该重载方法用于构造单例
     * @param context
     * @return
     */

    public static AMAPRequestSender getInstance(EntranceActivity context) {
        if (instance == null) try {
            instance = new AMAPRequestSender(context);
        } catch (Exception e) {
            String message = "AMAPRequestSender: cannot create instance";
            Log.w(thrower, message);
        }
        return (instance);
    }

    /**
     * 单例模式
     * 该重载方法用于获取单例
     * @return
     */

    public static AMAPRequestSender getInstance() {
        if (instance == null) {
            String message = "AMAPRequestSender: instance not created";
            Log.w(thrower, message);
            throw new NullPointerException(message);
        }
        return instance;
    }
    /**
     * 开始定位
     */

    public void requestLocation() {
        try {
            locationClient.startLocation();
        } catch (Exception e) {
            String message = "AMAPRequestSender: " + e.getMessage();
            Log.w(thrower, message, e);
        }
    }
    /**
     * 获取经度 并清空缓存
     * @return 经度
     *
     */

    public Double getLatitude() {
        try {
            return latitude;
        } finally {
            latitude = null;
            longitude=null;
        }
    }
    /**
     * 获取纬度 并清空缓存
     * @return 纬度
     *
     */

    public Double getLongitude() {
        try {
            return longitude;
        } finally {
            latitude = null;
            longitude = null;
        }
    }
}
