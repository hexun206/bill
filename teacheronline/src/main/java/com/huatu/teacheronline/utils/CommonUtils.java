package com.huatu.teacheronline.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.bluelinelabs.logansquare.LoganSquare;
import com.huatu.teacheronline.CustomApplication;
import com.huatu.teacheronline.R;
import com.huatu.teacheronline.bean.ProvinceBean;
import com.huatu.teacheronline.bean.ProvinceWithCityBean;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;

/**
 * Created by ljzyuhenda on 15/12/30.
 */
public class CommonUtils {
    private static CommonUtils mCommonUtils;
    private List<ProvinceWithCityBean> mProvinceWithCityBeanList;
    private Observable mObservable;

    public static CommonUtils getInstance() {
        if (mCommonUtils == null) {
            synchronized (CommonUtils.class) {
                if (mCommonUtils == null) {
                    mCommonUtils = new CommonUtils();
                }
            }
        }

        return mCommonUtils;
    }

    private static Context mApplicationContext = CustomApplication.applicationContext;


    public List<ProvinceWithCityBean> getProvinceWithCityBeanList() {
        if (mProvinceWithCityBeanList == null) {
            try {
                InputStream inputStream = mApplicationContext.getAssets().open("provinceDatas");
                mProvinceWithCityBeanList = LoganSquare.parseList(inputStream, ProvinceWithCityBean.class);
                Collections.sort(mProvinceWithCityBeanList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mProvinceWithCityBeanList;
    }

    /**
     * 根据行政区划获取城市名称
     */
    public String getCityNameByXzqh(String xzqh) {
        getProvinceWithCityBeanList();
        for (int i = 0; i < mProvinceWithCityBeanList.size(); i++) {
            ProvinceWithCityBean provinceWithCityBean = mProvinceWithCityBeanList.get(i);
            List<ProvinceBean> cityBeans = provinceWithCityBean.cityList;
            if (null != cityBeans && cityBeans.size() > 0) {
                // 有城市数据
                for (int j = 0; j < cityBeans.size(); j++) {
                    if (xzqh.equals(cityBeans.get(j).Xzqh)) {
                        return cityBeans.get(j).Name;
                    }
                }
            } else {
                // 直辖市
                if (xzqh.equals(provinceWithCityBean.Xzqh)) {
                    return provinceWithCityBean.Name;
                }
            }
        }
        return null;
    }

    /**
     * 根据行政区划获取省份名称
     */
    public String getProvinceNameByXzqh(String xzqh) {
        getProvinceWithCityBeanList();
        for (int i = 0; i < mProvinceWithCityBeanList.size(); i++) {
            ProvinceWithCityBean provinceWithCityBean = mProvinceWithCityBeanList.get(i);
            if (!TextUtils.isEmpty(xzqh) && !TextUtils.isEmpty(provinceWithCityBean.Xzqh) && xzqh.equals(provinceWithCityBean.Xzqh)) {
                return provinceWithCityBean.Name;
            }
        }
        return null;
    }

    /**
     * dp转px
     */
    public static int dip2px(float dpValue) {
        final float scale = mApplicationContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     */
    public static int px2dip(float pxValue) {
        final float scale = mApplicationContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        final float scale = mApplicationContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5F);
    }

    /**
     * sp转px
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5F);
    }

    /**
     * px转sp
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue) {
        final float scale = mApplicationContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scale + 0.5F);
    }

    /**
     * 取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        DisplayMetrics dm = mApplicationContext.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight() {
        DisplayMetrics dm = mApplicationContext.getResources().getDisplayMetrics();
        return dm.heightPixels - getStatusBarHeight();
    }

    /**
     * 取屏幕高度包含状态栏高度
     *
     * @return
     */
    public static int getScreenHeightWithStatusBar() {
        DisplayMetrics dm = mApplicationContext.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 取导航栏高度
     *
     * @return
     */
    public static int getNavigationBarHeight() {
        int result = 0;
        int resourceId = mApplicationContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mApplicationContext.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }


    /**
     * 取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = mApplicationContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mApplicationContext.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    public static int getActionBarHeight() {
        int actionBarHeight = 0;

        final TypedValue tv = new TypedValue();
        if (mApplicationContext.getTheme()
                .resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data, mApplicationContext.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }


    /**
     * 关闭输入法
     *
     * @param act
     */
    public static void closeInputMethod(Activity act) {
        View view = act.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) mApplicationContext.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    /**
     * 判断应用是否处于后台状态
     *
     * @return
     */
    public static boolean isBackground() {
        ActivityManager am = (ActivityManager) mApplicationContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(mApplicationContext.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 复制文本到剪贴板
     *
     * @param text
     */
    public static void copyToClipboard(String text) {
        ClipboardManager cbm = (ClipboardManager) mApplicationContext.getSystemService(Activity.CLIPBOARD_SERVICE);
        cbm.setPrimaryClip(ClipData.newPlainText(mApplicationContext.getPackageName(), text));
    }

    /**
     * 获取SharedPreferences
     *
     * @return SharedPreferences
     */
    public static SharedPreferences getSharedPreference() {
        return mApplicationContext.getSharedPreferences(mApplicationContext.getPackageName(), Activity.MODE_PRIVATE);
    }

    /**
     * 获取SharedPreferences
     *
     * @return SharedPreferences
     */
    public static SharedPreferences getSharedPreference(String name) {
        return mApplicationContext.getSharedPreferences(name, Activity.MODE_PRIVATE);
    }

    /**
     * 获取SharedPreferences
     *
     * @return SharedPreferences
     */
    public static SharedPreferences getSharedPreference(String name, int mode) {
        return mApplicationContext.getSharedPreferences(name, mode);
    }

    /**
     * 获取sp保存的string值
     *
     * @param preferenceName
     * @param itemName         key值
     * @param defaultValueName 无数据时默认获取到的值
     * @return
     */
    public static String getSharedPreferenceItem(String preferenceName, String itemName, String defaultValueName) {
        if (TextUtils.isEmpty(preferenceName)) {
            return getSharedPreference().getString(itemName, defaultValueName);
        } else {
            return getSharedPreference(preferenceName).getString(itemName, defaultValueName);
        }
    }

    /**
     * 获取sp保存的boolean值
     *
     * @param preferenceName
     * @param itemName       key值
     * @return
     */
    public static boolean getSharedPreferenceItemForBoolean(String preferenceName, String itemName) {
        if (TextUtils.isEmpty(preferenceName)) {
            return getSharedPreference().getBoolean(itemName, false);
        } else {
            return getSharedPreference(preferenceName).getBoolean(itemName, false);
        }
    }

    /**
     * 向SharedPreference保存string值
     *
     * @param preferenceName
     * @param itemName       key值
     * @param itemValue      value值
     */
    public static void putSharedPreferenceItem(String preferenceName, String itemName, String itemValue) {
        if (TextUtils.isEmpty(preferenceName)) {
            getSharedPreference().edit().putString(itemName, itemValue).commit();
        } else {
            getSharedPreference(preferenceName).edit().putString(itemName, itemValue).commit();
        }
    }

    /**
     * 向SharedPreference保存多个值
     *
     * @param preferenceName
     * @param itemNames      key值集合
     * @param itemValues     value值集合
     */
    public static void putSharedPreferenceItems(String preferenceName, String[] itemNames, String[] itemValues) {
        SharedPreferences.Editor editor;
        if (TextUtils.isEmpty(preferenceName)) {
            editor = getSharedPreference().edit();
        } else {
            editor = getSharedPreference(preferenceName).edit();
        }
        for (int i = 0; i < itemNames.length; i++) {
            editor.putString(itemNames[i], itemValues[i]);
        }
        editor.commit();
    }

    /**
     * 向SharedPreference保存boolean值
     *
     * @param preferenceName
     * @param itemName       key值
     * @param itemValue      value值
     */
    public static void putSharedPreferenceItemForBoolean(String preferenceName, String itemName, Boolean itemValue) {
        if (TextUtils.isEmpty(preferenceName)) {
            getSharedPreference().edit().putBoolean(itemName, itemValue).commit();
        } else {
            getSharedPreference(preferenceName).edit().putBoolean(itemName, itemValue).commit();
        }
    }

    /**
     * 退出登录后清空sp中保存的用户信息
     */
    public static void clearSharedPreferenceItems() {
        putSharedPreferenceItems(null,
                new String[]{UserInfo.KEY_SP_USERID, UserInfo.KEY_SP_ACCOUNT, UserInfo.KEY_SP_PASSWORD, UserInfo.KEY_SP_MOBILE, UserInfo.KEY_SP_NICKNAME,
                        UserInfo.KEY_SP_PROVINCE_ID, UserInfo.KEY_SP_PROVINCE_NAME, UserInfo.KEY_SP_CITY_ID,
                        UserInfo.KEY_SP_CITY_NAME, UserInfo.KEY_SP_PROVINCE_ID, UserInfo.KEY_SP_PROVINCE_NAME, UserInfo.KEY_SP_EXAMCATEGORY_ID, UserInfo
                        .KEY_SP_EXAMCATEGORY_NAME,
                        UserInfo.KEY_SP_EXAMSTAGE_ID, UserInfo.KEY_SP_EXAMSTAGE_NAME, UserInfo.KEY_SP_EXAMSUBJECT_ID,
                        UserInfo.KEY_SP_EXAMSUBJECT_NAME, UserInfo.KEY_SP_VIDEO_SUBJECTS_VERSION, UserInfo.KEY_SP_VIDEO_SUBJECTS,
                        UserInfo.KEY_SP_SEX, UserInfo.KEY_SP_FACEPATH, UserInfo.KEY_SP_GOLD, UserInfo.KEY_SP_POINT},
                new String[]{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                        "0", "0"});
        putSharedPreferenceItemForBoolean(null, UserInfo.KEY_SP_IFLOGIN, false);
    }

    /**
     * 经纬度测距
     *
     * @param jingdu1
     * @param weidu1
     * @param jingdu2
     * @param weidu2
     * @return
     */
    public static double distance(double jingdu1, double weidu1, double jingdu2, double weidu2) {
        double a, b, R;
        R = 6378137; // 地球半径
        weidu1 = weidu1 * Math.PI / 180.0;
        weidu2 = weidu2 * Math.PI / 180.0;
        a = weidu1 - weidu2;
        b = (jingdu1 - jingdu2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2
                * R
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(weidu1)
                * Math.cos(weidu2) * sb2 * sb2));
        return d;
    }

    /**
     * 非实时状态需要调用isNetWorkAvilable()更新
     */
    public static boolean netAvailable = true;

    /**
     * 是否有网络
     *
     * @return
     */
    public static boolean isNetWorkAvilable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mApplicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            netAvailable = false;
            return false;
        } else {
            netAvailable = true;
            return true;
        }
    }

    /**
     * 取APP版本号
     *
     * @return
     */
    public static int getAppVersionCode() {
        try {
            PackageManager mPackageManager = mApplicationContext.getPackageManager();
            PackageInfo _info = mPackageManager.getPackageInfo(mApplicationContext.getPackageName(), 0);
            return _info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    /**
     * 取APP版本名
     *
     * @return
     */
    public static String getAppVersionName() {
        try {
            PackageManager mPackageManager = mApplicationContext.getPackageManager();
            PackageInfo _info = mPackageManager.getPackageInfo(mApplicationContext.getPackageName(), 0);
            return _info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }


    /**
     *
     * @param oldVersionName
     * @param newVersionName
     * @return 1:>   0:=  -1:<
     */
    public static int compareVersionNames(String oldVersionName, String newVersionName) {
        int res = 0;

        String[] oldNumbers = oldVersionName.split("\\.");
        String[] newNumbers = newVersionName.split("\\.");

        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i++) {
            int oldVersionPart = Integer.valueOf(oldNumbers[i]);
            int newVersionPart = Integer.valueOf(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = 1;
                break;
            }
        }

        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length) ? 1 : -1;
        }

        return res;
    }

    public static Bitmap BitmapZoom(Bitmap b, float x, float y) {
        int w = b.getWidth();
        int h = b.getHeight();
        float sx = x / w;
        float sy = y / h;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w,
                h, matrix, true);
        return resizeBmp;
    }


    public static String MD5(byte[] data) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(data);
        byte[] m = md5.digest();//加密
        return Base64.encodeToString(m, Base64.DEFAULT);
    }

    // 视频中的秒数转成字符串
    public static String millsecondsToStr(int seconds) {
        seconds = seconds / 1000;
        String result = "";
        int hour = 0, min = 0, second = 0;
        hour = seconds / 3600;
        min = (seconds - hour * 3600) / 60;
        second = seconds - hour * 3600 - min * 60;
        if (hour < 10) {
            result += "0" + hour + ":";
        } else {
            result += hour + ":";
        }
        if (min < 10) {
            result += "0" + min + ":";
        } else {
            result += min + ":";
        }
        if (second < 10) {
            result += "0" + second;
        } else {
            result += second;
        }
        return result;
    }

    public static String converterToChineseSpell(String words, HanyuPinyinOutputFormat format) {
        char[] chars = words.toCharArray();
        StringBuilder chineseSpell = new StringBuilder();
        try {
            for (int index = 0; index < chars.length; index++) {
                String sEve = Character.toString(chars[index]);
                if (sEve.matches("[\\u4e00-\\u9fa5]")) {
                    chineseSpell.append(PinyinHelper.toHanyuPinyinStringArray(chars[index], format)[0]);
                } else {
                    chineseSpell.append(Character.toString(chars[index]));
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }

        return chineseSpell.toString();
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 是否电话号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        boolean flag = false;
        try {
            Pattern p = Pattern.compile("^(2|1)\\d{10}$");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        } catch (Exception e) {
            // Logger.error("验证手机号码错误", e);
            flag = false;
        }
        return flag;
    }

    /**
     * 根据考试类型的key值查询value值
     */
    public static String getExamCategoryValue(String key) {
        String[] categoryKeyList = mApplicationContext.getResources().getStringArray(R.array.key_category);
        String[] categoryNameList = mApplicationContext.getResources().getStringArray(R.array.name_category);
        int index = Arrays.asList(categoryKeyList).indexOf(key);
        if (index >= 0 && index < categoryKeyList.length) {
            return categoryNameList[index];
        } else {
            return null;
        }
    }

    /**
     * 根据考试学段的value值查询key值
     */
    public static String getExamStageKey(String value) {
        String[] stageLists = mApplicationContext.getResources().getStringArray(R.array.categoryArrForExamStage);
        String[] stageKeyLists = mApplicationContext.getResources().getStringArray(R.array.key_stage);
        int index = Arrays.asList(stageLists).indexOf(value);
        if (index >= 0 && index < stageLists.length) {
            return stageKeyLists[index];
        } else {
            return null;
        }
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }

        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }


    /******
     * 设置viewMargin
     ****/
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    public static long getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
//        return Formatter.formatFileSize(mApplicationContext, blockSize * totalBlocks);
        return blockSize * totalBlocks;
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public static long getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
//        return  Formatter.formatFileSize(mApplicationContext, blockSize * availableBlocks);
        return blockSize * availableBlocks;

    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    public static long getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
//        return Formatter.formatFileSize(mApplicationContext, blockSize * totalBlocks);
        return blockSize * totalBlocks;
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    public static long getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
//        return Formatter.formatFileSize(mApplicationContext, blockSize * availableBlocks);
        return blockSize * availableBlocks;
    }

    /**
     * 判断sd卡的剩余容量小于某值
     *
     * @param sizeMb
     * @return
     */
    public static boolean isAvaiableSpace(int sizeMb) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String sdcard = Environment.getExternalStorageDirectory().getPath();
//     File file = new File(sdcard);
            StatFs statFs = new StatFs(sdcard);
            long blockSize = statFs.getBlockSize();
            long blocks = statFs.getAvailableBlocks();
            long availableSpare = (blocks * blockSize) / (1024 * 1024);
//     long availableSpare = (long) (statFs.getBlockSize()*((long)statFs.getAvailableBlocks()-4))/(1024*1024);//以比特计算 换算成MB
            System.out.println("availableSpare = " + availableSpare);
            if (sizeMb > availableSpare) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * activity是否在栈顶
     *
     * @param mContext
     * @param currentActivityName
     * @return
     */
    public static boolean isTaskTop(Context mContext, String currentActivityName) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(mContext.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = am.getRunningTasks(1);
        String topActivityName = null;
        if (null != runningTaskInfoList) {
            topActivityName = (runningTaskInfoList.get(0).topActivity).getShortClassName();
        }
        if (TextUtils.isEmpty(topActivityName))
            return false;
        if (TextUtils.isEmpty(currentActivityName))
            throw new IllegalArgumentException();
        if (topActivityName.contains(currentActivityName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断当前应用程序处于前台还是后台 true后台 false前台
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;

    }

    /**
     * make true current connect service is wifi
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 关闭媒体音量
     *
     * @param mContext
     * @return
     */
    public static void closeMusic(Context mContext) {
        if (!isSilentMode(mContext)) {
            AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }
    }

    /**
     * 关闭媒体音量
     *
     * @param mContext
     * @return
     */
    public static void openMusic(Context mContext) {
        if (isSilentMode(mContext)) {
            AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }
    }

    /**
     * 媒体音量是否静音
     *
     * @param mContext
     * @return
     */
    public static boolean isSilentMode(Context mContext) {
        AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        DebugUtil.e("isSilentMode MUSIC", "max : " + max + "current:"+ current);
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0;
    }

    /**
     * 转换时分秒
     *
     * @param miss
     * @return
     */
    public static String FormatMiss(int miss) {
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }

    /**
     * 转换秒
     * hh + ":" + mm + ":" + ss 转换秒
     *
     * @param time
     * @return
     */
    public static String FormatSends(String[] time) {
        String timeString = "0.0";
        switch (time.length) {
            case 2:
                timeString = (Integer.valueOf(time[0]) * 60 + Integer.valueOf(time[1])) + "";
                break;
            case 3:
                timeString = (Integer.valueOf(time[2]) + Integer.valueOf(time[1]) * 60 + Integer.valueOf(time[0]) * 60 * 60) + "";
                break;
            default:
                break;
        }
//        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
//        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
//        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        return timeString + ".0";
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 压缩图片 小于32K 分享微信
     *
     * @param bitmap
     * @return
     */
    public static Bitmap imageCompress(Bitmap bitmap) {
        // 图片允许最大空间 单位：KB
        double maxSize = 32.00;
        // 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        // 将字节换成KB
        double mid = b.length / 1024;
        // 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            // 获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            // 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
            bitmap = zoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i),
                    bitmap.getHeight() / Math.sqrt(i));
        }
        return bitmap;
    }

    /***
     * 图片压缩方法二
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /*
     * 获取application中指定的meta-data
     *  @key  BaiduMobAd_CHANNEL_VALUE
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }
}
