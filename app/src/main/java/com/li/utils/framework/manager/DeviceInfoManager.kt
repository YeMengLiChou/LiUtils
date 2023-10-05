package com.li.utils.framework.manager

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.annotation.Px
import java.io.FileInputStream
import java.net.NetworkInterface
import kotlin.math.max
import kotlin.math.min


/**
 *
 * 设备的信息
 *
 * @author Gleamrise
 * <br/>Created: 2023/10/05
 */
@SuppressLint("HardwareIds")
object DeviceInfoManager {

    private const val TAG = "DeviceInfoManager"

    private lateinit var mContext: Application

    /**
     * 屏幕宽度 (Px)
     * */
    @Px
    var screenWidthPx = 0
        private set

    /**
     * 屏幕高度 (Px)
     * */
    @Px
    var screenHeightPx = 0
        private set

    /**
     * 屏幕宽度 (Dp)
     * */
    var screenWidthDp = 0
        private set

    /**
     * 屏幕高度 (Dp)
     * */
    var screenHeightDp = 0
        private set

    /**
     * density dpi
     */
    var densityDpi = 0
        private set

    /**
     * density scale
     */
    var density = 0f
        private set

    /**
     * 状态栏高度 (Px)
     */
    var statusBarHeight = 0
        private set

    /**
     * 导航栏高度 (Px)
     * */

    var navigationBarHeight = 0
        private set

    /**
     * product type
     */
    var productType: String? = null
        private set

    /**
     * 是否为大屏 （宽高比大于 16:9）
     * */
    var isBiggerScreen = false
        private set

    /**
     * 设备 imei 号
     *
     * IMEI（International Mobile Equipment Identity）是移动设备国际身份识别码的缩写，是一个全球唯一的号码，用于标识 GSM、UMTS 和 LTE 网络上的移动电话设备。
     * IMEI 号码是一个15位的数字序列，可以用于识别手机的制造商、型号和国际上的唯一标识。
     * */
    var imei: String = ""
        private set

    /**
     * 设备 imsi 号
     *
     * IMSI（International Mobile Subscriber Identity）是国际移动用户识别码的缩写，是与移动设备用户相关联的唯一标识。
     * IMSI 是一个全球唯一的号码，它存储在 SIM 卡中，用于识别手机用户并允许他们接入移动通信网络。
     * */
    var imsi: String = ""
        private set

    /**
     * androidId
     *
     * Android ID 是 Android 操作系统提供的一个用于识别设备的唯一标识符。它通常是一个 64 位的十六进制数字，用于区分不同的 Android 设备。
     *
     * Android ID 不同于设备的 IMEI（国际移动设备身份码）或 IMSI（国际移动用户识别码）。
     * IMSI 用于识别移动用户，而 IMEI 用于识别移动设备的硬件。Android ID 是在设备上生成的，通常不与 SIM 卡或硬件相关。
     * */
    var androidId: String = ""
        private set

    /**
     * 设备 mac 地址
     * */
    var macAddress: String = ""
        private set


    /**
     * WiFi mac地址
     *
     * Wi-Fi MAC 地址（Media Access Control Address）是一个用于标识网络设备的唯一地址。
     * 每个通过 Wi-Fi 连接到网络的设备，如智能手机、笔记本电脑、平板电脑等，都有一个唯一的 MAC 地址，
     * 该地址通常以十六进制表示，由 12 个字符组成，例如："00:1A:2B:3C:4D:5E"。
     */
    var wifiMacAddress: String = ""
        private set

    /**
     * WiFi ssid号
     *
     * SSID 是指 WLAN（无线局域网）中的服务集标识符（Service Set Identifier）。
     * 它是用于识别和区分无线网络的名称，通常由无线路由器或访问点广播。
     * SSID 是无线网络的名称，用户可以通过搜索可用的无线网络列表来找到并连接到所需的网络。
     * */
    var wifiSSID: String = ""
        private set

    /**
     * 手机型号
     * */
    var phoneModel: String = ""
        private set

    /**
     * 手机品牌
     * */
    var phoneBrand: String = ""
        private set

    /**
     * 手机制造商
     * */
    var phoneManufacturer: String = ""
        private set

