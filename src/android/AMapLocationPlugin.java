package com.joyo.cordova.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Locale;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;


/**
 * 高德地图定位
 *
 * api http://lbs.amap.com/api/android-location-sdk/guide/android-location/getlocation
 */
public class AMapLocationPlugin extends CordovaPlugin {
  // 定位方法名
  private static final String ACTION_GET_LOCATION = "getlocation";
  // 停止定位方法名
  private static final String ACTION_STOP_LOCATION = "stoplocation";

  // 是否只执行一次定位
  private boolean isOnceLocation;
  // 定位时间间隔
  private long locationInterval;

  protected static final String[] permissions = {
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION
  };
  public static final int ACCESS_LOCATION = 1;

  private AMapLocationClient locationClient = null;
  private AMapLocationClientOption locationClientOption = null;
  private Context context;
  private CallbackContext callbackContext = null;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    context = this.cordova.getActivity().getApplicationContext();
    super.initialize(cordova, webView);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onResume(boolean multitasking) {
    super.onResume(multitasking);

    // 切入前台后关闭后台定位功能
    // if (null != locationClient) {
    //   locationClient.disableBackgroundLocation(true);
    // }
  }

  @Override
  public void onStop() {
    super.onStop();

    // if (null != locationClient) {
    //   locationClient.enableBackgroundLocation(10010, buildNotification());
    // }
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.callbackContext = callbackContext;

    isOnceLocation = args.getBoolean(0);
    locationInterval = args.getLong(1);

    // 获取定位
    if (ACTION_GET_LOCATION.equals(action.toLowerCase(Locale.CHINA))) {
      if (context.getApplicationInfo().targetSdkVersion < 23) {
        getLocation();
      } else {
        boolean access_fine_location = PermissionHelper.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        boolean access_coarse_location = PermissionHelper.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (access_fine_location && access_coarse_location) {
          getLocation();
        } else {
          PermissionHelper.requestPermissions(this, ACCESS_LOCATION, permissions);
        }
      }
      return true;
    }

    // 停止定位
    if (ACTION_STOP_LOCATION.equals(action.toLowerCase(Locale.CHINA))) {
      stopLocation();
      return true;
    }

    // 启用后台定位功能
    if ("enableBackgroundLocation".equals(action)) {
      enableBackgroundLocation();
      return true;
    }

    // 关闭后台定位功能
    if ("disableBackgroundLocation".equals(action)) {
      disableBackgroundLocation();
      return true;
    }
    return false;
  }

