package com.min.mylibrary.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

/**
 * Created by Min on 2016/11/23.
 * 公共工具类，与Android API相关的辅助类
 */

public class CommonUtils {

    /**
     * 检查网咯是否可用
     * @param context
     * @return
     */
    public static boolean isNetConnect(Context context){
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * 判断SDCard是否存在，并可写
     */
    public static boolean checkSDCard(){
        String flag= Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(flag);
    }

}