    /**
     *
     * */
    var phoneDevice: String = ""
        private set

    /**
     * 手机系统版本号
     * */
    var phoneBuildRelease: String = ""
        private set

    /**
     * 版本名称
     * */
    var appVersionName: String = ""
        private set

    /**
     * 版本号
     * */
    var appVersionCode: Long = 0
        private set


    /**
     * 初始化 [DeviceInfoManager]
     * @param application
     * */
    fun init(application: Application) {
        mContext = application
        initDisplayInfo()
        initDeviceInfo()
        Log.i(TAG, "init: ${toString()}")
    }


    // ========================= Display Info =====================================

    /**
     * 初始化屏幕信息
     * */
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    private fun initDisplayInfo() {
        val windowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        // 屏幕宽高 (Px)
        screenHeightPx = max(metrics.heightPixels, metrics.widthPixels)
        screenWidthPx = min(metrics.heightPixels, metrics.widthPixels)
        // 大屏
        isBiggerScreen = screenHeightPx * 1.0 / screenWidthPx > 16.0 / 9
        // 屏幕密度
        densityDpi = metrics.densityDpi
        // 屏幕缩放，Px和Dp的比例
        density = metrics.density
        // 屏幕宽高 (Dp)
        screenHeightDp = (screenHeightPx / density).toInt()
        screenWidthDp = (screenWidthPx / density).toInt()

        // 状态栏高度
        initStatusBarHeight()
        // 导航栏高度
        initNavigationBarHeight()

    }

    /**
     * 获取状态栏高度
     * */
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    private fun initStatusBarHeight() {
        val statusHeightResId =
            mContext.resources.getIdentifier("status_bar_height", "dimen", "android")
        statusBarHeight = mContext.resources.getDimensionPixelSize(statusHeightResId)
    }

