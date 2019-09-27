package com.dec.dstar.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/3 13:47
 * 文件描述:
 */
public final class AndroidIDUtil {

    /**
     * 原生imei
     */
    public static String getImeiOrigin(Context context) {
        String deviceId = "";
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            deviceId = ((TelephonyManager) context.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
            if (!TextUtils.isEmpty(deviceId) && !deviceId.equals("Unknown")) {
                try {
                    long imei = Long.parseLong(deviceId);
                    if (imei == 0) {
                        deviceId = "";
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceId;
    }

    public static String getImei(Context sContext) {
        // 例: 868030024699025
        try {
            String deviceId = ((TelephonyManager) sContext.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
            if (!TextUtils.isEmpty(deviceId) && !deviceId.equals("Unknown")) {

                long imei = Long.parseLong(deviceId);
                if (imei == 0) {
                    deviceId = null;
                }

                if (!TextUtils.isEmpty(deviceId)) {
                    return deviceId;
                }
            } else {
                deviceId = Settings.Secure.getString(sContext.getContentResolver(), Settings.Secure.ANDROID_ID);
                if (!TextUtils.isEmpty(deviceId)) {
                    return deviceId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String m_szWLANMAC = getMac();
        if (!TextUtils.isEmpty(m_szWLANMAC) && !m_szWLANMAC.equals("00:00:00:00:00:00")) {
            return getMD5String(m_szWLANMAC);
        }

        return "M_" + new DeviceUuidFactory(sContext).getDeviceUuid();
    }

    /**
     * mac地址
     */
    public static String getMac() {
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (TextUtils.isEmpty(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return macSerial;
    }


    private static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    private static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }


    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static MessageDigest messageDigest = null;

    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String getMD5String(String str) {
        return getMD5String(str.getBytes());
    }

    private static String getMD5String(byte[] bytes) {
        messageDigest.update(bytes);
        return bytesToHex(messageDigest.digest());
    }

    private static String bytesToHex(byte bytes[]) {
        return bytesToHex(bytes, 0, bytes.length);
    }

    private static String bytesToHex(byte bytes[], int start, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < start + len; i++) {
            sb.append(byteToHex(bytes[i]));
        }
        return sb.toString();
    }

    private static String byteToHex(byte bt) {
        return HEX_DIGITS[(bt & 0xf0) >> 4] + "" + HEX_DIGITS[bt & 0xf];
    }


    private static class DeviceUuidFactory {

        private static final String PREFS_FILE = "device_id.xml";
        private static final String PREFS_DEVICE_ID = "device_id";
        private static volatile UUID uuid;

        private DeviceUuidFactory(Context context) {
            if (uuid == null) {
                synchronized (DeviceUuidFactory.class) {
                    if (uuid == null) {
                        final SharedPreferences prefs = context
                                .getSharedPreferences(PREFS_FILE, 0);
                        final String id = prefs.getString(PREFS_DEVICE_ID, null);
                        if (id != null) {
                            // Use the ids previously computed and stored in the
                            // prefs file
                            uuid = UUID.fromString(id);
                        } else {
                            final String androidId = Settings.Secure.getString(
                                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
                            // Use the Android ID unless it's broken, in which case
                            // fallback on deviceId,
                            // unless it's not available, then fallback on a random
                            // number which we store to a prefs file
                            try {
                                if (!"9774d56d682e549c".equals(androidId)) {
                                    uuid = UUID.nameUUIDFromBytes(androidId
                                            .getBytes("utf8"));
                                } else {

                                    uuid = UUID.randomUUID();
                                }
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                            // Write the value out to the prefs file
                            prefs.edit()
                                    .putString(PREFS_DEVICE_ID, uuid.toString())
                                    .commit();
                        }
                    }
                }
            }
        }

        private String getDeviceUuid() {
            return uuid.toString().replace("-", "");
        }
    }


    /**
     * 返回运营商 需要加入权限 <uses-permission android:name="android.permission.READ_PHONE_STATE"/> <BR>
     *
     * @return 1, 代表中国移动，2，代表中国联通，3，代表中国电信，0，代表未知
     * @author youzc@yiche.com
     */
    public static String getOperators(Context context) {
        // 移动设备网络代码（英语：Mobile Network Code，MNC）是与移动设备国家代码（Mobile Country Code，MCC）（也称为“MCC /
        // MNC”）相结合, 例如46000，前三位是MCC，后两位是MNC 获取手机服务商信息

        String operatorsName = "unknown";
        try {
            String IMSI = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();
            if (!TextUtils.isEmpty(IMSI)) {
                operatorsName = IMSI;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        // IMSI号前面3位460是国家，紧接着后面2位00 运营商代码
//        if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
//            OperatorsName = "中国移动";
//        } else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
//            OperatorsName = "中国联通";
//        } else if (IMSI.startsWith("46003") || IMSI.startsWith("46005")) {
//            OperatorsName = "中国电信";
//        }
//        String imsi = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimOperatorName();
//        if (imsi != null) {
//            operatorsName = imsi;
//        }
        return operatorsName;
    }

    /**
     * 获取网络类型
     *
     * @return
     */
    public static String getNetworkType(Context context) {
        String strNetworkType = "UNKNOWN";

        try {
            NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    strNetworkType = "WIFI";
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    String _strSubTypeName = networkInfo.getSubtypeName();

                    // TD-SCDMA   networkType is 17
                    int networkType = networkInfo.getSubtype();
                    switch (networkType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                            strNetworkType = "2G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                        case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                        case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                            strNetworkType = "3G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                            strNetworkType = "4G";
                            break;
                        default:
                            // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                            if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                strNetworkType = "3G";
                            } else {
                                strNetworkType = _strSubTypeName;
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strNetworkType;
    }

    /**
     * 获取屏幕的像素宽高，密度，dpi
     *
     * @param context
     * @return
     */
    public static int[] getDisplayInfo(Context context) {
        int[] display = new int[2];
        DisplayMetrics metric = new DisplayMetrics();
        try {
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metric);
            display[0] = metric.widthPixels;     // 屏幕宽度（像素）
            display[1] = metric.heightPixels;   // 屏幕高度（像素）
//            display[2] = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
//            display[3] = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240
        } catch (Exception e) {
            e.printStackTrace();
        }
        return display;
    }

    /**
     * 获取手机经纬度
     *
     * @param context
     * @return
     */
    public static double[] getLocation(Context context) {
        double latitude = 0.0;
        double longitude = 0.0;
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        double[] location = new double[2];
        location[0] = latitude;
        location[1] = longitude;
        return location;
    }

}
