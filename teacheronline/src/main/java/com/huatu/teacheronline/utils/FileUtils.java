package com.huatu.teacheronline.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;

import com.huatu.teacheronline.CustomApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Created by ljzyuhenda on 16/1/15.
 */
public class FileUtils {
    private static Context mApplicationContext = CustomApplication.applicationContext;
    private static boolean flag;
//    private boolean flag;//删除文件标签

    public static File sdPath() {
        return Environment.getExternalStorageDirectory();
    }

    public static File dataPath() {
        return mApplicationContext.getFilesDir();
    }

    /**
     * 数据解压缩
     *
     * @param compressedStr
     * @return
     * @throws Exception
     */
    public static String gunzip(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        byte[] compressed;
        String decompressed = null;
        try {
            compressed = Base64.decode(compressedStr, Base64.DEFAULT);
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);

            byte[] buffer = new byte[1024 * 8];
            int offset;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
                out.flush();
            }
            decompressed = out.toString();
        } catch (OutOfMemoryError e) {
            System.gc();
            DebugUtil.e(e.getMessage());
        } catch (IOException e) {
            DebugUtil.e(e.getMessage());
        } catch (IllegalArgumentException e) {
            DebugUtil.e(e.getMessage());
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }

        return decompressed;
    }

    /**
     * 获取缓存，有SD卡和没有sdk
     *
     * @return url
     */
    public static String getDiskCacheDir() {
        String cachePath = null;
        //Environment.getExtemalStorageState() 获取SDcard的状态
        //Environment.MEDIA_MOUNTED 手机装有SDCard,并且可以进行读写
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = mApplicationContext.getExternalCacheDir().getPath();
        } else {
            cachePath = mApplicationContext.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 获取讲义的存储地址
     *
     * @return
     */
    public static String getPDFDiskCacheDir() {
        return sdPath() + "/pdf/";
    }

    /**
     * 获取视频缓存的地址
     *
     * @return
     */
    public static String getVideoDiskCacheDir() {
        File file = new File(dataPath() + "/video/");
        if (!file.exists()) {
            file.mkdirs();
            DebugUtil.e("getVideoDiskCacheDir00" + file.mkdirs());
        }
        return dataPath() + "/video/";
    }

    /**
     * 获取视频缓存的地址
     *
     * @return
     */
    public static String getBjyVideoDiskCacheDir() {
        File file = new File(sdPath() + "/bjyDown/");
        if (!file.exists()) {
            file.mkdirs();
            DebugUtil.e("getBjyVideoDiskCacheDir" + file.mkdirs());
        }
        return sdPath() + "/bjyDown/";
    }

    /**
     * 获取录制面试视频存放文件夹
     *
     * @return
     */
    public static String getInterviewVideoRecordFolder() {
        File file = new File(sdPath() + "/huatu/interview/video");
        if (!file.exists()) {
            file.mkdirs();
        }
        return sdPath() + "/huatu/interview/video/";
    }

    /**
     * 获取面试视频的最终合成文件夹
     *
     * @return
     */
    public static String getInterviewVideoMergedFolder() {
        File file = new File(sdPath() + "/huatu/interview/merged");
        if (!file.exists()) {
            file.mkdirs();
        }
        return sdPath() + "/huatu/interview/merged/";
    }

    /**
     * 获取老师点评音频的下载文件夹
     *
     * @return
     */
    public static String getInterviewTeacherAudioFolder() {
        File file = new File(sdPath() + "/huatu/interview/teacherAudio");
        if (!file.exists()) {
            file.mkdirs();
        }
        return sdPath() + "/huatu/interview/teacherAudio";
    }

    /**
     * 回放视频缓存
     * @return
     */
//    public static String getBjyPlayBackDiskCacheDir(){
//        File file = new File(sdPath()+"/bjyPlayBackDown/");
//        if(!file.exists()){
//            file.mkdirs();
//            DebugUtil.e("getBjyVideoDiskCacheDir"+file.mkdirs());
//        }
//        return sdPath()+"/bjyPlayBackDown/";
//    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param sPath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public static boolean deleteFolder(String sPath) {
        flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) {  // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) {  // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else {  // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }
}