    /**
     * 获取导航栏高度
     * */
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    private fun initNavigationBarHeight() {
        // 导航栏高度
        try {
            val res: Resources = mContext.resources
            val rid = res.getIdentifier("config_showNavigationBar", "bool", "android")
            if (rid > 0) {
                val flag = res.getBoolean(rid)
                if (flag) {
                    val navigationResId =
                        res.getIdentifier("navigation_bar_height", "dimen", "android")
                    if (navigationResId > 0) {
                        navigationBarHeight = res.getDimensionPixelSize(navigationResId)
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "getNavigationBarHeight: $e")
        }
    }


    // ========================= Device Info =====================================

    /**
     * 初始化设备信息
     * */
    private fun initDeviceInfo() {
        initImei()
        initAndroidId()
        initMacAddress()
        initAppVersion()
        phoneModel = Build.MODEL
        phoneBrand = Build.BRAND
        phoneManufacturer = Build.MANUFACTURER
        phoneDevice = Build.DEVICE
        phoneBuildRelease = Build.VERSION.RELEASE
        productType = Build.MODEL.replace("[:{} \\[\\]\"']*".toRegex(), "")
    }

    /**
     * 初始化 Imei 和 Imsi
     * */
    @SuppressLint("MissingPermission")
    private fun initImei() {
        if (imei.isNotEmpty() || imsi.isNotEmpty()) {
            return
        }
        if (mContext.packageManager.checkPermission(
                android.Manifest.permission.READ_PHONE_STATE,
                mContext.packageName
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        try {
            val tm = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var tmpImei = ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tmpImei = tm.imei
            } else {
                tmpImei = tm.deviceId
            }
            if (tmpImei.isNotEmpty()) {
                imei = tmpImei.lowercase()
            }
            val tmpImsi = tm.subscriberId
            if (!tmpImsi.isNullOrEmpty()) {
                imsi = tmpImsi.lowercase()
            }
        } catch (e: Exception) {
            Log.e(TAG, "initImei: $e")
        }
    }

    /**
     * 初始化 AndroidId
     * */
    private fun initAndroidId() {
        if (androidId.isNotEmpty()) {
            return
        }
        val tmpAndroidId = Settings.Secure.getString(
            mContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        if (!tmpAndroidId.isNullOrEmpty()) {
            androidId = tmpAndroidId.lowercase()
        }
    }

    /**
     * 初始化 Mac 地址
     * */
    private fun initMacAddress() {
        if (macAddress.isNotEmpty() || wifiMacAddress.isNotEmpty() || wifiSSID.isNotEmpty()) {
            return
        }
        try {
            val wm = mContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // 6.0以前可以从wifiInfo中获取mac地址
                val wifiInfo = wm.connectionInfo
                if (wifiInfo != null && wifiInfo.macAddress != null) {
                    macAddress = wifiInfo.macAddress.lowercase()
                }
            } else {
                // 6.0~7.0读取设备文件获取
                val arrStrings = arrayOf("/sys/class/net/wlan0/address", "/sys/devices/virtual/net/wlan0/address")
                for (str in arrStrings) {
                    macAddress = readFile(str)
                    break
                }
                if (macAddress.isNotEmpty()) {
                    return
                }
                // 7.0及以上通过以下方式获取
                val interfaces = NetworkInterface.getNetworkInterfaces() ?: return
                while (interfaces.hasMoreElements()) {
                    val netInfo = interfaces.nextElement()
                    if ("wlan0" == netInfo.name) {
                        val addresses = netInfo.hardwareAddress
                        if (addresses == null || addresses.isEmpty()) {
                            continue
                        }
                        macAddress = macByte2String(addresses)
                        break
                    }
                }
            }
            if (!wm.connectionInfo.bssid.isNullOrEmpty()) {
                wifiMacAddress = wm.connectionInfo.bssid
            }
            if (!wm.connectionInfo.ssid.isNullOrEmpty()) {
                wifiSSID = wm.connectionInfo.ssid
            }
        } catch (e: Exception) {
            Log.e(TAG, "initMacAddress: $e")
        }
    }

    /**
     * 初始化版本号和版本名称
     * */
    private fun initAppVersion() {
        try {
            val packageManager = mContext.packageManager
            val packageName = mContext.packageName
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName
            // 版本名称
            if (versionName != null) {
                appVersionName = versionName
            }
            // 版本号
            appVersionCode = packageInfo.versionCode.toLong()
        } catch (e: Exception) {
            Log.e(TAG, "initAppVersion: $e")
        }
    }

    /**
     * 将 mac 的 byte 数组转化为string
     */
    private fun macByte2String(bytes: ByteArray): String {
        val buf = StringBuffer()
        for (b in bytes) {
            buf.append(String.format("%02X:", b))
        }
        if (buf.isNotEmpty()) {
            buf.deleteCharAt(buf.length - 1)
        }
        return buf.toString()
    }

    /**
     * 从指定的文件内读取内容
     */
    private fun readFile(filePath: String): String {
        var res = ""
        val fin: FileInputStream
        return try {
            fin = FileInputStream(filePath)
            fin.use {
                val length = fin.available()
                val buffer = ByteArray(length)
                val count = fin.read(buffer)
                if (count > 0) {
                    res = String(buffer, Charsets.UTF_8)
                }
                return@use res
            }
        } catch (e: Exception) {
            Log.e(TAG, "read file exception")
            return ""
        }
    }

    override fun toString(): String {
        return """
             DeviceInfoManager:
                screenWidthPx: $screenWidthPx
                screenHeightPx: $screenHeightPx
                screenWidthDp: $screenWidthDp
                screenHeightDp: $screenHeightDp
                densityDpi: $densityDpi
                density: $density
                statusBarHeight: $statusBarHeight
                navigationBarHeight: $navigationBarHeight
                isBiggerScreen: $isBiggerScreen
                productType: $productType
                imei: $imei
                imsi: $imsi
                androidId: $androidId
                macAddress: $macAddress
                wifiMacAddress: $wifiMacAddress
                wifiSSID: $wifiSSID
                phoneModel: $phoneModel
                phoneBrand: $phoneBrand
                phoneManufacturer: $phoneManufacturer
                phoneDevice: $phoneDevice
                phoneBuildRelease: $phoneBuildRelease
                appVersionName: $appVersionName
                appVersionCode: $appVersionCode
         """.trimIndent()
    }
}