  @Override
  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
    switch (requestCode) {
      case ACCESS_LOCATION:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          getLocation();
        } else {
          Toast.makeText(this.cordova.getActivity(), "请开启应用定位权限", Toast.LENGTH_SHORT).show();
        }
        break;
    }
  }

  // 高德定位监听
  private AMapLocationListener amapLocationListener = new AMapLocationListener() {
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
      if (amapLocation != null) {
        if (amapLocation.getErrorCode() == 0) {
          // 获取位置信息
          // 获取当前定位结果来源 定位类型对照表: http://lbs.amap.com/api/android-location-sdk/guide/utilities/location-type/
          int type = amapLocation.getLocationType();
          // 获取经度
          Double longitude = amapLocation.getLongitude();
          // 获取纬度
          Double latitude = amapLocation.getLatitude();
          // 获取精度信息
          float accuracy = amapLocation.getAccuracy();
          // 角度
          float bearing = amapLocation.getBearing();
          // 星数
          int satellites = amapLocation.getSatellites();
          // 国家信息
          String country = amapLocation.getCountry();
          // 省信息
          String province = amapLocation.getProvince();
          // 城市信息
          String city = amapLocation.getCity();
          // 城市编码
          String citycode = amapLocation.getCityCode();
          // 城区信息
          String district = amapLocation.getDistrict();
          // 地区编码
          String adcode = amapLocation.getAdCode();
          // 地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
          String address = amapLocation.getAddress();
          // 街道信息
          String street = amapLocation.getStreet();
          // 街道门牌号信息
          String streetNum = amapLocation.getStreetNum();
          // 获取当前定位点的AOI信息
          String aoiName = amapLocation.getAoiName();
          // 获取当前室内定位的楼层
          String floor = amapLocation.getFloor();
          // 获取GPS的当前状态
          int gpsAccuracyStatus = amapLocation.getGpsAccuracyStatus();
          // 时间
          long time = amapLocation.getTime();
          // 速度
          float speed = amapLocation.getSpeed();
          String road = amapLocation.getRoad();
          JSONObject jo = new JSONObject();
          try {
            jo.put("type", type);
            jo.put("longitude", longitude);
            jo.put("latitude", latitude);
            jo.put("accuracy", accuracy);
            jo.put("bearing", bearing);
            jo.put("satellites", satellites);
            jo.put("country", country);
            jo.put("province", province);
            jo.put("city", city);
            jo.put("citycode", citycode);
            jo.put("district", district);
            jo.put("adcode", adcode);
            jo.put("address", address);
            jo.put("street", street);
            jo.put("streetNum", streetNum);
            jo.put("aoiName", aoiName);
            jo.put("floor", floor);
            jo.put("gpsAccuracyStatus", gpsAccuracyStatus);
            jo.put("time", time);
            jo.put("speed", speed);
            jo.put("road", road);
          } catch (JSONException e) {
            jo = null;
            e.printStackTrace();
          }
          // callbackContext.success(jo);

          PluginResult r = new PluginResult(PluginResult.Status.OK, jo);
          r.setKeepCallback(true);
          callbackContext.sendPluginResult(r);
        } else {
          //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
          Log.e("AmapError", "location Error, ErrCode:"
                  + amapLocation.getErrorCode() + ", errInfo:"
                  + amapLocation.getErrorInfo());
          callbackContext.error(amapLocation.getErrorInfo());
        }
      }
    }
  };

  // 定位
  private void getLocation() {
    locationClient = new AMapLocationClient(context);
    locationClientOption = new AMapLocationClientOption();

    // 设置定位模式 Battery_Saving: 低功耗  Hight_Accuracy: 高精度  Device_Sensors: GPS
    locationClientOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
    // 设置是否使用手机传感器
    locationClientOption.setSensorEnable(true);
    // 设置为单次定位
    locationClientOption.setOnceLocation(isOnceLocation);
    // 设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
    locationClientOption.setInterval(locationInterval);
    // 使用签到定位场景
    // locationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);

    // 设置定位参数
    locationClient.setLocationOption(locationClientOption);
    // 设置定位监听
    locationClient.setLocationListener(amapLocationListener);

    // 设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
    // locationClient.stopLocation();
    // 启动定位
    locationClient.startLocation();

    PluginResult r = new PluginResult(PluginResult.Status.OK);
    r.setKeepCallback(true);
    callbackContext.sendPluginResult(r);
  }

  /**
   * 停止定位
   *
   * @Author JoyoDuan
   * @Date 2021/6/28
   * @Description:
   */
  private void stopLocation() {
    if (locationClient == null) {
      return;
    }

    try {
      locationClient.stopLocation();
      locationClient.onDestroy();
      locationClient = null;

      JSONObject jsonObject = new JSONObject();
      jsonObject.put("code", 2000);
      jsonObject.put("message", "Location is stopped");

      callbackContext.success(jsonObject);
    } catch (Exception e) {
      callbackContext.error(e.getMessage());
    }
  }

  /**
   * 启用后台定位
   *
   * @Author JoyoDuan
   * @Date 2021/7/5
   * @Description:
   */
  private void enableBackgroundLocation() {
    // 启用后台定位功能
    if (null != locationClient) {
      JSONObject jsonObject = new JSONObject();
      try {
        locationClient.enableBackgroundLocation(10010, buildNotification());

        jsonObject.put("code", 2000);
        jsonObject.put("message", "EnableBackgroundLocation is success");

        callbackContext.success(jsonObject);
      } catch (Exception e) {
        callbackContext.error(e.getMessage());
      }
    } else {
      callbackContext.error("LocationClient is null");
    }
  }

  /**
   * 关闭后台定位
   *
   * @Author JoyoDuan
   * @Date 2021/7/5
   * @Description:
   */
  private void disableBackgroundLocation() {
    // 关闭后台定位功能
    if (null != locationClient) {
      JSONObject jsonObject = new JSONObject();
      try {
        locationClient.disableBackgroundLocation(true);

        jsonObject.put("code", 2000);
        jsonObject.put("message", "DisableBackgroundLocation is success");

        callbackContext.success(jsonObject);
      } catch (Exception e) {
        callbackContext.error(e.getMessage());
      }
    } else {
      callbackContext.error("LocationClient is null");
    }
  }

  // private void coolMethod(String message, CallbackContext callbackContext) {
  //     if (message != null && message.length() > 0) {
  //         callbackContext.success(message);
  //     } else {
  //         callbackContext.error("Expected one non-empty string argument.");
  //     }
  // }


  private static final String NOTIFICATION_CHANNEL_NAME = "BackgroundLocation";
  private NotificationManager notificationManager = null;
  boolean isCreateChannel = false;
  @SuppressLint("NewApi")
  private Notification buildNotification() {

    Notification.Builder builder = null;
    Notification notification = null;
    if(android.os.Build.VERSION.SDK_INT >= 26) {
      //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
      if (null == notificationManager) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      }
      String channelId = context.getPackageName();
      if(!isCreateChannel) {
        NotificationChannel notificationChannel = new NotificationChannel(channelId,
                NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
        notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
        notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
        notificationManager.createNotificationChannel(notificationChannel);
        isCreateChannel = true;
      }
      builder = new Notification.Builder(context, channelId);
    } else {
      builder = new Notification.Builder(context);
    }
    builder.setContentTitle(getAppName(context))
            // .setSmallIcon(R.drawable.ic_launcher)
            .setContentText("正在后台运行")
            .setWhen(System.currentTimeMillis());

    return builder.build();
  }


  /**
   * 获取app的名称
   * @param context
   * @return
   */
  public static String getAppName(Context context) {
    String appName = "";
    try {
      PackageManager packageManager = context.getPackageManager();
      PackageInfo packageInfo = packageManager.getPackageInfo(
              context.getPackageName(), 0);
      int labelRes = packageInfo.applicationInfo.labelRes;
      appName =  context.getResources().getString(labelRes);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return appName;
  }
}
