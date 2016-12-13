package com.min.smalltalk.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.min.smalltalk.Exception.HttpException;
import com.min.smalltalk.bean.Groups;
import com.min.smalltalk.listener.OnDataListener;
import com.min.smalltalk.network.async.AsyncTaskManager;

import java.util.List;

/**
 * Created by Min on 2016/12/13.
 */

public class UserInfoManager implements OnDataListener {

    private final static String TAG = "UserInfoManager";
    private static final int GET_TOKEN = 800;

    /**
     * 用户信息全部未同步
     */
    private static final int NONE = 0;//00000
    /**
     * 好友信息同步成功
     */
    private static final int FRIEND = 1;//00001
    /**
     * 群组信息同步成功
     */
    private static final int GROUPS = 2;//00010
    /**
     * 群成员信息部分同步成功,n个群组n次访问网络,存在部分同步的情况
     */
    private static final int PARTGROUPMEMBERS = 4;//00100
    /**
     * 群成员信息同步成功
     */
    private static final int GROUPMEMBERS = 8;//01000
    /**
     * 黑名单信息同步成功
     */
    private static final int BLACKLIST = 16;//10000
    /**
     * 用户信息全部同步成功
     */
    private static final int ALL = 27;//11011

    private final Context mContext;
    private final AsyncTaskManager mAsyncTaskManager;
    private SharedPreferences sp;
    static Handler mHandler;
    private List<Groups> mGroupsList;//同步群组成员信息时需要这个数据

    public UserInfoManager(Context context) {
        mContext = context;
        mAsyncTaskManager = AsyncTaskManager.getInstance(mContext);
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
//        action = new SealAction(mContext);
        mHandler = new Handler(Looper.getMainLooper());
        mGroupsList = null;
    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {

    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }
}
